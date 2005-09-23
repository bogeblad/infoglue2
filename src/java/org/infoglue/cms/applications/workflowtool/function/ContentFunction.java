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

import org.infoglue.cms.entities.content.ContentVO;

import com.opensymphony.workflow.WorkflowException;

/**
 * Base class for all functions operating on <code>ContentVO</code> objects.
 */
public abstract class ContentFunction extends InfoglueFunction 
{
	/**
	 * The key used by the <code>content</code> in the <code>parameters</code>.
	 */
	public static final String CONTENT_PARAMETER = "content";
	
	/**
	 * The content parameter.
	 */
	private ContentVO contentVO;

	/**
	 * Default constructor.
	 */
	protected ContentFunction() 
	{ 
		super(); 
	}
	
	/**
	 * Returns the content parameter.
	 * 
	 * @return the content parameter.
	 */
	protected ContentVO getContentVO() 
	{ 
		return contentVO; 
	}
	
	/**
	 * Method used for initializing the object; will be called before <code>execute</code> is called.
	 * Note! You must call <code>super.initialize()</code> first.
	 * 
	 * @throws WorkflowException if an error occurs during the initialization.
	 */
	protected void initialize() throws WorkflowException 
	{
		super.initialize();
		contentVO = (ContentVO) getParameter(CONTENT_PARAMETER, false);
	}
}
