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
 * $Id: NewsWorkflowTestCase.java,v 1.5 2005/06/10 18:32:43 jed Exp $
 */
package org.infoglue.cms.workflow;

import java.util.*;

import org.infoglue.cms.util.*;

/**
 * @author <a href=mailto:jedprentice@gmail.com>Jed Prentice</a>
 * @version $Revision: 1.5 $ $Date: 2005/06/10 18:32:43 $
 */
public class NewsWorkflowTestCase extends WorkflowTestCase
{
	protected void setUp() throws Exception
	{
		super.setUp();
		setUserPrincipal(getAdminPrincipal());
		startWorkflow(0);
		checkWorkflow(1, 0, 1);
	}

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

	/**
	 * Invokes the "Create News" workflow action
	 * @throws java.lang.Exception if an error occurs
	 */
	protected void invokeCreateNews() throws Exception
	{
		invokeAction(new FakeHttpServletRequest(getCreateNewsParams()), 4);
	}

	/**
	 * Invokes the "Preview News and Approve" workflow action
	 * @throws Exception if an error occurs
	 */
	protected void invokePreviewNewsAndApprove() throws Exception
	{
		invokeAction(new FakeHttpServletRequest(), 5);
	}

	protected Map getCreateNewsParams()
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
