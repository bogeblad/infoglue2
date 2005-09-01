package org.infoglue.cms.applications.workflowtool.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 */
public class ContentValues {
	/**
	 * 
	 */
	public static final String PUBLISH_DATE_TIME = "PublishDateTime";

	/**
	 * 
	 */
	public static final String EXPIRE_DATE_TIME  = "ExpireDateTime";

	/**
	 * 
	 */
	public static final String NAME  = "Name";
	
	/**
	 * 
	 */
	private Date publishDateTime;

	/**
	 * 
	 */
	private Date expireDateTime;

	/**
	 * 
	 */
	private String name;

	
	
	/**
	 * 
	 */
	public ContentValues() { super(); }

	/**
	 * 
	 */
	public String getName() { return name; }
	
	/**
	 *
	 */
	public Date getPublishDateTime() { return publishDateTime; }

	/**
	 *
	 */
	public Date getExpireDateTime() { return expireDateTime; }

	/**
	 * 
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 *
	 */
	public void setPublishDateTime(final String publishDateTime) {
		this.publishDateTime = getDate(publishDateTime);
	}
	
	/**
	 *
	 */
	public void setExpireDateTime(final String expireDateTime) {
		this.expireDateTime = getDate(expireDateTime);
	}

	/**
	 *
	 */
	private static Date getDate(String dateString) {
		try {
			return (dateString == null) ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
		} catch(Exception e) {
			return null;
		}
	}

}