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

import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;

import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;

import java.util.Iterator;
import java.util.List;

/**
 * This action represents the create content versions including assets in the wizards.
 */

public class CreateContentWizardInputContentVersionsAction extends CreateContentWizardAbstractAction
{
	private ContentTypeDefinitionVO contentTypeDefinitionVO = null;
	private List contentTypeAttributes						= null;
	private String returnAddress							= null;
	private ConstraintExceptionBuffer ceb					= new ConstraintExceptionBuffer();

	private String versionValue								= null;
	private Integer currentEditorId 						= null;
	private Integer languageId 								= null;
	private ContentVersionVO contentVersionVO 				= new ContentVersionVO();
	
	public CreateContentWizardInputContentVersionsAction()
	{
	}
	

	/**
	 * This method presents the user with the initial input screen for creating a content.
	 * 
	 * @return
	 * @throws Exception
	 */
	 
	public String doInput() throws Exception
	{
		CreateContentWizardInfoBean createContentWizardInfoBean = getCreateContentWizardInfoBean();
		
		Integer contentTypeDefinitionId = createContentWizardInfoBean.getContentTypeDefinitionId();
		this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithId(contentTypeDefinitionId);
		
		this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().validateAndUpdateContentType(this.contentTypeDefinitionVO);
		List assetKeys = ContentTypeDefinitionController.getController().getDefinedAssetKeys(this.contentTypeDefinitionVO.getSchemaValue());
		
		LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(createContentWizardInfoBean.getRepositoryId());
		if(this.languageId == null)
			this.languageId = masterLanguageVO.getLanguageId();
		
		boolean missingAsset = false;
		Iterator assetKeysIterator = assetKeys.iterator();
		while(assetKeysIterator.hasNext())
		{
			String assetKey = (String)assetKeysIterator.next();
			if(!createContentWizardInfoBean.getDigitalAssets().containsKey(assetKey + "_" + masterLanguageVO.getId()))
				return "inputAssets";
		}
		
		this.contentTypeAttributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(this.contentTypeDefinitionVO.getSchemaValue());
		System.out.println("contentTypeAttributes:" + contentTypeAttributes.size());
		
		return "inputContentVersions";
	}

	/**
	 * This method validates the input and handles any deviations.
	 * 
	 * @return
	 * @throws Exception
	 */
	 
	public String doExecute() throws Exception
	{
		CreateContentWizardInfoBean createContentWizardInfoBean = getCreateContentWizardInfoBean();
		
		this.contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());
		System.out.println("languageId: " + languageId);
		System.out.println("contentVersionVO: " + contentVersionVO.getVersionValue());
		createContentWizardInfoBean.getContentVersions().put(languageId, this.contentVersionVO);
		//getCreateContentWizardInfoBean()
		//this.contentVO.setCreatorName(this.getInfoGluePrincipal().getName());

		//ceb = this.contentVO.validate();
	
		//if(!ceb.isEmpty())
		//	initialiaze();
	
		//ceb.throwIfNotEmpty();

		return "success";
	}

	public List getContentTypeAttributes()
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
		System.out.println("versionValue: " + versionValue);
		this.contentVersionVO.setVersionValue(versionValue);
	}

}
