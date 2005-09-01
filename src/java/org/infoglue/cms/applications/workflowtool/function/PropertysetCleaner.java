package org.infoglue.cms.applications.workflowtool.function;

import java.util.Map;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class PropertysetCleaner extends InfoglueFunction {
	/**
	 * 
	 */
	public PropertysetCleaner() { super(); }

	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		ps.remove();
	}
}
