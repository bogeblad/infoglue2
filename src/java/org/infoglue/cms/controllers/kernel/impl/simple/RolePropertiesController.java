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

import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.entities.content.DigitalAsset;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.ContentTypeDefinition;
import org.infoglue.cms.entities.management.RoleContentTypeDefinition;
import org.infoglue.cms.entities.management.RoleProperties;
import org.infoglue.cms.entities.management.RolePropertiesVO;
import org.infoglue.cms.entities.management.Language;
import org.infoglue.cms.entities.management.UserProperties;
import org.infoglue.cms.entities.management.impl.simple.RoleContentTypeDefinitionImpl;
import org.infoglue.cms.entities.management.impl.simple.RolePropertiesImpl;
import org.infoglue.cms.entities.management.impl.simple.LanguageImpl;
import org.infoglue.cms.exception.*;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.CmsLogger;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import org.apache.xerces.parsers.DOMParser;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * This class is the controller for all handling of extranet roles properties.
 */

public class RolePropertiesController extends BaseController
{
	
	/**
	 * Factory method
	 */

	public static RolePropertiesController getController()
	{
		return new RolePropertiesController();
	}
	
	
    public RoleProperties getRolePropertiesWithId(Integer rolePropertiesId, Database db) throws SystemException, Bug
    {
		return (RoleProperties) getObjectWithId(RolePropertiesImpl.class, rolePropertiesId, db);
    }
    
    public RolePropertiesVO getRolePropertiesVOWithId(Integer rolePropertiesId) throws SystemException, Bug
    {
		return (RolePropertiesVO) getVOWithId(RolePropertiesImpl.class, rolePropertiesId);
    }
  
    public List getRolePropertiesVOList() throws SystemException, Bug
    {
        return getAllVOObjects(RolePropertiesImpl.class, "rolePropertiesId");
    }

    
	/**
	 * This method created a new RolePropertiesVO in the database.
	 */

	public RolePropertiesVO create(Integer languageId, Integer contentTypeDefinitionId, RolePropertiesVO rolePropertiesVO) throws ConstraintException, SystemException
    {
		Database db = CastorDatabaseService.getDatabase();
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

		RoleProperties roleProperties = null;

		beginTransaction(db);
		try
		{
			roleProperties = create(languageId, contentTypeDefinitionId, rolePropertiesVO, db);
			commitTransaction(db);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
    
		return roleProperties.getValueObject();
	}     

	/**
	 * This method created a new RolePropertiesVO in the database. It also updates the extranetgroup
	 * so it recognises the change. 
	 */

	public RoleProperties create(Integer languageId, Integer contentTypeDefinitionId, RolePropertiesVO rolePropertiesVO, Database db) throws ConstraintException, SystemException, Exception
    {
		Language language = LanguageController.getController().getLanguageWithId(languageId, db);
		ContentTypeDefinition contentTypeDefinition = ContentTypeDefinitionController.getController().getContentTypeDefinitionWithId(contentTypeDefinitionId, db);

		RoleProperties roleProperties = new RolePropertiesImpl();
		roleProperties.setLanguage((LanguageImpl)language);
		roleProperties.setContentTypeDefinition((ContentTypeDefinition)contentTypeDefinition);
	
		roleProperties.setValueObject(rolePropertiesVO);
		db.create(roleProperties); 
		
		return roleProperties;
	}     
	
	/**
	 * This method updates an extranet role properties.
	 */

	public RolePropertiesVO update(Integer languageId, Integer contentTypeDefinitionId, RolePropertiesVO rolePropertiesVO) throws ConstraintException, SystemException
	{
		RolePropertiesVO realRolePropertiesVO = rolePropertiesVO;
    	
		if(rolePropertiesVO.getId() == null)
		{
			CmsLogger.logInfo("Creating the entity because there was no version at all for: " + contentTypeDefinitionId + " " + languageId);
			realRolePropertiesVO = create(languageId, contentTypeDefinitionId, rolePropertiesVO);
		}

		return (RolePropertiesVO) updateEntity(RolePropertiesImpl.class, (BaseEntityVO) realRolePropertiesVO);
	}        

	public RolePropertiesVO update(RolePropertiesVO rolePropertiesVO, String[] extranetUsers) throws ConstraintException, SystemException
	{
		Database db = CastorDatabaseService.getDatabase();
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

		RoleProperties roleProperties = null;

		beginTransaction(db);

		try
		{
			//add validation here if needed
			roleProperties = getRolePropertiesWithId(rolePropertiesVO.getRolePropertiesId(), db);       	
			roleProperties.setValueObject(rolePropertiesVO);

			//If any of the validations or setMethods reported an error, we throw them up now before create.
			ceb.throwIfNotEmpty();
            
			commitTransaction(db);
		}
		catch(ConstraintException ce)
		{
			CmsLogger.logWarning("An error occurred so we should not complete the transaction:" + ce, ce);
			rollbackTransaction(db);
			throw ce;
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred so we should not complete the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}

		return roleProperties.getValueObject();
	}     
	
	/**
	 * This method gets a list of roleProperties for a role
	 * The result is a list of propertiesblobs - each propertyblob is a list of actual properties.
	 */

	public List getRolePropertiesVOList(String roleName, Integer languageId) throws ConstraintException, SystemException
	{
		Database db = CastorDatabaseService.getDatabase();
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

		List rolePropertiesVOList = new ArrayList();

		beginTransaction(db);

		try
		{
			List roleProperties = getRolePropertiesList(roleName, languageId, db);
			rolePropertiesVOList = toVOList(roleProperties);
			
			//If any of the validations or setMethods reported an error, we throw them up now before create.
			ceb.throwIfNotEmpty();
            
			commitTransaction(db);
		}
		catch(ConstraintException ce)
		{
			CmsLogger.logWarning("An error occurred so we should not complete the transaction:" + ce, ce);
			rollbackTransaction(db);
			throw ce;
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred so we should not complete the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}

		return rolePropertiesVOList;
	}

	/**
	 * This method gets a list of roleProperties for a role
	 * The result is a list of propertiesblobs - each propertyblob is a list of actual properties.
	 */

	public List getRolePropertiesList(String roleName, Integer languageId, Database db) throws ConstraintException, SystemException, Exception
	{
		List rolePropertiesList = new ArrayList();

		OQLQuery oql = db.getOQLQuery("SELECT f FROM org.infoglue.cms.entities.management.impl.simple.RolePropertiesImpl f WHERE f.roleName = $1 AND f.language = $2");
		oql.bind(roleName);
		oql.bind(languageId);

		QueryResults results = oql.execute();
		while (results.hasMore()) 
		{
			RoleProperties roleProperties = (RoleProperties)results.next();
			rolePropertiesList.add(roleProperties);
		}

		return rolePropertiesList;
	}
	
    public void delete(RolePropertiesVO rolePropertiesVO) throws ConstraintException, SystemException
    {
    	deleteEntity(RolePropertiesImpl.class, rolePropertiesVO.getRolePropertiesId());
    }        

    
	/**
	 * This method should return a list of those digital assets the contentVersion has.
	 */
	   	
	public List getDigitalAssetVOList(Integer rolePropertiesId) throws SystemException, Bug
    {
    	Database db = CastorDatabaseService.getDatabase();
        ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

    	List digitalAssetVOList = new ArrayList();

        beginTransaction(db);

        try
        {
			RoleProperties roleProperties = RolePropertiesController.getController().getRolePropertiesWithId(rolePropertiesId, db); 
			if(roleProperties != null)
			{
				Collection digitalAssets = roleProperties.getDigitalAssets();
				digitalAssetVOList = toVOList(digitalAssets);
			}
			            
            commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logInfo("An error occurred when we tried to fetch the list of digitalAssets belonging to this roleProperties:" + e);
            e.printStackTrace();
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
    	
		return digitalAssetVOList;
    }

	
	/**
	 * This method deletes the relation to a digital asset - not the asset itself.
	 */
	public void deleteDigitalAssetRelation(Integer rolePropertiesId, DigitalAsset digitalAsset, Database db) throws SystemException, Bug
    {
	    RoleProperties roleProperties = getRolePropertiesWithId(rolePropertiesId, db);
	    roleProperties.getDigitalAssets().remove(digitalAsset);
        digitalAsset.getRoleProperties().remove(roleProperties);
    }


	/**
	 * This method fetches all content types available for this role. 
	 */
	
	public List getContentTypeDefinitionVOList(String roleName) throws ConstraintException, SystemException
	{
		Database db = CastorDatabaseService.getDatabase();
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

		List contentTypeDefinitionVOList = new ArrayList();

		beginTransaction(db);

		try
		{
			List roleContentTypeDefinitionList = getRoleContentTypeDefinitionList(roleName, db);
			Iterator contentTypeDefinitionsIterator = roleContentTypeDefinitionList.iterator();
			while(contentTypeDefinitionsIterator.hasNext())
			{
				RoleContentTypeDefinition roleContentTypeDefinition = (RoleContentTypeDefinition)contentTypeDefinitionsIterator.next();
				contentTypeDefinitionVOList.add(roleContentTypeDefinition.getContentTypeDefinition().getValueObject());
			}
	
			ceb.throwIfNotEmpty();
    
			commitTransaction(db);
		}
		catch(ConstraintException ce)
		{
			CmsLogger.logWarning("An error occurred so we should not complete the transaction:" + ce, ce);
			rollbackTransaction(db);
			throw ce;
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred so we should not complete the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}

		return contentTypeDefinitionVOList;
	}

	/**
	 * This method fetches all role content types available for this role within a transaction. 
	 */
	
	public List getRoleContentTypeDefinitionList(String roleName, Database db) throws ConstraintException, SystemException, Exception
	{
		List roleContentTypeDefinitionList = new ArrayList();

		OQLQuery oql = db.getOQLQuery("SELECT f FROM org.infoglue.cms.entities.management.impl.simple.RoleContentTypeDefinitionImpl f WHERE f.roleName = $1");
		oql.bind(roleName);

		QueryResults results = oql.execute();
		while (results.hasMore()) 
		{
			RoleContentTypeDefinition roleContentTypeDefinition = (RoleContentTypeDefinition)results.next();
			roleContentTypeDefinitionList.add(roleContentTypeDefinition);
		}

		return roleContentTypeDefinitionList;
	}
	
	/**
	 * This method fetches all content types available for this role. 
	 */

	public void updateContentTypeDefinitions(String roleName, String[] contentTypeDefinitionIds) throws ConstraintException, SystemException
	{
		Database db = CastorDatabaseService.getDatabase();
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

		List contentTypeDefinitionVOList = new ArrayList();

		beginTransaction(db);

		try
		{
			List roleContentTypeDefinitionList = this.getRoleContentTypeDefinitionList(roleName, db);
			Iterator contentTypeDefinitionsIterator = roleContentTypeDefinitionList.iterator();
			while(contentTypeDefinitionsIterator.hasNext())
			{
				RoleContentTypeDefinition roleContentTypeDefinition = (RoleContentTypeDefinition)contentTypeDefinitionsIterator.next();
				db.remove(roleContentTypeDefinition);
			}
			
			for(int i=0; i<contentTypeDefinitionIds.length; i++)
			{
				Integer contentTypeDefinitionId = new Integer(contentTypeDefinitionIds[i]);
				ContentTypeDefinition contentTypeDefinition = ContentTypeDefinitionController.getController().getContentTypeDefinitionWithId(contentTypeDefinitionId, db);
				RoleContentTypeDefinitionImpl roleContentTypeDefinitionImpl = new RoleContentTypeDefinitionImpl();
				roleContentTypeDefinitionImpl.setRoleName(roleName);
				roleContentTypeDefinitionImpl.setContentTypeDefinition(contentTypeDefinition);
				db.create(roleContentTypeDefinitionImpl);
			}
			
			ceb.throwIfNotEmpty();

			commitTransaction(db);
		}
		catch(ConstraintException ce)
		{
			CmsLogger.logWarning("An error occurred so we should not complete the transaction:" + ce, ce);
			rollbackTransaction(db);
			throw ce;
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred so we should not complete the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
	}
	
	
	/**
	 * This method fetches a value from the xml that is the roleProperties Value. It then updates that
	 * single value and saves it back to the db.
	 */
	 
	public void updateAttributeValue(Integer rolePropertiesId, String attributeName, String attributeValue) throws SystemException, Bug
	{
		RolePropertiesVO rolePropertiesVO = getRolePropertiesVOWithId(rolePropertiesId);
		
		if(rolePropertiesVO != null)
		{
			try
			{
				CmsLogger.logInfo("attributeName:"  + attributeName);
				CmsLogger.logInfo("versionValue:"   + rolePropertiesVO.getValue());
				CmsLogger.logInfo("attributeValue:" + attributeValue);
				InputSource inputSource = new InputSource(new StringReader(rolePropertiesVO.getValue()));
				
				DOMParser parser = new DOMParser();
				parser.parse(inputSource);
				Document document = parser.getDocument();
				
				NodeList nl = document.getDocumentElement().getChildNodes();
				Node attributesNode = nl.item(0);
				
				boolean existed = false;
				nl = attributesNode.getChildNodes();
				for(int i=0; i<nl.getLength(); i++)
				{
					Node n = nl.item(i);
					if(n.getNodeName().equalsIgnoreCase(attributeName))
					{
						if(n.getFirstChild() != null && n.getFirstChild().getNodeValue() != null)
						{
							n.getFirstChild().setNodeValue(attributeValue);
							existed = true;
							break;
						}
						else
						{
							CDATASection cdata = document.createCDATASection(attributeValue);
							n.appendChild(cdata);
							existed = true;
							break;
						}
					}
				}
				
				if(existed == false)
				{
					org.w3c.dom.Element attributeElement = document.createElement(attributeName);
					attributesNode.appendChild(attributeElement);
					CDATASection cdata = document.createCDATASection(attributeValue);
					attributeElement.appendChild(cdata);
				}
				
				StringBuffer sb = new StringBuffer();
				org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
				CmsLogger.logInfo("sb:" + sb);
				rolePropertiesVO.setValue(sb.toString());
				update(rolePropertiesVO.getLanguageId(), rolePropertiesVO.getContentTypeDefinitionId(), rolePropertiesVO);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * This method fetches a value from the xml that is the roleProperties Value. 
	 */
	 
	public String getAttributeValue(Integer rolePropertiesId, String attributeName, boolean escapeHTML) throws SystemException, Bug
	{
		String value = "";
		
		RolePropertiesVO rolePropertiesVO = getRolePropertiesVOWithId(rolePropertiesId);
		
		if(rolePropertiesVO != null)
		{	
			try
			{
				CmsLogger.logInfo("attributeName:" + attributeName);
				CmsLogger.logInfo("VersionValue:"  + rolePropertiesVO.getValue());
				InputSource inputSource = new InputSource(new StringReader(rolePropertiesVO.getValue()));
				
				DOMParser parser = new DOMParser();
				parser.parse(inputSource);
				Document document = parser.getDocument();
				
				NodeList nl = document.getDocumentElement().getChildNodes();
				Node n = nl.item(0);
				
				nl = n.getChildNodes();
				for(int i=0; i<nl.getLength(); i++)
				{
					n = nl.item(i);
					if(n.getNodeName().equalsIgnoreCase(attributeName))
					{
						if(n.getFirstChild() != null && n.getFirstChild().getNodeValue() != null)
						{
							value = n.getFirstChild().getNodeValue();
							CmsLogger.logInfo("Getting value: " + value);
							if(value != null && escapeHTML)
								value = new VisualFormatter().escapeHTML(value);
							break;
						}
					}
				}		        	
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		//CmsLogger.logInfo("value:" + value);	
		return value;
	}
	
	/**
	 * This is a method that gives the user back an newly initialized ValueObject for this entity that the controller
	 * is handling.
	 */

	public BaseEntityVO getNewVO()
	{
		return new RolePropertiesVO();
	}

}
 