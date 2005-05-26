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

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;

public final class BrowserBean implements Serializable
{
	private HttpServletRequest request = null;
	private String useragent           = null;
	private String languages           = null;
	private boolean netEnabled        = false;

	private boolean ie  = false;
	private boolean ns7 = false;
	private boolean ns6 = false;
	private boolean ns4 = false;
	
	private String os;

	public void setRequest(HttpServletRequest req)
	{
		this.request = req;
		this.useragent = request.getHeader("User-Agent");
		this.languages = request.getHeader("Accept-Language");
		System.out.println("useragent: " + useragent);
		if(this.languages != null)
			this.languages = this.languages.toLowerCase();

		if(useragent != null)
		{
			inituseragent();
			initOs();
		}
	}

	private void inituseragent()
	{
		String user = useragent.toLowerCase();
		if(user.indexOf("msie") != -1) 
		{
			this.ie = true;
		}
		else if(user.indexOf("netscape/7") != -1)
		{
			this.ns7 = true;
		}
		else if(user.indexOf("netscape6") != -1)
		{
			this.ns6 = true;
		}
		else if(user.indexOf("mozilla") != -1)
		{
			this.ns4 = true;
		}

		if(user.indexOf(".net clr") != -1)
			this.netEnabled = true;
	}

	private void initOs() 
	{
        if (this.useragent.indexOf("win") > -1)
        {
            if (this.useragent.indexOf("windows 95") > -1 || this.useragent.indexOf("win95") > -1)
            {
                this.os = "Windows 95";
            } 
            else if (this.useragent.indexOf("windows 98") > -1 || this.useragent.indexOf("win98") > -1)
            {
                this.os = "Windows 98";
            } 
            else if (this.useragent.indexOf("windows nt") > -1 || this.useragent.indexOf("winnt") > -1)
            {
                this.os = "Windows NT";
            } 
            else if (this.useragent.indexOf("win16") > -1 || this.useragent.indexOf("windows 3.") > -1)
            {
                this.os = "Windows 3.x";
            }
            else
                this.os = "Windows";

        } 
        else if (this.useragent.indexOf("Mac") > -1)
        {
            if (this.useragent.indexOf("Mac_PowerPC") > -1 || this.useragent.indexOf("Mac_PPC") > -1)
            {
                this.os = "Macintosh Power PC";
            } 
            else if (this.useragent.indexOf("Macintosh") > -1)
            {
                this.os = "Macintosh";
            } 
            else
            {
                this.os = "Mac";
            }
        }
    }	
	
	public String getUseragent()
	{
		return useragent;
	}

	public String getLanguageCode()
	{
		return this.languages;
	}

	public boolean isNetEnabled()
	{
		return netEnabled;
	}

	public boolean isIE()
	{
		return ie;
	}

	public boolean isNS7()
	{
		return ns7;
	}

 	public boolean isNS6()
	{
		return ns6;
	}

	public boolean isNS4()
	{
		return ns4;
	}
	
    public String getOs()
    {
        return os;
    }
}