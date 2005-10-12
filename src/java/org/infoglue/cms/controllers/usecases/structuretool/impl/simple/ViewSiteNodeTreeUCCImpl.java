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

package org.infoglue.cms.controllers.usecases.structuretool.impl.simple;

import org.infoglue.cms.controllers.usecases.structuretool.ViewSiteNodeTreeUCC;

import org.infoglue.cms.controllers.kernel.impl.simple.*;

import org.infoglue.cms.entities.structure.ServiceBindingVO;
import org.infoglue.cms.entities.structure.SiteNode;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.entities.structure.SiteNodeVersion;
import org.infoglue.cms.entities.content.Content;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.AvailableServiceBinding;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.Language;
import org.infoglue.cms.entities.management.Repository;
import org.infoglue.cms.entities.management.ServiceDefinition;
import org.infoglue.cms.entities.management.ServiceDefinitionVO;

import org.infoglue.cms.exception.*;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.*;

public class ViewSiteNodeTreeUCCImpl extends BaseUCCController implements ViewSiteNodeTreeUCC
{
	/**
	 * This method fetches the root siteNode for a particular repository.
	 * If there is no such siteNode we create one as all repositories need one to work.
	 */
	        
   	public SiteNodeVO getRootSiteNode(Integer repositoryId, InfoGluePrincipal infoGluePrincipal) throws ConstraintException, SystemException
   	{
        Database db = CastorDatabaseService.getDatabase();
        ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

        SiteNodeVO siteNodeVO = null;

        beginTransaction(db);

        try
        {
            getLogger().info("Fetching the root siteNode for the repository " + repositoryId);
			OQLQuery oql = db.getOQLQuery( "SELECT c FROM org.infoglue.cms.entities.structure.impl.simple.SiteNodeImpl c WHERE is_undefined(c.parentSiteNode) AND c.repository.repositoryId = $1");
			oql.bind(repositoryId);
			
        	QueryResults results = oql.execute(Database.ReadOnly);
			
			if (results.hasMore()) 
            {
			    siteNodeVO = ((SiteNode)results.next()).getValueObject();
            }
            else
            {
				//None found - we create it and give it the name of the repository.
				getLogger().info("Found no rootSiteNode so we create a new....");
				SiteNodeVO rootSiteNodeVO = new SiteNodeVO();
				Repository repository = RepositoryController.getController().getRepositoryWithId(repositoryId, db);
				rootSiteNodeVO.setName(repository.getName());
				rootSiteNodeVO.setIsBranch(new Boolean(true));
				SiteNode siteNode = SiteNodeController.getController().create(db, null, null, infoGluePrincipal, repositoryId, rootSiteNodeVO);
				//siteNodeVO = SiteNodeController.getController().create(null, null, infoGluePrincipal, repositoryId, siteNodeVO);
				//siteNodeVO = SiteNodeControllerProxy.getSiteNodeControllerProxy().acCreate(infoGluePrincipal, null, null, repositoryId, rootSiteNodeVO);
				siteNodeVO = siteNode.getValueObject();
				SiteNodeVO newSiteNodeVO = siteNodeVO;
				
            	//Also creates an initial meta info for the sitenode.
        		SiteNodeVersion siteNodeVersion = SiteNodeVersionController.getController().getLatestSiteNodeVersion(db, newSiteNodeVO.getId(), false);
        		Language masterLanguage 		= LanguageController.getController().getMasterLanguage(db, repositoryId);
        	   
        		Integer metaInfoContentTypeDefinitionId 		= null;
        		Integer availableServiceBindingId 				= null;
        		ServiceDefinitionVO singleServiceDefinitionVO 	= null;
        		
        		ContentVO contentVO = new ContentVO();
        		
        		List contentTypeDefinitionVOList = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOList(db);
        		Iterator contentTypeDefinitionVOListIterator = contentTypeDefinitionVOList.iterator();
        		while(contentTypeDefinitionVOListIterator.hasNext())
        		{
        			ContentTypeDefinitionVO contentTypeDefinitionVO = (ContentTypeDefinitionVO)contentTypeDefinitionVOListIterator.next();
        			if(contentTypeDefinitionVO.getName().equalsIgnoreCase("Meta info"))
        				metaInfoContentTypeDefinitionId = contentTypeDefinitionVO.getId();
        		}
        		
        		AvailableServiceBinding availableServiceBinding = AvailableServiceBindingController.getController().getAvailableServiceBindingWithName("Meta information", db, false);
        		availableServiceBindingId = availableServiceBinding.getId();
        		List serviceDefinitions = AvailableServiceBindingController.getController().getServiceDefinitionVOList(db, availableServiceBindingId);
        		if(serviceDefinitions == null || serviceDefinitions.size() == 0)
        		{
        		    ServiceDefinition serviceDefinition = ServiceDefinitionController.getController().getServiceDefinitionWithName("Core content service", db, false);
        		    availableServiceBinding.getServiceDefinitions().add(serviceDefinition);
        		    singleServiceDefinitionVO = serviceDefinition.getValueObject();
        		}
        		else if(serviceDefinitions.size() == 1)
        		{
        			singleServiceDefinitionVO = (ServiceDefinitionVO)serviceDefinitions.get(0);	    
        		}

        		Content parentFolderContent = null;
        		ContentVO parentFolderContentVO = null;
        		
        		Content rootContent = ContentControllerProxy.getController().getRootContent(db, repositoryId, infoGluePrincipal.getName(), true);
        		//ContentVO rootContentVO = ContentControllerProxy.getController().getRootContentVO(repositoryId, infoGluePrincipal.getName());
        		if(rootContent != null)
        		{
        			Collection children = rootContent.getChildren();
        			Iterator childrenIterator = children.iterator();
        			while(childrenIterator.hasNext())
        			{
        				Content child = (Content)childrenIterator.next();
        				if(child.getName().equalsIgnoreCase("Meta info folder"))
        				{
        					getLogger().info("Found the metainfo folder..");
        					parentFolderContent = child;
        					break;
        				}
        			}
        			
        			if(parentFolderContent == null)
        			{
        				parentFolderContentVO = new ContentVO();
        				parentFolderContentVO.setCreatorName(infoGluePrincipal.getName());
        				parentFolderContentVO.setIsBranch(new Boolean(true));
        				parentFolderContentVO.setName("Meta info folder");
        				parentFolderContentVO.setRepositoryId(repositoryId);

        				parentFolderContent = ContentController.getContentController().create(db, rootContent.getId(), null, repositoryId, parentFolderContentVO);
        				//parentFolderContentVO = ContentControllerProxy.getController().create(db, rootContent.getId(), null, repositoryId, parentFolderContentVO);
        			}

        			contentVO.setCreatorName(infoGluePrincipal.getName());
        			contentVO.setIsBranch(new Boolean(false));
        			contentVO.setName(siteNodeVO.getName() + " Metainfo");
        			contentVO.setRepositoryId(repositoryId);

        			Content content = ContentController.getContentController().create(db, parentFolderContent.getId(), metaInfoContentTypeDefinitionId, repositoryId, contentVO);
        			//Create initial content version also... in masterlanguage
        			String versionValue = "<?xml version='1.0' encoding='UTF-8'?><article xmlns=\"x-schema:ArticleSchema.xml\"><attributes><Title><![CDATA[" + siteNodeVO.getName() + "]]></Title><NavigationTitle><![CDATA[" + siteNodeVO.getName() + "]]></NavigationTitle><Description><![CDATA[" + siteNodeVO.getName() + "]]></Description><MetaInfo><![CDATA[" + siteNodeVO.getName() + "]]></MetaInfo><ComponentStructure><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?><components></components>]]></ComponentStructure></attributes></article>";
        		
        			ContentVersionVO contentVersionVO = new ContentVersionVO();
        			contentVersionVO.setVersionComment("Autogenerated version");
        			contentVersionVO.setVersionModifier(infoGluePrincipal.getName());
        			contentVersionVO.setVersionValue(versionValue);
        			ContentVersionController.getContentVersionController().create(contentVO.getId(), masterLanguage.getId(), contentVersionVO, null, db);
        			
        			ServiceBindingVO serviceBindingVO = new ServiceBindingVO();
        			serviceBindingVO.setName(siteNodeVO.getName() + " Metainfo");
        			serviceBindingVO.setPath("/None specified/");
        		
        			String qualifyerXML = "<?xml version='1.0' encoding='UTF-8'?><qualifyer><contentId>" + contentVO.getId() + "</contentId></qualifyer>";
        		
        			getLogger().info("serviceBindingVO:" + serviceBindingVO);
        			getLogger().info("qualifyerXML:" + qualifyerXML);
        			getLogger().info("availableServiceBindingId:" + availableServiceBindingId);
        			getLogger().info("siteNodeVersion:" + siteNodeVersion);
        			getLogger().info("singleServiceDefinitionVO:" + singleServiceDefinitionVO);
        			
        			ServiceBindingController.getController().create(db, serviceBindingVO, qualifyerXML, availableServiceBindingId, siteNodeVersion.getId(), singleServiceDefinitionVO.getId());	
        		}
        		//End creation of metainfo
        		
            }
           	getLogger().info("Did we find anything?");
            
            //If any of the validations or setMethods reported an error, we throw them up now before create. 
            ceb.throwIfNotEmpty();
            
            commitTransaction(db);
        }
        catch(ConstraintException ce)
        {
            getLogger().warn("An error occurred so we should not complete the transaction:" + ce, ce);
            rollbackTransaction(db);
            throw ce;
        }
        catch(Exception e)
        {
            getLogger().error("An error occurred so we should not complete the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }

        return siteNodeVO;
   	}
    
    public SiteNodeVO getSiteNode(Integer siteNodeId) throws ConstraintException, SystemException
    {
    	return SiteNodeController.getSiteNodeVOWithId(siteNodeId);
    	/*
    	Database db = CastorDatabaseService.getDatabase();
        ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

        SiteNode siteNode = null;

        beginTransaction(db);

        try
        {
            siteNode = SiteNodeController.getSiteNodeVOWithId(siteNodeId);
        
            //If any of the validations or setMethods reported an error, we throw them up now before create.
            ceb.throwIfNotEmpty();
            
            commitTransaction(db);
        }
        catch(ConstraintException ce)
        {
            getLogger().warn("An error occurred so we should not complete the transaction:" + ce, ce);
            rollbackTransaction(db);
            throw ce;
        }
        catch(Exception e)
        {
            getLogger().error("An error occurred so we should not complete the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }

        return siteNode.getValueObject();
		*/
    }        


    public List getSiteNodeChildren(Integer parentSiteNodeId) throws ConstraintException, SystemException
    {
		Database db = CastorDatabaseService.getDatabase();
        ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

        List childrenVOList = null;

        beginTransaction(db);

        try
        {
            SiteNode siteNode = SiteNodeController.getSiteNodeWithId(parentSiteNodeId, db, true);
	        Collection children = siteNode.getChildSiteNodes();
        	childrenVOList = SiteNodeController.toVOList(children);
        	
            //If any of the validations or setMethods reported an error, we throw them up now before create.
            ceb.throwIfNotEmpty();
            
            commitTransaction(db);
        }
        catch(ConstraintException ce)
        {
            getLogger().warn("An error occurred so we should not complete the transaction:" + ce, ce);
            rollbackTransaction(db);
            throw ce;
        }
        catch(Exception e)
        {
            getLogger().error("An error occurred so we should not complete the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
        
        return childrenVOList;
    } 
}
        
