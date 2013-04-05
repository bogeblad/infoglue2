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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.IndexWriter;


/**
 * An indexer is responsible for converting the values in the <em>entities</em> list into
 * searchable documents that are written to the indexWriter.
 * 
 * @author Erik Stenbäcka
 */
public interface Indexer extends ExternalSearchDelegate
{
	/**
	 * Uses the <em>entities</em> to populate the directory behind the <em>IndexWriter</em>.
	 * @param entities Data that may be used by the indexer when populating the index
	 * @param indexWriter An IndexWriter to write Lucene documents to.
	 */
	void index(List<Map<String,Object>> entities, Map<String, IndexableField> fields, IndexWriter indexWriter);
}
