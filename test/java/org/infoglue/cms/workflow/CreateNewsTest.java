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
 * $Id: CreateNewsTest.java,v 1.3 2005/01/18 21:48:52 jed Exp $
 */
package org.infoglue.cms.workflow;

import junit.framework.*;
import junit.swingui.TestRunner;

/**
 * Tests the WorkflowController using the Create News sample workflow
 * @see org.infoglue.cms.controllers.kernel.impl.simple.WorkflowController
 * @author <a href=mailto:jedprentice@gmail.com>Jed Prentice</a>
 */
public class CreateNewsTest extends NewsWorkflowTestCase
{
	public void testWorkflow() throws Exception
	{
		invokeCreateNews();
		checkWorkflow(1, 1, 1);
	}

	public static Test suite()
	{
		return new TestSuite(CreateNewsTest.class);
	}

	public static void main(String[] args)
	{
		TestRunner.run(CreateNewsTest.class);
	}
}
