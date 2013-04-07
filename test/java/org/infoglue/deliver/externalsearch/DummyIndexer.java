/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.util.List;
import java.util.Map;

import org.apache.lucene.index.IndexWriter;

/**
 * @author Erik Stenbäcka
 *
 */
public class DummyIndexer implements Indexer
{
	private Map<String, String> config;

	public Map<String, String> getConfig()
	{
		return this.config;
	}

	@Override
	public void setConfig(Map<String, String> config)
	{
		this.config = config;
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void registerFields(Map<String, IndexableField> fields)
	{
		
	}

	@Override
	public void index(List<Map<String,Object>> entities, Map<String, IndexableField> fields, IndexWriter indexWriter)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub

	}

}
