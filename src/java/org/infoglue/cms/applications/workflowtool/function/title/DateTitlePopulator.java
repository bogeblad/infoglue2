package org.infoglue.cms.applications.workflowtool.function.title;

import java.util.Date;

import org.infoglue.cms.applications.common.VisualFormatter;

/**
 * 
 */
public class DateTitlePopulator extends Populator {
	/**
	 * 
	 */
	private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm";

	
	
	/**
	 * 
	 */
	public DateTitlePopulator() { super(); }
	
	/**
	 * 
	 */
	protected String getTitle() { return new VisualFormatter().formatDate(new Date(), DATETIME_PATTERN); }
}
