/* ===============================================================================
 *
 * Part of the InfoGlue Content Management Platform (www.infoglue.org)
 *
 * ===============================================================================
 *
 *  Copyright (C)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2, as published by the
 * Free Software Foundation. See the file LICENSE.html for more information.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
 * Place, Suite 330 / Boston, MA 02111-1307 / USA.
 *
 * ===============================================================================
 */

package org.infoglue.deliver.externalsearch;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.infoglue.cms.exception.ConfigurationError;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.util.CacheNotificationCenter;
import org.infoglue.deliver.util.CacheNotificationListener;
import org.infoglue.deliver.util.ThreadedQueueCacheNotificationListener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * <p>The ExternalSearchManager class is a singleton responsible for handling all {@link ExternalSearchService} in the system.
 * The manager has three responsibilities:
 * </p>
 * <ul>
 *   <li>Provide a way to get access to each search service (through its {@link #getService(String)} method).</li>
 *   <li>Notify each service about updates in the external search configuration.</li>
 *   <li>Provide the hearth beat loop for the services index updating.</li>
 * </ul>
 * 
 * <p>Under normal circumstances the only method that should be of interest is the <em>getService(String)</em> method.
 * The rest of class's API is used by other parts of the External seach architecture.</p>
 * 
 * @author Erik Stenbäcka
 */
public class ExternalSearchManager extends ThreadedQueueCacheNotificationListener implements Runnable, CacheNotificationListener
{
	private static final Logger logger = Logger.getLogger(ExternalSearchManager.class);
	private Gson configParser;
	private Map<String, ExternalSearchService> services;
	private boolean stopped;

	private static ExternalSearchManager manager;

	/** 
	 * Used for testing.
	 */
	public static void injectManager(ExternalSearchManager fakeManager)
	{
		manager = fakeManager;
	}

	private synchronized static void initManager()
	{
		if (manager == null)
		{
			manager = new ExternalSearchManager();
			Thread thread = new Thread (manager);
			thread.setName("ExternalSearchManager");
			thread.start();
		}
	}

	/**
	 * Gets a reference to the manager singleton. If this is the first time
	 * the manager is accessed the singleton is created and the hearth beat
	 * loop is started.
	 * @return The external search manager singleton instance.
	 */
	public static ExternalSearchManager getManager()
	{
		if (manager == null)
		{
			initManager();
		}
		return manager;
	}

	//////////////////////////////////////////////////////////////////////
	// Instance methods

	public ExternalSearchManager()
	{
		this.services = new HashMap<String, ExternalSearchService>();
		this.stopped = false;
//		updateConfigurations();
		CacheNotificationCenter.getCenter().addListener(this);
	}

	private void initGSon()
	{
		class DelegateDeserializer<T extends ExternalSearchDelegate> implements JsonDeserializer<T>
		{
			Type configType = new TypeToken<Map<String, String>>() {}.getType();

			@Override
			public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				try
				{
		            JsonObject obj = (JsonObject) json;
		            Class<?> clazz = Class.forName(obj.get("class").getAsString());
		            @SuppressWarnings("unchecked")
					T delegate = (T) clazz.newInstance();

		            if (obj.has("config"))
		            {
			            Map<String, String> config = context.deserialize(obj.get("config"), configType);
			            delegate.setConfig(config);
		            }

		            return delegate;
		        }
				catch (Exception ex)
				{
		            throw new JsonParseException("Failed to deserialize element. Exception message: " + ex.getMessage(), ex);
		        }
			}

		}

		GsonBuilder gson = new GsonBuilder();

		Type fieldsType = new TypeToken<Map<String, IndexableField>>() {}.getType();

		gson.registerTypeAdapter(fieldsType, new IndexableField.Deserializer());
		gson.registerTypeAdapter(DataRetriever.class, new DelegateDeserializer<DataRetriever>());
		gson.registerTypeAdapter(Parser.class, new DelegateDeserializer<Parser>());
		gson.registerTypeAdapter(Indexer.class, new DelegateDeserializer<Indexer>());

		configParser = gson.create();
	}

	/**
	 * Attempts to parse the given String as a external search service configurations array. See general documentation
	 * of {@link ExternalSearchService} for an explanation of the configuration syntax.
	 * 
	 * @param configsString A String representation of an external search service configuration array.
	 * @return A list of external search service configurations based on the given configurations string
	 * @throws ConfigurationError Thrown if the JSON-parsing of the input fails.
	 */
	protected List<ExternalSearchServiceConfig> parseConfiguartions(String configsString) throws ConfigurationError
	{
		if (configParser == null)
		{
			initGSon();
		}

		Type configListType = new TypeToken<List<ExternalSearchServiceConfig>>() {}.getType();

		try
		{
			List<ExternalSearchServiceConfig> result = configParser.fromJson(configsString, configListType);
			if (result == null)
			{
				return new ArrayList<ExternalSearchServiceConfig>();
			}
			else
			{
				return result;
			}
		}
		catch (JsonSyntaxException jsex)
		{
			throw new ConfigurationError("The JSON parsing library threw an exception.", jsex);
		}
	}

	private boolean isValidConfig(ExternalSearchServiceConfig config)
	{
		return config.getName() != null && !config.getName().equals("")
				&& config.getDataRetriever() != null
				&& config.getParser() != null
				&& config.getIndexer() != null;
	}

	/**
	 * <p>Converts the given String into service configurations and create, delete and notifies
	 * services about the change of configuration. The method does not know about current configurations
	 * and can therefore not determine if anything has changed in the configurations. It is up to each service
	 * to determine if it needs to update its configuration.</p>
	 * 
	 * <p>If the given String cannot be parsed as a list of configurations the method will return without notifying
	 * the services. Passing null as the argument has the same effect.</p>
	 * 
	 * <p>If a configuration object has a name that does not match any current service a new service it initiated
	 * based on the configuration. Similarly if an existing service does not match any of the configurations
	 * that service will be removed. Otherwise the service with a name matching the configurations is notified
	 * of the new configuration object by a call to its {@link ExternalSearchService#setConfig(ExternalSearchServiceConfig)}.</p>
	 * 
	 * @param configsString A String representation of an external search service configuration array.
	 */
	protected void updateConfigurations(String configsString)
	{
		if (configsString == null)
		{
			logger.info("No configuration specified for external search services");
		}
		else
		{
			List<ExternalSearchServiceConfig> configs = null;
			try
			{
				configs = parseConfiguartions(configsString);
			}
			catch (ConfigurationError cex)
			{
				logger.error("Failed to parse external search configs. Message: " + cex.getMessage());
				logger.warn("Failed to parse external search configs.", cex);
			}

			if (configs != null)
			{
				synchronized (services)
				{
					// Add added new services and update existing
					for (ExternalSearchServiceConfig config : configs)
					{
						if (!isValidConfig(config))
						{
							logger.warn("Config was invalid will not apply to service. Config: " + config);
							continue;
						}

						if (!services.containsKey(config.getName()))
						{
							initService(config);
						}
						else
						{
							logger.debug("Updating config for service. Name: " + config.getName());
							services.get(config.getName()).setConfig(config);
						}
					}

					// Remove removed services
					Iterator<String> serviceNameIterator = services.keySet().iterator();
					String serviceName;
					serviceLoop:while (serviceNameIterator.hasNext())
					{
						serviceName = serviceNameIterator.next();
						for (ExternalSearchServiceConfig config : configs)
						{
							if (config.getName() != null && serviceName.equals(config.getName()))
							{
								continue serviceLoop;
							}
						}
						services.get(serviceName).destroyService();
						serviceNameIterator.remove();
					}
				}
			}
		}
	}
	/**
	 * Support method for initializing new services. The new service is added to list
	 * if available services.
	 * @param config The configuration the new service should be based on.
	 */
	protected void initService(ExternalSearchServiceConfig config)
	{
		logger.info("Initing new service. Name: " + config.getName());
		ExternalSearchService newService = new ExternalSearchService(config);
		services.put(config.getName(), newService);
	}

	/**
	 * Initiates an update of the external search configuration. The configuration
	 * is read from the <em>CmsPropertyHandler</em>. This method is called automatically
	 * by the system whenever the application is notified of a change in the ServerNodeProperties.
	 */
	public void updateConfigurations()
	{
		String configsString = CmsPropertyHandler.getExternalSearchServiceConfigs();

		updateConfigurations(configsString);
	}

	/**
	 * Returns a list of all available services. The returned values are not deep copies so operations
	 * done on the services will affect the managed services.
	 * @return A list of all the current services.
	 */
	public Collection<ExternalSearchService> getAllServices()
	{
		return services.values();
	}

	/**
	 * Attempts to get a service with the given <em>serviceName</em>.  If the service does not exist
	 * null is returned.
	 * 
	 * @param serviceName The service to look for
	 * @return The service, if found.
	 */
	public ExternalSearchService getService(String serviceName)
	{
		return services.get(serviceName);
	}

	/**
	 * Halts the hearth beat loop of the manager. When the manager is stopped the services
	 * will still be searchable but they will never have their indexes updated again. If the manager
	 * is in the process of notifying the services it will finish that task before stopping.
	 */
	public void stopServices()
	{
		this.stopped = true;
	}

	/**
	 * Indicates if the manager has stopped the hearth beat loop.
	 * @return True if the manager has stopped, false otherwise.
	 */
	public boolean isStopped()
	{
		return this.stopped;
	}

	//////////////////////////////////////////////////////////////////////
	//  Runnable

	@Override
	public void run()
	{
		updateConfigurations();

		while (!stopped)
		{
			try
			{
				logger.info("Will go through external search services");

				synchronized (services)
				{
					for (final ExternalSearchService service : services.values())
					{
						logger.debug("Requesting service indexing start. Service: " + service);
						service.startIndexing();
					}
				}

				logger.info("Did go through external search services");

				try
				{
					Thread.sleep(30000);
				}
				catch (InterruptedException iex) {}
			}
			catch (Throwable tr)
			{
				logger.error("Error in external search manager loop. Message: " + tr.getMessage() + ". Type: " + tr.getClass());
				logger.warn("Error in external search manager loop.", tr);
			}
		}
		logger.warn("The external search manager was stopped!");
	}

	@Override
	protected void handleNotification(String className)
	{
		if (className.equals("ServerNodeProperties"))
		{
			logger.info("Received server node properties change");
			updateConfigurations();
		}
	}
}
