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

import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.exception.*;

import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;

import java.util.List;
import java.io.*;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource; 

/**
 * This class implements the action class for viewContentTypeDefinition.
 * The use-case lets the user see all information about a specific site/contentTypeDefinition.
 * 
 * @author Mattias Bogeblad  
 */

public class ViewContentTypeDefinitionAction extends WebworkAbstractAction
{ 
    private ContentTypeDefinitionVO contentTypeDefinitionVO;
	private String currentContentTypeEditorViewLanguageCode;
    private List attributes = null;
    private List availableLanguages = null;
    private List languageVOList;
    private String title;
    private String inputTypeId;
    private String attributeName;
    private String newAttributeName;
	private String attributeParameterId;
	private String attributeParameterValueId;
	private String newAttributeParameterValueId;
	private String attributeParameterValueLabel;
	private String attributeParameterValueLocale;
	private String attributeToExpand;
	private String assetKey;
	private String newAssetKey;
	
    public ViewContentTypeDefinitionAction()
    {
        this(new ContentTypeDefinitionVO());
    }
    
    public ViewContentTypeDefinitionAction(ContentTypeDefinitionVO contentTypeDefinitionVO)
    {
        this.contentTypeDefinitionVO = contentTypeDefinitionVO;
    }
    
    protected void initialize(Integer contentTypeDefinitionId) throws Exception
    {
        this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithId(contentTypeDefinitionId);
    	//CmsLogger.logInfo("Initializing:" + this.contentTypeDefinitionVO.getSchemaValue());
    	
		this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().validateAndUpdateContentType(this.contentTypeDefinitionVO);
        this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithId(contentTypeDefinitionId);
		this.attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(this.contentTypeDefinitionVO.getSchemaValue());
		this.availableLanguages = LanguageController.getController().getLanguageVOList();
    } 

    /**
     * The main method that fetches the Value-object for this use-case
     */
    
    public String doExecute() throws Exception
    {
        this.initialize(getContentTypeDefinitionId());
        return "useEditor";
    }
            
	/**
	 * The method that initializes all for the editor mode
	 */
    
	public String doUseEditor() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		return "useEditor";
	}

	/**
	 * The method that initializes all for the simple mode
	 */
    
	public String doUseSimple() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		return "success";
	}

	/**
	 * Gets the list of defined assetKeys.
	 */
	
	public List getDefinedAssetKeys()
	{
		return ContentTypeDefinitionController.getController().getDefinedAssetKeys(this.contentTypeDefinitionVO.getSchemaValue());
	}

    
	public String doInsertAttribute() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		String newSchemaValue = ContentTypeDefinitionController.getController().insertContentTypeAttribute(this.contentTypeDefinitionVO.getSchemaValue(), this.inputTypeId);
		this.contentTypeDefinitionVO.setSchemaValue(newSchemaValue);
		ContentTypeDefinitionController.getController().update(this.contentTypeDefinitionVO);		
				
		this.initialize(getContentTypeDefinitionId());
		return "useEditor";
	}
	
	
	public String doDeleteAttribute() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		
		try
		{
			String contentTypeDefinitionString = this.contentTypeDefinitionVO.getSchemaValue();
			InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));
			
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();
			
			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				element.getParentNode().removeChild(element);
			}
			
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
			this.contentTypeDefinitionVO.setSchemaValue(sb.toString());
			
			ContentTypeDefinitionController.getController().update(contentTypeDefinitionVO);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.initialize(getContentTypeDefinitionId());
		return "useEditor";
	}

	/**
	 * This method moves an content type attribute up one step.
	 */
	
	public String doMoveAttributeUp() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		
		try
		{
			String contentTypeDefinitionString = this.contentTypeDefinitionVO.getSchemaValue();
			InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));
			
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();
			
			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			Element previousElement = null;
			for(int i=0; i < anl.getLength(); i++)
			{
				Element element = (Element)anl.item(i);
				if(element.getAttribute("name").equalsIgnoreCase(this.attributeName) && previousElement != null)
				{
					Element parent = (Element)element.getParentNode();
					parent.removeChild(element);
					parent.insertBefore(element, previousElement);
				}
				previousElement = element;
			}
			
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
			this.contentTypeDefinitionVO.setSchemaValue(sb.toString());
			
			ContentTypeDefinitionController.getController().update(contentTypeDefinitionVO);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.initialize(getContentTypeDefinitionId());
		return "useEditor";
	}


	/**
	 * This method moves an content type attribute down one step.
	 */
	
	public String doMoveAttributeDown() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		
		try
		{
			String contentTypeDefinitionString = this.contentTypeDefinitionVO.getSchemaValue();
			InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));
			
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();
			
			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			Element parent = null;
			Element elementToMove = null;
			boolean isInserted = false;
			int position = 0;
			for(int i=0; i < anl.getLength(); i++)
			{
				Element element = (Element)anl.item(i);
				parent = (Element)element.getParentNode();
					
				if(elementToMove != null)
				{
					if(position == 2)
					{
						parent.insertBefore(elementToMove, element);
						isInserted = true;
						break;
					}
					else
						position++;
				}
				
				if(element.getAttribute("name").equalsIgnoreCase(this.attributeName))
				{
					elementToMove = element;
					parent.removeChild(elementToMove);
					position++;
				}
			}
			
			if(!isInserted && elementToMove != null)
				parent.appendChild(elementToMove);
				
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
			this.contentTypeDefinitionVO.setSchemaValue(sb.toString());
			
			ContentTypeDefinitionController.getController().update(contentTypeDefinitionVO);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.initialize(getContentTypeDefinitionId());
		return "useEditor";
	}

	
	public String doDeleteAttributeParameterValue() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		
		try
		{
			String contentTypeDefinitionString = this.contentTypeDefinitionVO.getSchemaValue();
			InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));
			
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();
			
			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']/xs:annotation/xs:appinfo/params/param[@id='" + this.attributeParameterId +"']/values/value[@id='" + this.attributeParameterValueId + "']";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				element.getParentNode().removeChild(element);
			}
			
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
			this.contentTypeDefinitionVO.setSchemaValue(sb.toString());
			
			ContentTypeDefinitionController.getController().update(contentTypeDefinitionVO);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.initialize(getContentTypeDefinitionId());
		return "useEditor";
	}
	

	public String doDeleteAssetKey() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		
		try
		{
			String contentTypeDefinitionString = this.contentTypeDefinitionVO.getSchemaValue();
			InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));
			
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();
			
			String attributesXPath = "/xs:schema/xs:simpleType[@name = 'assetKeys']/xs:restriction/xs:enumeration[@value='" + this.assetKey + "']";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				element.getParentNode().removeChild(element);
			}
			
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
			this.contentTypeDefinitionVO.setSchemaValue(sb.toString());
			
			ContentTypeDefinitionController.getController().update(contentTypeDefinitionVO);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.initialize(getContentTypeDefinitionId());
		return "useEditor";
	}


	public String doInsertAttributeParameterValue() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		
		try
		{
			String contentTypeDefinitionString = this.contentTypeDefinitionVO.getSchemaValue();
			InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));
			
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();
			
			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']/xs:annotation/xs:appinfo/params/param[@id='" + this.attributeParameterId +"']/values";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				Element newValue = document.createElement("value");
				newValue.setAttribute("id", "undefined" + (int)(Math.random() * 100));
				newValue.setAttribute("label", "undefined" + (int)(Math.random() * 100));
				element.appendChild(newValue);
			}
			
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
			this.contentTypeDefinitionVO.setSchemaValue(sb.toString());
			
			ContentTypeDefinitionController.getController().update(contentTypeDefinitionVO);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.initialize(getContentTypeDefinitionId());
		return "useEditor";
	}

	/**
	 * We validate that ' ', '.', ''', '"' is not used in the attribute name as that will break the javascripts later. 
	 */

	public String doUpdateAttribute() throws Exception 
	{
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
		if(this.newAttributeName.indexOf(" ") > -1 || this.newAttributeName.indexOf(".") > -1 || this.newAttributeName.indexOf("'") > -1  || this.newAttributeName.indexOf("\"") > -1)
		{
			ceb.add(new ConstraintException("ContentTypeAttribute.updateAction", "3500"));
		}
		
		ceb.throwIfNotEmpty();
		
		
		this.initialize(getContentTypeDefinitionId());
		
		try
		{
			String contentTypeDefinitionString = this.contentTypeDefinitionVO.getSchemaValue();
			InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));
			
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();
			
			//Updating the content attribute
			String[] extraParameterNames = getRequest().getParameterValues("parameterNames");
			if(extraParameterNames != null)
			{
				for(int i=0; i < extraParameterNames.length; i++)
				{
					String extraParameterName = extraParameterNames[i];
					String value = getRequest().getParameter(extraParameterName);
			
					String extraParametersXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']/xs:annotation/xs:appinfo/params/param[@id='" + extraParameterName +"']/values/value";
					NodeList extraParamsNodeList = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), extraParametersXPath);
					if(extraParamsNodeList != null && extraParamsNodeList.getLength() > 0)
					{
						Element element = (Element)extraParamsNodeList.item(0);
						
						if(extraParameterName.equalsIgnoreCase("values") && (this.inputTypeId.equalsIgnoreCase("select") || this.inputTypeId.equalsIgnoreCase("checkbox") || this.inputTypeId.equalsIgnoreCase("radiobutton")))
						{
							((Element)element.getParentNode().getParentNode()).setAttribute("inputTypeId", "1");							
						}
						else
						{
							((Element)element.getParentNode().getParentNode()).setAttribute("inputTypeId", "0");
						}
						
						if(((Element)element.getParentNode().getParentNode()).getAttribute("inputTypeId").equals("0"))
						{
							if(this.currentContentTypeEditorViewLanguageCode != null && this.currentContentTypeEditorViewLanguageCode.length() > 0)
							{
								element.setAttribute("label_" + this.currentContentTypeEditorViewLanguageCode, value);
							}
							else
							{
								element.setAttribute("label", value);
							}
						}
					}
				}
			}

			//Updating the name and type
			String attributeXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributeXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				element.setAttribute("name", this.newAttributeName);
				element.setAttribute("type", this.inputTypeId);
			}

		
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
			this.contentTypeDefinitionVO.setSchemaValue(sb.toString());
			
			ContentTypeDefinitionController.getController().update(contentTypeDefinitionVO);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.initialize(getContentTypeDefinitionId());
		
		return "useEditor";
	}


	public String doUpdateAttributeParameterValue() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		
		try
		{
			String contentTypeDefinitionString = this.contentTypeDefinitionVO.getSchemaValue();
			InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));
			
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();
			
			String parameterValueXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']/xs:annotation/xs:appinfo/params/param[@id='" + this.attributeParameterId +"']/values/value[@id='" + this.attributeParameterValueId + "']";
			NodeList parameterValuesNodeList = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), parameterValueXPath);
			if(parameterValuesNodeList != null && parameterValuesNodeList.getLength() > 0)
			{
				Element element = (Element)parameterValuesNodeList.item(0);
				element.setAttribute("id", this.newAttributeParameterValueId);
				
				if(this.currentContentTypeEditorViewLanguageCode != null && this.currentContentTypeEditorViewLanguageCode.length() > 0)
					element.setAttribute("label_" + this.currentContentTypeEditorViewLanguageCode, this.attributeParameterValueLabel);
				else
					element.setAttribute("label", this.attributeParameterValueLabel);
			}
		
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
			this.contentTypeDefinitionVO.setSchemaValue(sb.toString());
			
			ContentTypeDefinitionController.getController().update(contentTypeDefinitionVO);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.initialize(getContentTypeDefinitionId());
		return "useEditor";
	}


	public String doInsertAssetKey() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		
		try
		{
			String contentTypeDefinitionString = this.contentTypeDefinitionVO.getSchemaValue();
			InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));
			
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();
			
			String assetKeysXPath = "/xs:schema/xs:simpleType[@name = 'assetKeys']/xs:restriction";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), assetKeysXPath);
			
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				Element assetKey = document.createElement("xs:enumeration");
				assetKey.setAttribute("value", "undefined" + (int)(Math.random() * 100));
				element.appendChild(assetKey);
			}
			else
			{
				//The assetKey type was not defined so we create it first.
				String schemaXPath = "/xs:schema";
				NodeList schemaNL = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), schemaXPath);
				if(schemaNL != null && schemaNL.getLength() > 0)
				{
					Element schemaElement = (Element)schemaNL.item(0);
					
					Element assetKeySimpleType = document.createElement("xs:simpleType");
					assetKeySimpleType.setAttribute("name", "assetKeys");

					Element assetKeyRestriction = document.createElement("xs:restriction");
					assetKeyRestriction.setAttribute("base", "xs:string");

					Element assetKey = document.createElement("xs:enumeration");
					assetKey.setAttribute("value", "undefined" + (int)(Math.random() * 100));
					
					assetKeyRestriction.appendChild(assetKey);
					assetKeySimpleType.appendChild(assetKeyRestriction);
					schemaElement.appendChild(assetKeySimpleType);
				}		
			}
			
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
			this.contentTypeDefinitionVO.setSchemaValue(sb.toString());
			
			ContentTypeDefinitionController.getController().update(contentTypeDefinitionVO);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.initialize(getContentTypeDefinitionId());
		return "useEditor";
	}


	public String doUpdateAssetKey() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		
		try
		{
			String contentTypeDefinitionString = this.contentTypeDefinitionVO.getSchemaValue();
			InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));
			
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();
			
			String attributesXPath = "/xs:schema/xs:simpleType[@name = 'assetKeys']/xs:restriction/xs:enumeration[@value='" + this.assetKey + "']";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				element.setAttribute("value", this.newAssetKey);
			}
			
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
			this.contentTypeDefinitionVO.setSchemaValue(sb.toString());
			
			ContentTypeDefinitionController.getController().update(contentTypeDefinitionVO);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.initialize(getContentTypeDefinitionId());
		return "useEditor";
	}

    public java.lang.Integer getContentTypeDefinitionId()
    {
        return this.contentTypeDefinitionVO.getContentTypeDefinitionId();
    }
        
    public void setContentTypeDefinitionId(java.lang.Integer contentTypeDefinitionId) throws Exception
    {
        this.contentTypeDefinitionVO.setContentTypeDefinitionId(contentTypeDefinitionId);
    }

	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

    public java.lang.String getName()
    {
        return this.contentTypeDefinitionVO.getName();
    }

    public java.lang.String getSchemaValue()
    {
        return this.contentTypeDefinitionVO.getSchemaValue();
    }

	public java.lang.Integer getType()
	{
		return this.contentTypeDefinitionVO.getType();
	}
	
	/**
	 * This method returns the attributes in the content type definition for generation.
	 */
	
	public List getContentTypeAttributes()
	{
		return this.attributes;
	}
	
	public String getInputTypeId()
	{
		return inputTypeId;
	}

	public void setInputTypeId(String string)
	{
		inputTypeId = string;
	}

	public String getAttributeName()
	{
		return this.attributeName;
	}

	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}

	public void setNewAttributeName(String newAttributeName)
	{
		this.newAttributeName = newAttributeName;
	}

	public String getAttributeParameterValueId()
	{
		return attributeParameterValueId;
	}

	public void setAttributeParameterValueId(String string)
	{
		attributeParameterValueId = string;
	}

	public String getAttributeParameterId()
	{
		return attributeParameterId;
	}

	public void setAttributeParameterId(String string)
	{
		attributeParameterId = string;
	}

	public String getAttributeParameterValueLabel()
	{
		return attributeParameterValueLabel;
	}

	public String getNewAttributeParameterValueId()
	{
		return newAttributeParameterValueId;
	}

	public void setAttributeParameterValueLabel(String string)
	{
		attributeParameterValueLabel = string;
	}

	public void setNewAttributeParameterValueId(String string)
	{
		newAttributeParameterValueId = string;
	}

	public String getAttributeParameterValueLocale()
	{
		return attributeParameterValueLocale;
	}

	public void setAttributeParameterValueLocale(String string)
	{
		attributeParameterValueLocale = string;
	}

	public String getAttributeToExpand()
	{
		return attributeToExpand;
	}

	public void setAttributeToExpand(String string)
	{
		attributeToExpand = string;
	}

	public String getCurrentContentTypeEditorViewLanguageCode()
	{
		return currentContentTypeEditorViewLanguageCode;
	}

	public void setCurrentContentTypeEditorViewLanguageCode(String string)
	{
		currentContentTypeEditorViewLanguageCode = string;
	}

	public List getAvailableLanguages()
	{
		return availableLanguages;
	}

	public String getAssetKey()
	{
		return assetKey;
	}

	public void setAssetKey(String assetKey)
	{
		this.assetKey = assetKey;
	}

	public void setNewAssetKey(String newAssetKey)
	{
		this.newAssetKey = newAssetKey;
	}

	public String getErrorKey()
	{
		return "ContentTypeAttribute.updateAction";
	}
	
	public String getReturnAddress()
	{
		return "ViewListContentTypeDefinition.action";
	}

}
