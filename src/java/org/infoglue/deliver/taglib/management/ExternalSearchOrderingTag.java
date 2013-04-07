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
public class ExternalSearchOrderingTag extends AbstractTag
{
	private static final long serialVersionUID = -4562505238121718003L;
	private static final Logger logger = Logger.getLogger(ExternalSearchOrderingTag.class);

	private SearchRequest request;

	public void addField(String name, boolean ascending)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Adding sort field. Name: " + name + ". Ascending: " + ascending);
		}
		request.addSortParameter(name, ascending);
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

}

