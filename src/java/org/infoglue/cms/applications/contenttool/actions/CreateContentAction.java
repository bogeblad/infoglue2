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

import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.exception.AccessConstraintException;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.applications.common.VisualFormatter;

import org.infoglue.cms.util.AccessConstraintExceptionBuffer;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.util.CmsLogger;

import java.util.List;

/**
 * This action represents the CreateContent Usecase.
 */

public class CreateContentAction extends WebworkAbstractAction
{

	private Integer parentContentId;
    private Integer contentTypeDefinitionId;
    private Integer repositoryId;
   	private ConstraintExceptionBuffer ceb;
   	private ContentVO contentVO;
   	private ContentVO newContentVO;
  
  
  	public CreateContentAction()
	{
		this(new ContentVO());
	}
	
	public CreateContentAction(ContentVO contentVO)
	{
		this.contentVO = contentVO;
		this.ceb = new ConstraintExceptionBuffer();			
	}	

	public void setParentContentId(Integer parentContentId)
	{
		this.parentContentId = parentContentId;
	}

	public Integer getParentContentId()
	{
		return this.parentContentId;
	}

	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	public Integer getRepositoryId()
	{
		return this.repositoryId;
	}

	public void setContentTypeDefinitionId(Integer contentTypeDefinitionId)
	{
		this.contentTypeDefinitionId = contentTypeDefinitionId;
	}

	public Integer getContentTypeDefinitionId()
	{
		return this.contentTypeDefinitionId;
	}	
	
    public java.lang.String getName()
    {
        return this.contentVO.getName();
    }

    public String getPublishDateTime()
    {    		
        return new VisualFormatter().formatDate(this.contentVO.getPublishDateTime(), "yyyy-MM-dd HH:mm");
    }
        
    public String getExpireDateTime()
    {
        return new VisualFormatter().formatDate(this.contentVO.getExpireDateTime(), "yyyy-MM-dd HH:mm");
    }

   	public long getPublishDateTimeAsLong()
    {    		
        return this.contentVO.getPublishDateTime().getTime();
    }
        
    public long getExpireDateTimeAsLong()
    {
        return this.contentVO.getExpireDateTime().getTime();
    }
    
	public Boolean getIsBranch()
	{
 		return this.contentVO.getIsBranch();
	}    
            
    public void setName(java.lang.String name)
    {
    	this.contentVO.setName(name);
    }
    	
    public void setPublishDateTime(String publishDateTime)
    {
       	CmsLogger.logInfo("publishDateTime:" + publishDateTime);
   		this.contentVO.setPublishDateTime(new VisualFormatter().parseDate(publishDateTime, "yyyy-MM-dd HH:mm"));
    }

    public void setExpireDateTime(String expireDateTime)
    {
       	CmsLogger.logInfo("expireDateTime:" + expireDateTime);
       	this.contentVO.setExpireDateTime(new VisualFormatter().parseDate(expireDateTime, "yyyy-MM-dd HH:mm"));
	}
 
    public void setIsBranch(Boolean isBranch)
    {
       	this.contentVO.setIsBranch(isBranch);
    }
     
	public Integer getContentId()
	{
		return newContentVO.getContentId();
	}

	/**
	 * This method fetches the list of ContentTypeDefinitions
	 */
	
	public List getContentTypeDefinitions() throws Exception
	{
		return ContentTypeDefinitionController.getController().getContentTypeDefinitionVOList();
	}      
    
      
    public String doExecute() throws Exception
    {
		this.contentVO.setCreatorName(this.getInfoGluePrincipal().getName());

    	ceb = this.contentVO.validate();
    	ceb.throwIfNotEmpty();
    			
    	newContentVO = ContentControllerProxy.getController().acCreate(this.getInfoGluePrincipal(), parentContentId, contentTypeDefinitionId, repositoryId, contentVO);
		//newContentVO = ContentController.create(parentContentId, contentTypeDefinitionId, repositoryId, contentVO);
    	
    	return "success";
    }
    
	public String doBindingView() throws Exception
	{
		doExecute();
		return "bindingView";
	}
	
	public String doTreeView() throws Exception
	{
		doExecute();
		return "treeView";
	}

    public String doInput() throws Exception
    {
		AccessConstraintExceptionBuffer ceb = new AccessConstraintExceptionBuffer();
		
		Integer protectedContentId = ContentControllerProxy.getController().getProtectedContentId(parentContentId);
		if(protectedContentId != null && !AccessRightController.getController().getIsPrincipalAuthorized(this.getInfoGluePrincipal(), "Content.Create", protectedContentId.toString()))
			ceb.add(new AccessConstraintException("Content.contentId", "1002"));
		
		ceb.throwIfNotEmpty();
		
		return "input";
    }
        
}
