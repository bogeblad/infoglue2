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
import org.infoglue.cms.util.CmsLogger;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.*;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.loader.*;
import com.opensymphony.workflow.query.*;
import com.opensymphony.workflow.spi.*;

/**
 * This controller acts as the api towards the OSWorkflow Workflow-engine.
 * @author Mattias Bogeblad
 * @author <a href="mailto:jedprentice@gmail.com">Jed Prentice</a>
 */
public class WorkflowController extends BaseController
{
	/**
	 * The initial action ID.  It is hard-coded to 0, which implies that the initial action of any workflow nust
	 * also be 0.  At some point we should find a way around this.
	 */
	private static final int INITIAL_ACTION = 0;

	private static final WorkflowController controller = new WorkflowController();

	/**
	 * Factory method
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
	 */
	public WorkflowVO createWorkflowInstance(InfoGluePrincipal userPrincipal, String workflowName) throws SystemException
	{
		try
		{
			WorkflowFacade workflow = new WorkflowFacade(userPrincipal);
			workflow.initialize(workflowName);
			CmsLogger.logInfo("Workflow initialized....");
			CmsLogger.logInfo("workflowId:" + workflow.getWorkflowId());
			CmsLogger.logInfo("name:" + workflow.getName());
			return workflow.createWorkflowVO();
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
	 * @throws SystemException if a workflow error occurs
	 */
	public WorkflowVO invokeAction(InfoGluePrincipal principal, HttpServletRequest request, long workflowId, int actionId)
			throws SystemException
	{
		CmsLogger.logInfo("invokeAction.............");
		CmsLogger.logInfo("workflowId:" + workflowId);
		CmsLogger.logInfo("actionId:" + actionId);

		try
		{
			Map parameters = new HashMap();
			parameters.putAll(request.getParameterMap());
			parameters.put("request", request);

			WorkflowFacade workflow = new WorkflowFacade(principal, workflowId);
			workflow.doAction(actionId, parameters);

			CmsLogger.logInfo("invokeAction end.............");
			return workflow.createWorkflowVO();
		}
		catch (Exception e)
		{
			throw new SystemException(e);
		}
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
		CmsLogger.logInfo("userPrincipal:" + userPrincipal);
		CmsLogger.logInfo("workflowId:" + workflowId);

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

	/**
	 * A facade to OSWorkflow that gives us a place to cache workflow data as we need it while interacting with it.
	 * This class has kind of a strange interface due to the idiosyncracies of the OSWorkflow, particularly
	 * the Workflow interface.  Anyway, the point was to encapsulate the interactions with OSWorkflow and eliminate the
	 * need to pass a Workflow reference and the workflow ID all over the place when extracting data from OSWorkflow
	 */
	private static class WorkflowFacade
	{
		private final InfoGluePrincipal userPrincipal;
		private final Workflow workflow;

		private long workflowId;
		private WorkflowDescriptor workflowDescriptor;

		/**
		 * Constructs a WorkflowFacade with the given user principal
		 * @param userPrincipal an InfoGluePrincipal representing a system user
		 */
		WorkflowFacade(InfoGluePrincipal userPrincipal)
		{
			this.userPrincipal = userPrincipal;
			workflow = new BasicWorkflow(userPrincipal.getName());
		}

		/**
		 * Constructs a WorkflowFacade for a user with the given workflow ID
		 * @param userPrincipal an InfoGluePrincipal representing a system user
		 * @param workflowId the ID representing an instance of the desired workflow
		 */
		WorkflowFacade(InfoGluePrincipal userPrincipal, long workflowId)
		{
			this(userPrincipal);
			this.workflowId = workflowId;
		}

		/**
		 * Returns the name of the underlying workflow
		 * @return the name of the underlying workflow
		 */
		String getName()
		{
			return workflow.getWorkflowName(workflowId);
		}

		/**
		 * Returns the workflow ID
		 * @return the workflow ID
		 */
		long getWorkflowId()
		{
			return workflowId;
		}

		/**
		 * Initializes the workflow, setting workflowId as a side-effect.
		 * @param name the name of the workflow to initialize
		 * @throws SystemException if a workflow error occurs.
		 */
		void initialize(String name) throws SystemException
		{
			try
			{
				workflowId = workflow.initialize(name, INITIAL_ACTION, new HashMap());
			}
			catch (Exception e)
			{
				throw new SystemException(e);
			}
		}

		/**
		 * Performs an action using the given inputs
		 * @param actionId the ID of the action to perform
		 * @param inputs a map of inputs to the action
		 * @throws SystemException if a workflow error occurs
		 */
		void doAction(int actionId, Map inputs) throws SystemException
		{
			try
			{
				workflow.doAction(workflowId, actionId, inputs);
			}
			catch (Exception e)
			{
				throw new SystemException(e);
			}
		}

		/**
		 * Returns the workflow descriptor, lazily initializing it if necessary.
		 * @return the workflow descriptor associated with the workflow with workflowId.
		 */
		WorkflowDescriptor getWorkflowDescriptor()
		{
			if (workflowDescriptor == null)
				workflowDescriptor = getWorkflowDescriptor(getName());

			return workflowDescriptor;
		}

		/**
		 * Returns the workflow descriptor for the workflow with the given name.
		 * @param name a workflow name
		 * @return the descriptor for the workflow with name
		 */
		private WorkflowDescriptor getWorkflowDescriptor(String name)
		{
			return workflow.getWorkflowDescriptor(name);
		}

		/**
		 * Returns the property set associated with the underlying workflow
		 * @return the property set associated with the underlying workflow
		 */
		PropertySet getPropertySet()
		{
			return workflow.getPropertySet(workflowId);
		}

		/**
		 * Returns a list of all declared workflows, i.e., workflows defined in workflows.xml
		 * @return a list WorkflowVOs representing all declared workflows
		 */
		List getDeclaredWorkflows()
		{
			String workflowNames[] = workflow.getWorkflowNames();
			List availableWorkflows = new ArrayList();

			for (int i = 0; i < workflowNames.length; i++)
			{
				CmsLogger.logInfo("workflowName:" + workflowNames[i]);
				if (workflow.canInitialize(workflowNames[i], INITIAL_ACTION))
					availableWorkflows.add(createWorkflowVO(workflowNames[i]));
			}

			return availableWorkflows;
		}

		/**
		 * Returns a list of all active workflows.
		 * @return a list of WorkflowVOs representing all active workflows
		 * @throws SystemException if an error occurs finding the active workflows
		 */
		List getActiveWorkflows() throws SystemException
		{
			List workflows = findActiveWorkflows();
			List workflowVOs = new ArrayList();

			for (Iterator iterator = workflows.iterator(); iterator.hasNext();)
			{
				workflowId = ((Long)iterator.next()).longValue();
				CmsLogger.logInfo("workflowId:" + workflowId);
				workflowVOs.add(createWorkflowVO());
			}

			return workflowVOs;
		}

		/**
		 * Finds all active workflows
		 * @return A list of workflowIds representing workflows that match the hard-wored query expression.
		 * @throws SystemException if a workflow error occurs during the search
		 */
		private List findActiveWorkflows() throws SystemException
		{
			try
			{
				return workflow.query(new WorkflowExpressionQuery(new FieldExpression(FieldExpression.STATE,
											FieldExpression.ENTRY, FieldExpression.EQUALS, new Integer(WorkflowEntry.ACTIVATED))));
			}
			catch (WorkflowException e)
			{
				throw new SystemException(e);
			}
		}

		/**
		 * Returns all current steps for the workflow, i.e., steps that could be performed in the workflow's current state
		 * Steps are filtered according to ownership; if a step has an owner, it is only included if the ownser matches
		 * the caller or if the current user is an administrator.
		 * TODO: Make this configurable for either roles or users
		 * @return a list of WorkflowStepVOs representing the current steps of the workflow with workflowId
		 */
		List getCurrentSteps()
		{
			return createStepVOs(workflow.getCurrentSteps(workflowId), new OwnerStepFilter(userPrincipal));
		}

		/**
		 * Returns all history steps for the workflow, i.e., all the steps that have already been performed.
		 * @return a list of WorkflowStepVOs representing all history steps for the workflow with workflowId
		 */
		private List getHistorySteps()
		{
			return createStepVOs(workflow.getHistorySteps(workflowId), new AllStepFilter());
		}

		/**
		 * Returns all steps for a workflow definition.  These are the steps declared in the workfow descriptor; there is
		 * no knowledge of current or history steps at this point.
		 * @return a list of WorkflowStepVOs representing all steps in the workflow.
		 */
		List getDeclaredSteps()
		{
			return getDeclaredSteps(getWorkflowDescriptor());
		}

		/**
		 * Creates a list of WorkflowStepVOs from the given list of steps using the given filter
		 * @param steps a list of Steps
		 * @param filter a filter to determine which steps to create
		 * @return a list of WorkflowStepVOs corresponding to all steps that pass the filter
		 */
		private List createStepVOs(List steps, StepFilter filter)
		{
			List stepVOs = new ArrayList();

			for (Iterator i = steps.iterator(); i.hasNext();)
			{
				Step step = (Step)i.next();
				if (filter.isAllowed(step))
					stepVOs.add(createStepVO(step));
			}

			return stepVOs;
		}

		/**
		 * Returns all steps for a workflow definition.  These are the steps declared in the workfow descriptor; there is
		 * no knowledge of current or history steps at this point.
		 * @param descriptor a workflow descriptor from which to get current steps
		 * @return a list of WorkflowStepVOs representing all steps in the workflow.
		 */
		List getDeclaredSteps(WorkflowDescriptor descriptor)
		{
			List steps = new ArrayList();
			for (Iterator i = descriptor.getSteps().iterator(); i.hasNext();)
				steps.add(createStepVO((StepDescriptor)i.next()));

			return steps;
		}

		/**
		 * Returns a list of global actions for a workflow
		 * @return a list of WorkflowActionVOs representing the global actions for the workflow with workflowId
		 */
		List getGlobalActions()
		{
			return createActionVOs(getWorkflowDescriptor().getGlobalActions());
		}

		/**
		 * Returns all actions in a stepDescriptor.
		 * @param stepDescriptor the desired stepDescriptor
		 * @return a list of all actions in stepDescriptor.
		 */
		List getAllActions(StepDescriptor stepDescriptor)
		{
			return createActionVOs(stepDescriptor.getActions());
		}

		/**
		 * Creates a list of WorkflowActionVOs from a list of action descriptors
		 * @param actionDescriptors a list of ActionDescriptors
		 * @return a list of WorkflowActionVOs representing actionDescriptors
		 */
		private List createActionVOs(List actionDescriptors)
		{
			List actions = new ArrayList();
			for (Iterator i = actionDescriptors.iterator(); i.hasNext();)
				actions.add(createActionVO((ActionDescriptor)i.next()));

			return actions;

		}

		/**
		 * Creates a new WorkflowVO.  This represents a pretty complete workflow; you get all the current steps, history
		 * steps, available actions, and global actions.
		 * @return a WorkflowVO representing workflow, with workflowId
		 */
		WorkflowVO createWorkflowVO()
		{
			WorkflowVO workflowVO = new WorkflowVO();
			workflowVO.setWorkflowId(new Long(workflowId));
			workflowVO.setName(workflow.getWorkflowName(workflowId));
			workflowVO.setCurrentSteps(getCurrentSteps());
			workflowVO.setHistorySteps(getHistorySteps());
			workflowVO.setGlobalActions(getGlobalActions());

			return workflowVO;
		}

		/**
		 * Creates a new WorkflowVO from workflow with the given name.  The resulting workflow VO contains only a
		 * minimal amount of data because we don't have the workflow ID.  Basically all you get is all the steps.
		 * @param name the name of the desired workflow
		 * @return a new WorkflowVO representing workflow
		 */
		WorkflowVO createWorkflowVO(String name)
		{
			WorkflowVO workflowVO = new WorkflowVO();
			workflowVO.setWorkflowId(null);
			workflowVO.setName(name);
			workflowVO.setDeclaredSteps(getDeclaredSteps(workflow.getWorkflowDescriptor(name)));
			return workflowVO;
		}

		/**
		 * Creates a WorkflowStepVO from the given step
		 * @param step the desired step
		 * @return a new WorkflowStepVO representing step.
		 */
		private WorkflowStepVO createStepVO(Step step)
		{
			CmsLogger.logInfo("step:" + step + ":" + step.getId());
			CmsLogger.logInfo("Owner:" + step.getOwner());

			WorkflowStepVO stepVO = new WorkflowStepVO();
			stepVO.setStepId(new Long(step.getId()));
			stepVO.setWorkflowId(new Long(workflowId));
			stepVO.setStatus(step.getStatus());
			stepVO.setStartDate(step.getStartDate());
			stepVO.setFinishDate(step.getFinishDate());
			stepVO.setOwner(step.getOwner());
			stepVO.setCaller(step.getCaller());

			StepDescriptor stepDescriptor = getWorkflowDescriptor().getStep(step.getStepId());
			stepVO.setName(stepDescriptor.getName());
			for (Iterator i = stepDescriptor.getActions().iterator(); i.hasNext();)
				stepVO.getActions().add(createActionVO((ActionDescriptor)i.next()));

			return stepVO;
		}

		/**
		 * Creates a WorkflowStepVO from a step descriptor.  Some of the step data, e.g., status, startDate,
		 * finishDate, etc. cannot be populated here because the step descriptor does not know about these things.
		 * @param stepDescriptor a step descriptor
		 * @return a WorkflowStepVO representing stepDescriptor
		 */
		private WorkflowStepVO createStepVO(StepDescriptor stepDescriptor)
		{
			WorkflowStepVO step = new WorkflowStepVO();
			step.setStepId(new Long(stepDescriptor.getId()));
			step.setName(stepDescriptor.getName());
			step.setStatus("Not started");
			step.setActions(getAllActions(stepDescriptor));
			return step;
		}

		/**
		 * Creates a WorkflowActionVO for the given action descriptor
		 * @param actionDescriptor an action descriptor
		 * @return a WorkflowActionVO representing actionDescriptor
		 */
		private WorkflowActionVO createActionVO(ActionDescriptor actionDescriptor)
		{
			CmsLogger.logInfo("Action:" + actionDescriptor.getId() + ":" + actionDescriptor.getName()
									+ ":" + actionDescriptor.getParent().getClass());

			WorkflowActionVO actionVO = new WorkflowActionVO();
			actionVO.setId(new Integer(actionDescriptor.getId()));
			actionVO.setWorkflowId(new Long(workflowId));
			actionVO.setName(actionDescriptor.getName());
			actionVO.setView(actionDescriptor.getView());
			actionVO.setAutoExecute(actionDescriptor.getAutoExecute());
			actionVO.setMetaAttributes(actionDescriptor.getMetaAttributes());
			if (actionDescriptor.getParent().getClass().equals(WorkflowDescriptor.class))
				actionVO.setIsGlobalAction(true);

			return actionVO;
		}
	}

	/**
	 * Provides a mechanism for filtering steps in a workflow based on the owner of the step and the current user
	 */
	private static interface StepFilter
	{
		/**
		 * Indicates whether the given step is allowed by the filter
		 * @param step a workflow step
		 * @return true if the step is allowed, otherwise returns false
		 */
		boolean isAllowed(Step step);
	}

	/**
	 * No-op filter that allows all steps
	 */
	private static class AllStepFilter implements StepFilter
	{
		public boolean isAllowed(Step step)
		{
			return true;
		}
	}

	/**
	 * Filters steps according to owner.  If a step has no owner, it is assumed that anyone can view it.
	 * An administrator can view all steps regardless of owner.
	 */
	private static class OwnerStepFilter implements StepFilter
	{
		private final InfoGluePrincipal userPrincipal;

		OwnerStepFilter(InfoGluePrincipal userPrincipal)
		{
			this.userPrincipal = userPrincipal;
		}

		/**
		 * Indicates whether the step is allowed
		 * @param step a workflow step.
		 * @return true if the step is owned by the current user, the current user is an administrator, or if the owner
		 * of the step is null; otherwise returns false.
		 */
		public boolean isAllowed(Step step)
		{
			return step.getOwner() == null
					|| step.getOwner().equalsIgnoreCase(userPrincipal.getName())
					|| userPrincipal.getIsAdministrator();
		}
	}
}
