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
 *
 */
public abstract class ContentBooleanFieldTag extends ElementTag 
{
	/**
	 * 
	 */
	private String value;
	
	/**
	 * 
	 */
	private String elementValue;
	
	/**
	 * Default constructor.
	 */
	public ContentBooleanFieldTag() 
	{
		super();
	}
	
	/**
	 * 
	 */
	public int doEndTag() throws JspException 
	{
		getElement().addAttribute("checked", "checked", isChecked());
		value = null;
		elementValue = null;
		return super.doEndTag();
	}
	
	/**
	 * 
	 */
	private boolean isChecked()
	{
		return value != null && elementValue != null && value.equals(elementValue);
	}
	
	/**
	 * 
	 */
	public void setName(final String name) 
	{
		getElement().addAttribute("name", name);
		this.elementValue = getPropertySet().getDataString(name);
	}

	/**
	 * 
	 */
	public void setValue(final String value) 
	{
		getElement().addAttribute("value", value);
		this.value = value; 
	}
}