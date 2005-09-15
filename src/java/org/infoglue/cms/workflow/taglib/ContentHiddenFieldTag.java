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

/**
 * This class implements the <iw:hidden> tag, which presents an <input type="hidden" ... /> 
 * form element where the value is taken from the propertyset associated with the workflow. 
 */
public class ContentHiddenFieldTag extends ElementTag 
{
	/**
	 * The universal version identifier.
	 */
	private static final long serialVersionUID = -1662296072294548592L;

	/**
	 * Default constructor.
	 */
	public ContentHiddenFieldTag() 
	{
		super();
	}

	/**
	 * Creates the element to use when constructing this tag.
	 * 
	 * @return the element to use when constructing this tag.
	 */
	protected Element createElement()
	{
		return new Element("input").addAttribute("type", "hidden");
	}
	
	/**
	 * Sets the name attribute of the input element. 
	 * As an side-effect, the value attribute will also be set, where the value is
	 * fetched from the propertyset using the specified name.
	 * 
	 * @param name the name to use.
	 */
	public void setName(final String name) 
	{
		getElement().addAttribute("name", name);
		getElement().addAttribute("value", getPropertySet().getDataString(name));
	}
}
