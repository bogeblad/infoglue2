/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.util.Locale;

/**
 * @author Erik Stenbäcka
 *
 */
public class SearchParameters
{
	public enum SortOrder {ASC, DESC};

	//Directory directory, String queryString, Integer startIndex, Integer count, String[] sortFields, Boolean sortAsc
	private String query;
	private Integer startIndex;
	private Integer count;
	private String[] sortFields;
	private SortOrder sortOrder = SortOrder.ASC;
	private Locale language;
	
	public boolean shouldSort()
	{
		return sortFields != null && sortOrder != null;
	}
	
	public String getQuery()
	{
		return query;
	}
	public void setQuery(String query)
	{
		this.query = query;
	}
	public Integer getStartIndex()
	{
		return startIndex;
	}
	public void setStartIndex(Integer startIndex)
	{
		this.startIndex = startIndex;
	}
	public Integer getCount()
	{
		return count;
	}
	public void setCount(Integer count)
	{
		this.count = count;
	}
	public String[] getSortFields()
	{
		return sortFields;
	}
	public void setSortFields(String[] sortFields)
	{
		this.sortFields = sortFields;
	}
	public SortOrder getSortOrder()
	{
		return sortOrder;
	}
	public void setSortOrder(SortOrder sortOrder)
	{
		this.sortOrder = sortOrder;
	}
	public Locale getLanguage()
	{
		return language;
	}
	public void setLanguage(Locale language)
	{
		this.language = language;
	}
}
