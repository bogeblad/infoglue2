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

package org.infoglue.deliver.controllers.kernel.impl.simple;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.entities.content.DigitalAsset;
import org.infoglue.cms.entities.content.DigitalAssetVO;
import org.infoglue.cms.entities.management.Repository;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.graphics.ThumbnailGenerator;
import org.infoglue.deliver.applications.databeans.DeliveryContext;
import org.infoglue.deliver.controllers.kernel.URLComposer;
import org.infoglue.deliver.util.HttpHelper;


public class DigitalAssetDeliveryController extends BaseDeliveryController
{
    private final static Logger logger = Logger.getLogger(DigitalAssetDeliveryController.class.getName());

	class FilenameFilterImpl implements FilenameFilter 
	{
		private String filter = ".";
		
		public FilenameFilterImpl(String aFilter)
		{
			filter = aFilter;
		}
		
    	public boolean accept(File dir, String name) 
    	{
        	return name.startsWith(filter);
    	}
	};


	/**
	 * Private constructor to enforce factory-use
	 */
	
	private DigitalAssetDeliveryController()
	{
	}
	
	/**
	 * Factory method
	 */
	
	public static DigitalAssetDeliveryController getDigitalAssetDeliveryController()
	{
		return new DigitalAssetDeliveryController();
	}
	
	
	/**
	 * This is the basic way of getting an asset-url for a digital asset. 
	 * If the asset is cached on disk it returns that path imediately it's ok - otherwise it dumps it fresh.
	 */

	public String getAssetUrl(DigitalAsset digitalAsset, Repository repository, DeliveryContext deliveryContext) throws SystemException, Exception
	{
		String assetUrl = "";
		
    	if(digitalAsset != null)
		{
			String fileName = digitalAsset.getDigitalAssetId() + "_" + digitalAsset.getAssetFileName();
			//String filePath = CmsPropertyHandler.getDigitalAssetPath();
			
			int i = 0;
			String filePath = CmsPropertyHandler.getProperty("digitalAssetPath." + i);
			//System.out.println("filePath:" + filePath);
			while(filePath != null)
			{
				try
				{
					DigitalAssetDeliveryController.getDigitalAssetDeliveryController().dumpDigitalAsset(digitalAsset, fileName, filePath);
				}
				catch(Exception e)
				{
					logger.warn("An file could not be written:" + e.getMessage(), e);
				}

				i++;
				filePath = CmsPropertyHandler.getProperty("digitalAssetPath." + i);
				//System.out.println("filePath:" + filePath);
			}

			//DigitalAssetDeliveryController.getDigitalAssetDeliveryController().dumpDigitalAsset(digitalAsset, fileName, filePath);
			
			String dnsName = CmsPropertyHandler.getWebServerAddress();
			if(repository != null && repository.getDnsName() != null && !repository.getDnsName().equals(""))
				dnsName = repository.getDnsName();
				
			assetUrl = URLComposer.getURLComposer().composeDigitalAssetUrl(dnsName, fileName, deliveryContext); 
		}

		return assetUrl;
	}


	/**
	 * This is the basic way of getting an asset-url for a digital asset. 
	 * If the asset is cached on disk it returns that path imediately it's ok - otherwise it dumps it fresh.
	 */

	public String getAssetThumbnailUrl(DigitalAsset digitalAsset, Repository repository, int width, int height, DeliveryContext deliveryContext) throws SystemException, Exception
	{
		String assetUrl = "";
		
    	if(digitalAsset != null)
		{
			String fileName = digitalAsset.getDigitalAssetId() + "_" + digitalAsset.getAssetFileName();
			String thumbnailFileName = "thumbnail_" + width + "_" + height + "_" + fileName;

			int i = 0;
			String filePath = CmsPropertyHandler.getProperty("digitalAssetPath." + i);
			while(filePath != null)
			{
				try
				{
					DigitalAssetDeliveryController.getDigitalAssetDeliveryController().dumpDigitalAsset(digitalAsset, fileName, filePath);
					DigitalAssetDeliveryController.getDigitalAssetDeliveryController().dumpDigitalAssetThumbnail(fileName, thumbnailFileName, filePath, width, height);
				}
				catch(Exception e)
				{
					logger.warn("An file could not be written:" + e.getMessage(), e);
				}
				
				i++;
				filePath = CmsPropertyHandler.getProperty("digitalAssetPath." + i);
			}

			//String filePath = CmsPropertyHandler.getDigitalAssetPath();
			//DigitalAssetDeliveryController.getDigitalAssetDeliveryController().dumpDigitalAsset(digitalAsset, fileName, filePath);
			//DigitalAssetDeliveryController.getDigitalAssetDeliveryController().dumpDigitalAssetThumbnail(digitalAsset, fileName, thumbnailFileName, filePath, width, height);
			
			String dnsName = CmsPropertyHandler.getWebServerAddress();
			if(repository != null && repository.getDnsName() != null && !repository.getDnsName().equals(""))
				dnsName = repository.getDnsName();
				
			assetUrl = URLComposer.getURLComposer().composeDigitalAssetUrl(dnsName, thumbnailFileName, deliveryContext); 
		}

		return assetUrl;
	}

	
   	/**
   	 * This method checks if the given file exists on disk. If it does it's ignored because
   	 * that means that the file is allready cached on the server. If not we dump
   	 * the text on it.
   	 */
   	
	public void dumpAttributeToFile(String attributeValue, String fileName, String filePath) throws Exception
	{
		File outputFile = new File(filePath + File.separator + fileName);
		PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
        pw.println(attributeValue);    
        pw.close();
	}
	
   	/**
   	 * This method checks if the given file exists on disk. If it does it's ignored because
   	 * that means that the file is allready cached on the server. If not we dump
   	 * the given url on it.
   	 */
   	
	public File dumpUrlToFile(String fileName, String filePath, String pageContent) throws Exception
	{
		File outputFile = new File(filePath + File.separator + fileName);
		logger.info("outputFile:" + outputFile.getAbsolutePath());
		if(!outputFile.exists() || outputFile.length() == 0)
		{
			PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
	        pw.println(pageContent);    
	        pw.close();
		}
		
		return outputFile;
	}

   	/**
   	 * This method checks if the given file exists on disk. If it does it's ignored because
   	 * that means that the file is allready cached on the server. If not we dump
   	 * the given url on it.
   	 */
   	/*
	public String getUrlToFile(String url, Map headers, String fileName, String filePath) throws Exception
	{
		String pageContent = null;
		
		File outputFile = new File(filePath + File.separator + fileName);
		System.out.println("outputFile:" + outputFile.getAbsolutePath());
		if(!outputFile.exists() || outputFile.length() == 0)
		{
			HttpHelper helper = new HttpHelper();
			pageContent = helper.getUrlContent(url, headers);
			System.out.println("pageContent:" + pageContent);
			if(pageContent == null || pageContent.equals(""))
				return null;

			PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
	        pw.println(pageContent);    
	        pw.close();
		}
		
		return pageContent;
	}
	*/
	

 	/**
   	 * This method checks if the given file exists on disk. If it does it's ignored because
   	 * that means that the file is allready cached on the server. If not we take out the stream from the 
   	 * digitalAsset-object and dumps it.
   	 */
/*
    public File dumpDigitalAsset(DigitalAsset digitalAsset, String fileName, String filePath) throws Exception
    {
		long timer = System.currentTimeMillis();

		File outputFile = outputFile = new File(filePath, fileName);

        // Check existing files
        if (outputFile.exists() && outputFile.createNewFile() == false)
        {
            return outputFile;
        }

        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        FileDescriptor fileDescriptor = null;
        FileChannel fileChannel = null;
        FileLock fileLock = null;
        try
        {

            fileOutputStream = new FileOutputStream(outputFile);
            fileDescriptor = fileOutputStream.getFD();
            fileChannel = fileOutputStream.getChannel();
            fileLock = fileChannel.tryLock();
            if (fileLock == null)
            {
                // if lock is taken return existing file
                return outputFile;
            }

            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedInputStream = new BufferedInputStream(digitalAsset.getAssetBlob());

            byte[] buf = new byte[4096];
            int c = 0;
            while ((c = bufferedInputStream.read(buf)) != -1)
            {
                bufferedOutputStream.write(buf, 0, c);
            }
        }
        catch (Exception exception)
        {
            throw exception;
        }
        finally
        {
            if (bufferedOutputStream != null)
            {
                bufferedOutputStream.flush();
            }
            if (fileDescriptor != null)
            {
                fileDescriptor.sync();
            }
            if (fileLock != null)
            {
                fileLock.release();
            }
            if (fileChannel != null)
            {
                fileChannel.close();
            }
            if (bufferedInputStream != null)
            {
                bufferedInputStream.close();
            }
            if (fileOutputStream != null)
            {
                fileOutputStream.close();
            }
            if (bufferedOutputStream != null)
            {
                bufferedOutputStream.close();
            }
            fileLock = null;
            fileChannel = null;
            bufferedInputStream = null;
            fileOutputStream = null;
            bufferedOutputStream = null;
            fileDescriptor = null;
        }

        logger.warn("Time for dumping file " + fileName + ":" + (System.currentTimeMillis() - timer));

        return outputFile;
    }
*/
	
	public File dumpDigitalAsset(DigitalAsset digitalAsset, String fileName, String filePath) throws Exception
	{
		long timer = System.currentTimeMillis();
		File outputFile = new File(filePath + File.separator + fileName);
		//logger.warn("outputFile:" + filePath + File.separator + fileName + ":" + outputFile.length());
		if(outputFile.exists())
		{
			//logger.warn("The file allready exists so we don't need to dump it again..");
			return outputFile;
		}

		try 
		{
			//System.out.println("outputFile:" + filePath + File.separator + fileName);
			outputFile.createNewFile();

			//Thread.sleep(5000);
			
			InputStream inputStream = digitalAsset.getAssetBlob();
			synchronized(inputStream)
			{
				//System.out.println("inputStream: " + inputStream + ":" + inputStream.available());
				if(inputStream.available() > 0)
				{
					FileOutputStream fos = new FileOutputStream(outputFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					BufferedInputStream bis = new BufferedInputStream(inputStream);
					
					//BufferedInputStream bis = new BufferedInputStream(digitalAsset.getAssetBlob());
					
					int character;
					int i=0;
			        while ((character = bis.read()) != -1)
			        {
						bos.write(character);
						i++;
			        }
			        
			        if(i == 0)
			        	logger.warn("Wrote " + i + " chars...");
			        
					bos.flush();
				    fos.close();
					bos.close();
						
			        bis.close();
				}
			}
			
			//logger.warn("Time for dumping file " + fileName + ":" + (System.currentTimeMillis() - timer));

			if(outputFile.length() == 0)
				logger.warn("written file:" + outputFile.length());
				
			//FileDescriptor fd = fos.getFD();
			//fd.sync();
		}
		catch (IOException e) 
		{
			throw new Exception("Could not write file " + outputFile.getAbsolutePath() + " - error reported:" + e.getMessage(), e);
	    }
		
        return outputFile;
	}


 	/**
   	 * This method checks if the given file exists on disk. If it does it's ignored because
   	 * that means that the file is allready cached on the server. If not we take out the stream from the 
   	 * digitalAsset-object and dumps it.
   	 */
   	
	public File dumpDigitalAsset(File masterFile, String fileName, String filePath) throws Exception
	{
		long timer = System.currentTimeMillis();
		
		File outputFile = new File(filePath + File.separator + fileName);
		//System.out.println("outputFile:" + filePath + File.separator + fileName + ":" + outputFile.length());
		if(outputFile.exists() && outputFile.length() > 0)
		{
			//logger.info("The file allready exists so we don't need to dump it again..");
			return outputFile;
		}
		
		try 
		{
			//System.out.println("outputFile:" + filePath + File.separator + fileName);
			outputFile.createNewFile();
			
			FileOutputStream fis = new FileOutputStream(outputFile);
			BufferedOutputStream bos = new BufferedOutputStream(fis);
	
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(masterFile));
			
			int character;
	        while ((character = bis.read()) != -1)
	        {
				bos.write(character);
	        }
			bos.flush();
			
	        bis.close();
			fis.close();
			bos.close();
			//logger.warn("Time for dumping file " + fileName + ":" + (System.currentTimeMillis() - timer));
	
			//FileDescriptor fd = fis.getFD();
			//fd.sync();
		}
		catch (IOException e) 
		{
			throw new Exception("Could not write file " + outputFile.getAbsolutePath() + " - error reported:" + e.getMessage(), e);
	    }
		
        return outputFile;
	}

	/**
	 * This method checks if the given file exists on disk. If it does it's ignored because
	 * that means that the file is allready cached on the server. If not we take out the stream from the 
	 * digitalAsset-object and dumps a thumbnail to it.
	 */
   	
	public File dumpDigitalAssetThumbnail(String fileName, String thumbnailFile, String filePath, int width, int height) throws Exception
	{
		long timer = System.currentTimeMillis();
		logger.info("fileName:" + fileName);
		logger.info("thumbnailFile:" + thumbnailFile);
		
		File outputFile = new File(filePath + File.separator + thumbnailFile);
		if(outputFile.exists())
		{
			logger.info("The file allready exists so we don't need to dump it again..");
			return outputFile;
		}
		
		ThumbnailGenerator tg = new ThumbnailGenerator();
		tg.transform(filePath + File.separator + fileName, filePath + File.separator + thumbnailFile, width, height, 100);
		
		logger.info("Time for dumping file " + fileName + ":" + (System.currentTimeMillis() - timer));
		
		return outputFile;
	}
	
	
	/**
   	 * This method dumps the digitalAsset to file and unzips it. If it does it's ignored because
   	 * that means that the file is allready cached on the server. If not we take out the stream from the 
   	 * digitalAsset-object and dumps it.
   	 */
   	
	public File dumpAndUnzipDigitalAsset(DigitalAsset digitalAsset, String fileName, String filePath, File unzipDirectory) throws Exception
	{
		File zipFile = dumpDigitalAsset(digitalAsset, fileName, filePath);
		File outputFile = new File(filePath + File.separator + fileName);
		unzipFile(outputFile, unzipDirectory);
		return zipFile;
	}

	/**
   	 * This method dumps the digitalAsset to file and unzips it. If it does it's ignored because
   	 * that means that the file is allready cached on the server. If not we take out the stream from the 
   	 * digitalAsset-object and dumps it.
   	 */
   	
	public File dumpAndUnzipDigitalAsset(File masterFile, String fileName, String filePath, File unzipDirectory) throws Exception
	{
		File zipFile = dumpDigitalAsset(masterFile, fileName, filePath);
		File outputFile = new File(filePath + File.separator + fileName);
		unzipFile(outputFile, unzipDirectory);
		return zipFile;
	}

	public Vector dumpAndGetZipEntries(DigitalAsset digitalAsset, String fileName, String filePath, File unzipDirectory) throws Exception
	{
		dumpDigitalAsset(digitalAsset, fileName, filePath);
		File outputFile = new File(filePath + File.separator + fileName);
		return getZipFileEntries(outputFile, unzipDirectory);
	}
	
	public Vector dumpAndGetZipEntries(File masterFile, String fileName, String filePath, File unzipDirectory) throws Exception
	{
		dumpDigitalAsset(masterFile, fileName, filePath);
		File outputFile = new File(filePath + File.separator + fileName);
		return getZipFileEntries(outputFile, unzipDirectory);
	}
	

	/**
	 * This method removes all images in the digitalAsset directory which belongs to a certain content version.
	 */

	public void deleteContentVersionAssets(Integer contentVersionId) throws SystemException, Exception
	{
		try
		{
		    List digitalAssetVOList = DigitalAssetController.getController().getDigitalAssetVOList(contentVersionId);
			Iterator assetIterator = digitalAssetVOList.iterator();
			while(assetIterator.hasNext())
			{
			    DigitalAssetVO digitalAssetVO = (DigitalAssetVO)assetIterator.next();
			    this.deleteDigitalAssets(digitalAssetVO.getId());
			}
		}
		catch(Exception e)
		{
			logger.error("Could not delete the assets for the contentVersion " + contentVersionId + ":" + e.getMessage(), e);
		}
	}

	
	
	/**
	 * This method removes all images in the digitalAsset directory which belongs to a certain digital asset.
	 */
	
	public void deleteDigitalAssets(Integer digitalAssetId) throws SystemException, Exception
	{ 
		try
		{
			int i = 0;
			String filePath = CmsPropertyHandler.getProperty("digitalAssetPath." + i);
			while(filePath != null)
			{
				File assetDirectory = new File(filePath);
				File[] files = assetDirectory.listFiles(new FilenameFilterImpl(digitalAssetId.toString())); 	
				for(int j=0; j<files.length; j++)
				{
					File file = files[j];
					logger.info("Deleting file " + file.getPath());
					file.delete();
				}
				i++;
				filePath = CmsPropertyHandler.getProperty("digitalAssetPath." + i);
			}

			//File assetDirectory = new File(CmsPropertyHandler.getDigitalAssetPath());
			//File[] files = assetDirectory.listFiles(new FilenameFilterImpl(digitalAssetId.toString())); 	
			//for(int i=0; i<files.length; i++)
			//{
			//	File file = files[i];
			//	logger.info("Deleting file " + file.getPath());
			//	file.delete();
			//}
	
		}
		catch(Exception e)
		{
			logger.error("Could not delete the assets for the digitalAsset " + digitalAssetId + ":" + e.getMessage(), e);
		}
	}
	
	/**
	 * This method unzips a zip-file recursively.
	 */
	
	private void unzipFile(File assetFile, File targetFolder) throws Exception
	{
		logger.info("Unzipping file " + assetFile.getPath() + " to " + targetFolder);
		Enumeration entries;
    	ZipFile zipFile = new ZipFile(assetFile);
      	entries = zipFile.entries();

      	while(entries.hasMoreElements()) 
      	{
        	ZipEntry entry = (ZipEntry)entries.nextElement();

	        if(entry.isDirectory()) 
	        {
	        	// Assume directories are stored parents first then children.
	          	//System.err.println("Extracting directory: " + targetFolder + File.separator + entry.getName());
	          	// This is not robust, just for demonstration purposes.
	          	(new File(targetFolder + File.separator + entry.getName())).mkdirs();
	          	continue;
	        }
	
	        //System.err.println("Extracting file: " + targetFolder + File.separator + entry.getName());
	        copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(targetFolder + File.separator + entry.getName())));
	    }
	
	    zipFile.close();
	}

	private Vector getZipFileEntries(File assetFile, File targetFolder) throws Exception
	{
		logger.info("Getting entries from " + assetFile.getPath());
		Enumeration entries;
		Vector entryCopies = new Vector();
		ZipFile zipFile = new ZipFile(assetFile);
		entries = zipFile.entries();

		while(entries.hasMoreElements()) 
		{
			ZipEntry entry = (ZipEntry)entries.nextElement();
			ZipEntry entryCopy = (ZipEntry) entry.clone();
			
			entryCopies.add(entryCopy);		
		}
	
		zipFile.close();
		return entryCopies;
	}


	/**
	 * Just copies the files...
	 */
	
	private void copyInputStream(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024];
    	int len;

    	while((len = in.read(buffer)) >= 0)
      		out.write(buffer, 0, len);

    	in.close();
    	out.close();
  	}


}