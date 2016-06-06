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

/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.infoglue.cms.exception.ConfigurationError;

/**
 * <p>Retrieves the content of a file on the local file system. The DataRetriever expects and requires a configuration containing the key "filePath".</p>
 * 
 * <p>
 * <h3>Config</h3>
 * <strong>filePath</strong> &ndash; The path to the file that shall be retrieved by the DataRetriever.
 * <strong>checkChanged</strong> &ndash; Boolean indicating whether the DataRetriever should use the <em>hasChanged</em> function.
 * </p>
 * 
 * @author Erik Stenbäcka
 */
public class LocalFileDataRetriever implements DataRetriever
{
	private static final Logger logger = Logger.getLogger(LocalFileDataRetriever.class);
	private String filePath;
	private boolean checkChanged;
	private InputStream stream;
	private Date lastFetched;

	@Override
	public void setConfig(Map<String, Object> config)
	{
		if (!config.containsKey("filePath") || !(config.get("filePath") instanceof String))
		{
			throw new ConfigurationError("Must specify filePath - should be the path to the file that will be retrieved");
		}
		else
		{
			this.filePath = (String)config.get("filePath");
		}

		if (config.containsKey("checkChanged"))
		{
			this.checkChanged = ((String)config.get("checkChanged")).equals("yes");
		}
		else
		{
			this.checkChanged = false;
		}
	}

	/**
	 * Does nothing.
	 */
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
				this.lastFetched = new Date();
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

	@Override
	public boolean hasChanged()
	{
		if (this.checkChanged)
		{
			File file = new File(filePath);
			if (!file.exists())
			{
				logger.warn("The given file does not exist. Cannot check alst modified date. File path: " + filePath);
				return true;
			}
			if (this.lastFetched == null)
			{
				return true;
			}
			return file.lastModified() > this.lastFetched.getTime();
		}
		else
		{
			return true;
		}
	}

	@Override
	public String getConfigDetails(String rowPrefix)
	{
		return rowPrefix + "File path: " + filePath;
	}
}
