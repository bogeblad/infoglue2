package org.infoglue.deliver.externalsearch;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.infoglue.cms.util.CmsPropertyHandler;

public class ExternalSearchServiceDirectoryHandler
{
	private static final Logger logger = Logger.getLogger(ExternalSearchServiceDirectoryHandler.class);

	private List<Object> oldReferences;
	private FSDirectory directory;
	private String serviceName;
	private Date creatingDateTime;

	public ExternalSearchServiceDirectoryHandler(String serviceName)
	{
		this.serviceName = serviceName;
		this.oldReferences = new LinkedList<Object>();
	}

	IndexSearcher handleOldDirectories()
	{
		File file = new File(getLuceneDirectoryPath());
		if (!file.exists() || !file.isDirectory())
		{
			logger.warn("The Lucene directory does not exist. Will not try to clear old directories. Path: " + file.getAbsolutePath());
		}
		else
		{
			File[] directories = file.listFiles();
			File directoryCandidate = null;
			int directoryCandidateTimeStamp = -1;
			List<File> oldDirectories = new LinkedList<File>();
			int startIndex = (serviceName + "_").length();
			int endOffset = ".directory".length();
			for (File directory : directories)
			{
				String fileName = directory.getName();
				System.out.println("file name: " + fileName);
				System.out.println("abs file name: " + directory.getAbsolutePath());
				if (fileName.startsWith(serviceName))
				{
					String timeStampString = fileName.substring(startIndex, fileName.length() - endOffset);
					logger.debug("Directory timestamp: " + timeStampString);
					try
					{
						int timeStamp = Integer.parseInt(timeStampString);
						if (directoryCandidate == null)
						{
							directoryCandidate = directory;
							directoryCandidateTimeStamp = timeStamp;
						}
						else
						{
							if (timeStamp > directoryCandidateTimeStamp)
							{
								logger.debug("Found new candidate. File name: " + fileName);
								if (directoryCandidate != null)
								{
									oldDirectories.add(directoryCandidate);
								}
								directoryCandidate = directory;
								directoryCandidateTimeStamp = timeStamp;
							}
							else
							{
								oldDirectories.add(directory);
							}
						}
					}
					catch (NumberFormatException nfex)
					{
						logger.info("Failed to parse time stamp. How can this happen? Time stamp: " + timeStampString + ". File name: " + fileName);
					}
				}
			}

			if (directoryCandidate != null)
			{
				try
				{
					this.directory = FSDirectory.getDirectory(directoryCandidate);
					this.creatingDateTime = new Date(directoryCandidateTimeStamp);
					return getIndexSearcher(null);
				}
				catch (IOException e)
				{
					logger.warn("Failed to initialize external search service from old directory file. IOException");
				}
			}

			logger.info("Found " + oldDirectories.size() + " old directories. Will attempt to remove them.");
			try
			{
				for (File directory : oldDirectories)
				{
					FileUtils.deleteDirectory(directory);
				}
			}
			catch (IOException ex)
			{
				logger.warn("Failed to clean-up old directory on disc. Message: " + ex.getMessage());
			}
		}

		return null;
	}

	private String getLuceneDirectoryPath()
	{
		StringBuilder sb = new StringBuilder();
		return sb.append(CmsPropertyHandler.getContextRootPath()).append(File.separator).append("lucene").append(File.separator).toString();
	}

	File getNewDirectoryFile() throws NullPointerException
	{
		if (serviceName == null)
		{
			throw new NullPointerException("serviceName may not be null");
		}
		String directoryFileName = getLuceneDirectoryPath() + serviceName + "_" + System.currentTimeMillis() + ".directory";
		File directoryFile = new File(directoryFileName);
		logger.info("Creating new directory. The directory will be stored in the file: " + directoryFile.getName());

		if (directoryFile.exists())
		{
			logger.warn("Directory name candidate already exists. Directory name: " + directoryFile.getName());
		}

		return directoryFile;
	}

	Directory getDirectory() throws NullPointerException, IOException
	{
		return FSDirectory.getDirectory(getNewDirectoryFile());
	}

	void clearOldDirectories()
	{
		/*
		 * Developer note: There has historically been cases where a directory
		 * could not be removed from the machine at a given time. It is
		 * therefore important to keep track of all old directories so that we
		 * can make sure they are delete at some point.
		 */
		synchronized (oldReferences)
		{
			Iterator<Object> it = oldReferences.iterator();
			Object reference;
			while (it.hasNext())
			{
				reference = it.next();
				if (reference == null)
				{
					continue;
				}
				if (reference instanceof FSDirectory)
				{
					((FSDirectory) reference).close();
					File file = ((FSDirectory) reference).getFile();
					boolean success = file.delete();
					if (success)
					{
						logger.debug("Removed directory file from disc. Remove directory from old directories list. File: " + file.getAbsolutePath());
						it.remove();
					}
				}
				else if (reference instanceof IndexSearcher)
				{
					try
					{
						((IndexSearcher) reference).close();
						logger.info("Closed old IndexSearcher");
					}
					catch (IOException ex)
					{
						logger.warn("Failed to close IndexSearcher. Will try again later. Message: " + ex.getMessage());
					}
				}
			}
		}
	}

	IndexSearcher getIndexSearcher(IndexSearcher oldIndexSearch)
	{
		try
		{
			if (oldIndexSearch != null)
			{
				oldReferences.add(oldIndexSearch);
			}
			return new IndexSearcher(directory);
		}
		catch (CorruptIndexException ciex)
		{
			logger.error("Indexes was corrupted when trying to make index searcher for external search service. Message: " + ciex.getMessage());
			logger.warn("Indexes was corrupted when trying to make index searcher for external search service", ciex);
		}
		catch (IOException ioex)
		{
			logger.error("IOException when trying to make index searcher for external search service. Message: " + ioex.getMessage());
			logger.warn("IOException when trying to make index searcher for external search service", ioex);
		}
		return null;
	}

	IndexSearcher changeDirectory(IndexSearcher oldIndexSearcher, Directory newDirectory)
	{
		if (!(newDirectory instanceof FSDirectory))
		{
			throw new IllegalArgumentException("The given directory was of the wrong type. Avoid this error by only providing directories that was generated by the directory handler type");
		}

		deleteDirectory(this.directory);
		this.directory = (FSDirectory)newDirectory;
		this.creatingDateTime = new Date();
		this.clearOldDirectories();
		return getIndexSearcher(oldIndexSearcher);
	}

	void deleteDirectory(Directory oldDirectory)
	{
		synchronized (oldReferences)
		{
			oldReferences.add(oldDirectory);
		}
	}
	
	/**
	 * Returns the age of the directory held by this directory handler. The age is given in seconds.
	 * 
	 * This value is reset when changeDirectory is called successfully.
	 * 
	 * @return The age, in seconds, of the current directory. If no directory is defined null is returned.
	 */
	Integer getDirectoryAge()
	{
		if (this.creatingDateTime == null)
		{
			return null;
		}
		return Math.round((new Date().getTime() - creatingDateTime.getTime()) / 1000.0f);
	}

	/**
	 * Life-cycle method for the directory handler. Gives it a chance to tidy things up when it should not be used anymore.
	 * 
	 * @param indexSearcher
	 */
	public void destroy(IndexSearcher indexSearcher)
	{
		try
		{
			if (indexSearcher != null)
			{
				indexSearcher.close();
			}
			if (this.directory != null)
			{
				this.directory.close();
			}
		}
		catch (IOException ex)
		{
			logger.warn("Failed to close index searcher due to an IO exception when terminating directory handler. The handler will remain open. Message: " + ex.getMessage());
		}
		this.directory = null;
		this.creatingDateTime = null;
	}
}
