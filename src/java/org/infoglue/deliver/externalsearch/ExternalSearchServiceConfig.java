/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.util.List;
import java.util.Locale;

/**
 * @author Erik Stenbäcka
 *
 */
public class ExternalSearchServiceConfig
{
	private String name;
	private Integer maxAge;
	private List<String> dependencis;
	private Locale defaultLanguage;
	private DataRetriever dataRetriever;
	private Parser parser;
	private Indexer indexer;

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public Integer getMaxAge()
	{
		return maxAge;
	}

	public void setMaxAge(Integer maxAge)
	{
		this.maxAge = maxAge;
	}

	public List<String> getDependencis()
	{
		return dependencis;
	}

	public void setDependencis(List<String> dependencis)
	{
		this.dependencis = dependencis;
	}

	public DataRetriever getDataRetriever()
	{
		return dataRetriever;
	}

	public void setDataRetriever(DataRetriever dataRetriever)
	{
		this.dataRetriever = dataRetriever;
	}

	public Parser getParser()
	{
		return parser;
	}

	public void setParser(Parser parser)
	{
		this.parser = parser;
	}

	public Indexer getIndexer()
	{
		return indexer;
	}

	public void setIndexer(Indexer indexer)
	{
		this.indexer = indexer;
	}

	public Locale getDefaultLanguage()
	{
		return defaultLanguage;
	}

	public void setDefaultLanguage(Locale defaultLanguage)
	{
		this.defaultLanguage = defaultLanguage;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Config {name:").append(name).append(", maxAge:").append(maxAge).append(", dependencies:").append(dependencis).append(", defaultLanguage:").append(defaultLanguage).append("}")
		  .append("\n\tDataRetriever:").append(dataRetriever == null ? "null" : dataRetriever.getClass())
		  .append("\n\tParser:").append(parser == null ? "null" : parser.getClass())
		  .append("\n\tIndexer:").append(indexer == null ? "null" : indexer.getClass())
		  .append("}");
		return sb.toString();
	}


}
