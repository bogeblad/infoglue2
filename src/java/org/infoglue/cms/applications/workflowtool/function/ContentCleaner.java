package org.infoglue.cms.applications.workflowtool.function;

import java.util.Map;

import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class ContentCleaner extends ContentFunction {
	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		delete();
	}
	
	/**
	 * 
	 */
	private void delete() throws WorkflowException {
		try {
			if(getContentVO() != null)
				ContentController.getContentController().delete(getContentVO(), getDatabase());
		} catch(Exception e) {
			throw new WorkflowException(e);
		}
	}
}