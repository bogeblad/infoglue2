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

package org.infoglue.cms.applications.common;

import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;

import java.util.Date;
import java.util.Locale;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class VisualFormatter
{
    public VisualFormatter()
    {
    }
    
    public String formatDate(Date date, String pattern)
    {	
    	CmsLogger.logInfo("About to convert " + date + " with the pattern " + pattern);
        if(date == null)
            return "";
            
        //CmsLogger.logInfo( TimeZone.getDefault().getID() );      
        /*
        SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, "PST");
        pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2*60*60*1000);
        pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*60*60*1000);
        */
        // Format the current time.
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(date);
		CmsLogger.logInfo("dateString: " + dateString);
        return dateString;
    }

	public String formatDate(Date date, Locale locale, String pattern)
	{	
		CmsLogger.logInfo("About to convert " + date + " with the pattern " + pattern);
		if(date == null)
			return "";
            
		// Format the current time.
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
		String dateString = formatter.format(date);
		CmsLogger.logInfo("dateString: " + dateString);
		return dateString;
	}
	
    public Date parseDate(String dateString, String pattern)
    {	
    	CmsLogger.logInfo("About to convert " + dateString + " with the pattern " + pattern);
        if(dateString == null)
            return new Date();
        
        Date date = new Date();    
        
        try
        {
	        CmsLogger.logInfo( TimeZone.getDefault().getID() );      
	        // Format the current time.
	        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
	        date = formatter.parse(dateString);
			CmsLogger.logInfo("date: " + date);
        }
        catch(Exception e)
        {
        	CmsLogger.logInfo("Error parsing date:" + dateString);
        }
        
        return date;
    }

	public Date parseDate(String dateString, Locale locale, String pattern)
	 {	
		 CmsLogger.logInfo("About to convert " + dateString + " with the pattern " + pattern);
		 if(dateString == null)
			 return new Date();
        
		 Date date = new Date();    
        
		 try
		 {
			 CmsLogger.logInfo( TimeZone.getDefault().getID() );      
			 // Format the current time.
			 SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
			 date = formatter.parse(dateString);
			 CmsLogger.logInfo("date: " + date);
		 }
		 catch(Exception e)
		 {
			 CmsLogger.logInfo("Error parsing date:" + dateString);
		 }
        
		 return date;
	 }
	
	/**
	 * This method converts all non-standard characters to html-equivalents.
	 */
	
	public final String escapeHTML(String s) throws Exception
	{
		if(s == null)
			return null;
			
	    StringBuffer sb = new StringBuffer();
		int n = s.length();
	    for (int i = 0; i < n; i++) 
	    {
	       	char c = s.charAt(i);
    	   	switch (c) 
	       	{
				case '<': sb.append("&lt;"); break;
	         	case '>': sb.append("&gt;"); break;
	         	case '&': sb.append("&amp;"); break;
	         	case '"': sb.append("&quot;"); break;
	         	/*
	         	case 'à': sb.append("&agrave;");break;
	         	case 'À': sb.append("&Agrave;");break;
	         	case 'â': sb.append("&acirc;");break;
	         	case 'Â': sb.append("&Acirc;");break;
	         	case 'ä': sb.append("&auml;");break;
	         	case 'Ä': sb.append("&Auml;");break;
	         	case 'å': sb.append("&aring;");break;
	         	case 'Å': sb.append("&Aring;");break;
	         	case 'æ': sb.append("&aelig;");break;
	         	case 'Æ': sb.append("&AElig;");break;
	         	case 'ç': sb.append("&ccedil;");break;
	         	case 'Ç': sb.append("&Ccedil;");break;
	         	case 'é': sb.append("&eacute;");break;
	         	case 'É': sb.append("&Eacute;");break;
	         	case 'è': sb.append("&egrave;");break;
	         	case 'È': sb.append("&Egrave;");break;
	         	case 'ê': sb.append("&ecirc;");break;
	         	case 'Ê': sb.append("&Ecirc;");break;
	         	case 'ë': sb.append("&euml;");break;
	         	case 'Ë': sb.append("&Euml;");break;
	         	case 'ï': sb.append("&iuml;");break;
	         	case 'Ï': sb.append("&Iuml;");break;
	         	case 'ô': sb.append("&ocirc;");break;
	         	case 'Ô': sb.append("&Ocirc;");break;
	         	case 'ö': sb.append("&ouml;");break;
	         	case 'Ö': sb.append("&Ouml;");break;
	         	case 'ø': sb.append("&oslash;");break;
	         	case 'Ø': sb.append("&Oslash;");break;
	         	case 'ß': sb.append("&szlig;");break;
	         	case 'ù': sb.append("&ugrave;");break;
	         	case 'Ù': sb.append("&Ugrave;");break;         
	         	case 'û': sb.append("&ucirc;");break;         
	         	case 'Û': sb.append("&Ucirc;");break;
	         	case 'ü': sb.append("&uuml;");break;
	         	case 'Ü': sb.append("&Uuml;");break;
	         	case '®': sb.append("&reg;");break;         
	         	case '©': sb.append("&copy;");break;   
	         	case '€': sb.append("&euro;"); break;
	         	*/

	         	default:  sb.append(c); break;
	      	}
	   	}
	   	return sb.toString();
	}
	

	/**
	 * 
	 * Temporary method, please do not use. (SS, 2004-12-13)
	 * @deprecated
	 */
	
	public final String escapeHTMLforXMLService(String s)
	{
		if(s == null)
			return null;
			
		StringBuffer sb = new StringBuffer();
		int n = s.length();
		for (int i = 0; i < n; i++) 
		{
			char c = s.charAt(i);
			switch (c) 
			{
				case '<': sb.append("&lt;"); break;
				case '>': sb.append("&gt;"); break;
				case '&': sb.append("&amp;"); break;
				case '"': sb.append("&quot;"); break;
				case 'à': sb.append("&agrave;");break;
				case 'À': sb.append("&Agrave;");break;
				case 'â': sb.append("&acirc;");break;
				case 'Â': sb.append("&Acirc;");break;
				case 'ä': sb.append("&auml;");break;
				case 'Ä': sb.append("&Auml;");break;
				case 'å': sb.append("&aring;");break;
				case 'Å': sb.append("&Aring;");break;
				case 'æ': sb.append("&aelig;");break;
				case 'Æ': sb.append("&AElig;");break;
				case 'ç': sb.append("&ccedil;");break;
				case 'Ç': sb.append("&Ccedil;");break;
				case 'é': sb.append("&eacute;");break;
				case 'É': sb.append("&Eacute;");break;
				case 'è': sb.append("&egrave;");break;
				case 'ò': sb.append("&ograve;");break;
				case 'È': sb.append("&Egrave;");break;
				case 'ê': sb.append("&ecirc;");break;
				case 'Ê': sb.append("&Ecirc;");break;
				case 'ë': sb.append("&euml;");break;
				case 'Ë': sb.append("&Euml;");break;
				case 'ï': sb.append("&iuml;");break;
				case 'Ï': sb.append("&Iuml;");break;
				case 'ô': sb.append("&ocirc;");break;
				case 'Ô': sb.append("&Ocirc;");break;
				case 'ö': sb.append("&ouml;");break;
				case 'Ö': sb.append("&Ouml;");break;
				case 'ø': sb.append("&oslash;");break;
				case 'Ø': sb.append("&Oslash;");break;
				case 'ß': sb.append("&szlig;");break;
				case 'ù': sb.append("&ugrave;");break;
				case 'Ù': sb.append("&Ugrave;");break;         
				case 'û': sb.append("&ucirc;");break;         
				case 'Û': sb.append("&Ucirc;");break;
				case 'ü': sb.append("&uuml;");break;
				case 'Ü': sb.append("&Uuml;");break;
				case '®': sb.append("&reg;");break;         
				case '©': sb.append("&copy;");break;   
				case '€': sb.append("&euro;"); break;
				case '\'': sb.append("&#146;"); break;
				
				default:  sb.append(c); break;
			}
		}
		return sb.toString();
	}
	
	public final String escapeExtendedHTML(String s)
	{
		if(s == null)
			return null;
			
		StringBuffer sb = new StringBuffer();
		int n = s.length();
		for (int i = 0; i < n; i++) 
		{
			char c = s.charAt(i);
			switch (c) 
			{
				case '<': sb.append("&lt;"); break;
				case '>': sb.append("&gt;"); break;
				case '&': sb.append("&amp;"); break;
				case '"': sb.append("&quot;"); break;
				/*
				case 'à': sb.append("&agrave;");break;
				case 'À': sb.append("&Agrave;");break;
				case 'â': sb.append("&acirc;");break;
				case 'Â': sb.append("&Acirc;");break;
				case 'ä': sb.append("&auml;");break;
				case 'Ä': sb.append("&Auml;");break;
				case 'å': sb.append("&aring;");break;
				case 'Å': sb.append("&Aring;");break;
				case 'æ': sb.append("&aelig;");break;
				case 'Æ': sb.append("&AElig;");break;
				case 'ç': sb.append("&ccedil;");break;
				case 'Ç': sb.append("&Ccedil;");break;
				case 'é': sb.append("&eacute;");break;
				case 'É': sb.append("&Eacute;");break;
				case 'è': sb.append("&egrave;");break;
				case 'È': sb.append("&Egrave;");break;
				case 'ê': sb.append("&ecirc;");break;
				case 'Ê': sb.append("&Ecirc;");break;
				case 'ë': sb.append("&euml;");break;
				case 'Ë': sb.append("&Euml;");break;
				case 'ï': sb.append("&iuml;");break;
				case 'Ï': sb.append("&Iuml;");break;
				case 'ô': sb.append("&ocirc;");break;
				case 'Ô': sb.append("&Ocirc;");break;
				case 'ö': sb.append("&ouml;");break;
				case 'Ö': sb.append("&Ouml;");break;
				case 'ø': sb.append("&oslash;");break;
				case 'Ø': sb.append("&Oslash;");break;
				case 'ß': sb.append("&szlig;");break;
				case 'ù': sb.append("&ugrave;");break;
				case 'Ù': sb.append("&Ugrave;");break;         
				case 'û': sb.append("&ucirc;");break;         
				case 'Û': sb.append("&Ucirc;");break;
				case 'ü': sb.append("&uuml;");break;
				case 'Ü': sb.append("&Uuml;");break;
				case '®': sb.append("&reg;");break;         
				case '©': sb.append("&copy;");break;   
				case '€': sb.append("&euro;"); break;
	         	*/
				case '\'': sb.append("&#146;"); break;

				default:  sb.append(c); break;
			}
		}
		return sb.toString();
	}
	
	/**
	 * This method converts all non-standard characters to html-equivalents.
	 */
	
	public final String escapeForJavascripts(String s)
	{
		if(s == null)
			return null;
			
		StringBuffer sb = new StringBuffer();
		int n = s.length();
		for (int i = 0; i < n; i++) 
		{
			char c = s.charAt(i);
			if(c == '\'') sb.append("\\'");
			else sb.append(c);
		}
		
		return sb.toString();
	}
	
	/**
	 * This method converts all non-standard characters to html-equivalents.
	 */
	
	public final String encode(String s) throws Exception
	{
		if(s == null)
			return null;
		
		return URLEncoder.encode(s, "UTF-8");
	}

	/**
	 * This method converts all non-standard characters to html-equivalents.
	 */
	
	public final String encodeURI(String s) throws Exception
	{
		if(s == null)
			return null;
		
		String encoding = CmsPropertyHandler.getProperty("URIEncoding");
		
		return URLEncoder.encode(s, encoding);
	}

}