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
 * $Id: NewsWorkflowTestCase.java,v 1.1 2005/01/18 21:47:00 jed Exp $
 */
package org.infoglue.cms.workflow;

import org.infoglue.cms.util.*;

/**
 * @author <a href=mailto:jedprentice@gmail.com>Jed Prentice</a>
 * @version $Revision: 1.1 $ $Date: 2005/01/18 21:47:00 $
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
		FakeHttpServletRequest request = new FakeHttpServletRequest();
		request.setParameter("name", getName());
		request.setParameter("title", getName());
		request.setParameter("navigationTitle", getName());
		request.setParameter("leadIn", getName());
		request.setParameter("fullText", getName());

		invokeAction(request, 4);
	}
}
