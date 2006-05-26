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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoglue.cms.util.CmsPropertyHandler;

/**
 * @author Mattias Bogeblad
 */
public class RequestAnalyser
{
    private static RequestAnalyser instance = new RequestAnalyser();
    //public Integer numberOfCurrentRequests = new Integer(0);
    
	//private static List currentRequests = new ArrayList();
    private static HttpServletRequest lastRequest = null;
    private static HttpServletResponse lastResponse = null;
    
	private static int maxClientsInt = 0;
	private static boolean blockRequests = false;
	
	static
	{
	    final String maxClients = CmsPropertyHandler.getMaxClients();
        if(maxClients != null && !maxClients.equals("") && maxClients.indexOf("@") == -1)
        {
            try
            {
                maxClientsInt = new Integer(maxClients).intValue();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

	}

	
	public static RequestAnalyser getRequestAnalyser()
	{
	    return instance;
	}
	
    public int getNumberOfCurrentRequests()
    {
        return Counter.getNumberOfCurrentRequests();
    }
    
    public void incNumberOfCurrentRequests()
    {
        Counter.incNumberOfCurrentRequests();
        /*
        synchronized(numberOfCurrentRequests)
        {
            numberOfCurrentRequests = new Integer(numberOfCurrentRequests.intValue() + 1);
        }
        */ 
    }

    public synchronized void decNumberOfCurrentRequests()
    {
        Counter.decNumberOfCurrentRequests();
        /*
        synchronized(numberOfCurrentRequests)
        {
            numberOfCurrentRequests = new Integer(numberOfCurrentRequests.intValue() - 1);
        } 
        */
    }

	/*
    public static int getNumberOfCurrentRequests()
    {
        synchronized(currentRequests)
        {
            return currentRequests.size();
        }
    }

    public static HttpServletRequest getLongestRequests()
    {
        HttpServletRequest longestRequest = null;
        
        long firstStart = System.currentTimeMillis();
        synchronized(currentRequests)
        {
	        Iterator i = currentRequests.iterator();
	        while(i.hasNext())
	        {
	            HttpServletRequest request = (HttpServletRequest)i.next();
	            Long startTime = (Long)request.getAttribute("startTime");
	            if(startTime.longValue() < firstStart)
	                longestRequest = request;
	        }
        }
        
        return longestRequest;
    }

    public static int getAverageTimeSpentOnOngoingRequests()
    {
        if(getNumberOfCurrentRequests() > 0)
        {
	        long elapsedTime = 0;
	        long now = System.currentTimeMillis();
	        synchronized(currentRequests)
	        {
	            Iterator i = currentRequests.iterator();
		        while(i.hasNext())
		        {
		            HttpServletRequest request = (HttpServletRequest)i.next();
		            Long startTime = (Long)request.getAttribute("startTime");
		            elapsedTime = elapsedTime + (now - startTime.longValue());
		        }
	        }
	        
	        return (int)elapsedTime / getNumberOfCurrentRequests();
        }
        return 0;
    }

    public static long getMaxTimeSpentOnOngoingRequests()
    {
        HttpServletRequest request = getLongestRequests();
        if(request != null)
        {
            Long firstStart = (Long)request.getAttribute("startTime");
            long now = System.currentTimeMillis();
            
            return (now - firstStart.longValue());
        }    
        
        return 0;
    }
    
    public static int getMaxClients()
    {
        return maxClientsInt;
    }
    
    public static void setMaxClients(int maxClientsInt)
    {
        RequestAnalyser.maxClientsInt = maxClientsInt;
    }
    */
    
    /*
    public static List getCurrentRequests()
    {
        return currentRequests;
    }
    */
    
    /*
    public static boolean getBlockRequests()
    {
        return blockRequests;
    }
    */
    
    public boolean getBlockRequests()
    {
        return Blocker.getIsBlocking();
    }

    public void setBlockRequests(boolean blockRequests)
    {
    	//try { throw new Exception("Apa"); }catch(Exception e) {e.printStackTrace(); }
    	Blocker.setBlocking(blockRequests);
    }

/*
    public static void setBlockRequests(boolean blockRequests)
    {
    	//try { throw new Exception("Apa"); }catch(Exception e) {e.printStackTrace(); }
        RequestAnalyser.blockRequests = blockRequests;
    }
*/    
    public static HttpServletRequest getLastRequest()
    {
        return lastRequest;
    }
    
    public static void setLastRequest(HttpServletRequest lastRequest)
    {
        RequestAnalyser.lastRequest = lastRequest;
    }
    
    public static HttpServletResponse getLastResponse()
    {
        return lastResponse;
    }
    
    public static void setLastResponse(HttpServletResponse lastResponse)
    {
        RequestAnalyser.lastResponse = lastResponse;
    }
}
