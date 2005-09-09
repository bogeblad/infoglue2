package org.infoglue.cms.applications.workflowtool.function;

import java.util.Map;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
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
	 * 
	 */
	public static final String PROPERTYSET_STATUS = "workflow_status";

	/**
	 * 
	 */
	public static final String TRANSIENT_VARS_DB_VARIABLE = "db";

	/**
	 * 
	 */
	private DatabaseSession workflowDatabase;
	
	
	
	/**
	 * 
	 */
	public InfoglueFunction() { super(); }

	/**
	 * 
	 */
	public final void execute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException 
	{
		try 
		{
			getLogger().debug(getClass().getName() + ".execute()--------- START");
			initializeWorkflowDatabase(transientVars);
			initialize(transientVars, args, ps);
			doExecute(transientVars, args, ps);
			getLogger().debug(getClass().getName() + ".execute()--------- STOP");
		} 
		catch(WorkflowException e) 
		{
			getLogger().error(e);
			setRollbackOnly();
			throw e;
		} 
		catch(Exception e) 
		{
			getLogger().error(e);
			setRollbackOnly();
			throw new WorkflowException(e);
		}
	}
	
	/**
	 * 
	 */
	private void initializeWorkflowDatabase(final Map transientVars) throws WorkflowException 
	{
		workflowDatabase = (DatabaseSession) getParameter(transientVars, TRANSIENT_VARS_DB_VARIABLE, true);
	}
	
	/**
	 *  
	 */
	protected final String getArgument(final Map args, final String key) throws WorkflowException 
	{
		if(!args.containsKey(key)) 
		{
			final WorkflowException e = new WorkflowException("Required argument " + key + " is missing.");
			logger.error(e.toString());
			throw e;
		}
		return (String) args.get(key);
	}
	
	/**
	 *  
	 */
	protected final Object getParameter(final Map transientVars, final String key) throws WorkflowException 
	{
		return getParameter(transientVars, key, true);
	}

	/**
	 * 
	 */
	protected final Object getParameter(final Map transientVars, final String key, final boolean required) throws WorkflowException 
	{
		final Object parameter = transientVars.get(key);
		if(required && parameter == null) {
			final WorkflowException e = new WorkflowException("Required parameter " + key + " is missing.");
			logger.error(e.toString());
			throw e;
		}
		return parameter;
	}
	
	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {}

	/**
	 * 
	 */
	protected abstract void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException;
	
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
	protected final void setStatus(final PropertySet ps, final String status) 
	{
		getLogger().debug("setStatus(" + status + ")");
		ps.setString(PROPERTYSET_STATUS, status);
	}
	
	/**
	 * 
	 */
	protected final Database getDatabase() throws WorkflowException 
	{
		return workflowDatabase != null ? workflowDatabase.getDB() : null;
	}

	/**
	 * 
	 */
	private void setRollbackOnly() throws WorkflowException 
	{
		if(workflowDatabase != null)
			workflowDatabase.setRollbackOnly();
	}
}
