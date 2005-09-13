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

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.applications.common.Session;
import org.infoglue.cms.applications.workflowtool.util.InfogluePropertySet;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.workflow.DatabaseSession;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public abstract class InfoglueFunction implements FunctionProvider 
{
	/**
	 * 
	 */
	private final static Logger logger = Logger.getLogger(InfoglueFunction.class.getName());
	
	/**
	 * The key used to lookup the <code>HttpServletRequest</code> in <code>parameters</code>.
	 */
	private static final String REQUEST_PARAMETER = "request";
	
	/**
	 * The key used to lookup the <code>principal</code> in <code>parameters</code>.
	 */
	private static final String PRINCIPAL_PARAMETER = "principal";
	
	/**
	 * The key used to lookup the <code>principal</code> in <code>parameters</code>.
	 */
	private static final String LOCALE_PARAMETER = "locale";
	
	/**
	 * 
	 */
	public static final String WORKFLOW_PROPERTYSET_PREFIX = "workflow_";
	
	/**
	 * 
	 */
	public static final String STATUS_PROPERTYSET_KEY = WORKFLOW_PROPERTYSET_PREFIX + "status";

	/**
	 * 
	 */
	private static final String DB_PARAMETER = "db";

	/**
	 * 
	 */
	private Map parameters;
	
	/**
	 * 
	 */
	private Map arguments;
	
	/**
	 * 
	 */
	private InfogluePropertySet propertySet;
	
	/**
	 * 
	 */
	private Locale locale;
	
	/**
	 * 
	 */
	private InfoGluePrincipal principal;
	
	/**
	 * 
	 */
	private DatabaseSession workflowDatabase;
	
	
	
	/**
	 * 
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
	 */
	protected void initialize() throws WorkflowException
	{
		initializeLocale();
		initializePrincipal();
		initializeDatabase();
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
	private void initializeDatabase() throws WorkflowException
	{
		workflowDatabase = (DatabaseSession) getParameter(DB_PARAMETER);
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
	 * 
	 */
	private void storeContext(final Map transientVars, final Map args, final PropertySet ps)
	{
		this.parameters  = transientVars;
		this.arguments   = Collections.unmodifiableMap(args);
		this.propertySet = new InfogluePropertySet(ps);
	}
	
	/**
	 * 
	 */
	protected void throwException(final String message) throws WorkflowException
	{
		throwException(new WorkflowException(message));
	}
	
	/**
	 * 
	 */
	protected void throwException(final Exception e) throws WorkflowException
	{
		logger.error(e.getMessage());
		workflowDatabase.setRollbackOnly();
		throw (e instanceof WorkflowException) ? (WorkflowException) e : new WorkflowException(e);
	}
	
	/**
	 *  
	 */
	protected final boolean argumentExists(final String key) 
	{
		return arguments.containsKey(key);
	}
	
	/**
	 * 
	 */
	protected final String getArgument(final String key) throws WorkflowException 
	{
		if(!arguments.containsKey(key)) 
		{
			throwException("Required argument " + key + " is missing.");
		}
		return arguments.get(key).toString();
	}
	
	/**
	 *  
	 */
	protected final String getArgument(final String key, final String defaultValue) throws WorkflowException 
	{
		return arguments.containsKey(key) ? arguments.get(key).toString() : defaultValue;
	}
	
	/**
	 *  
	 */
	protected final boolean parameterExists(final String key) throws WorkflowException 
	{
		return parameters.containsKey(key);
	}
	
	/**
	 *
	 */
	protected final Object getParameter(final String key) throws WorkflowException 
	{
		return getParameter(key, true);
	}
	
	/**
	 *  
	 */
	protected final Object getParameter(final String key, final Object defaultValue) throws WorkflowException 
	{
		return parameters.containsKey(key) ? parameters.get(key) : defaultValue;
	}
	
	/**
	 * 
	 */
	protected final Object getParameter(final String key, final boolean required) throws WorkflowException 
	{
		final Object parameter = parameters.get(key);
		if(required && parameter == null) 
		{
			final WorkflowException e = new WorkflowException("Required parameter " + key + " is missing.");
			logger.error(e.toString());
			throw e;
		}
		return parameter;
	}
	
	/**
	 * @todo : is this really needed?
	 */
	protected final String getRequestParameter(final String key) 
	{
		Object value = parameters.get(key);
		if(value != null && value.getClass().isArray()) 
		{
			final String[] values = (String[]) value;
			value = (values.length == 1) ? values[0] : null;
		}
		return (value == null) ? null : value.toString();
	}
	
	/**
	 *  
	 */
	protected final void setParameter(final String key, final Object value)
	{
		parameters.put(key, value);
	}
	
	/**
	 * 
	 */
	protected final Map getParameters()
	{
		return parameters;
	}
	
	/**
	 * 
	 */
	protected final boolean propertySetContains(final String key)
	{
		return propertySet.exists(key);
	}
	
	/**
	 * 
	 */
	protected final String getPropertySetDataString(final String key)
	{
		return propertySet.getDataString(key);
	}
	
	/**
	 * 
	 */
	protected final void setPropertySetDataString(final String key, final String value)
	{
		propertySet.setDataString(key, value);
	}
	
	/**
	 * 
	 */
	protected final String getPropertySetString(final String key)
	{
		return propertySet.getString(key);
	}
	
	/**
	 * 
	 */
	protected final void setPropertySetString(final String key, final String value)
	{
		propertySet.setString(key, value);
	}
	
	/**
	 * 
	 */
	protected final void removeFromPropertySet(final String key)
	{
		removeFromPropertySet(key, false);
	}
	
	/**
	 * 
	 */
	protected final void removeFromPropertySet(final String key, final boolean isPrefix)
	{
		propertySet.removeKeys(key, isPrefix);
	}

	/**
	 * 
	 */
	protected final InfogluePropertySet getPropertySet()
	{
		return propertySet;
	}
	
	/**
	 * 
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
	protected final Logger getLogger() 
	{ 
		return logger; 
	}
	
	/**
	 * 
	 */
	protected final Database getDatabase() throws WorkflowException 
	{
		return workflowDatabase.getDB();
	}

	/**
	 * 
	 */
	protected final void setStatus(final String status) 
	{
		getLogger().debug("setStatus(" + status + ")");
		getPropertySet().setString(STATUS_PROPERTYSET_KEY, status);
	}
}
