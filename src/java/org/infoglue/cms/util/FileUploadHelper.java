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

package org.infoglue.cms.util;

import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;

import webwork.multipart.MultiPartRequestWrapper;

import java.util.Enumeration;
import java.io.*;


/**
  * This is the action-class for UpdateDigitalAssetVersion
  * 
  * @author Mattias Bogeblad
  */

public class FileUploadHelper
{
	
	public File getUploadedFile(MultiPartRequestWrapper mpr)
	{
		File renamedFile = null;
		
		try 
		{
			if(mpr != null)
			{ 
				Enumeration names = mpr.getFileNames();
				while (names.hasMoreElements()) 
				{
					String name = (String)names.nextElement();
						            	
					File file = mpr.getFile(name);
					if(file != null)
					{
						String contentType    = mpr.getContentType(name);
						String fileSystemName = mpr.getFilesystemName(name);
						
						String fileName = "Import_" + System.currentTimeMillis() + fileSystemName;
						fileName = new VisualFormatter().replaceNonAscii(fileName, '_');
						
						String filePath = CmsPropertyHandler.getProperty("digitalAssetPath");
						fileSystemName =  filePath + File.separator + fileName;
		            	
						renamedFile = new File(fileSystemName);
						boolean isRenamed = file.renameTo(renamedFile);
					}
				}
			}
			else
			{
				CmsLogger.logSevere("File upload failed for some reason.");
			}
 
		} 
		catch (Exception e) 
		{
			CmsLogger.logSevere("An error occurred when we get and rename an uploaded file:" + e.getMessage(), e);
		}
		
		return renamedFile;
	}
	
}