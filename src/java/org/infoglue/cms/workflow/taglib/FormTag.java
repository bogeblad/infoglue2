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

import java.text.MessageFormat;

import javax.servlet.jsp.JspException;

/**
 *
 */
public class FormTag extends WorkflowTag 
{
	/**
	 * The universal version identifier.
	 */
	private static final long serialVersionUID = -558848421886366918L;

	/**
	 * 
	 */
	private static final String RETURN_ADDRESS_PARAMETER = "returnAddress";

	/**
	 * 
	 */
	private static final String FORM_START = "<form name=\"form\" id=\"form\" method=\"get\" action=\"{0}\">";

	/**
	 * 
	 */
	private static final String HIDDEN = "<div><input id=\"{0}\" name=\"{0}\" type=\"hidden\" value=\"{1}\"/></div>";

	/**
	 * 
	 */
	private static final String FORM_END = "</form>";

	/**
	 * Default constructor.
	 */
	public FormTag() 
	{
		super();
	}

	/**
	 * 
	 */
	public int doStartTag() throws JspException 
	{
		write(MessageFormat.format(FORM_START, new Object[] { getReturnAddress() }));
		write(MessageFormat.format(HIDDEN, new Object[] { ACTION_ID_PARAMETER, getActionID() }));
		write(MessageFormat.format(HIDDEN, new Object[] { WORKFLOW_ID_PARAMETER,  getWorkflowID() }));
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * 
	 */
	public int doEndTag() throws JspException 
	{
		write(FORM_END);
		return EVAL_PAGE;
	}

	/**
	 * 
	 */
	private String getReturnAddress() 
	{
		return pageContext.getRequest().getParameter(RETURN_ADDRESS_PARAMETER);
	}
}
