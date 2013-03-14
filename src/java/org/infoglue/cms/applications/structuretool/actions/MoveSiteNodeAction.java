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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.EventController;
import org.infoglue.cms.controllers.kernel.impl.simple.PublicationController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeVersionController;
import org.infoglue.cms.entities.publishing.PublicationVO;
import org.infoglue.cms.entities.structure.SiteNode;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.entities.structure.SiteNodeVersion;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.entities.workflow.EventVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

/**
 * This action represents the CreateSiteNode Usecase.
 */

public class MoveSiteNodeAction extends InfoGlueAbstractAction
{
    private final static Logger logger = Logger.getLogger(MoveSiteNodeAction.class.getName());

    private Integer siteNodeId;
    private Integer parentSiteNodeId;
    private Integer newParentSiteNodeId;
    private Integer repositoryId;
    private Integer changeTypeId;
    private ConstraintExceptionBuffer ceb;
   	private SiteNodeVO siteNodeVO;
  
  
  	public MoveSiteNodeAction()
	{
		this(new SiteNodeVO());
	}
	
	public MoveSiteNodeAction(SiteNodeVO siteNodeVO)
	{
		this.siteNodeVO = siteNodeVO;
		this.ceb = new ConstraintExceptionBuffer();			
	}	

	public void setSiteNodeId(Integer siteNodeId)
	{
		this.siteNodeVO.setSiteNodeId(siteNodeId);
	}

	public void setNewParentSiteNodeId(Integer newParentSiteNodeId)
	{
		this.newParentSiteNodeId = newParentSiteNodeId;
	}

	public void setParentSiteNodeId(Integer parentSiteNodeId)
	{
		this.parentSiteNodeId = parentSiteNodeId;
	}

	public void setChangeTypeId(Integer changeTypeId)
	{
		this.changeTypeId = changeTypeId;
	}

	public Integer getParentSiteNodeId()
	{
		return this.parentSiteNodeId;
	}
	
	public Integer getRepositoryId()
	{
		return this.repositoryId;
	}

	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}
	    
	public Integer getSiteNodeId()
	{
		return siteNodeVO.getSiteNodeId();
	}

	public Integer getNewParentSiteNodeId()
	{
		return this.newParentSiteNodeId;
	}
    
	public Integer getUnrefreshedSiteNodeId()
	{
		return this.newParentSiteNodeId;
	}

	public Integer getChangeTypeId()
	{
		return this.changeTypeId;
	}
      
    public String doExecute() throws Exception
    {
        ceb.throwIfNotEmpty();
    	
		Integer oldParentSiteNodeId = SiteNodeController.getController().getSiteNodeVOWithId(this.siteNodeVO.getId()).getParentSiteNodeId();

    	//SiteNodeController.moveSiteNode(this.siteNodeVO, this.newParentSiteNodeId);
		SiteNodeControllerProxy.getSiteNodeControllerProxy().acMoveSiteNode(this.getInfoGluePrincipal(), this.siteNodeVO, this.newParentSiteNodeId);
	    
		List<EventVO> resultingEvents = new ArrayList<EventVO>();
		
		//Creating the event that will notify the editor...
		try
		{
			EventVO eventVO = new EventVO();
			if(oldParentSiteNodeId != null)
			{
				SiteNodeVO oldParentSiteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(oldParentSiteNodeId);
				SiteNodeVersionVO snvVO = SiteNodeVersionController.getController().getLatestSiteNodeVersionVO(oldParentSiteNodeVO.getId());
				eventVO = new EventVO();
				eventVO.setDescription("Moved " + oldParentSiteNodeVO.getName());
				eventVO.setEntityClass(SiteNodeVersion.class.getName());
				eventVO.setEntityId(snvVO.getId());
		        eventVO.setName(oldParentSiteNodeVO.getName());
				eventVO.setTypeId(EventVO.PUBLISH);
		        eventVO = EventController.create(eventVO, oldParentSiteNodeVO.getRepositoryId(), this.getInfoGluePrincipal());			
		        eventVO.setName("Moved child from " + oldParentSiteNodeVO.getName());
				resultingEvents.add(eventVO);
			}
			
			SiteNodeVO newParentSiteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(this.newParentSiteNodeId);
			SiteNodeVersionVO snvVO = SiteNodeVersionController.getController().getLatestSiteNodeVersionVO(newParentSiteNodeVO.getId());
			eventVO = new EventVO();
			eventVO.setDescription("Moved child to " + newParentSiteNodeVO.getName());
			eventVO.setEntityClass(SiteNodeVersion.class.getName());
			eventVO.setEntityId(snvVO.getId());
	        eventVO.setName(newParentSiteNodeVO.getName());
			eventVO.setTypeId(EventVO.PUBLISH);
	        eventVO = EventController.create(eventVO, newParentSiteNodeVO.getRepositoryId(), this.getInfoGluePrincipal());			
	        eventVO.setName("Moved " + newParentSiteNodeVO.getName());
			resultingEvents.add(eventVO);

			SiteNodeVO actualSiteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(this.siteNodeVO.getId());
			snvVO = SiteNodeVersionController.getController().getLatestSiteNodeVersionVO(actualSiteNodeVO.getId());
			eventVO = new EventVO();
			eventVO.setDescription("Moved " + actualSiteNodeVO.getName());
			eventVO.setEntityClass(SiteNodeVersion.class.getName());
			eventVO.setEntityId(snvVO.getId());
	        eventVO.setName(actualSiteNodeVO.getName());
			eventVO.setTypeId(EventVO.PUBLISH);
	        eventVO = EventController.create(eventVO, actualSiteNodeVO.getRepositoryId(), this.getInfoGluePrincipal());			
	        eventVO.setName("Moved " + actualSiteNodeVO.getName());
			resultingEvents.add(eventVO);

			PublicationVO publicationVO = new PublicationVO();
		    publicationVO.setName("Direct publication by " + this.getInfoGluePrincipal().getName());
		    publicationVO.setDescription("Moved page to " +  this.newParentSiteNodeId);
		    publicationVO.setRepositoryId(repositoryId);
		    publicationVO = PublicationController.getController().createAndPublish(publicationVO, resultingEvents, new HashMap(), new HashMap(), false, this.getInfoGluePrincipal());
		}
		catch (Exception e) 
		{
			logger.error("Error publishing move operation: " + e.getMessage(), e);
		}
        
		return "success";
    }
    
    public String getErrorKey()
	{
		return "SiteNode.parentSiteNodeId";
	}
	
	public String getReturnAddress()
	{
		return "ViewSiteNode.action?siteNodeId=" + this.siteNodeVO.getId() + "&repositoryId=" + this.repositoryId;
	}   

        
}
