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

package org.infoglue.cms.controllers.kernel.impl.simple;

import org.infoglue.cms.entities.kernel.*;
import org.infoglue.cms.entities.management.ContentTypeAttribute;
import org.infoglue.cms.entities.management.ContentTypeAttributeParameter;
import org.infoglue.cms.entities.management.ContentTypeAttributeParameterValue;
import org.infoglue.cms.entities.management.ContentTypeDefinition;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.impl.simple.ContentTypeDefinitionImpl;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.deliver.util.CacheController;
import org.infoglue.cms.util.CmsLogger;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import java.util.List;
import java.util.ArrayList;
import java.io.*;

import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

/**
 * @author ss
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ContentTypeDefinitionController extends BaseController
{
	/**
	 * Factory method
	 */

	public static ContentTypeDefinitionController getController()
	{
		return new ContentTypeDefinitionController();
	}

    public ContentTypeDefinitionVO getContentTypeDefinitionVOWithId(Integer contentTypeDefinitionId) throws SystemException, Bug
    {
		return (ContentTypeDefinitionVO) getVOWithId(ContentTypeDefinitionImpl.class, contentTypeDefinitionId);
    }

	/*
    public static ContentTypeDefinition getContentTypeDefinitionWithId(Integer contentTypeDefinitionId) throws SystemException, Bug
    {
		return (ContentTypeDefinition) getObjectWithId(ContentTypeDefinitionImpl.class, contentTypeDefinitionId);
    }
	*/

    public ContentTypeDefinition getContentTypeDefinitionWithId(Integer contentTypeDefinitionId, Database db) throws SystemException, Bug
    {
		return (ContentTypeDefinition) getObjectWithId(ContentTypeDefinitionImpl.class, contentTypeDefinitionId, db);
    }

    public List getContentTypeDefinitionVOList() throws SystemException, Bug
    {
		String key = "contentTypeDefinitionVOList";
		CmsLogger.logInfo("key:" + key);
		List cachedContentTypeDefinitionVOList = (List)CacheController.getCachedObject("contentTypeDefinitionCache", key);
		if(cachedContentTypeDefinitionVOList != null)
		{
			CmsLogger.logInfo("There was an cached contentTypeDefinitionVOList:" + cachedContentTypeDefinitionVOList.size());
			return cachedContentTypeDefinitionVOList;
		}

		List contentTypeDefinitionVOList = getAllVOObjects(ContentTypeDefinitionImpl.class, "contentTypeDefinitionId");

		CacheController.cacheObject("contentTypeDefinitionCache", key, contentTypeDefinitionVOList);

		return contentTypeDefinitionVOList;
    }

	public List getContentTypeDefinitionVOList(Database db) throws SystemException, Bug
	{
		return getAllVOObjects(ContentTypeDefinitionImpl.class, "contentTypeDefinitionId", db);
	}

	/**
	 * Returns the Content Type Definition with the given name.
	 *
	 * @param name
	 * @return
	 * @throws SystemException
	 * @throws Bug
	 */

	public ContentTypeDefinitionVO getContentTypeDefinitionVOWithName(String name) throws SystemException, Bug
	{
		ContentTypeDefinitionVO contentTypeDefinitionVO = null;

		Database db = CastorDatabaseService.getDatabase();

		try
		{
			beginTransaction(db);

			ContentTypeDefinition contentTypeDefinition = getContentTypeDefinitionWithName(name, db);
			if(contentTypeDefinition != null)
				contentTypeDefinitionVO = contentTypeDefinition.getValueObject();

			commitTransaction(db);
		}
		catch (Exception e)
		{
			CmsLogger.logInfo("An error occurred so we should not complete the transaction:" + e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}

		return contentTypeDefinitionVO;
	}

	/**
	 * Returns the Content Type Definition with the given name fetched within a given transaction.
	 *
	 * @param name
	 * @param db
	 * @return
	 * @throws SystemException
	 * @throws Bug
	 */

	public ContentTypeDefinition getContentTypeDefinitionWithName(String name, Database db) throws SystemException, Bug
	{
		ContentTypeDefinition contentTypeDefinition = null;

		try
		{
			OQLQuery oql = db.getOQLQuery("SELECT f FROM org.infoglue.cms.entities.management.impl.simple.ContentTypeDefinitionImpl f WHERE f.name = $1");
			oql.bind(name);

			QueryResults results = oql.execute();
			if (results.hasMore())
			{
				contentTypeDefinition = (ContentTypeDefinition)results.next();
			}
		}
		catch(Exception e)
		{
			throw new SystemException("An error occurred when we tried to fetch a named ContentTypeDefinition. Reason:" + e.getMessage(), e);
		}

		return contentTypeDefinition;
	}

	public List getContentTypeDefinitionVOList(Integer type) throws SystemException, Bug
	{
		List contentTypeDefinitionVOList = null;
		Database db = CastorDatabaseService.getDatabase();

		try
		{
			beginTransaction(db);

			contentTypeDefinitionVOList = getContentTypeDefinitionVOList(type, db);

			commitTransaction(db);
		}
		catch (Exception e)
		{
			CmsLogger.logInfo("An error occurred so we should not complete the transaction:" + e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
		return contentTypeDefinitionVOList;
	}

	public List getContentTypeDefinitionVOList(Integer type, Database db)  throws SystemException, Bug
	{
		ArrayList contentTypeDefinitionVOList = new ArrayList();
		try
		{
			OQLQuery oql = db.getOQLQuery("SELECT f FROM org.infoglue.cms.entities.management.impl.simple.ContentTypeDefinitionImpl f WHERE f.type = $1");
			oql.bind(type);

			QueryResults results = oql.execute();
			while (results.hasMore())
			{
				ContentTypeDefinition contentTypeDefinition = (ContentTypeDefinition)results.next();
				contentTypeDefinitionVOList.add(contentTypeDefinition.getValueObject());
			}
		}
		catch(Exception e)
		{
			throw new SystemException("An error occurred when we tried to fetch a list of Function. Reason:" + e.getMessage(), e);
		}

		return contentTypeDefinitionVOList;
	}

    public ContentTypeDefinitionVO create(ContentTypeDefinitionVO contentTypeDefinitionVO) throws ConstraintException, SystemException
    {
        ContentTypeDefinition contentTypeDefinition = new ContentTypeDefinitionImpl();
        contentTypeDefinition.setValueObject(contentTypeDefinitionVO);
        contentTypeDefinition = (ContentTypeDefinition) createEntity(contentTypeDefinition);
        return contentTypeDefinition.getValueObject();
    }

    public void delete(ContentTypeDefinitionVO contentTypeDefinitionVO) throws ConstraintException, SystemException
    {
    	deleteEntity(ContentTypeDefinitionImpl.class, contentTypeDefinitionVO.getContentTypeDefinitionId());
    }

    public ContentTypeDefinitionVO update(ContentTypeDefinitionVO contentTypeDefinitionVO) throws ConstraintException, SystemException
    {
    	return (ContentTypeDefinitionVO) updateEntity(ContentTypeDefinitionImpl.class, contentTypeDefinitionVO);
    }


	/**
	 * This method fetches any predefined assetKeys from a xml-string representing a contentTypeDefinition.
	 */

	public List getDefinedAssetKeys(String contentTypeDefinitionString)
	{
		List keys = new ArrayList();

        try
        {
        	if(contentTypeDefinitionString != null)
        	{
		        InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));

				DOMParser parser = new DOMParser();
				parser.parse(xmlSource);
				Document document = parser.getDocument();

			    String attributesXPath = "/xs:schema/xs:simpleType";

				// Get assetKeys node
				NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
				for(int i=0; i < anl.getLength(); i++)
				{
					Node child = anl.item(i);
					Node attributeName = child.getAttributes().getNamedItem("name");
					if (attributeName.getNodeValue().compareTo("assetKeys") == 0)
					{
						NodeList ianl = org.apache.xpath.XPathAPI.selectNodeList(child, "xs:restriction/xs:enumeration");
						for(int ii=0; ii < ianl.getLength(); ii++)
						{
							Node ichild = ianl.item(ii);
							Node attributeValue = ichild.getAttributes().getNamedItem("value");
							keys.add(attributeValue.getNodeValue());
						}
					}
				}
        	}
        }
        catch(Exception e)
        {
        	CmsLogger.logWarning("An error occurred when trying to fetch the asset keys:" + e.getMessage(), e);
        }

		return keys;
	}


	/**
	 * This method returns the attributes in the content type definition for generation.
	 */

	public List getContentTypeAttributes(String schemaValue)
	{
		List attributes = new ArrayList();

		try
		{
			InputSource xmlSource = new InputSource(new StringReader(schemaValue));

			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			for(int i=0; i < anl.getLength(); i++)
			{
				Element child = (Element)anl.item(i);
				String attributeName = child.getAttribute("name");
				String attributeType = child.getAttribute("type");

				ContentTypeAttribute contentTypeAttribute = new ContentTypeAttribute();
				contentTypeAttribute.setPosition(i);
				contentTypeAttribute.setName(attributeName);
				contentTypeAttribute.setInputType(attributeType);

				// Get extra parameters
				Node paramsNode = org.apache.xpath.XPathAPI.selectSingleNode(child, "xs:annotation/xs:appinfo/params");
				if (paramsNode != null)
				{
					NodeList childnl = ((Element)paramsNode).getElementsByTagName("param");
					for(int ci=0; ci < childnl.getLength(); ci++)
					{
						Element param = (Element)childnl.item(ci);
						String paramId = param.getAttribute("id");
						String paramInputTypeId = param.getAttribute("inputTypeId");

						ContentTypeAttributeParameter contentTypeAttributeParameter = new ContentTypeAttributeParameter();
						contentTypeAttributeParameter.setId(paramId);
						if(paramInputTypeId != null && paramInputTypeId.length() > 0)
							contentTypeAttributeParameter.setType(Integer.parseInt(paramInputTypeId));

						contentTypeAttribute.putContentTypeAttributeParameter(paramId, contentTypeAttributeParameter);

						NodeList valuesNodeList = param.getElementsByTagName("values");
						for(int vsnli=0; vsnli < valuesNodeList.getLength(); vsnli++)
						{
							NodeList valueNodeList = param.getElementsByTagName("value");
							for(int vnli=0; vnli < valueNodeList.getLength(); vnli++)
							{
								Element value = (Element)valueNodeList.item(vnli);
								String valueId = value.getAttribute("id");

								ContentTypeAttributeParameterValue contentTypeAttributeParameterValue = new ContentTypeAttributeParameterValue();
								contentTypeAttributeParameterValue.setId(valueId);

								NamedNodeMap nodeMap = value.getAttributes();
								for(int nmi =0; nmi < nodeMap.getLength(); nmi++)
								{
									Node attribute = nodeMap.item(nmi);
									String valueAttributeName = attribute.getNodeName();
									String valueAttributeValue = attribute.getNodeValue();
									contentTypeAttributeParameterValue.addAttribute(valueAttributeName, valueAttributeValue);
								}

								contentTypeAttributeParameter.addContentTypeAttributeParameterValue(valueId, contentTypeAttributeParameterValue);
							}
						}
					}
				}
				// End extra parameters

				attributes.add(contentTypeAttribute);
			}

		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred when we tried to get the attributes of the content type: " + e.getMessage(), e);
		}

		return attributes;
	}

	/**
	 * This method adds a new content type attribute to the contentTypeDefinition. It sets some default values.
	 */

	public String insertContentTypeAttribute(String schemaValue, String inputTypeId)
	{
		String newSchemaValue = schemaValue;

		try
		{
			InputSource xmlSource = new InputSource(new StringReader(schemaValue));

			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();

			//Build the entire structure for a contenttype...

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			for(int i=0; i < anl.getLength(); i++)
			{
				Node child = anl.item(i);
				Element childElement = (Element)child;
				Element newAttribute = document.createElement("xs:element");
				newAttribute.setAttribute("name", "newAttributeName" + (int)(Math.random() * 100));
				newAttribute.setAttribute("type", inputTypeId);
				childElement.appendChild(newAttribute);

				Element annotation = document.createElement("xs:annotation");
				Element appInfo    = document.createElement("xs:appinfo");
				Element params     = document.createElement("params");

				addParameterElement(params, "title", "0");
				addParameterElement(params, "description", "0");
				addParameterElement(params, "class", "0");

				newAttribute.appendChild(annotation);
				annotation.appendChild(appInfo);
				appInfo.appendChild(params);

				if(inputTypeId.equalsIgnoreCase("checkbox") || inputTypeId.equalsIgnoreCase("select") || inputTypeId.equalsIgnoreCase("radiobutton"))
				{
					addParameterElement(params, "values", "1");
				}

				if(inputTypeId.equalsIgnoreCase("textarea"))
				{
					addParameterElement(params, "width", "0", "700");
					addParameterElement(params, "height", "0", "150");
					addParameterElement(params, "enableWYSIWYG", "0", "false");
					addParameterElement(params, "enableTemplateEditor", "0", "false");
					addParameterElement(params, "enableFormEditor", "0", "false");
					addParameterElement(params, "enableContentRelationEditor", "0", "false");
					addParameterElement(params, "enableStructureRelationEditor", "0", "false");
					addParameterElement(params, "activateExtendedEditorOnLoad", "0", "false");
				}
			}

			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
			newSchemaValue = sb.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return newSchemaValue;
	}


	/**
	 * This method creates a parameter for the given input type.
	 * This is to support form steering information later.
	 */

	private void addParameterElement(Element parent, String id, String inputTypeId)
	{
		Element parameter = parent.getOwnerDocument().createElement("param");
		parameter.setAttribute("id", id);
		parameter.setAttribute("inputTypeId", inputTypeId);  //Multiple values
		Element parameterValuesValues = parent.getOwnerDocument().createElement("values");
		Element parameterValuesValue  = parent.getOwnerDocument().createElement("value");
		parameterValuesValue.setAttribute("id", "undefined" + (int)(Math.random() * 100));
		parameterValuesValue.setAttribute("label", "undefined" + (int)(Math.random() * 100));
		parameterValuesValues.appendChild(parameterValuesValue);
		parameter.appendChild(parameterValuesValues);
		parent.appendChild(parameter);
	}


	/**
	 * This method creates a parameter for the given input type and the default value.
	 * This is to support form steering information later.
	 */

	private void addParameterElement(Element parent, String id, String inputTypeId, String defaultValue)
	{
		Element parameter = parent.getOwnerDocument().createElement("param");
		parameter.setAttribute("id", id);
		parameter.setAttribute("inputTypeId", inputTypeId);  //Multiple values
		Element parameterValuesValues = parent.getOwnerDocument().createElement("values");
		Element parameterValuesValue  = parent.getOwnerDocument().createElement("value");
		parameterValuesValue.setAttribute("id", id);
		parameterValuesValue.setAttribute("label", defaultValue);
		parameterValuesValues.appendChild(parameterValuesValue);
		parameter.appendChild(parameterValuesValues);
		parent.appendChild(parameter);
	}


	/**
	 * This method creates a parameter for the given input type and the default value.
	 * This is to support form steering information later.
	 */

	private void addParameterElementIfNotExists(Element parent, String id, String inputTypeId, String defaultValue) throws Exception
	{
		String parameterXPath = "param[@id='" + id + "']";
		NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(parent, parameterXPath);
		if(anl.getLength() == 0)
		{
			Element parameter = parent.getOwnerDocument().createElement("param");
			parameter.setAttribute("id", id);
			parameter.setAttribute("inputTypeId", inputTypeId);  //Multiple values
			Element parameterValuesValues = parent.getOwnerDocument().createElement("values");
			Element parameterValuesValue  = parent.getOwnerDocument().createElement("value");
			parameterValuesValue.setAttribute("id", id);
			parameterValuesValue.setAttribute("label", defaultValue);
			parameterValuesValues.appendChild(parameterValuesValue);
			parameter.appendChild(parameterValuesValues);
			parent.appendChild(parameter);

		}
	}

	/**
	 * This method validates the current content type and updates it to be valid in the future.
	 */

	public ContentTypeDefinitionVO validateAndUpdateContentType(ContentTypeDefinitionVO contentTypeDefinitionVO)
	{
		try
		{
			boolean isModified = false;

			InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionVO.getSchemaValue()));
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();

			//Set the new versionId
			String rootXPath = "/xs:schema";
			NodeList schemaList = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), rootXPath);
			for(int i=0; i < schemaList.getLength(); i++)
			{
				Element schemaElement = (Element)schemaList.item(i);
				if(schemaElement.getAttribute("version") == null || schemaElement.getAttribute("version").equalsIgnoreCase(""))
				{
					isModified = true;
					schemaElement.setAttribute("version", "2.0");

					//First check out if the old/wrong definitions are there and delete them
					String definitionsXPath = "/xs:schema/xs:simpleType";
					NodeList definitionList = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), definitionsXPath);
					for(int j=0; j < definitionList.getLength(); j++)
					{
						Element childElement = (Element)definitionList.item(j);
						if(!childElement.getAttribute("name").equalsIgnoreCase("assetKeys"))
							childElement.getParentNode().removeChild(childElement);
					}

					//Now we create the new definitions
					Element textFieldDefinition = document.createElement("xs:simpleType");
					textFieldDefinition.setAttribute("name", "textfield");
					Element restriction = document.createElement("xs:restriction");
					restriction.setAttribute("base", "xs:string");
					Element maxLength = document.createElement("xs:maxLength");
					maxLength.setAttribute("value", "100");
					restriction.appendChild(maxLength);
					textFieldDefinition.appendChild(restriction);
					schemaElement.insertBefore(textFieldDefinition, schemaElement.getFirstChild());

					Element selectDefinition = document.createElement("xs:simpleType");
					selectDefinition.setAttribute("name", "select");
					restriction = document.createElement("xs:restriction");
					restriction.setAttribute("base", "xs:string");
					maxLength = document.createElement("xs:maxLength");
					maxLength.setAttribute("value", "100");
					restriction.appendChild(maxLength);
					selectDefinition.appendChild(restriction);
					schemaElement.insertBefore(selectDefinition, schemaElement.getFirstChild());

					Element checkboxDefinition = document.createElement("xs:simpleType");
					checkboxDefinition.setAttribute("name", "checkbox");
					restriction = document.createElement("xs:restriction");
					restriction.setAttribute("base", "xs:string");
					maxLength = document.createElement("xs:maxLength");
					maxLength.setAttribute("value", "100");
					restriction.appendChild(maxLength);
					checkboxDefinition.appendChild(restriction);
					schemaElement.insertBefore(checkboxDefinition, schemaElement.getFirstChild());

					Element radiobuttonDefinition = document.createElement("xs:simpleType");
					radiobuttonDefinition.setAttribute("name", "radiobutton");
					restriction = document.createElement("xs:restriction");
					restriction.setAttribute("base", "xs:string");
					maxLength = document.createElement("xs:maxLength");
					maxLength.setAttribute("value", "100");
					restriction.appendChild(maxLength);
					radiobuttonDefinition.appendChild(restriction);
					schemaElement.insertBefore(radiobuttonDefinition, schemaElement.getFirstChild());

					Element textareaDefinition = document.createElement("xs:simpleType");
					textareaDefinition.setAttribute("name", "textarea");
					restriction = document.createElement("xs:restriction");
					restriction.setAttribute("base", "xs:string");
					maxLength = document.createElement("xs:maxLength");
					maxLength.setAttribute("value", "100");
					restriction.appendChild(maxLength);
					textareaDefinition.appendChild(restriction);
					schemaElement.insertBefore(textareaDefinition, schemaElement.getFirstChild());


					//Now we deal with the individual attributes and parameters
					String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element";
					NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
					for(int k=0; k < anl.getLength(); k++)
					{
						Element childElement = (Element)anl.item(k);

						if(childElement.getAttribute("type").equals("shortString"))
						{
							childElement.setAttribute("type", "textfield");
							isModified = true;
						}
						else if(childElement.getAttribute("type").equals("shortText"))
						{
							childElement.setAttribute("type", "textarea");
							isModified = true;
						}
						else if(childElement.getAttribute("type").equals("fullText"))
						{
							childElement.setAttribute("type", "textarea");
							isModified = true;
						}
						else if(childElement.getAttribute("type").equals("hugeText"))
						{
							childElement.setAttribute("type", "textarea");
							isModified = true;
						}

						String inputTypeId = childElement.getAttribute("type");

						NodeList annotationNodeList = childElement.getElementsByTagName("xs:annotation");
						if(annotationNodeList != null && annotationNodeList.getLength() > 0)
						{
							Element annotationElement = (Element)annotationNodeList.item(0);
							NodeList appinfoNodeList = childElement.getElementsByTagName("xs:appinfo");
							if(appinfoNodeList != null && appinfoNodeList.getLength() > 0)
							{
								Element appinfoElement = (Element)appinfoNodeList.item(0);
								NodeList paramsNodeList = childElement.getElementsByTagName("params");
								if(paramsNodeList != null && paramsNodeList.getLength() > 0)
								{
									Element paramsElement = (Element)paramsNodeList.item(0);
									addParameterElement(paramsElement, "title", "0");
									addParameterElement(paramsElement, "description", "0");
									addParameterElement(paramsElement, "class", "0");

									if(inputTypeId.equalsIgnoreCase("checkbox") || inputTypeId.equalsIgnoreCase("select") || inputTypeId.equalsIgnoreCase("radiobutton"))
									{
										addParameterElement(paramsElement, "values", "1");
									}

									if(inputTypeId.equalsIgnoreCase("textarea"))
									{
										addParameterElement(paramsElement, "width", "0", "700");
										addParameterElement(paramsElement, "height", "0", "150");
										addParameterElement(paramsElement, "enableWYSIWYG", "0", "false");
										addParameterElement(paramsElement, "enableTemplateEditor", "0", "false");
										addParameterElement(paramsElement, "enableFormEditor", "0", "false");
										addParameterElement(paramsElement, "enableContentRelationEditor", "0", "false");
										addParameterElement(paramsElement, "enableStructureRelationEditor", "0", "false");
										addParameterElement(paramsElement, "activateExtendedEditorOnLoad", "0", "false");
									}
								}
								else
								{
									Element paramsElement = document.createElement("params");

									addParameterElement(paramsElement, "title", "0");
									addParameterElement(paramsElement, "description", "0");
									addParameterElement(paramsElement, "class", "0");

									if(inputTypeId.equalsIgnoreCase("checkbox") || inputTypeId.equalsIgnoreCase("select") || inputTypeId.equalsIgnoreCase("radiobutton"))
									{
										addParameterElement(paramsElement, "values", "1");
									}

									if(inputTypeId.equalsIgnoreCase("textarea"))
									{
										addParameterElement(paramsElement, "width", "0", "700");
										addParameterElement(paramsElement, "height", "0", "150");
										addParameterElement(paramsElement, "enableWYSIWYG", "0", "false");
										addParameterElement(paramsElement, "enableTemplateEditor", "0", "false");
										addParameterElement(paramsElement, "enableFormEditor", "0", "false");
										addParameterElement(paramsElement, "enableContentRelationEditor", "0", "false");
										addParameterElement(paramsElement, "enableStructureRelationEditor", "0", "false");
										addParameterElement(paramsElement, "activateExtendedEditorOnLoad", "0", "false");
									}

									appinfoElement.appendChild(paramsElement);
									isModified = true;
								}
							}
							else
							{
								Element appInfo    	  = document.createElement("xs:appinfo");
								Element paramsElement = document.createElement("params");

								addParameterElement(paramsElement, "title", "0");
								addParameterElement(paramsElement, "description", "0");
								addParameterElement(paramsElement, "class", "0");

								if(inputTypeId.equalsIgnoreCase("checkbox") || inputTypeId.equalsIgnoreCase("select") || inputTypeId.equalsIgnoreCase("radiobutton"))
								{
									addParameterElement(paramsElement, "values", "1");
								}

								if(inputTypeId.equalsIgnoreCase("textarea"))
								{
									addParameterElement(paramsElement, "width", "0", "700");
									addParameterElement(paramsElement, "height", "0", "150");
									addParameterElement(paramsElement, "enableWYSIWYG", "0", "false");
									addParameterElement(paramsElement, "enableTemplateEditor", "0", "false");
									addParameterElement(paramsElement, "enableFormEditor", "0", "false");
									addParameterElement(paramsElement, "enableContentRelationEditor", "0", "false");
									addParameterElement(paramsElement, "enableStructureRelationEditor", "0", "false");
									addParameterElement(paramsElement, "activateExtendedEditorOnLoad", "0", "false");
								}

								annotationElement.appendChild(appInfo);
								appInfo.appendChild(paramsElement);
								isModified = true;
							}
						}
						else
						{
							Element annotation    = document.createElement("xs:annotation");
							Element appInfo       = document.createElement("xs:appinfo");
							Element paramsElement = document.createElement("params");

							addParameterElement(paramsElement, "title", "0");
							addParameterElement(paramsElement, "description", "0");
							addParameterElement(paramsElement, "class", "0");

							if(inputTypeId.equalsIgnoreCase("checkbox") || inputTypeId.equalsIgnoreCase("select") || inputTypeId.equalsIgnoreCase("radiobutton"))
							{
								addParameterElement(paramsElement, "values", "1");
							}

							if(inputTypeId.equalsIgnoreCase("textarea"))
							{
								addParameterElement(paramsElement, "width", "0", "700");
								addParameterElement(paramsElement, "height", "0", "150");
								addParameterElement(paramsElement, "enableWYSIWYG", "0", "false");
								addParameterElement(paramsElement, "enableTemplateEditor", "0", "false");
								addParameterElement(paramsElement, "enableFormEditor", "0", "false");
								addParameterElement(paramsElement, "enableContentRelationEditor", "0", "false");
								addParameterElement(paramsElement, "enableStructureRelationEditor", "0", "false");
								addParameterElement(paramsElement, "activateExtendedEditorOnLoad", "0", "false");
							}

							childElement.appendChild(annotation);
							annotation.appendChild(appInfo);
							appInfo.appendChild(paramsElement);
							isModified = true;
						}

					}
				}
				else if(schemaElement.getAttribute("version") != null && schemaElement.getAttribute("version").equalsIgnoreCase("2.0"))
				{
					isModified = true;
					schemaElement.setAttribute("version", "2.1");

					//Now we deal with the individual attributes and parameters
					String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element";
					NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
					for(int k=0; k < anl.getLength(); k++)
					{
						Element childElement = (Element)anl.item(k);

						String inputTypeId = childElement.getAttribute("type");

						NodeList annotationNodeList = childElement.getElementsByTagName("xs:annotation");
						if(annotationNodeList != null && annotationNodeList.getLength() > 0)
						{
							NodeList appinfoNodeList = childElement.getElementsByTagName("xs:appinfo");
							if(appinfoNodeList != null && appinfoNodeList.getLength() > 0)
							{
								NodeList paramsNodeList = childElement.getElementsByTagName("params");
								if(paramsNodeList != null && paramsNodeList.getLength() > 0)
								{
									Element paramsElement = (Element)paramsNodeList.item(0);

									if(inputTypeId.equalsIgnoreCase("textarea"))
									{
										addParameterElementIfNotExists(paramsElement, "width", "0", "700");
										addParameterElementIfNotExists(paramsElement, "height", "0", "150");
										addParameterElementIfNotExists(paramsElement, "enableWYSIWYG", "0", "false");
										addParameterElementIfNotExists(paramsElement, "enableTemplateEditor", "0", "false");
										addParameterElementIfNotExists(paramsElement, "enableFormEditor", "0", "false");
										addParameterElementIfNotExists(paramsElement, "enableContentRelationEditor", "0", "false");
										addParameterElementIfNotExists(paramsElement, "enableStructureRelationEditor", "0", "false");
										addParameterElementIfNotExists(paramsElement, "activateExtendedEditorOnLoad", "0", "false");
										isModified = true;
									}
								}
							}
						}
					}
				}

			}

			if(isModified)
			{
				StringBuffer sb = new StringBuffer();
				org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
				contentTypeDefinitionVO.setSchemaValue(sb.toString());

				update(contentTypeDefinitionVO);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return contentTypeDefinitionVO;
	}


	/**
	 * This method adds a parameter node with some default values if not allready existing.
	 */

	private boolean addParameterElement(Element parent, String name, String inputTypeId, String value, boolean isAllreadyModified)
	{
		boolean isModified = isAllreadyModified;

		NodeList titleNodeList = parent.getElementsByTagName(name);
		if(titleNodeList != null && titleNodeList.getLength() > 0)
		{
			Element titleElement = (Element)titleNodeList.item(0);
			if(!titleElement.hasChildNodes())
			{
				titleElement.appendChild(parent.getOwnerDocument().createTextNode(value));
				isModified = true;
			}
		}
		else
		{
			Element title = parent.getOwnerDocument().createElement(name);
			title.appendChild(parent.getOwnerDocument().createTextNode(value));
			parent.appendChild(title);
			isModified = true;
		}

		return isModified;
	}

	/**
	 * This is a method that gives the user back an newly initialized ValueObject for this entity that the controller
	 * is handling.
	 */

	public BaseEntityVO getNewVO()
	{
		return new ContentTypeDefinitionVO();
	}

}
