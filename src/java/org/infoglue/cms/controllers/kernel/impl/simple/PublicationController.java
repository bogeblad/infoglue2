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

import java.util.*;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import org.infoglue.cms.entities.kernel.*;
import org.infoglue.cms.entities.content.*;
import org.infoglue.cms.entities.management.*;
import org.infoglue.cms.entities.publishing.*;
import org.infoglue.cms.entities.publishing.impl.simple.*;
import org.infoglue.cms.entities.structure.*;
import org.infoglue.cms.entities.workflow.*;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.ChangeNotificationController;
import org.infoglue.cms.util.NotificationMessage;
import org.infoglue.cms.util.mail.*;


/**
 * PublicationController.java
 *
 * @author Stefan Sik, Mattias Bogeblad
 */

public class PublicationController extends BaseController
{
	public static final int OVERIDE_WORKING = 1;
	public static final int LEAVE_WORKING   = 2;

	/**
	 * This method just returns the publication with the given id within the given transaction.
	 */
	public static Publication getPublicationWithId(Integer publicationId, Database db) throws SystemException
	{
		return (Publication) getObjectWithId(PublicationImpl.class, publicationId, db);
	}

	/**
	 * This method returns a list of those events that are publication events and
	 * concerns this repository
	 */
	public static List getPublicationEvents(Integer repositoryId) throws SystemException, Exception
	{
		return EventController.getPublicationEventVOListForRepository(repositoryId);
	}

	/**
	 * This method returns a list of earlier editions for this site.
	 */
	public static List getAllEditions(Integer repositoryId) throws SystemException
	{
    	Database db = CastorDatabaseService.getDatabase();
        beginTransaction(db);
		List res = new ArrayList();
        try
        {
            OQLQuery oql = db.getOQLQuery( "SELECT c FROM org.infoglue.cms.entities.publishing.impl.simple.PublicationImpl c WHERE c.repositoryId = $1 order by publicationDateTime desc");
			oql.bind(repositoryId);

        	QueryResults results = oql.execute(Database.ReadOnly);

			while (results.hasMore())
            {
            	Publication publication = (Publication)results.next();
            	res.add(publication.getValueObject());
            }

            commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }

        return res;
	}

	/**
	 * This method returns a list of earlier editions for this site.
	 */
	public static EditionBrowser getEditionPage(Integer repositoryId, int startIndex) throws SystemException
	{
		int pageSize = new Integer(CmsPropertyHandler.getProperty("edition.pageSize")).intValue();

    	Database db = CastorDatabaseService.getDatabase();
        beginTransaction(db);
        try
        {
            OQLQuery oql = db.getOQLQuery("SELECT c FROM org.infoglue.cms.entities.publishing.impl.simple.PublicationImpl c WHERE c.repositoryId = $1 order by publicationDateTime desc");
			oql.bind(repositoryId);
        	QueryResults results = oql.execute(Database.ReadOnly);

			List allEditions = Collections.list(results);
			List page = allEditions.subList(startIndex, Math.min(startIndex+pageSize, allEditions.size()));

			EditionBrowser browser = new EditionBrowser(allEditions.size(), pageSize, startIndex);

			List editionVOs = new ArrayList();
			for (Iterator iter = page.iterator(); iter.hasNext();)
			{
				Publication pub = (Publication) iter.next();
				PublicationVO pubVO = pub.getValueObject();
				pubVO.setPublicationDetails(toVOList(pub.getPublicationDetails()));
				editionVOs.add(pubVO);
			}

			browser.setEditions(editionVOs);

            commitTransaction(db);

			return browser;
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
	}


	/**
	 * This method denies a requested publishing. What that means is that the entity specified in the
	 * event does not get published and that the request-event is deleted and a new one created to
	 * deliver the message back to the requester. If it is a deny of publishing we also deletes the
	 * publish-version as it no longer has any purpose.
	 */
	public static void denyPublicationRequest(Integer eventId, String publisherUserName, String referenceUrl) throws SystemException
	{
    	Database db = CastorDatabaseService.getDatabase();
		beginTransaction(db);

        try
        {
        	Event event = EventController.getEventWithId(eventId, db);
        	if(event.getTypeId().intValue() == EventVO.PUBLISH.intValue())
        	{
        		event.setTypeId(EventVO.PUBLISH_DENIED);
        		if(event.getEntityClass().equals(ContentVersion.class.getName()))
	        	{
	        		ContentVersion contentVersion = ContentVersionController.getContentVersionController().getContentVersionWithId(event.getEntityId(), db);
        			if(contentVersion.getStateId().intValue() == ContentVersionVO.PUBLISHED_STATE.intValue())
        			{
        				//If its a published version we just deletes the event - we don't want to delete the version.
	        			EventController.delete(event, db);
	        		}
        			else
        			{
	        			Content content = contentVersion.getOwningContent();
	        			Language language = contentVersion.getLanguage();
	        			//event.setEntityId(ContentVersionController.getPreviousContentVersionVO(content.getId(), language.getId(), contentVersion.getId()).getId());
	        			event.setEntityId(ContentVersionController.getContentVersionController().getPreviousActiveContentVersionVO(content.getId(), language.getId(), contentVersion.getId()).getId());
	        			ContentVersionController.getContentVersionController().delete(contentVersion, db);
        			}
	        	}
	        	else if(event.getEntityClass().equals(SiteNodeVersion.class.getName()))
	        	{
	        		SiteNodeVersion siteNodeVersion = SiteNodeVersionController.getController().getSiteNodeVersionWithId(event.getEntityId(), db);
	        		if(siteNodeVersion.getStateId().intValue() == SiteNodeVersionVO.PUBLISHED_STATE.intValue())
        			{
        				//If its a published version we just deletes the event - we don't want to delete the version.
	        			EventController.delete(event, db);
	        		}
        			else
        			{
		        		SiteNode siteNode = siteNodeVersion.getOwningSiteNode();
	        			//event.setEntityId(SiteNodeVersionController.getPreviousSiteNodeVersionVO(siteNode.getId(), siteNodeVersion.getId()).getId());
	        			event.setEntityId(SiteNodeVersionController.getPreviousActiveSiteNodeVersionVO(siteNode.getId(), siteNodeVersion.getId()).getId());
	        			SiteNodeVersionController.getController().delete(siteNodeVersion, db);
	        			//db.remove(siteNodeVersion);
        			}
	        	}
        	}
        	else if(event.getTypeId().intValue() == EventVO.UNPUBLISH_LATEST.intValue())
        	{
        		event.setTypeId(EventVO.UNPUBLISH_DENIED);
	        	if(event.getEntityClass().equals(ContentVersion.class.getName()))
	        	{
	        		event.setEntityClass(Content.class.getName());
        			event.setEntityId(ContentVersionController.getContentVersionController().getContentVersionWithId(event.getEntityId(), db).getOwningContent().getId());
	        	}
	        	else if(event.getEntityClass().equals(SiteNodeVersion.class.getName()))
	        	{
	        		event.setEntityClass(SiteNode.class.getName());
        			event.setEntityId(SiteNodeVersionController.getController().getSiteNodeVersionWithId(event.getEntityId(), db).getOwningSiteNode().getId());
	        	}
        	}

        	InfoGluePrincipal infoGluePrincipal = InfoGluePrincipalControllerProxy.getController().getInfoGluePrincipal(event.getCreator());
        	mailNotification(event, publisherUserName, infoGluePrincipal.getEmail(), referenceUrl);

			commitTransaction(db);
        }
        catch(Exception e)
        {
        	CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
	}


	/**
	 * This method denies a list of requested publishing. What that means is that the entities specified in the
	 * event does not get published and that the request-event is deleted and a new one created to
	 * deliver the message back to the requester. If it is a deny of publishing we also deletes the
	 * publish-version as it no longer has any purpose.
	 */
	public static void denyPublicationRequest(List eventVOList, String publisherUserName, String referenceUrl) throws SystemException
	{
		Database db = CastorDatabaseService.getDatabase();
		beginTransaction(db);

		try
		{
			Iterator eventIterator = eventVOList.iterator();
			while(eventIterator.hasNext())
			{
				EventVO eventVO = (EventVO)eventIterator.next();

				Event event = EventController.getEventWithId(eventVO.getId(), db);
				if(event.getTypeId().intValue() == EventVO.PUBLISH.intValue())
				{
					event.setTypeId(EventVO.PUBLISH_DENIED);
					if(event.getEntityClass().equals(ContentVersion.class.getName()))
					{
						ContentVersion contentVersion = ContentVersionController.getContentVersionController().getContentVersionWithId(event.getEntityId(), db);
						if(contentVersion.getStateId().intValue() == ContentVersionVO.PUBLISHED_STATE.intValue())
						{
							//If its a published version we just deletes the event - we don't want to delete the version.
							EventController.delete(event, db);
						}
						else
						{
							Content content = contentVersion.getOwningContent();
							Language language = contentVersion.getLanguage();
							//event.setEntityId(ContentVersionController.getPreviousContentVersionVO(content.getId(), language.getId(), contentVersion.getId()).getId());
							event.setEntityId(ContentVersionController.getContentVersionController().getPreviousActiveContentVersionVO(content.getId(), language.getId(), contentVersion.getId()).getId());
							ContentVersionController.getContentVersionController().delete(contentVersion, db);
						}
					}
					else if(event.getEntityClass().equals(SiteNodeVersion.class.getName()))
					{
						SiteNodeVersion siteNodeVersion = SiteNodeVersionController.getController().getSiteNodeVersionWithId(event.getEntityId(), db);
						if(siteNodeVersion.getStateId().intValue() == SiteNodeVersionVO.PUBLISHED_STATE.intValue())
						{
							//If its a published version we just deletes the event - we don't want to delete the version.
							EventController.delete(event, db);
						}
						else
						{
							SiteNode siteNode = siteNodeVersion.getOwningSiteNode();
							//event.setEntityId(SiteNodeVersionController.getPreviousSiteNodeVersionVO(siteNode.getId(), siteNodeVersion.getId()).getId());
							event.setEntityId(SiteNodeVersionController.getPreviousActiveSiteNodeVersionVO(siteNode.getId(), siteNodeVersion.getId()).getId());
							SiteNodeVersionController.getController().delete(siteNodeVersion, db);
							//db.remove(siteNodeVersion);
						}
					}
				}
				else if(event.getTypeId().intValue() == EventVO.UNPUBLISH_LATEST.intValue())
				{
					event.setTypeId(EventVO.UNPUBLISH_DENIED);
					if(event.getEntityClass().equals(ContentVersion.class.getName()))
					{
						event.setEntityClass(Content.class.getName());
						event.setEntityId(ContentVersionController.getContentVersionController().getContentVersionWithId(event.getEntityId(), db).getOwningContent().getId());
					}
					else if(event.getEntityClass().equals(SiteNodeVersion.class.getName()))
					{
						event.setEntityClass(SiteNode.class.getName());
						event.setEntityId(SiteNodeVersionController.getController().getSiteNodeVersionWithId(event.getEntityId(), db).getOwningSiteNode().getId());
					}
				}

				InfoGluePrincipal infoGluePrincipal = InfoGluePrincipalControllerProxy.getController().getInfoGluePrincipal(event.getCreator());
				mailNotification(event, publisherUserName, infoGluePrincipal.getEmail(), referenceUrl);
			}

			commitTransaction(db);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
	}



	/**
	 * This method mails the rejection to the recipient.
	 */
	private static void mailNotification(Event event, String editorName, String recipient, String referenceUrl)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("CMS notification: The changes you wanted published concerning \"" + event.getName() + "\" was rejected by " + editorName + ". \n");
		sb.append("\n");
		sb.append("You can go to the application now by clicking here: " + referenceUrl + "\n");
		sb.append("\n");
		sb.append("Please revise your changes and retry. \n");
		sb.append("\n");
		sb.append("-----------------------------------------------------------------------\n");
		sb.append("This email was automatically generated and the sender is the CMS-system. \n");
		sb.append("Do not reply to this email. \n");

		try
		{
			String systemEmailSender = CmsPropertyHandler.getProperty("systemEmailSender");
			if(systemEmailSender == null || systemEmailSender.equalsIgnoreCase(""))
				systemEmailSender = "InfoGlueCMS@" + CmsPropertyHandler.getProperty("mail.smtp.host");

			MailServiceFactory.getService().send(systemEmailSender, recipient, "CMS - Publishing was denied!!", sb.toString(), "text/plain", "UTF-8");
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("The notification was not sent. Reason:" + e.getMessage(), e);
		}
	}


	/**
	 * This method creates a new publication with the concerned events carried out.
	 */
	public static PublicationVO createAndPublish(PublicationVO publicationVO, List events, InfoGluePrincipal infoGluePrincipal) throws SystemException
    {
    	Database db = CastorDatabaseService.getDatabase();

		try
		{
	        beginTransaction(db);

	    	CmsLogger.logInfo("*********************************");
	    	CmsLogger.logInfo("Creating edition ");
	    	CmsLogger.logInfo("*********************************");

	        Publication publication = new PublicationImpl();
	        publicationVO.setPublicationDateTime(Calendar.getInstance().getTime());
	        publication.setValueObject(publicationVO);
			publication.setPublisher(infoGluePrincipal.getName());

			Iterator eventIterator = events.iterator();
			while(eventIterator.hasNext())
			{
				EventVO event = (EventVO)eventIterator.next();
				createPublicationInformation(publication, EventController.getEventWithId(event.getId(), db), infoGluePrincipal, db);
			}

			db.create(publication);

	        commitTransaction(db);
	    	CmsLogger.logInfo("Done with the transaction for the publication...");
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred when we tried to commit the publication: " + e.getMessage(), e);
	    	rollbackTransaction(db);
		}

        // Replicate database!!!
        try
		{
	    	CmsLogger.logInfo("Starting replication...");
			ReplicationMySqlController.updateSlaveServer();
	    	CmsLogger.logInfo("Finished replication...");
		}
		catch (Exception e)
		{
			CmsLogger.logSevere("An error occurred when we tried to replicate the data:" + e.getMessage(), e);
		}

		// Update live site!!!
		try
		{
			CmsLogger.logInfo("Notifying the entire system about a publishing...");
			NotificationMessage notificationMessage = new NotificationMessage("PublicationController.createAndPublish():", PublicationImpl.class.getName(), infoGluePrincipal.getName(), NotificationMessage.PUBLISHING, publicationVO.getId(), publicationVO.getName());
			//NotificationMessage notificationMessage = new NotificationMessage("PublicationController.createAndPublish():", NotificationMessage.PUBLISHING_TEXT, infoGluePrincipal.getName(), NotificationMessage.PUBLISHING, publicationVO.getId(), "org.infoglue.cms.entities.publishing.impl.simple.PublicationImpl");
			ChangeNotificationController.getInstance().addNotificationMessage(notificationMessage);
			CmsLogger.logInfo("Finished Notifying...");
		}
		catch (Exception e)
		{
			CmsLogger.logSevere("An error occurred when we tried to replicate the data:" + e.getMessage(), e);
		}

        return publicationVO;
    }



	/**
	 * Creates a connection between contentversion or siteNodeVersion and publication, ie adds a contentversion
	 * to the publication.
	 */
	private static void createPublicationInformation(Publication publication, Event event, InfoGluePrincipal infoGluePrincipal, Database db) throws Exception
	{
		String entityClass = event.getEntityClass();
		Integer entityId   = event.getEntityId();
		Integer typeId     = event.getTypeId();
		CmsLogger.logInfo("entityClass:" + entityClass);
		CmsLogger.logInfo("entityId:" + entityId);
		CmsLogger.logInfo("typeId:" + typeId);

		// Publish contentversions
        if(entityClass.equals(ContentVersion.class.getName()))
		{
			ContentVersion contentVersion = null;
			ContentVersion oldContentVersion = ContentVersionController.getContentVersionController().getContentVersionWithId(entityId, db);
			if(oldContentVersion != null && oldContentVersion.getOwningContent() != null && typeId.intValue() == EventVO.UNPUBLISH_LATEST.intValue())
			{
				contentVersion = ContentVersionController.getContentVersionController().getLatestPublishedContentVersion(oldContentVersion.getOwningContent().getContentId(), oldContentVersion.getLanguage().getLanguageId(), db);
				if(contentVersion != null)
				{
					//We just set the published version to not active.
					contentVersion.setIsActive(new Boolean(false));
				}
			}
			else if(oldContentVersion != null && oldContentVersion.getOwningContent() != null)
			{
			    List events = new ArrayList();
				Integer contentId = oldContentVersion.getOwningContent().getContentId();
	    		ContentVersion newContentVersion = ContentStateController.changeState(entityId, ContentVersionVO.PUBLISHED_STATE, "Published", infoGluePrincipal, contentId, db, events);
	    		contentVersion = ContentVersionController.getContentVersionController().getContentVersionWithId(newContentVersion.getContentVersionId(), db);
			}

			if(contentVersion != null)
			{
				//The contentVersion in here is the version we have done something with...
				PublicationDetail publicationDetail = new PublicationDetailImpl();
				publicationDetail.setCreationDateTime(new Date());
				publicationDetail.setDescription(event.getDescription());
				publicationDetail.setEntityClass(entityClass);
				publicationDetail.setEntityId(contentVersion.getId());
				publicationDetail.setName(event.getName());
				publicationDetail.setTypeId(event.getTypeId());
				publicationDetail.setPublication((PublicationImpl)publication);
				publicationDetail.setCreator(event.getCreator());

				Collection publicationDetails = publication.getPublicationDetails();
				if(publicationDetails == null)
					publication.setPublicationDetails(new ArrayList());

				publication.getPublicationDetails().add(publicationDetail);
				db.remove(event);
			}
		}

		// Publish sitenodeversions
        if(entityClass.equals(SiteNodeVersion.class.getName()))
		{
			SiteNodeVersion siteNodeVersion = null;
			SiteNodeVersion oldSiteNodeVersion = SiteNodeVersionController.getController().getSiteNodeVersionWithId(entityId, db);
			if(oldSiteNodeVersion != null && oldSiteNodeVersion.getOwningSiteNode() != null && typeId.intValue() == EventVO.UNPUBLISH_LATEST.intValue())
			{
				siteNodeVersion = SiteNodeVersionController.getLatestPublishedSiteNodeVersion(oldSiteNodeVersion.getOwningSiteNode().getSiteNodeId(), db);
				if(siteNodeVersion != null)
				{
					//We just set the published version to not active.
					siteNodeVersion.setIsActive(new Boolean(false));
				}
			}
			else if(oldSiteNodeVersion != null && oldSiteNodeVersion.getOwningSiteNode() != null)
			{
			    List events = new ArrayList();
				Integer siteNodeId = oldSiteNodeVersion.getOwningSiteNode().getSiteNodeId();
	    		SiteNodeVersion newSiteNodeVersion = SiteNodeStateController.getController().changeState(entityId, SiteNodeVersionVO.PUBLISHED_STATE, "Published", infoGluePrincipal, siteNodeId, db, events);
	    		siteNodeVersion = SiteNodeVersionController.getController().getSiteNodeVersionWithId(newSiteNodeVersion.getSiteNodeVersionId(), db);
			}

			if(siteNodeVersion != null)
			{
				//The siteNodeVersion in here is the version we have done something with...
				PublicationDetail publicationDetail = new PublicationDetailImpl();
				publicationDetail.setCreationDateTime(new Date());
				publicationDetail.setDescription(event.getDescription());
				publicationDetail.setEntityClass(entityClass);
				publicationDetail.setEntityId(siteNodeVersion.getId());
				publicationDetail.setName(event.getName());
				publicationDetail.setTypeId(event.getTypeId());
				publicationDetail.setPublication((PublicationImpl)publication);
				publicationDetail.setCreator(event.getCreator());

				Collection publicationDetails = publication.getPublicationDetails();
				if(publicationDetails == null)
					publication.setPublicationDetails(new ArrayList());

				publication.getPublicationDetails().add(publicationDetail);
				db.remove(event);
			}
		}
	}

	/**
	 * This method (currently used for testing) will create a Publication with associated PublicationDetails children.
	 */
	public static PublicationVO create(PublicationVO publication) throws SystemException
    {
		Database db = beginTransaction();

        try
        {
			PublicationImpl p = new PublicationImpl();
			p.setValueObject(publication);
			p.setPublicationDetails(new ArrayList());
			for (Iterator iter = publication.getPublicationDetails().iterator(); iter.hasNext();)
			{
				PublicationDetailVO detailVO = (PublicationDetailVO) iter.next();
				PublicationDetail pd = new PublicationDetailImpl();
				pd.setPublication(p);
				pd.setValueObject(detailVO);
				p.getPublicationDetails().add(pd);
			}

			db.create(p);

			PublicationVO returnPub = p.getValueObject();
			returnPub.setPublicationDetails(toVOList(p.getPublicationDetails()));

			commitTransaction(db);
			return returnPub;
        }
        catch(Exception e)
        {
			e.printStackTrace();
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
	}

	/**
	 * This method returns a list of all details a publication has.
	 */
	public static List getPublicationDetailVOList(Integer publicationId) throws SystemException
	{
		List publicationDetails = new ArrayList();

		Database db = CastorDatabaseService.getDatabase();
		beginTransaction(db);

        try
        {
        	Publication publication = getPublicationWithId(publicationId, db);
        	Collection details = publication.getPublicationDetails();
            publicationDetails = toVOList(details);

			commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }

        return publicationDetails;
	}


	/**
	 * This method unpublishes all entities in an edition if they are not unpublish-events.
	 */
	public static PublicationVO unPublish(Integer publicationId, InfoGluePrincipal infoGluePrincipal) throws SystemException
	{
		CmsLogger.logInfo("Starting unpublishing operation...");

		Database db = CastorDatabaseService.getDatabase();
		Publication publication = null;

        beginTransaction(db);

        try
        {
			publication = getPublicationWithId(publicationId, db);
			Collection publicationDetails = publication.getPublicationDetails();

			Iterator i = publicationDetails.iterator();
			while (i.hasNext())
			{
				PublicationDetail publicationDetail = (PublicationDetail)i.next();
				//We unpublish them as long as they are not unpublish-requests.
				if(publicationDetail.getTypeId().intValue() != PublicationDetailVO.UNPUBLISH_LATEST.intValue())
				{
					unpublishEntity(publicationDetail, infoGluePrincipal, db);
				}
			}

            db.remove(publication);

			commitTransaction(db);
			CmsLogger.logInfo("Done unpublishing operation...");
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }

        try
		{
			CmsLogger.logInfo("Starting replication operation...");
			ReplicationMySqlController.updateSlaveServer();
			CmsLogger.logInfo("Done replication operation...");
		}
		catch (Exception e)
		{
			CmsLogger.logSevere("An error occurred when we tried to replicate the data:" + e.getMessage(), e);
		}

		//Update live site!!!
		try
		{
			CmsLogger.logInfo("Notifying the entire system about an unpublishing...");
			NotificationMessage notificationMessage = new NotificationMessage("PublicationController.unPublish():", PublicationImpl.class.getName(), infoGluePrincipal.getName(), NotificationMessage.UNPUBLISHING, publication.getId(), publication.getName());
	      	ChangeNotificationController.getInstance().addNotificationMessage(notificationMessage);
	      	CmsLogger.logInfo("Finished Notifying...");
		}
		catch (Exception e)
		{
			CmsLogger.logSevere("An error occurred when we tried to replicate the data:" + e.getMessage(), e);
		}

        return publication.getValueObject();
	}


	/**
	 * Unpublished a entity by just setting it to isActive = false.
	 */
	private static void unpublishEntity(PublicationDetail publicationDetail, InfoGluePrincipal infoGluePrincipal, Database db) throws ConstraintException, SystemException
	{
		Integer repositoryId = null;

		if(publicationDetail.getEntityClass().equals(ContentVersion.class.getName()))
		{
			ContentVersion contentVersion = ContentVersionController.getContentVersionController().getContentVersionWithId(publicationDetail.getEntityId(), db);
			contentVersion.setIsActive(new Boolean(false));
			repositoryId = contentVersion.getOwningContent().getRepository().getId();
		}
		else if(publicationDetail.getEntityClass().equals(SiteNodeVersion.class.getName()))
		{
		 	SiteNodeVersion siteNodeVersion = SiteNodeVersionController.getController().getSiteNodeVersionWithId(publicationDetail.getEntityId(), db);
			siteNodeVersion.setIsActive(new Boolean(false));
			repositoryId = siteNodeVersion.getOwningSiteNode().getRepository().getId();
		}

		EventVO eventVO = new EventVO();
		eventVO.setDescription(publicationDetail.getDescription());
		eventVO.setEntityClass(publicationDetail.getEntityClass());
		eventVO.setEntityId(publicationDetail.getEntityId());
		eventVO.setName(publicationDetail.getName());
		eventVO.setTypeId(EventVO.PUBLISH);
		EventController.create(eventVO, repositoryId, infoGluePrincipal, db);

	}


	/**
	 * This method returns the owning content to a contentVersion.
	 */
	public static ContentVO getOwningContentVO(Integer id) throws SystemException
    {
	    ContentVO contentVO = null;

    	Database db = CastorDatabaseService.getDatabase();
		ContentVersion contentVersion = null;
        beginTransaction(db);
        try
        {
	    	contentVersion = ContentVersionController.getContentVersionController().getContentVersionWithId(id, db);
	    	System.out.println("contentVersion:" + contentVersion.getId());
	    	//System.out.println("contentVersion:" + contentVersion);
	    	contentVO = contentVersion.getOwningContent().getValueObject();
	    	//Content content = ContentController.getContentController().getContentWithId(contentVersion.getValueObject().getContentId(), db);
	    	//contentVO = content.getValueObject();

	    	commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }

    	return contentVO;
    }

	/**
	 * This method returns the owning siteNode to a siteNodeVersion.
	 */
    public static SiteNodeVO getOwningSiteNodeVO(Integer id) throws SystemException
    {
    	Database db = CastorDatabaseService.getDatabase();
		SiteNodeVersion siteNodeVersion = null;
        beginTransaction(db);
        try
        {
	    	siteNodeVersion = SiteNodeVersionController.getController().getSiteNodeVersionWithId(id, db);
	    	commitTransaction(db);
        }
        catch(Exception e)
        {
        	CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }

    	return siteNodeVersion.getOwningSiteNode().getValueObject();
    }


	/**
	 * This is a method that gives the user back an newly initialized ValueObject for this entity that the controller
	 * is handling.
	 */
	public BaseEntityVO getNewVO()
	{
		return new PublicationVO();
	}
}
