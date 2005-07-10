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

import org.infoglue.cms.security.InfoGlueRole;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.RoleControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.RolePropertiesController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;

import java.util.*;

public class ViewRoleAction extends InfoGlueAbstractAction
{
	private String roleName;
	private boolean supportsUpdate = true;
	private InfoGlueRole infoGlueRole;
	private List infoGluePrincipals;
	private List assignedInfoGluePrincipals;
	private List contentTypeDefinitionVOList;
	private List assignedContentTypeDefinitionVOList;    
	
    protected void initialize(String roleName) throws Exception
    {
		this.supportsUpdate				= RoleControllerProxy.getController().getSupportUpdate();
		this.infoGlueRole				= RoleControllerProxy.getController().getRole(roleName);
		this.assignedInfoGluePrincipals	= RoleControllerProxy.getController().getInfoGluePrincipals(roleName);
		this.infoGluePrincipals			= UserControllerProxy.getController().getAllUsers();
		
		this.contentTypeDefinitionVOList 			= ContentTypeDefinitionController.getController().getContentTypeDefinitionVOList(ContentTypeDefinitionVO.EXTRANET_ROLE_PROPERTIES);
		this.assignedContentTypeDefinitionVOList 	= RolePropertiesController.getController().getContentTypeDefinitionVOList(roleName);  
    } 

    public String doExecute() throws Exception
    {
        this.initialize(getRoleName());
        
        return "success";
    }
        
    public String getRoleName()
    {
        return roleName;
    }

	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}
            
    public java.lang.String getDescription()
    {
        return this.infoGlueRole.getDescription();
    }
        
  	public List getAllInfoGluePrincipals() throws Exception
	{
		return this.infoGluePrincipals;
	}	
	
	public List getAssignedInfoGluePrincipals() throws Exception
	{
		return this.assignedInfoGluePrincipals;
	}

	public List getAssignedContentTypeDefinitionVOList()
	{
		return assignedContentTypeDefinitionVOList;
	}

	public List getContentTypeDefinitionVOList()
	{
		return contentTypeDefinitionVOList;
	}

	public void setAssignedContentTypeDefinitionVOList(List list)
	{
		assignedContentTypeDefinitionVOList = list;
	}

	public void setContentTypeDefinitionVOList(List list)
	{
		contentTypeDefinitionVOList = list;
	}

	public boolean getSupportsUpdate()
	{
		return this.supportsUpdate;
	}
}
