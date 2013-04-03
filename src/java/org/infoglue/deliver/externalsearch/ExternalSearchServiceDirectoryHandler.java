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

/**
 * <p>Helper class for the {@link ExternalSearchService} class. This class will most likely
 * not provide any useful functions for classes other than the ExternalSearchService and it is
 * therefore package-private.</p>
 * 
 * <p>This class handles all interaction between the Lucene directory and the external search service.</p>
 * 
 * @author Erik Stenbäcka
 */
class ExternalSearchServiceDirectoryHandler
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

	/**
	 * <p>May be called at the beginning of the service life-cycles to clear up old stuff from the last time
	 * the service was active and also try and find an old directory that can be used to perform searches.</p>
	 *
	 * Using the same naming schema as {@link #getNewDirectoryFile()} the method will look for old directories on
	 * disc and remove them, except for the newest which will be used as the current directory for this handler.
	 *
	 * @return If an old directory was found in disk an IndexSearcher is created for that directory and returned.
	 */
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
			long directoryCandidateTimeStamp = -1;
			List<File> oldDirectories = new LinkedList<File>();
			int startIndex = (serviceName + "_").length();
			int endOffset = ".directory".length();
			for (File directory : directories)
			{
				String fileName = directory.getName();
				if (fileName.startsWith(serviceName))
				{
					String timeStampString = fileName.substring(startIndex, fileName.length() - endOffset);
					logger.debug("Directory timestamp: " + timeStampString);
					try
					{
						long timeStamp = Long.parseLong(timeStampString);
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
						logger.info("Failed to parse time stamp. How can this happen? Time stamp: '" + timeStampString + "'. File name: " + fileName);
					}
				}
			}

			if (directoryCandidate != null)
			{
				try
				{
					logger.info("Found old directory for service: " + serviceName + ". Directory: " + directoryCandidate.getName());
					this.directory = FSDirectory.getDirectory(directoryCandidate);
					this.creatingDateTime = new Date(directoryCandidateTimeStamp);
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

		return getIndexSearcher(null);
	}

	/**
	 * Gets the path to where all Lucene directories should be stored.
	 * @return
	 */
	private String getLuceneDirectoryPath()
	{
		StringBuilder sb = new StringBuilder();
		return sb.append(CmsPropertyHandler.getContextRootPath()).append(File.separator).append("lucene").append(File.separator).toString();
	}

	/**
	 * <p>Generates a File reference to a path that can be used to create a new directory. The intent of the method
	 * is to create a unique path so that it does not interfere with other directories however this is not guaranteed nor
	 * verified.</p>
	 * 
	 * <p>The method uses the service name combined with a unique value to created the directory name. It is therefore important
	 * that a service name is defined (i.e. not null) for this handler before this method is called.</p>
	 *
	 * @return A reference to a path where a directory can be created.
	 * @throws NullPointerException If the handle does not have a service name.
	 */
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

	/**
	 * Creates and returns a new Directory.
	 *
	 * @return A new directory for the handlers service.
	 * @throws NullPointerException If no service name has been provided for this service.
	 * @throws IOException If an error occurs when creating the directory.
	 */
	Directory getDirectory() throws NullPointerException, IOException
	{
		return FSDirectory.getDirectory(getNewDirectoryFile());
	}

	/**
	 * Goes through all references to old objects and attempts to dispose of them.
	 * This somewhat round about way of removing old references is used because
	 * there has been problems with removing references due to IO related problems.
	 * This solution keeps track of the objects until they can be removed for real.
	 */
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
			if (oldReferences.size() > 20)
			{
				logger.error("####################################################\nSomething is wrong with the old references in this directory handler. Will clear list. This is likely to leavev unwanted files on the server. References: " + oldReferences);
				oldReferences.clear();
			}

			Iterator<Object> it = oldReferences.iterator();
			Object reference;
			logger.info("Will try to clear " + oldReferences.size() + " from service with name: " + serviceName);
			while (it.hasNext())
			{
				reference = it.next();
				try
				{
					if (reference == null)
					{
						logger.debug("Null reference, removing from old reference list. Service: " + serviceName);
						it.remove();
						continue;
					}
					if (reference instanceof FSDirectory)
					{
						logger.debug("Found FSDirectory reference in old references. Service: " + serviceName);
						((FSDirectory) reference).close();
						File file = ((FSDirectory) reference).getFile();
						if (file == null)
						{
							logger.warn("The directory did not contain a file. Weird, lets remove it. Directory: " + reference);
							it.remove();
						}
						else
						{
							try
							{
								FileUtils.deleteDirectory(file);
								logger.debug("Removed directory file from disc. Remove directory from old directories list. File: " + file.getAbsolutePath());
								it.remove();
							}
							catch (IOException ioex)
							{
								logger.warn("Failed to remove old directory from disk. Will keep it in the list. File: " + file.getAbsolutePath() + ". Message: " + ioex.getMessage());
							}
						}
					}
					else if (reference instanceof IndexSearcher)
					{
						logger.debug("Found IndexSearcher reference in old references. Service: " + serviceName);
						try
						{
							((IndexSearcher) reference).close();
							it.remove();
							logger.info("Closed old IndexSearcher");
						}
						catch (IOException ex)
						{
							logger.warn("Failed to close IndexSearcher. Will try again later. Message: " + ex.getMessage());
						}
					}
					else
					{
						logger.warn("Found not-wanted object in old references. How can this happen? Object type: " + reference.getClass() + ". Tostring: " + reference);
						it.remove();
					}
				}
				catch (Throwable tr)
				{
					logger.warn("Caught exception when trying to dispose of an old reference. Will try to remove it again later. Object type: " + reference.getClass() + ". Tostring: " + reference +
							" Message: " + tr.getMessage() + ". Type: " + tr.getClass());
				}
			}
		}
	}

	/**
	 * Gets an IndexSearcher for the current directory. If there is no current directory or if an error occurs
	 * null is returned. The IndexSearcher parameter provides a possibility to mark an IndexSearcher for disposal.
	 * If null is given as the argument no IndexSearcher is marked for disposal.
	 *
	 * @param oldIndexSearch
	 * @return
	 */
	IndexSearcher getIndexSearcher(IndexSearcher oldIndexSearch)
	{
		try
		{
			if (oldIndexSearch != null)
			{
				oldReferences.add(oldIndexSearch);
			}
			if (this.directory != null)
			{
				return new IndexSearcher(directory);
			}
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

	/**
	 * <p>Indicate to the directory handler that the service wants to change (update) directory to a new one.
	 * The directory to change to is provided as an argument. The IndexSearcher is provided so that the directory
	 * handler may close and delete it. It is expected but not verified that the given IndexSearcher is connected
	 * to the current directory of this directory handler.</p>
	 * 
	 * <p>The old directory is closed and removed by this method and a new IndexSearcher, connected to the new
	 * directory, is initialized and returned. Observe that this method will fail silently if the old directory can not
	 * be deleted. However the directory handler will still keep track if it and attempt to remove it later.</p>
	 * 
	 * @param oldIndexSearcher If provided the directory handler will dispose of this IndexSearcher
	 * @param newDirectory The directory to change to.
	 * @return An IndexSearcher for the new (given) directory.
	 */
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

	/**
	 * Marks the given directory for deletion. Observe that the directory is not delete when this method is called
	 * but only marked for deletion. The directory is removed first when {@link #clearOldDirectories()} is called.
	 *
	 * @param oldDirectory The directory to mark for deletion
	 */
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
		return (int) (new Date().getTime() - creatingDateTime.getTime());
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
