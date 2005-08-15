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
	/**
	 * 
	 */
	private static final long serialVersionUID = 3256727294570804535L;
	
	private static final String TEXT_FIELD = "<input id=\"{0}\" name=\"{1}\" type=\"text\" value=\"{2}\" {3}/>";
	private static final String READONLY_ATTRIBUTE = " readonly=\"{0}\" ";
	private static final String CLASS_ATTRIBUTE    = " class=\"{0}\" ";
	
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
		StringBuffer extraAttributes = new StringBuffer();
		if(getCssClass() != null) 
			extraAttributes.append(MessageFormat.format(CLASS_ATTRIBUTE, new Object[] { getCssClass() }));
		if(getReadonly() != null) 
			extraAttributes.append(MessageFormat.format(READONLY_ATTRIBUTE, new Object[] { getReadonly() }));
		
		
		String value = (getContentValue() == null) ? "" : getContentValue();
		return MessageFormat.format(TEXT_FIELD, new Object[] { getIdAttr(), getName(), value, extraAttributes.toString() });
	}
}
