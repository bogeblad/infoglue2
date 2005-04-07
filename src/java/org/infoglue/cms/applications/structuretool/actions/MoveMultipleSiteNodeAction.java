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

package org.infoglue.cms.applications.structuretool.actions;

import java.util.ArrayList;
import java.util.List;

import org.infoglue.cms.controllers.kernel.impl.simple.*;

import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

/**
 * This action represents the CreateSiteNode Usecase.
 */

public class MoveMultipleSiteNodeAction extends InfoGlueAbstractAction
{

   	//  Initial params
    private Integer originalSiteNodeId;
    private Integer repositoryId;
    private Integer siteNodeId;
    private Integer parentSiteNodeId;
    private List qualifyers = new ArrayList();
    private boolean errorsOccurred = false;
	protected List repositories = null;
    
    //Move params
    protected String qualifyerXML = null;
    private Integer newParentSiteNodeId;
    
    //Tree params
    private Integer changeTypeId;
    private Integer topContentId;

    private ConstraintExceptionBuffer ceb;
   	private SiteNodeVO siteNodeVO;
   	
  
  	public MoveMultipleSiteNodeAction()
	{
		this(new SiteNodeVO());
	}
	
	public MoveMultipleSiteNodeAction(SiteNodeVO siteNodeVO)
	{
		this.siteNodeVO = siteNodeVO;
		this.ceb = new ConstraintExceptionBuffer();			
	}	

	public void setSiteNodeId(Integer siteNodeId)
	{
		this.siteNodeVO.setSiteNodeId(siteNodeId);
	}

	public Integer getSiteNodeId()
	{
		return siteNodeVO.getSiteNodeId();
	}
      
    public String doExecute() throws Exception
    {
        ceb.throwIfNotEmpty();
    	
		SiteNodeControllerProxy.getSiteNodeControllerProxy().acMoveSiteNode(this.getInfoGluePrincipal(), this.siteNodeVO, this.newParentSiteNodeId);
	    
        return "success";
    }
    
    public Integer getChangeTypeId()
    {
        return changeTypeId;
    }
    
    public void setChangeTypeId(Integer changeTypeId)
    {
        this.changeTypeId = changeTypeId;
    }
    
    public Integer getNewParentSiteNodeId()
    {
        return newParentSiteNodeId;
    }
    
    public void setNewParentSiteNodeId(Integer newParentSiteNodeId)
    {
        this.newParentSiteNodeId = newParentSiteNodeId;
    }
    
    public Integer getOriginalSiteNodeId()
    {
        return originalSiteNodeId;
    }
    
    public void setOriginalSiteNodeId(Integer originalSiteNodeId)
    {
        this.originalSiteNodeId = originalSiteNodeId;
    }
    
    public Integer getParentSiteNodeId()
    {
        return parentSiteNodeId;
    }
    
    public void setParentSiteNodeId(Integer parentSiteNodeId)
    {
        this.parentSiteNodeId = parentSiteNodeId;
    }
    
    public String getQualifyerXML()
    {
        return qualifyerXML;
    }
    
    public void setQualifyerXML(String qualifyerXML)
    {
        this.qualifyerXML = qualifyerXML;
    }
    
    public Integer getRepositoryId()
    {
        return repositoryId;
    }
    
    public void setRepositoryId(Integer repositoryId)
    {
        this.repositoryId = repositoryId;
    }
    
    public Integer getTopContentId()
    {
        return topContentId;
    }
    
    public void setTopContentId(Integer topContentId)
    {
        this.topContentId = topContentId;
    }
    
    public boolean isErrorsOccurred()
    {
        return errorsOccurred;
    }
    
    public List getQualifyers()
    {
        return qualifyers;
    }
    
    public List getRepositories()
    {
        return repositories;
    }
    
    public SiteNodeVO getSiteNodeVO()
    {
        return siteNodeVO;
    }
}
