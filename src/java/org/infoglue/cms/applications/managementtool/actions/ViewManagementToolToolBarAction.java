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
import org.infoglue.cms.applications.common.ImageButton;
import org.infoglue.cms.controllers.kernel.impl.simple.InterceptionPointController;
import org.infoglue.cms.controllers.kernel.impl.simple.RolePropertiesController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.UserPropertiesController;
import org.infoglue.cms.entities.management.InterceptionPointVO;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.net.URLEncoder;

/**
 * This class implements the action class for the framed page in the management tool.
 * 
 * @author Mattias Bogeblad  
 */

public class ViewManagementToolToolBarAction extends WebworkAbstractAction
{
	private String title = "";
	private String name  = "";
	private String toolbarKey = "";
	private String url   = "";
	
	//All id's that are used
	private Integer repositoryId = null;
	private String userName = null;
	private Integer extranetUserId = null;
	private String roleName = null;
	private Integer extranetRoleId = null;
	private Integer languageId = null;
	private Integer functionId = null;
	private Integer serviceDefinitionId = null;
	private Integer availableServiceBindingId = null;
	private Integer siteNodeTypeDefinitionId = null;
	private Integer contentTypeDefinitionId = null;
	private Integer interceptionPointId = null;
	private Integer interceptorId = null;
	private Integer categoryId = null;
	
	private String URIEncoding = CmsPropertyHandler.getProperty("URIEncoding");
	
	private InterceptionPointVO interceptionPointVO = null;
	
	private static HashMap buttonsMap = new HashMap();
		
	public String doExecute() throws Exception
    {
    	if(this.interceptionPointId != null)
	    	this.interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithId(this.interceptionPointId);
    	
        return "success";
    }
    
	public Integer getInterceptionPointId()
	{
		return this.interceptionPointId;
	}

	public void setInterceptionPointId(Integer interceptionPointId)
	{
		this.interceptionPointId = interceptionPointId;
	}

	public Integer getInterceptorId() 
	{
		return this.interceptorId;
	}
	
	public void setInterceptorId(Integer interceptorId) 
	{
		this.interceptorId = interceptorId;
	}

	public Integer getRepositoryId()
	{
		return this.repositoryId;
	}                   

	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	public Integer getExtranetUserId()
	{
		return extranetUserId;
	}

	public void setExtranetUserId(Integer integer)
	{
		extranetUserId = integer;
	}

	public Integer getLanguageId()
	{
		return this.languageId;
	}                   

	public void setLanguageId(Integer languageId)
	{
		this.languageId = languageId;
	}

	public String getRoleName()
	{
		return this.roleName;
	}                   

	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
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

	public Integer getContentTypeDefinitionId()
	{
		return this.contentTypeDefinitionId;
	}                   

	public void setContentTypeDefinitionId(Integer contentTypeDefinitionId)
	{
		this.contentTypeDefinitionId = contentTypeDefinitionId;
	}

	public Integer getCategoryId()			{ return categoryId; }
	public void setCategoryId(Integer i)	{ categoryId = i; }

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

	public Integer getExtranetRoleId()
	{
		return extranetRoleId;
	}

	public void setExtranetRoleId(Integer extranetRoleId)
	{
		this.extranetRoleId = extranetRoleId;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public List getButtons()
	{
		CmsLogger.logInfo("Title:" + this.title);
		CmsLogger.logInfo("toolbarKey:" + this.toolbarKey);
		
		try
		{
			if(this.toolbarKey.equalsIgnoreCase("repositories"))
				return getRepositoriesButtons();
			if(this.toolbarKey.equalsIgnoreCase("repository details"))
				return getRepositoryDetailsButtons();
			if(this.toolbarKey.equalsIgnoreCase("system users"))
				return getSystemUsersButtons();
			if(this.toolbarKey.equalsIgnoreCase("available updates"))
				return getAvailablePackagesButtons();
			if(this.toolbarKey.equalsIgnoreCase("user details"))
				return getSystemUserDetailsButtons();
			if(this.toolbarKey.equalsIgnoreCase("roles"))
				return getRolesButtons();
			if(this.toolbarKey.equalsIgnoreCase("role details"))
				return getRoleDetailsButtons();
			if(this.toolbarKey.equalsIgnoreCase("languages"))
				return getLanguagesButtons();
			if(this.toolbarKey.equalsIgnoreCase("language details"))
				return getLanguageDetailsButtons();
			if(this.toolbarKey.equalsIgnoreCase("InterceptionPoints"))
				return getInterceptionPointsButtons();
			if(this.toolbarKey.equalsIgnoreCase("InterceptionPoint details"))
				return getInterceptionPointButtons();
			if(this.toolbarKey.equalsIgnoreCase("Interceptors"))
				return getInterceptorsButtons();
			if(this.toolbarKey.equalsIgnoreCase("Interceptor details"))
				return getInterceptorButtons();
			if(this.toolbarKey.equalsIgnoreCase("service definitions"))
				return getServiceDefinitionsButtons();
			if(this.toolbarKey.equalsIgnoreCase("service definition details"))
				return getServiceDefinitionDetailsButtons();
			if(this.toolbarKey.equalsIgnoreCase("available service bindings"))
				return getAvailableServiceBindingsButtons();
			if(this.toolbarKey.equalsIgnoreCase("available service binding details"))
				return getAvailableServiceBindingDetailsButtons();
			if(this.toolbarKey.equalsIgnoreCase("site node type definitions"))
				return getSiteNodeTypeDefinitionsButtons();
			if(this.toolbarKey.equalsIgnoreCase("site node type definition details"))
				return getSiteNodeTypeDefinitionDetailsButtons();
			if(this.toolbarKey.equalsIgnoreCase("content type definitions"))
				return getContentTypeDefinitionsButtons();
			if(this.toolbarKey.equalsIgnoreCase("content type definition details"))
				return getContentTypeDefinitionDetailsButtons();
			if(this.toolbarKey.equalsIgnoreCase("categories") || this.toolbarKey.equalsIgnoreCase("edit category"))
				return getCategoryButtons();
		}
		catch(Exception e) {}			
					
		return null;				
	}
	

	private List getRepositoriesButtons()
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("CreateRepository!input.action", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.newRepository"), "New Repository"));	
		buttons.add(new ImageButton(true, "javascript:submitListForm('repository');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteRepository"), "Delete Repositories"));
		buttons.add(new ImageButton(true, "javascript:openPopup('ImportRepository!input.action', 'Import', 'width=400,height=250,resizable=no');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.importRepository"), getLocalizedString(getSession().getLocale(), "tool.managementtool.importRepository.header")));	
		return buttons;
	}
	
	private List getRepositoryDetailsButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("Confirm.action?header=Delete%20repository&yesDestination=" + URLEncoder.encode("DeleteRepository.action?repositoryId=" + this.repositoryId, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListRepository.action?title=Repositories", "UTF-8") + "&message=" + URLEncoder.encode("Do you really want to delete the repository " + this.name + "? This action will delete all information and structure contained in that repository so do not do it unless you are absolutely sure.", "UTF-8"), getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteRepository"), "Delete Repository"));
		buttons.add(new ImageButton(true, "javascript:openPopup('ExportRepository!input.action?repositoryId=" + this.repositoryId + "', 'Export', 'width=400,height=200,resizable=no');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.exportRepository"), getLocalizedString(getSession().getLocale(), "tool.managementtool.exportRepository.header")));	
		buttons.add(new ImageButton("ViewRepositoryProperties.action?repositoryId=" + this.repositoryId, getLocalizedString(getSession().getLocale(), "images.global.buttons.editProperties"), "Edit Properties", new Integer(22), new Integer(80)));
		return buttons;				
	}

	private List getAvailablePackagesButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("RefreshUpdates.action", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.refreshUpdates"), "Refresh Updates"));	
		buttons.add(new ImageButton(true, "javascript:submitListForm('updatePackage');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.installUpdate"), "Install update"));
		return buttons;
	}
	
	private List getSystemUsersButtons() throws Exception
	{
		List buttons = new ArrayList();
		if(UserControllerProxy.getController().getSupportCreate())
			buttons.add(new ImageButton("CreateSystemUser!input.action", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.newSystemUser"), "New System User"));	
		if(UserControllerProxy.getController().getSupportDelete())
			buttons.add(new ImageButton(true, "javascript:submitListFormWithPrimaryKey('systemUser', 'userName');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteSystemUser"), "Delete system users"));
		buttons.add(new ImageButton(true, "javascript:toggleSearchForm();", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.searchButton"), "Search Form"));
		return buttons;
	}

	private List getSystemUserDetailsButtons() throws Exception
	{
		List buttons = new ArrayList();
		if(UserControllerProxy.getController().getSupportDelete())
			buttons.add(new ImageButton("Confirm.action?header=Delete%20system user&yesDestination=" + URLEncoder.encode("DeleteSystemUser.action?userName=" + URLEncoder.encode(this.userName, URIEncoding), URIEncoding) + "&noDestination=" + URLEncoder.encode("ViewListSystemUser.action?title=SystemUsers", URIEncoding) + "&message=" + URLEncoder.encode("Do you really want to delete the system user " + URLEncoder.encode(this.userName, URIEncoding), URIEncoding), getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteSystemUser"), "Delete System User"));
		
		List contentTypeDefinitionVOList = UserPropertiesController.getController().getContentTypeDefinitionVOList(this.userName);
		if(contentTypeDefinitionVOList.size() > 0)
			buttons.add(new ImageButton("ViewUserProperties.action?userName=" + URLEncoder.encode(URLEncoder.encode(this.userName, URIEncoding), URIEncoding), getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.viewSystemUserProperties"), "View User Properties"));
		return buttons;				
	}

	private List getRolesButtons() throws Exception
	{
		List buttons = new ArrayList();
		if(UserControllerProxy.getController().getSupportCreate())
			buttons.add(new ImageButton("CreateRole!input.action", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.newRole"), "New Role"));	
		if(UserControllerProxy.getController().getSupportDelete())
			buttons.add(new ImageButton(true, "javascript:submitListFormWithPrimaryKey('role', 'roleName');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteRole"), "Delete roles"));
		
		return buttons;
	}
	
	private List getRoleDetailsButtons() throws Exception
	{
		List buttons = new ArrayList();
		
		String yesDestination 	= URLEncoder.encode("DeleteRole.action?roleName=" + URLEncoder.encode(this.roleName, URIEncoding), URIEncoding);
		String noDestination  	= URLEncoder.encode("ViewListRole.action?title=Roles", URIEncoding);
		String message 		 	= URLEncoder.encode("Do you really want to delete the role " + URLEncoder.encode(this.roleName, URIEncoding), URIEncoding);
		//System.out.println("yesDestination:" + yesDestination);
		//System.out.println("noDestination:" + noDestination);
		//System.out.println("message:" + message);
		
		if(UserControllerProxy.getController().getSupportDelete())
			buttons.add(new ImageButton("Confirm.action?header=Delete%20role&yesDestination=" + yesDestination + "&noDestination=" + noDestination + "&message=" + message, getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteRole"), "Delete Role"));
		
		List contentTypeDefinitionVOList = RolePropertiesController.getController().getContentTypeDefinitionVOList(this.roleName);
		if(contentTypeDefinitionVOList.size() > 0)
			buttons.add(new ImageButton("ViewRoleProperties.action?roleName=" + URLEncoder.encode(URLEncoder.encode(this.roleName, URIEncoding)), getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.viewRoleProperties"), "View Role Properties"));
		
		return buttons;				
	}

	private List getLanguagesButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("CreateLanguage!input.action", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.newLanguage"), "New Language"));	
		buttons.add(new ImageButton(true, "javascript:submitListForm('language');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteLanguage"), "Delete languages"));
		return buttons;
	}
	
	private List getLanguageDetailsButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("Confirm.action?header=Delete%20language&yesDestination=" + URLEncoder.encode("DeleteLanguage.action?languageId=" + this.languageId, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListLanguage.action?title=Languages", "UTF-8") + "&message=" + URLEncoder.encode("Do you really want to delete the language " + this.name, "UTF-8"), getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteLanguage"), "Delete Language"));
		return buttons;				
	}

	private List getInterceptionPointsButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("CreateInterceptionPoint!input.action", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.newInterceptionPoint"), "New InterceptionPoint"));	
		buttons.add(new ImageButton(true, "javascript:submitListForm('interceptionPoint');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteInterceptionPoint"), "Delete InterceptionPoints"));
		return buttons;
	}
	
	private List getInterceptionPointButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("Confirm.action?header=Delete%20InterceptionPoint&yesDestination=" + URLEncoder.encode("DeleteInterceptionPoint.action?interceptionPointId=" + this.interceptionPointId, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListInterceptionPoint.action?title=InterceptionPoints", "UTF-8") + "&message=" + URLEncoder.encode("Do you really want to delete the interceptionPoint " + this.name, "UTF-8"), getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteInterceptionPoint"), "Delete InterceptionPoint"));
		if(this.interceptionPointVO.getUsesExtraDataForAccessControl().booleanValue() == false)
			buttons.add(new ImageButton("ViewAccessRights.action?interceptionPointCategory=" + this.interceptionPointVO.getCategory() + "&interceptionPointId=" + this.interceptionPointId + "&returnAddress=ViewInterceptionPoint.action?interceptionPointId=" + this.interceptionPointId + "&colorScheme=ManagementTool", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.accessRights"), "InterceptionPoint Access Rights"));
		
		return buttons;				
	}

	private List getInterceptorsButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("CreateInterceptor!input.action", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.newInterceptor"), "New Interceptor"));	
		buttons.add(new ImageButton(true, "javascript:submitListForm('interceptor');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteInterceptor"), "Delete Interceptor"));
		return buttons;
	}
	
	private List getInterceptorButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("Confirm.action?header=Delete%20Interceptor&yesDestination=" + URLEncoder.encode("DeleteInterceptor.action?interceptorId=" + this.interceptorId, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListInterceptor.action?title=Interceptors", "UTF-8") + "&message=" + URLEncoder.encode("Do you really want to delete the interceptor " + this.name, "UTF-8"), getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteInterceptor"), "Delete Interceptor"));
		return buttons;				
	}

	private List getServiceDefinitionsButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("CreateServiceDefinition!input.action", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.newServiceDefinition"), "New ServiceDefinition"));	
		buttons.add(new ImageButton(true, "javascript:submitListForm('serviceDefinition');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteServiceDefinition"), "Delete serviceDefinitions"));
		return buttons;
	}
	
	private List getServiceDefinitionDetailsButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("Confirm.action?header=Delete%20serviceDefinition&yesDestination=" + URLEncoder.encode("DeleteServiceDefinition.action?serviceDefinitionId=" + this.serviceDefinitionId, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListServiceDefinition.action?title=ServiceDefinitions", "UTF-8") + "&message=" + URLEncoder.encode("Do you really want to delete the serviceDefinition " + this.name, "UTF-8"), getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteServiceDefinition"), "Delete ServiceDefinition"));
		return buttons;				
	}

	private List getAvailableServiceBindingsButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("CreateAvailableServiceBinding!input.action", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.newAvailableServiceBinding"), "New AvailableServiceBinding"));	
		buttons.add(new ImageButton(true, "javascript:submitListForm('availableServiceBinding');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteAvailableServiceBinding"), "Delete availableServiceBindings"));
		return buttons;
	}
	
	private List getAvailableServiceBindingDetailsButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("Confirm.action?header=Delete%20availableServiceBinding&yesDestination=" + URLEncoder.encode("DeleteAvailableServiceBinding.action?availableServiceBindingId=" + this.availableServiceBindingId, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListAvailableServiceBinding.action?title=AvailableServiceBindings", "UTF-8") + "&message=" + URLEncoder.encode("Do you really want to delete the availableServiceBinding " + this.name, "UTF-8"), getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteAvailableServiceBinding"), "Delete AvailableServiceBinding"));
		return buttons;				
	}

	private List getSiteNodeTypeDefinitionsButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("CreateSiteNodeTypeDefinition!input.action", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.newSiteNodeTypeDefinition"), "New SiteNodeTypeDefinition"));	
		buttons.add(new ImageButton(true, "javascript:submitListForm('siteNodeTypeDefinition');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteSiteNodeTypeDefinition"), "Delete siteNodeTypeDefinitions"));
		return buttons;
	}
	
	private List getSiteNodeTypeDefinitionDetailsButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("Confirm.action?header=Delete%20siteNodeTypeDefinition&yesDestination=" + URLEncoder.encode("DeleteSiteNodeTypeDefinition.action?siteNodeTypeDefinitionId=" + this.siteNodeTypeDefinitionId, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListSiteNodeTypeDefinition.action?title=SiteNodeTypeDefinitions", "UTF-8") + "&message=" + URLEncoder.encode("Do you really want to delete the siteNodeTypeDefinition " + this.name, "UTF-8"), getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteSiteNodeTypeDefinition"), "Delete SiteNodeTypeDefinition"));
		return buttons;				
	}


	private List getContentTypeDefinitionsButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("CreateContentTypeDefinition!input.action", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.newContentTypeDefinition"), "New ContentTypeDefinition"));	
		buttons.add(new ImageButton(true, "javascript:submitListForm('contentTypeDefinition');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteContentTypeDefinition"), "Delete contentTypeDefinitions"));
		return buttons;
	}
	
	private List getContentTypeDefinitionDetailsButtons() throws Exception
	{
		List buttons = new ArrayList();
		buttons.add(new ImageButton("Confirm.action?header=Delete%20contentTypeDefinition&yesDestination=" + URLEncoder.encode("DeleteContentTypeDefinition.action?contentTypeDefinitionId=" + this.contentTypeDefinitionId, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListContentTypeDefinition.action?title=ContentTypeDefinitions", "UTF-8") + "&message=" + URLEncoder.encode("Do you really want to delete the contentTypeDefinition " + this.name, "UTF-8"), getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteContentTypeDefinition"), "Delete ContentTypeDefinition"));
		buttons.add(getAccessRightsButton());
		return buttons;				
	}

	private List getCategoryButtons() throws Exception
	{
		String url = "CategoryManagement!new.action";
		if(getCategoryId() != null)
			url += "?model/parentId=" + getCategoryId();

		List buttons = new ArrayList();
		buttons.add(new ImageButton(url, getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.newCategory"), "New Category"));

		if(getCategoryId() != null)
			buttons.add(new ImageButton(true, "javascript:openPopup('CategoryManagement!displayTreeForMove.action?categoryId=" + getCategoryId() + "', 'Category', 'width=400,height=600,resizable=no,status=yes');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.moveCategory"), "Move Category"));

		buttons.add(new ImageButton(true, "javascript:submitListForm('category');", getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.deleteCategory"), "Delete Category"));
		return buttons;
	}
	
	private ImageButton getAccessRightsButton() throws Exception
	{
		String returnAddress = URLEncoder.encode(URLEncoder.encode("ViewContentTypeDefinition.action?contentTypeDefinitionId=" + this.contentTypeDefinitionId, "UTF-8"), "UTF-8");
		return new ImageButton("ViewAccessRights.action?interceptionPointCategory=ContentTypeDefinition&extraParameters=" + this.contentTypeDefinitionId +"&colorScheme=ManagementTool&returnAddress=" + returnAddress, getLocalizedString(getSession().getLocale(), "images.managementtool.buttons.accessRights"), "tool.managementtool.accessRights.header");
	}

}
