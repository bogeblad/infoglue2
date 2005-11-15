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
package org.infoglue.deliver.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.CmsJDOCallback;
import org.infoglue.cms.entities.content.impl.simple.SmallContentImpl;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.workflow.CustomClassExecutor;

import org.infoglue.deliver.util.CacheController;
import org.infoglue.deliver.util.RequestAnalyser;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author mattias
 *
 * This jobs searches for expiring contents or sitenodes and clears caches if found.
 */

public class ExpireCacheJob implements Job
{
    private final static Logger logger = Logger.getLogger(ExpireCacheJob.class.getName());

    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        try
        {
            CacheController.evictWaitingCache();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        logger.info("---" + context.getJobDetail().getFullName() + " executing.[" + new Date() + "]");
        
        try
        {
            Date firstExpireDateTime = CacheController.expireDateTime;
            logger.info("firstExpireDateTime:" + firstExpireDateTime);
            Date now = new Date();
            
            if(firstExpireDateTime != null && now.after(firstExpireDateTime))
            {
                logger.warn("setting block");
                RequestAnalyser.setBlockRequests(true);

				try
                {
	        	    String operatingMode = CmsPropertyHandler.getProperty("operatingMode");
	        	    if(operatingMode != null && operatingMode.equalsIgnoreCase("3"))
	        	    {
	        	        logger.warn("Updating all caches as this was a publishing-update");
		    			CacheController.clearCastorCaches();
		
		    			logger.warn("clearing all except page cache as we are in publish mode..");
		    		    CacheController.clearCaches(null, null, new String[] {"pageCache", "NavigationCache", "pagePathCache", "userCache", "pageCacheParentSiteNodeCache", "pageCacheLatestSiteNodeVersions", "pageCacheSiteNodeTypeDefinition"});
		    			
		    			logger.warn("Recaching all caches as this was a publishing-update");
		    			CacheController.cacheCentralCastorCaches();
		    			
		    			logger.warn("Finally clearing page cache as this was a publishing-update");
		    		    CacheController.clearCache("pageCache");
	        	    }
	        	    else
	        	    {
		    		    logger.warn("Updating all caches as this was a publishing-update");
		    			CacheController.clearCastorCaches();
		
		    			logger.warn("clearing all except page cache as we are in publish mode..");
		    		    CacheController.clearCaches(null, null, null);
	        	    }
                }
                catch(Exception e)
                {
                    logger.error("An error occurred when we tried to update cache:" + e.getMessage(), e);
                }
    		    
    		    logger.warn("releasing block");
                RequestAnalyser.setBlockRequests(false);
            }

            Date firstPublishDateTime = CacheController.publishDateTime;
            logger.info("firstPublishDateTime:" + firstPublishDateTime);
            
            if(firstPublishDateTime != null && now.after(firstPublishDateTime))
            {
                logger.warn("setting block");
                RequestAnalyser.setBlockRequests(true);
                
                try
                {
	        	    String operatingMode = CmsPropertyHandler.getProperty("operatingMode");
	        	    if(operatingMode != null && operatingMode.equalsIgnoreCase("3"))
	        	    {
	        	        logger.warn("Updating all caches as this was a publishing-update");
		    			CacheController.clearCastorCaches();
		
		    			logger.warn("clearing all except page cache as we are in publish mode..");
		    		    CacheController.clearCaches(null, null, new String[] {"pageCache", "NavigationCache", "pagePathCache", "userCache", "pageCacheParentSiteNodeCache", "pageCacheLatestSiteNodeVersions", "pageCacheSiteNodeTypeDefinition"});
		    			
		    			logger.warn("Recaching all caches as this was a publishing-update");
		    			CacheController.cacheCentralCastorCaches();
		    			
		    			logger.warn("Finally clearing page cache as this was a publishing-update");
		    		    CacheController.clearCache("pageCache");
	        	    }
	        	    else
	        	    {
		    		    logger.warn("Updating all caches as this was a publishing-update");
		    			CacheController.clearCastorCaches();
		
		    			logger.warn("clearing all except page cache as we are in publish mode..");
		    		    CacheController.clearCaches(null, null, null);
	        	    }
                }
                catch(Exception e)
                {
                    logger.error("An error occurred when we tried to update cache:" + e.getMessage(), e);
                }

                logger.warn("releasing block");
                RequestAnalyser.setBlockRequests(false);
            }

        }
        catch (Exception e)
        {
            logger.error("An error occurred when we tried to update cache:" + e.getMessage(), e);
        }
    }
    

}
