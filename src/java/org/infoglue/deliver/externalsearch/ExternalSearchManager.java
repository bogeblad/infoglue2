/**
 * 
 */
package org.infoglue.deliver.externalsearch;

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
import java.lang.reflect.Type;

/**
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
	 * Used for testing
	 */
	public static void injectManager(ExternalSearchManager fakeManager)
	{
		manager = fakeManager;
	}

	public synchronized static void initManager()
	{
		if (manager == null)
		{
			manager = new ExternalSearchManager();
			Thread thread = new Thread (manager);
			thread.setName("ExternalSearchManager");
			thread.start();
		}
	}

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
		class DelegateDeserializer<T extends ConfigurableDelegate> implements JsonDeserializer<T>
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
	protected void initService(ExternalSearchServiceConfig config)
	{
		logger.info("Initing new service");
		ExternalSearchService newService = new ExternalSearchService(config);
		services.put(config.getName(), newService);
	}

	public void updateConfigurations()
	{
		String configsString = CmsPropertyHandler.getExternalSearchServiceConfigs();

		updateConfigurations(configsString);
	}

	public Collection<ExternalSearchService> getAllServices()
	{
		return services.values();
	}

	public ExternalSearchService getService(String serviceName)
	{
		return services.get(serviceName);
	}

	public void stopService()
	{
		this.stopped = true;
	}

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
						service.startIndexing();
					}
				}

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
