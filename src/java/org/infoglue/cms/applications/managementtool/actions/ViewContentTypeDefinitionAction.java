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
import org.infoglue.cms.controllers.kernel.impl.simple.CategoryController;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.XMLHelper;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.exception.*;

import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.CategoryAttribute;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;

import java.util.List;
import java.util.Iterator;
import java.io.*;

import javax.xml.transform.TransformerException;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class implements the action class for viewContentTypeDefinition.
 * The use-case lets the user see all information about a specific site/contentTypeDefinition.
 *
 * @author Mattias Bogeblad
 */

public class ViewContentTypeDefinitionAction extends InfoGlueAbstractAction
{
	public static final String USE_EDITOR = "useEditor";

	private static CategoryController categoryController = CategoryController.getController();

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
	private String categoryKey;
	private String newCategoryKey;

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
        return USE_EDITOR;
    }

	/**
	 * The method that initializes all for the editor mode
	 */

	public String doUseEditor() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}

	/**
	 * The method that initializes all for the simple mode
	 */

	public String doUseSimple() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		return SUCCESS;
	}

	public String doInsertAttribute() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		String newSchemaValue = ContentTypeDefinitionController.getController().insertContentTypeAttribute(this.contentTypeDefinitionVO.getSchemaValue(), this.inputTypeId);
		this.contentTypeDefinitionVO.setSchemaValue(newSchemaValue);
		ContentTypeDefinitionController.getController().update(this.contentTypeDefinitionVO);

		this.initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}


	public String doDeleteAttribute() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				element.getParentNode().removeChild(element);
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}

	/**
	 * This method moves an content type attribute up one step.
	 */

	public String doMoveAttributeUp() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

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

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}


	/**
	 * This method moves an content type attribute down one step.
	 */

	public String doMoveAttributeDown() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

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

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}

	
	/**
	 * This method moves an content type assetKey up one step.
	 */

	public String doMoveAssetKeyUp() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:simpleType[@name = '" + ContentTypeDefinitionController.ASSET_KEYS + "']/xs:restriction/xs:enumeration[@value='" + this.assetKey + "']";
			NodeList anl = XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				Node parentElement = element.getParentNode();
				Node previuosSibling = element.getPreviousSibling();
				if(previuosSibling != null)
				{
				    parentElement.removeChild(element);
				    parentElement.insertBefore(element, previuosSibling);
				}
			}
						
			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		
		return USE_EDITOR;
	}


	/**
	 * This method moves an content type asset key down one step.
	 */

	public String doMoveAssetKeyDown() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:simpleType[@name = '" + ContentTypeDefinitionController.ASSET_KEYS + "']/xs:restriction/xs:enumeration[@value='" + this.assetKey + "']";
			NodeList anl = XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				Node parentElement = element.getParentNode();
				Node nextSibling = element.getNextSibling();
				if(nextSibling != null)
				{
			        parentElement.removeChild(nextSibling);
			        parentElement.insertBefore(nextSibling, element);
				}
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}


	public String doDeleteAttributeParameterValue() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']/xs:annotation/xs:appinfo/params/param[@id='" + this.attributeParameterId +"']/values/value[@id='" + this.attributeParameterValueId + "']";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				element.getParentNode().removeChild(element);
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}

	public String doInsertAttributeParameterValue() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']/xs:annotation/xs:appinfo/params/param[@id='" + this.attributeParameterId +"']/values";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				Element newValue = document.createElement("value");
				newValue.setAttribute("id", getRandomName());
				newValue.setAttribute("label", getRandomName());
				element.appendChild(newValue);
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
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
			Document document = createDocumentFromDefinition();

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

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());

		return USE_EDITOR;
	}


	public String doUpdateAttributeParameterValue() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

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

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}


	//-------------------------------------------------------------------------------------
	// Methods dealing with extra keys
	//
	// TODO: I think it makes sense to move all methods dealing with ContentTypeDefinition XML
	// TODO: into the ContentTypeDefinitionController, that way only ONE class knows about the
	// TODO: XML structure and we can make adequate tests
	//-------------------------------------------------------------------------------------
	/**
	 * Gets the list of defined assetKeys.
	 */
	public List getDefinedAssetKeys()
	{
		return ContentTypeDefinitionController.getController().getDefinedAssetKeys(contentTypeDefinitionVO.getSchemaValue());
	}

	/**
	 * Gets the list of defined categoryKeys, also populate the category name for the UI.
	 */
	public List getDefinedCategoryKeys() throws Exception
	{
		List categoryKeys = ContentTypeDefinitionController.getController().getDefinedCategoryKeys(contentTypeDefinitionVO.getSchemaValue());
		for (Iterator iter = categoryKeys.iterator(); iter.hasNext();)
		{
			CategoryAttribute info = (CategoryAttribute) iter.next();
			if(info.getCategoryId() != null)
				info.setCategoryName(getCategoryName(info.getCategoryId()));
			else
				info.setCategoryName("Undefined");
		}
		return categoryKeys;
	}

	/**
	 * Return the Category name, if we cannot find the category name (id not an int, bad id, etc)
	 * then do not barf, but return a user friendly name. This can happen if someone removes a
	 * category that is references by a content type definition.
	 */
	public String getCategoryName(Integer id)
	{
		try
		{
			return categoryController.findById(id).getName();
		}
		catch(SystemException e)
		{
			return "Category not found";
		}
	}

	public String doInsertAssetKey() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();
			createNewEnumerationKey(document, ContentTypeDefinitionController.ASSET_KEYS);
			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("Error adding asset key: ", e);
		}

		this.initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}

	public String doInsertCategoryKey() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();
			Element enum = createNewEnumerationKey(document, ContentTypeDefinitionController.CATEGORY_KEYS);

			Element annotation = document.createElement("xs:annotation");
			Element appinfo = document.createElement("xs:appinfo");
			Element params = document.createElement("params");

			enum.appendChild(annotation);
			annotation.appendChild(appinfo);
			appinfo.appendChild(params);
			params.appendChild(createTextElement(document, "title", getRandomName()));
			params.appendChild(createTextElement(document, "description", getRandomName()));
			params.appendChild(createTextElement(document, "categoryId", ""));

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("Error adding categories key: ", e);
		}

		this.initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}



	public String doUpdateAssetKey() throws Exception
	{
		initialize(getContentTypeDefinitionId());
		try
		{
			Document document = createDocumentFromDefinition();
			updateEnumerationKey(document, ContentTypeDefinitionController.ASSET_KEYS, getAssetKey(), getNewAssetKey());
			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("Error updating asset key: ", e);
		}

		initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}

	public String doUpdateCategoryKey() throws Exception
	{
		initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();
			Element enum = updateEnumerationKey(document, ContentTypeDefinitionController.CATEGORY_KEYS, getCategoryKey(), getNewCategoryKey());

			if(enum != null)
			{
				Element title = (Element)XPathAPI.selectSingleNode(enum, "xs:annotation/xs:appinfo/params/title");
				setTextElement(title, getSingleParameter("title"));

				Element description = (Element)XPathAPI.selectSingleNode(enum, "xs:annotation/xs:appinfo/params/description");
				setTextElement(description, getSingleParameter("description"));

				Element categoryId = (Element)XPathAPI.selectSingleNode(enum, "xs:annotation/xs:appinfo/params/categoryId");
				setTextElement(categoryId, getSingleParameter("categoryId"));
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("Error updating category key: ", e);
		}

		initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}

	public String doDeleteAssetKey() throws Exception
	{
		return deleteKey(ContentTypeDefinitionController.ASSET_KEYS, getAssetKey());
	}

	public String doDeleteCategoryKey() throws Exception
	{
		return deleteKey(ContentTypeDefinitionController.CATEGORY_KEYS, getCategoryKey());
	}

	private String deleteKey(String keyType, String key) throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:simpleType[@name = '" + keyType + "']/xs:restriction/xs:enumeration[@value='" + key + "']";
			NodeList anl = XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				element.getParentNode().removeChild(element);
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("Error updating key: " + keyType, e);
		}

		this.initialize(getContentTypeDefinitionId());
		return USE_EDITOR;
	}

	/**
	 * Returns the CategoryController, used by the ContentTypeDefinitionEditor
	 */
	public CategoryController getCategoryController()
	{
		return categoryController;
	}

 	/**
	 * Gets the list of all system categories.
	 */
	public List getAllCategories() throws SystemException
	{
		return getCategoryController().findAllActiveCategories();
	}

	//-------------------------------------------------------------------------------------
	// XML Helper Methods
	//-------------------------------------------------------------------------------------
	/**
	 * Consolidate the Document creation
	 */
	private Document createDocumentFromDefinition() throws SAXException, IOException
	{
		String contentTypeDefinitionString = this.contentTypeDefinitionVO.getSchemaValue();
		InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));
		DOMParser parser = new DOMParser();
		parser.parse(xmlSource);
		return parser.getDocument();
	}

	/**
	 * Consolidate the update of a ContentTypeDefinition Document to the persistence mechanism
	 */
	private void saveUpdatedDefinition(Document document) throws ConstraintException, SystemException
	{
		StringBuffer sb = new StringBuffer();
		XMLHelper.serializeDom(document.getDocumentElement(), sb);
		this.contentTypeDefinitionVO.setSchemaValue(sb.toString());
		ContentTypeDefinitionController.getController().update(contentTypeDefinitionVO);
	}

	/**
	 * Creates an <xs:enumeration> element with the specified key name
	 * @return The Element if child changes are needed, null if the element coudl not be created
	 */
	private Element createNewEnumerationKey(Document document, String keyType) throws TransformerException
	{
		Element enum = null;
		String assetKeysXPath = "/xs:schema/xs:simpleType[@name = '" + keyType + "']/xs:restriction";
		NodeList anl = XPathAPI.selectNodeList(document.getDocumentElement(), assetKeysXPath);

		Element keyRestriction = null;

		if(anl != null && anl.getLength() > 0)
		{
			keyRestriction = (Element)anl.item(0);
		}
		else
		{
			//The key type was not defined so we create it first.
			String schemaXPath = "/xs:schema";
			NodeList schemaNL = XPathAPI.selectNodeList(document.getDocumentElement(), schemaXPath);
			if(schemaNL != null && schemaNL.getLength() > 0)
			{
				Element schemaElement = (Element)schemaNL.item(0);

				Element keySimpleType = document.createElement("xs:simpleType");
				keySimpleType.setAttribute("name", keyType);

				keyRestriction = document.createElement("xs:restriction");
				keyRestriction.setAttribute("base", "xs:string");

				keySimpleType.appendChild(keyRestriction);
				schemaElement.appendChild(keySimpleType);
			}
		}

		enum = document.createElement("xs:enumeration");
		enum.setAttribute("value", getRandomName());
		keyRestriction.appendChild(enum);
		return enum;
	}

	/**
	 * Find an <xs:enumeration> element and update the key value.
	 * @return The Element if child changes are needed, null if the element is not found
	 */
	private Element updateEnumerationKey(Document document, String keyType, String oldKey, String newKey) throws TransformerException
	{
		String attributesXPath = "/xs:schema/xs:simpleType[@name = '" + keyType + "']/xs:restriction/xs:enumeration[@value='" + oldKey + "']";
		NodeList anl = XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
		if(anl != null && anl.getLength() > 0)
		{
			Element enum = (Element)anl.item(0);
			enum.setAttribute("value", newKey);
			return enum;
		}

		return null;
	}

	/**
	 * Creates a new text element
	 */
	private Element createTextElement(Document document, String tagName, String value)
	{
		Element e = document.createElement(tagName);
		e.appendChild(document.createTextNode(value));
		return e;
	}

	/**
	 * Updates the text child of an element, creating it if it needs to.
	 */
	private void setTextElement(Element e, String value)
	{
		if(e.getFirstChild() != null)
			e.getFirstChild().setNodeValue(value);
		else
			 e.appendChild(e.getOwnerDocument().createTextNode(value));
	}


	/**
	 * Generates a random name
	 */
	private String getRandomName()
	{
		return "undefined" + (int)(Math.random() * 100);
	}


	//-------------------------------------------------------------------------------------
	// Attribute Accessors
	//-------------------------------------------------------------------------------------
    public Integer getContentTypeDefinitionId()
    {
        return this.contentTypeDefinitionVO.getContentTypeDefinitionId();
    }

    public void setContentTypeDefinitionId(Integer contentTypeDefinitionId) throws Exception
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

    public String getName()
    {
        return this.contentTypeDefinitionVO.getName();
    }

    public String getSchemaValue()
    {
        return this.contentTypeDefinitionVO.getSchemaValue();
    }

	public Integer getType()
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

	public String getAssetKey()			{ return assetKey; }
	public void setAssetKey(String s)	{ assetKey = s; }

	public String getNewAssetKey()			{ return newAssetKey; }
	public void setNewAssetKey(String s)	{ newAssetKey = s; }

	public String getCategoryKey()			{ return categoryKey; }
	public void setCategoryKey(String s)	{ categoryKey = s; }

	public String getNewCategoryKey()		{ return newCategoryKey; }
	public void setNewCategoryKey(String s)	{ newCategoryKey = s; }

	public String getErrorKey()
	{
		return "ContentTypeAttribute.updateAction";
	}

	public String getReturnAddress()
	{
		return "ViewListContentTypeDefinition.action";
	}

}
