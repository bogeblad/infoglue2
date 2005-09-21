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

import javax.servlet.jsp.JspException;


/**
 * Base class for tags presenting checkbox/radio form elements representing a content/content version attribute. 
 * The value of the content/content version attribute is fetched (with the name of the input element as a key) 
 * from the propertyset associated with the workflow. 
 */
public abstract class ContentBooleanFieldTag extends ElementTag 
{
	/**
	 * The form element value.
	 */
	private String value;
	
	/**
	 * The value of the represented content/content version attribute.
	 */
	private String checked;
	
	/**
	 * Default constructor.
	 */
	public ContentBooleanFieldTag() 
	{
		super();
	}
	
	/**
	 * Process the end tag. Writes the element to the output stream.
	 * 
	 * @return indication of whether to continue evaluating the JSP page.
	 * @throws JspException if an I/O error occurs when writing to the output stream.
	 */
	public int doEndTag() throws JspException 
	{
		getElement().addAttribute("checked", "checked", isChecked());
		value = null;
		checked = null;
		return super.doEndTag();
	}
	
	/**
	 * Returns true if the form element should be checked. That will
	 * be te case if the value of the represented attribute equals the value of the
	 * form element.
	 * 
	 * @return true if the element should be checked; false otherwise.
	 */
	private boolean isChecked()
	{
		return value != null && checked != null && value.equals(checked);
	}
	
	/**
	 * Sets the name attribute of the input element. 
	 * 
	 * @param name the name to use.
	 */
	public void setName(final String name) 
	{
		getElement().addAttribute("name", name);
		checked = getPropertySet().getDataString(name);
	}

	/**
	 * Sets the value attribute of the input element. 
	 * 
	 * @param value the value to use.
	 */
	public void setValue(final String value) 
	{
		getElement().addAttribute("value", value);
		this.value = value; 
	}
}