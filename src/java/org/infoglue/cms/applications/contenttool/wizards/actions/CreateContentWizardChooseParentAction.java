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

package org.infoglue.cms.applications.contenttool.wizards.actions;

import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.applications.contenttool.actions.ViewContentTreeActionInterface;

import org.infoglue.cms.util.ConstraintExceptionBuffer;

import java.util.List;

/**
 * This action represents a tree where the user can select where to save his new content.
 */

public class CreateContentWizardChooseParentAction extends CreateContentWizardAbstractAction implements ViewContentTreeActionInterface
{
	//Used by the tree only
	private Integer contentId;
	private String tree;
	private String hideLeafs;
	
	private Integer parentContentId;
	private Integer repositoryId;
	private ConstraintExceptionBuffer ceb;
	
	private String returnAddress;


	public CreateContentWizardChooseParentAction()
	{
		this(new ContentVO());
	}
	
	public CreateContentWizardChooseParentAction(ContentVO contentVO)
	{
		this.ceb = new ConstraintExceptionBuffer();			
	}	

	public String doExecute() throws Exception
	{
		return "success";
	}
	
	public Integer getTopRepositoryId() throws ConstraintException, SystemException, Bug
	{
		List repositories = RepositoryController.getController().getAuthorizedRepositoryVOList(this.getInfoGluePrincipal(), false);
		
		Integer topRepositoryId = null;

		if (repositoryId != null)
			topRepositoryId = repositoryId;

		if(repositories.size() > 0)
		{
			topRepositoryId = ((RepositoryVO)repositories.get(0)).getRepositoryId();
		}
  	
		return topRepositoryId;
	}
  
	public void setHideLeafs(String hideLeafs)
	{
		this.hideLeafs = hideLeafs;
	}

	public String getHideLeafs()
	{
		return this.hideLeafs;
	}    

	public String getTree()
	{
		return tree;
	}

	public void setTree(String tree)
	{
		this.tree = tree;
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
		try
		{
			if(this.repositoryId == null)
			{	
				this.repositoryId = (Integer)getHttpSession().getAttribute("repositoryId");
					
				if(this.repositoryId == null)
				{
					this.repositoryId = getTopRepositoryId();
					getHttpSession().setAttribute("repositoryId", this.repositoryId);		
				}
			}
		}
		catch(Exception e)
		{
		}
	    	
		return repositoryId;
	}

	public void setContentId(Integer contentId)
	{
		this.contentId = contentId;
	}

	public Integer getContentId()
	{
		return this.contentId;
	}    
	
	public String getReturnAddress()
	{
		return returnAddress;
	}

	public void setReturnAddress(String string)
	{
		returnAddress = string;
	}

}
