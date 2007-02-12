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

package org.infoglue.cms.webservices;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.ServerNodeController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.UserPropertiesController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.content.DigitalAssetVO;
import org.infoglue.cms.entities.management.UserPropertiesVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.dom.DOMBuilder;
import org.infoglue.cms.webservices.elements.RemoteAttachment;
import org.infoglue.deliver.util.webservices.DynamicWebserviceSerializer;


/**
 * This class is responsible for letting an external application call InfoGlue
 * API:s remotely. It handles api:s to manage user properties.
 * 
 * @author Mattias Bogeblad
 */

public class RemoteUserPropertiesServiceImpl extends RemoteInfoGlueService
{
    private final static Logger logger = Logger.getLogger(RemoteUserPropertiesServiceImpl.class.getName());

	/**
	 * The principal executing the workflow.
	 */
	private InfoGluePrincipal principal;

    private static UserPropertiesController userPropertiesController = UserPropertiesController.getController();
    
    
    /**
     * Inserts a new UserProperty.
     */
    
    public int updateUserProperties(final String principalName, UserPropertiesVO userPropertiesVO) 
    {
        if(!ServerNodeController.getController().getIsIPAllowed(getRequest()))
        {
            logger.error("A client with IP " + getRequest().getRemoteAddr() + " was denied access to the webservice. Could be a hack attempt or you have just not configured the allowed IP-addresses correct.");
            return -1;
        }

        int newUserPropertiesId = 0;
        
        logger.info("***********************************************");
        logger.info("Creating user properties through webservice....");
        logger.info("***********************************************");
        
        try
        {
            initializePrincipal(principalName);
            UserPropertiesVO newUserPropertiesVO = userPropertiesController.update(userPropertiesVO.getLanguageId(), userPropertiesVO.getContentTypeDefinitionId(), userPropertiesVO);
            newUserPropertiesId = newUserPropertiesVO.getId().intValue();
        }
        catch(Exception e)
        {
            logger.error("En error occurred when we tried to create a new userProperty:" + e.getMessage(), e);
        }
        
        updateCaches();

        return newUserPropertiesId;
    }

    /**
     * Inserts a new UserProperty.
     */
    
    public Boolean updateUserProperties(final String principalName, int languageId, int contentTypeDefinitionId, final Object[] inputsArray) 
    {
        if(!ServerNodeController.getController().getIsIPAllowed(getRequest()))
        {
            logger.error("A client with IP " + getRequest().getRemoteAddr() + " was denied access to the webservice. Could be a hack attempt or you have just not configured the allowed IP-addresses correct.");
            return new Boolean(false);
        }
        
        Boolean status = new Boolean(true);

        int newUserPropertiesId = 0;
        
        logger.info("***********************************************");
        logger.info("Creating user properties through webservice....");
        logger.info("***********************************************");
        
        try
        {
			final DynamicWebserviceSerializer serializer = new DynamicWebserviceSerializer();
            Map userPropertiesAttributesMap = (Map)serializer.deserialize(inputsArray);
            
            initializePrincipal(principalName);
            logger.info("principalName:" + principalName);
            logger.info("principal:" + principal);
            logger.info("languageId:" + languageId);
            logger.info("contentTypeDefinitionId:" + contentTypeDefinitionId);
            
            UserPropertiesVO userPropertiesVO = new UserPropertiesVO();
            userPropertiesVO.setUserName(principal.getName());
            
            logger.info("userPropertiesAttributesMap:" + userPropertiesAttributesMap.size());
            
            DOMBuilder domBuilder = new DOMBuilder();
            Document document = domBuilder.createDocument();
            
            Element rootElement = domBuilder.addElement(document, "article");
            Element attributesRoot = domBuilder.addElement(rootElement, "attributes");
            
            Iterator attributesIterator = userPropertiesAttributesMap.keySet().iterator();
            while(attributesIterator.hasNext())
            {
                String attributeName  = (String)attributesIterator.next();
                String attributeValue = (String)userPropertiesAttributesMap.get(attributeName);
                
                logger.info(attributeName + "=" + attributeValue);
                
                Element attribute = domBuilder.addElement(attributesRoot, attributeName);
                domBuilder.addCDATAElement(attribute, attributeValue);
            }	                

            userPropertiesVO.setValue(document.asXML());

            
            UserPropertiesVO newUserPropertiesVO = userPropertiesController.update(new Integer(languageId), new Integer(contentTypeDefinitionId), userPropertiesVO);
            newUserPropertiesId = newUserPropertiesVO.getId().intValue();
        }
        catch(Throwable e)
        {
        	status = new Boolean(false);
            logger.error("En error occurred when we tried to create a new userProperty:" + e.getMessage(), e);
        }
        
        updateCaches();

        return status;
    }

    
    /**
     * Deletes a digital asset.
     */
    
    public Boolean deleteDigitalAsset(final String principalName, final Object[] inputsArray) 
    {
        if(!ServerNodeController.getController().getIsIPAllowed(getRequest()))
        {
            logger.error("A client with IP " + getRequest().getRemoteAddr() + " was denied access to the webservice. Could be a hack attempt or you have just not configured the allowed IP-addresses correct.");
            return new Boolean(false);
        }
        
        Boolean status = new Boolean(true);
        
        logger.info("****************************************");
        logger.info("Updating content through webservice....");
        logger.info("****************************************");
        
        logger.info("principalName:" + principalName);
        logger.info("inputsArray:" + inputsArray);
        //logger.warn("contents:" + contents);
        
        try
        {
			final DynamicWebserviceSerializer serializer = new DynamicWebserviceSerializer();
            Map digitalAsset = (Map) serializer.deserialize(inputsArray);
	        logger.info("digitalAsset:" + digitalAsset);

            initializePrincipal(principalName);
            
            Integer contentVersionId = (Integer)digitalAsset.get("contentVersionId");
            Integer contentId 		 = (Integer)digitalAsset.get("contentId");
            Integer languageId 		 = (Integer)digitalAsset.get("languageId");
            String assetKey 		 = (String)digitalAsset.get("assetKey");
            
            logger.info("contentVersionId:" + contentVersionId);
            logger.info("contentId:" + contentId);
            logger.info("languageId:" + languageId);
            logger.info("assetKey:" + assetKey);
            
            ContentVersionController.getContentVersionController().deleteDigitalAsset(contentId, languageId, assetKey);
               
	        logger.info("Done with contents..");

        }
        catch(Throwable e)
        {
	        status = new Boolean(false);	
            logger.error("En error occurred when we tried to delete a digitalAsset:" + e.getMessage(), e);
        }
        
        updateCaches();

        return status;
    }


    
	/**
	 * Checks if the principal exists and if the principal is allowed to create the workflow.
	 * 
	 * @param userName the name of the user.
	 * @param workflowName the name of the workflow to create.
	 * @throws SystemException if the principal doesn't exists or doesn't have permission to create the workflow.
	 */
	private void initializePrincipal(final String userName) throws SystemException 
	{
		try 
		{
			principal = UserControllerProxy.getController().getUser(userName);
		}
		catch(SystemException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new SystemException(e);
		}
		if(principal == null) 
		{
			throw new SystemException("No such principal [" + userName + "].");
		}
	}


}
