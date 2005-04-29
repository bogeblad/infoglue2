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
public class ContentTextFieldTag extends ContentInputTag {
	private static final long serialVersionUID = 4051330041428390198L;

	private static final String TEXT_FIELD = "<input id=\"{0}\" name=\"{0}\" type=\"text\" value=\"{1}\" class=\"{2}\"/>";
	
	/**
	 * 
	 */
	public ContentTextFieldTag() {
		super();
	}

	/**
	 * 
	 */
	public int doEndTag() throws JspException {
		write(createTextFieldHTML());
		return EVAL_PAGE;
	}


	/**
	 * 
	 */
	private String createTextFieldHTML() {
		String value = (getContentValue() == null) ? "" : getContentValue();
		String cssClass = (getCssClass() == null) ? "" : getCssClass();
		return MessageFormat.format(TEXT_FIELD, new Object[] { getName(), value, cssClass });
	}
}
