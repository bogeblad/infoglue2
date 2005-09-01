package org.infoglue.cms.applications.workflowtool.function.defaultvalue;

import java.util.Map;

import org.infoglue.cms.applications.workflowtool.function.InfoglueFunction;
import org.infoglue.cms.applications.workflowtool.util.PropertysetHelper;


import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public abstract class Populator extends InfoglueFunction {
	/**
	 * 
	 */
	private static final String ARGUMENT_NAME_NAME = "name";

	/**
	 * 
	 */
	private static final String ARGUMENT_VALUE_NAME = "value";

	
	
	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		final String name  = (String) args.get(ARGUMENT_NAME_NAME);
		final String value = (String) args.get(ARGUMENT_VALUE_NAME);
		
		if(name == null)
			populate(ps);
		else if(value != null)
			populate(ps, name, value);
		else 
			populate(ps, name);
	}

	/**
	 * 
	 */
	protected void doPopulate(final PropertySet ps, final String name, final String value) throws WorkflowException {
		new PropertysetHelper(ps).setData(name, value);
	}

	/**
	 * 
	 */
	protected abstract void populate(final PropertySet ps) throws WorkflowException;

	/**
	 * 
	 */
	protected abstract void populate(final PropertySet ps, final String name) throws WorkflowException;
	
	/**
	 * 
	 */
	protected abstract void populate(final PropertySet ps, final String name, final String value) throws WorkflowException;
}
