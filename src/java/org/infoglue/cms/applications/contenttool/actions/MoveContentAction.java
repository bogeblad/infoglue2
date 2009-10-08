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

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.applications.databeans.LinkBean;
import org.infoglue.cms.applications.structuretool.actions.MoveSiteNodeAction;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeControllerProxy;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

/**
 * This action represents the CreateContent Usecase.
 */

public class MoveContentAction extends InfoGlueAbstractAction
{
	private static final long serialVersionUID = 1L;
	
    private final static Logger logger = Logger.getLogger(MoveContentAction.class.getName());

    private Integer contentId;
    private Integer parentContentId;
    private Integer newParentContentId;
    private Integer changeTypeId;
    private Integer repositoryId;
	private String hideLeafs;
   	private String userSessionKey;
    private String originalAddress;
    private String returnAddress;

    private ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();	
   	private ContentVO contentVO = new ContentVO();
  
	public void setContentId(Integer contentId)
	{
		this.contentId = contentId;
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
      
    public void setHideLeafs(String hideLeafs)
    {
    	this.hideLeafs = hideLeafs;
    }

	public String getHideLeafs()
	{
		return this.hideLeafs;
	}    

	public String getUserSessionKey()
	{
		return userSessionKey;
	}

	public void setUserSessionKey(String userSessionKey)
	{
		this.userSessionKey = userSessionKey;
	}

    public String doInputV3() throws Exception
    {		
        userSessionKey = "" + System.currentTimeMillis();

		ContentVO contentVO = ContentControllerProxy.getContentController().getContentVOWithId(getContentId());
		System.out.println("getContentId():" + getContentId());
		System.out.println("contentVO:" + contentVO);
		System.out.println("repositoryId:" + contentVO.getRepositoryId());

        String createSiteNodeInlineOperationDoneHeader = getLocalizedString(getLocale(), "tool.structuretool.moveSiteNodeInlineOperationDoneHeader", contentVO.getName());
		String createSiteNodeInlineOperationBackToCurrentPageLinkText = getLocalizedString(getLocale(), "tool.structuretool.moveSiteNodeInlineOperationBackToCurrentPageLinkText");
		String createSiteNodeInlineOperationBackToCurrentPageTitleText = getLocalizedString(getLocale(), "tool.structuretool.moveSiteNodeInlineOperationBackToCurrentPageTitleText");

	    setActionMessage(userSessionKey, createSiteNodeInlineOperationDoneHeader);
	    addActionLink(userSessionKey, new LinkBean("currentPageUrl", createSiteNodeInlineOperationBackToCurrentPageLinkText, createSiteNodeInlineOperationBackToCurrentPageTitleText, createSiteNodeInlineOperationBackToCurrentPageTitleText, this.originalAddress, false, ""));

		return "inputV3";
    }

    public String doExecute() throws Exception
    {
        ceb.throwIfNotEmpty();
    	
		ContentControllerProxy.getController().acMoveContent(this.getInfoGluePrincipal(), this.contentVO, this.newParentContentId);
		
		this.returnAddress = "ViewContent.action?contentId=" + this.contentVO.getId() + "&repositoryId=" + this.repositoryId;
        
		return "success";
    }

    public String doV3() throws Exception
    {
        try
        {
            ceb.throwIfNotEmpty();
        	
    		ContentControllerProxy.getController().acMoveContent(this.getInfoGluePrincipal(), this.contentVO, this.newParentContentId);

            setActionExtraData(userSessionKey, "refreshToolbarAndMenu", "" + true);
            setActionExtraData(userSessionKey, "repositoryId", "" + this.repositoryId);
            setActionExtraData(userSessionKey, "contentId", "" + newParentContentId);
            setActionExtraData(userSessionKey, "unrefreshedContentId", "" + parentContentId);
            setActionExtraData(userSessionKey, "unrefreshedNodeId", "" + parentContentId);
            setActionExtraData(userSessionKey, "changeTypeId", "" + this.changeTypeId);
        }
        catch(ConstraintException ce)
        {
        	logger.warn("An error occurred so we should not complete the transaction:" + ce);

			ce.setResult(INPUT + "V3");
			throw ce;
        }
        catch(Exception e)
        {
            logger.error("An error occurred so we should not complete the transaction:" + e, e);
            throw new SystemException(e.getMessage());
        }
    	        
        if(this.returnAddress != null && !this.returnAddress.equals(""))
        {
	        String arguments 	= "userSessionKey=" + userSessionKey + "&isAutomaticRedirect=false";
	        String messageUrl 	= returnAddress + (returnAddress.indexOf("?") > -1 ? "&" : "?") + arguments;
	        
	        this.getResponse().sendRedirect(messageUrl);
	        return NONE;
        }
        else
        {
        	return "successV3";
        }
    }

    public String getErrorKey()
	{
		return "Content.parentContentId";
	}
	
	public String getReturnAddress()
	{
		return this.returnAddress;
	}    
	
    public String getOriginalAddress()
	{
		return originalAddress;
	}

	public void setOriginalAddress(String originalAddress)
	{
		this.originalAddress = originalAddress;
	}

	public void setReturnAddress(String returnAddress)
	{
		this.returnAddress = returnAddress;
	}

}
