/**
 * 
 */
package org.infoglue.deliver.taglib.management;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.AbstractTag;



/**
 * @author Erik Stenbäcka
 *
 */
public class ExternalSearchFieldTag extends AbstractTag
{
	private static final long serialVersionUID = -4562505278121718003L;
//	private static final Logger logger = Logger.getLogger(ExternalSearchFieldTag.class);

	private String name;
	private String value;
	private Boolean ascending;

	@Override
	public int doEndTag() throws JspException
	{
		Object parent = findAncestorWithClass(this, ExternalSearchQueryTag.class);
		if (parent != null)
		{
			ExternalSearchQueryTag query = (ExternalSearchQueryTag)parent;
			query.addField(name, value);
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

}

