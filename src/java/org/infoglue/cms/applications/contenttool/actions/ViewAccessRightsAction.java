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

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.exception.AccessConstraintException;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.AccessConstraintExceptionBuffer;
import org.infoglue.cms.util.CmsLogger;

import java.util.ArrayList;
import java.util.List;


/** 
 * This class shows which roles has access to the siteNode.
 */

public class ViewAccessRightsAction extends WebworkAbstractAction
{
	private Integer interceptionPointId = null;
	private String interceptionPointName = null;
	private String interceptionPointCategory = null;
	private String extraParameters = "";
	private String returnAddress;
	private String colorScheme;

	private List interceptionPointVOList = new ArrayList();
	private List roleList = null;
	//private List accessRightVOList = null;
	//private List extranetRoleVOList = null;
	//private List extranetAccessVOList = null;	 
    
    public String doExecute() throws Exception
    {
    	AccessConstraintExceptionBuffer ceb = new AccessConstraintExceptionBuffer();
		
		if(interceptionPointCategory.equalsIgnoreCase("Content"))
		{	
			Integer contentId = new Integer(extraParameters);
			ContentVO contentVO = ContentControllerProxy.getController().getContentVOWithId(contentId);
			if(!contentVO.getCreatorName().equalsIgnoreCase(this.getInfoGluePrincipal().getName()))
			{
				Integer protectedContentId = ContentControllerProxy.getController().getProtectedContentId(contentId);
				if(ContentControllerProxy.getController().getIsContentProtected(contentId) && !AccessRightController.getController().getIsPrincipalAuthorized(this.getInfoGluePrincipal(), "Content.ChangeAccessRights", contentId.toString()))
					ceb.add(new AccessConstraintException("Content.contentId", "1006"));
			}
		}
		else if(interceptionPointCategory.equalsIgnoreCase("SiteNodeVersion"))
		{	
			Integer siteNodeVersionId = new Integer(extraParameters);
			SiteNodeVersionVO siteNodeVersionVO = SiteNodeVersionController.getController().getSiteNodeVersionVOWithId(siteNodeVersionId);
			if(!siteNodeVersionVO.getVersionModifier().equalsIgnoreCase(this.getInfoGluePrincipal().getName()))
			{
				Integer protectedSiteNodeVersionId = SiteNodeVersionControllerProxy.getSiteNodeVersionControllerProxy().getProtectedSiteNodeVersionId(siteNodeVersionId);
				if(protectedSiteNodeVersionId != null && !AccessRightController.getController().getIsPrincipalAuthorized(this.getInfoGluePrincipal(), "SiteNodeVersion.ChangeAccessRights", siteNodeVersionId.toString()))
					ceb.add(new AccessConstraintException("SiteNodeVersion.siteNodeId", "1006"));
			}
		}
		
		ceb.throwIfNotEmpty();
		
		this.interceptionPointVOList = InterceptionPointController.getController().getInterceptionPointVOList(interceptionPointCategory);
		//this.roleVOList = RoleController.getController().getRoleVOList();
		this.roleList = RoleControllerProxy.getController().getAllRoles();
		//this.accessRightVOList = AccessRightController.getController().getAccessRightVOList(this.interceptionPointId, extraParameters);
				
		//this.extranetRoleVOList = ExtranetRoleController.getController().getExtranetRoleVOList();
		//this.extranetAccessVOList = ExtranetAccessController.getController().getExtranetAccessVOList(this.name, this.value);
        
    	return "success";
    }
    
    public ViewAccessRightsAction getThis()
	{
		return this;
	}
	
	public boolean getHasAccessRight(Integer interceptionPointId, String extraParameters, String roleName) throws SystemException, Bug
	{
		List accessRights = AccessRightController.getController().getAccessRightVOList(interceptionPointId, extraParameters, roleName);
		boolean hasAccessRight = (accessRights.size() > 0) ? true : false;
		CmsLogger.logInfo("hasAccessRight:" + hasAccessRight);
		return hasAccessRight;
	}
	
	/*        
	public List getExtranetRoleVOList()
	{
		return this.extranetRoleVOList;
	}

	public void setExtranetRoleVOList(List extranetRoleVOList)
	{
		this.extranetRoleVOList = extranetRoleVOList;
	}

	public List getExtranetAccessVOList()
	{
		return this.extranetAccessVOList;
	}

	public void setExtranetAccessVOList(List extranetAccessVOList)
	{
		this.extranetAccessVOList = extranetAccessVOList;
	}
	
	public List getAccessRightVOList()
	{
		return accessRightVOList;
	}
	*/
	
	public List getRoleList()
	{
		return this.roleList;
	}

	public String getReturnAddress()
	{
		return returnAddress;
	}

	public void setReturnAddress(String returnAddress)
	{
		this.returnAddress = returnAddress;
	}

	public String getColorScheme()
	{
		return this.colorScheme;
	}

	public void setColorScheme(String colorScheme)
	{
		this.colorScheme = colorScheme;
	}

	public Integer getInterceptionPointId()
	{
		return this.interceptionPointId;
	}

	public void setInterceptionPointId(Integer interceptionPointId)
	{
		this.interceptionPointId = interceptionPointId;
	}

	public String getExtraParameters()
	{
		return this.extraParameters;
	}

	public String getInterceptionPointName()
	{
		return this.interceptionPointName;
	}

	public void setExtraParameters(String extraParameters)
	{
		this.extraParameters = extraParameters;
	}

	public void setInterceptionPointName(String interceptionPointName)
	{
		this.interceptionPointName = interceptionPointName;
	}

	public String getInterceptionPointCategory()
	{
		return this.interceptionPointCategory;
	}

	public void setInterceptionPointCategory(String interceptionPointCategory)
	{
		this.interceptionPointCategory = interceptionPointCategory;
	}

	public List getInterceptionPointVOList()
	{
		return this.interceptionPointVOList;
	}

}
