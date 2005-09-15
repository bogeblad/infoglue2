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
package org.infoglue.cms.applications.workflowtool.function;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.infoglue.cms.applications.common.Session;
import org.infoglue.cms.applications.workflowtool.util.InfoglueWorkflowBase;
import org.infoglue.cms.security.InfoGluePrincipal;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public abstract class InfoglueFunction extends InfoglueWorkflowBase implements FunctionProvider 
{
	/**
	 * The key used to lookup the <code>HttpServletRequest</code> in the <code>parameters</code>.
	 */
	private static final String REQUEST_PARAMETER = "request";
	
	/**
	 * The key used to lookup the <code>principal</code> in the <code>parameters</code>.
	 */
	private static final String PRINCIPAL_PARAMETER = "principal";
	
	/**
	 * The key used to lookup the <code>principal</code> in the <code>parameters</code>.
	 */
	private static final String LOCALE_PARAMETER = "locale";
	
	/**
	 * The locale associated with the current session.
	 */
	private Locale locale;
	
	/**
	 * 
	 */
	private InfoGluePrincipal principal;
	
	/**
	 * Default constructor.
	 */
	public InfoglueFunction() 
	{ 
		super(); 
	}

	/**
	 * 
	 */
	public final void execute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException 
	{
		try 
		{
			storeContext(transientVars, args, ps);
			getLogger().debug(getClass().getName() + ".execute()--------- START");
			initialize();
			execute();
			getLogger().debug(getClass().getName() + ".execute()--------- STOP");
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
	
	/**
	 * 
	 * Note! You must call <code>super.initialize()</code> first. 
	 */
	protected void initialize() throws WorkflowException
	{
		super.initialize();
		initializeLocale();
		initializePrincipal();
	}
	
	/**
	 * 
	 */
	private void initializeLocale() throws WorkflowException
	{
		if(parameterExists(LOCALE_PARAMETER))
		{
			locale = (Locale) getParameter(LOCALE_PARAMETER);
		}
		else
		{
			locale = getSession().getLocale();
		}
	}
	
	/**
	 * 
	 */
	private void initializePrincipal() throws WorkflowException
	{
		if(parameterExists(PRINCIPAL_PARAMETER))
		{
			principal = (InfoGluePrincipal) getParameter(PRINCIPAL_PARAMETER);
		}
		else
		{
			principal = getSession().getInfoGluePrincipal();
		}
	}
	
	/**
	 * 
	 */
	private Session getSession() throws WorkflowException
	{
		return new Session(((HttpServletRequest) getParameter(REQUEST_PARAMETER)).getSession());
	}
	
	/**
	 *
	 */
	protected abstract void execute() throws WorkflowException;
	
	/**
	 * @todo : is this really needed?
	 */
	protected final String getRequestParameter(final String key) 
	{
		Object value = getParameters().get(key);
		if(value != null && value.getClass().isArray()) 
		{
			final String[] values = (String[]) value;
			value = (values.length == 1) ? values[0] : null;
		}
		return (value == null) ? null : value.toString();
	}
	

	/**
	 * Returns the locale associated with the current session.
	 * 
	 * @return the locale associated with the current session.
	 */
	protected final Locale getLocale() throws WorkflowException
	{
		return locale;
	}
	
	/**
	 * 
	 */
	protected final InfoGluePrincipal getPrincipal()
	{
		return principal;
	}
	
	/**
	 * 
	 */
	protected final void setFunctionStatus(final String status) 
	{
		getLogger().debug("setFunctionStatus(" + status + ")");
		getPropertySet().setString(FUNCTION_STATUS_PROPERTYSET_KEY, status);
	}
}
