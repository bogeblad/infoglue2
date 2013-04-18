/**
 * 
 */
package org.infoglue.deliver.taglib.management;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.externalsearch.SearchRequest.ParameterType;
import org.infoglue.deliver.taglib.AbstractTag;



/**
 * @author Erik Stenbäcka
 *
 */
public class ExternalSearchFieldTag extends AbstractTag
{
	private static final long serialVersionUID = -4562505278121718003L;

	private String name;
	private String value;
	private String type;
	private Boolean ascending;

	@Override
	public int doEndTag() throws JspException
	{
		Object parent = findAncestorWithClass(this, ExternalSearchQueryTag.class);
		if (parent != null)
		{
			ExternalSearchQueryTag query = (ExternalSearchQueryTag)parent;
			ParameterType parameterType = ParameterType.MUST;
			if (type != null)
			{
				parameterType = ParameterType.valueOf(type.toUpperCase());
			}
			query.addField(name, value, parameterType);
		}
		else
		{
			parent = findAncestorWithClass(this, ExternalSearchOrderingTag.class);
			if (parent != null)
			{
				ExternalSearchOrderingTag ordering = (ExternalSearchOrderingTag)parent;
				ordering.addField(name, ascending);
			}
			else
			{
				throw new JspException("Illegal placement of externalSearchField tag. Must be within an ExternalSearchQuery tag or an ExternalSearchOrdering tag.");
			}
		}

		name = null;
		value = null;
		ascending = null;
		type = null;

		return EVAL_PAGE;
	}

	public void setName(String name) throws JspException
	{
		this.name = evaluateString("externalSearchField", "name", name);
	}

	public void setValue(String value) throws JspException
	{
		this.value = evaluateString("externalSearchField", "value", value);
	}

	public void setAscending(String ascending) throws JspException
	{
		this.ascending = (Boolean)evaluate("externalSearchField", "ascending", ascending, Boolean.class);
	}

	public void setType(String type) throws JspException
	{
		this.type = evaluateString("externalSearchField", "type", type);
	}

}

