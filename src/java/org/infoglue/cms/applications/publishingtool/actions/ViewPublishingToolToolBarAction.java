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

package org.infoglue.cms.applications.publishingtool.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.ImageButton;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.util.CmsPropertyHandler;

/**
 * This class implements the action class for the framed page in the management tool.
 * 
 * @author Mattias Bogeblad  
 */

public class ViewPublishingToolToolBarAction extends InfoGlueAbstractAction
{
    private final static Logger logger = Logger.getLogger(ViewPublishingToolToolBarAction.class.getName());

	private static final long serialVersionUID = 1L;

	private String title = "";
	private String name  = "";
	private String toolbarKey = "";
	private String url   = "";
	
	//All id's that are used
	private Integer repositoryId = null;
	private Integer systemUserId = null;
	private Integer roleId = null;
	private Integer languageId = null;
	private Integer functionId = null;
	private Integer serviceDefinitionId = null;
	private Integer availableServiceBindingId = null;
	private Integer siteNodeTypeDefinitionId = null;
	
	private static HashMap buttonsMap = new HashMap();
		
	public String doExecute() throws Exception
    {
        return "success";
    }

	public Integer getRepositoryId()
	{
		return this.repositoryId;
	}                   

	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	public Integer getSystemUserId()
	{
		return this.systemUserId;
	}                   

	public void setSystemUserId(Integer systemUserId)
	{
		this.systemUserId = systemUserId;
	}

	public Integer getLanguageId()
	{
		return this.languageId;
	}                   

	public void setLanguageId(Integer languageId)
	{
		this.languageId = languageId;
	}

	public Integer getRoleId()
	{
		return this.roleId;
	}                   

	public void setRoleId(Integer roleId)
	{
		this.roleId = roleId;
	}

	public Integer getFunctionId()
	{
		return this.functionId;
	}                   

	public void setFunctionId(Integer functionId)
	{
		this.functionId = functionId;
	}

	public Integer getServiceDefinitionId()
	{
		return this.serviceDefinitionId;
	}                   

	public void setServiceDefinitionId(Integer serviceDefinitionId)
	{
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public Integer getAvailableServiceBindingId()
	{
		return this.availableServiceBindingId;
	}                   

	public void setAvailableServiceBindingId(Integer availableServiceBindingId)
	{
		this.availableServiceBindingId = availableServiceBindingId;
	}

	public Integer getSiteNodeTypeDefinitionId()
	{
		return this.siteNodeTypeDefinitionId;
	}                   

	public void setSiteNodeTypeDefinitionId(Integer siteNodeTypeDefinitionId)
	{
		this.siteNodeTypeDefinitionId = siteNodeTypeDefinitionId;
	}

	public String getTitle()
	{
		return this.title;
	}                   
	
	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getName()
	{
		return this.name;
	}                   
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getToolbarKey()
	{
		return this.toolbarKey;
	}                   

	public void setToolbarKey(String toolbarKey)
	{
		this.toolbarKey = toolbarKey;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}
	
	public String getUrl()
	{
		return this.url;
	}

	public List getButtons()
	{
		logger.info("Title:" + this.title);
		
		if(this.toolbarKey.equalsIgnoreCase("publications"))
			return getPublicationsButtons();
					
		return null;				
	}
	
	
	private List getPublicationsButtons()
	{
	    List buttons = new ArrayList();
		buttons.add(new ImageButton(true, "javascript:submitToPreview();", getLocalizedString(getSession().getLocale(), "images.publishingtool.buttons.previewContent"), "tool.publishtool.preview.header", "Preview marked versions"));
		buttons.add(new ImageButton(true, "javascript:submitToCreate();", getLocalizedString(getSession().getLocale(), "images.publishingtool.buttons.createEdition"), "tool.publishtool.createEdition.header", "Create a new edition of the marked versions"));
		buttons.add(new ImageButton(true, "javascript:submitToUnpublish('publication');", getLocalizedString(getSession().getLocale(), "images.publishingtool.buttons.unpublishEdition"), "tool.publishtool.unpublishEdition.header", "Unpublish the marked edition and send the versions back to publishable state"));
		buttons.add(new ImageButton(true, "javascript:submitToDeny();", getLocalizedString(getSession().getLocale(), "images.publishingtool.buttons.denyPublishing"), "tool.publishtool.denyPublication.header", "Deny the checked items from publication"));
		
		try
		{
		    RepositoryVO repositoryVO = RepositoryController.getController().getRepositoryVOWithId(this.repositoryId);

		    String repositoryName = repositoryVO.getName();
			String dnsName = repositoryVO.getDnsName();

		    String previewUrl = null;
		    
		    String keyword = "preview=";
		    int startIndex = (dnsName == null) ? -1 : dnsName.indexOf(keyword);
		    if(startIndex != -1)
		    {
		        int endIndex = dnsName.indexOf(",", startIndex);
			    if(endIndex > -1)
		            dnsName = dnsName.substring(startIndex, endIndex);
		        else
		            dnsName = dnsName.substring(startIndex);

			    previewUrl = dnsName.split("=")[1] + CmsPropertyHandler.getComponentRendererUrl().replaceAll("Working", "Preview") + "ViewPage.action";
		    }
		    else
		    {
		        previewUrl = CmsPropertyHandler.getStagingDeliveryUrl();
		    }

			buttons.add(new ImageButton(true, "javascript:openPopup('" + previewUrl + "?repositoryName=" + repositoryName + "', 'StagingPreview', 'width=800,height=600,resizable=yes,toolbar=yes,scrollbars=yes,status=yes,location=yes,menubar=yes');", getLocalizedString(getSession().getLocale(), "images.publishingtool.buttons.previewEnvironment"), "tool.publishtool.previewEnvironment.header"));
		}
		catch(Exception e)
		{
		    e.printStackTrace();
		}
		
		return buttons;				
	}


}
