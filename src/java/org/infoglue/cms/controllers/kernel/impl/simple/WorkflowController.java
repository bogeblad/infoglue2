/* ===============================================================================
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

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.mydesktop.*;
import org.infoglue.cms.exception.SystemException;

import org.infoglue.cms.security.InfoGluePrincipal;

import org.infoglue.cms.util.workflow.WorkflowFacade;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * This controller acts as the api towards the OSWorkflow Workflow-engine.
 * @author Mattias Bogeblad
 * @author <a href="mailto:jedprentice@gmail.com">Jed Prentice</a>
 */
public class WorkflowController extends BaseController
{
	private static final WorkflowController controller = new WorkflowController();

	/**
	 * Returns the WorkflowController singleton
	 * @return a reference to a WorkflowController
	 */
	public static WorkflowController getController()
	{
		return controller;
	}

	private WorkflowController() {}

	/**
	 * Creates a new instance of a named workflow.
	 * @param userPrincipal the user principal representing the desired user
	 * @param workflowName the name of the workflow to create.
	 * @return a WorkflowVO representing the newly created workflow instance
	 * @throws SystemException if an error occurs while initiaizing the workflow
	 * @deprecated use initializeWorkflow() instead; this method relies on a hard-coded initial action ID of 0.
	 * @see #initializeWorkflow
	 */
	public WorkflowVO createWorkflowInstance(InfoGluePrincipal userPrincipal, String workflowName) throws SystemException
	{
		return initializeWorkflow(userPrincipal, workflowName, 0);
	}

	/**
	 * @param principal the user principal representing the desired user
	 * @param name the name of the workflow to create.
	 * @param actionId the ID of the initial action
	 * @return a WorkflowVO representing the newly created workflow instance
	 * @throws SystemException if an error occurs while initiaizing the workflow
	 */
	public WorkflowVO initializeWorkflow(InfoGluePrincipal principal, String name, int actionId) throws SystemException
	{
		try
		{
			return new WorkflowFacade(principal, name, actionId).createWorkflowVO();
		}
		catch (Exception e)
		{
			throw new SystemException(e);
		}
	}

	/**
	 * @param principal the user principal representing the desired user
	 * @param name the name of the workflow to create.
	 * @param actionId the ID of the initial action
	 * @return a WorkflowVO representing the newly created workflow instance
	 * @throws SystemException if an error occurs while initiaizing the workflow
	 */
	public WorkflowVO initializeWorkflow(InfoGluePrincipal principal, String name, int actionId, Map map) throws SystemException
	{
		try
		{
			return new WorkflowFacade(principal, name, actionId, map).createWorkflowVO();
		}
		catch (Exception e)
		{
			throw new SystemException(e);
		}
	}

	/**
	 * Returns a list of all available workflows, i.e., workflows defined in workflows.xml
	 * @param userPrincipal a user principal
	 * @return a list WorkflowVOs representing available workflows
	 */
	public List getAvailableWorkflowVOList(InfoGluePrincipal userPrincipal)
	{
		return new WorkflowFacade(userPrincipal).getDeclaredWorkflows();
	}

	/**
	 * Returns current workflows, i.e., workflows that are active.
	 * @param userPrincipal a user principal
	 * @return a list of WorkflowVOs representing all active workflows
	 * @throws SystemException if an error occurs while finding the current workflows
	 */
	public List getCurrentWorkflowVOList(InfoGluePrincipal userPrincipal) throws SystemException
	{
		return new WorkflowFacade(userPrincipal).getActiveWorkflows();
	}

	/**
	 * Invokes an action on a workflow for a given user and request
	 * <b>TODO:</b> Remove dependency on HTTP request
	 * @param principal the user principal
	 * @param request the current HTTP request
	 * @param workflowId the ID of the desired workflow
	 * @param actionId the ID of the desired action
	 * @return a WorkflowVO representing the current state of the workflow identified by workflowId
	 * @throws WorkflowException if a workflow error occurs
	 */
	public WorkflowVO invokeAction(InfoGluePrincipal principal, HttpServletRequest request, long workflowId, int actionId)
			throws WorkflowException
	{
		getLogger().info("invokeAction.............");
		getLogger().info("workflowId:" + workflowId);
		getLogger().info("actionId:" + actionId);

		Map parameters = new HashMap();
		parameters.putAll(request.getParameterMap());
		parameters.put("request", request);

		WorkflowFacade workflow = new WorkflowFacade(principal, workflowId);
		workflow.doAction(actionId, parameters);

		getLogger().info("invokeAction end.............");
		return workflow.createWorkflowVO();
	}

	/**
	 * Returns the workflow property set for a particular user and workflow
	 * @return the workflow property set for the workflow with workflowId and the user represented by userPrincipal
	 */
	public PropertySet getPropertySet(InfoGluePrincipal userPrincipal, long workflowId)
	{
		return new WorkflowFacade(userPrincipal, workflowId).getPropertySet();
	}

	/**
	 * Returns the contents of the PropertySet for a particular workflow
	 * @param userPrincipal a user principal
	 * @param workflowId the ID of the desired workflow
	 * @return a map containing the contents of the workflow property set
	 */
	public Map getProperties(InfoGluePrincipal userPrincipal, long workflowId)
	{
		getLogger().info("userPrincipal:" + userPrincipal);
		getLogger().info("workflowId:" + workflowId);

		PropertySet propertySet = getPropertySet(userPrincipal, workflowId);
		Map parameters = new HashMap();
		for (Iterator keys = getPropertySet(userPrincipal, workflowId).getKeys().iterator(); keys.hasNext();)
		{
			String key = (String)keys.next();
			parameters.put(key, propertySet.getString(key));
		}

		return parameters;
	}

	/**
	 * Returns all history steps for a workflow, i.e., all the steps that have already been performed.
	 * @param userPrincipal a user principal
	 * @param workflowId the ID of the desired workflow
	 * @return a list of WorkflowStepVOs representing all history steps for the workflow with workflowId
	 */
	public List getHistorySteps(InfoGluePrincipal userPrincipal, long workflowId)
	{
		return new WorkflowFacade(userPrincipal, workflowId).getHistorySteps();
	}

	/**
	 * Returns all current steps for a workflow, i.e., steps that could be performed in the workflow's current state
	 * @param userPrincipal a user principal
	 * @param workflowId the Id of the desired workflow
	 * @return a list of WorkflowStepVOs representing the current steps of the workflow with workflowId
	 */
	public List getCurrentSteps(InfoGluePrincipal userPrincipal, long workflowId)
	{
		return new WorkflowFacade(userPrincipal, workflowId).getCurrentSteps();
	}

	/**
	 * Returns all steps for a workflow definition.  These are the steps declared in the workfow descriptor; there is
	 * no knowledge of current or history steps at this point.
	 * @param userPrincipal an InfoGluePrincipal representing a system user
	 * @param workflowId a workflowId
	 * @return a list of WorkflowStepVOs representing all steps in the workflow.
	 */
	public List getAllSteps(InfoGluePrincipal userPrincipal, long workflowId)
	{
		return new WorkflowFacade(userPrincipal, workflowId).getDeclaredSteps();
	}

	/**
	 * Returns a new WorkflowActionVO.  This method is apparently unused, but is required by BaseController.  We don't
	 * use it internally because it requires a cast; it is simpler to just use <code>new</code> to create an instance.
	 * @return a new WorkflowActionVO.
	 */
	public BaseEntityVO getNewVO()
	{
		return new WorkflowActionVO();
	}
}
