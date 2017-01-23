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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.databeans.AssetKeyDefinition;
import org.infoglue.cms.applications.databeans.SessionInfoBean;
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
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.CmsSessionContextListener;
import org.infoglue.cms.util.dom.DOMBuilder;

import com.mullassery.imaging.Imaging;
import com.mullassery.imaging.ImagingFactory;

import webwork.action.ActionContext;
import webwork.multipart.MultiPartRequestWrapper;


public class CreateDigitalAssetAction extends ViewDigitalAssetAction
{
    private final static Logger logger = Logger.getLogger(CreateDigitalAssetAction.class.getName());

	private static final long serialVersionUID = 1L;
	
	private String entity;
	private Integer entityId;

	private Integer contentVersionId = null;
	private String digitalAssetKey   = "";
	private Integer uploadedFilesCounter = new Integer(0);
	private ContentVersionVO contentVersionVO;
	private ContentTypeDefinitionVO contentTypeDefinitionVO;
	private String reasonKey;
	private String uploadMaxSize;
	private DigitalAssetVO digitalAssetVO = null;
	private String closeOnLoad;
	private Integer contentTypeDefinitionId;
	private String returnAddress = "";
	private InfoGluePrincipal principal = null;
	private boolean useFileNameAsContentTypeBase = false;
	private boolean useFckUploadMessages = false;
	
	private VisualFormatter formatter = new VisualFormatter();
	private Imaging imaging = ImagingFactory.createImagingInstance(ImagingFactory.AWT_LOADER, ImagingFactory.JAVA2D_TRANSFORMER);
	
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
	   
	public List<AssetKeyDefinition> getDefinedAssetKeys()
	{
		return ContentTypeDefinitionController.getController().getDefinedAssetKeys(this.contentTypeDefinitionVO.getSchemaValue());
	}
	
    public String doMultiple() throws Exception //throws Exception
    {
    	logger.info("Uploading file....");
    	this.principal = getInfoGluePrincipal();
    	
		logger.info("QueryString:" + this.getRequest().getQueryString());
		String requestSessionId = this.getRequest().getParameter("JSESSIONID");
		logger.info("JSESSIONID:" + requestSessionId);
		boolean allowedSessionId = false;
		List<SessionInfoBean> activeSessionBeanList = CmsSessionContextListener.getSessionInfoBeanList();
		Iterator<SessionInfoBean> activeSessionsIterator = activeSessionBeanList.iterator();
		logger.info("activeSessionBeanList:" + activeSessionBeanList.size());
		while(activeSessionsIterator.hasNext())
		{
			SessionInfoBean sessionBean = (SessionInfoBean)activeSessionsIterator.next();
			logger.info("sessionBean:" + sessionBean.getId() + "=" + sessionBean.getPrincipal().getName());
			if(sessionBean.getId().equals(requestSessionId))
			{
				logger.info("Found a matching sessionId");
				allowedSessionId = true;
		    	this.principal = sessionBean.getPrincipal();

				break;
			}
		}
		
		if(!allowedSessionId)
		{
			return "uploadFailed";
		}

		useFileNameAsContentTypeBase = true;

		String result = doExecute();

		if(result.equals("success"))
		{
			String assetUrl = getDigitalAssetUrl();
			logger.info("assetUrl:" + assetUrl);
			String assetThumbnailUrl = getAssetThumbnailUrl();
			logger.info("assetThumbnailUrl:" + assetThumbnailUrl);
			this.getResponse().setContentType("text/plain");
			this.getResponse().getWriter().println(assetThumbnailUrl + ":" + this.digitalAssetKey);
			return NONE;
		}
		else
		{
			this.getResponse().setContentType("text/plain");
			this.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			this.getResponse().getWriter().println("Error uploading to " + this.digitalAssetKey);
			return NONE;
		}
	}

	private String getContentTypeFromFileName(String fileSystemName)
	{
		String contentType = null;
		if (fileSystemName.lastIndexOf(".") > -1)
		{
			String extension = fileSystemName.substring(fileSystemName.lastIndexOf(".") + 1);
			logger.info("extension:" + extension);

			if(extension.equalsIgnoreCase("gif"))
				contentType = "image/gif";
			else if(extension.equalsIgnoreCase("jpg"))
				contentType = "image/jpg";
			else if(extension.equalsIgnoreCase("png"))
				contentType = "image/png";

			else if(extension.equalsIgnoreCase("pdf"))
				contentType = "application/pdf";
			else if(extension.equalsIgnoreCase("doc"))
				contentType = "application/msword";
			else if(extension.equalsIgnoreCase("xls"))
				contentType = "application/vnd.ms-excel";
			else if(extension.equalsIgnoreCase("ppt"))
				contentType = "application/vnd.ms-powerpoint";
			else if(extension.equalsIgnoreCase("zip"))
				contentType = "application/zip";
			else if(extension.equalsIgnoreCase("xml"))
				contentType = "text/xml";
		}
		return contentType;
	}
	
	private String getAssetKeyFromFileName(String fileName)
	{
		String value;
		if (fileName.lastIndexOf(".") > -1)
		{
			value = fileName.substring(0, fileName.lastIndexOf("."));
		}
		else
		{
			value = fileName;
		}

		return formatter.replaceNiceURINonAsciiWithSpecifiedChars(
				value,
				CmsPropertyHandler.getNiceURIDefaultReplacementCharacter()
			);
	}

	public String doExecute() throws IOException
	{
		logger.info("Uploading file....");
		if(this.principal == null)
		{
			this.principal = getInfoGluePrincipal();
		}

		InputStream is = null;
		File file = null;

		try
		{
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

			String fromEncoding = CmsPropertyHandler.getUploadFromEncoding();
			if(fromEncoding == null)
				fromEncoding = "iso-8859-1";

			String toEncoding = CmsPropertyHandler.getUploadToEncoding();
			if(toEncoding == null)
				toEncoding = "utf-8";

			digitalAssetKey = new String(digitalAssetKey.getBytes(fromEncoding), toEncoding);

			MultiPartRequestWrapper mpr = ActionContext.getMultiPartRequest();
			if(mpr == null)
			{
				this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnSizeText";
				int maxSize = DigitalAssetController.getController().getAssetMaxFileSize(this.principal, contentTypeDefinitionVO, digitalAssetKey);
				this.uploadMaxSize = "(Max " + formatter.formatFileSize(maxSize) + ")";
				return "uploadFailed";
			}

			@SuppressWarnings("unchecked")
			Enumeration<String> names = mpr.getFileNames();
			while (names.hasMoreElements())
			{
				String name = names.nextElement();
				file = mpr.getFile(name);
				if (file != null)
				{
					String contentType = mpr.getContentType(name);
					String fileSystemName = mpr.getFilesystemName(name);

					logger.info("fileSystemName:" + fileSystemName);
					if(digitalAssetKey == null || digitalAssetKey.equals(""))
					{
						digitalAssetKey = getAssetKeyFromFileName(fileSystemName);
					}
					logger.info("digitalAssetKey:" + digitalAssetKey);
					logger.info("name:" + name);
					logger.info("contentType:" + contentType);
					logger.info("digitalAssetKey:" + digitalAssetKey);

					if (useFileNameAsContentTypeBase)
					{
						String newContentType = getContentTypeFromFileName(fileSystemName);
						if (newContentType != null)
						{
							contentType = newContentType;
						}
						logger.info("new contentType:" + contentType);
					}

					String fileName = fileSystemName;
					logger.info("fileSystemName:" + fileSystemName);
					fileName = formatter.replaceNiceURINonAsciiWithSpecifiedChars(fileName, CmsPropertyHandler.getNiceURIDefaultReplacementCharacter());

					List<String> errors =
							DigitalAssetController.getController().validateUploadeFile(
								file,
								contentType,
								contentVersionId,
								contentTypeDefinitionId,
								digitalAssetKey,
								getInfoGluePrincipal()
							);

					if (!errors.isEmpty())
					{
						this.getResponse().setContentType("text/html; charset=UTF-8");
						this.getResponse().setHeader("sendIGError", "true");
						file.delete();
						this.reasonKey = errors.get(0);
						int maxSize = DigitalAssetController.getController().getAssetMaxFileSize(this.principal, contentTypeDefinitionVO, digitalAssetKey);
						this.uploadMaxSize = "(Max " + formatter.formatFileSize(maxSize) + ")";
						return "uploadFailed";
					}

					String filePath = CmsPropertyHandler.getDigitalAssetPath();

					DigitalAssetVO newAsset = new DigitalAssetVO();
					newAsset.setAssetContentType(contentType);
					newAsset.setAssetKey(digitalAssetKey);
					newAsset.setAssetFileName(fileName);
					newAsset.setAssetFilePath(filePath);
					newAsset.setAssetFileSize(new Integer(new Long(file.length()).intValue()));
					if(CmsPropertyHandler.getEnableDiskAssets().equals("false"))
					{
						is = new FileInputStream(file);
					}

					boolean keepOriginal = true;
					if (this.contentVersionId != null)
					{
						AssetKeyDefinition assetKeyDefinition = ContentTypeDefinitionController.getController().getDefinedAssetKey(contentTypeDefinitionVO.getSchemaValue(), digitalAssetKey);
						keepOriginal = handleTransformations(newAsset, file, contentType, assetKeyDefinition);
						if(keepOriginal)
						{
							List<Integer> newContentVersionIdList = new ArrayList<Integer>();
							digitalAssetVO = DigitalAssetController.create(newAsset, is, this.contentVersionId, this.getInfoGluePrincipal(), newContentVersionIdList);
							if(newContentVersionIdList.size() > 0)
							{
								Integer newContentVersionId = newContentVersionIdList.get(0);
								setContentVersionId(newContentVersionId);
							}
						}
					}
					else
					{
						digitalAssetVO = DigitalAssetController.create(newAsset, is, this.entity, this.entityId);
					}

					if(CmsPropertyHandler.getEnableDiskAssets().equals("true"))
					{
						if(keepOriginal)
						{
							String folderName = "" + (digitalAssetVO.getDigitalAssetId().intValue() / 1000);
							String assetFileName = "" + digitalAssetVO.getAssetFilePath() + File.separator + folderName + File.separator + digitalAssetVO.getId() + "_" + digitalAssetVO.getAssetFileName();
							File assetFile = new File(assetFileName);
							file.renameTo(assetFile);
						}
					}

					this.uploadedFilesCounter = new Integer(this.uploadedFilesCounter.intValue() + 1);
				}
			}
		}
		catch (Throwable ex)
		{
			logger.error("An error occurred when we tried to upload a new asset. Message: " + ex.getMessage());
			logger.warn("An error occurred when we tried to upload a new asset.", ex);
		}
		finally
		{
			try
			{
				if(CmsPropertyHandler.getEnableDiskAssets().equals("false"))
				{
					if (is != null)
					{
						is.close();
					}
					file.delete();
				}
			}
			catch(Throwable e)
			{
				logger.error("An error occurred when we tried to close the fileinput stream and delete the file:" + e.getMessage(), e);
			}
		}

		if (returnAddress != null && !returnAddress.equals(""))
		{
			Integer oldContentVersionId = this.getContentVersionId();

			if (!oldContentVersionId.equals(contentVersionVO.getId()) && returnAddress.indexOf("contentVersionId") > -1)
			{
				int index = returnAddress.indexOf("contentVersionId=");
				int endIndex = returnAddress.indexOf("&", index);
				if(index > 0 && endIndex > index)
					returnAddress = returnAddress.substring(0, index) + "contentVersionId=" + contentVersionVO.getId() + returnAddress.substring(endIndex);
				else if(index > 0)
					returnAddress = returnAddress.substring(0, index) + "contentVersionId=" + contentVersionVO.getId();
			}

			this.getResponse().sendRedirect(returnAddress);
			return NONE;
		}

		return "success";
	}

	private boolean handleTransformations(DigitalAssetVO originalAssetVO, File file, String contentType, AssetKeyDefinition assetKeyDefinition)
	{
		boolean keepOriginal = true;
		try
		{
			String transformationsXML = null;
			if(assetKeyDefinition != null)
				transformationsXML = assetKeyDefinition.getAssetUploadTransformationsSettings();

			if(transformationsXML == null || transformationsXML.equals(""))
				transformationsXML = CmsPropertyHandler.getAssetUploadTransformationsSettings();

			if(transformationsXML == null || transformationsXML.equals("") || transformationsXML.equals("none"))
				return keepOriginal;
			
			DOMBuilder domBuilder = new DOMBuilder();
			Document document = domBuilder.getDocument(transformationsXML);
		    Element rootElement = document.getRootElement();
		    
			String transformationXPath = "//transformation";
			@SuppressWarnings("unchecked")
			List<Node> transformationElements = rootElement.selectNodes(transformationXPath);
			logger.info("transformationElements:" + transformationElements.size());
			
			/*
			<transformations>
			  <transformation inputFilePattern=".*(jpeg|jpg|gif|png).*" keepOriginal="false">
			    <tranformResult type="scaleImage" width="100" height="100" outputFormat="png" assetSuffix="medium"/>
			    <tranformResult type="scaleImage" width="50" height="50" outputFormat="jpg" assetSuffix="small"/>
			  </transformation>
			</transformations>
			*/
			
			Iterator<Node> transformationElementsIterator = transformationElements.iterator();
			while (transformationElementsIterator.hasNext())
			{
				final Node node = transformationElementsIterator.next();
				if (!(node instanceof Element))
				{
					continue;
				}
				final Element transformationElement = (Element)node;
			
				String assetKeyPattern  = transformationElement.attributeValue("assetKeyPattern");
				String inputFilePattern  = transformationElement.attributeValue("inputFilePattern");
				logger.info("inputFilePattern: " + inputFilePattern);
				
				boolean assetKeyMatch = false;
				if(assetKeyPattern == null || assetKeyPattern.equals(""))
					assetKeyMatch = true;
				else if(originalAssetVO.getAssetKey().matches(assetKeyPattern))
					assetKeyMatch = true;
										
				if(assetKeyMatch && contentType.matches(inputFilePattern))
				{
					logger.info("We got a match on contentType:" + contentType + " : " + inputFilePattern);

					String keepOriginalAsset = transformationElement.attributeValue("keepOriginal");
					if(keepOriginalAsset != null && keepOriginalAsset.equalsIgnoreCase("false"))
						keepOriginal = false;
					
					logger.info("keepOriginal:" + keepOriginal);

					@SuppressWarnings("unchecked")
					List<Element> tranformResultElements = transformationElement.elements("tranformResult");
					Iterator<Element> tranformResultElementsIterator = tranformResultElements.iterator();
					while(tranformResultElementsIterator.hasNext())
					{
						Element tranformResultElement = tranformResultElementsIterator.next();
					
						String type 		= tranformResultElement.attributeValue("type");
						String width 		= tranformResultElement.attributeValue("width");
						String height 		= tranformResultElement.attributeValue("height");
						String outputFormat	= tranformResultElement.attributeValue("outputFormat");
						String assetSuffix 	= tranformResultElement.attributeValue("assetSuffix");
	
						logger.info("type: " + type);
						logger.info("width: " + width);
						logger.info("height: " + height);
						logger.info("outputFormat: " + outputFormat);
						logger.info("assetSuffix: " + assetSuffix);
						
						if(type.equalsIgnoreCase("scaleImage"))
							scaleAndSaveImage(originalAssetVO, file, Integer.parseInt(width), Integer.parseInt(height), outputFormat, assetSuffix);
					}
				}
				else
				{
					logger.info("NOOOO match on contentType:" + contentType + " : " + inputFilePattern);
				}
			}
		}
		catch (Exception e) 
		{
			logger.error("Error transforming asset:" + e.getMessage());
		}
		
		return keepOriginal;
	}

	private void scaleAndSaveImage(DigitalAssetVO originalAssetVO, File file, int width, int height, String outputFormat, String assetSuffix) throws Exception
	{
		logger.info("Scaling image to new format:" + originalAssetVO + ":" + outputFormat);
    	BufferedImage original = javax.imageio.ImageIO.read(file);
    	
    	int originalWidth = original.getWidth();
    	int originalHeight = original.getHeight();
    	float aspect = (float)originalWidth / (float)originalHeight;
    	BufferedImage image = null;
    	
    	if(height == -1 && width != -1)
    		aspect = (float)width / (float)originalWidth;
    	else if(width == -1 && height != -1)
    		aspect = (float)height / (float)originalHeight;
    	else
    		aspect = (float)width / (float)originalWidth;
    	
    	logger.info("aspect:" + aspect);
    	
    	image = imaging.scale(original, aspect);        	
	
    	String workingFileName = "" + originalAssetVO.getDigitalAssetId() + "_" + assetSuffix + "." + outputFormat.toLowerCase();
    	long timeStamp = System.currentTimeMillis();
    	if(originalAssetVO.getDigitalAssetId() == null)
    		workingFileName = "" + timeStamp + "_" + assetSuffix + "." + outputFormat.toLowerCase();
    	
    	File outputFile = new File(CmsPropertyHandler.getDigitalAssetPath() + File.separator + File.separator + workingFileName);
		javax.imageio.ImageIO.write(image, outputFormat, outputFile);
		
		String assetContentType = "image/png";
		if(outputFormat.equalsIgnoreCase("gif"))
			assetContentType = "image/gif";
		if(outputFormat.equalsIgnoreCase("jpg"))
			assetContentType = "image/jpeg";
		
		DigitalAssetVO digitalAssetVO = new DigitalAssetVO();
		digitalAssetVO.setAssetContentType(assetContentType);
		digitalAssetVO.setAssetFileName(outputFile.getName());
		digitalAssetVO.setAssetFilePath(outputFile.getPath());
		digitalAssetVO.setAssetFileSize(new Integer((int)outputFile.length()));
		digitalAssetVO.setAssetKey(originalAssetVO.getAssetKey() + "_" + assetSuffix);
		
		InputStream is = new FileInputStream(outputFile);
		
    	List<Integer> newContentVersionIdList = new ArrayList<Integer>();
    	this.digitalAssetVO = DigitalAssetController.create(digitalAssetVO, is, this.contentVersionId, this.getInfoGluePrincipal(), newContentVersionIdList);
    	if(newContentVersionIdList.size() > 0)
    	{
    		Integer newContentVersionId = newContentVersionIdList.get(0);
    		setContentVersionId(newContentVersionId);
    	}

    	is.close();
		
		//logger.info("this.digitalAssetVO in scale:" + this.digitalAssetVO.getId());
    	String folderName = "" + (this.digitalAssetVO.getId().intValue() / 1000);
		String newWorkingFileName = "" + this.digitalAssetVO.getId() + "_" + timeStamp + "_" + assetSuffix + "." + outputFormat.toLowerCase();
    	File finalOutputFile = new File(CmsPropertyHandler.getDigitalAssetPath() + File.separator + folderName + File.separator + newWorkingFileName);
    	outputFile.renameTo(finalOutputFile);
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
			logger.warn("We could not get the url of the digitalAsset: " + e.getMessage(), e);
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
		    logger.warn("We could not get the url of the thumbnail: " + e.getMessage(), e);
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
    
    public void setReturnAddress(String returnAddress) 
	{
		this.returnAddress = returnAddress;
	}

	public String getUploadErrorMaxSize()
	{
		return uploadMaxSize;
	}
	
	public boolean getUseFckUploadMessages()
	{
		return useFckUploadMessages;
	}

	public void setUseFckUploadMessages(boolean useFckUploadMessages)
	{
		this.useFckUploadMessages = useFckUploadMessages;
	}

	public ContentVersionVO getContentVersionVO()
	{
		return contentVersionVO;
	}

}