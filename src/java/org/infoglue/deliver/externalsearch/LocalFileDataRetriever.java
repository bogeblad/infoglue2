/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.infoglue.cms.exception.ConfigurationError;

/**
 * @author Erik Stenbäcka
 *
 */
public class LocalFileDataRetriever implements DataRetriever
{
	private static final Logger logger = Logger.getLogger(LocalFileDataRetriever.class);
	private String filePath;
	private InputStream stream;
	
	@Override
	public void setConfig(Map<String, String> config)
	{
		if (!config.containsKey("filePath"))
		{
			throw new ConfigurationError("Must specify filePath - should be the path to the file that will be retrieved");
		}
		else
		{
			this.filePath = config.get("filePath");
		}
	}

	@Override
	public void init()
	{
	}

	@Override
	public InputStream openConnection()
	{
		if (stream != null)
		{
			closeConnection();
		}
		File file = new File(filePath);
		if (!file.exists())
		{
			logger.warn("The given file does not exist. File path: " + filePath);
			return null;
		}
		FileInputStream fis;
		try
		{
			fis = new FileInputStream(file);
			this.stream = fis;
			return this.stream;
		}
		catch (FileNotFoundException e)
		{
			logger.warn("The given file does not exist. File path: " + filePath);
		}

		return null;
	}

	@Override
	public boolean closeConnection()
	{
		if (stream != null)
		{
			try
			{
				stream.close();
				this.stream = null;
				return true;
			}
			catch (IOException ex)
			{
				logger.error("Failed to close file input stream in data retriever. Message: " + ex.getMessage());
				return false;
			}
		}
		logger.info("No stream to close for data retriever. File path: " + filePath);
		return true;
	}

	@Override
	public void destroy()
	{
	}
}
