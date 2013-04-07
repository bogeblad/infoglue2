/**
 * 
 */
package org.infoglue.deliver.taglib.management;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.infoglue.deliver.externalsearch.SearchRequest;
import org.infoglue.deliver.taglib.AbstractTag;



/**
 * @author Erik Stenbäcka
 *
 */
public class ExternalSearchQueryTag extends AbstractTag
{
	private static final long serialVersionUID = -4562505238121718093L;
	private static final Logger logger = Logger.getLogger(ExternalSearchQueryTag.class);

	private SearchRequest request;

	public void addField(String name, String value)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Adding search field. Name: " + name + ". Value: " + value);
		}
		request.addSearchParameter(name, value);
	}
	
	@Override
	public int doStartTag() throws JspException
	{
		Object parent = findAncestorWithClass(this, ExternalSearchTag.class);
		if (parent == null)
		{
			throw new JspException("Could not find parent external search tag for query tag");
		}
		else
		{
			request = ((ExternalSearchTag)parent).getSearchRequest();
		}

		return super.doStartTag();
	}

	public void setQuery(String query) throws JspException
	{
		request.setQuery(evaluateString("externalSearch", "query", query));
	}
}

