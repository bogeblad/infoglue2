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

package org.infoglue.cms.applications.managementtool.actions.deployment;

import java.io.IOException;
import java.io.StringReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.xml.namespace.QName;

import org.apache.xerces.parsers.DOMParser;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.applications.databeans.AssetKeyDefinition;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.CategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.GroupControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.RoleControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.WorkflowDefinitionController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.Category;
import org.infoglue.cms.entities.management.CategoryAttribute;
import org.infoglue.cms.entities.management.CategoryVO;
import org.infoglue.cms.entities.management.ContentTypeAttribute;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.workflow.WorkflowDefinition;
import org.infoglue.cms.entities.workflow.WorkflowDefinitionVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.mail.MailServiceFactory;
import org.infoglue.cms.util.sorters.ReflectionComparator;
import org.infoglue.deliver.util.webservices.DynamicWebservice;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ViewDeploymentSynchronizeServersAction extends InfoGlueAbstractAction
{
	private static final long serialVersionUID = 1L;
	
	private boolean synchronizeContentTypeDefinitions;
	private boolean synchronizeCategories;
	private boolean synchronizeWorkflows;
	private boolean synchronizeComponents;
	
	private Integer deploymentServerIndex = null;
	private List<DeploymentCompareBean> deviatingContentTypes = new ArrayList<DeploymentCompareBean>();
	private List<DeploymentCompareBean> deviatingCategoryVOList = new ArrayList<DeploymentCompareBean>();
	private List<DeploymentCompareBean> deviatingWorkflows = new ArrayList<DeploymentCompareBean>();
	private List<DeploymentCompareBean> deviatingContents = new ArrayList<DeploymentCompareBean>();
	private List<DeploymentCompareBean> deviatingSiteNodes = new ArrayList<DeploymentCompareBean>();
	
    public String doInput() throws Exception
    {
    	List<String> deploymentServers = CmsPropertyHandler.getDeploymentServers();
    	String deploymentServerUrl = deploymentServers.get(deploymentServerIndex);
    	
    	String targetEndpointAddress = deploymentServerUrl + "/services/RemoteDeploymentService";
    	
    	if(synchronizeContentTypeDefinitions)
    	{
	    	Object[] contentTypeDefinitionVOArray = (Object[])invokeOperation(targetEndpointAddress, "getContentTypeDefinitions", "contentTypeDefinition", null, ContentTypeDefinitionVO.class, "infoglue");
	    	List remoteContentTypeDefinitionVOList = Arrays.asList(contentTypeDefinitionVOArray);
		    Collections.sort(remoteContentTypeDefinitionVOList, new ReflectionComparator("name"));
	
	    	//System.out.println("remoteContentTypeDefinitionVOList:" + remoteContentTypeDefinitionVOList.size());
	    	
	    	Iterator remoteContentTypeDefinitionVOListIterator = remoteContentTypeDefinitionVOList.iterator();
	    	while(remoteContentTypeDefinitionVOListIterator.hasNext())
	    	{
	    		ContentTypeDefinitionVO remoteContentTypeDefinitionVO = (ContentTypeDefinitionVO)remoteContentTypeDefinitionVOListIterator.next();
	    		//System.out.println("remoteContentTypeDefinitionVO:" + remoteContentTypeDefinitionVO.getName());
	    		ContentTypeDefinitionVO localContentTypeDefinitionVO = (ContentTypeDefinitionVO)ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithName(remoteContentTypeDefinitionVO.getName());
	    		DeploymentCompareBean bean = new DeploymentCompareBean();
	    		bean.setRemoteVersion(remoteContentTypeDefinitionVO);
	    		if(localContentTypeDefinitionVO != null)
	    		{
	    			//System.out.println("localContentTypeDefinitionVO:" + localContentTypeDefinitionVO.getName());
	        		bean.setLocalVersion(localContentTypeDefinitionVO);    			
	    		}
	    		deviatingContentTypes.add(bean);
	    	}
    	}
    	
    	if(synchronizeCategories)
    	{
	    	//Getting deviatingCategories
	    	Object[] categoryVOArray = (Object[])invokeOperation(targetEndpointAddress, "getAllActiveCategories", "category", null, CategoryVO.class, "infoglue");
	    	List remoteCategoryVOList = Arrays.asList(categoryVOArray);
		    Collections.sort(remoteCategoryVOList, new ReflectionComparator("name"));
		    //System.out.println("remoteCategoryVOList:" + remoteCategoryVOList.size());
	    	
		    List<CategoryVO> allLocalCategories = CategoryController.getController().findAllActiveCategories(true);
		    //System.out.println("allLocalCategories:" + allLocalCategories.size());
	    	
		    compareCategoryLists(remoteCategoryVOList, allLocalCategories);
	
		    //System.out.println("deviatingCategoryVOList:" + deviatingCategoryVOList.size());
    	}
    	
    	if(synchronizeWorkflows)
    	{
	    	//Getting deviatingWorkflows
	    	Object[] workflowVOArray = (Object[])invokeOperation(targetEndpointAddress, "getWorkflowDefinitions", "workflowDefinition", null, WorkflowDefinitionVO.class, "infoglue");
	    	List remoteWorkflowDefinitionVOList = Arrays.asList(workflowVOArray);
		    Collections.sort(remoteWorkflowDefinitionVOList, new ReflectionComparator("name"));
	
		    //System.out.println("remoteWorkflowDefinitionVOList:" + remoteWorkflowDefinitionVOList.size());
	    	
	    	Iterator remoteWorkflowDefinitionVOListIterator = remoteWorkflowDefinitionVOList.iterator();
	    	while(remoteWorkflowDefinitionVOListIterator.hasNext())
	    	{
	    		WorkflowDefinitionVO remoteWorkflowDefinitionVO = (WorkflowDefinitionVO)remoteWorkflowDefinitionVOListIterator.next();
	    		//System.out.println("remoteWorkflowDefinitionVO:" + remoteWorkflowDefinitionVO.getName());
	    		WorkflowDefinitionVO localWorkflowDefinitionVO = (WorkflowDefinitionVO)WorkflowDefinitionController.getController().getWorkflowDefinitionVOWithName(remoteWorkflowDefinitionVO.getName());
	    		//System.out.println("localWorkflowDefinitionVO:" + localWorkflowDefinitionVO);
	    		DeploymentCompareBean bean = new DeploymentCompareBean();
	    		bean.setRemoteVersion(remoteWorkflowDefinitionVO);
	    		if(localWorkflowDefinitionVO != null)
	    		{
	    			//System.out.println("localWorkflowDefinitionVO:" + localWorkflowDefinitionVO.getName());
	        		bean.setLocalVersion(localWorkflowDefinitionVO);    			
	    		}
	    		deviatingWorkflows.add(bean);
	    	}
    	}
    	
    	//System.out.println("synchronizeComponents:" + synchronizeComponents);
    	if(synchronizeComponents)
    	{
	    	//Getting deviatingComponents
	    	Object[] contentVOArray = (Object[])invokeOperation(targetEndpointAddress, "getComponents", "content", null, ContentVO.class, "infoglue");
	    	List remoteContentVOList = Arrays.asList(contentVOArray);
		    //Collections.sort(remoteContentVOList, new ReflectionComparator("name"));
		    //Collections.sort(remoteContentVOList, Collections.reverseOrder(new ReflectionComparator("modifiedDateTime")));

	    	//System.out.println("remoteContentVOList:" + remoteContentVOList.size());
	    	
	    	List components = ContentController.getContentController().getContentVOWithContentTypeDefinition("HTMLTemplate");
	    	
	    	Iterator remoteContentVOListIterator = remoteContentVOList.iterator();
	    	while(remoteContentVOListIterator.hasNext())
	    	{
	    		ContentVO remoteContentVO = (ContentVO)remoteContentVOListIterator.next();
	    		//System.out.println("remoteContentVO:" + remoteContentVO.getName());
	    		
	    		Iterator componentsIterator = components.iterator();
	    		ContentVO localContentVO = null;
	    		while(componentsIterator.hasNext())
	    		{
	    			ContentVO candidate = (ContentVO)componentsIterator.next();
	    			if(candidate.getName().equals(remoteContentVO.getName()))
	    			{
	    				localContentVO = candidate;
	    			}
	    		}
	    		//System.out.println("localContentVO:" + localContentVO);
	    		DeploymentCompareBean bean = new DeploymentCompareBean();
	    		bean.setRemoteVersion(remoteContentVO);
	    		if(localContentVO != null)
	    		{
	    			//System.out.println("localContentVO:" + localContentVO.getName());
	        		bean.setLocalVersion(localContentVO);
	    		
					LanguageVO languageVO = LanguageController.getController().getMasterLanguage(localContentVO.getRepositoryId());

					//System.out.println("languageVO:" + languageVO);
					ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(localContentVO.getId(), languageVO.getId());
					//System.out.println("contentVersionVO:" + contentVersionVO);
					if(contentVersionVO != null)
					{
						localContentVO.setVersions(new String[]{contentVersionVO.getVersionValue()});
					}
	    		}
	    		/*
	    		else
	    		{
	    			System.out.println("No localversion for:" + remoteContentVO.getName() + ":" + remoteContentVO.getExtraProperties() + ":" + remoteContentVO.getFullPath());
	    		}
	    		*/
        		
	    		//System.out.println("bean:" + bean.getLocalVersion());
	    		deviatingContents.add(bean);
	    	}
    	}

    	return "input";
    }

    private void compareCategoryLists(List remoteCategoryVOList, List<CategoryVO> allLocalCategories)
	{
    	Iterator remoteCategoryVOListIterator = remoteCategoryVOList.iterator();
    	while(remoteCategoryVOListIterator.hasNext())
    	{
    		CategoryVO remoteCategoryVO = (CategoryVO)remoteCategoryVOListIterator.next();
    		//System.out.println("remoteCategoryVO:" + remoteCategoryVO.getName());
    		
    		boolean categoryExists = false;
    		CategoryVO localCategoryVO = null;
    		Iterator allLocalCategoriesIterator = allLocalCategories.iterator();
    		while(allLocalCategoriesIterator.hasNext())
        	{
        		localCategoryVO = (CategoryVO)allLocalCategoriesIterator.next();
        		//System.out.println("remoteCategoryVO:" + remoteCategoryVO.getName());
        		if(localCategoryVO.getName().equals(remoteCategoryVO.getName()))
        		{
        			categoryExists = true;
        			break;
        		}
        	}
        	
        	if(!categoryExists)
        	{
        		DeploymentCompareBean bean = new DeploymentCompareBean();
	    		bean.setRemoteVersion(remoteCategoryVO);
	    		deviatingCategoryVOList.add(bean);
        	}
        	
        	if(remoteCategoryVO.getChildren() != null && remoteCategoryVO.getChildren().size() > 0)
        	{
        		if(localCategoryVO != null)
        			compareCategoryLists(remoteCategoryVO.getChildren(), localCategoryVO.getChildren());
        		else
        			compareCategoryLists(remoteCategoryVO.getChildren(), new ArrayList());
        	}
        }
    }
    
    private void compareAndCompleteCategoryLists(List remoteCategoryVOList, List<CategoryVO> allLocalCategories, CategoryVO localParentCategory, Map handledRemoteCategoryPaths) throws SystemException
	{
    	Iterator remoteCategoryVOListIterator = remoteCategoryVOList.iterator();
    	while(remoteCategoryVOListIterator.hasNext())
    	{
    		CategoryVO remoteCategoryVO = (CategoryVO)remoteCategoryVOListIterator.next();
    		//System.out.println("remoteCategoryVO:" + remoteCategoryVO.getName());
    		
    		boolean categoryExists = false;
    		CategoryVO localCategoryVO = null;
    		Iterator allLocalCategoriesIterator = allLocalCategories.iterator();
    		while(allLocalCategoriesIterator.hasNext())
        	{
        		localCategoryVO = (CategoryVO)allLocalCategoriesIterator.next();
        		//System.out.println("remoteCategoryVO:" + remoteCategoryVO.getName());
        		if(localCategoryVO.getName().equals(remoteCategoryVO.getName()))
        		{
        			categoryExists = true;
        			break;
        		}
        	}
        	
    		boolean skipLocalCategory = false;
        	if(!categoryExists)
        	{
        		Database db = CastorDatabaseService.getDatabase();
        		
        		try
        		{
        			beginTransaction(db);
        			
        			//System.out.println("Category:" + remoteCategoryVO.getId() + "-" + remoteCategoryVO.getName() + " (" + remoteCategoryVO.getCategoryPath() + ") did not exist.");
        			String remoteParentPath = remoteCategoryVO.getCategoryPath();
        			//System.out.println("remoteParentPath:" + remoteParentPath);

            		String isCategorySelected = this.getRequest().getParameter(remoteParentPath + "_transfer");
            		//System.out.println("isCategorySelected:[" + isCategorySelected + "]");
            		if(isCategorySelected != null && isCategorySelected.equals("true"))
            		{
            			//System.out.println("Before - remoteCategoryVO:" + remoteCategoryVO);
            			skipLocalCategory = true;
            			handleSubCategories(localParentCategory, remoteCategoryVO, handledRemoteCategoryPaths, db);
            			
            		}
            		
        			commitTransaction(db);
        		}
        		catch (Exception e) 
        		{
        			e.printStackTrace();
        			rollbackTransaction(db);
        		}
        	}
        	
        	if(remoteCategoryVO.getChildren() != null && remoteCategoryVO.getChildren().size() > 0)
        	{
        		if(localCategoryVO != null && !skipLocalCategory)
        		{
        			compareAndCompleteCategoryLists(remoteCategoryVO.getChildren(), localCategoryVO.getChildren(), localCategoryVO, handledRemoteCategoryPaths);
        		}
        		else
        		{
        			compareAndCompleteCategoryLists(remoteCategoryVO.getChildren(), new ArrayList(), null, handledRemoteCategoryPaths);
        		}
        	}
        }
    }
    
    public void handleSubCategories(CategoryVO localParentCategory, CategoryVO remoteCategoryVO, Map handledRemoteCategoryPaths, Database db) throws SystemException
    {
    	//System.out.println("\n");
		String isCategorySelected = this.getRequest().getParameter(remoteCategoryVO.getCategoryPath() + "_transfer");
		//System.out.println("isCategorySelected:[" + isCategorySelected + "]");
		if(isCategorySelected != null && isCategorySelected.equals("true"))
		{
			if(handledRemoteCategoryPaths.containsKey(remoteCategoryVO.getCategoryPath()))
			{
				//System.out.println("Category with path " + remoteCategoryVO.getCategoryPath() + " was allready handled.");
			}
			else
			{
				//System.out.println("Creating local version of:" + remoteCategoryVO + " under " + localParentCategory);
				remoteCategoryVO.setCategoryId(null);
				if(localParentCategory != null)
					remoteCategoryVO.setParentId(localParentCategory.getId());
				
				Category newLocalCategory = CategoryController.getController().save(remoteCategoryVO, db);
				handledRemoteCategoryPaths.put(remoteCategoryVO.getCategoryPath(), "true");
				//if(localParentCategory != null)
				//	localParentCategory.getChildren().add(newLocalCategory.getValueObject());

		    	List subCategories = remoteCategoryVO.getChildren();
		    	Iterator subCategoriesIterator = subCategories.iterator();
		    	while(subCategoriesIterator.hasNext())
		    	{
		    		CategoryVO subCategory = (CategoryVO)subCategoriesIterator.next();
		    		//System.out.println("subCategory:[" + subCategory + "]");
		    		handleSubCategories(newLocalCategory.getValueObject(), subCategory, handledRemoteCategoryPaths, db);
		    	}
			}
		}
    }
    
	public String doUpdateContentTypes() throws Exception
    {
    	List<String> deploymentServers = CmsPropertyHandler.getDeploymentServers();
    	String deploymentServerUrl = deploymentServers.get(deploymentServerIndex);
    	
    	//System.out.println("Fetching sync info from deploymentServerUrl:" + deploymentServerUrl);
    	
    	String targetEndpointAddress = deploymentServerUrl + "/services/RemoteDeploymentService";
    	//System.out.println("targetEndpointAddress:" + targetEndpointAddress);
    	
    	Object[] contentTypeDefinitionVOArray = (Object[])invokeOperation(targetEndpointAddress, "getContentTypeDefinitions", "contentTypeDefinition", null, ContentTypeDefinitionVO.class, "infoglue");
    	List remoteContentTypeDefinitionVOList = Arrays.asList(contentTypeDefinitionVOArray);
	    Collections.sort(remoteContentTypeDefinitionVOList, new ReflectionComparator("name"));

	    //System.out.println("remoteContentTypeDefinitionVOList:" + remoteContentTypeDefinitionVOList.size());

    	String[] missingContentTypeNameArray = this.getRequest().getParameterValues("missingContentTypeName");
    	//System.out.println("missingContentTypeNameArray:" + missingContentTypeNameArray);
    	
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
    	
    	String[] deviatingContentTypeNameArray = this.getRequest().getParameterValues("deviatedContentTypeName");
    	//System.out.println("deviatingContentTypeNameArray:" + deviatingContentTypeNameArray);
    	
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
	        			
	        	    	String[] attributeNameArray = this.getRequest().getParameterValues(deviatingContentTypeName + "_attributeName");
	        	    	//System.out.println("attributeNameArray:" + attributeNameArray);
	        	    	if(attributeNameArray != null)
	        	    	{
	        	    		for(int j=0; j<attributeNameArray.length; j++)
	        	    		{
		        	    		String attributeName = attributeNameArray[j];
		        	    		//System.out.println("  * Updating attributeName:" + attributeName);
		        			
			        			newSchemaValue = copyAttribute(remoteContentTypeDefinitionVO.getSchemaValue(), newSchemaValue, attributeName);
			        		}
	        	    	}	        			

	        	    	String[] categoryNameArray = this.getRequest().getParameterValues(deviatingContentTypeName + "_categoryName");
	        	    	//System.out.println("categoryNameArray:" + categoryNameArray);
	        	    	if(categoryNameArray != null)
	        	    	{
	        	    		for(int j=0; j<categoryNameArray.length; j++)
	        	    		{
		        	    		String categoryName = categoryNameArray[j];
		        	    		//System.out.println("  * Updating categoryName:" + categoryName);
		        			
			        			newSchemaValue = copyCategory(remoteContentTypeDefinitionVO.getSchemaValue(), newSchemaValue, categoryName);
			        		}
	        	    	}	

	        	    	String[] assetKeyArray = this.getRequest().getParameterValues(deviatingContentTypeName + "_assetKey");
	        	    	//System.out.println("assetKeyArray:" + assetKeyArray);
	        	    	if(assetKeyArray != null)
	        	    	{
	        	    		for(int j=0; j<assetKeyArray.length; j++)
	        	    		{
		        	    		String assetKey = assetKeyArray[j];
		        	    		//System.out.println("  * Updating assetKey:" + assetKey);
		        			
			        			newSchemaValue = copyAssetKey(remoteContentTypeDefinitionVO.getSchemaValue(), newSchemaValue, assetKey);
			        		}
	        	    	}
	        	    	
	        			localContentTypeDefinitionVO.setSchemaValue(newSchemaValue);
			        	ContentTypeDefinitionController.getController().update(localContentTypeDefinitionVO);
	        		}
	        	}
	    	}
    	}
    	

    	return doInput();
    }

    public String doUpdateCategories() throws Exception
    {
    	//System.out.println("*****************************");
    	//System.out.println("*    UPDATING CATEGORIES    *");
    	//System.out.println("*****************************");
    	
    	List<String> deploymentServers = CmsPropertyHandler.getDeploymentServers();
    	String deploymentServerUrl = deploymentServers.get(deploymentServerIndex);
    	
    	//System.out.println("Fetching sync info from deploymentServerUrl:" + deploymentServerUrl);
    	
    	String targetEndpointAddress = deploymentServerUrl + "/services/RemoteDeploymentService";
    	//System.out.println("targetEndpointAddress:" + targetEndpointAddress);
    	
    	Object[] categoryVOArray = (Object[])invokeOperation(targetEndpointAddress, "getAllActiveCategories", "category", null, CategoryVO.class, "infoglue");
    	List remoteCategoryVOList = Arrays.asList(categoryVOArray);
	    Collections.sort(remoteCategoryVOList, new ReflectionComparator("name"));
	    //System.out.println("remoteCategoryVOList:" + remoteCategoryVOList.size());
    	
	    List<CategoryVO> allLocalCategories = CategoryController.getController().findAllActiveCategories();
	    //System.out.println("allLocalCategories:" + allLocalCategories.size());
    	
	    Map handledRemoteCategoryPaths = new HashMap();
	    
	    compareAndCompleteCategoryLists(remoteCategoryVOList, allLocalCategories, null, handledRemoteCategoryPaths);

	    //System.out.println("deviatingCategoryVOList:" + deviatingCategoryVOList.size());
    	
    	//return NONE;
    	return doInput();
    }

    public String doUpdateWorkflows() throws Exception
    {
    	List<String> deploymentServers = CmsPropertyHandler.getDeploymentServers();
    	String deploymentServerUrl = deploymentServers.get(deploymentServerIndex);
    	
    	//System.out.println("Fetching sync info from deploymentServerUrl:" + deploymentServerUrl);
    	
    	String targetEndpointAddress = deploymentServerUrl + "/services/RemoteDeploymentService";
    	//System.out.println("targetEndpointAddress:" + targetEndpointAddress);
    	
    	Object[] workflowDefinitionVOArray = (Object[])invokeOperation(targetEndpointAddress, "getWorkflowDefinitions", "workflowDefinition", null, WorkflowDefinitionVO.class, "infoglue");
    	List remoteWorkflowDefinitionVOList = Arrays.asList(workflowDefinitionVOArray);
	    Collections.sort(remoteWorkflowDefinitionVOList, new ReflectionComparator("name"));

	    //System.out.println("remoteWorkflowDefinitionVOList:" + remoteWorkflowDefinitionVOList.size());

    	String[] missingWorkflowDefinitionNameArray = this.getRequest().getParameterValues("missingWorkflowDefinitionName");
    	//System.out.println("missingWorkflowDefinitionNameArray:" + missingWorkflowDefinitionNameArray);
    	
    	if(missingWorkflowDefinitionNameArray != null)
    	{
	    	for(int i=0; i<missingWorkflowDefinitionNameArray.length; i++)
	    	{
	    		String missingWorkflowDefinitionName = missingWorkflowDefinitionNameArray[i];
	    		//System.out.println("Updating missingWorkflowDefinitionName:" + missingWorkflowDefinitionName);
	
	        	Iterator remoteWorkflowDefinitionVOListIterator = remoteWorkflowDefinitionVOList.iterator();
	        	while(remoteWorkflowDefinitionVOListIterator.hasNext())
	        	{
	        		WorkflowDefinitionVO remoteWorkflowDefinitionVO = (WorkflowDefinitionVO)remoteWorkflowDefinitionVOListIterator.next();
	        		//System.out.println("remoteContentTypeDefinitionVO:" + remoteContentTypeDefinitionVO.getName());
	        		if(remoteWorkflowDefinitionVO.getName().equals(missingWorkflowDefinitionName))
	        		{
	        			WorkflowDefinitionController.getController().create(remoteWorkflowDefinitionVO);
	        		}
	        	}
	    	}
    	}

    	return doInput();
    }

    public String doUpdateContents() throws Exception
    {
    	List<String> deploymentServers = CmsPropertyHandler.getDeploymentServers();
    	String deploymentServerUrl = deploymentServers.get(deploymentServerIndex);
    	
    	//System.out.println("Fetching sync info from deploymentServerUrl:" + deploymentServerUrl);
    	
    	String targetEndpointAddress = deploymentServerUrl + "/services/RemoteDeploymentService";
    	//System.out.println("targetEndpointAddress:" + targetEndpointAddress);
    	
    	Object[] contentVOArray = (Object[])invokeOperation(targetEndpointAddress, "getContents", "content", null, ContentVO.class, "infoglue");
    	List remoteContentVOList = Arrays.asList(contentVOArray);
	    Collections.sort(remoteContentVOList, new ReflectionComparator("name"));

	    //System.out.println("remoteContentVOList:" + remoteContentVOList.size());

    	String[] deviatingContentNameArray = this.getRequest().getParameterValues("deviatingContentName");
    	//System.out.println("deviatingContentNameArray:" + deviatingContentNameArray);
    	
    	List components = ContentController.getContentController().getContentVOWithContentTypeDefinition("HTMLTemplate");
    	
    	if(deviatingContentNameArray != null)
    	{
	    	for(int i=0; i<deviatingContentNameArray.length; i++)
	    	{
	    		String deviatingContentName = deviatingContentNameArray[i];
	    		//System.out.println("Updating deviatingContentName:" + deviatingContentName);
	
	        	Iterator remoteContentVOListIterator = remoteContentVOList.iterator();
	        	while(remoteContentVOListIterator.hasNext())
	        	{
	        		ContentVO remoteContentVO = (ContentVO)remoteContentVOListIterator.next();
	        		//System.out.println("remoteContentVO:" + remoteContentVO.getName());
	        		if(remoteContentVO.getName().equals(deviatingContentName))
	        		{
	        			String[] versionValues = remoteContentVO.getVersions();
	        			if(versionValues != null && versionValues.length > 0)
	        			{
		        			String remoteVersionValue = versionValues[0];

	        				Iterator componentsIterator = components.iterator();
	        	    		ContentVO localContentVO = null;
	        	    		while(componentsIterator.hasNext())
	        	    		{
	        	    			ContentVO candidate = (ContentVO)componentsIterator.next();
	        	    			if(candidate.getName().equals(remoteContentVO.getName()))
	        	    			{
	        	    				localContentVO = candidate;
	        	    			}
	        	    		}
	        	    		
	    					LanguageVO languageVO = LanguageController.getController().getMasterLanguage(localContentVO.getRepositoryId());
	    					ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(localContentVO.getId(), languageVO.getId());
	    					if(contentVersionVO != null)
	    					{
	    						contentVersionVO.setVersionValue(remoteVersionValue);
	    						//System.out.println("Updating :" + localContentVO.getName() + " with new latest versionValue");
	    						ContentVersionController.getContentVersionController().update(contentVersionVO.getId(), contentVersionVO);
	    					}
	        			}
	        		}
	        	}
	    	}
    	}

    	return doInput();
    }

    public String doExecute() throws Exception
    {
    	List<String> deploymentServers = CmsPropertyHandler.getDeploymentServers();
    	String deploymentServerUrl = deploymentServers.get(deploymentServerIndex);

    	//System.out.println("Synchronizing with deploymentServerUrl:" + deploymentServerUrl);

    	return "success";
    }

	public void setDeploymentServerIndex(Integer deploymentServerIndex)
	{
		this.deploymentServerIndex = deploymentServerIndex;
	}

	/*
	public List<DeploymentCompareBean> getDeviatingCategories()
	{
		return deviatingCategories;
	}
	*/

	public List<DeploymentCompareBean> getDeviatingContents()
	{
		return deviatingContents;
	}

	public List<DeploymentCompareBean> getDeviatingContentTypes()
	{
		return deviatingContentTypes;
	}

	public List<DeploymentCompareBean> getDeviatingSiteNodes()
	{
		return deviatingSiteNodes;
	}

	public List<DeploymentCompareBean> getDeviatingWorkflows()
	{
		return deviatingWorkflows;
	}

	public List<DeploymentCompareBean> getDeviatingCategoryVOList()
	{
		return deviatingCategoryVOList;
	}

	public List getDeviatingAttributes(String remoteSchemaValue, String localSchemaValue)
	{
		List deviatingAttributes = new ArrayList();
		
		List remoteAttributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(remoteSchemaValue);
		List localAttributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(localSchemaValue);

		Iterator remoteAttributesIterator = remoteAttributes.iterator();
		while(remoteAttributesIterator.hasNext())
		{
			ContentTypeAttribute conentTypeAttribute = (ContentTypeAttribute)remoteAttributesIterator.next();
			//System.out.println("conentTypeAttribute:" + conentTypeAttribute.getName());
			Iterator localAttributesIterator = localAttributes.iterator();
			boolean attributeExisted = false;
			while(localAttributesIterator.hasNext())
			{
				ContentTypeAttribute localConentTypeAttribute = (ContentTypeAttribute)localAttributesIterator.next();
				if(localConentTypeAttribute.getName().equals(conentTypeAttribute.getName()))
					attributeExisted = true;
			}
			if(!attributeExisted)
				deviatingAttributes.add(conentTypeAttribute);
		}
		
		return deviatingAttributes;
	}

	public List getDeviatingAssetKeys(String remoteSchemaValue, String localSchemaValue) throws Exception
	{
		List deviatingAssetKeys = new ArrayList();
		
		List remoteAssetKeys = ContentTypeDefinitionController.getController().getDefinedAssetKeys(remoteSchemaValue);
		List localAssetKeys = ContentTypeDefinitionController.getController().getDefinedAssetKeys(localSchemaValue);

		Iterator assetsIterator = remoteAssetKeys.iterator();
		while(assetsIterator.hasNext())
		{
			AssetKeyDefinition assetKeyDefinition = (AssetKeyDefinition)assetsIterator.next();
			//System.out.println("assetKeyDefinition:" + assetKeyDefinition.getAssetKey());
			
			Iterator localAssetKeysIterator = localAssetKeys.iterator();
			boolean assetKeyExisted = false;
			while(localAssetKeysIterator.hasNext())
			{
				AssetKeyDefinition localAssetKeyDefinition = (AssetKeyDefinition)localAssetKeysIterator.next();
				//System.out.println("localAssetKeyDefinition:" + localAssetKeyDefinition.getAssetKey());
				if(localAssetKeyDefinition.getAssetKey().equals(localAssetKeyDefinition.getAssetKey()))
					assetKeyExisted = true;
			}
			
			//System.out.println("assetKeyExisted:" + assetKeyExisted);
			if(!assetKeyExisted)
				deviatingAssetKeys.add(assetKeyDefinition);
		}
		
		return deviatingAssetKeys;
	}

	public List getDeviatingCategories(String remoteSchemaValue, String localSchemaValue) throws Exception
	{
		List deviatingCategories = new ArrayList();
		
		List remoteCategoryKeys = getDefinedCategoryKeys(remoteSchemaValue);
		List localCategoryKeys = getDefinedCategoryKeys(localSchemaValue);

		Iterator categoriesIterator = remoteCategoryKeys.iterator();
		while(categoriesIterator.hasNext())
		{
			CategoryAttribute categoryAttribute = (CategoryAttribute)categoriesIterator.next();
			//System.out.println("categoryAttribute:" + categoryAttribute.getCategoryName());
			
			Iterator localCategoriesIterator = localCategoryKeys.iterator();
			boolean categoryExisted = false;
			while(localCategoriesIterator.hasNext())
			{
				CategoryAttribute localCategoryAttribute = (CategoryAttribute)localCategoriesIterator.next();
				if(localCategoryAttribute.getCategoryName().equals(categoryAttribute.getCategoryName()))
					categoryExisted = true;
			}
			//System.out.println("categoryExisted:" + categoryExisted);
			
			if(!categoryExisted)
				deviatingCategories.add(categoryAttribute);
		}
		
		return deviatingCategories;
	}

	/**
	 * Gets the list of defined categoryKeys, also populate the category name for the UI.
	 */
	
	public List getDefinedCategoryKeys(String schemaValue) throws Exception
	{
		List categoryKeys = ContentTypeDefinitionController.getController().getDefinedCategoryKeys(schemaValue);
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
			return CategoryController.getController().findById(id).getName();
		}
		catch(SystemException e)
		{
			return "Category not found";
		}
	}
	
	private String copyAttribute(String remoteSchemaValue, String localSchemaValue, String contentTypeAttributeName)
	{
		String newSchemaValue = null;

		try
		{
			Document remoteDocument = createDocumentFromDefinition(remoteSchemaValue);
			Document localDocument = createDocumentFromDefinition(localSchemaValue);

			String attributeXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + contentTypeAttributeName + "']";
			Node attributeNode = org.apache.xpath.XPathAPI.selectSingleNode(remoteDocument.getDocumentElement(), attributeXPath);
			//System.out.println("attributeNode:" + attributeNode);

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all";
			Node attributesNode = org.apache.xpath.XPathAPI.selectSingleNode(localDocument.getDocumentElement(), attributesXPath);

			//Node node = attributeNode.cloneNode(true);
			Node node = localDocument.importNode(attributeNode, true);
			attributesNode.appendChild(node);
			
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(localDocument.getDocumentElement(), sb);
			newSchemaValue = sb.toString();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return newSchemaValue;
	}

	private String copyCategory(String remoteSchemaValue, String localSchemaValue, String categoryName)
	{
		String newSchemaValue = null;

		try
		{
			Document remoteDocument = createDocumentFromDefinition(remoteSchemaValue);
			Document localDocument = createDocumentFromDefinition(localSchemaValue);
			
			String attributeXPath = "/xs:schema/xs:simpleType[@name='categoryKeys']/xs:restriction/xs:enumeration[@value='" + categoryName + "']";
			Node attributeNode = org.apache.xpath.XPathAPI.selectSingleNode(remoteDocument.getDocumentElement(), attributeXPath);
			//System.out.println("attributeNode:" + attributeNode);

			String attributesXPath = "/xs:schema/xs:simpleType[@name='categoryKeys']/xs:restriction";
			Node attributesNode = org.apache.xpath.XPathAPI.selectSingleNode(localDocument.getDocumentElement(), attributesXPath);
			//System.out.println("attributesNode:" + attributesNode);
			if(attributesNode == null)
			{
				attributesNode = ContentTypeDefinitionController.getController().createNewEnumerationKey(localDocument, ContentTypeDefinitionController.CATEGORY_KEYS);
			}

			Node node = localDocument.importNode(attributeNode, true);
			attributesNode.appendChild(node);
			
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(localDocument.getDocumentElement(), sb);
			newSchemaValue = sb.toString();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return newSchemaValue;
	}

	private String copyAssetKey(String remoteSchemaValue, String localSchemaValue, String assetKey)
	{
		String newSchemaValue = null;

		try
		{
			Document remoteDocument = createDocumentFromDefinition(remoteSchemaValue);
			Document localDocument = createDocumentFromDefinition(localSchemaValue);

			String attributeXPath = "/xs:schema/xs:simpleType[@name='assetKeys']/xs:restriction/xs:enumeration[@value='" + assetKey + "']";
			Node attributeNode = org.apache.xpath.XPathAPI.selectSingleNode(remoteDocument.getDocumentElement(), attributeXPath);
			//System.out.println("attributeNode:" + attributeNode);

			String attributesXPath = "/xs:schema/xs:simpleType[@name='assetKeys']/xs:restriction";
			Node attributesNode = org.apache.xpath.XPathAPI.selectSingleNode(localDocument.getDocumentElement(), attributesXPath);
			//System.out.println("attributesNode:" + attributesNode);
			if(attributesNode == null)
			{
				attributesNode = ContentTypeDefinitionController.getController().createNewEnumerationKey(localDocument, ContentTypeDefinitionController.ASSET_KEYS);
			}
			
			//Node node = attributeNode.cloneNode(true);
			Node node = localDocument.importNode(attributeNode, true);
			attributesNode.appendChild(node);
			
			StringBuffer sb = new StringBuffer();
			org.infoglue.cms.util.XMLHelper.serializeDom(localDocument.getDocumentElement(), sb);
			newSchemaValue = sb.toString();
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

	protected Object invokeOperation(String endpointAddress, String operationName, String name, Object argument, Class returnType, String nameSpace) throws JspException
    {
		Object result = null;
		
        try
        {
        	InfoGluePrincipal principal = this.getInfoGluePrincipal();

            final DynamicWebservice ws = new DynamicWebservice(principal);

            ws.setTargetEndpointAddress(endpointAddress);
            ws.setOperationName(operationName);
            //ws.setReturnType(ContentVersionVO.class, new QName(nameSpace, ws.getClassName(ContentVersionVO.class)));
            ws.setReturnType(returnType, new QName(nameSpace, ws.getClassName(returnType)));
            
            if(argument != null)
            {
	            if(argument instanceof Map || argument instanceof HashMap)
	                ws.addArgument(name, (Map)argument);
	            else if(argument instanceof List || argument instanceof ArrayList)
	                ws.addArgument(name, (List)argument);
	            else
	                ws.addArgument(name, argument);
            }
            
            ws.callService();
            result = ws.getResult();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JspTagException(e.getMessage());
        }
        
        return result;
    }

	public String getCategoryPath(Integer categoryId) throws SystemException
	{
		return CategoryController.getController().getCategoryPath(categoryId);
	}

	public void setSynchronizeCategories(boolean synchronizeCategories)
	{
		this.synchronizeCategories = synchronizeCategories;
	}

	public void setSynchronizeContentTypeDefinitions(boolean synchronizeContentTypeDefinitions)
	{
		this.synchronizeContentTypeDefinitions = synchronizeContentTypeDefinitions;
	}

	public void setSynchronizeWorkflows(boolean synchronizeWorkflows)
	{
		this.synchronizeWorkflows = synchronizeWorkflows;
	}

	public boolean getSynchronizeCategories()
	{
		return this.synchronizeCategories;
	}

	public boolean getSynchronizeContentTypeDefinitions()
	{
		return this.synchronizeContentTypeDefinitions;
	}

	public boolean getSynchronizeWorkflows()
	{
		return this.synchronizeWorkflows;
	}

	public void setSynchronizeComponents(boolean synchronizeComponents)
	{
		this.synchronizeComponents = synchronizeComponents;
	}

	public boolean getSynchronizeComponents()
	{
		return this.synchronizeComponents;
	}

}
