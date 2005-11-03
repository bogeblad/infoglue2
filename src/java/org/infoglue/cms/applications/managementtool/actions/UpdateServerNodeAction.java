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

package org.infoglue.cms.applications.managementtool.actions;

import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;


/**
  * This is the action-class for UpdateRepository
  * 
  * @author Mattias Bogeblad
  */
public class UpdateServerNodeAction extends ViewRepositoryAction //WebworkAbstractAction
{
	
	private RepositoryVO repositoryVO;
	private Integer repositoryId;
	private String userAction = "";
	private ConstraintExceptionBuffer ceb;
	
	public UpdateServerNodeAction()
	{
		this(new RepositoryVO());
	}
	
	public UpdateServerNodeAction(RepositoryVO repositoryVO)
	{
		this.repositoryVO = repositoryVO;
		this.ceb = new ConstraintExceptionBuffer();	
	}

       	
	public String doExecute() throws Exception
    {
		super.initialize(getRepositoryId());

    	ceb.add(this.repositoryVO.validate());
    	ceb.throwIfNotEmpty();		
    	
    	String[] values = getRequest().getParameterValues("languageId");
		
		RepositoryController.getController().update(this.repositoryVO, values);
				
		return "success";
	}

	public String doLocal() throws Exception
    {
		super.initialize(getRepositoryId());

		ceb.throwIfNotEmpty();
    	
		RepositoryController.getController().update(this.repositoryVO);
		
		return "successLocal";
	}

	public String doSaveAndExit() throws Exception
    {
		doExecute();
						
		return "saveAndExit";
	}

	public String doSaveAndExitLocal() throws Exception
    {
		doLocal();
						
		return "saveAndExitLocal";
	}

	public void setRepositoryId(Integer repositoryId) throws Exception
	{
		this.repositoryVO.setRepositoryId(repositoryId);	
	}

    public java.lang.Integer getRepositoryId()
    {
        return this.repositoryVO.getRepositoryId();
    }
        
    public java.lang.String getName()
    {
    	return this.repositoryVO.getName();
    }
        
    public void setName(java.lang.String name)
    {
       	this.repositoryVO.setName(name);
    }

    public String getDescription()
    {
        return this.repositoryVO.getDescription();
    }
        
    public void setDescription(String description)
    {
       	this.repositoryVO.setDescription(description);
    }
    
    public String getDnsName()
    {
        return this.repositoryVO.getDnsName();
    }
        
    public void setDnsName(String dnsName)
    {
       	this.repositoryVO.setDnsName(dnsName);
    }
    
}
