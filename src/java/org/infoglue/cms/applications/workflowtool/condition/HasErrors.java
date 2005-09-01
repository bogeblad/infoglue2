package org.infoglue.cms.applications.workflowtool.condition;

import java.util.Map;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.workflowtool.function.ErrorPopulator;


import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class HasErrors implements Condition {
	/**
	 * 
	 */
    private final static Logger logger = Logger.getLogger(HasErrors.class.getName());

	
	
	/**
	 * 
	 */
	public HasErrors() { super(); }

	/**
	 * 
	 */
	public boolean passesCondition(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		final boolean condition = !ps.getKeys(ErrorPopulator.PROPERTYSET_ERROR_PREFIX).isEmpty();
		logger.debug("HasErrors.passesCondition() : " + condition);
		return condition;
	}
}
