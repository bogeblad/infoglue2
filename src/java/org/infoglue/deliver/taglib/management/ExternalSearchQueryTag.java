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
public class ExternalSearchQueryTag extends AbstractTag
{
	private static final long serialVersionUID = -4562505238121718093L;
	private static final Logger logger = Logger.getLogger(ExternalSearchQueryTag.class);

	private ExternalSearchTag parent;
	private String freeTextSearchString;
	private String query;
	private Boolean listAll;

	public void addField(String name, String value)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Adding search field. Name: " + name + ". Value: " + value);
		}
		parent.getSearchRequest().addSearchParameter(name, value);
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
			parent = (ExternalSearchTag)parentObject;
		}

		if (freeTextSearchString != null || query != null || listAll != null)
		{
			logger.info("One of the search query attributes was set. Will skip body evaluation.");
			return SKIP_BODY;
		}
		else
		{
			return EVAL_BODY_INCLUDE;
		}
	}

	@Override
	public int doEndTag() throws JspException
	{
		if (freeTextSearchString != null)
		{
			parent.setFreeTextSearchString(freeTextSearchString);
		}
		else if (query != null)
		{
			parent.setQuery(query);
		}
		else if (listAll != null)
		{
			parent.listAll();
		}

		this.parent = null;
		this.freeTextSearchString = null;
		this.query = null;
		this.listAll = null;

		return EVAL_PAGE;
	}

	public void setQuery(String query) throws JspException
	{
		this.query = evaluateString("externalSearchQuery", "query", query);
	}

	public void setFreeTextSearch(String freeTextSearch) throws JspException
	{
		this.freeTextSearchString = evaluateString("externalSearchQuery", "freeTextSearch", freeTextSearch);
	}

	public void setListAll(String listAll) throws JspException
	{
		this.listAll = (Boolean)evaluate("externalSearchQuery", "listAll", listAll, Boolean.class);
	}
}

