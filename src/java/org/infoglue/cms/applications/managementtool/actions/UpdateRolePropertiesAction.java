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

package org.infoglue.cms.applications.managementtool.actions;

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.entities.management.RolePropertiesVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

/**
  * This is the action-class for UpdateExtranetGroupPropertiesAction
  * 
  * @author Mattias Bogeblad
  */

public class UpdateRolePropertiesAction extends WebworkAbstractAction 
{
	private RolePropertiesVO rolePropertiesVO;
	private Integer languageId;
	private Integer contentTypeDefinitionId;
	private Integer currentEditorId;
	private String attributeName;

	private ConstraintExceptionBuffer ceb;
	
	public UpdateRolePropertiesAction()
	{
		rolePropertiesVO = new RolePropertiesVO();
		this.ceb = new ConstraintExceptionBuffer();	
	}
		
	public String doExecute() throws Exception
	{
		//super.initialize(this.contentVersionId, this.contentId, this.languageId);
		ceb.throwIfNotEmpty();
		RolePropertiesController.getController().update(this.languageId, this.contentTypeDefinitionId, this.rolePropertiesVO);
		
		return "success";
	}

	/*
	public String doStandalone() throws Exception
	{
		//super.initialize(this.contentVersionId, this.contentId, this.languageId);
		ceb.throwIfNotEmpty();
		ContentVersionController.update(this.contentId, this.languageId, getRequest().getRemoteUser(), this.contentVersionVO);
		
		return "standalone";
	}
*/
	public String doSaveAndExit() throws Exception
	{
		doExecute();
						 
		return "saveAndExit";
	}

	public String doSaveAndExitStandalone() throws Exception
	{
		doExecute();
						 
		return "saveAndExitStandalone";
	}
				   
	public void setRolePropertiesId(Integer rolePropertiesId)
	{
		this.rolePropertiesVO.setRolePropertiesId(rolePropertiesId);
	}

	public java.lang.Integer getRolePropertiesId()
	{
		return this.rolePropertiesVO.getRolePropertiesId();
	}
	
	public Integer getContentTypeDefinitionId()
	{
		return contentTypeDefinitionId;
	}

	public void setContentTypeDefinitionId(Integer contentTypeDefinitionId)
	{
		this.contentTypeDefinitionId = contentTypeDefinitionId;
	}

	public String getRoleName()
	{
		return this.rolePropertiesVO.getRoleName();
	}

	public void setRoleName(String roleName)
	{
		this.rolePropertiesVO.setRoleName(roleName);
	}

	public void setLanguageId(Integer languageId)
	{
		this.languageId = languageId;
	}

	public java.lang.Integer getLanguageId()
	{
		return this.languageId;
	}
        
	public java.lang.String getValue()
	{
		return this.rolePropertiesVO.getValue();
	}
        
	public void setValue(java.lang.String value)
	{
		this.rolePropertiesVO.setValue(value);
	}
    
	public Integer getCurrentEditorId() 
	{
		return currentEditorId;
	}

	public void setCurrentEditorId(Integer integer) 
	{
		currentEditorId = integer;
	}

	public String getAttributeName()
	{
		return this.attributeName;
	}

	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}


}
