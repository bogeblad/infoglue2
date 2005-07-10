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

package org.infoglue.deliver.applications.actions;

import org.exolab.castor.jdo.Database;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.deliver.controllers.kernel.impl.simple.*;
import org.infoglue.deliver.util.CacheController;
import org.infoglue.cms.util.*;
import org.infoglue.cms.io.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

/**
 * This is the action that shows the application state and also can be used to set up surveilence.
 * The idea is to have one command which allways returns a known resultpage if it's ok. Otherwise it prints
 * an error-statement. This action is then called every x minutes by the surveilence and an alarm is raised if something is wrong.
 * We also have a command which can list more status about the application.
 *
 * @author Mattias Bogeblad
 */

public class ViewApplicationStateAction extends InfoGlueAbstractAction 
{
    private List states 					= new ArrayList();
    
	private boolean databaseConnectionOk 	= false;
	private boolean applicationSettingsOk 	= false;
	private boolean testQueriesOk			= false;
	private boolean diskPermissionOk 		= false;
	
	private String cacheName				= "";

	/**
	 * The constructor for this action - contains nothing right now.
	 */
    
    public ViewApplicationStateAction() 
    {
	
    }
    
    /**
     * This action allows clearing of the given cache manually.
     */
    public String doClearCache() throws Exception
    {
        CacheController.clearCache(cacheName);
        
        return "cleared";
    }

    /**
     * This action allows clearing of the caches manually.
     */
    public String doClearCaches() throws Exception
    {
        CacheController.clearCastorCaches();
        CacheController.clearCaches(null, null);
        
        return "cleared";
    }

    private List getList(String key, String value)
    {
        List list = new ArrayList();
        list.add(key);
        list.add(value);

        return list;
    }
    /**
     * This method is the application entry-point. The method does a lot of checks to see if infoglue
     * is installed correctly and if all resources needed are available.
     */
         
    public String doExecute() throws Exception
    {
        states.add(getList("Maximum memory", "" + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB"));
        states.add(getList("Free memory", "" + Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB"));
        states.add(getList("Total memory", "" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB"));
        
        Database db = CastorDatabaseService.getDatabase();
		
		beginTransaction(db);

		try
		{
			List repositoryVOList = RepositoryDeliveryController.getRepositoryDeliveryController().getRepositoryVOList(db);
			getLogger().info("Number of repositories:" + repositoryVOList.size());
			this.databaseConnectionOk = true;

	        closeTransaction(db);
		}
		catch(Exception e)
		{
			getLogger().error("An error occurred so we should not complete the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
		
		this.applicationSettingsOk 	= true;
		this.testQueriesOk 			= true;
		
		try
		{
			File testAsset = new File(CmsPropertyHandler.getProperty("digitalAssetPath") + File.separator + "test.txt");
			FileHelper.writeToFile(testAsset, "ViewApplicationState checking file permissions in asset directory", false);
			testAsset.delete();
			
			File testLog = new File(new File(CmsPropertyHandler.getProperty("logPath")).getParent() + File.separator + "test.txt");
			FileHelper.writeToFile(testLog, "ViewApplicationState checking file permissions in logs directory", false);
			testLog.delete();

			this.diskPermissionOk = true;
		}
		catch(Exception e)
		{
			getLogger().error(e.getMessage(), e);            
		}
		
        return "success";
    }
    

    
	public boolean getIsApplicationSettingsOk()
	{
		return applicationSettingsOk;
	}

	public boolean getIsDatabaseConnectionOk()
	{
		return databaseConnectionOk;
	}

	public boolean getIsDiskPermissionOk()
	{
		return diskPermissionOk;
	}

	public boolean getIsTestQueriesOk()
	{
		return testQueriesOk;
	}

	public Map getCaches()
	{
		return CacheController.getCaches();
	}

    public List getStates()
    {
        return states;
    }
    
    public void setCacheName(String cacheName)
    {
        this.cacheName = cacheName;
    }
}
