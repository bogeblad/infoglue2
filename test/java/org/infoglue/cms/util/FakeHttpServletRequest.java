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
 *
 * $Id: FakeHttpServletRequest.java,v 1.1 2004/11/29 15:29:10 jed Exp $
 */
package org.infoglue.cms.util;

import java.io.*;
import java.security.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A quick-and-dirty stub of HttpServletRequest.  We support attributes and parameters and implement the rest
 * of the methods as no-ops.
 * @author <a href=mailto:jedprentice@gmail.com>Jed Prentice</a>
 */
public class FakeHttpServletRequest implements HttpServletRequest
{
	private final Hashtable attributes = new Hashtable();
	private final Hashtable parameters = new Hashtable();
	private HttpSession session;

	public FakeHttpServletRequest()
	{
		this(new FakeHttpSession());
	}

	public FakeHttpServletRequest(HttpSession session)
	{
		setSession(session);
	}

	public Object getAttribute(String name)
	{
		return attributes.get(name);
	}

	public Enumeration getAttributeNames()
	{
		return attributes.keys();
	}

	public void setAttribute(String name, Object value)
	{
		attributes.put(name, value);
	}

	public void removeAttribute(String name)
	{
		attributes.remove(name);
	}

	public String getParameter(String name)
	{
		return (String)parameters.get(name);
	}

	/**
	 * Not part of HttpServletRequest, but required so tests can hook up the parameters
	 */
	public void setParameter(String name, String value)
	{
		parameters.put(name, value);
	}

	public Enumeration getParameterNames()
	{
		return parameters.keys();
	}

	public String[] getParameterValues(String name)
	{
		return (String[])parameters.get(name);
	}

	public Map getParameterMap()
	{
		return parameters;
	}

	public HttpSession getSession(boolean create)
	{
		if (create && session == null)
			session = new FakeHttpSession();

		return getSession();
	}

	public HttpSession getSession()
	{
		return session;
	}

	/**
	 * Not part of HttpServletRequest, but required so tests can hook up the session whenever it is convenient
	 */
	public void setSession(HttpSession session)
	{
		this.session = session;
	}

	public String getCharacterEncoding()   { return null; }
	public int getContentLength()          { return 0; }
	public String getContentType()         { return null; }
	public ServletInputStream getInputStream() throws IOException  { return null; }
	public String getProtocol()   { return null; }
	public String getScheme()     { return null; }
	public String getServerName() { return null; }
	public int getServerPort()    { return 0; }
	public BufferedReader getReader() throws IOException { return null; }
	public String getRemoteAddr() { return null; }
	public String getRemoteHost() { return null; }
	public Locale getLocale()        { return null; }
	public Enumeration getLocales()  { return null; }
	public boolean isSecure()        { return false; }
	public RequestDispatcher getRequestDispatcher(String path) { return null; }
	public String getRealPath(String path)   { return null; }

	public String getAuthType()      { return null; }
	public Cookie[] getCookies()     { return null; }
	public long getDateHeader(String name)   { return 0; }
	public String getHeader(String name)  { return null; }
	public Enumeration getHeaders(String name)  { return null; }
	public Enumeration getHeaderNames()       { return null; }
	public int getIntHeader(String name)        { return 0; }
	public String getMethod()                 { return null; }
	public String getPathInfo()               { return null; }
	public String getPathTranslated()         { return null; }
	public boolean isUserInRole(String role)  { return false; }
	public Principal getUserPrincipal()       { return null; }
	public String getContextPath()            { return null; }
	public String getQueryString()            { return null; }
	public String getRemoteUser()             { return null; }
	public String getRequestedSessionId()     { return null; }
	public String getRequestURI()             { return null; }
	public String getServletPath()            { return null; }
	public boolean isRequestedSessionIdValid() 	{ return false; }
	public boolean isRequestedSessionIdFromCookie() { return false; }
	public boolean isRequestedSessionIdFromURL()    { return false; }
	public boolean isRequestedSessionIdFromUrl()    { return false; }
	public StringBuffer getRequestURL()				{ return null; }
	public void setCharacterEncoding(String s) throws UnsupportedEncodingException {}
}
