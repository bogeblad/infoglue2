/* ===============================================================================
 *
 * Part of the InfoGlue Content Management Platform (www.infoglue.org)
 *
 * ===============================================================================
 *
 *  Copyright (C)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2, as published by the
 * Free Software Foundation. See the file LICENSE.html for more information.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
 * Place, Suite 330 / Boston, MA 02111-1307 / USA.
 *
 * ===============================================================================
 */

package org.infoglue.deliver.externalsearch;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

/**
 * @author Erik Stenbäcka
 */
public class SearchRequest
{
	private static final Logger logger = Logger.getLogger(SearchRequest.class);
	public enum ParameterType {MUST,MUST_NOT,SHOULD};
	static final String ANY_FIELD = "$$any";
	static final String ANY_QUERY = "true";

	private String queryString;
	private Integer startIndex;
	private Integer count;
	private Locale language;

	private LinkedList<SearchFieldVars> searchFields;
	private Map<String, Boolean> sortFields;

	private Map<String, IndexableField> serviceFields;
	
	public SearchRequest(Map<String, IndexableField> serviceFields, Locale language)
	{
		this.serviceFields = serviceFields;
		this.searchFields = new LinkedList<SearchFieldVars>();
		this.sortFields = new LinkedHashMap<String, Boolean>();
		this.language = language;
	}

	public boolean shouldSort()
	{
		return sortFields.size() > 0;
	}

	public void setQuery(String query)
	{
		this.queryString = query;
	}

	public void addParameter(String fieldName, String query)
	{
		addParameter(fieldName, query, ParameterType.MUST);
	}

	public void addParameter(String fieldName, String query, ParameterType type)
	{
		if (fieldName.equals(ANY_FIELD))
		{
			return;
		}
		searchFields.addLast(new SearchFieldVars(fieldName, query, getBooleanClause(type)));
	}

	public void addSearchParameter(String fieldName, String query)
	{
		addSearchParameter(fieldName, query, ParameterType.MUST);
	}

	public void addSearchParameter(String fieldName, String query, ParameterType type)
	{
		IndexableField field = serviceFields.get(fieldName);
		if (field == null)
		{
			throw new IllegalArgumentException("The given field is not searchable in this service. Field name: " + fieldName);
		}
		searchFields.addLast(new SearchFieldVars(field.getFieldName(language), query, getBooleanClause(type)));
	}

	public void addSortParameter(String fieldName, boolean ascending)
	{
		IndexableField field = serviceFields.get(fieldName);
		if (field == null)
		{
			throw new IllegalArgumentException("The given field is not sortable in this service. Field name: " + fieldName);
		}
		sortFields.put(field.getFieldName(language), ascending);
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

	private BooleanClause.Occur getBooleanClause(ParameterType type)
	{
		switch (type)
		{
			case MUST:
				return BooleanClause.Occur.MUST;
			case SHOULD:
				return BooleanClause.Occur.SHOULD;
			case MUST_NOT:
				return BooleanClause.Occur.MUST_NOT;
			default:
				return BooleanClause.Occur.MUST;
		}
	}

	public Locale getLanguage()
	{
		return language;
	}

	public void listAll()
	{
		searchFields.clear();
		searchFields.addLast(new SearchFieldVars(ANY_FIELD, ANY_QUERY, BooleanClause.Occur.MUST));
	}

	public Query getQuery(Analyzer analyzer) throws ParseException
	{
		if (queryString != null)
		{
			return new QueryParser("", analyzer).parse(queryString);
		}
		else
		{
			String[] queries = new String[searchFields.size()];
			String[] fields = new String[searchFields.size()];
			BooleanClause.Occur[] fieldTypes = new BooleanClause.Occur[searchFields.size()];

			int i = 0;
			for (SearchFieldVars entry : searchFields)
			{
				fields[i] = entry.field;
				queries[i] = entry.query;
				fieldTypes[i] = entry.type;
				i++;
			}

			if (logger.isDebugEnabled())
			{
				logger.debug("Generating search query with fields: " + Arrays.toString(fields) + " for language: " + language);
			}

			return MultiFieldQueryParser.parse(queries, fields, fieldTypes, analyzer);
		}
	}

	public Sort getOrdering()
	{
		if (sortFields.size() == 0)
		{
			logger.debug("SearchRequest has not sort fields. Cannot sort");
			return null;
		}
		else
		{
			SortField[] sorts = new SortField[sortFields.size()];

			int i = 0;
			for (Map.Entry<String, Boolean> entry : sortFields.entrySet())
			{
				sorts[i++] = new SortField(entry.getKey(), !entry.getValue());
			}

			if (logger.isDebugEnabled())
			{
				logger.debug("Generating sort with fields: " + Arrays.toString(sorts) + " for language: " + language);
			}

			return new Sort(sorts);
		}
	}

	private static class SearchFieldVars
	{
		String field;
		String query;
		BooleanClause.Occur type;
		public SearchFieldVars(String field, String query, Occur type)
		{
			super();
			this.field = field;
			this.query = query;
			this.type = type;
		}
	}
}
