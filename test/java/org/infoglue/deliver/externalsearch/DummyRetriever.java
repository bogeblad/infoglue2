/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.io.InputStream;
import java.util.Map;

/**
 * @author Erik Stenbäcka
 *
 */
public class DummyRetriever implements DataRetriever
{
	Map<String, String> config;

	public Map<String, String> getConfig()
	{
		return this.config;
	}

	@Override
	public void init()
	{
	}

	@Override
	public InputStream openConnection()
	{
		return null;
	}

	@Override
	public boolean closeConnection()
	{
		return true;
	}

	@Override
	public void destroy()
	{

	}

	@Override
	public void setConfig(Map<String, String> config)
	{
		this.config = config;
	}

	@Override
	public String getConfigDetails(String rowPrefix)
	{
		return null;
	}

}
