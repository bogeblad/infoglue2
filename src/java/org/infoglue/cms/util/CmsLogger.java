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

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Handler;

import java.io.*;

/**
 * This class is the log-utility. It uses the built in Java 1.4.1 logger and
 * is used to log on disk or wherever. 
 * 
 * @author Mattias Bogeblad
 */

public class CmsLogger 
{	
	private static Logger logger = null;
	
	/**
	 * This method initializes the logging-functionality. There are a couple of parameters in the
	 * property-files that can be set to influence the behaviour. We can for example state if we want
	 * the console to visualize the loginfo. 
	 */

	public static final void initializeLogger() 
	{
		try
		{
			logger = Logger.getLogger(CmsPropertyHandler.getApplicationName() + "Logger");
			
			Handler[] handlers = logger.getHandlers();
			for(int i=0; i<handlers.length; i++)
			{
				System.out.println("Removing old handler:" + handlers[i].toString());
				logger.removeHandler(handlers[i]);
			}
			
			logger.setUseParentHandlers(false);
			
			boolean logToConsole        = true;
			boolean logToFile           = false;
			String logLevel              = "INFO";
			
			String logToConsoleString = CmsPropertyHandler.getProperty("logToConsole");
			if(logToConsoleString != null && logToConsoleString.equalsIgnoreCase("false"))
				logToConsole = false;
	
			String logToFileString = CmsPropertyHandler.getProperty("logToFile");
			if(logToFileString != null && logToFileString.equalsIgnoreCase("true"))
				logToFile = true;
	
			String logLevelString = CmsPropertyHandler.getProperty("logLevel");
			if(logLevelString != null)
				logLevel = logLevelString;
				
				
			if(logToConsole)
			{
				ConsoleHandler ch = new ConsoleHandler();
				ch.setFormatter(new CmsSimpleFormatter());
				logger.addHandler(ch);
			}	
	
			if(logToFile)
			{
				FileHandler fh = new FileHandler(CmsPropertyHandler.getProperty("logPath"));
				fh.setFormatter(new CmsSimpleFormatter());
				logger.addHandler(fh);
			}
			
			logger.setLevel(Level.parse(logLevel));
			
			logInfo("*****************************************");
			logInfo("* Logger initialized with the following *");
			logInfo("*****************************************");
			logInfo("loggerName:"   + CmsPropertyHandler.getApplicationName() + "Logger");
			logInfo("logToConsole:" + logToConsole);
			logInfo("logToFile:"    + logToFile);
			logInfo("logLevel:"     + logLevel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	} 

/*
	public static void initializeLogger(String loggerName, boolean logToConsole, boolean logToFile, String logLevel) 
	{
		try
		{
			logger = Logger.getLogger(loggerName);
			logger.setUseParentHandlers(false);
			
			if(logToConsole)
			{
				ConsoleHandler ch = new ConsoleHandler();
				ch.setFormatter(new CmsSimpleFormatter());
				logger.addHandler(ch);
			}	
	
			if(logToFile)
			{
				FileHandler fh = new FileHandler(CmsPropertyHandler.getProperty("logFile"));
				fh.setFormatter(new CmsSimpleFormatter());
				logger.addHandler(fh);
			}
			
			logger.setLevel(Level.parse(logLevel));
			
			logInfo("*****************************************");
			logInfo("* Logger initialized with the following *");
			logInfo("*****************************************");
			logInfo("loggerName:"   + loggerName);
			logInfo("logToConsole:" + logToConsole);
			logInfo("logToFile:"    + logToFile);
			logInfo("logLevel:"     + logLevel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	} 
*/

	private static final Logger getLogger()
	{
		if(logger == null)
			initializeLogger();
			//initializeLogger("cmsLogger", true, true, "INFO");
			
		return logger;
	}
	
	/**
	 * This method logs a message as info.
	 */
	
	public static final void logInfo(String info)
	{
		try
		{
			getLogger().info(info);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}

	/**
	 * This method logs a message as warning.
	 */

	public static void logWarning(String warning)
	{
		try
		{
			getLogger().warning(warning);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}

	/**
	 * This method logs a message as servere.
	 */

	public static void logSevere(String severe)
	{
		try
		{
			getLogger().severe(severe);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}


	/**
	 * This method logs a message as info.
	 */
	
	public static void logInfo(String info, Exception e)
	{
		try
		{
			getLogger().info(info);
			getLogger().info(stackTraceToString(e));
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}

	/**
	 * This method logs a message as warning.
	 */

	public static void logWarning(String warning, Exception e)
	{
		try
		{
			getLogger().warning(warning);
			getLogger().warning(stackTraceToString(e));
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}

	/**
	 * This method logs a message as servere.
	 */

	public static void logSevere(String severe, Exception e)
	{
		try
		{
			getLogger().severe(severe);
			getLogger().severe(stackTraceToString(e));
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}


    /**
     * A utility-method used for fetching java stacktrace as a string.
     * 
     * @param e The exception thrown which contains the stacktrace.
     * @return A String.
     */

    private static String stackTraceToString(Exception e) 
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bytes, true);
        e.printStackTrace(writer);
        return bytes.toString();
    }

}