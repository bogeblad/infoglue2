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
 *
 * $Id: WorkflowTestCase.java,v 1.4 2004/12/21 17:50:00 jed Exp $
 */
package org.infoglue.cms.util;

import java.util.*;

import javax.servlet.http.*;

import org.infoglue.cms.security.*;
import org.infoglue.cms.entities.mydesktop.*;
import org.infoglue.cms.controllers.kernel.impl.simple.WorkflowController;
import com.opensymphony.module.propertyset.PropertySet;

/**
 * Base class for workflow tests.  Uses the "Create News" sample workflow.  We don't do the "Preview news and approve"
 * step because we don't want to deal with saving content that we have to clean up later.  Going through the first step
 * should suffice to demonstrate that the right things are happening as far as the workflow is concerned. It is easy
 * enough to hook up "Preview news and approve" if it turns out that testing the step is worth the headache of figuring
 * out which content to remove when the test finishes.
 * @author <a href=mailto:jedprentice@gmail.com>Jed Prentice</a>
 */
public abstract class WorkflowTestCase extends InfoGlueTestCase
{
	/**
	 * The ID of the global action "Finish Workflow".  Since this ID is the same for both Create News and Create User,
	 * we can get away with hard-coding it for now.
	 */
	protected static final int FINISH_WORKFLOW = 201;

	private WorkflowVO workflow;
	private HttpSession session = new FakeHttpSession();

	/**
	 * Finishes, but does not delete.  Clearing the tables is tricky due to FK constraints in the OSWorkflow tables,
	 * although we should eventually figure out how to do it.  For now we'll have to live with finishing the workflow
	 * but leaving all the dead ones we create behind in the DB.  For mysql, periodically use the script
	 * testsrc/etc/clean-workflows.sql to clean up.
	 * @throws Exception
	 */
	protected void tearDown() throws Exception
	{
		finishWorkflow();
	}

	/**
	 * Subclasses must supply the workflow name
	 * @return the name of the workflow under test
	 */
	protected abstract String getWorkflowName();

	/**
	 * Returns the number of global actions.  Subclasses should override if the workflow under test has global
	 * actions; this default implementation returns zero.
	 * @return the number of global actions
	 */
	protected int getNumberOfGlobalActions()
	{
		return 0;
	}

	protected WorkflowVO getWorkflow()
	{
		return workflow;
	}

	protected void setWorkflow(WorkflowVO workflow)
	{
		this.workflow = workflow;
	}

	protected long getWorkflowId()
	{
		return getWorkflow().getId().longValue();
	}

	protected PropertySet getPropertySet()
	{
		return WorkflowController.getController().getPropertySet(getUserPrincipal(), getWorkflowId());
	}

	protected HttpSession getSession()
	{
		return session;
	}

	protected InfoGluePrincipal getUserPrincipal()
	{
		return (InfoGluePrincipal)getSession().getAttribute(InfoGlueAuthenticationFilter.INFOGLUE_FILTER_USER);
	}

	protected void setUserPrincipal(InfoGluePrincipal userPrincipal)
	{
		getSession().setAttribute(InfoGlueAuthenticationFilter.INFOGLUE_FILTER_USER, userPrincipal);
	}

	/**
	 * Starts the workflow by creating a new workflow instance
	 * @return the Id of the newly-created workflow
	 * @throws Exception
	 */
	protected WorkflowVO startWorkflow() throws Exception
	{
		return WorkflowController.getController().createWorkflowInstance(getUserPrincipal(), getWorkflowName());
	}

	/**
	 * Invokes the "Finish Workflow" action
	 * @throws Exception if an error occurs
	 */
	protected void finishWorkflow() throws Exception
	{
		invokeAction(new FakeHttpServletRequest(getSession()), FINISH_WORKFLOW);
		assertWorkflowFinished();
	}

	/**
	 * Invokes a workflow action
	 * @param request the HTTP request (or simulation thereof)
	 * @param actionId the ID of the desired workflow action
	 * @throws Exception if an error occurs
	 */
	protected void invokeAction(HttpServletRequest request, int actionId) throws Exception
	{
		workflow = WorkflowController.getController().invokeAction(getUserPrincipal(), request, getWorkflowId(), actionId);
	}

	/**
	 * Verifies that workflow is what we expect
	 * @param currentSteps the expected number of current steps
	 * @param historySteps the expected number of history steps
	 * @param availableActions the expected number of available actions
	 */
	protected void checkWorkflow(int currentSteps, int historySteps, int availableActions)
	{
		assertEquals("Wrong ID:", getWorkflowId(), workflow.getWorkflowId().longValue());
		assertEquals("Wrong name:", getWorkflowName(), workflow.getName());
		assertEquals("Wrong number of current steps:", currentSteps, workflow.getCurrentSteps().size());
		assertEquals("Wrong number of history steps:", historySteps, workflow.getHistorySteps().size());
		assertEquals("Wrong number of steps:", currentSteps + historySteps, workflow.getSteps().size());
		checkActions(availableActions, workflow.getAvailableActions());
		checkActions(getNumberOfGlobalActions(), workflow.getGlobalActions());
	}

	/**
	 * Verifies that each action in the list has at least a couple of critical properties defined
	 * @param expectedSize the expected size of the list
	 * @param actions a list of WorkflowActionVOs
	 */
	protected void checkActions(int expectedSize, List actions)
	{
		assertEquals("Wrong number of actions:", expectedSize, actions.size());

		for (Iterator i = actions.iterator(); i.hasNext();)
		{
			WorkflowActionVO action = (WorkflowActionVO)i.next();
			assertNotNull("Action should have a workflowId", action.getWorkflowId());
			assertNotNull("Action should have a name", action.getName());
		}
	}

	/**
	 * Asserts the the current workflow is finished by verifying that no workflow with workflowId is in the list
	 * of current workflows.
	 * @throws Exception if an error occurs.
	 */
	protected void assertWorkflowFinished() throws Exception
	{
		assertNull("Workflow should not be in current workflows list", findCurrentWorkflow());
	}

	/**
	 * Finds the current workflow that matches workflowId
	 * @return the WorkflowVO whose ID matches workflowId, or null if no match is found
	 * @throws Exception if an error occurs
	 */
	protected WorkflowVO findCurrentWorkflow() throws Exception
	{
		return findWorkflow(WorkflowController.getController().getCurrentWorkflowVOList(getUserPrincipal()));
	}

	/**
	 * Finds the workflow in the given list that matches workflowId.
	 * @param workflows a list of WorkflowVOs
	 * @return the WorkflowVO whose ID matches workflowId, or null if no match is found
	 */
	protected WorkflowVO findWorkflow(List workflows)
	{
		for (Iterator i = workflows.iterator(); i.hasNext();)
		{
			WorkflowVO workflow = (WorkflowVO)i.next();
			if (getWorkflowId() == workflow.getWorkflowId().longValue())
				return workflow;
		}

		return null;
	}

	/**
	 * Finds the workflow in the given list that matches the name of the workflow under test
	 * @param workflows a list of WorkflowVOs
	 * @return the WorkflowVO whose ID matches workflowId, or null if no match is found
	 */
	protected WorkflowVO findWorkflowByName(List workflows)
	{
		for (Iterator i = workflows.iterator(); i.hasNext();)
		{
			WorkflowVO workflow = (WorkflowVO)i.next();
			if (getWorkflowName().equals(workflow.getName()))
				return workflow;
		}

		return null;
	}

	/**
	 * Prints the workflows in the list to stdout.
	 * @param workflows a list of WorkflowVOs
	 */
	protected static void printWorkflows(List workflows)
	{
		for (Iterator i = workflows.iterator(); i.hasNext();)
		{
			WorkflowVO workflow = (WorkflowVO)i.next();
			System.out.println(workflow.getId() + " " + workflow.getName());
		}
	}

	/**
	 * Prints a list of steps to stdout
	 * @param steps a list of WorkflowStepVOs to print
	 */
	protected static void printSteps(List steps)
	{
		for (Iterator i = steps.iterator(); i.hasNext();)
		{
			WorkflowStepVO step = (WorkflowStepVO)i.next();
			System.out.println(step.getStepId() + " " + step.getName());
		}
	}

	/**
	 * Prints all available actions for the give workflow to stdout
	 * @param workflow the workflow whoise actions will be printed
	 */
	protected static void printAvailableActions(WorkflowVO workflow)
	{
		System.out.println("\n*** DEBUG: available actions for workflow " + workflow.getId() + " " + workflow.getName() +":");
		printActions(workflow.getAvailableActions());
	}

	/**
	 * Prints the given list of actions to stdout
	 * @param actions a list of WorkflowActionVOs
	 */
	protected static void printActions(List actions)
	{
		for (Iterator i = actions.iterator(); i.hasNext();)
			System.out.println(((WorkflowActionVO)i.next()).getName());
	}
}
