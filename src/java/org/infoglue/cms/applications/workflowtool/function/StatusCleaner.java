package org.infoglue.cms.applications.workflowtool.function;

import java.util.Map;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

public class StatusCleaner extends InfoglueFunction {
	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		if(ps.exists(PROPERTYSET_STATUS))
			ps.remove(PROPERTYSET_STATUS);
	}
}
