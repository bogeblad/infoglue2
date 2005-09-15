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

import java.util.ArrayList;
import java.util.List;

import org.infoglue.cms.controllers.kernel.impl.simple.ContentStateController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.PublicationController;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.publishing.PublicationVO;

import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class ContentPublisher extends ContentFunction 
{
	/**
	 * 
	 */
	private static final String STATUS_OK = "status.publish.ok";
	
	/**
	 * 
	 */
	private static final String STATUS_NOK = "status.publish.nok";
	
	/**
	 * 
	 */
	private LanguageVO language;

	
	
	/**
	 * 
	 */
	protected void execute() throws WorkflowException 
	{
		if(getContentVO() != null)
		{
			publish();
		}
		else
		{
			setFunctionStatus(STATUS_NOK);
		}
	}
	
	/**
	 * 
	 */
	private void publish() throws WorkflowException 
	{
		try 
		{
			final ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(getContentVO().getId(), language.getId(), getDatabase());
			if(contentVersionVO.getStateId().equals(ContentVersionVO.WORKING_STATE)) 
			{
				final List events = new ArrayList();
				ContentStateController.changeState(contentVersionVO.getContentVersionId(), ContentVersionVO.PUBLISH_STATE, "Auto", getPrincipal(), getContentVO().getId(), getDatabase(), events);
				PublicationController.getController().createAndPublish(createPublicationVO(), events, getPrincipal(), getDatabase());
				setFunctionStatus(STATUS_OK);
			} 
			else 
			{
				setFunctionStatus(STATUS_NOK);
			}
		} 
		catch(Exception e) 
		{
			setFunctionStatus(STATUS_NOK);
			throwException(e);
		}
	}
	
	/**
	 * 
	 */
	private PublicationVO createPublicationVO() 
	{
	    final PublicationVO publicationVO = new PublicationVO();
	    publicationVO.setName("Workflow publication by " + getPrincipal().getName());
	    publicationVO.setDescription("Workflow publication by " + getPrincipal().getName());
	    publicationVO.setRepositoryId(getContentVO().getRepositoryId());
		return publicationVO;
	}

	/**
	 * 
	 */
	protected void initialize() throws WorkflowException 
	{
		super.initialize();
		language =  (LanguageVO) getParameter(LanguageProvider.LANGUAGE_PARAMETER);
	}
}