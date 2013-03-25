/**
 * 
 */
package org.infoglue.deliver.taglib.management;

import java.util.Collections;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.deliver.externalsearch.ExternalSearchManager;
import org.infoglue.deliver.externalsearch.ExternalSearchService;
import org.infoglue.deliver.externalsearch.SearchParameters;
import org.infoglue.deliver.externalsearch.SearchResult;
import org.infoglue.deliver.taglib.AbstractTag;



/**
 * @author Erik Stenbäcka
 *
 */
public class ExternalSearchTag extends AbstractTag
{
	private static final long serialVersionUID = -4562505238121718003L;
	private static final Logger logger = Logger.getLogger(ExternalSearchTag.class);

	private String serviceName;
	private String query;
	private String[] sortFields;
	private Boolean sortAscending;
	private String resultCount;
	private Integer startIndex;
	private Integer count;

	@Override
	public int doEndTag() throws JspException
	{
		ExternalSearchService service = ExternalSearchManager.getManager().getService(serviceName);

		if (service == null)
		{
			throw new JspException("External search service not found. Given service name: " + serviceName);
		}

		try
		{
			SearchParameters params = ExternalSearchService.ParametersFactory.getFactory().getParameters(query, sortFields, sortAscending, startIndex, count);

			SearchResult searchResult = service.search(params);

			if (resultCount != null)
			{
				pageContext.setAttribute(resultCount, searchResult.totalSize);
			}

			setResultAttribute(searchResult.result);
		}
		catch (SystemException ex)
		{
//			throw new JspException("Error when searching in external search service", ex);

			if (resultCount != null)
			{
				pageContext.setAttribute(resultCount, 0);
			}
			setResultAttribute(Collections.emptyList());
		}

		return EVAL_PAGE;
	}

	public void setServiceName(String serviceName) throws JspException
	{
		this.serviceName = evaluateString("externalSearch", "serviceName", serviceName);
	}

	public void setQuery(String query) throws JspException
	{
		this.query = evaluateString("externalSearch", "query", query);
	}

	public void setSortFields(String sortFields) throws JspException
	{
		String sortFieldsString = evaluateString("externalSearch", "serviceName", sortFields);

		if (sortFieldsString == null || sortFieldsString.length() == 0)
		{
			this.sortFields = null;
		}
		else
		{
			this.sortFields = sortFieldsString.split(",");
		}
	}

	public void setSortAscending(String sortAscending) throws JspException
	{
		this.sortAscending = (Boolean) evaluate("externalSearch", "sortAscending", sortAscending, Boolean.class);
	}

	public void setResultCount(String resultCount) throws JspException
	{
		this.resultCount = resultCount;
	}

	public void setStartIndex(String startIndex) throws JspException
	{
		this.startIndex = evaluateInteger("externalSearch", "startIndex", startIndex);
	}

	public void setCount(String count) throws JspException
	{
		this.count = evaluateInteger("externalSearch", "count", count);
	}

}
