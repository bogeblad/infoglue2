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

package org.infoglue.deliver.cache;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.TransactionHistoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.NotificationMessage;
import org.infoglue.deliver.applications.databeans.DatabaseWrapper;
import org.infoglue.deliver.applications.databeans.DeliveryContext;
import org.infoglue.deliver.controllers.kernel.impl.simple.BasicTemplateController;
import org.infoglue.deliver.controllers.kernel.impl.simple.IntegrationDeliveryController;
import org.infoglue.deliver.controllers.kernel.impl.simple.NodeDeliveryController;
import org.infoglue.deliver.util.LiveInstanceMonitor;

/*
 *  This class keeps track of all live deliver states by polling them with regular intervals. 
 *  It also acts as a message queue so publication messages are resent if not successful the first time.
 */

public class MatchingContentsQueue implements Runnable
{
    private final static Logger logger = Logger.getLogger(MatchingContentsQueue.class.getName());

	private static MatchingContentsQueue singleton = null;
	
	private Map<String, MatchingContentsQueueBean> instanceMatchingContentsQueueBeans = new HashMap<String, MatchingContentsQueueBean>();
	private Map<String, String> instancePublicationQueueMeta = new HashMap<String, String>();
    
	private boolean keepRunning = true;
	
	private MatchingContentsQueue()
	{
	}

	/**
	 * Get the singleton and start the thread if not active
	 */
	public static MatchingContentsQueue getMatchingContentsQueue()
	{
		if(singleton == null)
		{
			singleton = new MatchingContentsQueue();
			Thread thread = new Thread (singleton);
			thread.start();
		}
		
		return singleton;
	}
	
	public Map<String, MatchingContentsQueueBean> getInstanceMatchingContentsQueueBeans()
	{
		return instanceMatchingContentsQueueBeans;
	}
	
	/**
	 * This method gets when the queued beans for a specific instance.
	 */
	public MatchingContentsQueueBean getInstanceMatchingContentsQueueBeans(String cacheKey)
	{
		synchronized (instanceMatchingContentsQueueBeans) 
		{
			if(instanceMatchingContentsQueueBeans.containsKey(cacheKey))
				return instanceMatchingContentsQueueBeans.get(cacheKey);
			else
				return null;
		}		
	}

	/**
	 * This method allows you to add a publication queue bean and register it against a certain deliver instance.
	 * It makes sure each bean is unique.
	 */
	public void addMatchingContentsQueueBean(String cacheKey, MatchingContentsQueueBean bean)
	{
		logger.info("Adding url:" + cacheKey);
		//MatchingContentsQueueBean matchingContentsQueueBean = instanceMatchingContentsQueueBeans.get(cacheKey);
		//if(matchingContentsQueueBean == null)
		//{
		synchronized (instanceMatchingContentsQueueBeans) 
		{
			instanceMatchingContentsQueueBeans.put(cacheKey, bean);			
		}
		//}
		/*
		synchronized(matchingContentsQueueBeans)
		{
			if(matchingContentsQueueBeans < 1000)
				matchingContentsQueueBeans.put(cacheKey, bean);
			else
				logger.error("Skipping queue for this bean as to many beans allready is in queue - must be something very wrong with the instance");
		}
		*/
		logger.info("Done...");
	}

	/**
	 * Allows for manual clearing of a live instance queue.
	 */
	public void clearMatchingContentsQueueBean(String cacheKey)
	{
		logger.error("Clearing queue manually for " + cacheKey);
		synchronized(instanceMatchingContentsQueueBeans)
		{
			instanceMatchingContentsQueueBeans.remove(cacheKey);
		}
		instancePublicationQueueMeta.put(cacheKey + "_manualClearTimestamp", "" + System.currentTimeMillis());
	}

	/**
	 * The thread runner - with each run it goes through all the queues and tries to call (POST) the deliver instance
	 * If the post fails the beans is kept in the queue and retried later if the instance is up at that time.  
	 */
	 
	public synchronized void run()
	{
		logger.info("Running HttpUniqueRequestQueue...");
		while(keepRunning)
		{
			logger.info("Running..: " + instanceMatchingContentsQueueBeans.size());
			if(instanceMatchingContentsQueueBeans.size() > 1000)
			{
				synchronized (instanceMatchingContentsQueueBeans) 
				{
					instanceMatchingContentsQueueBeans.clear();	
				}
			}
			
			Iterator<String> cacheKeysIterator = instanceMatchingContentsQueueBeans.keySet().iterator();
			while(cacheKeysIterator.hasNext())
			{
				String cacheKey = cacheKeysIterator.next();
				MatchingContentsQueueBean bean = instanceMatchingContentsQueueBeans.get(cacheKey);
				
				if(logger.isInfoEnabled())
					logger.info("MatchingContentsQueueBean cacheKey:" + cacheKey);
				
				try
				{
					long diff = (System.currentTimeMillis() - bean.getLastFetched()) / 1000;

					List cachedMatchingContents = null;
					DatabaseWrapper dbWrapperCached = new DatabaseWrapper(CastorDatabaseService.getDatabase());
					try
					{
						dbWrapperCached.getDatabase().begin();
						
						InfoGluePrincipal user = UserControllerProxy.getController(dbWrapperCached.getDatabase()).getUser(bean.getUserName());
						BasicTemplateController tc = new BasicTemplateController(dbWrapperCached, user);
						DeliveryContext deliveryContext = DeliveryContext.getDeliveryContext(false);
						tc.setDeliveryControllers(NodeDeliveryController.getNodeDeliveryController(null, null, null), null, null);	
						tc.setDeliveryContext(deliveryContext);

						cachedMatchingContents = tc.getMatchingContents(bean.getContentTypeDefinitionNames(), 
								   bean.getCategoryCondition(), 
								   bean.getFreeText(), 
								   bean.getFreeTextAttributeNamesList(), 
								   bean.getFromDate(), 
								   bean.getToDate(), 
								   bean.getExpireFromDate(),
								   bean.getExpireToDate(),
								   bean.getVersionModifier(), 
								   bean.getMaximumNumberOfItems(), 
								   true, 
								   true, 
								   bean.getCacheInterval(), 
								   bean.getCacheName(), 
								   bean.getCacheKey(), 
								   false,
								   bean.getScheduleInterval(),
								   bean.getRepositoryIdsList(), 
								   bean.getLanguageId(), 
								   bean.getSkipLanguageCheck(), 
								   bean.getStartNodeId(),
								   bean.getSortColumn(),
								   bean.getSortOrder(), 
								   false,
								   bean.getValidateAccessRightsAsAnonymous(), 
								   true);
						
						dbWrapperCached.getDatabase().rollback();
					}
					catch (Exception e) 
					{
						dbWrapperCached.getDatabase().rollback();
						logger.error("Error in matching contents:" + e.getMessage(), e);
					}
					finally
					{
						dbWrapperCached.getDatabase().close();
					}
					
					logger.info("diff:" + diff);
					logger.info("bean.getScheduleInterval()" + bean.getScheduleInterval());
					logger.info("Cached matches:" + (cachedMatchingContents == null ? "null" : cachedMatchingContents.size()));
					
					if(diff > bean.getScheduleInterval() || cachedMatchingContents == null || cachedMatchingContents.size() == 0)
					{
						logger.info("Running match either because the time was now or because no cached result was found");
						
						DatabaseWrapper dbWrapper = new DatabaseWrapper(CastorDatabaseService.getDatabase());
						try
						{
							dbWrapper.getDatabase().begin();
							
							InfoGluePrincipal user = UserControllerProxy.getController(dbWrapper.getDatabase()).getUser(bean.getUserName());
							BasicTemplateController tc = new BasicTemplateController(dbWrapper, user);
							DeliveryContext deliveryContext = DeliveryContext.getDeliveryContext(false);
							tc.setDeliveryControllers(NodeDeliveryController.getNodeDeliveryController(null, null, null), null, null);	
							tc.setDeliveryContext(deliveryContext);
							
							List matchingContents = tc.getMatchingContents(bean.getContentTypeDefinitionNames(), 
												   bean.getCategoryCondition(), 
												   bean.getFreeText(), 
												   bean.getFreeTextAttributeNamesList(), 
												   bean.getFromDate(), 
												   bean.getToDate(), 
												   bean.getExpireFromDate(),
												   bean.getExpireToDate(),
												   bean.getVersionModifier(), 
												   bean.getMaximumNumberOfItems(), 
												   true, 
												   true, 
												   bean.getCacheInterval(), 
												   bean.getCacheName(), 
												   bean.getCacheKey(), 
												   false,
												   bean.getScheduleInterval(),
												   bean.getRepositoryIdsList(), 
												   bean.getLanguageId(), 
												   bean.getSkipLanguageCheck(), 
												   bean.getStartNodeId(),
												   bean.getSortColumn(),
												   bean.getSortOrder(), 
												   true,
												   bean.getValidateAccessRightsAsAnonymous(), 
												   false);
							
							bean.setLastFetched(System.currentTimeMillis());
							
							logger.info("matchingContents in queue:" + matchingContents.size());

							dbWrapper.getDatabase().rollback();
						}
						catch (Exception e) 
						{
							dbWrapper.getDatabase().rollback();
							logger.error("Error in matching contents:" + e.getMessage(), e);
						}
						finally
						{
							dbWrapper.getDatabase().close();
						}
					}
				}
				catch(Exception e)
				{
					/*
					synchronized (instanceMatchingContentsQueueBeans)
					{
						Map<String, Set<MatchingContentsQueueBean>> currentLiveInstanceMatchingContentsQueueBeans = instanceMatchingContentsQueueBeans;
						Set<MatchingContentsQueueBean> currentMatchingContentsQueueBeans = currentLiveInstanceMatchingContentsQueueBeans.get(serverBaseUrl);
						if(currentMatchingContentsQueueBeans == null)
						{
							currentMatchingContentsQueueBeans = new HashSet<MatchingContentsQueueBean>();
							currentLiveInstanceMatchingContentsQueueBeans.put(serverBaseUrl, currentMatchingContentsQueueBeans);
						}
						currentMatchingContentsQueueBeans.addAll(beans);
					}
					*/

					logger.error("Error updating cache at " + cacheKey + ". We skip further tries for now and queue it:" + e.getMessage(), e);
				}
			}
	
			try
			{ 
				Thread.sleep(10000);
		    } 
			catch( InterruptedException e ) 
			{
				logger.error("Interrupted Exception caught");
		    }
		}
	}

}
