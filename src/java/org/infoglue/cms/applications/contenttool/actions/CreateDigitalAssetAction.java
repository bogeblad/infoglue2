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

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;

import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.databeans.AssetKeyDefinition;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.GroupPropertiesController;
import org.infoglue.cms.controllers.kernel.impl.simple.RolePropertiesController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserPropertiesController;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.content.DigitalAssetVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.GroupProperties;
import org.infoglue.cms.entities.management.GroupPropertiesVO;
import org.infoglue.cms.entities.management.RoleProperties;
import org.infoglue.cms.entities.management.RolePropertiesVO;
import org.infoglue.cms.entities.management.UserProperties;
import org.infoglue.cms.entities.management.UserPropertiesVO;
import org.infoglue.cms.util.CmsPropertyHandler;

import webwork.action.ActionContext;
import webwork.multipart.MultiPartRequestWrapper;


public class CreateDigitalAssetAction extends ViewDigitalAssetAction
{
	private static final long serialVersionUID = 1L;
	
	private String entity;
	private Integer entityId;

	private Integer contentVersionId = null;
	private String digitalAssetKey   = "";
	private Integer uploadedFilesCounter = new Integer(0);
	private ContentVersionVO contentVersionVO;
	private ContentTypeDefinitionVO contentTypeDefinitionVO;
	private String reasonKey;
	private DigitalAssetVO digitalAssetVO = null;
	private String closeOnLoad;
	private Integer contentTypeDefinitionId;

    public CreateDigitalAssetAction()
    {
    }
        
    public void setContentVersionId(Integer contentVersionId)
	{
		this.contentVersionId = contentVersionId;
	}    
     
    public Integer getContentVersionId()
	{
		return this.contentVersionId;
	}
	
    public void setDigitalAssetKey(String digitalAssetKey)
	{
		this.digitalAssetKey = digitalAssetKey;
	}

    public String getDigitalAssetKey()
    {
        return digitalAssetKey;
    }

	public void setUploadedFilesCounter(Integer uploadedFilesCounter)
	{
		this.uploadedFilesCounter = uploadedFilesCounter;
	}

	public Integer getUploadedFilesCounter()
	{
		return this.uploadedFilesCounter;
	}
	   
	public List getDefinedAssetKeys()
	{
		return ContentTypeDefinitionController.getController().getDefinedAssetKeys(this.contentTypeDefinitionVO.getSchemaValue());
	}
	
    public String doExecute() //throws Exception
    {
        try
        {
            MultiPartRequestWrapper mpr = ActionContext.getMultiPartRequest();
            if(mpr == null)
            {
                this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnSizeText";
                return "uploadFailed";
            }
            
	        if(this.contentVersionId != null)
	        {
		    	this.contentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(this.contentVersionId);
		        this.contentTypeDefinitionVO = ContentController.getContentController().getContentTypeDefinition(contentVersionVO.getContentId());
	        }
	        else
	        {
	            if(this.entity.equalsIgnoreCase(UserProperties.class.getName()))
	            {
	                UserPropertiesVO userPropertiesVO = UserPropertiesController.getController().getUserPropertiesVOWithId(this.entityId);
	                this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithId(userPropertiesVO.getContentTypeDefinitionId());            
	            }
	            else if(this.entity.equalsIgnoreCase(RoleProperties.class.getName()))
	            {
	                RolePropertiesVO rolePropertiesVO = RolePropertiesController.getController().getRolePropertiesVOWithId(this.entityId);
	                this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithId(rolePropertiesVO.getContentTypeDefinitionId());            
	            }
	            else if(this.entity.equalsIgnoreCase(GroupProperties.class.getName()))
	            {
	                GroupPropertiesVO groupPropertiesVO = GroupPropertiesController.getController().getGroupPropertiesVOWithId(this.entityId);
	                this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithId(groupPropertiesVO.getContentTypeDefinitionId());            
	            }
	        }
	        
	
	    	InputStream is = null;
			//File renamedFile = null;
			File file = null;
			
	    	try 
	    	{
	    		if(mpr != null)
	    		{ 
		    		Enumeration names = mpr.getFileNames();
		         	while (names.hasMoreElements()) 
		         	{
		            	String name 		  = (String)names.nextElement();
						String contentType    = mpr.getContentType(name);
						String fileSystemName = mpr.getFilesystemName(name);
						
		            	getLogger().info("digitalAssetKey:" + digitalAssetKey);
		            	getLogger().info("name:" + name);
		            	getLogger().info("contentType:" + contentType);
		            	getLogger().info("fileSystemName:" + fileSystemName);
		            	
		            	file = mpr.getFile(name);
		            	//String fileName = this.contentVersionId + "_" + System.currentTimeMillis() + "_" + fileSystemName;
						String fileName = fileSystemName;
						
						fileName = new VisualFormatter().replaceNonAscii(fileName, '_');
						
						String tempFileName = "tmp_" + System.currentTimeMillis() + "_" + fileName;
		            	//String filePath = file.getParentFile().getPath();
		            	String filePath = CmsPropertyHandler.getDigitalAssetPath();
		            	fileSystemName = filePath + File.separator + tempFileName;
		            	
						//getLogger().info("New fileSystemName:" + fileSystemName);
		            	//renamedFile = new File(fileSystemName);
						//boolean isRenamed = file.renameTo(renamedFile);
						//getLogger().info("isRenamed:" + isRenamed);
						
		            	DigitalAssetVO newAsset = new DigitalAssetVO();
						newAsset.setAssetContentType(contentType);
						newAsset.setAssetKey(digitalAssetKey);
						newAsset.setAssetFileName(fileName);
						newAsset.setAssetFilePath(filePath);
						newAsset.setAssetFileSize(new Integer(new Long(file.length()).intValue()));
						//is = new FileInputStream(renamedFile);
						is = new FileInputStream(file);
						
						String fileUploadMaximumSize = getPrincipalPropertyValue("fileUploadMaximumSize", false, true);
						getLogger().info("fileUploadMaximumSize in create:" + fileUploadMaximumSize);
						if(!fileUploadMaximumSize.equalsIgnoreCase("-1") && new Integer(fileUploadMaximumSize).intValue() < new Long(file.length()).intValue())
						{
						    file.delete();
						    this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnSizeText";
		                	return "uploadFailed";
						}
						
						if(this.contentTypeDefinitionId != null && digitalAssetKey != null)
						{
							this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithId(this.contentTypeDefinitionId);
							AssetKeyDefinition assetKeyDefinition = ContentTypeDefinitionController.getController().getDefinedAssetKey(contentTypeDefinitionVO.getSchemaValue(), digitalAssetKey);
							
							if(assetKeyDefinition != null)
							{
								if(assetKeyDefinition.getMaximumSize().intValue() < new Long(file.length()).intValue())
								{   
								    file.delete();
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
						
						if(this.contentVersionId != null)
						    digitalAssetVO = DigitalAssetController.create(newAsset, is, this.contentVersionId);
		         		else
		         		    digitalAssetVO = DigitalAssetController.create(newAsset, is, this.entity, this.entityId);
		         		    
						this.uploadedFilesCounter = new Integer(this.uploadedFilesCounter.intValue() + 1);
		         	}
	    		}
	    		else
	    		{
	    		    getLogger().error("File upload failed for some reason.");
	    		}
	      	} 
	      	catch (Throwable e) 
	      	{
	      	    getLogger().error("An error occurred when we tried to upload a new asset:" + e.getMessage(), e);
	      	}
			finally
			{
				try
				{
					is.close();
					file.delete();
				}
				catch(Throwable e)
				{ 
				    getLogger().error("An error occurred when we tried to close the fileinput stream and delete the file:" + e.getMessage(), e);
				}
			}
        }
        catch(Throwable e)
        { 
      	    getLogger().error("An error occurred when we tried to upload a new asset:" + e.getMessage(), e);
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
       		imageHref = DigitalAssetController.getDigitalAssetUrl(digitalAssetVO.getDigitalAssetId());
		}
		catch(Exception e)
		{
			getLogger().warn("We could not get the url of the digitalAsset: " + e.getMessage(), e);
		}
		
		return imageHref;
	}
	
    public String getAssetThumbnailUrl()
    {
        String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetThumbnailUrl(digitalAssetVO.getDigitalAssetId());
		}
		catch(Exception e)
		{
		    getLogger().warn("We could not get the url of the thumbnail: " + e.getMessage(), e);
		}
		
		return imageHref;
    }
    

    public String getEntity()
    {
        return entity;
    }
    
    public void setEntity(String entity)
    {
        this.entity = entity;
    }
    
    public Integer getEntityId()
    {
        return entityId;
    }
    
    public void setEntityId(Integer entityId)
    {
        this.entityId = entityId;
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
    
    public Integer getContentTypeDefinitionId()
    {
        return contentTypeDefinitionId;
    }
    
    public void setContentTypeDefinitionId(Integer contentTypeDefinitionId)
    {
        this.contentTypeDefinitionId = contentTypeDefinitionId;
    }
    
    public ContentTypeDefinitionVO getContentTypeDefinitionVO()
    {
        return contentTypeDefinitionVO;
    }
}