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

package org.infoglue.cms.applications.contenttool.actions;

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.util.CmsLogger;

import org.infoglue.cms.entities.content.ContentVO;

/**
 * This action removes a content from the system.
 * 
 * @author Mattias Bogeblad
 */

public class DeleteContentAction extends WebworkAbstractAction
{
	private ContentVO contentVO;
	private Integer contentId;
	private Integer parentContentId;
	private Integer changeTypeId;
	private Integer repositoryId;
    
	
	public DeleteContentAction()
	{
		this(new ContentVO());
	}

	public DeleteContentAction(ContentVO contentVO) 
	{
		this.contentVO = contentVO;
	}
	
	protected String doExecute() throws Exception 
	{
		try
		{
			this.parentContentId = ContentController.getParentContent(this.contentVO.getContentId()).getContentId();
		}
		catch(Exception e)
		{
			CmsLogger.logInfo("The content must have been a root-content because we could not find a parent.");
		}
		
		ContentControllerProxy.getController().acDelete(this.getInfoGluePrincipal(), this.contentVO);
		//ContentController.delete(this.contentVO);
		
		return "success";
	}
	
	public void setContentId(Integer contentId)
	{
		this.contentVO.setContentId(contentId);
	}

	public void setParentContentId(Integer parentContentId)
	{
		this.parentContentId = parentContentId;
	}

	public void setChangeTypeId(Integer changeTypeId)
	{
		this.changeTypeId = changeTypeId;
	}

	public Integer getContentId()
	{
		return this.parentContentId;
	}
	
	public Integer getUnrefreshedContentId()
	{
		return this.parentContentId;
	}
	
	public Integer getChangeTypeId()
	{
		return this.changeTypeId;
	}
        
    public String getErrorKey()
	{
		return "ContentVersion.stateId";
	}
	
	public String getReturnAddress()
	{
		return "ViewContent.action?contentId=" + this.contentVO.getId() + "&repositoryId=" + this.contentVO.getRepositoryId();
	}

	
}
