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

import java.util.List;

import javax.transaction.SystemException;

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

/**
 * This action represents the Copy Content Usecase.
 */

public class CopyContentAction extends InfoGlueAbstractAction
{
	private static final long serialVersionUID = 1L;
	
    private Integer contentId;
    private Integer newParentContentId;
    private Integer repositoryId;
    private Integer maxAssetSize = -1;
	private String onlyLatestVersions = "false";
	protected List repositories = null;

	public void setContentId(Integer contentId)
	{
		this.contentId = contentId;
	}

	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	public void setNewParentContentId(Integer newParentContentId)
	{
		this.newParentContentId = newParentContentId;
	}

	public Integer getContentId()
	{
		return this.contentId;
	}

	public Integer getNewParentContentId()
	{
		return this.newParentContentId;
	}
    
	public Integer getRepositoryId()
	{
		return this.repositoryId;
	}

	public Integer getMaxAssetSize() 
	{
		return maxAssetSize;
	}

	public void setMaxAssetSize(Integer maxAssetSize) 
	{
		this.maxAssetSize = maxAssetSize;
	}

	public String getOnlyLatestVersions() 
	{
		return onlyLatestVersions;
	}

	public void setOnlyLatestVersions(String onlyLatestVersions) 
	{
		this.onlyLatestVersions = onlyLatestVersions;
	}

    public List getRepositories()
    {
        return repositories;
    }

    public String doInput() throws Exception
    {    	
		this.repositories = RepositoryController.getController().getAuthorizedRepositoryVOList(this.getInfoGluePrincipal(), false);

        return "input";
    }
	
    public String doExecute() throws Exception
    {
        if(this.newParentContentId == null)
        {
    		this.repositories = RepositoryController.getController().getAuthorizedRepositoryVOList(this.getInfoGluePrincipal(), false);
            return "input";
        }
        
        Long totalSize = ContentController.getContentController().getContentWeightTotal(contentId, true);
        if(totalSize > (500 * 1000 * 1000))
        	throw new SystemException("Folder / content is to large to copy. Please clean some versions or copy subparts.");

		ContentControllerProxy.getController().acCopyContent(this.getInfoGluePrincipal(), contentId, newParentContentId, maxAssetSize, onlyLatestVersions);
		
        return "success";
    }
    
    
    /*
    public String getErrorKey()
	{
		return "Content.parentContentId";
	}
	
	public String getReturnAddress()
	{
		return "ViewContent.action?contentId=" + this.contentVO.getId() + "&repositoryId=" + this.repositoryId;
	}
	*/   
}
