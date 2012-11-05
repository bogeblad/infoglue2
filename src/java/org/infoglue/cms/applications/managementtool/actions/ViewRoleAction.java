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

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.RoleControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.RolePropertiesController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.security.InfoGlueRole;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.sorters.ReflectionComparator;

public class ViewRoleAction extends InfoGlueAbstractAction
{
    private final static Logger logger = Logger.getLogger(ViewRoleAction.class.getName());

	private static final long serialVersionUID = 1L;

	private String roleName;
	private boolean supportsUpdate = true;
	private Integer availableSystemUserCount = 0;
	private InfoGlueRole infoGlueRole;
	private List infoGluePrincipals = new ArrayList();
	private List assignedInfoGluePrincipals;
	private List unassignedInfoGluePrincipals;
	private List contentTypeDefinitionVOList;
	private List assignedContentTypeDefinitionVOList;    
	
	/**
	 * This method initializes the view by populating all the entities. 
	 * It fetches the role itself, the type of authorization update support and all the assigned principals.
	 * It then populates a list of unassigned principals.
	 */

    protected void initialize(String roleName) throws Exception
    {
    	this.infoGlueRole				= RoleControllerProxy.getController().getRole(roleName);
		this.supportsUpdate				= this.infoGlueRole.getAutorizationModule().getSupportUpdate();
		this.availableSystemUserCount 	= UserControllerProxy.getTableCount("cmSystemUser", "userName").getCount();
		if(this.supportsUpdate)
		{
			this.assignedInfoGluePrincipals	= this.infoGlueRole.getAutorizationModule().getRoleUsers(roleName);
			if(availableSystemUserCount < 5000)
			{
				this.infoGluePrincipals			= this.infoGlueRole.getAutorizationModule().getUsers();
				//this.supportsUpdate				= RoleControllerProxy.getController().getSupportUpdate();
				if(this.supportsUpdate) //Only fetch if the user can edit.
				{
					List newInfogluePrincipals = new ArrayList();
					newInfogluePrincipals.addAll(this.infoGluePrincipals);
					newInfogluePrincipals.removeAll(assignedInfoGluePrincipals);
					this.unassignedInfoGluePrincipals = newInfogluePrincipals;
				}
			}
		}
		
		this.contentTypeDefinitionVOList 			= ContentTypeDefinitionController.getController().getContentTypeDefinitionVOList(ContentTypeDefinitionVO.EXTRANET_ROLE_PROPERTIES);
		this.assignedContentTypeDefinitionVOList 	= RolePropertiesController.getController().getContentTypeDefinitionVOList(roleName);  
    } 

    public String doExecute() throws Exception
    {
        this.initialize(getRoleName());
        
        return "success";
    }

    public String doV3() throws Exception
    {
        this.initialize(getRoleName());
        
        return "successV3";
    }

    public String getRoleName()
    {
        return roleName;
    }

	public void setRoleName(String roleName) throws Exception
	{	
		if(roleName != null)
		{
			String fromEncoding = CmsPropertyHandler.getURIEncoding();
			String toEncoding = "utf-8";
			
			logger.info("roleName:" + roleName);
			String testRoleName = new String(roleName.getBytes(fromEncoding), toEncoding);
			if(logger.isInfoEnabled())
			{
				for(int i=0; i<roleName.length(); i++)
					logger.info("c:" + roleName.charAt(i) + "=" + (int)roleName.charAt(i));
			}
			if(testRoleName.indexOf((char)65533) == -1)
				roleName = testRoleName;
			
			logger.info("roleName after:" + roleName);
		}
		
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
	    Collections.sort(this.assignedInfoGluePrincipals, new ReflectionComparator("name"));

	    return this.assignedInfoGluePrincipals;
	}

	public List getUnAssignedInfoGluePrincipals() throws Exception
	{
		return this.unassignedInfoGluePrincipals;
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

	public Integer getAvailableSystemUserCount()
	{
		return this.availableSystemUserCount;
	}
}
