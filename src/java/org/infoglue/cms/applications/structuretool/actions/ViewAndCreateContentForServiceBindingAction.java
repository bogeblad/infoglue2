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

package org.infoglue.cms.applications.structuretool.actions;

import org.infoglue.cms.controllers.kernel.impl.simple.*;

import org.infoglue.cms.entities.content.*;
import org.infoglue.cms.entities.structure.*;
import org.infoglue.cms.entities.management.*;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

import java.util.List;
import java.util.Iterator;

/**
 * This action first checks if there is a bound content linked - if not one is created in a special folder.
 * The content is then shown to the user for editing.
 */

public class ViewAndCreateContentForServiceBindingAction extends WebworkAbstractAction
{
    private Integer siteNodeVersionId;
    private Integer repositoryId;
    private Integer availableServiceBindingId;
    private Integer serviceDefinitionId;
    private Integer bindingTypeId;
    private ConstraintExceptionBuffer ceb;
   	private Integer siteNodeId;
   	private ServiceDefinitionVO singleServiceDefinitionVO;
   	//private String qualifyerXML;
	private String tree;	
	private List repositories;
	private ContentVO contentVO = new ContentVO();
	private Integer languageId = null;
	private Integer metaInfoContentTypeDefinitionId = null;
	
   	private ServiceBindingVO serviceBindingVO = null;
   
  
  	public ViewAndCreateContentForServiceBindingAction()
	{
		this(new ServiceBindingVO());
	}
	
	public ViewAndCreateContentForServiceBindingAction(ServiceBindingVO serviceBindingVO)
	{
		this.serviceBindingVO = serviceBindingVO;
		this.ceb = new ConstraintExceptionBuffer();			
	}	

	public void setSiteNodeVersionId(Integer siteNodeVersionId)
	{
		this.siteNodeVersionId = siteNodeVersionId;
	}

	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	public void setAvailableServiceBindingId(Integer availableServiceBindingId)
	{
		this.availableServiceBindingId = availableServiceBindingId;
	}

	public void setServiceDefinitionId(Integer serviceDefinitionId)
	{
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public void setBindingTypeId(Integer bindingTypeId)
	{
		this.serviceBindingVO.setBindingTypeId(bindingTypeId);
	}

	public void setPath(String path)
	{
		this.serviceBindingVO.setPath(path);
	}
	
	public Integer getSiteNodeVersionId()
	{
		return this.siteNodeVersionId;
	}

	public void setSiteNodeId(Integer siteNodeId)
	{
		this.siteNodeId = siteNodeId;
	}

	public Integer getSiteNodeId()
	{
		return this.siteNodeId;
	}
	    
	public Integer getRepositoryId()
	{
		return this.repositoryId;
	}

	public Integer getAvailableServiceBindingId()
	{
		return this.availableServiceBindingId;
	}
    
	public Integer getServiceDefinitionId()
	{
		return this.singleServiceDefinitionVO.getServiceDefinitionId();
	}
	
	public Integer getBindingTypeId()
	{
		return this.bindingTypeId;
	}

	public void setServiceBindingId(Integer serviceBindingId)
	{
		this.serviceBindingVO.setServiceBindingId(serviceBindingId);
	}

	public ServiceDefinitionVO getSingleServiceDefinitionVO()
	{
		return this.singleServiceDefinitionVO;
	}
	
	public String getTree()
	{
		return tree;
	}

	public void setTree(String string)
	{
		tree = string;
	}
	
	public String getCurrentAction()
	{
		return "ViewAndCreateContentForServiceBinding.action";
	}
	
	/**
	 * We first checks if there is a bound content linked - if not one is created in a special folder and
	 * a new service binding is created to it. The content is then shown to the user for editing. Most of this method should 
	 * be moved to an controller.
	 */
	
    public String doExecute() throws Exception
    {		
		LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(this.repositoryId);
		this.languageId = masterLanguageVO.getLanguageId();
		
		/*	
		List repositoryLanguages = RepositoryLanguageController.getController().getRepositoryLanguageVOListWithRepositoryId(this.repositoryId);
		Iterator i = repositoryLanguages.iterator();
		if(i.hasNext())
		{
			RepositoryLanguageVO repositoryLanguageVO = (RepositoryLanguageVO)i.next();
			this.languageId = repositoryLanguageVO.getLanguageId();
		}
		*/
		ContentTypeDefinitionVO contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithName("Meta info");
		this.metaInfoContentTypeDefinitionId = contentTypeDefinitionVO.getId();
		
		/*
		List contentTypeDefinitionVOList = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOList();
		Iterator contentTypeDefinitionVOListIterator = contentTypeDefinitionVOList.iterator();
		while(contentTypeDefinitionVOListIterator.hasNext())
		{
			ContentTypeDefinitionVO contentTypeDefinitionVO = (ContentTypeDefinitionVO)contentTypeDefinitionVOListIterator.next();
			if(contentTypeDefinitionVO.getName().equalsIgnoreCase("Meta info"))
				this.metaInfoContentTypeDefinitionId = contentTypeDefinitionVO.getId();
		}
		*/
		boolean hadMetaInfo = false;
    	if(this.serviceBindingVO.getId() == null)
    	{
			AvailableServiceBindingVO availableServiceBindingVO = AvailableServiceBindingController.getController().getAvailableServiceBindingVOWithName("Meta information");
			
			List serviceBindings = SiteNodeVersionController.getServiceBindningVOList(this.siteNodeVersionId);
			Iterator serviceBindingIterator = serviceBindings.iterator();
			while(serviceBindingIterator.hasNext())
			{
				ServiceBindingVO serviceBindingVO = (ServiceBindingVO)serviceBindingIterator.next();
				if(serviceBindingVO.getAvailableServiceBindingId().intValue() == availableServiceBindingVO.getAvailableServiceBindingId().intValue())
				{
					List boundContents = ContentController.getBoundContents(serviceBindingVO.getServiceBindingId()); 			
					if(boundContents.size() > 0)
					{
						this.contentVO = (ContentVO)boundContents.get(0);		
						hadMetaInfo = true;
						break;
					}						
				}
			}

    		if(!hadMetaInfo)
    		{
		    	List serviceDefinitions = AvailableServiceBindingController.getController().getServiceDefinitionVOList(this.availableServiceBindingId);
		    	if(serviceDefinitions == null || serviceDefinitions.size() == 0)
		    	{
			    }
		    	else if(serviceDefinitions.size() == 1)
		    	{
			        this.singleServiceDefinitionVO = (ServiceDefinitionVO)serviceDefinitions.get(0);	    
		    	}
	
	    		SiteNodeVO siteNodeVO = SiteNodeController.getSiteNodeVOWithId(this.siteNodeId);
	    		
				CmsLogger.logInfo("The service definition was null so we must create a new content and binding..");
				
				ContentVO parentFolderContentVO = null;
				
				ContentVO rootContentVO = ContentControllerProxy.getController().getRootContentVO(this.repositoryId, this.getInfoGluePrincipal().getName());
				if(rootContentVO != null)
				{
					List children = ContentController.getContentController().getContentChildrenVOList(rootContentVO.getId());
					Iterator childrenIterator = children.iterator();
					while(childrenIterator.hasNext())
					{
						ContentVO child = (ContentVO)childrenIterator.next();
						if(child.getName().equalsIgnoreCase("Meta info folder"))
						{
							CmsLogger.logInfo("Found the metainfo folder..");
							parentFolderContentVO = child;
							break;
						}
					}
					
					if(parentFolderContentVO == null)
					{
						parentFolderContentVO = new ContentVO();
						parentFolderContentVO.setCreatorName(this.getInfoGluePrincipal().getName());
						parentFolderContentVO.setIsBranch(new Boolean(true));
						parentFolderContentVO.setName("Meta info folder");
						parentFolderContentVO.setRepositoryId(this.repositoryId);
	
						parentFolderContentVO = ContentControllerProxy.getController().create(rootContentVO.getId(), null, this.repositoryId, parentFolderContentVO);
					}
				
					this.contentVO.setCreatorName(this.getInfoGluePrincipal().getName());
					this.contentVO.setIsBranch(new Boolean(false));
					this.contentVO.setName(siteNodeVO.getName() + " Metainfo");
					this.contentVO.setRepositoryId(this.repositoryId);
		
					this.contentVO = ContentControllerProxy.getController().create(parentFolderContentVO.getId(), this.metaInfoContentTypeDefinitionId, this.repositoryId, this.contentVO);
				
					ServiceBindingVO serviceBindingVO = new ServiceBindingVO();
					serviceBindingVO.setName(siteNodeVO.getName() + " Metainfo");
					serviceBindingVO.setPath("/None specified/");
				
					String qualifyerXML = "<?xml version='1.0' encoding='ISO-8859-1'?><qualifyer><contentId>" + contentVO.getId() + "</contentId></qualifyer>";
				
					ServiceBindingController.create(this.serviceBindingVO, qualifyerXML, this.availableServiceBindingId, this.siteNodeVersionId, singleServiceDefinitionVO.getId());	
	    		
					return "success";
	
				}
	    		else
	    		{
	    			//throw new SystemException("");
	    		}
    		}
    	}
		else
		{
			List boundContents = ContentController.getBoundContents(this.serviceBindingVO.getId()); 			
			if(boundContents.size() > 0)
				this.contentVO = (ContentVO)boundContents.get(0);		 	
		}
		
    	return "success";
    }
       
	public List getRepositories()
	{
		return repositories;
	}
	
	public Integer getContentId()
	{
		return this.contentVO.getId();
	}
	
	public Integer getLanguageId()
	{
		return this.languageId;
	}
}
