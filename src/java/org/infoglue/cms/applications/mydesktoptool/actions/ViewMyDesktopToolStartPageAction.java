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

package org.infoglue.cms.applications.mydesktoptool.actions;

import java.net.URLEncoder;
import java.util.*;

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.WorkflowController;
import org.infoglue.cms.entities.mydesktop.*;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.workflow.StepFilter;
import org.infoglue.cms.exception.SystemException;

import webwork.action.ActionContext;

/**
 * This class implements the action class for the startpage in the mydesktop tool.
 * <b>TODO:</b> This class needs to be restructured to take advantage of the fact that we have access to a WorkflowVO
 * containing the current state of the workflow.
 * @author Mattias Bogeblad
 */

public class ViewMyDesktopToolStartPageAction extends InfoGlueAbstractAction
{
	private List availableWorkflowVOList;
	private List workflowVOList;

	private long workflowId;
	private int actionId;
	private String url = "ViewMyDesktopToolStartPage.action";

	public List getWorkflowVOList()
	{
		return workflowVOList;
	}

	public List getAvailableWorkflowVOList()
	{
		return availableWorkflowVOList;
	}

	public List getWorkflowActionVOList()
	{
		return getAvailableActions(null);
	}

	public List getWorkflowActionVOList(StepFilter filter)
	{
		return getAvailableActions(filter);
	}

	public int getActionId()
	{
		return actionId;
	}

	public void setActionId(int actionId)
	{
		this.actionId = actionId;
	}

	public long getWorkflowId()
	{
		return workflowId;
	}

	public void setWorkflowId(long workflowId)
	{
		this.workflowId = workflowId;
	}

	public String getUrl()
	{
		return url;
	}

	/**
	 * Populates the lists of workflow and workflow action VOs.
	 * @return Action.SUCCESS
	 * @throws SystemException if a workflow error occurs
	 */
	public String doExecute() throws SystemException
	{
		populateLists();
		return SUCCESS;
	}

	/**
	 * Invokes an action in a workflow based on the values of the actionId and workflowId request parameters.  Assumes
	 * there will be one current action with a view URL; we iterate over the available actions until we find one with
	 * a view, then redirect to that view.  If there are other actions with different views, hard luck.
	 * @return NONE if there is an available action with a view and we redirect to the action's view page, otherwise
	 * returns "successInvoke"
	 * @throws Exception
	 * <b>TODO:</b> find a better heuristic for determining which view to go to.
	 */
	public String doInvoke() throws Exception
	{
		CmsLogger.logInfo("****************************************");
		CmsLogger.logInfo("workflowId:" + workflowId);
		CmsLogger.logInfo("actionId:" + actionId);
		CmsLogger.logInfo("****************************************");

		WorkflowVO workflow = WorkflowController.getController().invokeAction(getInfoGluePrincipal(),
																									 ActionContext.getRequest(),
																									 workflowId, actionId);

		for (Iterator i = workflow.getAvailableActions().iterator(); i.hasNext();)
		{
			url = getViewUrl((WorkflowActionVO)i.next());
			if (url.length() > 0)
				return redirect(url);
		}

		CmsLogger.logInfo("No new action...");
		populateLists();
		return "successInvoke";
	}

	/**
	 * Populates availableWorkflowVOList, workflowVOList, and workflowActionVOList.
	 * @throws SystemException if a workflow error occurs
	 */
	private void populateLists() throws SystemException
	{
		availableWorkflowVOList = WorkflowController.getController().getAvailableWorkflowVOList(getInfoGluePrincipal());
		workflowVOList = WorkflowController.getController().getCurrentWorkflowVOList(getInfoGluePrincipal());
	}

	/**
	 * Builds a list of all available actions for all workflows using the given step filter.  Assumes workflowVOList has
	 * been populated.
	 * @return a list of all available actions
	 * @throws NullPointerException if workflowVOList has not been populated
	 */
	private List getAvailableActions(StepFilter filter)
	{
		List actions = new ArrayList();
		for (Iterator i = workflowVOList.iterator(); i.hasNext();)
			actions.addAll(((WorkflowVO)i.next()).getAvailableActions(filter));

		return actions;
	}

	/**
	 * Creates the view URL from the given workflow action.
	 * @param action a workflow action
	 * @return the view URL
	 * @throws SystemException if an error occurs while encoding the URL
	 * <b>TODO:</b> Simplify this URL; this could be much, much simpler.
	 */
	private String getViewUrl(WorkflowActionVO action) throws Exception
	{
		if (!action.hasView())
			return "";

		StringBuffer buffer = new StringBuffer(action.getView());
		if (containsQuestionMark(action.getView()))
			buffer.append('&');
		else
			buffer.append('?');

		return buffer.append("workflowId=").append(workflowId).append("&actionId=").append(action.getId())
				.append("&returnAddress=").append(getReturnAddress()).toString();
	}

	private static boolean containsQuestionMark(String s)
	{
		return s.indexOf("?") >= 0;
	}

	/**
	 * Returns the return address
	 * @return the return address
	 * @throws SystemException if an error occurs while encoding the URL
	 * <b>TODO:</b> there are much simpler and more explicit ways to do this; let's use them
	 */
	private String getReturnAddress() throws Exception
	{
		return URLEncoder.encode(getURLBase() + "/ViewMyDesktopToolStartPage!invoke.action", "UTF-8");
	}

	private String redirect(String url) throws Exception
	{
		CmsLogger.logInfo("Url in doInvoke:" + url);
		getResponse().sendRedirect(url);
		return NONE;
	}
}
