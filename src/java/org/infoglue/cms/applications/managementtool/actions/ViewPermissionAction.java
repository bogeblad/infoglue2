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

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.RoleControllerProxy;
import org.infoglue.cms.entities.management.RoleVO;

import java.util.Collection;
import java.util.List;


/**
 * @author Magnus Güvenal
 *
 *	Action class for usecase ViewListSystemUserUCC 
 * 
 */
public class ViewPermissionAction extends WebworkAbstractAction 
{
	private List roles;
	private RoleVO roleVO;
	private java.lang.Integer repositoryId;
	private List assignedInfoGlueRoles;

		
	public ViewPermissionAction()throws Exception
	{
		this(new RoleVO());
	}
	
	public ViewPermissionAction(RoleVO roleVO)
	{
		this.roleVO = roleVO;
	}
	
	public void setRepositoryId(java.lang.Integer repositoryId)
	{
		this.repositoryId = repositoryId;	
	}
	
	public java.lang.Integer getRepositoryId()
	{
		return this.repositoryId;
	}

	protected String doExecute() throws Exception 
	{
		return "success";
	}
	
	
	public String doUpdate() throws Exception{
    	
		String[] values = getRequest().getParameterValues("roleName");
		
		RepositoryController.getController().updateRepositoryRoles(this.repositoryId, values);

		return SUCCESS;	
	}
	
	public Collection getDefinedRoles()throws Exception
	{
		return RoleControllerProxy.getController().getAllRoles();
	}

	public ViewPermissionAction getThis()
	{
		return this;
	}

	
	public List getAssignedInfoGlueRoles() throws Exception
	{
		return RepositoryController.getController().getAssignedRoles(repositoryId);
	}

	public void setAssignedInfoGlueRoles(List assignedInfoGlueRoles)
	{
		this.assignedInfoGlueRoles = assignedInfoGlueRoles;
	}

}

