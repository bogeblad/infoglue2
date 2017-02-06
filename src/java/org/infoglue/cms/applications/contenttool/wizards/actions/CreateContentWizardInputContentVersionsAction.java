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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.content.DigitalAssetVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.util.CmsPropertyHandler;

/**
 * This action represents the create content versions including assets in the wizards.
 */

public class CreateContentWizardInputContentVersionsAction extends CreateContentWizardAbstractAction
{
	private static final long serialVersionUID = -5460318924326071117L;
	private static final Logger logger = Logger.getLogger(CreateContentWizardInputContentVersionsAction.class);

	private ContentTypeDefinitionVO contentTypeDefinitionVO = null;
	private List<?> contentTypeAttributes					= null;

	private Integer currentEditorId 						= null;
	private Integer languageId 								= null;
	private ContentVersionVO contentVersionVO 				= new ContentVersionVO();
	private Collection<DigitalAssetVO> digitalAssets		= new ArrayList<DigitalAssetVO>();

	public CreateContentWizardInputContentVersionsAction()
	{
	}

	private String getInputResultView()
	{
		String wysiwygEditor = CmsPropertyHandler.getWysiwygEditor();
		if (wysiwygEditor == null || wysiwygEditor.equalsIgnoreCase("") || wysiwygEditor.equalsIgnoreCase("HTMLArea"))
		{
			return "inputContentVersions";
		}
		else
		{
			return "inputContentVersionsForFCKEditor";
		}
	}

	private void setupInputView() throws Exception
	{
		CreateContentWizardInfoBean createContentWizardInfoBean = getCreateContentWizardInfoBean();
		
		Integer contentTypeDefinitionId = createContentWizardInfoBean.getContentTypeDefinitionId();
		this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithId(contentTypeDefinitionId);
		
		this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().validateAndUpdateContentType(this.contentTypeDefinitionVO);
		ContentTypeDefinitionController.getController().getDefinedAssetKeys(this.contentTypeDefinitionVO.getSchemaValue());
		
		if (this.languageId == null)
		{
			this.languageId = createContentWizardInfoBean.getLanguageId();
			if(this.languageId == null)
			{			
				LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(createContentWizardInfoBean.getRepositoryId());
				this.languageId = masterLanguageVO.getLanguageId();
			}
		}
		
		if(this.contentVersionVO != null && this.contentVersionVO.getContentVersionId() != null)
		{
			digitalAssets = DigitalAssetController.getDigitalAssetVOList(this.contentVersionVO.getId());
		}

		this.contentTypeAttributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(this.contentTypeDefinitionVO.getSchemaValue());
	}

	/**
	 * This method presents the user with the initial input screen for creating a content.
	 *
	 * @return
	 * @throws Exception
	 */
	public String doInput() throws Exception
	{
		setupInputView();
		return getInputResultView();
	}


	/**
	 * This method validates the input and handles any deviations.
	 * 
	 * @return
	 * @throws Exception
	 */
	 
	public String doExecute() throws Exception
	{
		this.contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());

		try
		{
			ContentVersionControllerProxy.getController().acUpdate(this.getInfoGluePrincipal(), this.getContentId(), this.languageId, this.contentVersionVO);
		}
		catch (ConstraintException cex)
		{
			logger.info("Constraint exception when saving content verison. Message: " + cex.getMessage());
			setupInputView();
			cex.setResult(getInputResultView());
			throw cex;
		}

		return "success";
	}

	public List<?> getContentTypeAttributes()
	{
		return this.contentTypeAttributes;
	}

	public ContentTypeDefinitionVO getContentTypeDefinitionVO()
	{
		return this.contentTypeDefinitionVO;
	}

	public Integer getCurrentEditorId()
	{
		return this.currentEditorId;
	}

	public Integer getLanguageId()
	{
		return this.languageId;
	}

	public Integer getContentId()
	{
		return getCreateContentWizardInfoBean().getContentVO().getId();
	}

	public void setCurrentEditorId(Integer currentEditorId)
	{
		this.currentEditorId = currentEditorId;
	}

	public void setLanguageId(Integer languageId)
	{
		this.languageId = languageId;
		this.contentVersionVO.setLanguageId(languageId);
	}

	public void setVersionValue(String versionValue)
	{
		this.contentVersionVO.setVersionValue(versionValue);
	}

	public void setContentVersionId(Integer contentVersionId)
	{
		this.contentVersionVO.setContentVersionId(contentVersionId);
	}
	
	public Integer getContentVersionId()
	{
		return this.contentVersionVO.getContentVersionId();
	}

	public Collection<DigitalAssetVO> getDigitalAssets()
	{
		return digitalAssets;
	}

	public String getAttributeValue(String key)
	{
		String value = "";

		if(this.contentVersionVO != null)
		{
			try
			{
				logger.info("key:" + key);
				logger.info("VersionValue:" + this.contentVersionVO.getVersionValue());

				String xml = this.contentVersionVO.getVersionValue();

				int startTagIndex = xml.indexOf("<" + key + ">");
				int endTagIndex   = xml.indexOf("]]></" + key + ">");

				if(startTagIndex > 0 && startTagIndex < xml.length() && endTagIndex > startTagIndex && endTagIndex <  xml.length())
				{
					value = xml.substring(startTagIndex + key.length() + 11, endTagIndex);
					value = new VisualFormatter().escapeHTML(value);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		logger.info("value:" + value);

		return value;
	}
}
