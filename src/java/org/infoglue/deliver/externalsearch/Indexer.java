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
public interface Indexer extends ConfigurableDelegate
{
	void init();
	void index(List<Map<String,Object>> entities, IndexWriter indexWriter);
	void destroy();
}
