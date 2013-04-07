package org.infoglue.deliver.externalsearch;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
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
		logger.error("About to index entities. Num: " + entities.size());
		prepareIndexing(entities, fields.values());
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

	protected abstract void prepareIndexing(List<Map<String, Object>> entity, Collection<IndexableField> indexableFields);
	protected abstract Document handleEntity(Map<String, Object> entity, Collection<IndexableField> indexableFields) throws IOException;

	protected void registerField(Document document, IndexableField field, String value, Locale language)
	{
		if (value == null)
		{
			value = "";
		}
		try
		{
			String key = field.getFieldName(language);
			if (logger.isDebugEnabled())
			{
				logger.debug("Adding field to index. Field: " + field + ". Value: " + value);
			}
			document.add(new Field(key, value, Field.Store.NO, Field.Index.UN_TOKENIZED));
		}
		catch (NullPointerException nex)
		{
			logger.warn("An attempt to index an unregistered field was made. Field: " + field);
			throw new IllegalArgumentException("An attempt to index an unregistered field was made. Field: " + field);
		}
	}

	protected void storeObject(Document document, Locale language, byte[] value)
	{
		document.add(new Field(SearchResult.getResultLabel(language), value, Field.Store.YES));
	}

}
