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

package org.infoglue.cms.applications.contenttool.actions;

import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.entities.content.*;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;

import webwork.action.ActionContext;
import webwork.multipart.MultiPartRequestWrapper;

import java.util.Enumeration;
import java.io.*;


/**
  * This is the action-class for UpdateDigitalAssetVersion
  * 
  * @author Mattias Bogeblad
  */

public class UpdateDigitalAssetAction extends ViewDigitalAssetAction 
{
	private Integer contentVersionId = null;
	private Integer digitalAssetId   = null;
	private String digitalAssetKey   = null;
	private boolean isUpdated       = false;
	
	private ConstraintExceptionBuffer ceb;
	
    public UpdateDigitalAssetAction()
    {
    	//this.digitalAssetVO = new DigitalAssetVO();
    	this.ceb = new ConstraintExceptionBuffer();
    }
        	
    public void setDigitalAssetKey(String digitalAssetKey)
	{
		this.digitalAssetKey = digitalAssetKey;
	}
		   
    public String doExecute() throws Exception
    {
    	ceb.throwIfNotEmpty();
		
    	InputStream is = null;
		File file = null;
		
    	try 
    	{
           	DigitalAssetVO digitalAssetVO = DigitalAssetController.getDigitalAssetVOWithId(this.digitalAssetId);
			digitalAssetVO.setAssetKey(this.digitalAssetKey);

    		MultiPartRequestWrapper mpr = ActionContext.getContext().getMultiPartRequest();
    		if(mpr != null)
    		{ 
	    		Enumeration names = mpr.getFileNames();
	         	while (names.hasMoreElements()) 
	         	{
	            	String name = (String)names.nextElement();
						            	
	            	file = mpr.getFile(name);
	            	if(file != null)
	            	{
		            	String contentType    = mpr.getContentType(name);
						String fileSystemName = mpr.getFilesystemName(name);
					
						String fileName = fileSystemName;
						String tempFileName = "tmp_" + System.currentTimeMillis() + "_" + fileName;
						//String filePath = file.getParentFile().getPath();
		            	String filePath = CmsPropertyHandler.getProperty("digitalAssetPath");
		            	fileSystemName =  filePath + File.separator + tempFileName;
		            	
		            	/*
		            	file = new File(tempFileName);
		            	boolean isRenamed = file.renameTo(renamedFile);
		            	*/
		            	
		            	digitalAssetVO.setAssetContentType(contentType);
						digitalAssetVO.setAssetFileName(fileName);
						digitalAssetVO.setAssetFilePath(filePath);
						digitalAssetVO.setAssetFileSize(new Integer(new Long(file.length()).intValue()));
						is = new FileInputStream(file);    	
	            	}
	         	}
    		}
    		else
    		{
    			CmsLogger.logSevere("File upload failed for some reason.");
    		}
    		
       		DigitalAssetController.update(digitalAssetVO, is);
			isUpdated = true;

      	} 
      	catch (Exception e) 
      	{
      		CmsLogger.logSevere("An error occurred when we tried to upload a new asset:" + e.getMessage(), e);
      	}
		finally
		{
			try
			{
				is.close();
				file.delete();
			}
			catch(Exception e){}
		}
					    
        return "success";
    }    

	public Integer getDigitalAssetId()
	{
		return digitalAssetId;
	}

	public void setDigitalAssetId(Integer digitalAssetId)
	{
		this.digitalAssetId = digitalAssetId;
	}

	public String getDigitalAssetKey()
	{
		return digitalAssetKey;
	}

	public boolean getIsUpdated()
	{
		return isUpdated;
	}

	public Integer getContentVersionId()
	{
		return contentVersionId;
	}

	public void setContentVersionId(Integer contentVersionId)
	{
		this.contentVersionId = contentVersionId;
	}

}
