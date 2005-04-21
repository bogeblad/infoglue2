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

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.infoglue.cms.controllers.kernel.impl.simple.CategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentCategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.PropertiesCategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.RoleControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.RolePropertiesController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserPropertiesController;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.RoleProperties;
import org.infoglue.cms.entities.management.RolePropertiesVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.structure.QualifyerVO;
import org.infoglue.cms.security.InfoGlueRole;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.dom.DOMBuilder;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ViewRolePropertiesAction extends ViewEntityPropertiesAction
{
	private String roleName;
	private RolePropertiesVO rolePropertiesVO;
	private List rolePropertiesVOList;
	
	
    public ViewRolePropertiesAction()
    {
        this.setCurrentAction("ViewRoleProperties.action");
        this.setUpdateAction("UpdateRoleProperties");
        this.setCancelAction("ViewRole.action");
        this.setToolbarKey("tool.managementtool.viewRoleProperties.header");
        this.setTitleKey("tool.managementtool.viewRoleProperties.header");
        this.setArguments("");
        this.setEntityName(RoleProperties.class.getName());
    }
		
	/**
	 * Initializes all properties needed for the usecase.
	 * @param extranetRoleId
	 * @throws Exception
	 */

	protected void initialize(String roleName) throws Exception
	{
	    super.initialize();
				
		CmsLogger.logInfo("roleName:" + roleName);
		System.out.println("roleName:" + roleName);

		List contentTypeDefinitionVOList = RolePropertiesController.getController().getContentTypeDefinitionVOList(roleName);
		if(contentTypeDefinitionVOList != null && contentTypeDefinitionVOList.size() > 0)
			this.setContentTypeDefinitionVO((ContentTypeDefinitionVO)contentTypeDefinitionVOList.get(0));
		
		InfoGlueRole infoGlueRole = RoleControllerProxy.getController().getRole(roleName);
		rolePropertiesVOList = RolePropertiesController.getController().getRolePropertiesVOList(roleName, this.getLanguageId());
		if(rolePropertiesVOList != null && rolePropertiesVOList.size() > 0)
		{
			this.rolePropertiesVO = (RolePropertiesVO)rolePropertiesVOList.get(0);
			this.setContentTypeDefinitionId(this.rolePropertiesVO.getContentTypeDefinitionId());
		}
		else
		{
			this.setContentTypeDefinitionId(this.getContentTypeDefinitionVO().getContentTypeDefinitionId());
		}
		System.out.println("this.rolePropertiesVO:" + this.rolePropertiesVO);
		
		this.setAttributes(ContentTypeDefinitionController.getController().getContentTypeAttributes(this.getContentTypeDefinitionVO().getSchemaValue()));	
	
		CmsLogger.logInfo("attributes:" + this.getContentTypeAttributes().size());		
		CmsLogger.logInfo("availableLanguages:" + this.getAvailableLanguages().size());		
		
	} 

	public String doExecute() throws Exception
	{
		this.initialize(getRoleName());   
		
		return "success";
	}


	/**
	 * Returns a list of digital assets available for this content version.
	 */
	
	public List getDigitalAssets()
	{
		List digitalAssets = null;
		
		try
		{
			if(this.rolePropertiesVO != null && this.rolePropertiesVO.getId() != null)
	       	{
	       		digitalAssets = RolePropertiesController.getController().getDigitalAssetVOList(this.rolePropertiesVO.getId());
	       	}
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("We could not fetch the list of digitalAssets: " + e.getMessage(), e);
		}
		
		return digitalAssets;
	}	

	
	
	

	/**
	 * Returns all current Category relationships for th specified attrbiute name
	 * @param attribute
	 * @return
	 */
	public List getRelatedCategories(String attribute)
	{
		try
		{
			if(this.rolePropertiesVO != null && this.rolePropertiesVO.getId() != null)
		    	return getPropertiesCategoryController().findByPropertiesAttribute(attribute, RoleProperties.class.getName(),  this.rolePropertiesVO.getId());
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("We could not fetch the list of defined category keys: " + e.getMessage(), e);
		}

		return Collections.EMPTY_LIST;
	}

	
	public String getXML()
	{
	    return (this.rolePropertiesVO == null) ? null : this.rolePropertiesVO.getValue();
	}

	
	public String getRoleName()
	{
		return roleName;
	}

	public RolePropertiesVO getRolePropertiesVO()
	{
		return rolePropertiesVO;
	}

	public List getRolePropertiesVOList()
	{
		return rolePropertiesVOList;
	}

	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
		this.setOwnerEntityId(roleName);
	}
    
    public Integer getEntityId()
    {
        return this.rolePropertiesVO.getId();
    }
    
    public void setOwnerEntityId(String ownerEntityId)
    {
        super.setOwnerEntityId(ownerEntityId);
        this.roleName = ownerEntityId;
    }
     
    public String getReturnAddress() throws Exception
    {
    	String URIEncoding = CmsPropertyHandler.getProperty("URIEncoding");
        return this.getCurrentAction() + "?roleName=" + URLEncoder.encode(this.roleName, URIEncoding) + "&languageId=" + this.getLanguageId();
    }

    public String getCancelAddress() throws Exception
    {
    	String URIEncoding = CmsPropertyHandler.getProperty("URIEncoding");
        return this.getCancelAction() + "?roleName=" + URLEncoder.encode(this.roleName, URIEncoding);
    }
}
