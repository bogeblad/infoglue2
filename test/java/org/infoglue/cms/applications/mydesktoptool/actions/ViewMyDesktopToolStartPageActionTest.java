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
 * $Id: ViewMyDesktopToolStartPageActionTest.java,v 1.1 2005/01/17 17:54:35 jed Exp $
 */
package org.infoglue.cms.applications.mydesktoptool.actions;

import org.infoglue.cms.util.*;
import org.infoglue.cms.applications.mydesktoptool.actions.ViewMyDesktopToolStartPageAction;
import webwork.action.ActionContext;

/**
 * Tests ViewMyDesktopToolStartPageAction using the Create News workflow.
 * @author <a href="mailto:jedprentice@gmail.com">Jed Prentice</a>
 */
public class ViewMyDesktopToolStartPageActionTest extends WorkflowTestCase
{
	private ViewMyDesktopToolStartPageAction action = new ViewMyDesktopToolStartPageAction();
	private FakeHttpServletRequest request = new FakeHttpServletRequest();

	protected String getWorkflowName()
	{
		return "Create News";
	}

	protected void setUp() throws Exception
	{
		super.setUp();
		setUserPrincipal(getAdminPrincipal());

		ActionContext.setRequest(request);
		action.setServletRequest(request);
		action.setServletResponse(new FakeHttpServletResponse());

		action.setWorkflowName(getWorkflowName());
		action.doStartWorkflow();
		setWorkflow(action.getWorkflow());
	}

	public void testExecute() throws Exception
	{
		WebWorkTestCase.assertSuccess(action.doExecute());
		assertEquals("Wrong number of available workflows:", 2, action.getAvailableWorkflowVOList().size());
		assertFalse("There should be at least 1 current workflow", action.getWorkflowVOList().isEmpty());
		assertFalse("There should be at least 1 current action", action.getWorkflowActionVOList().isEmpty());
	}

	public void testInvoke() throws Exception
	{
		request.setParameter("name", getName());
		request.setParameter("title", getName());
		request.setParameter("navigationTitle", getName());
		request.setParameter("leadIn", getName());
		request.setParameter("fullText", getName());

		action.setWorkflowId(getWorkflowId());
		action.setActionId(4);
		WebWorkTestCase.assertNone(action.doInvoke());

		assertNull("Available workflows should be null:", action.getAvailableWorkflowVOList());
		assertNull("Current workflows should be null:", action.getWorkflowVOList());
	}
}
