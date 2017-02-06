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

package org.infoglue.cms.applications.contenttool.wizards.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.databeans.AssetKeyDefinition;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.content.DigitalAssetVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsPropertyHandler;

import webwork.action.ActionContext;
import webwork.multipart.MultiPartRequestWrapper;


public class CreateContentWizardInputAssetsAction extends CreateContentWizardAbstractAction
{
	private static final long serialVersionUID = -2738690853533045311L;

	private final static Logger logger = Logger.getLogger(CreateContentWizardInputAssetsAction.class.getName());

	private String mandatoryAssetKey						= null;
	private String mandatoryAssetMaximumSize				= null;
	private String digitalAssetKey							= "";
	private String currentDigitalAssetKey					= null;
	private Integer uploadedFilesCounter 					= new Integer(0);
	private ContentTypeDefinitionVO contentTypeDefinitionVO	= null;
	private Integer languageId 								= null;
	private Integer contentVersionId 						= null;
	private String inputMoreAssets							= null;
	private String reasonKey								= null;

	private VisualFormatter formatter = new VisualFormatter();

	public CreateContentWizardInputAssetsAction()
	{
	}

	private void setupCurrentDigitalAsset()
	{
		if (mandatoryAssetKey != null)
		{
			this.currentDigitalAssetKey = mandatoryAssetKey;
		}
		else if (digitalAssetKey == null || digitalAssetKey.equals(""))
		{
			if (getDefinedAssetKeys().size() > 0 && !getBlankAssetKeyAsDefault())
			{
				this.currentDigitalAssetKey = getDefinedAssetKeys().get(0).getAssetKey();
			}
		}
		else
		{
			this.currentDigitalAssetKey = digitalAssetKey;
		}
	}

	private String setupView() throws Exception
	{
		CreateContentWizardInfoBean createContentWizardInfoBean = this.getCreateContentWizardInfoBean();
		this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithId(createContentWizardInfoBean.getContentTypeDefinitionId());

		List<AssetKeyDefinition> assetKeys = ContentTypeDefinitionController.getController().getDefinedAssetKeys(this.contentTypeDefinitionVO.getSchemaValue());

		if(this.languageId == null)
		{
			this.languageId = createContentWizardInfoBean.getLanguageId();
			if(this.languageId == null)
			{
				LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(createContentWizardInfoBean.getRepositoryId());
				this.languageId = masterLanguageVO.getLanguageId();
			}
		}

		if (this.contentVersionId == null)
		{
			ContentVersionVO newContentVersion = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(createContentWizardInfoBean.getContentVO().getId(), languageId);
			this.contentVersionId = newContentVersion.getId();
		}

		boolean hasMandatoryAssets = false;
		boolean missingAsset = false;
		Iterator<AssetKeyDefinition> assetKeysIterator = assetKeys.iterator();
		while (assetKeysIterator.hasNext())
		{
			AssetKeyDefinition assetKeyDefinition = assetKeysIterator.next();
			if (assetKeyDefinition.getIsMandatory().booleanValue())
			{
				hasMandatoryAssets = true;
				DigitalAssetVO asset = DigitalAssetController.getDigitalAssetVO(createContentWizardInfoBean.getContentVO().getId(), languageId, assetKeyDefinition.getAssetKey(), false);
				if(asset == null)
				{
					mandatoryAssetKey = assetKeyDefinition.getAssetKey();
					mandatoryAssetMaximumSize = "" + assetKeyDefinition.getMaximumSize();
					missingAsset = true;
					break;
				}
			}
		}

		if (!hasMandatoryAssets && !inputMoreAssets.equalsIgnoreCase("false"))
		{
			inputMoreAssets = "true";
		}

		setupCurrentDigitalAsset();

		if(missingAsset)
		{
			inputMoreAssets = "false";
			return "input";
		}
		else
		{
			if(inputMoreAssets != null && inputMoreAssets.equalsIgnoreCase("true"))
			{
				return "input";
			}
			else
			{
				return "success";
			}
		}
	}

	public String doInput() throws Exception
	{
		try
		{
			final String result = setupView();
			return result;
		}
		catch (Throwable ex)
		{
			logger.warn("An error occured with content wizard bean. The bean will be discarded. Error type " + ex.getClass() + ". Message: " + ex.getMessage());
			invalidateCreateContentWizardInfoBean();
			throw new SystemException(ex);
		}
	}

	public String doExecute() throws Exception
	{
		InputStream is = null;
		File file = null;
		DigitalAssetVO digitalAssetVO = null;
		
		try 
		{
			MultiPartRequestWrapper mpr = ActionContext.getMultiPartRequest();
			if(mpr == null)
			{
				this.reasonKey = "tool.contenttool.fileUpload.fileUploadFailedOnSizeText";
				logger.info("File upload failed. Reason: " + reasonKey);
				setupView();
				return "input";
			}

			String fromEncoding = CmsPropertyHandler.getUploadFromEncoding();
			if(fromEncoding == null)
				fromEncoding = "iso-8859-1";

			String toEncoding = CmsPropertyHandler.getUploadToEncoding();
			if(toEncoding == null)
				toEncoding = "utf-8";

			this.digitalAssetKey = new String(this.digitalAssetKey.getBytes(fromEncoding), toEncoding);

			CreateContentWizardInfoBean createContentWizardInfoBean = this.getCreateContentWizardInfoBean();
			@SuppressWarnings("unchecked")
			Enumeration<String> names = mpr.getFileNames();
			while (names.hasMoreElements())
			{
				String name = names.nextElement();
				file = mpr.getFile(name);
				if (file != null)
				{
					String contentType		= mpr.getContentType(name);
					String fileSystemName	= mpr.getFilesystemName(name);
					String fileName = fileSystemName;
					fileName = formatter.replaceNiceURINonAsciiWithSpecifiedChars(fileName, CmsPropertyHandler.getNiceURIDefaultReplacementCharacter());

					List<String> errors =
							DigitalAssetController.getController().validateUploadeFile(
								file,
								contentType,
								contentVersionId,
								createContentWizardInfoBean.getContentTypeDefinitionId(),
								digitalAssetKey,
								getInfoGluePrincipal()
							);
					
					if (!errors.isEmpty())
					{
						this.getResponse().setContentType("text/html; charset=UTF-8");
						this.getResponse().setHeader("sendIGError", "true");
						file.delete();
						this.reasonKey = errors.get(0);
						setupView();
						return "input";
					}

					String filePath = CmsPropertyHandler.getDigitalAssetPath();

					DigitalAssetVO newAsset = new DigitalAssetVO();
					newAsset.setAssetContentType(contentType);
					newAsset.setAssetKey(digitalAssetKey);
					newAsset.setAssetFileName(fileName);
					newAsset.setAssetFilePath(filePath);
					newAsset.setAssetFileSize(new Integer(new Long(file.length()).intValue()));
					if (CmsPropertyHandler.getEnableDiskAssets().equals("false"))
					{
						is = new FileInputStream(file);
					}
					digitalAssetVO = DigitalAssetController.create(newAsset, is, this.contentVersionId, this.getInfoGluePrincipal());

					this.uploadedFilesCounter = new Integer(this.uploadedFilesCounter.intValue() + 1);
				}
			}
		}
		catch (Exception ex)
		{
			logger.error("An error occurred when we tried to upload a new asset. Message: " + ex.getMessage());
			logger.warn("An error occurred when we tried to upload a new asset.", ex);
		}
		finally
		{
			try
			{
				if (is != null)
				{
					is.close();
				}
				
				if (CmsPropertyHandler.getEnableDiskAssets().equals("true"))
				{
					String folderName = "" + (digitalAssetVO.getDigitalAssetId().intValue() / 1000);
					String assetFileName = "" + digitalAssetVO.getAssetFilePath() + File.separator + folderName + File.separator + digitalAssetVO.getId() + "_" + digitalAssetVO.getAssetFileName();
					File assetFile = new File(assetFileName);
					file.renameTo(assetFile);
				}
				else
				{
					file.delete();
				}
			}
			catch(Exception e){}
		}

		// Clear 'state' before showing input view again
		digitalAssetKey = null;
		return doInput();
	}

	public String doFinish() throws Exception
	{
		return "success";
	}
	
	public void setDigitalAssetKey(String digitalAssetKey)
	{
		this.digitalAssetKey = digitalAssetKey;
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

	public String getMandatoryAssetKey()
	{
		return mandatoryAssetKey;
	}

	public void setMandatoryAssetKey(String string)
	{
		mandatoryAssetKey = string;
	}

	public String getMandatoryAssetMaximumSize()
	{
		return mandatoryAssetMaximumSize;
	}

	public Integer getLanguageId()
	{
		return languageId;
	}

	public void setLanguageId(Integer integer)
	{
		languageId = integer;
	}

	public Integer getContentVersionId()
	{
		return contentVersionId;
	}

	public void setContentVersionId(Integer contentVersionId)
	{
		this.contentVersionId = contentVersionId;
	}

	public String getInputMoreAssets()
	{
		return inputMoreAssets;
	}

	public void setInputMoreAssets(String inputMoreAssets)
	{
		this.inputMoreAssets = inputMoreAssets;
	}

	public boolean getBlankAssetKeyAsDefault()
	{
		try
		{
			Object value = ContentTypeDefinitionController.getController().getAssetSettingValue(this.contentTypeDefinitionVO.getSchemaValue(), "blankAsDefault");
			if (value == null)
			{
				return false;
			}
			else
			{
				return ((Boolean)value).booleanValue();
			}
		}
		catch (SystemException ex)
		{
			logger.info("Error when checking blank asset key as default. Message: " + ex.getMessage());
			return false;
		}
	}

	public String getReasonKey()
	{
		return this.reasonKey;
	}

	public int getMaximumAssetFileSizeForAssetKey(String assetKey)
	{
		return DigitalAssetController.getController().getAssetMaxFileSize(getInfoGluePrincipal(), contentTypeDefinitionVO, assetKey);
	}

	public int getMaximumAssetFileSize()
	{
		return getMaximumAssetFileSizeForAssetKey((String)null);
	}

	public int getMaximumAssetFileSize(AssetKeyDefinition assetKeyDefinition)
	{
		return getMaximumAssetFileSizeForAssetKey(assetKeyDefinition.getAssetKey());
	}

	public String getCurrentDigitalAssetKey()
	{
		return this.currentDigitalAssetKey;
	}

}
