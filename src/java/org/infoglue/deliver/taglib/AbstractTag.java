package org.infoglue.deliver.taglib;

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
}
