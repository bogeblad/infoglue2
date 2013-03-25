/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.Directory;
import org.infoglue.cms.exception.ConfigurationError;
import org.infoglue.cms.exception.SystemException;

/**
 * @author Erik Stenbäcka
 * 
 */
public class ExternalSearchService
{
	private static final Logger logger = Logger.getLogger(ExternalSearchService.class);

	private AtomicBoolean running;
	private String name;
	private Integer maxAge;
	private List<String> dependencies;
	private boolean startIndexing;

	private DataRetriever dataRetriever;
	private Parser parser;
	private Indexer indexer;

	private ExternalSearchServiceConfig newConfig;
	private IndexSearcher indexSearcher;
	
	private ExternalSearchServiceDirectoryHandler directoryHandler;

	public ExternalSearchService(ExternalSearchServiceConfig config) throws ConfigurationError
	{
		this(config, true, null);
	}

	public ExternalSearchService(ExternalSearchServiceConfig config, boolean startIndexing, ExternalSearchServiceDirectoryHandler directoryHandler) throws ConfigurationError
	{
		this.name = config.getName();
		if (this.name == null)
		{
			throw new ConfigurationError("A service needs a name. The configuration file should contain one.");
		}
		if (directoryHandler == null)
		{
			this.directoryHandler = new ExternalSearchServiceDirectoryHandler(this.name);
		}
		else
		{
			this.directoryHandler = directoryHandler;
		}
		this.running = new AtomicBoolean(false);
		this.startIndexing = startIndexing;
		setConfig(config);
		this.indexSearcher = this.directoryHandler.handleOldDirectories();
	}

	public void setConfig(ExternalSearchServiceConfig newConfig) throws ConfigurationError
	{
		logger.info("Queing new config for service: " + name);
		this.newConfig = newConfig;
		if (running.compareAndSet(false, true))
		{
			updateConfig();
			running.set(false);
		}
	}

	public boolean startIndexing()
	{
		if ((startIndexing || indexHasExpired()) && checkDependencies())
		{
			if (running.compareAndSet(false, true))
			{
				startIndexing = false;
				logger.debug("Should start indexing for service: " + name);
				new Thread()
				{
					@Override
					public void run()
					{
						logger.info("Starting indexing for service: " + name);
						updateIndex();
						try
						{
							updateConfig();
						}
						catch (ConfigurationError cex)
						{
							logger.error("Invalid config for external search service. See warning log for more info. Service name: " + name);
							logger.warn("Invalid config for external search service. See warning log for more info. Service name: " + name + ". Config: " + newConfig);
						}
						running.set(false);
					}
				}.start();

				return true;
			}
		}

		return false;
	}

	private String[] languageAdoptFields(String[] sortFields, Locale language)
	{
		if (sortFields == null)
		{
			return null;
		}
		for (int i = 0; i < sortFields.length; i++)
		{
			sortFields[i] = sortFields[i] + language.getLanguage();
		}
		return sortFields;
	}

	public SearchResult search(SearchParameters params) throws SystemException
	{
		List<Object> result = new ArrayList<Object>();
		if (isSearchable())
		{
			// Searcher searcher = null;
			try
			{
				// searcher = new IndexSearcher(directory);
				StandardAnalyzer analyzer = new StandardAnalyzer();
				// Build a Query object
				Query query = new QueryParser("", analyzer).parse(params.getQuery());

				// Search for the query
				Hits hits;
				if (params.shouldSort())
				{
					String[] sortFields = params.getSortFields();
					if (params.getLanguage() != null)
					{
						sortFields = languageAdoptFields(sortFields, params.getLanguage());
					}
					if (logger.isDebugEnabled())
					{
						logger.debug("Searching with sort. Using the following fields for sorting: " + Arrays.toString(sortFields));
					}
					SortField[] sorts = new SortField[params.getSortFields().length];
					for (int i = 0; i < sortFields.length; i++)
					{
						sorts[i] = new SortField(sortFields[i], params.getSortOrder().equals(SearchParameters.SortOrder.DESC));
					}
					hits = indexSearcher.search(query, new Sort(sorts));
				}
				else
				{
					logger.debug("Searching without sort");
					hits = indexSearcher.search(query);
				}

				// Examine the Hits object to see if there were any matches
				int hitCount = hits.length();
				if (logger.isDebugEnabled())
				{
					logger.debug("hit count: " + hitCount + " for query: " + params.getQuery());
				}
				if (hitCount > 0)
				{
					if (params.getCount() != null)
					{
						hitCount = Math.min(hitCount, params.getStartIndex() + params.getCount());
					}
					for (int i = params.getStartIndex() == null ? 0 : params.getStartIndex(); i < hitCount; i++)
					{
						try
						{
							Document document = hits.doc(i);

							byte[] resultBytes = document.getBinaryValue(SearchResult.RESULT);
							if (resultBytes != null)
							{
								ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(resultBytes));
								Object post = ois.readObject();
								result.add(post);
							}
						}
						catch (Exception ex)
						{
							logger.warn("There was an error preparing a search hit for the result list. The item will be excluded from the list. Message: " + ex.getMessage() + ". Type: " + ex.getClass());
						}
					}
				}
				return new SearchResult(result, hits.length());
			}
			catch (ParseException pex)
			{
				logger.warn("Invalid Lucene search query in external search service. Service.name: " + name + ". Message: " + pex.getMessage());
				throw new SystemException("Invalid Lucene search query. See warning logs for more information.");
			}
			catch (Throwable tr)
			{
				logger.error("Error when searching in external search service. Service.name: " + name + ". Message: " + tr.getMessage() + ". Type: " + tr.getClass());
				logger.warn("Error when updating index for external search service. Service.name: " + name, tr);
				throw new SystemException("Error in Lucene search. See warning logs for more information.");
			}
		}

		return new SearchResult(Collections.emptyList(), 0);
	}

	public void destroyService()
	{
		if (this.dataRetriever != null)
		{
			this.dataRetriever.destroy();
		}
		if (this.parser != null)
		{
			this.parser.destroy();
		}
		if (this.indexer != null)
		{
			this.indexer.destroy();
		}
		if (this.directoryHandler != null)
		{
			this.directoryHandler.destroy(this.indexSearcher);
		}
	}

	public boolean isSearchable()
	{
		return indexSearcher != null;
	}

	public boolean indexHasExpired()
	{
		if (this.maxAge == null)
		{
			return false;
		}
		Integer directoryAge = this.directoryHandler.getDirectoryAge();
		if (directoryAge == null)
		{
			return true;
		}
		else
		{
			return new Date().getTime() > (directoryAge + this.maxAge);
		}
	}

	// ///////////////////////////////////////////////////////////////

	private void updateConfig() throws ConfigurationError
	{
		if (newConfig != null)
		{
			logger.info("Updating config for service: " + name);

			if (this.newConfig.getDataRetriever() == null || this.newConfig.getParser() == null || this.newConfig.getIndexer() == null)
			{
				throw new ConfigurationError("The new configuration does not contain all required entities. Config: " + this.newConfig);
			}

			this.maxAge = this.newConfig.getMaxAge() == null ? null : this.newConfig.getMaxAge() * 1000;
			this.dependencies = this.newConfig.getDependencis() == null ? new LinkedList<String>() : this.newConfig.getDependencis();

			if (this.dataRetriever != null)
			{
				this.dataRetriever.destroy();
			}
			this.dataRetriever = this.newConfig.getDataRetriever();
			this.dataRetriever.init();

			if (this.parser != null)
			{
				this.parser.destroy();
			}
			this.parser = this.newConfig.getParser();
			this.parser.init();

			if (this.indexer != null)
			{
				this.indexer.destroy();
			}
			this.indexer = this.newConfig.getIndexer();
			this.indexer.init();

			this.newConfig = null;
			startIndexing = true;
		}
		else
		{
			logger.debug("No new config to update to for service name: " + name);
		}
	}

	private void updateIndex()
	{
		try
		{
			InputStream input = this.dataRetriever.openConnection();
			List<Map<String, Object>> entities = this.parser.parse(input);
			this.dataRetriever.closeConnection();

			Directory directory = this.directoryHandler.getDirectory();

			StandardAnalyzer analyzer = new StandardAnalyzer();
			IndexWriter indexWriter = new IndexWriter(directory, analyzer, true);

			this.indexer.index(entities, indexWriter);

			indexWriter.optimize();
			indexWriter.close();

			this.indexSearcher = this.directoryHandler.changeDirectory(this.indexSearcher, directory);
		}
		catch (Throwable tr)
		{
			logger.error("Error when updating index for external search service. Service.name: " + name + ". Message: " + tr.getMessage() + ". Type: " + tr.getClass());
			logger.warn("Error when updating index for external search service. Service.name: " + name, tr);
		}
	}

	private boolean checkDependencies()
	{
		if (dependencies != null)
		{
			for (String serviceName : dependencies)
			{
				logger.info("Checking dependency for service: " + name + ". Dependency: " + serviceName);
				ExternalSearchService service = ExternalSearchManager.getManager().getService(serviceName);
				if (service == null)
				{
					logger.debug("Dependecy was not found");
					return false;
				}
				else
				{
					if (!service.isSearchable())
					{
						logger.debug("Dependecy was not searchable");
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public static class ParametersFactory
	{
		private static ParametersFactory factory;

		public static ParametersFactory getFactory()
		{
			if (factory == null)
			{
				factory = new ParametersFactory();
			}
			return factory;
		}

		public SearchParameters getParameters()
		{
			return new SearchParameters();
		}

		public SearchParameters getSimpleSearchParameters(String query)
		{
			SearchParameters params = new SearchParameters();
			params.setQuery(query);
			return params;
		}

		public SearchParameters getParameters(String query, String[] sortFields, Boolean sortAscending, Integer startIndex, Integer count)
		{
			SearchParameters.SortOrder sortOrder;
			if (sortFields == null)
			{
				sortOrder = null;
			}
			else
			{
				if (sortAscending == null)
				{
					sortOrder = SearchParameters.SortOrder.ASC;
				}
				else
				{
					if (sortAscending)
					{
						sortOrder = SearchParameters.SortOrder.ASC;
					}
					else
					{
						sortOrder = SearchParameters.SortOrder.DESC;
					}
				}
			}

			return getParameters(query, sortFields, sortOrder, startIndex, count);
		}

		public SearchParameters getParameters(String query, String[] sortFields, SearchParameters.SortOrder sortOrder, Integer startIndex, Integer count)
		{
			SearchParameters params = new SearchParameters();

			params.setQuery(query);
			params.setSortFields(sortFields);
			params.setSortOrder(sortOrder);
			params.setStartIndex(startIndex);
			params.setCount(count);

			return params;
		}
	}
}
