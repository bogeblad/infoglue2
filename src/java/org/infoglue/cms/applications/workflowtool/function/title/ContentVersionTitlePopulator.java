package org.infoglue.cms.applications.workflowtool.function.title;

import java.util.Map;

import org.infoglue.cms.applications.workflowtool.function.ContentPopulator;
import org.infoglue.cms.applications.workflowtool.util.ContentVersionValues;


import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

public class ContentVersionTitlePopulator extends Populator {
	/**
	 * 
	 */
    private static final String ARGUMENT_ATTRIBUTE_NAME = "attributeName";
	
	/**
	 * 
	 */
	private String title;
	
	
	
	/**
	 * 
	 */
	public ContentVersionTitlePopulator() { super(); }
	
	/**
	 * 
	 */
	protected String getTitle() { return title; } 

	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		super.initialize(transientVars, args, ps);
		final String attributeName = (String) args.get(ARGUMENT_ATTRIBUTE_NAME);
		final ContentVersionValues contentVersionValues = (ContentVersionValues) transientVars.get(ContentPopulator.TRANSIENT_VARS_CONTENT_VERSION_VARIABLE);
		if(attributeName != null && contentVersionValues != null && contentVersionValues.contains(attributeName))
			title = (String) contentVersionValues.get(attributeName);
	}
}
