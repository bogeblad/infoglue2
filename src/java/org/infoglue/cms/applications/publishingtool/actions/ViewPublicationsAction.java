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

package org.infoglue.cms.applications.publishingtool.actions;

import java.util.List;

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.PublicationController;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.exception.SystemException;

/**
 * ViewPublicationsAction.java
 * Created on 2002-okt-01 
 * @author Stefan Sik, ss@frovi.com 
 * ss
 * 
 */
public class ViewPublicationsAction extends WebworkAbstractAction 
{
	private java.lang.Integer repositoryId;
	//private List contentToPublish;
	//private List siteNodeToPublish;
	
	private RepositoryVO repositoryVO;
	private List publicationEvents;
	private List editions;

	public ViewPublicationsAction getThis()
	{
		return this;
	}


	/**
	 * This default method fetched all old publications and also 
	 * all events lined up which has to do with publishing.
	 */
	
	protected String doExecute() throws Exception
	{
		this.repositoryVO      = RepositoryController.getController().getRepositoryVOWithId(this.repositoryId);
		this.publicationEvents = PublicationController.getPublicationEvents(this.repositoryId);
		this.editions          = PublicationController.getEditions(this.repositoryId);
		
		return "success";
	}

	/** 
	 * Returns the list of events that are up for review.
	 */
	
	public List getPublicationEvents()
	{
		return this.publicationEvents;
	}
	
/*
 	protected String doExecute() throws Exception
	{
		this.contentToPublish = PublicationController.getContentVersionVOToPublish(repositoryId);
		this.siteNodeToPublish = PublicationController.getSiteNodeVersionVOToPublish(repositoryId);
		this.editions = PublicationController.getEditions(repositoryId);
		
		return "success";
	}
*/	

	public static List getPublicationDetails(Integer publicationId) throws SystemException
	{
		return PublicationController.getPublicationDetailVOList(publicationId);
	}

	public ContentVO getOwningContent(Integer id) throws SystemException
	{
		return PublicationController.getOwningContentVO(id);
	}

	public SiteNodeVO getOwningSiteNode(Integer id) throws SystemException
	{
		return PublicationController.getOwningSiteNodeVO(id);
	}

	/*
	public List getPublicationContentVersionVOToPublish(Integer id) throws SystemException
	{
		return PublicationController.getPublicationContentVersionVOToPublish(id);
	}

	public List getPublicationSiteNodeVersionVOToPublish(Integer id) throws SystemException
	{
		return PublicationController.getPublicationSiteNodeVersionVOToPublish(id);
	}
	*/


	/**
	 * Returns the repositoryId.
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getRepositoryId()
	{
		return repositoryId;
	}

	/**
	 * Sets the repositoryId.
	 * @param repositoryId The repositoryId to set
	 */
	public void setRepositoryId(java.lang.Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	/**
	 * Returns the contentToPublish.
	 * @return List
	 */
/*
 	public List getContentToPublish()
	{
		return contentToPublish;
	}
*/

	/**
	 * Returns the editions.
	 * @return List
	 */
	public List getEditions()
	{
		return editions;
	}

	/**
	 * Sets the contentToPublish.
	 * @param contentToPublish The contentToPublish to set
	 */
/*
 	public void setContentToPublish(List contentToPublish)
	{
		this.contentToPublish = contentToPublish;
	}
*/

	/**
	 * Sets the editions.
	 * @param editions The editions to set
	 */
	public void setEditions(List editions)
	{
		this.editions = editions;
	}

	/**
	 * Returns the repositoryVO.
	 * @return RepositoryVO
	 */
	public RepositoryVO getRepositoryVO()
	{
		return repositoryVO;
	}

	/**
	 * Escapes the string
	 * @author mattias
	 */
	
	public String escape(String string)
	{
		return string.replace('\'', '´');
	}

	/**
	 * Returns the siteNodeToPublish.
	 * @return List
	 */
/*
 	public List getSiteNodeToPublish() {
		return siteNodeToPublish;
	}
*/
	/**
	 * Sets the siteNodeToPublish.
	 * @param siteNodeToPublish The siteNodeToPublish to set
	 */
/*
	public void setSiteNodeToPublish(List siteNodeToPublish) {
		this.siteNodeToPublish = siteNodeToPublish;
	}
*/
}
