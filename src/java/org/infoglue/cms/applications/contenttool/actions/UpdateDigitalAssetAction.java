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

import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.databeans.AssetKeyDefinition;
import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.entities.content.*;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;

import webwork.action.ActionContext;
import webwork.multipart.MultiPartRequestWrapper;

import java.util.Enumeration;
import java.awt.Image;
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
	private String reasonKey;
	private ContentTypeDefinitionVO contentTypeDefinitionVO;
	private DigitalAssetVO digitalAssetVO = null;
	private DigitalAssetVO updatedDigitalAssetVO = null;
	private String closeOnLoad;
	private Integer contentTypeDefinitionId;
	
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
            MultiPartRequestWrapper mpr = ActionContext.getMultiPartRequest();
            if(mpr == null)
            {
                this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnSizeText";
                return "uploadFailed";
            }
            
           	DigitalAssetVO digitalAssetVO = DigitalAssetController.getDigitalAssetVOWithId(this.digitalAssetId);
			digitalAssetVO.setAssetKey(this.digitalAssetKey);

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
						fileName = new VisualFormatter().replaceNonAscii(fileName, '_');

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
						
						this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithId(this.contentTypeDefinitionId);
						AssetKeyDefinition assetKeyDefinition = ContentTypeDefinitionController.getController().getDefinedAssetKey(contentTypeDefinitionVO.getSchemaValue(), digitalAssetKey);
						
						if(assetKeyDefinition.getMaximumSize().intValue() < new Long(file.length()).intValue())
						{   
						    this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnSizeText";
		                	return "uploadFailed";
						}
						if(assetKeyDefinition.getAllowedContentTypes().startsWith("image"))
						{
						    if(!contentType.startsWith("image"))
						    {
							    file.delete();
							    this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnTypeNotImageText";
			                	return "uploadFailed";						        
						    }

						    Image image = javax.imageio.ImageIO.read(file);
						    int width = image.getWidth(null);
						    int height = image.getHeight(null);
						    
						    String allowedWidth = assetKeyDefinition.getImageWidth();
						    String allowedHeight = assetKeyDefinition.getImageHeight();
						    
						    if(!allowedWidth.equals("*"))
						    {
						        Integer allowedWidthNumber = new Integer(allowedWidth.substring(1));
						        if(allowedWidth.startsWith("<") && width >= allowedWidthNumber.intValue())
						        {
							        file.delete();
								    this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnImageToWideText";
				                	return "uploadFailed";			
						        }
						        if(allowedWidth.startsWith(">") && width <= allowedWidthNumber.intValue())
						        {
							        file.delete();
								    this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnImageNotWideEnoughText";
				                	return "uploadFailed";			
						        }
						        if(!allowedWidth.startsWith(">") && !allowedWidth.startsWith("<") && width != new Integer(allowedWidth).intValue())
						        {
						            file.delete();
								    this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnImageWrongWidthText";
				                	return "uploadFailed";	
						        }
						    }
						    
						    if(!allowedHeight.equals("*"))
						    {
						        Integer allowedHeightNumber = new Integer(allowedHeight.substring(1));
						        if(allowedHeight.startsWith("<") && height >= allowedHeightNumber.intValue())
						        {
							        file.delete();
								    this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnImageToHighText";
				                	return "uploadFailed";			
						        }
						        if(allowedHeight.startsWith(">") && height <= allowedHeightNumber.intValue())
						        {
							        file.delete();
								    this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnImageNotHighEnoughText";
				                	return "uploadFailed";			
						        }
						        if(!allowedHeight.startsWith(">") && !allowedHeight.startsWith("<") && height != new Integer(allowedHeight).intValue())
						        {
						            file.delete();
								    this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnImageWrongHeightText";
				                	return "uploadFailed";	
						        }
						    }
						}

	            	}
	         	}
    		}
    		else
    		{
    			CmsLogger.logSevere("File upload failed for some reason.");
    		}
    		
    		updatedDigitalAssetVO = DigitalAssetController.update(digitalAssetVO, is);
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

	/**
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetUrl() throws Exception
	{
		String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetUrl(updatedDigitalAssetVO.getDigitalAssetId());
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("We could not get the url of the digitalAsset: " + e.getMessage(), e);
		}
		
		return imageHref;
	}
	
    public String getAssetThumbnailUrl()
    {
        String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetThumbnailUrl(updatedDigitalAssetVO.getDigitalAssetId());
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("We could not get the url of the thumbnail: " + e.getMessage(), e);
		}
		
		return imageHref;
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

    public String getReasonKey()
    {
        return reasonKey;
    }
    
    public String getCloseOnLoad()
    {
        return closeOnLoad;
    }
    
    public void setCloseOnLoad(String closeOnLoad)
    {
        this.closeOnLoad = closeOnLoad;
    }
    
    public void setContentTypeDefinitionId(Integer contentTypeDefinitionId)
    {
        this.contentTypeDefinitionId = contentTypeDefinitionId;
    }
}
