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
 * $Id: ViewMyDesktopToolStartPageActionTest.java,v 1.2 2005/01/18 16:31:14 jed Exp $
 */
package org.infoglue.cms.applications.mydesktoptool.actions;

import java.util.*;

import org.infoglue.cms.util.*;
import org.infoglue.cms.applications.mydesktoptool.actions.ViewMyDesktopToolStartPageAction;
import org.infoglue.cms.applications.common.Session;
import org.infoglue.cms.controllers.kernel.impl.simple.WorkflowController;
import org.infoglue.cms.entities.mydesktop.*;
import webwork.action.ActionContext;

/**
 * @author <a href="mailto:jedprentice@gmail.com">Jed Prentice</a>
 */
public class ViewMyDesktopToolStartPageActionTest extends WebWorkTestCase
{
	private static final WorkflowController controller = WorkflowController.getController();

	private ViewMyDesktopToolStartPageAction action = new ViewMyDesktopToolStartPageAction();
	private FakeHttpServletRequest request = new FakeHttpServletRequest();

	protected void setUp() throws Exception
	{
		super.setUp();
		new Session().setInfoGluePrincipal(getAdminPrincipal());

		ActionContext.setRequest(request);
		action.setServletRequest(request);
		action.setServletResponse(new FakeHttpServletResponse());
	}

	public void testExecute() throws Exception
	{
		assertSuccess(action.doExecute());
		assertEquals("Wrong number of available workflows:", 2, action.getAvailableWorkflowVOList().size());
	}

	public void testExecute2ActiveWorkflows() throws Exception
	{
		WorkflowVO workflow1 = controller.initializeWorkflow(getAdminPrincipal(), "Create News", 0);
		WorkflowVO workflow2 = controller.initializeWorkflow(getAdminPrincipal(), "Create User", 0);

		try
		{
			assertSuccess(action.doExecute());
			assertEquals("Wrong number of available workflows:", 2, action.getAvailableWorkflowVOList().size());
			assertTrue("There should be at least 2 current workflows", action.getWorkflowVOList().size() >= 2);

			List availableActions = action.getWorkflowActionVOList();
			assertTrue("There should be at least 2 current actions", availableActions.size() >= 2);
			assertContains(availableActions,
								new WorkflowActionVO(new Integer(4), workflow1.getWorkflowId(), "Create news content"));
			assertContains(availableActions,
								new WorkflowActionVO(new Integer(4), workflow2.getWorkflowId(), "Register Name 1"));
		}
		finally
		{
			finishWorkflow(workflow1.getIdAsPrimitive());
			finishWorkflow(workflow2.getIdAsPrimitive());
		}
	}

	public void testStartWorkflow() throws Exception
	{
		action.setWorkflowName("Create News");
		action.doStartWorkflow();

		try
		{
			assertFalse("There should be at least 1 current workflow", action.getWorkflowVOList().isEmpty());
			List workflowActions = action.getWorkflowActionVOList();
			assertFalse("There should be at least 1 current action", workflowActions.isEmpty());
			assertContains(workflowActions,
								new WorkflowActionVO(new Integer(4), action.getWorkflow().getWorkflowId(), "Create news content"));
		}
		finally
		{
			finishWorkflow(action.getWorkflow().getIdAsPrimitive());
		}
	}

	public void testInvoke() throws Exception
	{
		WorkflowVO workflow = controller.initializeWorkflow(getAdminPrincipal(), "Create News", 0);

		try
		{
			request.setParameter("name", getName());
			request.setParameter("title", getName());
			request.setParameter("navigationTitle", getName());
			request.setParameter("leadIn", getName());
			request.setParameter("fullText", getName());

			action.setWorkflowId(workflow.getIdAsPrimitive());
			action.setActionId(4);
			assertNone(action.doInvoke());

			assertNull("Available workflows should be null:", action.getAvailableWorkflowVOList());
			assertNull("Current workflows should be null:", action.getWorkflowVOList());
		}
		finally
		{
			finishWorkflow(workflow.getIdAsPrimitive());
		}
	}

	private void finishWorkflow(long workflowId) throws Exception
	{
		controller.invokeAction(getAdminPrincipal(), request, workflowId, 201);
	}

	private static void assertContains(List actions, WorkflowActionVO expected)
	{
		boolean containsAction = false;
		for (Iterator i = actions.iterator(); i.hasNext() && !containsAction;)
			if (isSameAction(expected, (WorkflowActionVO)i.next()))
				containsAction = true;

		assertTrue("Action " + expected.getId() + " " + expected.getName() + " should be in the list of workflow actions",
					  containsAction);
	}

	/**
	 * A loose check to see if 2 actions are the same.  Compares id, workflowId, and name.  For now, this is sufficient.
	 * @param one an action
	 * @param another another action
	 * @return true if both actions have the same id, workflowId, and name; otherwise returns false.
	 */
	private static boolean isSameAction(WorkflowActionVO one, WorkflowActionVO another)
	{
		return one.getId().equals(another.getId())
				&& one.getWorkflowId().equals(another.getWorkflowId())
				&& one.getWorkflowId().equals(another.getWorkflowId())
				&& one.getName().equals(another.getName());
	}
}
