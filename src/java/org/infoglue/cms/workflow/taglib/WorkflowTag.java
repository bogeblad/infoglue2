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
*/
package org.infoglue.cms.workflow.taglib;

import javax.servlet.jsp.tagext.TagSupport;

import org.infoglue.cms.applications.workflowtool.util.InfogluePropertySet;
import org.infoglue.cms.controllers.kernel.impl.simple.WorkflowController;
import org.infoglue.cms.security.InfoGluePrincipal;

/**
 * 
 */
public abstract class WorkflowTag extends TagSupport 
{
	/**
	 * 
	 */
	public static final String ACTION_ID_PARAMETER   = "actionId";
	
	/**
	 * 
	 */
	public static final String WORKFLOW_ID_PARAMETER = "workflowId";

	/**
	 * 
	 */
	protected WorkflowTag() 
	{
	}

	/**
	 * 
	 */
	protected final String getWorkflowID() {
		return pageContext.getRequest().getParameter(WORKFLOW_ID_PARAMETER);
	}

	/**
	 * 
	 */
	protected final String getActionID() 
	{
		return pageContext.getRequest().getParameter(ACTION_ID_PARAMETER);
	}

	/**
	 * 
	 */
	protected final InfoGluePrincipal getPrincipal() 
	{
		return (InfoGluePrincipal) pageContext.getSession().getAttribute("org.infoglue.cms.security.user");
	}

	/**
	 * 
	 */
	protected final InfogluePropertySet getPropertySet() 
	{
		return new InfogluePropertySet(WorkflowController.getController().getPropertySet(getPrincipal(), Long.valueOf(getWorkflowID()).longValue()));
	}
	
	/**
	 * 
	 */
	protected final String getElementValue(final String name)
	{
		return getPropertySet().getDataString(name);
	}
}
