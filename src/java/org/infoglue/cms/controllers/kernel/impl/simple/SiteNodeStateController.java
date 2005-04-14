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

import org.infoglue.cms.entities.content.Content;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.kernel.*;
import org.infoglue.cms.entities.management.AccessRight;
import org.infoglue.cms.entities.management.AccessRightVO;
import org.infoglue.cms.entities.management.AvailableServiceBinding;
import org.infoglue.cms.entities.management.InterceptionPoint;
import org.infoglue.cms.entities.management.Language;
import org.infoglue.cms.entities.structure.*;
import org.infoglue.cms.entities.structure.impl.simple.*;
import org.infoglue.cms.entities.workflow.*;

import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.CmsLogger;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.exolab.castor.jdo.Database;

public class SiteNodeStateController extends BaseController 
{
	/**
	 * This method handles versioning and state-control of siteNodes.
	 * Se inline documentation for further explainations.
	 */
	
    public static SiteNodeVersion changeState(Integer oldSiteNodeVersionId, Integer stateId, String versionComment, InfoGluePrincipal infoGluePrincipal, Integer siteNodeId, List resultingEvents) throws ConstraintException, SystemException
    {
        SiteNodeVersion newSiteNodeVersion = null; 
        
        Database db = CastorDatabaseService.getDatabase();
        ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
		
		beginTransaction(db);

		try
		{	
			SiteNodeVersion siteNodeVersion = SiteNodeVersionController.getSiteNodeVersionWithIdAsReadOnly(oldSiteNodeVersionId, db);
			CmsLogger.logInfo("siteNodeVersion:" + siteNodeVersion.getId() + ":" + siteNodeVersion.getStateId());
			
			newSiteNodeVersion = changeState(oldSiteNodeVersionId, stateId, versionComment, infoGluePrincipal, siteNodeId, db, resultingEvents);
        	
			List languages = LanguageController.getController().getLanguageList(siteNodeVersion.getOwningSiteNode().getRepository().getId(), db);
			Language masterLanguage = LanguageController.getController().getMasterLanguage(db, siteNodeVersion.getOwningSiteNode().getRepository().getId());
			
			Integer metaInfoAvailableServiceBindingId = null;
			Integer serviceBindingId = null;
			AvailableServiceBinding availableServiceBinding = AvailableServiceBindingController.getController().getAvailableServiceBindingWithName("Meta information", db, true);
			if(availableServiceBinding != null)
				metaInfoAvailableServiceBindingId = availableServiceBinding.getAvailableServiceBindingId();
			
			Collection serviceBindings = siteNodeVersion.getServiceBindings();
			Iterator serviceBindingIterator = serviceBindings.iterator();
			while(serviceBindingIterator.hasNext())
			{
				ServiceBinding serviceBinding = (ServiceBinding)serviceBindingIterator.next();
				if(serviceBinding.getAvailableServiceBinding().getId().intValue() == metaInfoAvailableServiceBindingId.intValue())
				{
					serviceBindingId = serviceBinding.getId();
					break;
				}
			}

			if(serviceBindingId != null)
			{
				List boundContents = ContentController.getBoundContents(serviceBindingId); 
				CmsLogger.logInfo("boundContents:" + boundContents.size());
				if(boundContents.size() > 0)
				{
					ContentVO contentVO = (ContentVO)boundContents.get(0);
					CmsLogger.logInfo("contentVO:" + contentVO.getId());
					
					Iterator languageIterator = languages.iterator();
					while(languageIterator.hasNext())
					{
						Language language = (Language)languageIterator.next();
						ContentVersion contentVersion = ContentVersionController.getContentVersionController().getLatestActiveContentVersion(contentVO.getId(), language.getId(), db);
						
						CmsLogger.logInfo("language:" + language.getId());
						CmsLogger.logInfo("contentVersion:" + contentVersion);
	
						if(language.getId().equals(masterLanguage.getId()) && contentVersion == null)
							throw new Exception("The contentVersion was null or states did not match.. the version and meta info content should allways match when it comes to master language version...");
		
						if(contentVersion != null && contentVersion.getStateId().equals(siteNodeVersion.getStateId()))
						{
							CmsLogger.logInfo("changing state on contentVersion:" + contentVersion.getId());
							List events = new ArrayList();
							ContentStateController.changeState(contentVersion.getId(), stateId, versionComment, infoGluePrincipal, contentVO.getId(), db, events);
						}
					}
				}
			}
        	
        	commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not complete the transaction:" + e, e);
            e.printStackTrace();
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }    	    	
    	
        return newSiteNodeVersion;
    }        


	/**
	 * This method handles versioning and state-control of siteNodes.
	 * Se inline documentation for further explainations.
	 */
	
    public static SiteNodeVersion changeState(Integer oldSiteNodeVersionId, Integer stateId, String versionComment, InfoGluePrincipal infoGluePrincipal, Integer siteNodeId, Database db, List resultingEvents) throws ConstraintException, SystemException
    {
		SiteNodeVersion newSiteNodeVersion = null;
		
        try
        { 
	    	//Here we create a new version if it was a state-change back to working, it's a copy of the publish-version
	    	if(stateId.intValue() == SiteNodeVersionVO.WORKING_STATE.intValue())
	    	{
	    		CmsLogger.logInfo("About to create a new working version");
				SiteNodeVersion oldSiteNodeVersion = SiteNodeVersionController.getController().getSiteNodeVersionWithId(oldSiteNodeVersionId, db);
	    	    
				if (siteNodeId == null)
					siteNodeId = new Integer(oldSiteNodeVersion.getOwningSiteNode().getId().intValue());

				SiteNodeVersionVO newSiteNodeVersionVO = new SiteNodeVersionVO();
		    	newSiteNodeVersionVO.setStateId(stateId);
				newSiteNodeVersionVO.setVersionComment("New working version");
		    	newSiteNodeVersionVO.setModifiedDateTime(new Date());
				newSiteNodeVersionVO.setVersionModifier(infoGluePrincipal.getName());

				newSiteNodeVersionVO.setContentType(oldSiteNodeVersion.getContentType());
				newSiteNodeVersionVO.setDisableEditOnSight(oldSiteNodeVersion.getDisableEditOnSight());
				newSiteNodeVersionVO.setDisablePageCache(oldSiteNodeVersion.getDisablePageCache());
				newSiteNodeVersionVO.setIsProtected(oldSiteNodeVersion.getIsProtected());
			    
				newSiteNodeVersion = SiteNodeVersionController.create(siteNodeId, infoGluePrincipal, newSiteNodeVersionVO, db);
				copyServiceBindings(oldSiteNodeVersion, newSiteNodeVersion, db);
				copyAccessRights(oldSiteNodeVersion, newSiteNodeVersion, db);
	    	}
	
	    	//If the user changes the state to publish we create a copy and set that copy to publish.
	    	if(stateId.intValue() == SiteNodeVersionVO.PUBLISH_STATE.intValue())
	    	{
	    		CmsLogger.logInfo("About to copy the working copy to a publish-one");
	    		//First we update the old working-version so it gets a comment
	    		SiteNodeVersion oldSiteNodeVersion = SiteNodeVersionController.getController().getSiteNodeVersionWithId(oldSiteNodeVersionId, db);
				
				if (siteNodeId == null)
					siteNodeId = new Integer(oldSiteNodeVersion.getOwningSiteNode().getId().intValue());

	    		SiteNodeVersionVO oldSiteNodeVersionVO = oldSiteNodeVersion.getValueObject();
	    	    oldSiteNodeVersion.setVersionComment(versionComment);
	
	    		//Now we create a new version which is basically just a copy of the working-version	    	
		    	SiteNodeVersionVO newSiteNodeVersionVO = new SiteNodeVersionVO();
		    	newSiteNodeVersionVO.setStateId(stateId);
		    	newSiteNodeVersionVO.setVersionComment(versionComment);
				newSiteNodeVersionVO.setVersionModifier(infoGluePrincipal.getName());
		    	newSiteNodeVersionVO.setModifiedDateTime(new Date()); 
		    	
				newSiteNodeVersionVO.setContentType(oldSiteNodeVersion.getContentType());
				newSiteNodeVersionVO.setDisableEditOnSight(oldSiteNodeVersion.getDisableEditOnSight());
				newSiteNodeVersionVO.setDisablePageCache(oldSiteNodeVersion.getDisablePageCache());
				newSiteNodeVersionVO.setIsProtected(oldSiteNodeVersion.getIsProtected());
			    
		    	newSiteNodeVersion = SiteNodeVersionController.create(siteNodeId, infoGluePrincipal, newSiteNodeVersionVO, db);
				copyServiceBindings(oldSiteNodeVersion, newSiteNodeVersion, db);
				copyAccessRights(oldSiteNodeVersion, newSiteNodeVersion, db);
	    	
				//Creating the event that will notify the editor...
				EventVO eventVO = new EventVO();
				eventVO.setDescription(newSiteNodeVersion.getVersionComment());
				eventVO.setEntityClass(SiteNodeVersion.class.getName());
				eventVO.setEntityId(new Integer(newSiteNodeVersion.getId().intValue()));
		        eventVO.setName(newSiteNodeVersion.getOwningSiteNode().getName());
				eventVO.setTypeId(EventVO.PUBLISH);
				eventVO = EventController.create(eventVO, newSiteNodeVersion.getOwningSiteNode().getRepository().getId(), infoGluePrincipal, db);			
				resultingEvents.add(eventVO);
	    	}
	
	    	if(stateId.intValue() == SiteNodeVersionVO.PUBLISHED_STATE.intValue())
	    	{
	    		CmsLogger.logInfo("About to publish an existing version");
	    		SiteNodeVersion oldSiteNodeVersion = SiteNodeVersionController.getController().getSiteNodeVersionWithId(oldSiteNodeVersionId, db);
				oldSiteNodeVersion.setStateId(stateId);
				oldSiteNodeVersion.setIsActive(new Boolean(true));
				newSiteNodeVersion = oldSiteNodeVersion;
	    	}        	
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not complete the transaction:" + e, e);
            e.printStackTrace();
            throw new SystemException(e.getMessage());
        }    	  
          	
    	return newSiteNodeVersion;
    }        



	/**
	 * This method copies all serviceBindings a siteNodeVersion has to the new siteNodeVersion.
	 */

    private static void copyServiceBindings(SiteNodeVersion originalSiteNodeVersion, SiteNodeVersion newSiteNodeVersion, Database db) throws ConstraintException, SystemException, Exception
	{
		Collection serviceBindings = originalSiteNodeVersion.getServiceBindings();	
		Iterator iterator = serviceBindings.iterator();
		while(iterator.hasNext())
		{
			ServiceBinding serviceBinding = (ServiceBinding)iterator.next();
			ServiceBindingVO serviceBindingVO = serviceBinding.getValueObject();			
			ServiceBindingVO newServiceBindingVO = new ServiceBindingVO();
			newServiceBindingVO.setBindingTypeId(serviceBindingVO.getBindingTypeId());
			newServiceBindingVO.setName(serviceBindingVO.getName());
			newServiceBindingVO.setPath(serviceBindingVO.getPath());
			ServiceBinding newServiceBinding = ServiceBindingController.create(newServiceBindingVO, serviceBinding.getAvailableServiceBinding().getAvailableServiceBindingId(), newSiteNodeVersion.getSiteNodeVersionId(), serviceBinding.getServiceDefinition().getServiceDefinitionId(), db);
			newSiteNodeVersion.getServiceBindings().add(newServiceBinding);
			copyQualifyers(serviceBinding, newServiceBinding, db);
		}
	}	


	/**
	 * This method copies all qualifyers a serviceBinding has to the new serviceBinding.
	 */
	private static void copyQualifyers(ServiceBinding originalServiceBinding, ServiceBinding newServiceBinding, Database db) throws ConstraintException, SystemException, Exception
	{
		Collection qualifyers = originalServiceBinding.getBindingQualifyers();	
		Collection newBindingQualifyers = new ArrayList();
		
		Iterator iterator = qualifyers.iterator();
		while(iterator.hasNext())
		{
			Qualifyer qualifyer = (Qualifyer)iterator.next();
			QualifyerVO qualifyerVO = qualifyer.getValueObject();
			Qualifyer newQualifyer = new QualifyerImpl();
			newQualifyer.setValueObject(qualifyerVO);
			newQualifyer.setServiceBinding((ServiceBindingImpl)newServiceBinding);
			newBindingQualifyers.add(newQualifyer);
			//QualifyerController.create(newQualifyerVO, newServiceBinding.getServiceBindingId(), db);
		}
		newServiceBinding.setBindingQualifyers(newBindingQualifyers);
					
	}	

	/**
	 * This method assigns the same access rights as the old content-version has.
	 */
	
	private static void copyAccessRights(SiteNodeVersion originalSiteNodeVersion, SiteNodeVersion newSiteNodeVersion, Database db) throws ConstraintException, SystemException, Exception
	{
		List interceptionPointList = InterceptionPointController.getController().getInterceptionPointList("SiteNodeVersion", db);
		CmsLogger.logInfo("interceptionPointList:" + interceptionPointList.size());
		Iterator interceptionPointListIterator = interceptionPointList.iterator();
		while(interceptionPointListIterator.hasNext())
		{
			InterceptionPoint interceptionPoint = (InterceptionPoint)interceptionPointListIterator.next();
			List accessRightList = AccessRightController.getController().getAccessRightListForEntity(interceptionPoint.getId(), originalSiteNodeVersion.getId().toString(), db);
			CmsLogger.logInfo("accessRightList:" + accessRightList.size());
			Iterator accessRightListIterator = accessRightList.iterator();
			while(accessRightListIterator.hasNext())
			{
				AccessRight accessRight = (AccessRight)accessRightListIterator.next();
				CmsLogger.logInfo("accessRight:" + accessRight.getId());
				
				AccessRightVO copiedAccessRight = accessRight.getValueObject().createCopy();
				copiedAccessRight.setParameters(newSiteNodeVersion.getId().toString());
				AccessRightController.getController().create(copiedAccessRight, interceptionPoint, db);
			}
		}
	}	
	
	/**
	 * This is a method that never should be called.
	 */

	public BaseEntityVO getNewVO()
	{
		return null;
	}

}
 
