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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.cookie.CookieSpecBase;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.infoglue.cms.util.CmsLogger;

import javax.servlet.http.HttpServletRequest;


/**
 * This class sends an entire request to another url. It includes cookies and header info as well.
 *
 * @author Mattias Bogeblad
 */

public class HttpRequestForwarder extends EntityEnclosingMethod
{
    HttpServletRequest originalRequest;
    String destination;

    public HttpRequestForwarder(HttpServletRequest request, String destination) throws IOException
    {
        super(destination.toString());
        CmsLogger.logInfo("destination:" + destination.toString());
        this.originalRequest = request;
        this.destination = destination;
        this.setFollowRedirects(false);
        this.setPath(destination/*request.getRequestURI()*/);
        cloneHeaders();
        cloneCookies();
        cloneCookies();
        cloneParameters();
        //this.setRequestBody(originalRequest.getInputStream());
        setRequestEntity(new InputStreamRequestEntity(originalRequest.getInputStream()));
    }

    public String getName()
    {
        return originalRequest.getMethod();
    }


    private void cloneHeaders()
    {
        Enumeration e = originalRequest.getHeaderNames();
        while (e.hasMoreElements())
        {
            String header = (String) e.nextElement();
            String headerValue = originalRequest.getHeader(header);
            this.addRequestHeader(header, headerValue);
        }
    }

    private void cloneParameters()
    {
        Enumeration e = originalRequest.getParameterNames();
        while (e.hasMoreElements())
        {
            String name = (String) e.nextElement();
            String[] values = originalRequest.getParameterValues(name);
            System.out.println(name + "=" + values);
            //this.set .addRequestHeader(name, values);

        }
    }


    private void cloneCookies()
    {
        ArrayList newCookiesList = new ArrayList();
        javax.servlet.http.Cookie[] cookies = originalRequest.getCookies();
        if (cookies != null)
        {
            for (int i = 0; i < cookies.length; i++)
            {
                String domain = cookies[i].getDomain();
                String name = cookies[i].getName();
                String path = cookies[i].getPath();
                String value = cookies[i].getValue();
                System.out.println("domain:" + domain);
                System.out.println("name:" + name);
                System.out.println("path:" + path);
                System.out.println("value:" + value);
                Cookie cookie = new Cookie();
                cookie.setDomain(domain);
                cookie.setName(name);
                cookie.setPath(path);
                cookie.setValue(value);
                newCookiesList.add(cookie);
            }

            CookieSpecBase cookieFormatter = new CookieSpecBase();
            Header cookieHeader = cookieFormatter.formatCookieHeader((Cookie[])newCookiesList.toArray(new Cookie[0]));
            this.addRequestHeader(cookieHeader);
        }

    }

}
