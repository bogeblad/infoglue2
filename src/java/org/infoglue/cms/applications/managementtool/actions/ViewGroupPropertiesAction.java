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
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.GroupControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.GroupPropertiesController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserPropertiesController;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.GroupPropertiesVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.security.InfoGlueGroup;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.dom.DOMBuilder;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;

import java.util.List;

public class ViewGroupPropertiesAction extends InfoGlueAbstractAction
{
	private final String currentAction		= "ViewGroupProperties.action";
	private final String updateAction 		= "UpdateGroupProperties";
	private final String labelKey 			= "Group Properties";
	private final String title 				= "Group Properties";
	private final String extraParameters 	= "";

	private String groupName;
	private GroupPropertiesVO groupPropertiesVO;
	private List groupPropertiesVOList;
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
	 * @param extranetGroupId
	 * @throws Exception
	 */

	protected void initialize(String groupName) throws Exception
	{
		this.availableLanguages = LanguageController.getController().getLanguageVOList();
		
		if(this.languageId == null && this.availableLanguages.size() > 0)
			this.languageId = ((LanguageVO)this.availableLanguages.get(0)).getLanguageId();
		
		CmsLogger.logInfo("Language:" + this.languageId);
		CmsLogger.logInfo("groupName:" + groupName);
		
		List contentTypeDefinitionVOList = GroupPropertiesController.getController().getContentTypeDefinitionVOList(groupName);
		if(contentTypeDefinitionVOList != null && contentTypeDefinitionVOList.size() > 0)
			this.contentTypeDefinitionVO = (ContentTypeDefinitionVO)contentTypeDefinitionVOList.get(0);
		
		CmsLogger.logInfo("contentTypeDefinitionVO:" + contentTypeDefinitionVO.getName());
		
		InfoGlueGroup infoGlueGroup = GroupControllerProxy.getController().getGroup(groupName);
		groupPropertiesVOList = GroupPropertiesController.getController().getGroupPropertiesVOList(groupName, this.languageId);
		if(groupPropertiesVOList != null && groupPropertiesVOList.size() > 0)
		{
			this.groupPropertiesVO = (GroupPropertiesVO)groupPropertiesVOList.get(0);
			this.contentTypeDefinitionId = this.groupPropertiesVO.getLanguageId();
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
		this.initialize(getGroupName());   
		
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
	 * Returns a list of digital assets available for this content version.
	 */
	
	public List getDigitalAssets()
	{
		List digitalAssets = null;
		
		try
		{
			if(this.groupPropertiesVO != null && this.groupPropertiesVO.getId() != null)
	       	{
	       		digitalAssets = GroupPropertiesController.getController().getDigitalAssetVOList(this.groupPropertiesVO.getId());
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
		return (this.groupPropertiesVO == null) ? null : this.groupPropertiesVO.getValue();
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
	
	public String getGroupName()
	{
		return groupName;
	}

	public GroupPropertiesVO getGroupPropertiesVO()
	{
		return groupPropertiesVO;
	}

	public List getGroupPropertiesVOList()
	{
		return groupPropertiesVOList;
	}

	public void setGroupName(String string)
	{
		groupName = string;
	}

}
