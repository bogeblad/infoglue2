package org.infoglue.cms.applications.workflowtool.function.title;

import java.util.Map;

import org.infoglue.cms.applications.workflowtool.function.InfoglueFunction;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public abstract class Populator extends InfoglueFunction {
	/**
	 * 
	 */
	private static final String PROPERTYSET_TITLE_VARIABLE = "workflow.title";
	
	/**
	 * 
	 */
	public Populator() { super(); }

	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		populate(ps, getTitle());
	}
	
	/**
	 * 
	 */
	private void populate(final PropertySet ps, final String title) {
		if(title != null && title.trim().length() > 0)
			ps.setString(PROPERTYSET_TITLE_VARIABLE, title.trim());
	}
	
	/**
	 * 
	 */
	protected abstract String getTitle();
	
	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		super.initialize(transientVars, args, ps);
	}
}
