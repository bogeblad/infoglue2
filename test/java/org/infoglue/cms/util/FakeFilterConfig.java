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
 * $Id: FakeFilterConfig.java,v 1.2 2006/03/06 16:54:41 mattias Exp $
 */
package org.infoglue.cms.util;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * Allows us to configure the InfoGlueAuthorizationFilter for testing outside of a servlet environment.  Parameter
 * keys and values are read from cms.properties.
 * @author <a href=mailto:jedprentice@gmail.com>Jed Prentice</a>
 */
public class FakeFilterConfig implements FilterConfig
{
	private final Hashtable initParameters = new Hashtable();

	public FakeFilterConfig()
	{
		initParameters.put("org.infoglue.cms.security.loginUrl", CmsPropertyHandler.getProperty("loginUrl"));
		initParameters.put("org.infoglue.cms.security.invalidLoginUrl",
											CmsPropertyHandler.getProperty("invalidLoginUrl"));
		initParameters.put("org.infoglue.cms.security.authenticatorClass",
											CmsPropertyHandler.getProperty("authenticatorClass"));
		initParameters.put("org.infoglue.cms.security.authorizerClass",
											CmsPropertyHandler.getProperty("authorizerClass"));
		initParameters.put("org.infoglue.cms.security.serverName", CmsPropertyHandler.getProperty("serverName"));
		initParameters.put("org.infoglue.cms.security.authConstraint",
											CmsPropertyHandler.getProperty("authConstraint"));
		initParameters.put("org.infoglue.cms.security.extraParametersFile",
											CmsPropertyHandler.getProperty("extraParametersFile"));
		initParameters.put("org.infoglue.cms.security.casValidateUrl",
											CmsPropertyHandler.getProperty("casValidateUrl"));
		initParameters.put("org.infoglue.cms.security.casServiceUrl",
											CmsPropertyHandler.getProperty("casServiceUrl"));
	}

	/**
	 * Returns the name of this filter
	 * @return "fakeServletFilter"
	 */
	public String getFilterName()
	{
		return "fakeServletFilter";
	}

	/**
	 * Returns the servlet context
	 * @return a reference to FakeServletContext
	 * @see org.infoglue.cms.util.FakeServletContext
	 */
	public ServletContext getServletContext()
	{
		return FakeServletContext.getContext();
	}

	/**
	 * Returns the init parameter with the given name
	 * @param name the name of the desired init parameter
	 * @return the value of the init parameter associated with name, or null if no init parametrer with name exists.
	 */
	public String getInitParameter(String name)
	{
		return (String)initParameters.get(name);
	}

	/**
	 * Returns an enumeration of all init parameters
	 * @return an enumeration of all init parameters
	 */
	public Enumeration getInitParameterNames()
	{
		return initParameters.keys();
	}
}
