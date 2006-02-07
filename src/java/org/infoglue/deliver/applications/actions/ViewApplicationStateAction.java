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

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.applications.common.Session;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.ServerNodeController;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.deliver.applications.filters.RedirectFilter;
import org.infoglue.deliver.applications.filters.ViewPageFilter;
import org.infoglue.deliver.controllers.kernel.impl.simple.*;
import org.infoglue.deliver.util.CacheController;
import org.infoglue.deliver.util.RequestAnalyser;
import org.infoglue.cms.util.*;
import org.infoglue.cms.io.*;

import webwork.action.Action;
import webwork.action.ActionContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.net.InetAddress;

import javax.servlet.http.HttpServletResponse;

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
    
    private Category getDeliverCategory()
    {
        Enumeration enumeration = Logger.getCurrentCategories();
        while(enumeration.hasMoreElements())
        {
            Category category = (Category)enumeration.nextElement();
            if(category.getName().equalsIgnoreCase("org.infoglue.deliver"))
                return category;
        }
        
        return null;
    }

    private Category getCastorJDOCategory()
    {
        Enumeration enumeration = Logger.getCurrentCategories();
        while(enumeration.hasMoreElements())
        {
            Category category = (Category)enumeration.nextElement();
            if(category.getName().equalsIgnoreCase("org.exolab.castor.jdo"))
                return category;
        }
        
        return null;
    }

    /**
     * This action allows clearing of the given cache manually.
     */
    public String doClearCache() throws Exception
    {
        if(!ServerNodeController.getController().getIsIPAllowed(this.getRequest()))
        {
            this.getResponse().setContentType("text/plain");
            this.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            this.getResponse().getWriter().println("You have no access to this view - talk to your administrator if you should.");
            
            return NONE;
        }
        
        CacheController.clearCache(cacheName);
        
        //this.getHttpSession().invalidate();
        
        return "cleared";
    }

    /**
     * This action allows setting of the loglevel on some basic classes.
     */
    public String doSetLogInfo() throws Exception
    {
        //ViewPageFilter.logger.setLevel(Level.INFO);
        //ViewPageAction.logger.setLevel(Level.INFO);
        //RedirectFilter.logger.setLevel(Level.INFO);
        CastorDatabaseService.logger.setLevel(Level.INFO);
        CacheController.logger.setLevel(Level.INFO);
        getDeliverCategory().setLevel(Level.INFO);
        getCastorJDOCategory().setLevel(Level.INFO);
        
        return "cleared";
    }

    /**
     * This action allows setting of the loglevel on some basic classes.
     */
    public String doSetLogWarning() throws Exception
    {
        //ViewPageFilter.logger.setLevel(Level.WARN);
        //ViewPageAction.logger.setLevel(Level.WARN);
        //RedirectFilter.logger.setLevel(Level.WARN);
        CastorDatabaseService.logger.setLevel(Level.WARN);
        CacheController.logger.setLevel(Level.WARN);
        getDeliverCategory().setLevel(Level.WARN);
        getCastorJDOCategory().setLevel(Level.WARN);
        
        return "cleared";
    }

    /**
     * This action allows setting of the loglevel on some basic classes.
     */
    public String doSetLogError() throws Exception
    {
        //ViewPageFilter.logger.setLevel(Level.ERROR);
        //ViewPageAction.logger.setLevel(Level.ERROR);
        //RedirectFilter.logger.setLevel(Level.ERROR);
        CastorDatabaseService.logger.setLevel(Level.ERROR);
        CacheController.logger.setLevel(Level.ERROR);
        getDeliverCategory().setLevel(Level.ERROR);
        getCastorJDOCategory().setLevel(Level.ERROR);

        return "cleared";
    }

    /**
     * This action allows clearing of the caches manually.
     */
    public String doClearCaches() throws Exception
    {
        if(!ServerNodeController.getController().getIsIPAllowed(this.getRequest()))
        {
            this.getResponse().setContentType("text/plain");
            this.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            this.getResponse().getWriter().println("You have no access to this view - talk to your administrator if you should.");
            
            return NONE;
        }
        
        CacheController.clearCastorCaches();
        CacheController.clearCaches(null, null, null);
        
        return "cleared";
    }

    /**
     * This action allows recaching of some parts of the caches manually.
     */
    public String doReCache() throws Exception
    {
        if(!ServerNodeController.getController().getIsIPAllowed(this.getRequest()))
        {
            this.getResponse().setContentType("text/plain");
            this.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            this.getResponse().getWriter().println("You have no access to this view - talk to your administrator if you should.");
            
            return NONE;
        }
        
        CacheController.cacheCentralCastorCaches();
        
        return "cleared";        
    }
    
    private List getList(String key, String value)
    {
        List list = new ArrayList();
        list.add(key);
        list.add(value);

        return list;
    }
    
    public String doGC() throws Exception
    {
        if(!ServerNodeController.getController().getIsIPAllowed(this.getRequest()))
        {
            this.getResponse().setContentType("text/plain");
            this.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            this.getResponse().getWriter().println("You have no access to this view - talk to your administrator if you should.");
            
            return NONE;
        }
        
        Runtime.getRuntime().gc();
        
        return doExecute();
    }
    
    /**
     * This method is the application entry-point. The method does a lot of checks to see if infoglue
     * is installed correctly and if all resources needed are available.
     */
         
    public String doExecute() throws Exception
    {
        long start = System.currentTimeMillis();
        if(!ServerNodeController.getController().getIsIPAllowed(this.getRequest()))
        {
            this.getResponse().setContentType("text/plain");
            this.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            this.getResponse().getWriter().println("You have no access to this view - talk to your administrator if you should.");
            
            return NONE;
        }
        
        String sessionTimeout = CmsPropertyHandler.getProperty("session.timeout");
		if(sessionTimeout == null)
		    sessionTimeout = "1800";
		
        states.add(getList("Maximum memory", "" + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB"));
        states.add(getList("Used memory", "" + ((Runtime.getRuntime().totalMemory()- Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " MB"));
        states.add(getList("Free memory", "" + Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB"));
        states.add(getList("Total memory", "" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB"));
        states.add(getList("Number of sessions", "" + CmsSessionContextListener.getActiveSessions() + "(remains for " + (Integer.parseInt(sessionTimeout) / 60) + " minutes after last request)"));
        states.add(getList("Number of request being handled now", "" + RequestAnalyser.getRequestAnalyser().getNumberOfCurrentRequests()));
        //states.add(getList("Number of request being handled now", "" + RequestAnalyser.getNumberOfCurrentRequests() + "(average request take " + (RequestAnalyser.getAverageTimeSpentOnOngoingRequests()) + " ms, max now is " + RequestAnalyser.getMaxTimeSpentOnOngoingRequests() + ")"));
        //states.add(getList("The slowest request handled now is", "" + ((RequestAnalyser.getLongestRequests() != null) ? RequestAnalyser.getLongestRequests().getAttribute("progress") : "")));
        
        /*
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
		*/
				
		//this.getHttpSession().invalidate();

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

	public Map getEventListeners()
	{
		return CacheController.getEventListeners();
	}

    public List getStates()
    {
        return states;
    }
    
    public void setCacheName(String cacheName)
    {
        this.cacheName = cacheName;
    }

    public List getSessionInfoBeanList()
    {
    	return CmsSessionContextListener.getSessionInfoBeanList();
    }
    
    public String getServerName()
    {
    	String serverName = "Unknown";

    	try
    	{
		    InetAddress localhost = InetAddress.getLocalHost();
		    serverName = localhost.getHostName();
    	}
    	catch(Exception e)
    	{
    		
    	}
    	
	    return serverName;
    }
}
