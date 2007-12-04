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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.infoglue.cms.controllers.kernel.impl.simple.CategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.ServerNodeController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.WorkflowDefinitionController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.CategoryVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.workflow.WorkflowDefinitionVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.sorters.ReflectionComparator;
import org.infoglue.deliver.util.webservices.DynamicWebserviceSerializer;


/**
 * This class is responsible for all deployment actions.
 * 
 * @author Mattias Bogeblad
 */

public class RemoteDeploymentServiceImpl extends RemoteInfoGlueService
{
    private final static Logger logger = Logger.getLogger(RemoteDeploymentServiceImpl.class.getName());

	/**
	 * The principal executing the workflow.
	 */
	private InfoGluePrincipal principal;

	private static ContentTypeDefinitionController contentTypeDefinitionController = ContentTypeDefinitionController.getController();
	private static WorkflowDefinitionController workflowDefinitionController = WorkflowDefinitionController.getController();
	private static CategoryController categoryController = CategoryController.getController();
    private static ContentControllerProxy contentControllerProxy = ContentControllerProxy.getController();
    //private static ContentVersionControllerProxy contentVersionControllerProxy = ContentVersionControllerProxy.getController();
    

	/**
     * Gets all content type definitions from the cms.
     */
    
    public List<ContentTypeDefinitionVO> getContentTypeDefinitions(final String principalName) 
    {
        if(!ServerNodeController.getController().getIsIPAllowed(getRequest()))
        {
            logger.error("A client with IP " + getRequest().getRemoteAddr() + " was denied access to the webservice. Could be a hack attempt or you have just not configured the allowed IP-addresses correct.");
            return null;
        }
        
        if(logger.isInfoEnabled())
        {
	        logger.info("******************************************************");
	        logger.info("* Getting content type definition through webservice *");
	        logger.info("******************************************************");
        }
	        
        List<ContentTypeDefinitionVO> contentTypeDefinitionVOList = new ArrayList<ContentTypeDefinitionVO>();
        
        try
        {
			final DynamicWebserviceSerializer serializer = new DynamicWebserviceSerializer();
	        
			if(logger.isInfoEnabled())
	        {
		        logger.info("principalName:" + principalName);
	        }
	        
            contentTypeDefinitionVOList = contentTypeDefinitionController.getContentTypeDefinitionVOList();
        }
        catch(Throwable t)
        {
            logger.error("En error occurred when we tried to get the contentVersionVO:" + t.getMessage(), t);
        }
        
        return contentTypeDefinitionVOList;
    }

	/**
     * Gets all content type definitions from the cms.
     */
    
    public Boolean updateContentTypeDefinitions(final String principalName, final Object[] input/*final Object[] inputsArray*//*final ContentTypeDefinitionVO contentTypeDefinitionVO */ /*final Object[] inputsArray, final String[] missingContentTypeNameArray, final Map requestParameters*/) 
    {
        if(!ServerNodeController.getController().getIsIPAllowed(getRequest()))
        {
            logger.error("A client with IP " + getRequest().getRemoteAddr() + " was denied access to the webservice. Could be a hack attempt or you have just not configured the allowed IP-addresses correct.");
            return new Boolean(false);
        }
        
    	Boolean status = new Boolean(true);

        if(logger.isInfoEnabled())
        {
	        logger.info("*******************************************************");
	        logger.info("* Updating content type definition through webservice *");
	        logger.info("*******************************************************");
        }
	        
        try
        {
        	System.out.println("input:" + input);
        	
        	final DynamicWebserviceSerializer serializer = new DynamicWebserviceSerializer();
        	Map arguments = (Map)serializer.deserialize(input);
        	
            System.out.println("arguments:" + arguments);
            
            
        	/*
        	System.out.println("inputsArray:" + inputsArray.length);

    		final DynamicWebserviceSerializer serializer = new DynamicWebserviceSerializer();
            List remoteContentTypeDefinitionVOList = (List)serializer.deserialize(inputsArray);

            System.out.println("remoteContentTypeDefinitionVOList:" + remoteContentTypeDefinitionVOList.size());
			*/
        	
        	//System.out.println("contentTypeDefinitionVO:" + contentTypeDefinitionVO);
        	
			if(logger.isInfoEnabled())
	        {
		        logger.info("principalName:" + principalName);
	        }
			//System.out.println("missingContentTypeNameArray:" + missingContentTypeNameArray);
	    	/*
			if(missingContentTypeNameArray != null)
	    	{
		    	for(int i=0; i<missingContentTypeNameArray.length; i++)
		    	{
		    		String missingContentTypeName = missingContentTypeNameArray[i];
		    		//System.out.println("Updating missingContentTypeName:" + missingContentTypeName);
		
		        	Iterator remoteContentTypeDefinitionVOListIterator = remoteContentTypeDefinitionVOList.iterator();
		        	while(remoteContentTypeDefinitionVOListIterator.hasNext())
		        	{
		        		ContentTypeDefinitionVO remoteContentTypeDefinitionVO = (ContentTypeDefinitionVO)remoteContentTypeDefinitionVOListIterator.next();
		        		//System.out.println("remoteContentTypeDefinitionVO:" + remoteContentTypeDefinitionVO.getName());
		        		if(remoteContentTypeDefinitionVO.getName().equals(missingContentTypeName))
		        		{
		        			ContentTypeDefinitionController.getController().create(remoteContentTypeDefinitionVO);
		        		}
		        	}
		    	}
	    	}
	    	*/
	    	
	    	//String[] deviatingContentTypeNameArray = (String[])requestParameters.get("deviatedContentTypeName");
			//System.out.println("deviatingContentTypeNameArray:" + deviatingContentTypeNameArray);
	    	/*
	    	if(deviatingContentTypeNameArray != null)
	    	{
		    	for(int i=0; i<deviatingContentTypeNameArray.length; i++)
		    	{
		    		String deviatingContentTypeName = deviatingContentTypeNameArray[i];
		    		//System.out.println("Updating deviatingContentTypeName:" + deviatingContentTypeName);
		
		        	Iterator remoteContentTypeDefinitionVOListIterator = remoteContentTypeDefinitionVOList.iterator();
		        	while(remoteContentTypeDefinitionVOListIterator.hasNext())
		        	{
		        		ContentTypeDefinitionVO remoteContentTypeDefinitionVO = (ContentTypeDefinitionVO)remoteContentTypeDefinitionVOListIterator.next();
		        		//System.out.println("remoteContentTypeDefinitionVO:" + remoteContentTypeDefinitionVO.getName());
		        		if(remoteContentTypeDefinitionVO.getName().equals(deviatingContentTypeName))
		        		{
		        			ContentTypeDefinitionVO localContentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithName(remoteContentTypeDefinitionVO.getName());
		        			String newSchemaValue = localContentTypeDefinitionVO.getSchemaValue();
		        			
		        	    	String[] attributeNameArray = (String[])requestParameters.get(deviatingContentTypeName + "_attributeName");
		        	    	//System.out.println("attributeNameArray:" + attributeNameArray);
		        	    	if(attributeNameArray != null)
		        	    	{
		        	    		for(int j=0; j<attributeNameArray.length; j++)
		        	    		{
			        	    		String attributeName = attributeNameArray[j];
			        	    		//System.out.println("  * Updating attributeName:" + attributeName);
			        			
				        			newSchemaValue = contentTypeDefinitionController.copyAttribute(remoteContentTypeDefinitionVO.getSchemaValue(), newSchemaValue, attributeName);
				        		}
		        	    	}	        			

		        	    	String[] categoryNameArray = (String[])requestParameters.get(deviatingContentTypeName + "_categoryName");
		        	    	//System.out.println("categoryNameArray:" + categoryNameArray);
		        	    	if(categoryNameArray != null)
		        	    	{
		        	    		for(int j=0; j<categoryNameArray.length; j++)
		        	    		{
			        	    		String categoryName = categoryNameArray[j];
			        	    		//System.out.println("  * Updating categoryName:" + categoryName);
			        			
				        			newSchemaValue = contentTypeDefinitionController.copyCategory(remoteContentTypeDefinitionVO.getSchemaValue(), newSchemaValue, categoryName);
				        		}
		        	    	}	

		        	    	String[] assetKeyArray = (String[])requestParameters.get(deviatingContentTypeName + "_assetKey");
		        	    	//System.out.println("assetKeyArray:" + assetKeyArray);
		        	    	if(assetKeyArray != null)
		        	    	{
		        	    		for(int j=0; j<assetKeyArray.length; j++)
		        	    		{
			        	    		String assetKey = assetKeyArray[j];
			        	    		//System.out.println("  * Updating assetKey:" + assetKey);
			        			
				        			newSchemaValue = contentTypeDefinitionController.copyAssetKey(remoteContentTypeDefinitionVO.getSchemaValue(), newSchemaValue, assetKey);
				        		}
		        	    	}
		        	    	
		        			localContentTypeDefinitionVO.setSchemaValue(newSchemaValue);
				        	ContentTypeDefinitionController.getController().update(localContentTypeDefinitionVO);
		        		}
		        	}
		    	}
	    	}
			*/
        }
        catch(Throwable t)
        {
        	status = new Boolean(false);
            logger.error("En error occurred when we tried to get the contentVersionVO:" + t.getMessage(), t);
        }
        
        updateCaches();

        return status;
    }

	/**
     * Gets all workflows from the cms.
     */
    
    public List<WorkflowDefinitionVO> getWorkflowDefinitions(final String principalName) 
    {
        if(!ServerNodeController.getController().getIsIPAllowed(getRequest()))
        {
            logger.error("A client with IP " + getRequest().getRemoteAddr() + " was denied access to the webservice. Could be a hack attempt or you have just not configured the allowed IP-addresses correct.");
            return null;
        }
        
        if(logger.isInfoEnabled())
        {
	        logger.info("******************************************************");
	        logger.info("*   Getting workflow definitions through webservice  *");
	        logger.info("******************************************************");
        }
	        
        List<WorkflowDefinitionVO> workflowDefinitionVOList = new ArrayList<WorkflowDefinitionVO>();
        
        try
        {
			final DynamicWebserviceSerializer serializer = new DynamicWebserviceSerializer();
	        
			if(logger.isInfoEnabled())
	        {
		        logger.info("principalName:" + principalName);
	        }
	        
			workflowDefinitionVOList = workflowDefinitionController.getWorkflowDefinitionVOList();
        }
        catch(Throwable t)
        {
            logger.error("En error occurred when we tried to get the contentVersionVO:" + t.getMessage(), t);
        }
        
        return workflowDefinitionVOList;
    }

	/**
     * Gets all categories from the cms.
     */
    
    public List<CategoryVO> getAllActiveCategories(final String principalName) 
    {
        if(!ServerNodeController.getController().getIsIPAllowed(getRequest()))
        {
            logger.error("A client with IP " + getRequest().getRemoteAddr() + " was denied access to the webservice. Could be a hack attempt or you have just not configured the allowed IP-addresses correct.");
            return null;
        }
        
        if(logger.isInfoEnabled())
        {
	        logger.info("******************************************");
	        logger.info("*   Getting categies through webservice  *");
	        logger.info("******************************************");
        }
	        
        List<CategoryVO> categoryVOList = new ArrayList<CategoryVO>();
        
        try
        {
			final DynamicWebserviceSerializer serializer = new DynamicWebserviceSerializer();
	        
			if(logger.isInfoEnabled())
	        {
		        logger.info("principalName:" + principalName);
	        }
	        
			categoryVOList = categoryController.findAllActiveCategories(true);
        }
        catch(Throwable t)
        {
            logger.error("En error occurred when we tried to get the contentVersionVO:" + t.getMessage(), t);
        }
        
        return categoryVOList;
    }

    
	/**
     * Gets all component contents from the cms.
     */
    
    public List<ContentVO> getComponents(final String principalName) 
    {
        if(!ServerNodeController.getController().getIsIPAllowed(getRequest()))
        {
            logger.error("A client with IP " + getRequest().getRemoteAddr() + " was denied access to the webservice. Could be a hack attempt or you have just not configured the allowed IP-addresses correct.");
            return null;
        }
        
        if(logger.isInfoEnabled())
        {
	        logger.info("******************************************************");
	        logger.info("*        Getting components through webservice       *");
	        logger.info("******************************************************");
        }
	        
        List<ContentVO> contentVOList = new ArrayList<ContentVO>();
        
        try
        {
			final DynamicWebserviceSerializer serializer = new DynamicWebserviceSerializer();
	        
			if(logger.isInfoEnabled())
	        {
		        logger.info("principalName:" + principalName);
	        }
	        			
			contentVOList = contentControllerProxy.getContentVOWithContentTypeDefinition("HTMLTemplate");
			Iterator contentVOListIterator = contentVOList.iterator();
			while(contentVOListIterator.hasNext())
			{
				ContentVO contentVO = (ContentVO)contentVOListIterator.next(); 
				
				LanguageVO languageVO = LanguageController.getController().getMasterLanguage(contentVO.getRepositoryId());
				
				String fullPath = ContentController.getContentController().getContentPath(contentVO.getId(), true, true);
				
				ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(contentVO.getId(), languageVO.getId());
				if(contentVersionVO != null)
				{
					contentVO.setVersions(new String[]{contentVersionVO.getVersionValue()});
				}
				
				contentVO.setFullPath(fullPath);
			}
        }
        catch(Throwable t)
        {
            logger.error("En error occurred when we tried to get the contents:" + t.getMessage(), t);
        }
        
	    Collections.sort(contentVOList, Collections.reverseOrder(new ReflectionComparator("modifiedDateTime")));
        
        return contentVOList;
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
