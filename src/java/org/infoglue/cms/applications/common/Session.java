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

import org.infoglue.cms.entities.management.SystemUser;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.security.InfoGlueAuthenticationFilter;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import webwork.action.factory.SessionMap;
import webwork.action.ActionContext;


/**
 * Session wrapper to ease getting things out of the Session. Abstract it
 * so that I can work well with a Map (for testing, or ActionContext.getSession())
 * or an HttpSession, but not rely on an HttpSession.
 *
 * @author <a href="mailto:meat_for_the_butcher@yahoo.com">Patrik Nyborg</a>
 * @author Frank Febbraro (frank@phase2technology.com)
 */
public class Session
{
	// Session attribute names
	public static final String LOCALE = "locale";
	private static final String USER   = "user";
	private static final String IG_PRINCIPAL = InfoGlueAuthenticationFilter.INFOGLUE_FILTER_USER;
	public static final String TOOL_ID = "toolId";


	private Map sessionDelegate;

	public Session()
	{
		this(ActionContext.getSession());
	}

	public Session(Map session)
	{
		this.sessionDelegate = session;
	}

	public Session(HttpSession httpSession)
	{
		this.sessionDelegate = new SessionMap(httpSession);
	}

	/**
	* Returns the locale used for the session.
	*/
	public final Locale getLocale()
	{
		//If empty set it to english
		if(sessionDelegate.get(LOCALE) == null)
		{
	        setLocale(java.util.Locale.ENGLISH);
		}

		return (Locale) sessionDelegate.get(LOCALE);
	}

	/**
	* Returns the locale used for the session.
	*/
	public final Integer getToolId()
	{
		//If empty set it to english
		if(sessionDelegate.get(TOOL_ID) == null)
		{
	        setToolId(new Integer(0));
		}

		return (Integer) sessionDelegate.get(TOOL_ID);
	}

	/**
	 * Sets the locale used for the session.
	 *
	 * @param locale the locale to use for the session.
	 */
	public final void setLocale(Locale locale)
	{
		sessionDelegate.put(LOCALE, locale);
	}

	/**
	 * Sets the locale used for the session.
	 *
	 * @param locale the locale to use for the session.
	 */
	public final void setToolId(Integer toolId)
	{
		sessionDelegate.put(TOOL_ID, toolId);
	}

	/**
	 * Returns the user of the session.
	 *
	 * @return the user of the session.
	 */
	public final SystemUser getUser()
	{
		return (SystemUser) sessionDelegate.get(USER);
	}

	/**
	 * Sets the user of the session.
	 *
	 * @param systemUser the user of the session.
	 */
	public final void setSystemUser(SystemUser systemUser)
	{
		// <todo>throw error if this happens more than once</todo>
		sessionDelegate.put(USER, systemUser);
	}

	/**
	 * Returns the InfoGlue principal associated with the current user and session.
	 * TODO: Update InfoGlueAuthenticationFilter to use this Session Object
	 * @return the InfoGlue principal associated with the current user and session
	 */
	public InfoGluePrincipal getInfoGluePrincipal()
	{
		return (InfoGluePrincipal)sessionDelegate.get(IG_PRINCIPAL);
	}

	/**
	 * Sets the InfoGlue principal associated with the current user and session.
	 * TODO: Update InfoGlueAuthenticationFilter to use this Session Object
	 */
	public void setInfoGluePrincipal(InfoGluePrincipal p)
	{
		sessionDelegate.put(IG_PRINCIPAL, p);
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer("<Session>\n");
		sb.append("  locale=[" + getLocale() + "]\n");
		sb.append("    user=[" + getUser() + "]\n");
		return sb.toString();
	}
	
}