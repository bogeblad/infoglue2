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

import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;

public abstract class AbstractTag extends TagSupport 
{
	public AbstractTag()
	{
		super();
	}

	protected Object evaluate(String tagName, String attributeName, String expression, Class expectedType) throws JspException
	{
		return ExpressionUtil.evalNotNull(tagName, attributeName, expression, expectedType, this, pageContext);
	}

	protected Integer evaluateInteger(String tagName, String attributeName, String expression) throws JspException
	{
		return (Integer) evaluate(tagName, attributeName, expression, Integer.class);
	}

	protected String evaluateString(String tagName, String attributeName, String expression) throws JspException
	{
		return (String) evaluate(tagName, attributeName, expression, String.class);
	}

	protected Collection evaluateCollection(String tagName, String attributeName, String expression) throws JspException
	{
		return (Collection) evaluate(tagName, attributeName, expression, Collection.class);
	}

	protected List evaluateList(String tagName, String attributeName, String expression) throws JspException
	{
		return (List) evaluate(tagName, attributeName, expression, List.class);
	}
}
