package org.infoglue.cms.applications.workflowtool.function;

import java.util.Map;

import org.infoglue.cms.entities.content.ContentVO;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public abstract class ContentFunction extends InfoglueFunction {
	/**
	 * 
	 */
	private ContentVO contentVO;

	/**
	 * 
	 */
	public ContentFunction() { super(); }
	
	/**
	 * 
	 */
	protected ContentVO getContentVO() { return contentVO; }
	
	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		super.initialize(transientVars, args, ps);
		contentVO = (ContentVO) getParameter(transientVars, ContentProvider.TRANSIENT_VARS_VARIABLE, false);
	}
}
