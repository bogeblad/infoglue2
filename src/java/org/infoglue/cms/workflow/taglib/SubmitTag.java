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

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

public class SubmitTag extends TagSupport {
	private static final long serialVersionUID = 3256726169356613942L;

	private static final String SUBMIT_FIELD = "<input type=\"submit\" value=\"{0}\" onclick=\"document.getElementById({1}).value={2}\"/>";

	private String actionID;
	private String value;

	/**
	 * 
	 */
    public SubmitTag() {
        super();
    }

	/**
	 * 
	 */
	public int doEndTag() throws JspException {
		write(createSubmitHTML());
        return EVAL_PAGE;
	}
	
	/**
	 * 
	 */
	private void write(String text) throws JspException {
		try {
			pageContext.getOut().write(text);
		} catch(IOException e) {
			e.printStackTrace();
			throw new JspTagException("IO error: " + e.getMessage());
		}
	}

	/**
	 * 
	 */
	private String createSubmitHTML() {
		final String actionIDName = "'" + WorkflowHelper.ACTION_ID_PARAMETER + "'";
		return MessageFormat.format(SUBMIT_FIELD, new Object[] { value, actionIDName, actionID });
	}
		
	/**
	 * 
	 */
	public void setActionID(String id) {
        this.actionID = id;
    }

	/**
	 * 
	 */
	public void setValue(String value) {
        this.value = value;
    }
}