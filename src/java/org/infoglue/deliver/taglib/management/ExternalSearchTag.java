/**
 * 
 */
package org.infoglue.deliver.taglib.management;

import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.deliver.controllers.kernel.impl.simple.LanguageDeliveryController;
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
	private String exception;
	private Locale language;

	private void setReturnValuesForError(Throwable tr)
	{
		if (exception != null)
		{
			pageContext.setAttribute(exception, tr);
		}
		if (resultCount != null)
		{
			pageContext.setAttribute(resultCount, 0);
		}
		setResultAttribute(null);
	}

	@Override
	public int doEndTag() throws JspException
	{
		ExternalSearchService service = ExternalSearchManager.getManager().getService(serviceName);

		if (service == null)
		{
			setReturnValuesForError(new JspException("External search service not found. Given service name: " + serviceName));
		}
		else
		{
			try
			{
				SearchParameters params = ExternalSearchService.ParametersFactory.getFactory().getParameters(query, sortFields, sortAscending, startIndex, count, language);

				SearchResult searchResult = service.search(params);

				if (resultCount != null)
				{
					pageContext.setAttribute(resultCount, searchResult.totalSize);
				}

				setResultAttribute(searchResult.result);
			}
			catch (SystemException ex)
			{
				logger.warn("Error in external service search. Message: " + ex.getMessage());
				setReturnValuesForError(ex);
			}
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

	public void setLanguage(String language) throws SystemException, Exception
	{
		Object languageObject = evaluate("externalSearch", "language", language, Object.class);
		if (languageObject == null)
		{
			this.language = null;
		}
		else
		{
			if (languageObject instanceof Locale)
			{
				this.language = (Locale)languageObject;
			}
			else if (languageObject instanceof Integer)
			{
				Integer languageId = (Integer)languageObject;
				LanguageVO languageVO = LanguageController.getController().getLanguageVOWithId(languageId);
				this.language = languageVO.getLocale();
			}
			else
			{
				String languageCode = languageObject.toString();
				if (logger.isInfoEnabled())
				{
					logger.info("Got to else case when evaluating language. Assuming language code string. Value: " + languageCode);
				}
				this.language = LanguageDeliveryController.getLanguageDeliveryController().getLocaleWithCode(languageCode);;
			}
		}

		if (logger.isInfoEnabled())
		{
			logger.info("Language in external search tag evaluated to: " + this.language);
		}
	}
}

