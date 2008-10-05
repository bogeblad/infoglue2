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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.infoglue.deliver.taglib.AbstractTag;

/**
 * This class implements the &lt;common:parameter&gt; tag, which adds a parameter
 * to the parameters of the parent tag.
 *
 *  Note! This tag must have a &lt;common:urlBuilder&gt; ancestor.
 */
public class URLParameterTag extends AbstractTag 
{
	/**
	 * The universal version identifier.
	 */
	private static final long serialVersionUID = 4482006814634520239L;

	/**
	 * The name of the parameter.
	 */
	private String name;
	
	/**
	 * The value of the parameter.
	 */
	private String value;

	/**
	 * The value of the parameter.
	 */
	private String scope = "requestParameter";

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
		addParameter();
		return EVAL_PAGE;
    }
	
	/**
	 * Adds the parameter to the ancestor tag.
	 * 
	 * @throws JspException if the ancestor tag isn't a url tag.
	 */
	protected void addParameter() throws JspException
	{
		URLTag urlParent = (URLTag) findAncestorWithClass(this, URLTag.class);
		ImportTag importParent = null;
		if(urlParent == null)
			importParent = (ImportTag) findAncestorWithClass(this, ImportTag.class);
		XSLTransformTag transformParent = null;
		if(urlParent == null && importParent == null)
			transformParent = (XSLTransformTag) findAncestorWithClass(this, XSLTransformTag.class);
		ForwardTag forwardParent = null;
		if(urlParent == null && importParent == null && transformParent == null)
			forwardParent = (ForwardTag) findAncestorWithClass(this, ForwardTag.class);

		if(urlParent == null && importParent == null && transformParent == null && forwardParent == null)
		{
			throw new JspTagException("URLParameterTag must either have a URLTag ancestor, a ImportTag ancestor, a ForwardTag ancestor or an XSLTransformTag ancestor.");
		}
		
		if(urlParent != null)
			((URLTag) urlParent).addParameter(name, value);
		if(importParent != null)
		{
			if(this.scope.equalsIgnoreCase("requestParameter"))
				((ImportTag) importParent).addParameter(name, value);
			else
				((ImportTag) importParent).addProperty(name, value);
		}
		if(transformParent != null)
			((XSLTransformTag) transformParent).addParameter(name, value);
		if(forwardParent != null)
			((ForwardTag) forwardParent).addParameter(name, value);
	}
	
	/**
	 * Sets the name attribute.
	 * 
	 * @param name the name to use.
	 * @throws JspException if an error occurs while evaluating name parameter.
	 */
	public void setName(final String name) throws JspException
	{
		this.name = evaluateString("parameter", "name", name);
	}

	/**
	 * Sets the value attribute.
	 * 
	 * @param value the value to use.
	 * @throws JspException if an error occurs while evaluating value parameter.
	 */
	public void setValue(final String value) throws JspException
	{
		this.value = evaluateString("parameter", "value", value);
	}

	/**
	 * Sets the value attribute.
	 * 
	 * @param scope the scope to use. In for example common:import you can use requestProperty instead of the normal dafult requestParameter.
	 * @throws JspException if an error occurs while evaluating value parameter.
	 */
	public void setScope(final String scope) throws JspException
	{
		this.scope = evaluateString("parameter", "scope", scope);
	}
}
