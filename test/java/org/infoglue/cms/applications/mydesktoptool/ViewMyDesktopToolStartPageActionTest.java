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
 * $Id: ViewMyDesktopToolStartPageActionTest.java,v 1.1 2004/11/29 15:29:10 jed Exp $
 */
package org.infoglue.cms.applications.mydesktoptool;

import org.infoglue.cms.util.*;
import org.infoglue.cms.applications.mydesktoptool.actions.ViewMyDesktopToolStartPageAction;
import junit.framework.*;
import junit.swingui.TestRunner;
import webwork.action.ActionContext;

public class ViewMyDesktopToolStartPageActionTest extends WorkflowTestCase
{
	private ViewMyDesktopToolStartPageAction action = new ViewMyDesktopToolStartPageAction();
	private FakeHttpServletRequest request = new FakeHttpServletRequest();

	protected void setUp() throws Exception
	{
		super.setUp();
		setUserPrincipal(getAdminPrincipal());

		request.setSession(getSession());
		ActionContext.setRequest(request);
		action.setServletRequest(request);
		action.setServletResponse(new FakeHttpServletResponse());

		setWorkflow(startWorkflow());		
	}

	public void testExecute() throws Exception
	{
		WebWorkTestCase.assertSuccess(action.doExecute());
		assertEquals("Wrong number of available workflows:", 2, action.getAvailableWorkflowVOList().size());
		assertFalse("There should be at least 1 current workflow", action.getWorkflowVOList().isEmpty());
		assertFalse("There should be at least 1 current action", action.getWorkflowActionVOList().isEmpty());
	}

	public void testInvokeCreateNews() throws Exception
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
		assertEquals("Wrong number of current actions:", 1, action.getWorkflowActionVOList().size());
		assertTrue("URL should contain view for 'Preview News and Approve'",
					  action.getUrl().indexOf("workflows/ig_create_news/previewNewsAndApprove.jsp") >= 0);
	}

	protected String getWorkflowName()
	{
		return "Create News";
	}

	public static Test suite()
	{
		return new TestSuite(ViewMyDesktopToolStartPageActionTest.class);
	}

	public static void main(String[] args)
	{
		TestRunner.run(ViewMyDesktopToolStartPageActionTest.class);
	}
}
