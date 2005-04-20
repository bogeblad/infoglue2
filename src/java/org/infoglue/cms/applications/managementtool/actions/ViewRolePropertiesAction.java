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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ViewRolePropertiesAction extends InfoGlueAbstractAction
{
	private static CategoryController categoryController = CategoryController.getController();
	private static PropertiesCategoryController propertiesCategoryController = PropertiesCategoryController.getController();

	private final String currentAction		= "ViewRoleProperties.action";
	private final String updateAction 		= "UpdateRoleProperties";
	private final String labelKey 			= "Role Properties";
	private final String title 				= "Role Properties";
	private final String extraParameters 	= "";

	private String roleName;
	private RolePropertiesVO rolePropertiesVO;
	private List rolePropertiesVOList;
	private List availableLanguages;
	private List contentTypeDefinitionVOList;
	private List attributes;
	private ContentTypeDefinitionVO contentTypeDefinitionVO;
	private Integer contentTypeDefinitionId;
	private Integer languageId;
	private Integer currentEditorId;
	private String attributeName = "";
	private String textAreaId = "";
	
	
	/**
	 * Initializes all properties needed for the usecase.
	 * @param extranetRoleId
	 * @throws Exception
	 */

	protected void initialize(String roleName) throws Exception
	{
		this.availableLanguages = LanguageController.getController().getLanguageVOList();
		
		if(this.languageId == null && this.availableLanguages.size() > 0)
			this.languageId = ((LanguageVO)this.availableLanguages.get(0)).getLanguageId();
		
		CmsLogger.logInfo("Language:" + this.languageId);
		CmsLogger.logInfo("roleName:" + roleName);
		
		List contentTypeDefinitionVOList = RolePropertiesController.getController().getContentTypeDefinitionVOList(roleName);
		if(contentTypeDefinitionVOList != null && contentTypeDefinitionVOList.size() > 0)
			this.contentTypeDefinitionVO = (ContentTypeDefinitionVO)contentTypeDefinitionVOList.get(0);
		
		CmsLogger.logInfo("contentTypeDefinitionVO:" + contentTypeDefinitionVO.getName());
		
		InfoGlueRole infoGlueRole = RoleControllerProxy.getController().getRole(roleName);
		rolePropertiesVOList = RolePropertiesController.getController().getRolePropertiesVOList(roleName, this.languageId);
		if(rolePropertiesVOList != null && rolePropertiesVOList.size() > 0)
		{
			this.rolePropertiesVO = (RolePropertiesVO)rolePropertiesVOList.get(0);
			this.contentTypeDefinitionId = this.rolePropertiesVO.getLanguageId();
		}
		else
		{
			this.contentTypeDefinitionId = this.contentTypeDefinitionVO.getContentTypeDefinitionId();
		}
		
		this.attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(this.contentTypeDefinitionVO.getSchemaValue());	
	
		CmsLogger.logInfo("attributes:" + this.attributes.size());		
		CmsLogger.logInfo("availableLanguages:" + this.availableLanguages.size());		
		
	} 

	public String doExecute() throws Exception
	{
		this.initialize(getRoleName());   
		
		return "success";
	}
		
	/**
	 * This method fetches a value from the xml that is the contentVersions Value. If the 
	 * contentVersioVO is null the contentVersion has not been created yet and no values are present.
	 */
	 
	public String getAttributeValue(String key)
	{
		String value = "";
		try
		{
			String xml = this.getXML();
			if(xml != null)
			{	
				CmsLogger.logInfo("key:" + key);
				CmsLogger.logInfo("XML:" + this.getXML());
				
				DOMBuilder domBuilder = new DOMBuilder();
				
				Document document = domBuilder.getDocument(this.getXML());
				CmsLogger.logInfo("rootElement:" + document.getRootElement().asXML());
				
				Node node = document.getRootElement().selectSingleNode("attributes/" + key);
				if(node != null)
				{
					value = node.getStringValue();
					CmsLogger.logInfo("Getting value: " + value);
					if(value != null)
						value = new VisualFormatter().escapeHTML(value);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return value;
	}
	
	/**
	 * This method fetches a value from the xml that is the contentVersions Value. If the 
	 * contentVersioVO is null the contentVersion has not been created yet and no values are present.
	 */
	 
	public String getUnescapedAttributeValue(String key)
	{
		String value = "";
		try
		{
		    String xml = this.getXML();
		    
			int startTagIndex = xml.indexOf("<" + key + ">");
			int endTagIndex   = xml.indexOf("]]></" + key + ">");

			if(startTagIndex > 0 && startTagIndex < xml.length() && endTagIndex > startTagIndex && endTagIndex <  xml.length())
			{
				value = xml.substring(startTagIndex + key.length() + 11, endTagIndex);
			}					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return value;
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
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetUrl(Integer digitalAssetId) throws Exception
	{
		String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetUrl(digitalAssetId);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("We could not get the url of the digitalAsset: " + e.getMessage(), e);
			imageHref = e.getMessage();
		}
		
		return imageHref;
	}
	
	
	/**
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetThumbnailUrl(Integer digitalAssetId) throws Exception
	{
		String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetThumbnailUrl(digitalAssetId);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("We could not get the url of the thumbnail: " + e.getMessage(), e);
			imageHref = e.getMessage();
		}
		
		return imageHref;
	}
	
	public String getQualifyerPath(String entity, String entityId)
	{	
		try
		{	
			if(entity.equalsIgnoreCase("Content"))
				return ContentController.getContentController().getContentVOWithId(new Integer(entityId)).getName();
			else if(entity.equalsIgnoreCase("SiteNode"))
				return SiteNodeController.getController().getSiteNodeVOWithId(new Integer(entityId)).getName();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}
	
	public List getContentRelationQualifyers(String qualifyerXML)
	{
		CmsLogger.logInfo("Content qualifyerXML:" + qualifyerXML);
	    return parseQualifyersFromXML(qualifyerXML, "contentId");
	}

	public List getSiteNodeRelationQualifyers(String qualifyerXML)
	{
		CmsLogger.logInfo("Content qualifyerXML:" + qualifyerXML);
	    return parseQualifyersFromXML(qualifyerXML, "siteNodeId");
	}

	private List parseQualifyersFromXML(String qualifyerXML, String currentEntityIdentifyer)
	{
		List qualifyers = new ArrayList(); 
    	
		if(qualifyerXML == null || qualifyerXML.length() == 0)
			return qualifyers;
		
		try
		{
			Document document = new DOMBuilder().getDocument(qualifyerXML);
			
			String entity = document.getRootElement().attributeValue("entity");
			
			List children = document.getRootElement().elements();
			Iterator i = children.iterator();
			while(i.hasNext())
			{
				Element child = (Element)i.next();
				String id = child.getStringValue();
				
				QualifyerVO qualifyerVO = new QualifyerVO();
				qualifyerVO.setName(currentEntityIdentifyer);
				qualifyerVO.setValue(id);    
				qualifyerVO.setPath(this.getQualifyerPath(entity, id));
				//qualifyerVO.setSortOrder(new Integer(i));
				qualifyers.add(qualifyerVO);     	
			}		        	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return qualifyers;
	}
	
	/**
	 * Return the listing of Category attributes for this type of Content
	 */
	public List getDefinedCategoryKeys()
	{
		try
		{
			if(contentTypeDefinitionVO != null)
				return ContentTypeDefinitionController.getController().getDefinedCategoryKeys(contentTypeDefinitionVO.getSchemaValue());
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("We could not fetch the list of defined category keys: " + e.getMessage(), e);
		}

		return Collections.EMPTY_LIST;
	}

	/**
	 * Returns the Category tree for the given Category id.
	 * @param categoryId The base Category
	 * @return A list of all Children (and their children, etc)
	 */
	public List getAvailableCategories(Integer categoryId)
	{
		try
		{	
		    String protectCategories = CmsPropertyHandler.getProperty("protectCategories");
		    if(protectCategories != null && protectCategories.equalsIgnoreCase("true"))
		        return categoryController.getAuthorizedActiveChildren(categoryId, this.getInfoGluePrincipal());
			else
			    return categoryController.findAllActiveChildren(categoryId);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("We could not fetch the list of categories: " + e.getMessage(), e);
		}

		return Collections.EMPTY_LIST;
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
		    	return propertiesCategoryController.findByPropertiesAttribute(attribute, RoleProperties.class.getName(),  this.rolePropertiesVO.getId());
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("We could not fetch the list of defined category keys: " + e.getMessage(), e);
		}

		return Collections.EMPTY_LIST;
	}

	public List getAvailableLanguages()
	{
		return this.availableLanguages;
	}
	
        
	public Integer getContentTypeDefinitionId()
	{
		return this.contentTypeDefinitionId;
	}
	
	public String getExtraParameters()
	{
		return extraParameters;
	}

	public String getLabelKey()
	{
		return labelKey;
	}

	public String getTitle()
	{
		return title;
	}

	public String getUpdateAction()
	{
		return updateAction;
	}

	public String getCurrentAction()
	{
		return currentAction;
	}

	public String getXML()
	{
		return (this.rolePropertiesVO == null) ? null : this.rolePropertiesVO.getValue();
	}

	public Integer getLanguageId()
	{
		return languageId;
	}

	public void setLanguageId(Integer languageId)
	{
		this.languageId = languageId;
	}

	public Integer getCurrentEditorId()
	{
		return currentEditorId;
	}

	public void setCurrentEditorId(Integer integer)
	{
		currentEditorId = integer;
	}

	public String getAttributeName()
	{
		return this.attributeName;
	}

	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}

	public String getTextAreaId()
	{
		return this.textAreaId;
	}

	public void setTextAreaId(String textAreaId)
	{
		this.textAreaId = textAreaId;
	}
	
	/**
	 * This method returns the attributes in the content type definition for generation.
	 */
	
	public List getContentTypeAttributes()
	{   		
		return this.attributes;
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

	public void setRoleName(String string)
	{
		roleName = string;
	}

}
