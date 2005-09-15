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
package org.infoglue.cms.applications.workflowtool.util;

import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.util.workflow.DatabaseSession;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public abstract class InfoglueWorkflowBase 
{
	/**
	 * The class logger.
	 */
	private final static Logger logger = Logger.getLogger(InfoglueWorkflowBase.class.getName());
	
	/**
	 * 
	 */
	public static final String WORKFLOW_PROPERTYSET_PREFIX = "workflow_";
	
	/**
	 * 
	 */
	public static final String FUNCTION_STATUS_PROPERTYSET_KEY = WORKFLOW_PROPERTYSET_PREFIX + "status";

	/**
	 * 
	 */
	public static final String ERROR_PROPERTYSET_PREFIX = "error_";

	/**
	 * The key used to lookup the <code>DatabaseSession</code> in the <code>parameters</code>.
	 */
	private static final String DB_PARAMETER = "db";
	
	/**
	 * The parameters (transient variables) of the current execution context.
	 */
	private Map parameters;
	
	/**
	 * The arguments passed to the function.
	 */
	private Map arguments;
	
	/**
	 * The propertyset associated with the current workflow.
	 */
	private InfogluePropertySet propertySet;

	/**
	 * The database associated with the current execution.
	 */
	private DatabaseSession workflowDatabase;
	
	
	
	/**
	 * Default constructor.
	 */
	public InfoglueWorkflowBase() 
	{ 
		super(); 
	}

	/**
	 * 
	 * Note! You must call <code>super.initialize()</code> first. 
	 */
	protected void initialize() throws WorkflowException
	{
		workflowDatabase = (DatabaseSession) getParameter(DB_PARAMETER);
	}
	
	/**
	 * Stores the execution context.
	 * 
	 * @param transientVars the transient variables of the current execution context.
	 * @param args the arguments of the function.
	 * @param ps the propertyset associated with the current workflow.
	 */
	protected void storeContext(final Map transientVars, final Map args, final PropertySet ps)
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
	 * Returns true if the specified argument exists; false otherwise.
	 *  
	 * @param name the name of the argument.
	 * @return true if the specified argument exists; false otherwise.
	 */
	protected final boolean argumentExists(final String name) 
	{
		return arguments.containsKey(name);
	}
	
	/**
	 * Returns the specified argument if it exists; otherwise an exception is thrown.
	 * Only use this method if the argument is absolutely required.
	 * 
	 * @param name the name of the argument.
	 * @return the specified argument.
	 * @throws WorkflowException if the specified argument doesn't exists.
	 */
	protected final String getArgument(final String name) throws WorkflowException 
	{
		if(!arguments.containsKey(name)) 
		{
			throwException("Required argument " + name + " is missing.");
		}
		return arguments.get(name).toString();
	}
	
	/**
	 * Returns the specified argument if it exists; otherwise the default value.
	 * 
	 * @param name the name of the argument.
	 * @param defaultValue the default value.
	 * @return the specified argument if it exists; otherwise the default value.
	 */
	protected final String getArgument(final String name, final String defaultValue) throws WorkflowException 
	{
		return arguments.containsKey(name) ? arguments.get(name).toString() : defaultValue;
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
	 * Returns the class logger.
	 * 
	 * @return the class logger.
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
}
