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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;
import org.infoglue.cms.applications.databeans.AssetKeyDefinition;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.CategoryAttribute;
import org.infoglue.cms.entities.management.ContentTypeAttribute;
import org.infoglue.cms.entities.management.ContentTypeAttributeParameter;
import org.infoglue.cms.entities.management.ContentTypeAttributeParameterValue;
import org.infoglue.cms.entities.management.ContentTypeAttributeValidator;
import org.infoglue.cms.entities.management.ContentTypeDefinition;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.impl.simple.ContentTypeDefinitionImpl;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.sorters.ReflectionComparator;
import org.infoglue.deliver.util.NullObject;
import org.infoglue.deliver.util.CacheController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
    private final static Logger logger = Logger.getLogger(ContentTypeDefinitionController.class.getName());

	public static final String ASSET_KEYS = "assetKeys";
	public static final String CATEGORY_KEYS = "categoryKeys";

	private static final NodeList EMPTY_NODELIST = new NodeList()
	{
		public int getLength()	{ return 0; }
		public Node item(int i)	{ return null; }
	};

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
		logger.info("key:" + key);
		List cachedContentTypeDefinitionVOList = (List)CacheController.getCachedObject("contentTypeDefinitionCache", key);
		if(cachedContentTypeDefinitionVOList != null)
		{
			logger.info("There was an cached contentTypeDefinitionVOList:" + cachedContentTypeDefinitionVOList.size());
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

	public List getContentTypeDefinitionList(Database db) throws SystemException, Bug
	{
		return getAllObjects(ContentTypeDefinitionImpl.class, "contentTypeDefinitionId", db);
	}

	/**
	 * This method can be used by actions and use-case-controllers that only need to have simple access to the
	 * functionality. They don't get the transaction-safety but probably just wants to show the info.
	 */	
	
	public List getAuthorizedContentTypeDefinitionVOList(InfoGluePrincipal infoGluePrincipal) throws ConstraintException, SystemException, Bug
	{    	
		List accessableContentTypeDefinitionVOList = new ArrayList();
    	
		List allContentTypeDefinitionVOList = this.getContentTypeDefinitionVOList(); 
		Iterator i = allContentTypeDefinitionVOList.iterator();
		while(i.hasNext())
		{
		    ContentTypeDefinitionVO contentTypeDefinitionVO = (ContentTypeDefinitionVO)i.next();
			if(getIsAccessApproved(contentTypeDefinitionVO.getId(), infoGluePrincipal))
			    accessableContentTypeDefinitionVOList.add(contentTypeDefinitionVO);
		}
    	
		return accessableContentTypeDefinitionVOList;
	}

	/**
	 * This method can be used by actions and use-case-controllers that only need to have simple access to the
	 * functionality. They don't get the transaction-safety but probably just wants to show the info.
	 */	
	
	public List getSortedAuthorizedContentTypeDefinitionVOList(InfoGluePrincipal infoGluePrincipal, Database db) throws ConstraintException, SystemException, Bug
	{
		List authorizedContentTypeDefinitionVOList = getAuthorizedContentTypeDefinitionVOList(infoGluePrincipal, db);
		
		Collections.sort(authorizedContentTypeDefinitionVOList, new ReflectionComparator("name"));
		
		return authorizedContentTypeDefinitionVOList;
	}
	
	/**
	 * This method can be used by actions and use-case-controllers that only need to have simple access to the
	 * functionality. They don't get the transaction-safety but probably just wants to show the info.
	 */	
	
	public List getAuthorizedContentTypeDefinitionVOList(InfoGluePrincipal infoGluePrincipal, Database db) throws ConstraintException, SystemException, Bug
	{    	
		List accessableContentTypeDefinitionVOList = new ArrayList();
    	
		List allContentTypeDefinitionVOList = this.getContentTypeDefinitionVOList(db); 

		String protectContentTypes = CmsPropertyHandler.getProtectContentTypes();
		if(protectContentTypes != null && protectContentTypes.equalsIgnoreCase("true"))
		{
			Iterator i = allContentTypeDefinitionVOList.iterator();
			while(i.hasNext())
			{
			    ContentTypeDefinitionVO contentTypeDefinitionVO = (ContentTypeDefinitionVO)i.next();
				if(getIsAccessApproved(contentTypeDefinitionVO.getId(), infoGluePrincipal, db))
				    accessableContentTypeDefinitionVOList.add(contentTypeDefinitionVO);
			}
		}
		else
		{
			accessableContentTypeDefinitionVOList.addAll(allContentTypeDefinitionVOList);
		}
		
		return accessableContentTypeDefinitionVOList;
	}

	/**
	 * This method returns true if the user should have access to the contentTypeDefinition sent in.
	 */
    
	public boolean getIsAccessApproved(Integer contentTypeDefinitionId, InfoGluePrincipal infoGluePrincipal) throws SystemException
	{
		logger.info("getIsAccessApproved for " + contentTypeDefinitionId + " AND " + infoGluePrincipal);
		boolean hasAccess = false;
    	
		Database db = CastorDatabaseService.getDatabase();
       
		beginTransaction(db);

		try
		{ 
			hasAccess = AccessRightController.getController().getIsPrincipalAuthorized(db, infoGluePrincipal, "ContentTypeDefinition.Read", contentTypeDefinitionId.toString());
		
			commitTransaction(db);
		}
		catch(Exception e)
		{
			logger.error("An error occurred so we should not complete the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
    
		return hasAccess;
	}

	/**
	 * This method returns true if the user should have access to the contentTypeDefinition sent in.
	 */
    
	public boolean getIsAccessApproved(Integer contentTypeDefinitionId, InfoGluePrincipal infoGluePrincipal, Database db) throws SystemException
	{
		logger.info("getIsAccessApproved for " + contentTypeDefinitionId + " AND " + infoGluePrincipal);
		boolean hasAccess = false;
    	
		hasAccess = AccessRightController.getController().getIsPrincipalAuthorized(db, infoGluePrincipal, "ContentTypeDefinition.Read", contentTypeDefinitionId.toString());
		
		return hasAccess;
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
			logger.info("An error occurred so we should not complete the transaction:" + e);
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

	    	this.logger.info("Fetching entity in read/write mode" + name);
			
	    	QueryResults results = oql.execute();
			if (results.hasMore())
			{
				contentTypeDefinition = (ContentTypeDefinition)results.next();
			}
			
			results.close();
			oql.close();
		}
		catch(Exception e)
		{
			throw new SystemException("An error occurred when we tried to fetch a named ContentTypeDefinition. Reason:" + e.getMessage(), e);
		}

		return contentTypeDefinition;
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

	public ContentTypeDefinitionVO getContentTypeDefinitionVOWithName(String name, Database db) throws SystemException, Bug
	{
		String key = "" + name;
		logger.info("key:" + key);
		ContentTypeDefinitionVO contentTypeDefinitionVO = (ContentTypeDefinitionVO)CacheController.getCachedObject("contentTypeDefinitionCache", key);
		if(contentTypeDefinitionVO != null)
		{
			logger.info("There was an cached contentTypeDefinitionVO:" + contentTypeDefinitionVO);
		}
		else
		{
			logger.info("Refetching contentTypeDefinitionVO:" + contentTypeDefinitionVO);

			try
			{
				OQLQuery oql = db.getOQLQuery("SELECT f FROM org.infoglue.cms.entities.management.impl.simple.ContentTypeDefinitionImpl f WHERE f.name = $1");
				oql.bind(name);
	
				QueryResults results = oql.execute(Database.ReadOnly);
				if (results.hasMore())
				{
				    ContentTypeDefinition contentTypeDefinition = (ContentTypeDefinition)results.next();
				    contentTypeDefinitionVO = contentTypeDefinition.getValueObject();

				    CacheController.cacheObject("contentTypeDefinitionCache", key, contentTypeDefinitionVO);
				}
				
				results.close();
				oql.close();
			}
			catch(Exception e)
			{
				throw new SystemException("An error occurred when we tried to fetch a named ContentTypeDefinition. Reason:" + e.getMessage(), e);
			}
		}
		
		return contentTypeDefinitionVO;
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
			logger.info("An error occurred so we should not complete the transaction:" + e);
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

			QueryResults results = oql.execute(Database.ReadOnly);
			while (results.hasMore())
			{
				ContentTypeDefinition contentTypeDefinition = (ContentTypeDefinition)results.next();
				contentTypeDefinitionVOList.add(contentTypeDefinition.getValueObject());
			}
			
			results.close();
			oql.close();
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
	 * This method fetches a predefined assetKeys from a xml-string representing a contentTypeDefinition.
	 */
	public AssetKeyDefinition getDefinedAssetKey(String contentTypeDefinitionString, String assetKey)
	{
	    AssetKeyDefinition assetKeyDefinition = null;
	    
	    List definedAssetKeys = getDefinedAssetKeys(contentTypeDefinitionString);
	    Iterator i = definedAssetKeys.iterator();
	    while(i.hasNext())
	    {
	        AssetKeyDefinition currentAssetKeyDefinition = (AssetKeyDefinition)i.next();
	        if(currentAssetKeyDefinition.getAssetKey().equals(assetKey))
	        {
	            assetKeyDefinition = currentAssetKeyDefinition;
	        	break;
	        }
	    }
	    
	    return assetKeyDefinition;
	}

	/**
	 * This method fetches any predefined assetKeys from a xml-string representing a contentTypeDefinition.
	 */
	public List getDefinedAssetKeys(String contentTypeDefinitionString)
	{
		NodeList nodes = getEnumerationNodeList(contentTypeDefinitionString, ASSET_KEYS);

		return getEnumValues(nodes);
	}


	/**
	 * This method fetches any predefined categoryKeys from a xml-string representing a contentTypeDefinition.
	 */
	public List getDefinedCategoryKeys(String contentTypeDefinitionString)
	{
		NodeList nodes = getEnumerationNodeList(contentTypeDefinitionString, CATEGORY_KEYS);
		return getCategoryInfo(nodes);
	}

	/**
	 * Returns a List of values fro the "value" atribute of the provided NodeList
	 */
	protected List getEnumValues(NodeList nodes)
	{
	   List keys = new ArrayList();
		for(int i = 0; i < nodes.getLength(); i++)
		{
		    Node ichild = nodes.item(i);
		    
		    logger.info("ichild:" + ichild.getNodeName() + ":" + ichild.getNodeValue());
			
			try
			{
			    Node assetKeyValue = ichild.getAttributes().getNamedItem("value");

			    Element params = (Element)XPathAPI.selectSingleNode(ichild, "xs:annotation/xs:appinfo/params");

			    String isMandatoryValue = "false";
			    String descriptionValue = "";
			    String maximumSizeValue = "1000000";
			    String allowedContentTypesValue = "*";
			    String imageWidthValue = "*";
			    String imageHeightValue = "*";
		    
			    if(params != null)
			    {
			    	isMandatoryValue = getElementValue(params, "isMandatory");
			    	if(isMandatoryValue == null)
			    		isMandatoryValue = "false";
				    descriptionValue = getElementValue(params, "description");
				    maximumSizeValue = getElementValue(params, "maximumSize");
				    allowedContentTypesValue = getElementValue(params, "allowedContentTypes");
				    imageWidthValue = getElementValue(params, "imageWidth");
				    imageHeightValue = getElementValue(params, "imageHeight");
			    }
			    
				AssetKeyDefinition assetKeyDefinition = new AssetKeyDefinition(); 
				
				assetKeyDefinition.setAssetKey(assetKeyValue.getNodeValue());
				assetKeyDefinition.setIsMandatory(new Boolean(isMandatoryValue));
				assetKeyDefinition.setDescription(descriptionValue);
				assetKeyDefinition.setMaximumSize(new Integer(maximumSizeValue));
				assetKeyDefinition.setAllowedContentTypes(allowedContentTypesValue);
				assetKeyDefinition.setImageWidth(imageWidthValue);
				assetKeyDefinition.setImageHeight(imageHeightValue);
				
				logger.info("Adding assetKeyDefinition " + assetKeyDefinition.getAssetKey());
				keys.add(assetKeyDefinition);
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			}
		}
		
		logger.info("keys:" + keys.size());
		
		return keys;
	}

	/**
	 * Returns a List of CategoryInfos for the category atributes of the NodeList
	 */
	protected List getCategoryInfo(NodeList nodes)
	{
		String attributesXPath = "xs:annotation/xs:appinfo/params";

		List keys = new ArrayList();
		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node enumeration = nodes.item(i);
			String value = enumeration.getAttributes().getNamedItem("value").getNodeValue();
			try
			{
				CategoryAttribute category = new CategoryAttribute(value);
				keys.add(category);

				Element params = (Element)XPathAPI.selectSingleNode(enumeration, attributesXPath);
				if(params != null)
				{
					category.setTitle(getElementValue(params, "title"));
					category.setDescription(getElementValue(params, "description"));
					category.setCategoryId(getElementValue(params, "categoryId"));
				}
			}
			catch (TransformerException e)
			{
				keys.add(new CategoryAttribute(value));
			}
		}
		return keys;
	}

	/**
	 * Returns a list of xs:enumeration nodes base on the provided key.
	 * @param keyType The key to find enumerations for
	 */
	protected NodeList getEnumerationNodeList(String contentTypeDefinitionString, String keyType)
	{
        try
        {
        	if(contentTypeDefinitionString != null)
        	{
		        InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));

				DOMParser parser = new DOMParser();
				parser.parse(xmlSource);
				Document document = parser.getDocument();

				String attributesXPath = "/xs:schema/xs:simpleType[@name = '" + keyType + "']/xs:restriction/xs:enumeration";
				return XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
        	}
        }
        catch(Exception e)
        {
        	logger.warn("An error occurred when trying to fetch the asset keys:" + e.getMessage(), e);
        }

		return EMPTY_NODELIST;
	}

	/**
	 * Get the CDATA value from the provided elements child tag
	 * @param root The root element to find the child tag
	 * @param tagName The tag name of the child to get the CDATA value
	 * @return The String CDATA or null if the tag does not exist or no value is set.
	 */
	protected String getElementValue(Element root, String tagName)
	{
		NodeList nodes = root.getElementsByTagName(tagName);
		if(nodes.getLength() > 0)
		{
			Node cdata = nodes.item(0).getFirstChild();
			return (cdata != null)? cdata.getNodeValue() : null;
		}

		return null;
	}

	/**
	 * This method returns the attributes in the content type definition for generation.
	 */

	public List getContentTypeAttributes(String schemaValue)
	{
		//List attributes = new ArrayList();

	    String key = "schemaValue_" + schemaValue.hashCode();
		Object attributesCandidate = CacheController.getCachedObject("contentTypeDefinitionCache", key);
		List attributes = new ArrayList();
			
		if(attributesCandidate != null)
		{
			if(attributesCandidate instanceof NullObject)
				attributes = new ArrayList();				
			else
				attributes = (List)attributesCandidate;
				
			//logger.info("Returning cached attributes for key " + key + "-" + attributes);
		}
		else
		{
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
	
					String validatorsXPath = "/xs:schema/xs:complexType[@name = 'Validation']/xs:annotation/xs:appinfo/form-validation/formset/form/field[@property = '"+ attributeName +"']";
	
					// Get validators
					NodeList validatorNodeList = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), validatorsXPath);
					for(int j=0; j < validatorNodeList.getLength(); j++)
					{
						Element validatorNode = (Element)validatorNodeList.item(j);
						if (validatorNode != null)
						{
						    Map arguments = new HashMap();
						    
						    NodeList varNodeList = validatorNode.getElementsByTagName("var");
						    for(int k=0; k < varNodeList.getLength(); k++)
							{
								Element varNode = (Element)varNodeList.item(k);
								
								String varName = getElementValue(varNode, "var-name");
								String varValue = getElementValue(varNode, "var-value");
	
								arguments.put(varName, varValue);
							}	    
						    
						    String attribute = ((Element)validatorNode).getAttribute("depends");
						    String[] depends = attribute.split(",");
						    for(int dependsIndex=0; dependsIndex < depends.length; dependsIndex++)
						    {
						        String name = depends[dependsIndex];
	
						        ContentTypeAttributeValidator contentTypeAttributeValidator = new ContentTypeAttributeValidator();
						        contentTypeAttributeValidator.setName(name);
						        contentTypeAttributeValidator.setArguments(arguments);
						        contentTypeAttribute.getValidators().add(contentTypeAttributeValidator);					        
						    }
						    
						    
						}
					}
					
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
										Node attribute = (Node)nodeMap.item(nmi);
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
				logger.error("An error occurred when we tried to get the attributes of the content type: " + e.getMessage(), e);
			}
		}

		if(attributes != null)
		    CacheController.cacheObject("contentTypeDefinitionCache", key, attributes);
		else
			CacheController.cacheObject("contentTypeDefinitionCache", key, new NullObject());
				
		return attributes;
	}

	/**
	 * This method adds a new content type attribute to the contentTypeDefinition. It sets some default values.
	 */

	public String insertContentTypeAttribute(String schemaValue, String inputTypeId, List activatedName)
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
				String name = "newAttributeName" + (int)(Math.random() * 100);
				activatedName.add(name);
				newAttribute.setAttribute("name", name);
				newAttribute.setAttribute("type", inputTypeId);
				childElement.appendChild(newAttribute);

				Element annotation = document.createElement("xs:annotation");
				Element appInfo    = document.createElement("xs:appinfo");
				Element params     = document.createElement("params");

				addParameterElement(params, "title", "0");
				addParameterElement(params, "description", "0");
				addParameterElement(params, "initialData", "");
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
					addParameterElement(params, "WYSIWYGToolbar", "0", "Default");
					addParameterElement(params, "WYSIWYGExtraConfig", "0", "");
					addParameterElement(params, "enableTemplateEditor", "0", "false");
					addParameterElement(params, "enableFormEditor", "0", "false");
					addParameterElement(params, "enableContentRelationEditor", "0", "false");
					addParameterElement(params, "enableStructureRelationEditor", "0", "false");
					addParameterElement(params, "enableComponentPropertiesEditor", "0", "false");
					addParameterElement(params, "activateExtendedEditorOnLoad", "0", "false");
				}

				if(inputTypeId.equalsIgnoreCase("customfield"))
				{
					addParameterElement(params, "Markup", "2", "");
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
										addParameterElement(paramsElement, "WYSIWYGToolbar", "0", "Default");
										addParameterElement(paramsElement, "WYSIWYGExtraConfig", "0", "");
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
										addParameterElement(paramsElement, "WYSIWYGToolbar", "0", "Default");
										addParameterElement(paramsElement, "WYSIWYGExtraConfig", "0", "");
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
									addParameterElement(paramsElement, "WYSIWYGToolbar", "0", "Default");
									addParameterElement(paramsElement, "WYSIWYGExtraConfig", "0", "");
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
								addParameterElement(paramsElement, "WYSIWYGToolbar", "0", "Default");
								addParameterElement(paramsElement, "WYSIWYGExtraConfig", "0", "");
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
										addParameterElementIfNotExists(paramsElement, "WYSIWYGToolbar", "0", "Default");
										addParameterElementIfNotExists(paramsElement, "WYSIWYGExtraConfig", "0", "");
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
				else if(schemaElement.getAttribute("version") != null && schemaElement.getAttribute("version").equalsIgnoreCase("2.1"))
				{
					isModified = true;
					schemaElement.setAttribute("version", "2.2");

					//Now we deal with adding the validation part if not existent
					String validatorsXPath = "/xs:schema/xs:complexType[@name = 'Validation']";
					Node formNode = org.apache.xpath.XPathAPI.selectSingleNode(document.getDocumentElement(), validatorsXPath);
					if(formNode == null)
					{
					    String schemaXPath = "/xs:schema";
						Node schemaNode = org.apache.xpath.XPathAPI.selectSingleNode(document.getDocumentElement(), schemaXPath);
						
					    Element element = (Element)schemaNode;
					    
					    String validationXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xs:complexType name=\"Validation\" xmlns:xi=\"http://www.w3.org/2001/XInclude\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:annotation><xs:appinfo><form-validation><global><validator name=\"required\" classname=\"org.infoglue.cms.util.validators.CommonsValidator\" method=\"validateRequired\" methodParams=\"java.lang.Object,org.apache.commons.validator.Field\" msg=\"300\"/><validator name=\"requiredif\" classname=\"org.infoglue.cms.util.validators.CommonsValidator\" method=\"validateRequiredIf\" methodParams=\"java.lang.Object,org.apache.commons.validator.Field,org.apache.commons.validator.Validator\" msg=\"315\"/><validator name=\"matchRegexp\" classname=\"org.infoglue.cms.util.validators.CommonsValidator\" method=\"validateRegexp\" methodParams=\"java.lang.Object,org.apache.commons.validator.Field\" msg=\"300\"/></global><formset><form name=\"requiredForm\"></form></formset></form-validation></xs:appinfo></xs:annotation></xs:complexType>";
					    
					    InputSource validationXMLSource = new InputSource(new StringReader(validationXML));
						DOMParser parser2 = new DOMParser();
						parser2.parse(validationXMLSource);
						Document document2 = parser2.getDocument();

						Node node = document.importNode(document2.getDocumentElement(), true);
						element.appendChild(node);
					}
				}
				else if(schemaElement.getAttribute("version") != null && schemaElement.getAttribute("version").equalsIgnoreCase("2.2"))
				{
					isModified = true;
					schemaElement.setAttribute("version", "2.3");

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

									addParameterElementIfNotExists(paramsElement, "initialData", "0", "");
									isModified = true;
								}
							}
						}
					}

				}
				else if(schemaElement.getAttribute("version") != null && schemaElement.getAttribute("version").equalsIgnoreCase("2.3"))
				{
					isModified = true;
					schemaElement.setAttribute("version", "2.4");

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

									addParameterElementIfNotExists(paramsElement, "enableComponentPropertiesEditor", "0", "false");

									isModified = true;
								}
							}
						}
					}

				}
				else if(schemaElement.getAttribute("version") != null && schemaElement.getAttribute("version").equalsIgnoreCase("2.4"))
				{
					isModified = true;
					schemaElement.setAttribute("version", "2.5");

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

									addParameterElementIfNotExists(paramsElement, "WYSIWYGToolbar", "0", "Default");
									addParameterElementIfNotExists(paramsElement, "WYSIWYGExtraConfig", "0", "");

									isModified = true;
								}
							}
						}
					}
				}
				else if(schemaElement.getAttribute("version") != null && schemaElement.getAttribute("version").equalsIgnoreCase("2.5"))
				{
					isModified = true;
					schemaElement.setAttribute("version", "2.5.1");

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

									addParameterElementIfNotExists(paramsElement, "WYSIWYGExtraConfig", "0", "");

									isModified = true;
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
	 * Creates an <xs:enumeration> element with the specified key name
	 * @return The Element if child changes are needed, null if the element coudl not be created
	 */
	public Element createNewEnumerationKey(Document document, String keyType) throws TransformerException
	{
		Element enumeration = null;
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

		enumeration = document.createElement("xs:enumeration");
		enumeration.setAttribute("value", getRandomName());
		keyRestriction.appendChild(enumeration);
		return enumeration;
	}
	
	/**
	 * Generates a random name
	 */
	private String getRandomName()
	{
		return "undefined" + (int)(Math.random() * 100);
	}
	
	
	//**
	
	public String copyAttribute(String remoteSchemaValue, String localSchemaValue, String contentTypeAttributeName)
	{
		String newSchemaValue = localSchemaValue;

		try
		{
			Document remoteDocument = createDocumentFromDefinition(remoteSchemaValue);
			Document localDocument = createDocumentFromDefinition(localSchemaValue);

			String attributeXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + contentTypeAttributeName + "']";
			Node attributeNode = org.apache.xpath.XPathAPI.selectSingleNode(remoteDocument.getDocumentElement(), attributeXPath);
			
			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all";
			Node attributesNode = org.apache.xpath.XPathAPI.selectSingleNode(localDocument.getDocumentElement(), attributesXPath);
			logger.info("attributesNode:" + attributesNode);
			if(attributesNode != null && localDocument != null && attributeNode != null)
			{
				Node node = localDocument.importNode(attributeNode, true);
	
				attributesNode.appendChild(node);
				
				StringBuffer sb = new StringBuffer();
				org.infoglue.cms.util.XMLHelper.serializeDom(localDocument.getDocumentElement(), sb);
				newSchemaValue = sb.toString();
			}
			else
			{
				logger.error("Problem:" + attributesNode + " - " + localDocument + " - " + attributeNode);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return newSchemaValue;
	}

	public String copyCategory(String remoteSchemaValue, String localSchemaValue, String categoryName)
	{
		String newSchemaValue = localSchemaValue;

		try
		{
			Document remoteDocument = createDocumentFromDefinition(remoteSchemaValue);
			Document localDocument = createDocumentFromDefinition(localSchemaValue);
			
			String attributeXPath = "/xs:schema/xs:simpleType[@name='categoryKeys']/xs:restriction/xs:enumeration[@value='" + categoryName + "']";
			Node attributeNode = org.apache.xpath.XPathAPI.selectSingleNode(remoteDocument.getDocumentElement(), attributeXPath);
			
			String attributesXPath = "/xs:schema/xs:simpleType[@name='categoryKeys']/xs:restriction";
			Node attributesNode = org.apache.xpath.XPathAPI.selectSingleNode(localDocument.getDocumentElement(), attributesXPath);
			if(attributesNode == null)
			{
				attributesNode = ContentTypeDefinitionController.getController().createNewEnumerationKey(localDocument, ContentTypeDefinitionController.CATEGORY_KEYS);
			}

			if(attributesNode != null && localDocument != null && attributeNode != null)
			{
				Node node = localDocument.importNode(attributeNode, true);
				attributesNode.appendChild(node);
				
				StringBuffer sb = new StringBuffer();
				org.infoglue.cms.util.XMLHelper.serializeDom(localDocument.getDocumentElement(), sb);
				newSchemaValue = sb.toString();
			}
			else
			{
				logger.error("Problem:" + attributesNode + " - " + localDocument + " - " + attributeNode);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return newSchemaValue;
	}

	public String copyAssetKey(String remoteSchemaValue, String localSchemaValue, String assetKey)
	{
		String newSchemaValue = localSchemaValue;

		try
		{
			Document remoteDocument = createDocumentFromDefinition(remoteSchemaValue);
			Document localDocument = createDocumentFromDefinition(localSchemaValue);

			String attributeXPath = "/xs:schema/xs:simpleType[@name='assetKeys']/xs:restriction/xs:enumeration[@value='" + assetKey + "']";
			Node attributeNode = org.apache.xpath.XPathAPI.selectSingleNode(remoteDocument.getDocumentElement(), attributeXPath);
		
			String attributesXPath = "/xs:schema/xs:simpleType[@name='assetKeys']/xs:restriction";
			Node attributesNode = org.apache.xpath.XPathAPI.selectSingleNode(localDocument.getDocumentElement(), attributesXPath);
			if(attributesNode == null)
			{
				attributesNode = ContentTypeDefinitionController.getController().createNewEnumerationKey(localDocument, ContentTypeDefinitionController.ASSET_KEYS);
			}
			
			if(attributesNode != null && localDocument != null && attributeNode != null)
			{
				Node node = localDocument.importNode(attributeNode, true);
				attributesNode.appendChild(node);
				
				StringBuffer sb = new StringBuffer();
				org.infoglue.cms.util.XMLHelper.serializeDom(localDocument.getDocumentElement(), sb);
				newSchemaValue = sb.toString();
			}
			else
			{
				logger.error("Problem:" + attributesNode + " - " + localDocument + " - " + attributeNode);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return newSchemaValue;
	}

	
	/**
	 * Consolidate the Document creation
	 */
	private Document createDocumentFromDefinition(String schemaValue) throws SAXException, IOException
	{
		InputSource xmlSource = new InputSource(new StringReader(schemaValue));
		DOMParser parser = new DOMParser();
		parser.parse(xmlSource);
		return parser.getDocument();
	}

	//**
	

	/**
	 * This is a method that gives the user back an newly initialized ValueObject for this entity that the controller
	 * is handling.
	 */

	public BaseEntityVO getNewVO()
	{
		return new ContentTypeDefinitionVO();
	}
}
