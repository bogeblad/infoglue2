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

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Node;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.entities.content.DigitalAsset;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.management.RoleProperties;
import org.infoglue.cms.entities.management.UserProperties;
import org.infoglue.cms.entities.structure.SiteNode;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.AuthorizationModule;
import org.infoglue.cms.security.InfoGlueAuthenticationFilter;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.security.InfoGlueRole;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.dom.DOMBuilder;
import org.infoglue.deliver.controllers.kernel.impl.simple.DigitalAssetDeliveryController;
import org.infoglue.deliver.controllers.kernel.impl.simple.LanguageDeliveryController;

/**
 * @author Mattias Bogeblad
 * 
 * This class acts as the proxy for getting a principal from the right source. The source may vary depending
 * on security setup.
 */

public class InfoGluePrincipalControllerProxy extends BaseController 
{

	public static InfoGluePrincipalControllerProxy getController()
	{
		return new InfoGluePrincipalControllerProxy();
	}
	
	/**
	 * This method returns a specific content-object
	 */
	
    public InfoGluePrincipal getInfoGluePrincipal(String userName) throws ConstraintException, SystemException
    {
		InfoGluePrincipal infoGluePrincipal = null;
    	
    	try
    	{
			AuthorizationModule authorizationModule = (AuthorizationModule)Class.forName(InfoGlueAuthenticationFilter.authorizerClass).newInstance();
			authorizationModule.setExtraProperties(InfoGlueAuthenticationFilter.extraProperties);
			
			infoGluePrincipal = authorizationModule.getAuthorizedInfoGluePrincipal(userName);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return infoGluePrincipal;
    }
 
    
	/**
	 * Getting a property for a Principal - used for personalisation. 
	 * This method starts with getting the property on the user and if it does not exist we check out the
	 * group-properties as well.
	 */
	
	public String getPrincipalPropertyValue(InfoGluePrincipal infoGluePrincipal, String propertyName, Integer languageId, Integer siteNodeId, boolean useLanguageFallback, boolean escapeSpecialCharacters) throws Exception
	{
		String value = "";
		
		if(infoGluePrincipal == null || propertyName == null)
			return null;
		
		Database db = CastorDatabaseService.getDatabase();
		
		beginTransaction(db);
		
		try
        {
		    Collection userPropertiesList = UserPropertiesController.getController().getUserPropertiesList(infoGluePrincipal.getName(), languageId, db);
			//CmsLogger.logInfo("userProperties:" + userPropertiesList.size());
			Iterator userPropertiesListIterator = userPropertiesList.iterator();
			while(userPropertiesListIterator.hasNext())
			{
				UserProperties userProperties = (UserProperties)userPropertiesListIterator.next();
				//CmsLogger.logInfo("userProperties:" + userProperties.getValue());
				//CmsLogger.logInfo("propertyName:" + propertyName);
	
				if(userProperties != null && userProperties.getLanguage().getLanguageId().equals(languageId) && userProperties.getValue() != null && propertyName != null)
				{
					String propertyXML = userProperties.getValue();
					DOMBuilder domBuilder = new DOMBuilder();
					Document document = domBuilder.getDocument(propertyXML);
		
					Node node = document.getRootElement().selectSingleNode("attributes/" + propertyName);
					if(node != null)
					{
						//CmsLogger.logInfo("node:" + node.asXML());
						value = node.getStringValue();
						//CmsLogger.logInfo("value:" + value);
						CmsLogger.logInfo("Getting value: " + value);
						if(value != null && escapeSpecialCharacters)
							value = new VisualFormatter().escapeHTML(value);
						break;
					}
				}
			}
			
			if(value.equals(""))
			{	
				//CmsLogger.logInfo("infoGluePrincipal:" + infoGluePrincipal.getName());
				List roles = infoGluePrincipal.getRoles();
				//CmsLogger.logInfo("roles:" + roles.size());
				Iterator rolesIterator = roles.iterator();
				while(rolesIterator.hasNext())
				{
					InfoGlueRole role = (InfoGlueRole)rolesIterator.next();
					//CmsLogger.logInfo("role:" + role.getName());
					
					Collection rolePropertiesList = RolePropertiesController.getController().getRolePropertiesList(role.getName(), languageId, db);
					//CmsLogger.logInfo("roleProperties:" + rolePropertiesList.size());
					Iterator rolePropertiesListIterator = rolePropertiesList.iterator();
					while(rolePropertiesListIterator.hasNext())
					{
						RoleProperties roleProperties = (RoleProperties)rolePropertiesListIterator.next();
						//CmsLogger.logInfo("roleProperties:" + roleProperties.getValue());
						//CmsLogger.logInfo("propertyName:" + propertyName);
						
						if(roleProperties != null && roleProperties.getLanguage().getLanguageId().equals(languageId) && roleProperties.getValue() != null && propertyName != null)
						{
							String propertyXML = roleProperties.getValue();
							DOMBuilder domBuilder = new DOMBuilder();
							Document document = domBuilder.getDocument(propertyXML);
							
							Node node = document.getRootElement().selectSingleNode("attributes/" + propertyName);
							if(node != null)
							{
								//CmsLogger.logInfo("node:" + node.asXML());
								value = node.getStringValue();
								//CmsLogger.logInfo("value:" + value);
								CmsLogger.logInfo("Getting value: " + value);
								if(value != null && escapeSpecialCharacters)
									value = new VisualFormatter().escapeHTML(value);
								break;
							}
						}
					}
										
				}
				
				if(value.equals("") && useLanguageFallback)
				{
					LanguageVO masterLanguageVO = LanguageDeliveryController.getLanguageDeliveryController().getMasterLanguageForSiteNode(db, siteNodeId);
					if(!masterLanguageVO.getLanguageId().equals(languageId))
						return getPrincipalPropertyValue(infoGluePrincipal, propertyName, masterLanguageVO.getLanguageId(), siteNodeId, useLanguageFallback, escapeSpecialCharacters);
				}
			}
			
		    commitTransaction(db);
        }
        catch(Exception e)
        {
        	CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }		
        	
		return value;
	}	
	
	
	
	/**
	 * Getting a property for a Principal - used for personalisation. 
	 * This method starts with getting the property on the user and if it does not exist we check out the
	 * group-properties as well. The value in question is a map - name-value.
	 */
	
	public Map getPrincipalPropertyHashValues(InfoGluePrincipal infoGluePrincipal, String propertyName, Integer languageId, Integer siteNodeId, boolean useLanguageFallback, boolean escapeSpecialCharacters) throws Exception
	{
		Properties properties = new Properties();
		
		String attributeValue = getPrincipalPropertyValue(infoGluePrincipal, propertyName, languageId, siteNodeId, useLanguageFallback, escapeSpecialCharacters);
		
		ByteArrayInputStream is = new ByteArrayInputStream(attributeValue.getBytes("UTF-8"));

		properties.load(is);
        
		return properties;
	}	
    
	public BaseEntityVO getNewVO()
	{
		return null;
	}
}
