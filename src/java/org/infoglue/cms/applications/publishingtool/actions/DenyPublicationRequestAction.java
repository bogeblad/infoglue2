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

import java.util.ArrayList;
import java.util.List;

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.EventController;
import org.infoglue.cms.controllers.kernel.impl.simple.PublicationController;
import org.infoglue.cms.entities.workflow.EventVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsLogger;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Mattias Bogeblad
 *
 * This action denies a requested publishing of an entity or whatever is in the que. 
 * It deletes the old event and creates a new one with a reply to the requester.
 */

public class DenyPublicationRequestAction extends WebworkAbstractAction 
{
	private Integer eventId;
	private Integer repositoryId;
	private List events;

	public Integer getEventId() 
	{
		return eventId;
	}
	
	public void setEventId(Integer eventId) 
	{
		this.eventId = eventId;
	}
		
	public Integer getRepositoryId() 
	{
		return repositoryId;
	}

	public void setRepositoryId(Integer repositoryId) 
	{
		this.repositoryId = repositoryId;
	}
	
	
	protected String doExecute() throws Exception 
	{
		setEvents(getRequest().getParameterValues("sel"));
		
		PublicationController.denyPublicationRequest(this.events, this.getInfoGluePrincipal().getName(), getApplicationBaseUrl(getRequest()));
		return "success";
	}

	private String getApplicationBaseUrl(HttpServletRequest request)
	{
		return request.getRequestURL().toString().substring(0, request.getRequestURL().lastIndexOf("/") + 1) + "ViewCMSTool.action";
	}
	
	private void setEvents(String[] eventArguments) throws SystemException, Exception
	{
		List events = new ArrayList();
	
		for(int i=0; i < eventArguments.length; i++)
		{
			CmsLogger.logInfo("EventId:" + eventArguments[i]);
			EventVO eventVO = EventController.getEventVOWithId(new Integer(eventArguments[i]));
			events.add(eventVO);
		}		
	
		this.events = events;
	}

}
