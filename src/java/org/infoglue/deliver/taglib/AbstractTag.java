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
package org.infoglue.deliver.taglib;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;

/**
 * 
 */
public abstract class AbstractTag extends TagSupport 
{
	/**
	 * TODO: remove, use var instead.
	 */
	private String id;
	
	/**
	 *
	 */
	public AbstractTag()
	{
		super();
	}

	/**
	 * TODO: remove, use var instead.
	 */
    public void setId(String id)
    {
        this.id = id;
    }

	/**
	 *
	 */
	protected void setResultAttribute(Object value)
	{
		if(value == null)
		{
			pageContext.removeAttribute(id);
		}
		else
		{
			pageContext.setAttribute(id, value);
		}
		
	}
	
	/**
	 *
	 */
	protected void produceResult(Object value) throws JspTagException
	{
	    if(id == null)
	    {
			write(value.toString());
	    }
		else
		{
			setResultAttribute(value);
		}
	}

	/**
	 * Writes the specified text to the response stream.
	 * 
	 * @param text the text to write.
	 * @throws JspException if an I/O error occurs.
	 */
	protected void write(final String text) throws JspTagException
	{
		try 
		{
			pageContext.getOut().write(text);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw new JspTagException("IO error: " + e.getMessage());
		}
	}
	
	/**
	 *
	 */
	protected Object evaluate(String tagName, String attributeName, String expression, Class expectedType) throws JspException
	{
		return ExpressionUtil.evalNotNull(tagName, attributeName, expression, expectedType, this, pageContext);
	}

	/**
	 *
	 */
	protected Integer evaluateInteger(String tagName, String attributeName, String expression) throws JspException
	{
		return (Integer) evaluate(tagName, attributeName, expression, Integer.class);
	}

	/**
	 *
	 */
	protected String evaluateString(String tagName, String attributeName, String expression) throws JspException
	{
		return (String) evaluate(tagName, attributeName, expression, String.class);
	}

	/**
	 *
	 */
	protected Collection evaluateCollection(String tagName, String attributeName, String expression) throws JspException
	{
		return (Collection) evaluate(tagName, attributeName, expression, Collection.class);
	}

	/**
	 *
	 */
	protected List evaluateList(String tagName, String attributeName, String expression) throws JspException
	{
		return (List) evaluate(tagName, attributeName, expression, List.class);
	}
}
