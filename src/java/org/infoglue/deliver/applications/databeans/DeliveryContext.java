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

package org.infoglue.deliver.applications.databeans;

import org.infoglue.cms.applications.common.Session;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;

/**
 * @author Mattias Bogeblad
 *
 * This class is used to store the context of a page and get and set information that is central to it.
 * TODO - write more
 */

public class DeliveryContext
{
	public static final String META_INFO_BINDING_NAME 					= "Meta information";
	public static final String TEMPLATE_ATTRIBUTE_NAME   				= "Template";
	public static final String TITLE_ATTRIBUTE_NAME     		 		= "Title";
	public static final String NAV_TITLE_ATTRIBUTE_NAME 		 		= "NavigationTitle";
	/*
	protected static final String DISABLE_PAGE_CACHE_ATTRIBUTE_NAME		= "DisablePageCache";
	protected static final String PAGE_CONTENT_TYPE_ATTRIBUTE_NAME		= "ContentType";
	protected static final String ENABLE_PAGE_PROTECTION_ATTRIBUTE_NAME = "ProtectPage";
	protected static final String DISABLE_EDIT_ON_SIGHT_ATTRIBUTE_NAME	= "DisableEditOnSight";
	*/

	public static final boolean USE_LANGUAGE_FALLBACK        	= true;
	public static final boolean DO_NOT_USE_LANGUAGE_FALLBACK 	= false;
	public static final boolean USE_INHERITANCE 				= true;
	public static final boolean DO_NOT_USE_INHERITANCE 			= false;

	//These are the standard parameters which uniquely defines which page to show.
	private Integer siteNodeId = null;
	private Integer contentId  = null;
	private Integer languageId = null;

	//This parameter are set if you want to access a certain repository startpage
	private String repositoryName = null;

	private String pageKey = null;
	private String pagePath = null;

	private Session session = null;
	private WebworkAbstractAction webworkAbstractAction = null;

	//private InfoGluePrincipal infoGluePrincipal = null;


	public static DeliveryContext getDeliveryContext()
	{
		return new DeliveryContext();
	}

	private DeliveryContext()
	{
	}
	/*
	public static DeliveryContext getDeliveryContext(InfoGluePrincipal infoGluePrincipal)
	{
		return new DeliveryContext(infoGluePrincipal);
	}

	private DeliveryContext(InfoGluePrincipal infoGluePrincipal)
	{
		this.infoGluePrincipal = infoGluePrincipal;
	}
	*/

	public java.lang.Integer getSiteNodeId()
	{
		return this.siteNodeId;
	}

	public void setSiteNodeId(Integer siteNodeId)
	{
		this.siteNodeId = siteNodeId;
	}

	public Integer getContentId()
	{
		return this.contentId;
	}

	public void setContentId(Integer contentId)
	{
		this.contentId = contentId;
	}

	public Integer getLanguageId()
	{
		return this.languageId;
	}

	public void setLanguageId(Integer languageId)
	{
		this.languageId = languageId;
	}

	public String getRepositoryName()
	{
		return this.repositoryName;
	}

	public void setRepositoryName(String repositoryName)
	{
		this.repositoryName = repositoryName;
	}

	public String getPageKey()
	{
		return this.pageKey;
	}

	public String getPagePath()
	{
		return this.pagePath;
	}

	public void setPageKey(String pageKey)
	{
		this.pageKey = pageKey;
	}

	public void setPagePath(String pagePath)
	{
		this.pagePath = pagePath;
	}

	/*
	public InfoGluePrincipal getPrincipal()
	{
		return this.infoGluePrincipal;
	}
	*/

	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("siteNodeId:" + this.siteNodeId);
		sb.append("languageId:" + this.languageId);
		sb.append("contentId:" + this.contentId);
		//sb.append("InfoGluePrincipal:" + this.infoGluePrincipal);

		return sb.toString();
	}

	public Session getSession()
	{
		return this.session;
	}

	public void setSession(Session session)
	{
		this.session = session;
	}

	public WebworkAbstractAction getWebworkAbstractAction()
	{
		return webworkAbstractAction;
	}

	public void setWebworkAbstractAction(WebworkAbstractAction action)
	{
		webworkAbstractAction = action;
	}

}
