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

import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.entities.content.DigitalAssetVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.util.CmsLogger;

import java.util.List;


/**
 * This class represents the form for creating and updating digital assets.
 */

public class ViewDigitalAssetAction extends WebworkAbstractAction
{
	private List availableLanguages  = null;
	private Integer contentVersionId = null;	
	private Integer digitalAssetId   = null;
	private Integer uploadedFilesCounter = new Integer(0);

	private ContentVersionVO contentVersionVO;
	private ContentTypeDefinitionVO contentTypeDefinitionVO;
	private DigitalAssetVO digitalAssetVO;
	
    public ViewDigitalAssetAction() 
    {
        this(new ContentVersionVO());
    }
    
    public ViewDigitalAssetAction(ContentVersionVO contentVersionVO) 
    {
		CmsLogger.logInfo("Construction ViewDigitalAssetAction");
        this.contentVersionVO = contentVersionVO;
    }
    
    public String doExecute() throws Exception
    {
    	this.contentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(this.contentVersionId);
        this.contentTypeDefinitionVO = ContentController.getContentController().getContentTypeDefinition(contentVersionVO.getContentId());

        return "success";
    }
       
    public String doUpdate() throws Exception
    {
    	this.digitalAssetVO = DigitalAssetController.getDigitalAssetVOWithId(this.digitalAssetId);
    	this.contentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(this.contentVersionId);
        this.contentTypeDefinitionVO = ContentController.getContentController().getContentTypeDefinition(contentVersionVO.getContentId());

        return "update";
    }
     
    public java.lang.Integer getContentVersionId()
    {
        return this.contentVersionId;
    }
        
    public void setContentVersionId(java.lang.Integer contentVersionId)
    {
	    this.contentVersionId = contentVersionId;
    }
    
	public List getAvailableLanguages()
	{
		return this.availableLanguages;
	}	
	
	public Integer getUploadedFilesCounter()
	{
		return this.uploadedFilesCounter;
	}

	public List getDefinedAssetKeys()
	{
		return ContentTypeDefinitionController.getController().getDefinedAssetKeys(this.contentTypeDefinitionVO.getSchemaValue());
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
		return this.digitalAssetVO.getAssetKey();
	}

}
