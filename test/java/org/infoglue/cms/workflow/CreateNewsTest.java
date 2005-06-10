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
 * $Id: CreateNewsTest.java,v 1.5 2005/06/10 14:42:47 jed Exp $
 */
package org.infoglue.cms.workflow;

import java.util.*;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.*;
import org.infoglue.cms.util.*;

/**
 * Tests the Create News sample workflow
 * @author <a href=mailto:jedprentice@gmail.com>Jed Prentice</a>
 */
public class CreateNewsTest extends WorkflowTestCase
{
	protected String getWorkflowName()
	{
		return "Create News";
	}

	/**
	 * Returns the number of global actions
	 * @return the number of global actions
	 */
	protected int getNumberOfGlobalActions()
	{
		return 2;
	}

	protected void setUp() throws Exception
	{
		super.setUp();
		setUserPrincipal(getAdminPrincipal());
		startWorkflow(0);
		checkWorkflow(1, 0, 1);
	}

	public void testCreateNewsAndApprove() throws Exception
	{
		checkCreateNews();
		checkPreviewNewsAndApprove();
	}

	public void testCreateNewsAndApproveInactive() throws Exception
	{
		testCreateNewsAndApprove();

		try
		{
			invokeCreateNews();
			fail("InvalidActionException should have been thrown");
		}
		catch (InvalidActionException e)
		{
			// Expected
		}
	}

	public void testCreateNewsTwice() throws Exception
	{
		checkCreateNews();

		try
		{
			invokeCreateNews();
			fail("InvalidActionException should have been thrown");
		}
		catch (InvalidActionException e)
		{
			// Expected
		}
	}

	private void checkCreateNews() throws Exception
	{
		invokeCreateNews();
		checkWorkflow(1, 1, 1);

		PropertySet propertySet = getPropertySet();
		Map params = getCreateNewsParams();
		for (Iterator names = params.keySet().iterator(); names.hasNext();)
		{
			String name = (String)names.next();
			assertEquals("Wrong " + name + ':', params.get(name), propertySet.getString(name));
		}
	}

	private void checkPreviewNewsAndApprove() throws Exception
	{
		invokePreviewNewsAndApprove();
		assertWorkflowFinished();
	}

	/**
	 * Invokes the "Create News" workflow action
	 * @throws Exception if an error occurs
	 */
	private void invokeCreateNews() throws Exception
	{
		invokeAction(new FakeHttpServletRequest(getCreateNewsParams()), 4);
	}

	/**
	 * Invokes the "Preview News and Approve" workflow action
	 * @throws Exception if an error occurs
	 */
	private void invokePreviewNewsAndApprove() throws Exception
	{
		invokeAction(new FakeHttpServletRequest(), 5);
	}

	private Map getCreateNewsParams()
	{
		Map params = new HashMap();
		params.put("name", getName());
		params.put("title", getName());
		params.put("navigationTitle", getName());
		params.put("leadIn", getName());
		params.put("fullText", getName());

		return params;
	}
}
