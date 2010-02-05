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

package org.infoglue.deliver.util;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.infoglue.cms.controllers.kernel.impl.simple.CmsJDOCallback;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.mail.MailServiceFactory;

/*
 *  Kill a thread after a given timeout has elapsed
 */

public class ThreadMonitor implements Runnable
{

    private final static Logger logger = Logger.getLogger(ThreadMonitor.class.getName());

	private Thread targetThread;
	private long millis;
	private long started;
	private Thread watcherThread;
	private boolean loop;
	private boolean enabled;
	private HttpServletRequest request;
	private String message;
	private boolean kill = false;
	private long threadId;
	private static long lastSentTimer = System.currentTimeMillis();
	private long startTimeInNs;
		
	/// Constructor.  Give it a thread to watch, and a timeout in milliseconds.
	// After the timeout has elapsed, the thread gets killed.  If you want
	// to cancel the kill, just call done().
	public ThreadMonitor(Thread targetThread, long millis, HttpServletRequest request, String message, boolean kill)
	{
		this.targetThread = targetThread;
		this.millis = millis;
		this.started = System.currentTimeMillis();
		watcherThread = new Thread(this);
		enabled = true;
		this.request = request;
		this.message = message;
		this.kill = kill;
		this.threadId = Thread.currentThread().getId();
		
		if(millis > 0)
			watcherThread.start();

		// Hack - pause a bit to let the watcher thread get started.
		/*
		try
		{
			Thread.sleep(100);
		} 
		catch (InterruptedException e)
		{
		}
		*/
	}

	/// Constructor, current thread.
	public ThreadMonitor(long millis, HttpServletRequest request, String message, boolean kill)
	{
		this(Thread.currentThread(), millis, request, message, kill);
	}

	/// Call this when the target thread has finished.
	public synchronized void done()
	{
		loop = false;
		enabled = false;
		notify();
	}

	/// Call this to restart the wait from zero.
	public synchronized void reset()
	{
		loop = true;
		notify();
	}

	/// Call this to restart the wait from zero with a different timeout value.
	public synchronized void reset(long millis)
	{
		this.millis = millis;
		reset();
	}

	/// The watcher thread - from the Runnable interface.
	// This has to be pretty anal to avoid monitor lockup, lost
	// threads, etc.
	public synchronized void run()
	{
		Thread me = Thread.currentThread();
		me.setPriority(Thread.MAX_PRIORITY);

		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    	startTimeInNs = threadMXBean.getCurrentThreadCpuTime();

		if (enabled)
		{
			do
			{
				loop = false;
				try
				{
					wait(millis);
				} 
				catch (InterruptedException e)
				{
				}
			} 
			while (enabled && loop);
		}
		
		if (enabled && targetThread.isAlive())
		{
			printThread();
			if(kill)
			{
				logger.error("Trying to kill thread with id:" + threadId);
				//targetThread.interrupt();
				//targetThread.stop();
			}
		}
	}

	
	private void printThread()
	{
		long now = System.currentTimeMillis();
		
		//Only sends if the last stack was sent more than 3 seconds ago.
		if((now - lastSentTimer) > 3000)
		{			
	    	StackTraceElement[] el = targetThread.getStackTrace();

	        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        	long stopTimeInNs = threadMXBean.getCurrentThreadCpuTime();
	        long diff = (stopTimeInNs - startTimeInNs) / 1000000000;
        	//System.out.println("diff:" + diff);
        	
	        StringBuffer stackString = new StringBuffer("\n\n" + message + "\n\n");
	        stackString.append("ServerName: " + getServerName() + "\n");
	        stackString.append("Maximum memory (MB): " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "\n");
	        stackString.append("Used memory (MB): " + ((Runtime.getRuntime().totalMemory()- Runtime.getRuntime().freeMemory()) / 1024 / 1024) + "\n");
	        stackString.append("Free memory (MB): " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + "\n");
	        stackString.append("Total memory (MB): " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "\n");
	        stackString.append("Number of current requests: " + RequestAnalyser.getRequestAnalyser().getNumberOfCurrentRequests() + "\n");
	        stackString.append("Number of active requests: " + RequestAnalyser.getRequestAnalyser().getNumberOfActiveRequests() + "\n");
	        stackString.append("Number of long requests: " + RequestAnalyser.getLongThreadMonitors().size() + "\n");
	        stackString.append("Current thread time: " + diff + " seconds\n");
	        stackString.append("Average time: " + RequestAnalyser.getRequestAnalyser().getAverageElapsedTime() + "\n");
	        stackString.append("Longest time: " + RequestAnalyser.getRequestAnalyser().getMaxElapsedTime() + "\n");
	        stackString.append("Original url: " + getOriginalFullURL() + "\n");
	        stackString.append("UserInfo: " + getUserInfo() + "\n");
	        stackString.append("--------------------------------------------\n\n");
	        stackString.append("Thread with id [" + threadId + "] at report time:\n");
	        if (el != null && el.length != 0)
	        {
	            for (int j = 0; j < el.length; j++)
	            {
	            	StackTraceElement frame = el[j];
	            	if (frame == null)
	            		stackString.append("    null stack frame" + "\n");
	            	else	
	            		stackString.append("    ").append(frame.toString()).append("\n");
	            	
	            	if((stackString.indexOf("infoglue") > -1 && j > 20) || j > 35)
	            		break;
				}                    
	       	}
	
			stackString.append("\n\n**********************************\nConcurrent long threads (Only an excerpt of all)\n**********************************");
	
		    ThreadMXBean t = ManagementFactory.getThreadMXBean();
	
	        List threadMonitors = RequestAnalyser.getLongThreadMonitors();
	        Iterator threadMonitorsIterator = threadMonitors.iterator();
	        int threadCount = 0;
	        while(threadMonitorsIterator.hasNext() && threadCount < 5)
		    {
				ThreadMonitor tm = (ThreadMonitor)threadMonitorsIterator.next();
				
				if(threadId == tm.getThreadId())
					continue;
				
				long threads[] = {tm.getThreadId()};
			    ThreadInfo[] tinfo = t.getThreadInfo(threads, 20);
	
				stackString.append("\n\n---------------------------------\nConcurrent long thread [").append(tm.getThreadId()).append("]:\n");
				stackString.append("Elapsed time:").append(tm.getElapsedTime()).append("\n Thread id: ").append(tm.getThreadId()).append("\n Original url: ").append(tm.getOriginalFullURL()).append(")");
	
		        for (int i=0; i<tinfo.length; i++)
			    {
					ThreadInfo e = tinfo[i];
			
			        el = e.getStackTrace();
			        
			        if (el != null && el.length != 0)
			        {
			            for (int n = 0; n < el.length; n++)
			            {
			            	StackTraceElement frame = el[n];
			            	if (frame == null)
			            		stackString.append("    null stack frame\n");
			            	else	
			            		stackString.append("    null stack frame").append(frame.toString()).append("\n");
						}                    
			       	}
			    }
		        	    
				threadCount++;
		    }
	        		 
	        logger.warn(stackString);
	
	        String warningEmailReceiver = CmsPropertyHandler.getWarningEmailReceiver();
	        if(warningEmailReceiver != null && !warningEmailReceiver.equals("") && warningEmailReceiver.indexOf("@warningEmailReceiver@") == -1)
	        {
				try
				{
					MailServiceFactory.getService().sendEmail(CmsPropertyHandler.getMailContentType(), warningEmailReceiver, warningEmailReceiver, null, null, null, null, message, stackString.toString().replaceAll("\n", "<br/>"), "utf-8");
				} 
				catch (Exception e)
				{
					logger.error("Could not send mail:" + e.getMessage(), e);
				}
	        }

	        lastSentTimer = System.currentTimeMillis();
		}
		else
		{
			logger.warn("A thread took to long but the system seems to be really clogged so we don't send this one.");
		}
		
	}
	
	/**
	 * This method returns the exact full url from the original request - not modified
	 * @return
	 */
	
	public String getOriginalFullURL()
	{
    	String originalRequestURL = this.request.getParameter("originalRequestURL");
    	if(originalRequestURL == null || originalRequestURL.length() == 0)
    		originalRequestURL = this.request.getRequestURL().toString();

    	String originalQueryString = this.request.getParameter("originalQueryString");
    	if(originalQueryString == null || originalQueryString.length() == 0)
    		originalQueryString = this.request.getQueryString();

    	return originalRequestURL + "?" + originalQueryString;
	}

	/**
	 * This method returns userinfo from the original request
	 * @return
	 */
	
	public String getUserInfo()
	{
		String userAgent = this.request.getHeader("user-agent");
        if(userAgent != null) 
        	userAgent = userAgent.toLowerCase();
        
        String userIP = this.request.getRemoteAddr();
        
    	return userAgent + " (" + userIP + ")";
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


	public long getMillis() 
	{
		return millis;
	}

	public long getStarted() 
	{
		return this.started;
	}
	
	public long getElapsedTime()
	{
		return System.currentTimeMillis() - this.started;
	}

	public long getThreadId() 
	{
		return threadId;
	}

}
