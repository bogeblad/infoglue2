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
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

public class CreateRepositoryAction extends WebworkAbstractAction
{
	private RepositoryVO repositoryVO;
	private ConstraintExceptionBuffer ceb;

	
	public CreateRepositoryAction()
	{
		this(new RepositoryVO());
	}
	
	public CreateRepositoryAction(RepositoryVO repositoryVO)
	{
		this.repositoryVO = repositoryVO;
		this.ceb = new ConstraintExceptionBuffer();
			
	}	
	public Integer getRepositoryId()
	{
		return this.repositoryVO.getId();	
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


    public String doExecute() throws Exception
    {
		ceb.add(this.repositoryVO.validate());
    	ceb.throwIfNotEmpty();				
    	
		this.repositoryVO = RepositoryController.getController().create(repositoryVO);
		
        return "success";
    }
        
    public String doInput() throws Exception
    {
    	return "input";
    }    
}
