package org.infoglue.cms.applications.workflowtool.function.defaultvalue;

import java.util.Date;

import org.infoglue.cms.applications.common.VisualFormatter;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class DatePopulator extends Populator {
	/**
	 * 
	 */
	private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm";

	
	
	/**
	 * 
	 */
	public DatePopulator() { super(); }

	/**
	 * 
	 */
	protected void populate(final PropertySet ps) throws WorkflowException {}

	/**
	 * 
	 */
	protected void populate(final PropertySet ps, final String name) throws WorkflowException {
		populate(ps, name, new Date());
	}

	/**
	 * 
	 */
	protected void populate(final PropertySet ps, final String name, final String value) throws WorkflowException {
		populate(ps, name, new VisualFormatter().parseDate(value, DATETIME_PATTERN));
	}

	/**
	 * 
	 */
	protected void populate(final PropertySet ps, final String name, final Date value) throws WorkflowException {
		doPopulate(ps, name, new VisualFormatter().formatDate(value, DATETIME_PATTERN));
	}
}
