/**
 * $Id: WorkflowVOTest.java,v 1.1 2004/12/29 17:20:08 jed Exp $
 * Created by jed on Dec 29, 2004
 */
package org.infoglue.cms.entities.mydesktop;

import java.util.Iterator;

import org.infoglue.cms.util.InfoGlueTestCase;
import org.infoglue.cms.util.workflow.*;
import org.infoglue.cms.security.InfoGluePrincipal;

/**
 * @author jed
 * @version $Revision: 1.1 $ $Date: 2004/12/29 17:20:08 $
 */
public class WorkflowVOTest extends InfoGlueTestCase
{
	private static final StepFilter adminFilter = new OwnerStepFilter(getAdminPrincipal());
	private static final StepFilter userFilter = new OwnerStepFilter(getCmsUserPrincipal());

	private WorkflowVO workflow = new WorkflowVO();

	protected void setUp() throws Exception
	{
		workflow.getCurrentSteps().add(createStep(getAdminPrincipal()));
		workflow.getCurrentSteps().add(createStep(getCmsUserPrincipal()));
		workflow.getCurrentSteps().add(new WorkflowStepVO());

		for (Iterator steps = workflow.getCurrentSteps().iterator(); steps.hasNext();)
			((WorkflowStepVO)steps.next()).getActions().add(new WorkflowActionVO());
	}

	public void testGetAvailableActions() throws Exception
	{
		assertEquals("Wrong number of available actions:", 3, workflow.getAvailableActions().size());
	}

	public void testGetAvailableActionsFiltered() throws Exception
	{
		assertEquals("Wrong number of admin actions:", 3, workflow.getAvailableActions(adminFilter).size());
		assertEquals("Wrong number of user actions:", 2, workflow.getAvailableActions(userFilter).size());
	}

	public void testGetCurrentStepsFiltered() throws Exception
	{
		assertEquals("Wrong number of admin steps:", 3, workflow.getCurrentSteps(adminFilter).size());
		assertEquals("Wrong number of user steps:", 2, workflow.getCurrentSteps(userFilter).size());
	}

	private static WorkflowStepVO createStep(InfoGluePrincipal owner)
	{
		WorkflowStepVO step = new WorkflowStepVO();
		step.setOwner(owner.getName());
		return step;
	}
}
