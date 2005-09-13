/* ===============================================================================
*
* Part of the InfoGlue Content Management Platform (www.infoglue.org)
*
* ===============================================================================
*
*  Copyright (C)
*
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU General Public License version 2, as published by the
* Free Software Foundation. See the file LICENSE.html for more information.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
* Place, Suite 330 / Boston, MA 02111-1307 / USA.
*
* ===============================================================================
*/
package org.infoglue.cms.applications.workflowtool.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 */
public class ContentValues 
{
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
	public ContentValues() 
	{ 
		super(); 
	}

	/**
	 * 
	 */
	public String getName() 
	{ 
		return name; 
	}
	
	/**
	 *
	 */
	public Date getPublishDateTime() 
	{ 
		return publishDateTime; 
	}

	/**
	 *
	 */
	public Date getExpireDateTime() 
	{ 
		return expireDateTime; 
	}

	/**
	 * 
	 */
	public void setName(final String name) 
	{
		this.name = name;
	}
	
	/**
	 *
	 */
	public void setPublishDateTime(final String publishDateTime) 
	{
		this.publishDateTime = getDate(publishDateTime);
	}
	
	/**
	 *
	 */
	public void setExpireDateTime(final String expireDateTime) 
	{
		this.expireDateTime = getDate(expireDateTime);
	}

	/**
	 *
	 */
	private static Date getDate(final String dateString) 
	{
		try 
		{
			return (dateString == null) ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
		} 
		catch(Exception e) 
		{
			return null;
		}
	}

}