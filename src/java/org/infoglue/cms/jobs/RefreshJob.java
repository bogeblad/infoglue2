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
package org.infoglue.cms.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.TransactionNotInProgressException;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.CategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.RedirectController;
import org.infoglue.cms.controllers.kernel.impl.simple.TransactionHistoryController;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.util.ChangeNotificationController;
import org.infoglue.cms.util.NotificationMessage;
import org.infoglue.cms.util.RemoteCacheUpdater;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

/**
 * @author mattias
 *
 * This jobs searches for expiring contents or sitenodes and clears caches if found.
 */

public class RefreshJob implements Job
{
    private final static Logger logger = Logger.getLogger(RefreshJob.class.getName());

    public synchronized void execute(JobExecutionContext context) throws JobExecutionException
    {
    	logger.error("*********************************************************************");
    	logger.error("* Starting refresh job which should run with nice intervals         *");
    	logger.error("* Purpose is to keep connection pool alive and to clear live caches *");
    	logger.error("*********************************************************************");

		try
		{
	    	logger.error("Fetching redirects just to get anything really - really.");
			Database db1 = CastorDatabaseService.getDatabase();
			Database db2 = CastorDatabaseService.getDatabase();
			Database db3 = CastorDatabaseService.getDatabase();
			Database db4 = CastorDatabaseService.getDatabase();
			Database db5 = CastorDatabaseService.getDatabase();
			getRedirects(db1);
			getRedirects(db2);
			getRedirects(db3);
			getRedirects(db4);
			getRedirects(db5);
		}
		catch (Exception e) 
		{
			logger.error("Problem getting data.");
		}
		
		try
		{
		    logger.error("Getting propertySet...");
			Map args = new HashMap();
		    args.put("globalKey", "infoglue");
		    PropertySet propertySet = PropertySetManager.getInstance("jdbc", args);
		}
		catch(Exception e)
	    {
	    	logger.error("Could not get property set: " + e.getMessage());
	    }
		
		try
		{
		    logger.error("Notifying caches...");
			NotificationMessage notificationMessage = new NotificationMessage("NightlyRefreshJob.execute():", "ServerNodeProperties", "administrator", NotificationMessage.SYSTEM, "0", "ServerNodeProperties");
		    ChangeNotificationController.getInstance().addNotificationMessage(notificationMessage);
        	ChangeNotificationController.notifyListeners();
	        RemoteCacheUpdater.clearSystemNotificationMessages();
		}
		catch(Exception e)
	    {
	    	logger.error("Could not get property set: " + e.getMessage());
	    }
	   
	   	logger.error("Refresh-job finished");
    }

	private void getRedirects(Database db) throws Bug, TransactionNotInProgressException, PersistenceException
	{
		try
		{
			db.begin();
			
			List redirects = RedirectController.getController().getRedirectVOList(db);
			logger.error("redirects:" + redirects.size());
			
			db.commit();
		}
		catch (Exception e) 
		{
			logger.error("Problem getting data.");
			db.rollback();
		}
		finally
		{
			db.close();
		}
	}
    
}
