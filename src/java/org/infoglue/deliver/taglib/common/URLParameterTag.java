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
package org.infoglue.deliver.taglib.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.infoglue.deliver.taglib.AbstractTag;

/**
 * This class implements the <common:parameter> tag, which adds a parameter
 * to the parameters of the parent tag.
 *
 * If an parameter with the specified name exists, it will be overwritten.
 *
 *  Note! This tag can only be used as a child of <common:url>.
 */
public class URLParameterTag extends AbstractTag 
{
	/**
	 * The universal version identifier.
	 */
	private static final long serialVersionUID = 8015304806886114032L;

	/**
	 * The name of the parameter.
	 */
	private String name;
	
	/**
	 * The value of the parameter.
	 */
	private String value;
	
	/**
	 * Default constructor. 
	 */
	public URLParameterTag()
	{
		super();
	}

	/**
	 * Adds a parameter with the specified name and value to the parameters
	 * of the parent tag.
	 * 
	 * @return indication of whether to continue evaluating the JSP page.
	 * @throws JspException if an error occurred while processing this tag.
	 */
	public int doEndTag() throws JspException
    {
		//getParameters().put(name, value);
		return EVAL_PAGE;
    }
	
	/**
	 * Returns the parameter map from the parent tag.
	 * 
	 * @return the parameters.
	 * @throws JspException if the ancestor tag isn't a url tag.
	 */
	protected Map getParameters() throws JspException
	{
		return null;
		/*
		if(getParent() == null || !(getParent() instanceof org.infoglue.deliver.taglib.common.URLTag))
		{
			throw new JspTagException("URLParameterTag must be used inside a URLTag");
		}
		return ((org.infoglue.deliver.taglib.common.URLTag) getParent()).getParameters();
		*/
	}
	
	/**
	 * Sets the name attribute.
	 * 
	 * @param name the name to use.
	 * @throws JspException if an error occurs while evaluating name parameter.
	 */
	public void setN(final String name) throws JspException
	{
		//this.name = evaluateString("parameter", "name", name);
	}

	/**
	 * Sets the value attribute.
	 * 
	 * @param value the value to use.
	 * @throws JspException if an error occurs while evaluating value parameter.
	 */
	public void setV(final String value) throws JspException
	{
		//this.value = evaluateString("parameter", "value", value);
	}
}
