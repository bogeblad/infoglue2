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

import java.util.Properties;
import java.io.*;
import java.util.Enumeration;
import org.infoglue.cms.util.CmsLogger;


/**
 * CMSPropertyHandler.java
 * Created on 2002-sep-12 
 * 
 * This class is used to get properties for the system in a transparent way.
 * 
 * @author Stefan Sik, ss@frovi.com
 * @author Mattias Bogeblad 
 */

public class CmsPropertyHandler
{
	private static Properties cachedProperties = null;
	private static Properties cachedUserProperties = null;
	
	private static String applicationName = null;
	private static File propertyFile = null;
	
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
			
			Enumeration enum = cachedProperties.keys();
			while(enum.hasMoreElements())
			{
				String key = (String)enum.nextElement();
				if(key.indexOf("webwork.") > 0)
				{
					webwork.config.Configuration.set(key, cachedProperties.getProperty(key)); 
				}
			}
			
		}	
		catch(Exception e)
		{
			cachedProperties = null;
			CmsLogger.logSevere("Error loading properties from file " + "/" + applicationName + ".properties" + ". Reason:" + e.getMessage());
			e.printStackTrace();
		}
		
	}

	/**
	 * This method initializes the parameter hash with values.
	 */

	private static void initializeUserProperties()
	{
		try
		{
			// User properties
			cachedUserProperties = new Properties();
			cachedUserProperties.load(new FileInputStream(new File(getProperty("UserPropertiesFile"))));
		}	
		catch(Exception e)
		{
			CmsLogger.logSevere("Error loading properties:" + e.getMessage());
			e.printStackTrace();
		}
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
	 * User Properties
	 * This method returns a propertyValue corresponding to the key supplied for the specified user.
	 */
	public static String getUserProperty(String userName, String key)
	{
		if(cachedUserProperties == null)
			initializeUserProperties();

		key = userName + "." + key;		
		String value = cachedUserProperties.getProperty(key);
		return value;
	}	
	
	public static void setUserProperty(String userName, String key, String value)
	{
		if(cachedUserProperties == null)
			initializeUserProperties();
		
		key = userName + "." + key;
		cachedUserProperties.setProperty(key, value);
		
		try {
			cachedUserProperties.store(new FileOutputStream(new File(getProperty("UserPropertiesFile"))), "Properties File");
		}
		catch (FileNotFoundException e){
			CmsLogger.logSevere("properties file not found");
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		CmsLogger.logInfo("Saving Key:" + key + " rendered " + value);
	}	

		

}
