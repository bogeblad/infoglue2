package org.infoglue.cms.applications.workflowtool.function.defaultvalue;

import org.infoglue.cms.applications.workflowtool.function.ContentPopulator;
import org.infoglue.cms.applications.workflowtool.util.ContentValues;


import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class PublishDatePopulator extends DatePopulator {
	/**
	 * 
	 */
	public PublishDatePopulator() { super(); }

	/**
	 * 
	 */
	protected void populate(final PropertySet ps) throws WorkflowException {
		super.populate(ps, ContentPopulator.PROPERTYSET_CONTENT_PREFIX + ContentValues.PUBLISH_DATE_TIME);
	}

	/**
	 * 
	 */
	protected void populate(final PropertySet ps, final String name) throws WorkflowException {
		super.populate(ps, ContentPopulator.PROPERTYSET_CONTENT_PREFIX + ContentValues.PUBLISH_DATE_TIME);
	}

	/**
	 * 
	 */
	protected void populate(final PropertySet ps, final String name, final String value) throws WorkflowException {
		super.populate(ps, ContentPopulator.PROPERTYSET_CONTENT_PREFIX + ContentValues.PUBLISH_DATE_TIME);
	}
}
