package org.infoglue.deliver.applications.actions;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.deliver.externalsearch.ExternalSearchManager;
import org.infoglue.deliver.externalsearch.ExternalSearchService;
import org.infoglue.deliver.externalsearch.SearchRequest;

public class ViewExternalSearchServiceAction extends InfoGlueAbstractAction
{
	private static final long serialVersionUID = -4780659611171789860L;
	private static final Logger logger = Logger.getLogger(ViewExternalSearchServiceAction.class);

	private String serviceName;
	private ExternalSearchService service;

	private SimpleDateFormat timeFormat;
	private List<Object> searchResult;
	private Map<String, String> searchFields;

	@Override
	protected String doExecute() throws Exception
	{
		timeFormat  = new SimpleDateFormat("HH:mm:ss");
		timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		if (this.serviceName != null)
		{
			this.service = ExternalSearchManager.getManager().getService(serviceName);
		}
		return SUCCESS;
	}
	
	private int getIndexFromParameter(String param) throws NumberFormatException
	{
		int startIndex = param.indexOf("_");
		if (startIndex != -1)
		{
			return Integer.parseInt(param.substring(startIndex + 1));
		}
		throw new NumberFormatException("Could not find number to parse as index");
	}

	private void parseSearchParameters()
	{
		String keyBase = "field";
		String valueBase = "value";
		Map<Integer, String> values = new HashMap<Integer, String>();
		Map<Integer, String> fields = new HashMap<Integer, String>();
		@SuppressWarnings("unchecked")
		Enumeration<String> params = getRequest().getParameterNames();
		String param = null;
		while (params.hasMoreElements())
		{
			try
			{
				param = params.nextElement();
				if (param.startsWith(keyBase))
				{
					String value = getRequest().getParameter(param);
					if (value != null && !"".equals(value))
					{
						fields.put(getIndexFromParameter(param), value);
					}
				}
				if (param.startsWith(valueBase))
				{
					String value = getRequest().getParameter(param);
					if (value != null && !"".equals(value))
					{
						values.put(getIndexFromParameter(param), value);
					}
				}
			}
			catch (NumberFormatException ex)
			{
				logger.warn("Failed to parse index in parameter name. Param: " + param + ". Message: " + ex.getMessage());
			}
		}

		this.searchFields = new HashMap<String, String>();
		for (Map.Entry<Integer, String> fieldMap : fields.entrySet())
		{
			searchFields.put(fieldMap.getValue(), values.get(fieldMap.getKey()));
		}
	}

	public String doSearch() throws SystemException
	{
		this.service = ExternalSearchManager.getManager().getService(serviceName);
		if (this.service == null)
		{
			throw new SystemException("No service was found with that name");
		}

		parseSearchParameters();

		
		SearchRequest sr = service.getSearchRequest();
		for (Map.Entry<String, String> fieldMap : searchFields.entrySet())
		{
			sr.addParameter(fieldMap.getKey(), fieldMap.getValue());
		}

		searchResult = service.search(sr).result;

		return "search";
	}

	public String doReindex() throws SystemException
	{
		this.service = ExternalSearchManager.getManager().getService(serviceName);
		if (this.service == null)
		{
			throw new SystemException("No service was found with that name");
		}
		this.service.forceReindexing();
		return "backToList";
	}

	public List<Object> getSearchResult()
	{
		return searchResult;
	}

	public Map<String, String> getSearchFields()
	{
		return searchFields;
	}

	public String getIndexAge(ExternalSearchService service)
	{
		if (service == null)
		{
			return null;
		}
		Date diff = new Date(service.getIndexAge());
		return timeFormat.format(diff);
	}

	public String getTimeToReindex(ExternalSearchService service)
	{
		if (service == null)
		{
			return null;
		}
		long timeDiff = service.getMaxAge() - service.getIndexAge();
		Date diff = new Date(Math.abs(timeDiff));
		return (timeDiff < 0 ? "-" : "") + timeFormat.format(diff);
	}

	public Collection<ExternalSearchService> getServices()
	{
		return ExternalSearchManager.getManager().getAllServices();
	}

	public ExternalSearchService getService()
	{
		return service;
	}

	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

}
