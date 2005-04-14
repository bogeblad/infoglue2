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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.publishing.PublicationVO;
import org.infoglue.cms.entities.workflow.EventVO;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;


public class ChangeMultiContentStatePublishAction extends WebworkAbstractAction
{
	private Integer contentId;
	private List contentVersionId = new ArrayList();
	private Integer stateId;
	private Integer languageId;
	private String versionComment;
	private Integer repositoryId;
	private String attemptDirectPublishing = "false";

	
	//private ContentVO contentVO = new ContentVO();	
	//private ContentVersionVO contentVersionVO = new ContentVersionVO();	
		    
	/**
	 * This method gets called when calling this action. 
	 * If the stateId is 2 which equals that the user tries to prepublish the page. If so we
	 * ask the user for a comment as this is to be regarded as a new version. 
	 */
	   
    public String doExecute() throws Exception
    {   
		setContentVersionId( getRequest().getParameterValues("sel") );
		Iterator it = getContentVersionId().iterator();
		
		List events = new ArrayList();
		while(it.hasNext())
		{
			ContentVersion contentVersion = ContentStateController.changeState((Integer) it.next(), ContentVersionVO.PUBLISH_STATE, getVersionComment(), this.getInfoGluePrincipal(), null, events);
		}
		
		if(attemptDirectPublishing.equalsIgnoreCase("true"))
		{
		    PublicationVO publicationVO = new PublicationVO();
		    publicationVO.setName("Direct publication by " + this.getInfoGluePrincipal().getName());
		    publicationVO.setDescription(getVersionComment());
		    publicationVO.setRepositoryId(repositoryId);
		    publicationVO = PublicationController.createAndPublish(publicationVO, events, this.getInfoGluePrincipal());
		}
		
       	return "success";
    }
        
    public java.lang.Integer getContentId()
    {
        return this.contentId;
    }
        
    public void setContentId(java.lang.Integer contentId)
    {
	    this.contentId = contentId;
    }

    public java.lang.Integer getLanguageId()
    {
        return this.languageId;
    }
        
    public void setLanguageId(java.lang.Integer languageId)
    {
	    this.languageId = languageId;
    }
                	
	public void setStateId(Integer stateId)
	{
		this.stateId = stateId;
	}

	public void setVersionComment(String versionComment)
	{
		this.versionComment = versionComment;
	}
	
	public String getVersionComment()
	{
		return this.versionComment;
	}
	
	public Integer getStateId()
	{
		return this.stateId;
	}
            
	/**
	 * @return
	 */
	public List getContentVersionId() 
	{
		return contentVersionId;
	}

	/**
	 * @param list
	 */
	private void setContentVersionId(String[] list) 
	{
		contentVersionId = new ArrayList();
		for(int i=0; i < list.length; i++)
		{
			contentVersionId.add(new Integer(list[i]));
		}		
	}

    public Integer getRepositoryId()
    {
        return repositoryId;
    }

    public void setAttemptDirectPublishing(String attemptDirectPublishing)
    {
        this.attemptDirectPublishing = attemptDirectPublishing;
    }
    
    public void setRepositoryId(Integer repositoryId)
    {
        this.repositoryId = repositoryId;
    }
}
