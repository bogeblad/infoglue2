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

import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.AvailableServiceBindingVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.management.ServiceDefinitionVO;
import org.infoglue.cms.entities.structure.ServiceBindingVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.applications.common.VisualFormatter;

import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.controllers.kernel.impl.simple.AvailableServiceBindingController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.ServiceBindingController;
import org.infoglue.cms.controllers.kernel.impl.simple.ServiceDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeVersionController;
import org.infoglue.cms.util.CmsLogger;

import java.util.Iterator;
import java.util.List;

/**
 * This action represents the CreateSiteNode Usecase.
 */

public class CreateSiteNodeAction extends WebworkAbstractAction
{

    private Integer siteNodeId;
    private String name;
    private Boolean isBranch;
    private Integer parentSiteNodeId;
    private Integer siteNodeTypeDefinitionId;
    private Integer repositoryId;
   	private ConstraintExceptionBuffer ceb;
   	private SiteNodeVO siteNodeVO;
   	private SiteNodeVO newSiteNodeVO;
  
  	public CreateSiteNodeAction()
	{
		this(new SiteNodeVO());
	}
	
	public CreateSiteNodeAction(SiteNodeVO siteNodeVO)
	{
		this.siteNodeVO = siteNodeVO;
		this.ceb = new ConstraintExceptionBuffer();			
	}	

	public void setParentSiteNodeId(Integer parentSiteNodeId)
	{
		this.parentSiteNodeId = parentSiteNodeId;
	}

	public Integer getParentSiteNodeId()
	{
		return this.parentSiteNodeId;
	}

	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	public Integer getRepositoryId()
	{
		return this.repositoryId;
	}

	public void setSiteNodeTypeDefinitionId(Integer siteNodeTypeDefinitionId)
	{
		this.siteNodeTypeDefinitionId = siteNodeTypeDefinitionId;
	}

	public Integer getSiteNodeTypeDefinitionId()
	{
		return this.siteNodeTypeDefinitionId;
	}	
	
    public java.lang.String getName()
    {
        return this.siteNodeVO.getName();
    }

    public String getPublishDateTime()
    {    		
        return new VisualFormatter().formatDate(this.siteNodeVO.getPublishDateTime(), "yyyy-MM-dd HH:mm");
    }
        
    public String getExpireDateTime()
    {
        return new VisualFormatter().formatDate(this.siteNodeVO.getExpireDateTime(), "yyyy-MM-dd HH:mm");
    }

	public Boolean getIsBranch()
	{
 		return this.siteNodeVO.getIsBranch();
	}    
            
    public void setName(java.lang.String name)
    {
        this.siteNodeVO.setName(name);
    }
    	
    public void setPublishDateTime(String publishDateTime)
    {
       	CmsLogger.logInfo("publishDateTime:" + publishDateTime);
   		this.siteNodeVO.setPublishDateTime(new VisualFormatter().parseDate(publishDateTime, "yyyy-MM-dd HH:mm"));
    }

    public void setExpireDateTime(String expireDateTime)
    {
       	CmsLogger.logInfo("expireDateTime:" + expireDateTime);
       	this.siteNodeVO.setExpireDateTime(new VisualFormatter().parseDate(expireDateTime, "yyyy-MM-dd HH:mm"));
	}
 
    public void setIsBranch(Boolean isBranch)
    {
       	this.siteNodeVO.setIsBranch(isBranch);
    }
     
	public Integer getSiteNodeId()
	{
		return newSiteNodeVO.getSiteNodeId();
	}
    
	/**
	 * This method fetches the list of SiteNodeTypeDefinitions
	 */
	
	public List getSiteNodeTypeDefinitions() throws Exception
	{
		return SiteNodeTypeDefinitionController.getController().getSiteNodeTypeDefinitionVOList();
	}      
      
    public String doExecute() throws Exception
    {
        ceb = this.siteNodeVO.validate();
    	ceb.throwIfNotEmpty();
    	
    	CmsLogger.logInfo("name:" + this.siteNodeVO.getName());
    	CmsLogger.logInfo("publishDateTime:" + this.siteNodeVO.getPublishDateTime());
    	CmsLogger.logInfo("expireDateTime:" + this.siteNodeVO.getExpireDateTime());
    	CmsLogger.logInfo("isBranch:" + this.siteNodeVO.getIsBranch());
    	
		newSiteNodeVO = SiteNodeControllerProxy.getSiteNodeControllerProxy().acCreate(this.getInfoGluePrincipal(), this.parentSiteNodeId, this.siteNodeTypeDefinitionId, this.repositoryId, this.siteNodeVO);
    	//newSiteNodeVO = SiteNodeController.getController().create(this.parentSiteNodeId, this.siteNodeTypeDefinitionId, this.getInfoGluePrincipal(), this.repositoryId, this.siteNodeVO);
		
		//Test
		SiteNodeVersionVO siteNodeVersionVO = SiteNodeVersionController.getController().getLatestSiteNodeVersionVO(newSiteNodeVO.getId());
		LanguageVO masterLanguageVO 		= LanguageController.getController().getMasterLanguage(this.repositoryId);
	   
		Integer metaInfoContentTypeDefinitionId 		= null;
		Integer availableServiceBindingId 				= null;
		ServiceDefinitionVO singleServiceDefinitionVO 	= null;
		
		ContentVO contentVO = new ContentVO();
		
		List contentTypeDefinitionVOList = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOList();
		Iterator contentTypeDefinitionVOListIterator = contentTypeDefinitionVOList.iterator();
		while(contentTypeDefinitionVOListIterator.hasNext())
		{
			ContentTypeDefinitionVO contentTypeDefinitionVO = (ContentTypeDefinitionVO)contentTypeDefinitionVOListIterator.next();
			if(contentTypeDefinitionVO.getName().equalsIgnoreCase("Meta info"))
				metaInfoContentTypeDefinitionId = contentTypeDefinitionVO.getId();
		}
		
		AvailableServiceBindingVO availableServiceBindingVO = AvailableServiceBindingController.getController().getAvailableServiceBindingVOWithName("Meta information");
		availableServiceBindingId = availableServiceBindingVO.getId();
		List serviceDefinitions = AvailableServiceBindingController.getController().getServiceDefinitionVOList(availableServiceBindingId);
		if(serviceDefinitions == null || serviceDefinitions.size() == 0)
		{
		    ServiceDefinitionVO serviceDefinitionVO = ServiceDefinitionController.getController().getServiceDefinitionVOWithName("Core content service");
		    String[] values = {serviceDefinitionVO.getId().toString()};
		    AvailableServiceBindingController.getController().update(availableServiceBindingVO, values);
		    singleServiceDefinitionVO = serviceDefinitionVO;
		}
		else if(serviceDefinitions.size() == 1)
		{
			singleServiceDefinitionVO = (ServiceDefinitionVO)serviceDefinitions.get(0);	    
		}

		SiteNodeVO siteNodeVO = newSiteNodeVO;
		
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

			contentVO.setCreatorName(this.getInfoGluePrincipal().getName());
			contentVO.setIsBranch(new Boolean(false));
			contentVO.setName(siteNodeVO.getName() + " Metainfo");
			contentVO.setRepositoryId(this.repositoryId);

			contentVO = ContentControllerProxy.getController().create(parentFolderContentVO.getId(), metaInfoContentTypeDefinitionId, this.repositoryId, contentVO);
			//Create initial content version also... in masterlanguage
			String versionValue = "<?xml version='1.0' encoding='UTF-8'?><article xmlns=\"x-schema:ArticleSchema.xml\"><attributes><Title><![CDATA[" + this.siteNodeVO.getName() + "]]></Title><NavigationTitle><![CDATA[" + this.siteNodeVO.getName() + "]]></NavigationTitle><Description><![CDATA[" + this.siteNodeVO.getName() + "]]></Description><MetaInfo><![CDATA[" + this.siteNodeVO.getName() + "]]></MetaInfo><ComponentStructure><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?><components></components>]]></ComponentStructure></attributes></article>";
		
			ContentVersionVO contentVersionVO = new ContentVersionVO();
			contentVersionVO.setVersionComment("Autogenerated version");
			contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());
			contentVersionVO.setVersionValue(versionValue);
			ContentVersionController.getContentVersionController().create(contentVO.getId(), masterLanguageVO.getId(), contentVersionVO, null);
			
			ServiceBindingVO serviceBindingVO = new ServiceBindingVO();
			serviceBindingVO.setName(siteNodeVO.getName() + " Metainfo");
			serviceBindingVO.setPath("/None specified/");
		
			String qualifyerXML = "<?xml version='1.0' encoding='UTF-8'?><qualifyer><contentId>" + contentVO.getId() + "</contentId></qualifyer>";
		
			ServiceBindingController.create(serviceBindingVO, qualifyerXML, availableServiceBindingId, siteNodeVersionVO.getId(), singleServiceDefinitionVO.getId());	
		
			return "success";

		}
		else
		{
			//throw new SystemException("");
		}

	    //End Test
	    
        return "success";
    }

    public String doInput() throws Exception
    {
        return "input";
    }
        
}
