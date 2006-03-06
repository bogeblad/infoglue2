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

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

/**
 * This action represents the CreateContent Usecase.
 */

public class MoveContentAction extends InfoGlueAbstractAction
{
	private static final long serialVersionUID = 1L;
	
    private Integer contentId;
    private Integer parentContentId;
    private Integer newParentContentId;
    private Integer changeTypeId;
    private Integer repositoryId;
    private ConstraintExceptionBuffer ceb;
   	private ContentVO contentVO;
  
  
  	public MoveContentAction()
	{
		this(new ContentVO());
	}
	
	public MoveContentAction(ContentVO contentVO)
	{
		this.contentVO = contentVO;
		this.ceb = new ConstraintExceptionBuffer();			
	}	

	public void setContentId(Integer contentId)
	{
		this.contentVO.setContentId(contentId);
	}

	public void setNewParentContentId(Integer newParentContentId)
	{
		this.newParentContentId = newParentContentId;
	}

	public void setParentContentId(Integer parentContentId)
	{
		this.parentContentId = parentContentId;
	}

	public void setChangeTypeId(Integer changeTypeId)
	{
		this.changeTypeId = changeTypeId;
	}

	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}
	
	public Integer getRepositoryId()
	{
		return this.repositoryId;
	}

	public Integer getParentContentId()
	{
		return this.parentContentId;
	}
	    
	public Integer getContentId()
	{
		return contentVO.getContentId();
	}

	public Integer getNewParentContentId()
	{
		return this.newParentContentId;
	}
    
	public Integer getUnrefreshedContentId()
	{
		return this.newParentContentId;
	}

	public Integer getChangeTypeId()
	{
		return this.changeTypeId;
	}
      
    public String doExecute() throws Exception
    {
        ceb.throwIfNotEmpty();
    	
    	//ContentController.moveContent(this.contentVO, this.newParentContentId);
		ContentControllerProxy.getController().acMoveContent(this.getInfoGluePrincipal(), this.contentVO, this.newParentContentId);
		
        return "success";
    }

    
    public String getErrorKey()
	{
		return "Content.parentContentId";
	}
	
	public String getReturnAddress()
	{
		return "ViewContent.action?contentId=" + this.contentVO.getId() + "&repositoryId=" + this.repositoryId;
	}    
}
