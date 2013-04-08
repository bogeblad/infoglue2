/**
 * 
 */
package org.infoglue.deliver.taglib.management;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.infoglue.deliver.taglib.AbstractTag;



/**
 * @author Erik Stenbäcka
 *
 */
public class ExternalSearchOrderingTag extends AbstractTag
{
	private static final long serialVersionUID = -4562505238121718003L;
	private static final Logger logger = Logger.getLogger(ExternalSearchOrderingTag.class);

	private ExternalSearchTag parent;
//	private SearchRequest request;

	public void addField(String name, boolean ascending)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Adding sort field. Name: " + name + ". Ascending: " + ascending);
		}
		parent.getSearchRequest().addSortParameter(name, ascending);
	}

	@Override
	public int doStartTag() throws JspException
	{
		Object parentObject = findAncestorWithClass(this, ExternalSearchTag.class);
		if (parentObject == null)
		{
			throw new JspException("Could not find parent external search tag for query tag");
		}
		else
		{
			this.parent = (ExternalSearchTag)parentObject;
		}

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException
	{
		this.parent = null;
		return EVAL_PAGE;
	}

}

