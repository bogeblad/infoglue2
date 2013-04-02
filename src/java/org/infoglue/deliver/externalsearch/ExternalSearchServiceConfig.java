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

import java.util.List;
import java.util.Locale;

/**
 * <p>This class represents an {@link ExternalSearchService} configuration. It is a POJO with
 * not logic of its own. The expected usage if this class is that the ExternnalSearchManager creates
 * objects if the class based on the external search service configuration and then manages services
 * based in the information in the parsed objects. A configuration is also passed to each service
 * and the service uses the information in the configuration to configure itself.</p>
 * 
 * <p>Observe that the class is used with a deserializing library when parsing the configuration.
 * Changes to this class may confuse the parsing library. Refer to the {@link ExternalSearchManager}
 * soruce code for details of the parsing process.</p>
 * 
 * @author Erik Stenbäcka
 */
class ExternalSearchServiceConfig
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
