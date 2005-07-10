/* ===============================================================================
 *
 * Part of the InfoGlue SiteNode Management Platform (www.infoglue.org)
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
import org.infoglue.cms.controllers.kernel.impl.simple.AccessRightController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeVersionControllerProxy;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.exception.AccessConstraintException;
import org.infoglue.cms.util.AccessConstraintExceptionBuffer;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mattias Bogeblad
 * 
 * Present a list of siteNodeVersions under a given siteNode, recursing down in the hierarcy.
 * Used to publish an entire hierarchy of pages.
 * 
 */

public class ViewListSiteNodeVersionAction extends InfoGlueAbstractAction 
{

	private List siteNodeVersionVOList = new ArrayList();
	private List contentVersionVOList = new ArrayList();
	private Integer siteNodeVersionId;
	private Integer siteNodeId;
	private Integer repositoryId;
	
	

	protected String doExecute() throws Exception 
	{
		getLogger().info("siteNodeId:" + this.siteNodeId);
		getLogger().info("siteNodeVersionId:" + this.siteNodeVersionId);
		if(this.siteNodeVersionId == null)
		{
		    SiteNodeVersionVO siteNodeVersionVO = SiteNodeVersionControllerProxy.getSiteNodeVersionControllerProxy().getACLatestActiveSiteNodeVersionVO(this.getInfoGluePrincipal(), siteNodeId);
		    if(siteNodeVersionVO != null)
		        this.siteNodeVersionId = siteNodeVersionVO.getId();
		}
		
		if(this.siteNodeVersionId != null)
		{
			AccessConstraintExceptionBuffer ceb = new AccessConstraintExceptionBuffer();
		
			Integer protectedSiteNodeVersionId = SiteNodeVersionControllerProxy.getSiteNodeVersionControllerProxy().getProtectedSiteNodeVersionId(siteNodeVersionId);
			if(protectedSiteNodeVersionId != null && !AccessRightController.getController().getIsPrincipalAuthorized(this.getInfoGluePrincipal(), "SiteNodeVersion.SubmitToPublish", protectedSiteNodeVersionId.toString()))
				ceb.add(new AccessConstraintException("SiteNodeVersion.siteNodeVersionId", "1005"));
		
			ceb.throwIfNotEmpty();

			SiteNodeVersionController.getController().getSiteNodeAndAffectedItemsRecursive(this.siteNodeId, SiteNodeVersionVO.WORKING_STATE, this.siteNodeVersionVOList, this.contentVersionVOList);
		}

	    return "success";
	}
	

	public List getSiteNodeVersions()
	{
		return this.siteNodeVersionVOList;		
	}
	

	public Integer getSiteNodeId()
	{
		return siteNodeId;
	}

	public void setSiteNodeId(Integer siteNodeId)
	{
		this.siteNodeId = siteNodeId;
	}

	public Integer getSiteNodeVersionId()
	{
		return siteNodeVersionId;
	}

	public void setSiteNodeVersionId(Integer siteNodeVersionId)
	{
		this.siteNodeVersionId = siteNodeVersionId;
	}

    public Integer getRepositoryId()
    {
        return repositoryId;
    }
    
    public void setRepositoryId(Integer repositoryId)
    {
        this.repositoryId = repositoryId;
    }
    
    public List getContentVersionVOList()
    {
        return contentVersionVOList;
    }
    
    public List getSiteNodeVersionVOList()
    {
        return siteNodeVersionVOList;
    }
}
