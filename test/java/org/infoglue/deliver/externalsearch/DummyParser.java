/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Erik Stenbäcka
 *
 */
public class DummyParser implements Parser
{
	private Map<String, Object> config;

	public Map<String, Object> getConfig()
	{
		return this.config;
	}

	@Override
	public void setConfig(Map<String, Object> config)
	{
		this.config = config;
	}

	@Override
	public void init()
	{
	}

	@Override
	public List<Map<String, Object>> parse(InputStream input)
	{
		return null;
	}

	@Override
	public void destroy()
	{
	}

	@Override
	public String getConfigDetails(String rowPrefix)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
