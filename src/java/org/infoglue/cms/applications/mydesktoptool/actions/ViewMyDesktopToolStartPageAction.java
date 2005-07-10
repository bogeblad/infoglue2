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
import java.io.*;

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.WorkflowController;
import org.infoglue.cms.entities.mydesktop.*;

import org.infoglue.cms.util.workflow.StepFilter;
import org.infoglue.cms.exception.SystemException;

import webwork.action.ActionContext;
import com.opensymphony.workflow.*;

/**
 * This class implements the action class for the startpage in the mydesktop tool.
 * @author Mattias Bogeblad
 * @author <a href="mailto:jedprentice@gmail.com">Jed Prentice</a>
 */
public class ViewMyDesktopToolStartPageAction extends InfoGlueAbstractAction
{
	protected static final String INVALID_ACTION = "invalidAction";

	private static final WorkflowController controller = WorkflowController.getController();

	private List availableWorkflowVOList;
	private List workflowVOList;

	private WorkflowVO workflow = new WorkflowVO();
	private int actionId;

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

	/**
	 * Provides access to the underlying workflow
	 * @return a reference to the underlying workflow
	 */
	WorkflowVO getWorkflow()
	{
		return workflow;
	}

	/**
	 * Allows the workflow name to be set via the request parameter "workflowName".
	 * @param name the name of the desired workflow
	 */
	public void setWorkflowName(String name)
	{
		workflow.setName(name);
	}

	private String getWorkflowName()
	{
		return workflow.getName();
	}

	/**
	 * Allows the action ID to be set via the request parameter "actionId"
	 * @param actionId the ID of the action to execute
	 */
	public void setActionId(int actionId)
	{
		this.actionId = actionId;
	}

	/**
	 * Allows the workflowID to be set via the request parameter "workflowId"
	 * @param workflowId the ID of the desired workflow
	 */
	public void setWorkflowId(long workflowId)
	{
		workflow.setWorkflowId(new Long(workflowId));
	}

	private long getWorkflowId()
	{
		return workflow.getIdAsPrimitive();
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
	 * Starts the workflow specified in the request parameter "workflowName" with the initial action identified by
	 * the request parameter "actionId" and redirects to the view page for the desired initial action.  If no actionId
	 * is passed in the request, we assume that the ID of the initial action is zero.
	 * @return Action.NONE if the desired initial action has a view, otherwise the effect is the same as calling
	 * doExecute()
	 * @throws SystemException
	 * @see #doExecute
	 */
	public String doStartWorkflow() throws SystemException
	{
		workflow = controller.initializeWorkflow(getInfoGluePrincipal(), getWorkflowName(), actionId, getRequest().getParameterMap());
		return redirectToView();
	}

	/**
	 * Invokes an action in a workflow based on the values of the actionId and workflowId request parameters and
	 * redirects to the action's view if one has been defined.  If there is no action view, the user will be taken to
	 * ViewMyDesktopToolStartPage.action, i.e., the effect is the same as calling doExecute().
	 * <b>NOTE:</b> Assumes there will be one current action with a view; we iterate over the available actions until we
	 * find one with a view, then redirect to that view.  If there are other actions with different views, they will be
	 * ignored.
	 * @return NONE if there is an available action with a view and we redirect to the action's view page, otherwise
	 * performs the same operations as doExecute().
	 * @throws SystemException if an error occurs invoking the action or URL-encoding the action view
	 * @see #doExecute
	 */
	public String doInvoke() throws SystemException
	{
		getLogger().info("****************************************");
		getLogger().info("workflowId:" + getWorkflowId());
		getLogger().info("actionId:" + actionId);
		getLogger().info("****************************************");

		try
		{
			workflow = controller.invokeAction(getInfoGluePrincipal(), ActionContext.getRequest(), getWorkflowId(), actionId);
			return redirectToView();
		}
		catch (InvalidActionException e)
		{
			return INVALID_ACTION;
		}
		catch (WorkflowException e)
		{
			throw new SystemException(e);
		}
	}

	/**
	 * Redirects to view defined for the current action(s) in the given workflow.
	 * <b>NOTE:</b> Assumes there will be one current action with a view; we iterate over the available actions until we
	 * find one with a view, then redirect to that view.  If there are other actions with different views, they will be
	 * ignored.  If we go through the entire list without finding a view, the user will be sent to
	 * ViewMyDesktopToolStartPage.action, i.e., the effect will be the same as calling doExecute();
	 * @return NONE if there is an available action with a view and we redirect to the action's view page, otherwise
	 * performs the same operations as doExecute().
	 * @throws SystemException if an error occurs invoking the action or URL-encoding the action view
	 * @see #doExecute
	 * <b>TODO:</b> find a better heuristic for determining which view to go to.
	 */
	private String redirectToView() throws SystemException
	{
	    for (Iterator i = workflow.getAvailableActions().iterator(); i.hasNext();)
		{
			String url = getViewUrl((WorkflowActionVO)i.next());
			if (url.length() > 0)
				return redirect(url);
		}

		getLogger().info("No action view, coming back to mydesktop...");
		return doExecute();
	}

	/**
	 * Populates availableWorkflowVOList, workflowVOList, and workflowActionVOList.
	 * @throws SystemException if a workflow error occurs
	 */
	private void populateLists() throws SystemException
	{
		availableWorkflowVOList = controller.getAvailableWorkflowVOList(getInfoGluePrincipal());
		workflowVOList = controller.getCurrentWorkflowVOList(getInfoGluePrincipal());
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
		for (Iterator workflows = workflowVOList.iterator(); workflows.hasNext();)
			actions.addAll(((WorkflowVO)workflows.next()).getAvailableActions(filter));

		return actions;
	}

	/**
	 * Creates the view URL from the given workflow action.
	 * @param action a workflow action
	 * @return the view URL
	 * @throws SystemException if an error occurs while encoding the URL
	 */
	private String getViewUrl(WorkflowActionVO action) throws SystemException
	{
		if (!action.hasView())
			return "";

		StringBuffer buffer = new StringBuffer(action.getView());
		if (containsQuestionMark(action.getView()))
			buffer.append('&');
		else
			buffer.append('?');

		return buffer.append("workflowId=").append(getWorkflowId()).append("&actionId=").append(action.getId())
				.append("&returnAddress=").append(getReturnAddress()).append('&')
				.append(getRequest().getQueryString()).toString();
	}

	private static boolean containsQuestionMark(String s)
	{
		return s.indexOf("?") >= 0;
	}

	/**
	 * Returns the return address
	 * @return the return address
	 * @throws SystemException if an error occurs while encoding the URL
	 */
	private String getReturnAddress() throws SystemException
	{
		try
		{
			return URLEncoder.encode(getURLBase() + "/ViewMyDesktopToolStartPage!invoke.action", "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new SystemException(e);
		}
	}

	private String redirect(String url) throws SystemException
	{
		try
		{
			getLogger().info("Url in doInvoke:" + url);
			getResponse().sendRedirect(url);
			return NONE;
		}
		catch (IOException e)
		{
			throw new SystemException(e);
		}
	}
}
