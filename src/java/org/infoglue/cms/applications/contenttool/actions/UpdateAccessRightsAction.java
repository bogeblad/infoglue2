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

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.AccessRightController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeVersionControllerProxy;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.exception.AccessConstraintException;
import org.infoglue.cms.util.AccessConstraintExceptionBuffer;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

 
/**
  * This is the action-class for Update Access Rights
  * 
  * @author Mattias Bogeblad
  */

public class UpdateAccessRightsAction extends WebworkAbstractAction
{
	private Integer interceptionPointId;
	private String parameters = "";
	private String roleName;
	private String returnAddress;
	private String url;
	
	private String interceptionPointCategory;
	
	private ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
	
	public String doExecute() throws Exception
    {   
		AccessConstraintExceptionBuffer ceb = new AccessConstraintExceptionBuffer();
		
		if(interceptionPointCategory.equalsIgnoreCase("Content"))
		{	
			Integer contentId = new Integer(parameters);
			ContentVO contentVO = ContentControllerProxy.getController().getContentVOWithId(contentId);
			if(!contentVO.getCreatorName().equalsIgnoreCase(this.getInfoGluePrincipal().getName()))
			{
				Integer protectedContentId = ContentControllerProxy.getController().getProtectedContentId(contentId);
				if(ContentControllerProxy.getController().getIsContentProtected(contentId) && !AccessRightController.getController().getIsPrincipalAuthorized(this.getInfoGluePrincipal(), "Content.ChangeAccessRights", protectedContentId.toString()))
					ceb.add(new AccessConstraintException("Content.contentId", "1006"));
			}
		}
		else if(interceptionPointCategory.equalsIgnoreCase("SiteNodeVersion"))
		{	
			Integer siteNodeVersionId = new Integer(parameters);
			SiteNodeVersionVO siteNodeVersionVO = SiteNodeVersionController.getController().getSiteNodeVersionVOWithId(siteNodeVersionId);
			if(!siteNodeVersionVO.getVersionModifier().equalsIgnoreCase(this.getInfoGluePrincipal().getName()))
			{
				Integer protectedSiteNodeVersionId = SiteNodeVersionControllerProxy.getSiteNodeVersionControllerProxy().getProtectedSiteNodeVersionId(siteNodeVersionId);
				if(protectedSiteNodeVersionId != null && !AccessRightController.getController().getIsPrincipalAuthorized(this.getInfoGluePrincipal(), "SiteNodeVersion.ChangeAccessRights", siteNodeVersionId.toString()))
					ceb.add(new AccessConstraintException("SiteNodeVersion.siteNodeId", "1006"));
			}
		}
		
		ceb.throwIfNotEmpty();
		
		AccessRightController.getController().update(this.parameters, this.getRequest());
		//ExtranetAccessController.getController().update(this.name, this.value, this.getRequest());

		this.url = getResponse().encodeRedirectURL(this.returnAddress);
		//getResponse().sendRedirect(url);
		
		return "success";
	}

	public String doSaveAndExit() throws Exception
    {
		doExecute();
						
		return "saveAndExit";
	}


	public String getReturnAddress()
	{
		return returnAddress;
	}

	public void setReturnAddress(String returnAddress)
	{
		this.returnAddress = returnAddress;
	}

	public Integer getInterceptionPointId()
	{
		return this.interceptionPointId;
	}

	public void setInterceptionPointId(Integer interceptionPointId)
	{
		this.interceptionPointId = interceptionPointId;
	}

	public String getParameters()
	{
		return this.parameters;
	}

	public void setParameters(String parameters)
	{
		this.parameters = parameters;
	}

	public String getInterceptionPointCategory()
	{
		return interceptionPointCategory;
	}

	public void setInterceptionPointCategory(String interceptionPointCategory)
	{
		this.interceptionPointCategory = interceptionPointCategory;
	}

	public String getUrl()
	{
		return url;
	}

}
