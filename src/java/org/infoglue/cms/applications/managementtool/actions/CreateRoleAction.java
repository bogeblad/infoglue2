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
import org.infoglue.cms.controllers.kernel.impl.simple.RoleControllerProxy;
import org.infoglue.cms.entities.management.RoleVO;
import org.infoglue.cms.security.InfoGlueRole;
import org.infoglue.cms.util.ConstraintExceptionBuffer;


/**
 * @author mgu
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */


public class CreateRoleAction extends WebworkAbstractAction
{
	private RoleVO roleVO;
	private InfoGlueRole infoGlueRole;
	private ConstraintExceptionBuffer ceb;

	public CreateRoleAction()
	{
		this(new RoleVO());
	}

	public CreateRoleAction(RoleVO RoleVO)
	{
		this.roleVO = RoleVO;	
		this.ceb = new ConstraintExceptionBuffer();
	}
		
	public String doInput() throws Exception
    {
    	return "input";
    }
	
	protected String doExecute() throws Exception 
	{
		ceb.add(this.roleVO.validate());
    	ceb.throwIfNotEmpty();	
    				
		this.infoGlueRole = RoleControllerProxy.getController().createRole(this.roleVO);
		
		return "success";
	}
	
	public void setRoleName(String roleName)
	{
		this.roleVO.setRoleName(roleName);	
	}

    public String getRoleName()
    {
        return this.roleVO.getRoleName();
    }
	
	public void setDescription(java.lang.String description)
	{
        this.roleVO.setDescription(description);
	}

	public String getDescription()
	{
		return this.roleVO.getDescription();	
	}
    


}
