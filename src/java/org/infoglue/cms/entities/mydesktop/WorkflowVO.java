/* ===============================================================================
*
* Part of the InfoGlue Content Management Platform (www.infoglue.org)
*
* ===============================================================================
*
* Copyright (C) Mattias Bogeblad
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

package org.infoglue.cms.entities.mydesktop;

import java.util.*;

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.workflow.StepFilter;

/**
 * This is the general action description object. Can be used by any workflow engine hopefully.
 *
 * @author Mattias Bogeblad
 */

public class WorkflowVO implements BaseEntityVO
{
	private Long workflowId;
	private String name;
	private List declaredSteps = new ArrayList();
	private List currentSteps = new ArrayList();
	private List historySteps = new ArrayList();
	private List globalActions = new ArrayList();

	public Integer getId()
	{
		return new Integer(workflowId.intValue());
	}

	public void setId(Integer id)
	{
		setWorkflowId(new Long(id.longValue()));
	}

	public Long getWorkflowId()
	{
		return workflowId;
	}

	public void setWorkflowId(Long workflowId)
	{
		this.workflowId = workflowId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List getDeclaredSteps()
	{
		return declaredSteps;
	}

	public void setDeclaredSteps(List steps)
	{
		declaredSteps = (steps == null) ? new ArrayList() : steps;
	}

	public List getCurrentSteps()
	{
		return currentSteps;
	}

	/**
	 * Returns all the current steps allowed by the given filter.  Useful to restrict the current steps for display, e.g.
	 * return only the steps owned by the current user.
	 * @param filter a StepFilter
	 * @return the current steps allowed by filter
	 */
	public List getCurrentSteps(StepFilter filter)
	{
		List filteredSteps = new ArrayList();
		for (Iterator steps = currentSteps.iterator(); steps.hasNext();)
		{
			WorkflowStepVO step = (WorkflowStepVO)steps.next();
			if (filter.isAllowed(step))
				filteredSteps.add(step);
		}

		return filteredSteps;
	}

	public void setCurrentSteps(List steps)
	{
		currentSteps = (steps == null) ? new ArrayList() : steps;
	}

	public List getHistorySteps()
	{
		return historySteps;
	}

	public void setHistorySteps(List steps)
	{
		historySteps = (steps == null) ? new ArrayList() : steps;
	}

	public List getSteps()
	{
		List steps = new ArrayList();
		steps.addAll(currentSteps);
		steps.addAll(historySteps);
		return steps;
	}

	public List getAvailableActions()
	{
		List availableActions = new ArrayList();
		for (Iterator i = currentSteps.iterator(); i.hasNext();)
			availableActions.addAll(((WorkflowStepVO)i.next()).getActions());

		return availableActions;
	}

	public List getGlobalActions()
	{
		return globalActions;
	}

	public void setGlobalActions(List actions)
	{
		globalActions = (actions == null) ? new ArrayList() : actions;
	}

	public ConstraintExceptionBuffer validate()
	{
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
		return ceb;
	}
}
