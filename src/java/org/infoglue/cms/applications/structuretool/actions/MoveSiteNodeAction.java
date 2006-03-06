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

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeControllerProxy;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

/**
 * This action represents the CreateSiteNode Usecase.
 */

public class MoveSiteNodeAction extends InfoGlueAbstractAction
{

    private Integer siteNodeId;
    private Integer parentSiteNodeId;
    private Integer newParentSiteNodeId;
    private Integer repositoryId;
    private Integer changeTypeId;
    private ConstraintExceptionBuffer ceb;
   	private SiteNodeVO siteNodeVO;
  
  
  	public MoveSiteNodeAction()
	{
		this(new SiteNodeVO());
	}
	
	public MoveSiteNodeAction(SiteNodeVO siteNodeVO)
	{
		this.siteNodeVO = siteNodeVO;
		this.ceb = new ConstraintExceptionBuffer();			
	}	

	public void setSiteNodeId(Integer siteNodeId)
	{
		this.siteNodeVO.setSiteNodeId(siteNodeId);
	}

	public void setNewParentSiteNodeId(Integer newParentSiteNodeId)
	{
		this.newParentSiteNodeId = newParentSiteNodeId;
	}

	public void setParentSiteNodeId(Integer parentSiteNodeId)
	{
		this.parentSiteNodeId = parentSiteNodeId;
	}

	public void setChangeTypeId(Integer changeTypeId)
	{
		this.changeTypeId = changeTypeId;
	}

	public Integer getParentSiteNodeId()
	{
		return this.parentSiteNodeId;
	}
	
	public Integer getRepositoryId()
	{
		return this.repositoryId;
	}

	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}
	    
	public Integer getSiteNodeId()
	{
		return siteNodeVO.getSiteNodeId();
	}

	public Integer getNewParentSiteNodeId()
	{
		return this.newParentSiteNodeId;
	}
    
	public Integer getUnrefreshedSiteNodeId()
	{
		return this.newParentSiteNodeId;
	}

	public Integer getChangeTypeId()
	{
		return this.changeTypeId;
	}
      
    public String doExecute() throws Exception
    {
        ceb.throwIfNotEmpty();
    	
    	//SiteNodeController.moveSiteNode(this.siteNodeVO, this.newParentSiteNodeId);
		SiteNodeControllerProxy.getSiteNodeControllerProxy().acMoveSiteNode(this.getInfoGluePrincipal(), this.siteNodeVO, this.newParentSiteNodeId);
	    
        return "success";
    }
    
    public String getErrorKey()
	{
		return "SiteNode.parentSiteNodeId";
	}
	
	public String getReturnAddress()
	{
		return "ViewSiteNode.action?siteNodeId=" + this.siteNodeVO.getId() + "&repositoryId=" + this.repositoryId;
	}   

        
}
