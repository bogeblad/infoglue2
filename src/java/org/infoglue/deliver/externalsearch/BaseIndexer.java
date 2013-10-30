package org.infoglue.deliver.externalsearch;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

public abstract class BaseIndexer implements Indexer
{
	private static final Logger logger = Logger.getLogger(BaseIndexer.class);

	@Override
	public void index(List<Map<String, Object>> entities, Map<String, IndexableField> fields, IndexWriter indexWriter)
	{
		boolean prepareSuccess = prepareIndexing(entities, fields.values());
		if (!prepareSuccess)
		{
			logger.warn("Will not perform indexing because there was an error in the preparation.");
		}
		else
		{
			for (Map<String, Object> entity : entities)
			{
				try
				{
					Document document = handleEntity(entity, fields.values());
					if (document != null)
					{
						document.add(new Field(SearchRequest.ANY_FIELD, "true", Field.Store.NO, Field.Index.UN_TOKENIZED));
						indexWriter.addDocument(document);
					}
				}
				catch (IOException ex)
				{
					logger.error("Failed to serialize result object. Will ignore entiry post. Message: " + ex.getMessage() + ". Type: " + ex.getClass());
					logger.warn("Failed to serialize result object. Will ignore entiry post.", ex);
				}
			}
		}
	}

	protected abstract boolean prepareIndexing(List<Map<String, Object>> entity, Collection<IndexableField> indexableFields);
	protected abstract Document handleEntity(Map<String, Object> entity, Collection<IndexableField> indexableFields) throws IOException;
}
