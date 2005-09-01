package org.infoglue.cms.applications.workflowtool.function.defaultvalue;

import java.util.Calendar;

import org.infoglue.cms.applications.workflowtool.function.ContentPopulator;
import org.infoglue.cms.applications.workflowtool.util.ContentValues;


import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

public class ExpireDatePopulator extends DatePopulator {
	/**
	 * 
	 */
	public ExpireDatePopulator() { super();	}
	
	/**
	 * 
	 */
	protected void populate(final PropertySet ps) throws WorkflowException {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 10);
		super.populate(ps, ContentPopulator.PROPERTYSET_CONTENT_PREFIX + ContentValues.EXPIRE_DATE_TIME, calendar.getTime());
	}
	
	/**
	 * 
	 */
	protected void populate(final PropertySet ps, final String name) throws WorkflowException {
		populate(ps);
	}

	/**
	 * 
	 */
	protected void populate(final PropertySet ps, final String name, final String value) throws WorkflowException {
		populate(ps);
	}
}
