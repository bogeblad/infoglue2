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

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.util.CmsLogger;

import org.infoglue.cms.entities.structure.SiteNodeVO;


/**
 * This action removes a siteNode from the system.
 * 
 * @author Mattias Bogeblad
 */

public class DeleteSiteNodeAction extends WebworkAbstractAction
{
	private SiteNodeVO siteNodeVO;
	private Integer siteNodeId;
	private Integer parentSiteNodeId;
	private Integer changeTypeId;
	private Integer repositoryId;
    
	
	public DeleteSiteNodeAction()
	{
		this(new SiteNodeVO());
	}

	public DeleteSiteNodeAction(SiteNodeVO siteNodeVO) 
	{
		this.siteNodeVO = siteNodeVO;
	}
	
	protected String doExecute() throws Exception 
	{
		try
		{
			this.parentSiteNodeId = SiteNodeController.getParentSiteNode(this.siteNodeVO.getSiteNodeId()).getSiteNodeId();
		}
		catch(Exception e)
		{
			CmsLogger.logInfo("The siteNode must have been a root-siteNode because we could not find a parent.");
		}
		
		SiteNodeControllerProxy.getSiteNodeControllerProxy().acDelete(this.getInfoGluePrincipal(), this.siteNodeVO);

		return "success";
	}
	
	public void setSiteNodeId(Integer siteNodeId)
	{
		this.siteNodeVO.setSiteNodeId(siteNodeId);
	}

	public void setParentSiteNodeId(Integer parentSiteNodeId)
	{
		this.parentSiteNodeId = parentSiteNodeId;
	}

	public void setChangeTypeId(Integer changeTypeId)
	{
		this.changeTypeId = changeTypeId;
	}

	public Integer getSiteNodeId()
	{
		return this.parentSiteNodeId;
	}
	
	public Integer getUnrefreshedSiteNodeId()
	{
		return this.parentSiteNodeId;
	}
	
	public Integer getChangeTypeId()
	{
		return this.changeTypeId;
	}
        
	public String getErrorKey()
	{
		return "SiteNodeVersion.stateId";
	}
	
	public String getReturnAddress()
	{
		return "ViewSiteNode.action?siteNodeId=" + this.siteNodeVO.getId() + "&repositoryId=" + this.siteNodeVO.getRepositoryId();
	}
	
    public Integer getRepositoryId()
    {
        return repositoryId;
    }
    
    public void setRepositoryId(Integer repositoryId)
    {
        this.repositoryId = repositoryId;
    }
}
