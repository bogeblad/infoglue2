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

package org.infoglue.deliver.util.webloggers;

import org.infoglue.cms.util.*;

import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The CommonLogger class implements the abstract Logger class.
 * The resulting log will conform to the
 * <a href="http://www.w3.org/Daemon/User/Config/Logging.html#common-logfile-format">common log format</a>).
 */

public class CommonLogger extends Logger
{

	private static String hostAddress = null;
	private static String hostName = null;

	/**
	 * Construct a new Logger instance.
	 */

	public CommonLogger()
	{
	}

    /**
     * Log the given HTTP transaction.
     * Not implemented yet!!!
     */

    public void logRequest(HttpServletRequest request, HttpServletResponse response, String pagePath, long duration)
    {
		StringBuffer sb = new StringBuffer();

		sb.append(defaultValueIfNull(request.getRemoteAddr())); 				//c-ip
		sb.append(" ");
		sb.append("-");											 				//???
		sb.append(" ");
		sb.append(defaultValueIfNull(request.getRemoteUser())); 				//cs-username
		sb.append(" ");
		sb.append("[" + defaultValueIfNull(getCurrentDate("dd/MMM/yyyy:HH:mm:ss")) + " " + getOffset() + "]");  	//date + time
		sb.append(" ");
		sb.append("\"" + request.getMethod() + " " + pagePath + " " + request.getProtocol() + "\"");  	//date + time
		sb.append(" ");
		sb.append("304");															//sc-status
		sb.append(" ");
		sb.append("-");			//sc-bytes
		sb.append(" ");
		sb.append("\"" + defaultValueIfNull(request.getHeader("Referer")) + "\"");			//cs(Referer)
		sb.append(" ");
		sb.append("\"" + defaultValueIfNull(request.getHeader("User-Agent")) + "\"");			//cs(User-Agent)

		writeRequest(getCurrentDate("yyyy-MM-dd"), sb.toString());
    }



    /**
     * Initialize this logger for the given server.
     * This method gets the server properties describe above to
     * initialize its various log files.
     */

    public void initialize()
    {
    }

	private List logBuffer = new ArrayList();

    /**
     * This method writes a request to the logfile
     */

    protected void writeRequest(String date, String row)
    {
    	logBuffer.add(row);

    	if(logBuffer.size() > 20)
    	{
    		String logPath = CmsPropertyHandler.getProperty("statisticsLogPath");
			String statisticsLogOneFilePerDay = CmsPropertyHandler.getProperty("statisticsLogOneFilePerDay");
			File file = new File(logPath + File.separator + "statistics.log");
			if(statisticsLogOneFilePerDay != null && statisticsLogOneFilePerDay.equalsIgnoreCase("true"))
				file = new File(logPath + File.separator + "stat" + date + ".log");

			boolean isFileCreated = file.exists();

			PrintWriter pout = null;
	    	try
	    	{
				pout = new PrintWriter(new FileOutputStream(file, true));
				if(!isFileCreated)
				{
				}

				Iterator i = logBuffer.iterator();
				while(i.hasNext())
				{
					pout.println(i.next().toString());
				}
			    pout.close();
	    	}
	    	catch(Exception e)
	    	{
	    		CmsLogger.logSevere(e.getMessage(), e);
	    	}
	    	finally
	    	{
	    		try
	    		{
	    			pout.close();
	    		}
	    		catch(Exception e)
	    		{
	    		}
	    	}

	    	logBuffer = new ArrayList();
    	}
    }


    /**
     * This method returns a date as a string.
     */

    public String getCurrentDate(String pattern)
    {
        /*
        SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, "PST");
        pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2*60*60*1000);
        pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*60*60*1000);
        */
        // Format the current time.
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(date);
		return dateString;
    }


    private String getOffset()
    {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		String offsetString = "";
		int offset = (cal.get(java.util.Calendar.ZONE_OFFSET) + cal.get(java.util.Calendar.DST_OFFSET)) / (60*1000*60);
		if(offset < 10 && offset > -10)
		{
			if(offset > 0)
				offsetString = "+0" + offset + "00";
			else
				offsetString = "-0" + offset + "00";
		}
		else
		{
			if(offset > 0)
				offsetString = "+" + offset + "00";
			else
				offsetString = "-" + offset + "00";
		}

		return offsetString;
    }

    public String getHostAddress()
    {
    	if(hostAddress != null)
    		return hostAddress;

    	String address = null;

    	try
    	{
    		address = java.net.InetAddress.getLocalHost().getHostAddress();
    	}
    	catch(Exception e)
    	{
    		CmsLogger.logSevere(e.getMessage(), e);
    	}

    	hostAddress = address;

    	return address;
    }

    public String getHostName()
    {
    	if(hostName != null)
    		return hostName;

    	String name = null;

    	try
    	{
    		name = java.net.InetAddress.getLocalHost().getHostName();
    	}
    	catch(Exception e)
    	{
    		CmsLogger.logSevere(e.getMessage(), e);
    	}

    	hostName = name;

    	return name;
    }


    public String defaultValueIfNull(String value)
    {
    	if(value == null || value.equals(""))
    		return "-";

    	return value;
    }
}



