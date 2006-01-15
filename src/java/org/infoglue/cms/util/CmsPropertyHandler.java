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

package org.infoglue.cms.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.io.*;
import java.net.InetAddress;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.infoglue.cms.controllers.kernel.impl.simple.ServerNodeController;
import org.infoglue.cms.entities.management.ServerNodeVO;
import org.infoglue.deliver.util.CacheController;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;



/**
 * CMSPropertyHandler.java
 * Created on 2002-sep-12 
 * 
 * This class is used to get properties for the system in a transparent way.
 * The second evolution of this class made it possible for properties to be fetched from the propertyset if there instead. Fallback to file.
 * 
 * @author Stefan Sik, ss@frovi.com
 * @author Mattias Bogeblad 
 */

public class CmsPropertyHandler
{
    private final static Logger logger = Logger.getLogger(CmsPropertyHandler.class.getName());

	private static Properties cachedProperties 		= null;
	private static PropertySet propertySet			= null; 

	private static String serverNodeName			= null;
	
	private static String globalSettingsServerNodeId= "-1";
	private static String localSettingsServerNodeId	= null;
	
	private static String applicationName 			= null;
	private static File propertyFile 				= null;
	
	public static void setApplicationName(String theApplicationName)
	{
		applicationName = theApplicationName;
	}
	
	public static String getApplicationName()
	{
		return applicationName;
	}
	
	public static void setPropertyFile(File aPropertyFile)
	{
		propertyFile = aPropertyFile;
	}
	
	/**
	 * This method initializes the parameter hash with values.
	 */

	private static void initializeProperties()
	{
	    try
		{
			System.out.println("**************************************");
			System.out.println("Initializing properties from file.....");
			System.out.println("**************************************");
			
			cachedProperties = new Properties();
			if(propertyFile != null)
			    cachedProperties.load(new FileInputStream(propertyFile));
			else
			    cachedProperties.load(CmsPropertyHandler.class.getResourceAsStream("/" + applicationName + ".properties"));
			
			Enumeration enumeration = cachedProperties.keys();
			while(enumeration.hasMoreElements())
			{
				String key = (String)enumeration.nextElement();
				if(key.indexOf("webwork.") > 0)
				{
					webwork.config.Configuration.set(key, cachedProperties.getProperty(key)); 
				}
			}
			
	        Map args = new HashMap();
		    args.put("globalKey", "infoglue");
		    propertySet = PropertySetManager.getInstance("jdbc", args);
		    
		    serverNodeName = cachedProperties.getProperty("serverNodeName");
		    
		    if(serverNodeName == null || serverNodeName.length() == 0)
		    {
			    InetAddress localhost = InetAddress.getLocalHost();
			    serverNodeName = localhost.getHostName();
		    }
		    
		    System.out.println("serverNodeName:" + serverNodeName);
		    
		    initializeLocalServerNodeId();
		}	
		catch(Exception e)
		{
			cachedProperties = null;
			logger.error("Error loading properties from file " + "/" + applicationName + ".properties" + ". Reason:" + e.getMessage());
			e.printStackTrace();
		}
		
	}

	/**
	 * This method gets the local server node id if available.
	 */

	public static void initializeLocalServerNodeId()
	{
        try
	    {
	        List serverNodeVOList = ServerNodeController.getController().getServerNodeVOList();
	        Iterator serverNodeVOListIterator = serverNodeVOList.iterator();
	        while(serverNodeVOListIterator.hasNext())
	        {
	            ServerNodeVO serverNodeVO = (ServerNodeVO)serverNodeVOListIterator.next();
	            if(serverNodeVO.getName().equalsIgnoreCase(serverNodeName))
	            {
	                localSettingsServerNodeId = serverNodeVO.getId().toString();
	                break;
	            }
	        }
	    }
	    catch(Exception e)
	    {
	        logger.warn("An error occurred trying to get localSettingsServerNodeId: " + e.getMessage(), e);
	    }
	    
	    System.out.println("localSettingsServerNodeId:" + localSettingsServerNodeId);
	}
	
	/**
	 * This method returns all properties .
	 */

	public static Properties getProperties()
	{
		if(cachedProperties == null)
			initializeProperties();
				
		return cachedProperties;
	}	

 
	/**
	 * This method returns a propertyValue corresponding to the key supplied.
	 */

	public static String getProperty(String key)
	{
		String value;
		if(cachedProperties == null)
			initializeProperties();
		
		value = cachedProperties.getProperty(key);
		if (value != null)
			value = value.trim();
				
		return value;
	}	


	/**
	 * This method sets a property during runtime.
	 */

	public static void setProperty(String key, String value)
	{
		if(cachedProperties == null)
			initializeProperties();
		
		cachedProperties.setProperty(key, value);
	}	

	/**
	 * This method gets the serverNodeProperty but also fallbacks to the old propertyfile if not found in the propertyset.
	 * 
	 * @param key
	 * @param inherit
	 * @return
	 */
	
	public static String getServerNodeProperty(String key, boolean inherit)
	{
	    String value = null;
	    
        String cacheKey = "" + key + "_" + inherit;
        String cacheName = "serverNodePropertiesCache";
		logger.info("cacheKey:" + cacheKey);
		value = (String)CacheController.getCachedObject(cacheName, cacheKey);
		if(value != null)
		{
			return value;
		}
	    
	    if(localSettingsServerNodeId != null)
	    {
	        value = propertySet.getString("serverNode_" + localSettingsServerNodeId + "_" + key);
	        System.out.println("Local value: " + value);
	        if(value == null || value.equals("") || value.equalsIgnoreCase("inherit") && inherit)
	        {
	            value = propertySet.getString("serverNode_" + globalSettingsServerNodeId + "_" + key);
		        System.out.println("Global value: " + value);
	        }
	    }
		else
		{
            value = propertySet.getString("serverNode_" + globalSettingsServerNodeId + "_" + key);
	        System.out.println("Global value immediately: " + value);
		}
	    
	    if(value == null || value.equals("") || value.equalsIgnoreCase("inherit") && inherit)
	    {
	        value = getProperty(key);
	        System.out.println("Property value: " + value);
	    }
	    
	    CacheController.cacheObject(cacheName, cacheKey, value);
	    
	    return value;
	}

	public static String getIsPageCacheOn()
	{
	    return getServerNodeProperty("isPageCacheOn", true);
	}

	
	public static String getPreferredLanguageCode(String userName)
	{
        return propertySet.getString("principal_" + userName + "_languageCode");
	}

	public static String getPreferredToolId(String userName)
	{
	    return propertySet.getString("principal_" + userName + "_defaultToolId");
	}
		
	public static String getAnonymousPassword()
	{
		String password = "anonymous";
		String specifiedPassword = getProperty("security.anonymous.password");
		if(specifiedPassword != null && !specifiedPassword.equalsIgnoreCase("") && specifiedPassword.indexOf("security.anonymous.password") == -1)
			password = specifiedPassword;
		
		return password;
	}

	public static String getAnonymousUser()
	{
		String userName = "anonymous";
		String specifiedUserName = getProperty("security.anonymous.username");
		if(specifiedUserName != null && !specifiedUserName.equalsIgnoreCase("") && specifiedUserName.indexOf("security.anonymous.username") == -1)
			userName = specifiedUserName;
		
		return userName;
	}

}
