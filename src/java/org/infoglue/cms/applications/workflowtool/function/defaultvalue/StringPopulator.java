package org.infoglue.cms.applications.workflowtool.function.defaultvalue;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

public class StringPopulator extends Populator {
	/**
	 * 
	 */
	public StringPopulator() { super(); }
	
	/**
	 * 
	 */
	protected void populate(final PropertySet ps) throws WorkflowException {}

	/**
	 * 
	 */
	protected void populate(final PropertySet ps, final String name) throws WorkflowException {
		populate(ps, name, "");
	}

	/**
	 * 
	 */
	protected void populate(final PropertySet ps, final String name, final String value) throws WorkflowException {
		doPopulate(ps, name, value);
	}
}
