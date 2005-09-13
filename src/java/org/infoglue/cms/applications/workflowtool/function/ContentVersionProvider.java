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
import org.infoglue.cms.entities.management.LanguageVO;

import com.opensymphony.workflow.WorkflowException;

/**
 * 
 *
 */
public class ContentVersionProvider extends ContentFunction {
	/**
	 * 
	 */
	private LanguageVO languageVO;
	

	
	/**
	 * 
	 */
	public ContentVersionProvider() 
	{
		super();
	}

	/**
	 * 
	 */
	protected void execute() throws WorkflowException 
	{
		setParameter(ContentVersionFunction.CONTENT_VERSION_PARAMETER, getContentVersionVO());
	}
	
	/**
	 * 
	 */
	private ContentVersionVO getContentVersionVO() throws WorkflowException
	{
		try 
		{
			final ContentVersionController controller = ContentVersionController.getContentVersionController();
			return controller.getLatestActiveContentVersionVO(getContentVO().getId(), languageVO.getId(), getDatabase());
		}
		catch(Exception e)
		{
			throwException(e);
		}
		return null; // dummy
	}
	
	/**
	 * 
	 */
	protected void initialize() throws WorkflowException
	{
		super.initialize();
		languageVO = (LanguageVO) getParameter(LanguageProvider.LANGUAGE_PARAMETER);
	}
}
