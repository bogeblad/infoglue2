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
package org.infoglue.cms.applications.workflowtool.function;

import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.entities.content.ContentVersionVO;

import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public abstract class ContentVersionFunction extends InfoglueFunction 
{
	/**
	 * The name of the content version parameter. 
	 */
	public static final String CONTENT_VERSION_PARAMETER = "contentVersion";
	
	/**
	 * The content version parameter.
	 */
	private ContentVersionVO contentVersionVO;

	/**
	 * Indicates if the <code>CONTENT_VERSION_PARAMETER</code> is required.
	 */
	private final boolean required;
	
	
	
	/**
	 *
	 */
	protected ContentVersionFunction()
	{
		this(true);
	}
	
	/**
	 * 
	 */
	protected ContentVersionFunction(final boolean required) 
	{ 
		super();
		this.required = required;
	}
	
	/**
	 * Returns the content version parameter.
	 * 
	 * @return the content version parameter.
	 */
	protected ContentVersionVO getContentVersionVO() 
	{ 
		return contentVersionVO; 
	}
	
	/**
	 * 
	 */
	protected String getAttribute(final String name, final boolean escapeHTML) throws WorkflowException
	{
		if(contentVersionVO == null)
		{
			throwException("No content version.");
		}
		
		String value = "";
		try 
		{
			value = ContentVersionController.getContentVersionController().getAttributeValue(contentVersionVO, name, escapeHTML);
		}
		catch(Exception e)
		{
			throwException(e);
		}
		return value;
	}
	
	/**
	 * 
	 */
	protected void initialize() throws WorkflowException 
	{
		super.initialize();
		contentVersionVO = (ContentVersionVO) getParameter(CONTENT_VERSION_PARAMETER, required);
	}
}
