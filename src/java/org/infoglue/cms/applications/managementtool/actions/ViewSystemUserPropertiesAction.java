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
import org.dom4j.Node;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.UserPropertiesController;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.UserPropertiesVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.security.InfoGluePrincipal;

import org.infoglue.cms.util.dom.DOMBuilder;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;

import java.util.List;

public class ViewSystemUserPropertiesAction extends InfoGlueAbstractAction
{
	private final String currentAction		= "ViewSystemUserProperties.action";
	private final String updateAction 		= "UpdateSystemUserProperties";
	private final String labelKey 			= "SystemUser Properties";
	private final String title 				= "SystemUser Properties";
	private final String extraParameters 	= "";
	
	private String userName;
	private UserPropertiesVO userPropertiesVO;
	private List userPropertiesVOList;
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
	 * @param systemUserId
	 * @throws Exception
	 */

	protected void initialize(String userName) throws Exception
	{
		this.availableLanguages = LanguageController.getController().getLanguageVOList();
		
		if(this.languageId == null && this.availableLanguages.size() > 0)
			this.languageId = ((LanguageVO)this.availableLanguages.get(0)).getLanguageId();
		
		getLogger().info("Language:" + this.languageId);
		
		List contentTypeDefinitionVOList = UserPropertiesController.getController().getContentTypeDefinitionVOList(userName);
		if(contentTypeDefinitionVOList != null && contentTypeDefinitionVOList.size() > 0)
			this.contentTypeDefinitionVO = (ContentTypeDefinitionVO)contentTypeDefinitionVOList.get(0);
		
		getLogger().info("contentTypeDefinitionVO:" + contentTypeDefinitionVO.getName());
		
		InfoGluePrincipal infoGluePrincipal = UserControllerProxy.getController().getUser(userName);
		userPropertiesVOList = UserPropertiesController.getController().getUserPropertiesVOList(userName, this.languageId);
		if(userPropertiesVOList != null && userPropertiesVOList.size() > 0)
		{
			this.userPropertiesVO = (UserPropertiesVO)userPropertiesVOList.get(0);
			this.contentTypeDefinitionId = this.userPropertiesVO.getLanguageId();
		}
		else
		{
			this.contentTypeDefinitionId = this.contentTypeDefinitionVO.getContentTypeDefinitionId();
		}

		this.attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(this.contentTypeDefinitionVO.getSchemaValue());	
	
		getLogger().info("attributes:" + this.attributes.size());		
		getLogger().info("availableLanguages:" + this.availableLanguages.size());		
	} 

	public String doExecute() throws Exception
	{
		this.initialize(getUserName());   
		
		return "success";
	}
		
	/**
	 * This method fetches a value from the xml that is the contentVersions Value. If the 
	 * contentVersioVO is null the contentVersion has not been created yet and no values are present.
	 */
	 
	public String getAttributeValue(String key)
	{
		getLogger().info("Getting: " + key);
		String value = "";
		try
		{
			String xml = this.getXML();
			if(xml != null)
			{	
				getLogger().info("key:" + key);
				getLogger().info("XML:" + this.getXML());
				
				DOMBuilder domBuilder = new DOMBuilder();
				
				Document document = domBuilder.getDocument(this.getXML());
				getLogger().info("rootElement:" + document.getRootElement().asXML());
				
				Node node = document.getRootElement().selectSingleNode("attributes/" + key);
				if(node != null)
				{
					value = node.getStringValue();
					getLogger().info("Getting value: " + value);
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
	
	public List getAvailableLanguages()
	{
		return this.availableLanguages;
	}
	
	public Integer getContentTypeDefinitionId()
	{
		return this.contentTypeDefinitionId;
	}
	
	public UserPropertiesVO getUserPropertiesVO()
	{
		return userPropertiesVO;
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
		return (this.userPropertiesVO == null) ? null : this.userPropertiesVO.getValue();
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
	
	public String getUserName()
	{
		return this.userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

}
