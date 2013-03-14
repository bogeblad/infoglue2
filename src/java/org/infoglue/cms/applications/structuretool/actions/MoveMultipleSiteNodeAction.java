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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.EventController;
import org.infoglue.cms.controllers.kernel.impl.simple.PublicationController;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeVersionController;
import org.infoglue.cms.entities.publishing.PublicationVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.entities.structure.SiteNodeVersion;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.entities.workflow.EventVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.dom.DOMBuilder;

/**
 * This action represents the CreateSiteNode Usecase.
 */

public class MoveMultipleSiteNodeAction extends InfoGlueAbstractAction
{
    private final static Logger logger = Logger.getLogger(MoveMultipleSiteNodeAction.class.getName());

   	//  Initial params
    private Integer originalSiteNodeId;
    private Integer repositoryId;
    private Integer siteNodeId;
    private Integer parentSiteNodeId;
    private List qualifyers = new ArrayList();
    private boolean errorsOccurred = false;
	protected List repositories = null;
    
    //Move params
    protected String qualifyerXML = null;
    private Integer newParentSiteNodeId;
    
    //Tree params
    private Integer changeTypeId;
    private Integer topSiteNodeId;

    private ConstraintExceptionBuffer ceb;
   	private SiteNodeVO siteNodeVO;
   	
  
  	public MoveMultipleSiteNodeAction()
	{
		this(new SiteNodeVO());
	}
	
	public MoveMultipleSiteNodeAction(SiteNodeVO siteNodeVO)
	{
		this.siteNodeVO = siteNodeVO;
		this.ceb = new ConstraintExceptionBuffer();			
	}	

	public void setSiteNodeId(Integer siteNodeId)
	{
		this.siteNodeVO.setSiteNodeId(siteNodeId);
	}

	public Integer getSiteNodeId()
	{
		return siteNodeVO.getSiteNodeId();
	}
      
	
   public String doInput() throws Exception
    {    	
		this.repositories = RepositoryController.getController().getAuthorizedRepositoryVOList(this.getInfoGluePrincipal(), false);

        if(this.qualifyerXML != null && !this.qualifyerXML.equals(""))
        {
            this.qualifyers = parseSiteNodesFromXML(this.qualifyerXML);
        }
        else
        {
            SiteNodeVO siteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(getSiteNodeId());
            this.qualifyers.add(siteNodeVO);
        }
        
        return "input";
    }
    
    public String doExecute() throws Exception
    {
        if(this.newParentSiteNodeId == null)
        {
    		this.repositories = RepositoryController.getController().getAuthorizedRepositoryVOList(this.getInfoGluePrincipal(), false);
            return "chooseDestination";
        }
        
        ceb.throwIfNotEmpty();
    	
        try
		{
    		List<EventVO> resultingEvents = new ArrayList<EventVO>();

            if(this.qualifyerXML != null && this.qualifyerXML.length() != 0)
		    {
		        Document document = new DOMBuilder().getDocument(this.qualifyerXML);
				List siteNodes = parseSiteNodesFromXML(this.qualifyerXML);
				Iterator i = siteNodes.iterator();
				while(i.hasNext())
				{
				    SiteNodeVO siteNodeVO = (SiteNodeVO)i.next();
				    try
					{	
						Integer oldParentSiteNodeId = siteNodeVO.getParentSiteNodeId();

				        SiteNodeControllerProxy.getSiteNodeControllerProxy().acMoveSiteNode(this.getInfoGluePrincipal(), siteNodeVO, this.newParentSiteNodeId);

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
						}
						catch (Exception e) 
						{
							logger.error("Error adding publishing info: " + e.getMessage(), e);
						}
					}
					catch(Exception e)
					{
					    this.errorsOccurred = true;
					}
		    	}
		    }
            
			PublicationVO publicationVO = new PublicationVO();
		    publicationVO.setName("Direct publication by " + this.getInfoGluePrincipal().getName());
		    publicationVO.setDescription("Moved pages to " +  this.newParentSiteNodeId);
		    publicationVO.setRepositoryId(repositoryId);
		    publicationVO = PublicationController.getController().createAndPublish(publicationVO, resultingEvents, new HashMap(), new HashMap(), false, this.getInfoGluePrincipal());
		}
		catch(Exception e)
		{
		    logger.error("Problem moving pages: " + e.getMessage(), e);
		}
		
		this.topSiteNodeId = SiteNodeController.getController().getRootSiteNodeVO(this.repositoryId).getId();
		    
        return "success";
    }

	private List parseSiteNodesFromXML(String qualifyerXML)
	{
		List siteNodes = new ArrayList(); 
    	
		try
		{
			Document document = new DOMBuilder().getDocument(qualifyerXML);
			
			String entity = document.getRootElement().attributeValue("entity");
			
			Map addedSiteNodes = new HashMap();
			
			List children = document.getRootElement().elements();
			Iterator i = children.iterator();
			while(i.hasNext())
			{
				Element child = (Element)i.next();
				String id = child.getStringValue();
				String path = child.attributeValue("path");
				
				if(!addedSiteNodes.containsKey(id))
				{
				    SiteNodeVO siteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(new Integer(id));
				    siteNodes.add(siteNodeVO);     
					addedSiteNodes.put(id, siteNodeVO);
				}    
			}		        	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return siteNodes;
	}
	
    
    public Integer getChangeTypeId()
    {
        return changeTypeId;
    }
    
    public void setChangeTypeId(Integer changeTypeId)
    {
        this.changeTypeId = changeTypeId;
    }
    
    public Integer getNewParentSiteNodeId()
    {
        return newParentSiteNodeId;
    }
    
    public void setNewParentSiteNodeId(Integer newParentSiteNodeId)
    {
        this.newParentSiteNodeId = newParentSiteNodeId;
    }
    
    public Integer getOriginalSiteNodeId()
    {
        return originalSiteNodeId;
    }
    
    public void setOriginalSiteNodeId(Integer originalSiteNodeId)
    {
        this.originalSiteNodeId = originalSiteNodeId;
    }
    
    public Integer getParentSiteNodeId()
    {
        return parentSiteNodeId;
    }
    
    public void setParentSiteNodeId(Integer parentSiteNodeId)
    {
        this.parentSiteNodeId = parentSiteNodeId;
    }
    
    public String getQualifyerXML()
    {
        return qualifyerXML;
    }
    
    public void setQualifyerXML(String qualifyerXML)
    {
        this.qualifyerXML = qualifyerXML;
    }
    
    public Integer getRepositoryId()
    {
        return repositoryId;
    }
    
    public void setRepositoryId(Integer repositoryId)
    {
        this.repositoryId = repositoryId;
    }
    
    public Integer getTopSiteNodeId()
    {
        return topSiteNodeId;
    }
    
    public void setTopSiteNodeId(Integer topSiteNodeId)
    {
        this.topSiteNodeId = topSiteNodeId;
    }
    
    public boolean isErrorsOccurred()
    {
        return errorsOccurred;
    }
    
    public List getQualifyers()
    {
        return qualifyers;
    }
    
    public List getRepositories()
    {
        return repositories;
    }
    
    public SiteNodeVO getSiteNodeVO()
    {
        return siteNodeVO;
    }
}
