package org.infoglue.cms.controllers.kernel.impl.simple;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.ToolbarButton;
import org.infoglue.cms.applications.common.ToolbarButton;
import org.infoglue.cms.entities.management.InterceptionPointVO;
import org.infoglue.cms.entities.workflow.WorkflowDefinitionVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGlueGroup;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.security.InfoGlueRole;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.StringManager;
import org.infoglue.cms.util.StringManagerFactory;

public class ToolbarController
{
	private final static Logger logger = Logger.getLogger(ToolbarController.class.getName());

	private static final long serialVersionUID = 1L;
	
	private String URIEncoding = CmsPropertyHandler.getURIEncoding();

	private InfoGluePrincipal principal = null;
	private Locale locale = null;
	private String primaryKey = null;
	private Integer primaryKeyAsInteger = null;
	private String extraParameters = null;
	
	public List<ToolbarButton> getToolbarButtons(String toolbarKey, InfoGluePrincipal principal, Locale locale, String primaryKey, String extraParameters)
	{
		this.principal = principal;
		this.locale = locale;
		this.primaryKey = primaryKey;
		this.extraParameters = extraParameters;
		try
		{
			primaryKeyAsInteger = new Integer(primaryKey);
		}
		catch (Exception e) 
		{
			//Do nothing
		}
		logger.info("toolbarKey:" + toolbarKey);
		logger.info("primaryKey:" + primaryKey);
		logger.info("extraParameters:" + extraParameters);
		
		try
		{
			/*
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.repositoryList.header"))
				return getRepositoriesButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewRepository.header"))
				return getRepositoryDetailsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewSystemUserList.header"))
				return getSystemUsersButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewSystemUser.header"))
				return getSystemUserDetailsButtons();
	*/
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewRoleList.header"))
				return getRolesButtons();
		/*
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewRole.header"))
				return getRoleDetailsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewGroupList.header"))
				return getGroupsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewGroup.header"))
				return getGroupDetailsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewLanguageList.header"))
				return getLanguagesButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewLanguage.header"))
				return getLanguageDetailsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewInterceptionPointList.header"))
				return getInterceptionPointsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewInterceptionPoint.header"))
				return getInterceptionPointButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewInterceptorList.header"))
				return getInterceptorsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewInterceptor.header"))
				return getInterceptorButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewServiceDefinitionList.header"))
				return getServiceDefinitionsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewServiceDefinition.header"))
				return getServiceDefinitionDetailsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewAvailableServiceBindingList.header"))
				return getAvailableServiceBindingsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewAvailableServiceBinding.header"))
				return getAvailableServiceBindingDetailsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewSiteNodeTypeDefinitionList.header"))
				return getSiteNodeTypeDefinitionsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewSiteNodeTypeDefinition.header"))
				return getSiteNodeTypeDefinitionDetailsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewContentTypeDefinitionList.header"))
				return getContentTypeDefinitionsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewContentTypeDefinition.header"))
				return getContentTypeDefinitionDetailsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewCategoryList.header") || toolbarKey.equalsIgnoreCase("tool.managementtool.editCategory.header"))
				return getCategoryButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewUp2DateList.header"))
				return getAvailablePackagesButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewWorkflowDefinitionList.header"))
				return getWorkflowDefinitionsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewWorkflowDefinition.header"))
				return getWorkflowDefinitionDetailsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.portletList.header"))
				return getPortletsButtons();
			//if(toolbarKey.equalsIgnoreCase("tool.managementtool.portlet.header"))
			//	return getPortletDetailsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.redirectList.header"))
				return getRedirectsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewRedirect.header"))
				return getRedirectDetailsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.serverNodeList.header"))
				return getServerNodesButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewServerNode.header"))
				return getServerNodeDetailsButtons();
			if(toolbarKey.equalsIgnoreCase("tool.managementtool.viewMessageCenter.header"))
				return getMessageCenterButtons();
			*/
		}
		catch(Exception e) {e.printStackTrace();}			
					
		return null;				
	}
	
	/*
	private List<ToolbarButton> getRepositoriesButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("CreateRepository!input.action", getLocalizedString(locale, "images.managementtool.buttons.newRepository"), "tool.managementtool.createRepository.header"));	
		buttons.add(new ToolbarButton(true, "javascript:submitListForm('repository');", getLocalizedString(locale, "images.managementtool.buttons.deleteRepository"), "tool.managementtool.deleteRepositories.header"));
		buttons.add(new ToolbarButton(true, "javascript:openPopup('ImportRepository!input.action', 'Import', 'width=400,height=250,resizable=no');", getLocalizedString(locale, "images.managementtool.buttons.importRepository"), getLocalizedString(locale, "tool.managementtool.importRepository.header")));	
		
		return buttons;
	}
	
	private List<ToolbarButton> getRepositoryDetailsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteRepository.header&yesDestination=" + URLEncoder.encode("DeleteRepository.action?repositoryId=" + primaryKey, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListRepository.action?title=Repositories", "UTF-8") + "&message=tool.managementtool.deleteRepository.text&extraParameters=" + extraParameters, getLocalizedString(locale, "images.managementtool.buttons.deleteRepository"), "tool.managementtool.deleteRepository.header"));
		buttons.add(new ToolbarButton(true, "javascript:openPopup('ExportRepository!input.action?repositoryId=" + primaryKey + "', 'Export', 'width=600,height=500,resizable=no,scrollbars=yes');", getLocalizedString(locale, "images.managementtool.buttons.exportRepository"), getLocalizedString(locale, "tool.managementtool.exportRepository.header")));	
		buttons.add(new ToolbarButton("ViewRepositoryProperties.action?repositoryId=" + primaryKey, getLocalizedString(locale, "images.global.buttons.editProperties"), "Edit Properties", new Integer(22), new Integer(80)));
		
		String returnAddress = URLEncoder.encode(URLEncoder.encode("ViewRepository.action?repositoryId=" + primaryKey, "UTF-8"), "UTF-8");
		buttons.add(new ToolbarButton("ViewAccessRights.action?interceptionPointCategory=Repository&extraParameters=" + primaryKey +"&colorScheme=ManagementTool&returnAddress=" + returnAddress, getLocalizedString(locale, "images.managementtool.buttons.accessRights"), "tool.managementtool.accessRights.header"));
		buttons.add(new ToolbarButton("ViewListRepositoryLanguage.action?repositoryId=" + primaryKey +"&returnAddress=" + returnAddress, getLocalizedString(locale, "images.managementtool.buttons.repositoryLanguages"), "tool.managementtool.repositoryLanguages.header"));
		
		buttons.add(new ToolbarButton(true, "javascript:openPopup('RebuildRegistry!input.action?repositoryId=" + primaryKey + "', 'Registry', 'width=400,height=200,resizable=no');", getLocalizedString(locale, "images.managementtool.buttons.rebuildRegistry"), getLocalizedString(locale, "tool.managementtool.rebuildRegistry.header")));	
		
		return buttons;				
	}

	private List<ToolbarButton> getAvailablePackagesButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("RefreshUpdates.action", getLocalizedString(locale, "images.managementtool.buttons.refreshUpdates"), "Refresh Updates"));	
		buttons.add(new ToolbarButton(true, "javascript:submitListForm('updatePackage');", getLocalizedString(locale, "images.managementtool.buttons.installUpdate"), "Install update"));
		return buttons;
	}
	
	private List<ToolbarButton> getSystemUsersButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		if(UserControllerProxy.getController().getSupportCreate())
			buttons.add(new ToolbarButton("CreateSystemUser!input.action", getLocalizedString(locale, "images.managementtool.buttons.newSystemUser"), "New System User"));	
		//if(UserControllerProxy.getController().getSupportDelete())
		//	buttons.add(new ToolbarButton(true, "javascript:submitListFormWithPrimaryKey('systemUser', 'userName');", getLocalizedString(locale, "images.managementtool.buttons.deleteSystemUser"), "tool.managementtool.deleteSystemUsers.header"));
		buttons.add(new ToolbarButton(true, "javascript:toggleSearchForm();", getLocalizedString(locale, "images.managementtool.buttons.searchButton"), "Search Form"));
		return buttons;
	}

	private List<ToolbarButton> getSystemUserDetailsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		if(!this.primaryKey.equals(CmsPropertyHandler.getAnonymousUser()))
		{
			InfoGluePrincipal user = UserControllerProxy.getController().getUser(primaryKey);
			if(user.getAutorizationModule().getSupportDelete())
				buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteSystemUser.header&yesDestination=" + URLEncoder.encode("DeleteSystemUser.action?userName=" + URLEncoder.encode(primaryKey, URIEncoding), URIEncoding) + "&noDestination=" + URLEncoder.encode("ViewListSystemUser.action?title=SystemUsers", URIEncoding) + "&message=tool.managementtool.deleteSystemUser.text&extraParameters=" + URLEncoder.encode(primaryKey, URIEncoding), getLocalizedString(locale, "images.managementtool.buttons.deleteSystemUser"), "tool.managementtool.deleteSystemUser.header"));
		
			if(user.getAutorizationModule().getSupportUpdate())
				buttons.add(new ToolbarButton("UpdateSystemUserPassword!input.action?userName=" + URLEncoder.encode(URLEncoder.encode(primaryKey, URIEncoding), URIEncoding), getLocalizedString(locale, "images.managementtool.buttons.updateSystemUserPassword"), "Update user password"));
		}
		
		List<ToolbarButton> contentTypeDefinitionVOList<ToolbarButton> = UserPropertiesController.getController().getContentTypeDefinitionVOList(primaryKey);
		if(contentTypeDefinitionVOList.size() > 0)
			buttons.add(new ToolbarButton("ViewUserProperties.action?userName=" + URLEncoder.encode(URLEncoder.encode(primaryKey, URIEncoding), URIEncoding), getLocalizedString(locale, "images.managementtool.buttons.viewSystemUserProperties"), "View User Properties"));
		
		if(principal.getIsAdministrator())
			buttons.add(new ToolbarButton("AuthorizationSwitchManagement!inputUser.action?userName=" + URLEncoder.encode(URLEncoder.encode(primaryKey, URIEncoding)), getLocalizedString(locale, "images.managementtool.buttons.transferUserAccessRights"), "Transfer Users Access Rights"));

		return buttons;				
	}
	*/
	
	private List<ToolbarButton> getRolesButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		if(UserControllerProxy.getController().getSupportCreate())
			buttons.add(new ToolbarButton(getLocalizedString(locale, "tool.managementtool.createRole.header"), 
										  getLocalizedString(locale, "tool.managementtool.createRole.header"),
										  "CreateRole!inputV3.action",
										  "images/v3/createBackgroundPenPaper.gif"));
		
		return buttons;
	}
	
	/*
	private List<ToolbarButton> getRoleDetailsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		
		String yesDestination 	= URLEncoder.encode("DeleteRole.action?roleName=" + URLEncoder.encode(this.primaryKey, URIEncoding), URIEncoding);
		String noDestination  	= URLEncoder.encode("ViewListRole.action?title=Roles", URIEncoding);
		String message 		 	= URLEncoder.encode("Do you really want to delete the role " + URLEncoder.encode(this.primaryKey, URIEncoding), URIEncoding);
		
		InfoGlueRole role = RoleControllerProxy.getController().getRole(this.primaryKey);
		if(role.getAutorizationModule().getSupportDelete())
			buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteRole.header&yesDestination=" + yesDestination + "&noDestination=" + noDestination + "&message=tool.managementtool.deleteRole.text&extraParameters=" + URLEncoder.encode(primaryKey, URIEncoding), getLocalizedString(locale, "images.managementtool.buttons.deleteRole"), "tool.managementtool.deleteRole.header"));
		
		List<ToolbarButton> contentTypeDefinitionVOList<ToolbarButton> = RolePropertiesController.getController().getContentTypeDefinitionVOList(this.primaryKey);
		if(contentTypeDefinitionVOList.size() > 0)
			buttons.add(new ToolbarButton("ViewRoleProperties.action?roleName=" + URLEncoder.encode(URLEncoder.encode(this.primaryKey, URIEncoding)), getLocalizedString(locale, "images.managementtool.buttons.viewRoleProperties"), "View Role Properties"));
		
		if(principal.getIsAdministrator())
			buttons.add(new ToolbarButton("AuthorizationSwitchManagement!inputRole.action?roleName=" + URLEncoder.encode(URLEncoder.encode(this.primaryKey, URIEncoding)), getLocalizedString(locale, "images.managementtool.buttons.transferRoleAccessRights"), "Transfer Roles Access Rights"));
		
		boolean hasAccessToManageAllAccessRights = hasAccessTo(principal, "Role.ManageAllAccessRights", true);
		boolean hasAccessToManageAccessRights = hasAccessTo(principal, "Role.ManageAccessRights", "" + this.primaryKey);
		if(hasAccessToManageAllAccessRights || hasAccessToManageAccessRights)
			buttons.add(new ToolbarButton("ViewAccessRights.action?interceptionPointCategory=Role&extraParameters=" + URLEncoder.encode(this.primaryKey, URIEncoding) + "&returnAddress=ViewRole.action?roleName=" + URLEncoder.encode(primaryKey, URIEncoding) + "&colorScheme=ManagementTool", getLocalizedString(locale, "images.managementtool.buttons.accessRights"), "Role Access Rights"));

		return buttons;				
	}
	
	private List<ToolbarButton> getGroupsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		if(UserControllerProxy.getController().getSupportCreate())
			buttons.add(new ToolbarButton("CreateGroup!input.action", getLocalizedString(locale, "images.managementtool.buttons.newGroup"), "New Group"));	
		//if(UserControllerProxy.getController().getSupportDelete())
		//	buttons.add(new ToolbarButton(true, "javascript:submitListFormWithPrimaryKey('group', 'groupName');", getLocalizedString(locale, "images.managementtool.buttons.deleteGroup"), "tool.managementtool.deleteGroups.header"));
		
		return buttons;
	}
	
	private List<ToolbarButton> getGroupDetailsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		
		String yesDestination 	= URLEncoder.encode("DeleteGroup.action?groupName=" + URLEncoder.encode(this.primaryKey, URIEncoding), URIEncoding);
		String noDestination  	= URLEncoder.encode("ViewListGroup.action?title=Groups", URIEncoding);
		String message 		 	= URLEncoder.encode("Do you really want to delete the group " + URLEncoder.encode(this.primaryKey, URIEncoding), URIEncoding);
		
		InfoGlueGroup group = GroupControllerProxy.getController().getGroup(this.primaryKey);
		if(group.getAutorizationModule().getSupportDelete())
			buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteGroup.header&yesDestination=" + yesDestination + "&noDestination=" + noDestination + "&message=tool.managementtool.deleteGroup.text&extraParameters=" + URLEncoder.encode(primaryKey, URIEncoding), getLocalizedString(locale, "images.managementtool.buttons.deleteGroup"), "tool.managementtool.deleteGroup.header"));
		
		List<ToolbarButton> contentTypeDefinitionVOList<ToolbarButton> = GroupPropertiesController.getController().getContentTypeDefinitionVOList(this.primaryKey);
		if(contentTypeDefinitionVOList.size() > 0)
			buttons.add(new ToolbarButton("ViewGroupProperties.action?groupName=" + URLEncoder.encode(URLEncoder.encode(this.primaryKey, URIEncoding)), getLocalizedString(locale, "images.managementtool.buttons.viewGroupProperties"), "View Group Properties"));
		
		if(principal.getIsAdministrator())
			buttons.add(new ToolbarButton("AuthorizationSwitchManagement!inputGroup.action?groupName=" + URLEncoder.encode(URLEncoder.encode(this.primaryKey, URIEncoding)), getLocalizedString(locale, "images.managementtool.buttons.transferGroupAccessRights"), "Transfer Groups Access Rights"));
				
		boolean hasAccessToManageAllAccessRights = hasAccessTo(principal, "Group.ManageAllAccessRights", true);
		boolean hasAccessToManageAccessRights = hasAccessTo(principal, "Group.ManageAccessRights", "" + this.primaryKey);
		if(hasAccessToManageAllAccessRights || hasAccessToManageAccessRights)
			buttons.add(new ToolbarButton("ViewAccessRights.action?interceptionPointCategory=Group&extraParameters=" + URLEncoder.encode(primaryKey, URIEncoding) + "&returnAddress=ViewGroup.action?groupName=" + URLEncoder.encode(primaryKey, URIEncoding) + "&colorScheme=ManagementTool", getLocalizedString(locale, "images.managementtool.buttons.accessRights"), "Group Access Rights"));

		return buttons;				
	}

	private List<ToolbarButton> getLanguagesButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("CreateLanguage!input.action", getLocalizedString(locale, "images.managementtool.buttons.newLanguage"), "New Language"));	
		buttons.add(new ToolbarButton(true, "javascript:submitListForm('language');", getLocalizedString(locale, "images.managementtool.buttons.deleteLanguage"), "tool.managementtool.deleteLanguages.header"));
		return buttons;
	}
	
	private List<ToolbarButton> getLanguageDetailsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		String name = LanguageController.getController().getLanguageVOWithId(this.primaryKeyAsInteger).getName();
		buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteLanguage.header&yesDestination=" + URLEncoder.encode("DeleteLanguage.action?languageId=" + primaryKeyAsInteger, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListLanguage.action?title=Languages", "UTF-8") + "&message=tool.managementtool.deleteLanguage.text&extraParameters=" + this.extraParameters, getLocalizedString(locale, "images.managementtool.buttons.deleteLanguage"), "tool.managementtool.deleteLanguage.header"));
		return buttons;				
	}

	private List<ToolbarButton> getInterceptionPointsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("CreateInterceptionPoint!input.action", getLocalizedString(locale, "images.managementtool.buttons.newInterceptionPoint"), "New InterceptionPoint"));	
		buttons.add(new ToolbarButton(true, "javascript:submitListForm('interceptionPoint');", getLocalizedString(locale, "images.managementtool.buttons.deleteInterceptionPoint"), "tool.managementtool.deleteInterceptionPoints.header"));
		return buttons;
	}
	
	private List<ToolbarButton> getInterceptionPointButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		InterceptionPointVO interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithId(this.primaryKeyAsInteger);
		String name = interceptionPointVO.getName();
		buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteInterceptionPoint.header&yesDestination=" + URLEncoder.encode("DeleteInterceptionPoint.action?interceptionPointId=" + primaryKeyAsInteger, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListInterceptionPoint.action?title=InterceptionPoints", "UTF-8") + "&message=tool.managementtool.deleteInterceptionPoint.text&extraParameters=" + this.extraParameters, getLocalizedString(locale, "images.managementtool.buttons.deleteInterceptionPoint"), "tool.managementtool.deleteInterceptionPoint.header"));
		if(interceptionPointVO.getUsesExtraDataForAccessControl().booleanValue() == false)
			buttons.add(new ToolbarButton("ViewAccessRights.action?interceptionPointCategory=" + interceptionPointVO.getCategory() + "&interceptionPointId=" + primaryKeyAsInteger + "&returnAddress=ViewInterceptionPoint.action?interceptionPointId=" + primaryKeyAsInteger + "&colorScheme=ManagementTool", getLocalizedString(locale, "images.managementtool.buttons.accessRights"), "InterceptionPoint Access Rights"));
		
		return buttons;				
	}

	private List<ToolbarButton> getInterceptorsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("CreateInterceptor!input.action", getLocalizedString(locale, "images.managementtool.buttons.newInterceptor"), "New Interceptor"));	
		buttons.add(new ToolbarButton(true, "javascript:submitListForm('interceptor');", getLocalizedString(locale, "images.managementtool.buttons.deleteInterceptor"), "tool.managementtool.deleteInterceptors.header"));
		return buttons;
	}
	
	private List<ToolbarButton> getInterceptorButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		String name = InterceptorController.getController().getInterceptorVOWithId(primaryKeyAsInteger).getName();
		buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteInterceptor.header&yesDestination=" + URLEncoder.encode("DeleteInterceptor.action?interceptorId=" + primaryKeyAsInteger, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListInterceptor.action?title=Interceptors", "UTF-8") + "&message=tool.managementtool.deleteInterceptor.text&extraParameters=" + this.extraParameters, getLocalizedString(locale, "images.managementtool.buttons.deleteInterceptor"), "tool.managementtool.deleteInterceptor.header"));
		return buttons;				
	}

	private List<ToolbarButton> getServiceDefinitionsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("CreateServiceDefinition!input.action", getLocalizedString(locale, "images.managementtool.buttons.newServiceDefinition"), "New ServiceDefinition"));	
		buttons.add(new ToolbarButton(true, "javascript:submitListForm('serviceDefinition');", getLocalizedString(locale, "images.managementtool.buttons.deleteServiceDefinition"), "tool.managementtool.deleteServiceDefinitions.header"));
		return buttons;
	}
	
	private List<ToolbarButton> getServiceDefinitionDetailsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		String name = ServiceDefinitionController.getController().getServiceDefinitionVOWithId(primaryKeyAsInteger).getName();
		buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteServiceDefinition.header&yesDestination=" + URLEncoder.encode("DeleteServiceDefinition.action?serviceDefinitionId=" + primaryKeyAsInteger, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListServiceDefinition.action?title=ServiceDefinitions", "UTF-8") + "&message=tool.managementtool.deleteServiceDefinition.text&extraParameters=" + this.extraParameters, getLocalizedString(locale, "images.managementtool.buttons.deleteServiceDefinition"), "tool.managementtool.deleteServiceDefinition.header"));
		return buttons;				
	}

	private List<ToolbarButton> getAvailableServiceBindingsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("CreateAvailableServiceBinding!input.action", getLocalizedString(locale, "images.managementtool.buttons.newAvailableServiceBinding"), "New AvailableServiceBinding"));	
		buttons.add(new ToolbarButton(true, "javascript:submitListForm('availableServiceBinding');", getLocalizedString(locale, "images.managementtool.buttons.deleteAvailableServiceBinding"), "tool.managementtool.deleteAvailableServiceBindings.header"));
		return buttons;
	}
	
	private List<ToolbarButton> getAvailableServiceBindingDetailsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		String name = AvailableServiceBindingController.getController().getAvailableServiceBindingVOWithId(this.primaryKeyAsInteger).getName();
		buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteAvailableServiceBinding.header&yesDestination=" + URLEncoder.encode("DeleteAvailableServiceBinding.action?availableServiceBindingId=" + primaryKeyAsInteger, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListAvailableServiceBinding.action?title=AvailableServiceBindings", "UTF-8") + "&message=tool.managementtool.deleteAvailableServiceBinding.text&extraParameters=" + this.extraParameters, getLocalizedString(locale, "images.managementtool.buttons.deleteAvailableServiceBinding"), "tool.managementtool.deleteAvailableServiceBinding.header"));
		return buttons;				
	}

	private List<ToolbarButton> getSiteNodeTypeDefinitionsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("CreateSiteNodeTypeDefinition!input.action", getLocalizedString(locale, "images.managementtool.buttons.newSiteNodeTypeDefinition"), "New SiteNodeTypeDefinition"));	
		buttons.add(new ToolbarButton(true, "javascript:submitListForm('siteNodeTypeDefinition');", getLocalizedString(locale, "images.managementtool.buttons.deleteSiteNodeTypeDefinition"), "tool.managementtool.deleteSiteNodeTypeDefinitions.header"));
		return buttons;
	}
	
	private List<ToolbarButton> getSiteNodeTypeDefinitionDetailsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		String name = SiteNodeTypeDefinitionController.getController().getSiteNodeTypeDefinitionVOWithId(this.primaryKeyAsInteger).getName();
		buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteSiteNodeTypeDefinition.header&yesDestination=" + URLEncoder.encode("DeleteSiteNodeTypeDefinition.action?siteNodeTypeDefinitionId=" + primaryKeyAsInteger, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListSiteNodeTypeDefinition.action?title=SiteNodeTypeDefinitions", "UTF-8") + "&message=tool.managementtool.deleteSiteNodeTypeDefinition.text&extraParameters=" + this.extraParameters, getLocalizedString(locale, "images.managementtool.buttons.deleteSiteNodeTypeDefinition"), "tool.managementtool.deleteSiteNodeTypeDefinition.header"));
		return buttons;				
	}


	private List<ToolbarButton> getContentTypeDefinitionsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("CreateContentTypeDefinition!input.action", getLocalizedString(locale, "images.managementtool.buttons.newContentTypeDefinition"), "New ContentTypeDefinition"));	
		buttons.add(new ToolbarButton(true, "javascript:submitListForm('contentTypeDefinition');", getLocalizedString(locale, "images.managementtool.buttons.deleteContentTypeDefinition"), "tool.managementtool.deleteContentTypeDefinitions.header"));
		return buttons;
	}
	
	private List<ToolbarButton> getContentTypeDefinitionDetailsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		String name = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithId(this.primaryKeyAsInteger).getName();
		buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteContentTypeDefinition.header&yesDestination=" + URLEncoder.encode("DeleteContentTypeDefinition.action?contentTypeDefinitionId=" + primaryKeyAsInteger, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListContentTypeDefinition.action?title=ContentTypeDefinitions", "UTF-8") + "&message=tool.managementtool.deleteContentTypeDefinition.text&extraParameters=" + this.extraParameters, getLocalizedString(locale, "images.managementtool.buttons.deleteContentTypeDefinition"), "tool.managementtool.deleteContentTypeDefinition.header"));
		
		String protectContentTypes = CmsPropertyHandler.getProtectContentTypes();
		if(protectContentTypes != null && protectContentTypes.equalsIgnoreCase("true"))
		{
			String returnAddress = URLEncoder.encode(URLEncoder.encode("ViewContentTypeDefinition.action?contentTypeDefinitionId=" + this.primaryKey, "UTF-8"), "UTF-8");
			buttons.add(getAccessRightsButton("ContentTypeDefinition", this.primaryKey, returnAddress));
		}
		
		return buttons;				
	}

	private List<ToolbarButton> getCategoryButtons() throws Exception
	{
	    String url = "CategoryManagement!new.action";
		if(primaryKeyAsInteger != null)
			url += "?model/parentId=" + primaryKeyAsInteger;

		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton(url, getLocalizedString(locale, "images.managementtool.buttons.newCategory"), "New Category"));

		if(primaryKeyAsInteger != null)
			buttons.add(new ToolbarButton(true, "javascript:openPopup('CategoryManagement!displayTreeForMove.action?categoryId=" + this.primaryKey + "', 'Category', 'width=400,height=600,resizable=no,status=yes');", getLocalizedString(locale, "images.managementtool.buttons.moveCategory"), "Move Category"));

		buttons.add(new ToolbarButton(true, "javascript:submitListForm('category');", getLocalizedString(locale, "images.managementtool.buttons.deleteCategory"), "Delete Category"));
		
		if(primaryKeyAsInteger != null)
		{	
		    String returnAddress = URLEncoder.encode(URLEncoder.encode("CategoryManagement!edit.action?categoryId=" + this.primaryKey + "&title=Category%20Details", "UTF-8"), "UTF-8");
		    buttons.add(getAccessRightsButton("Category", this.primaryKey, returnAddress));
		}
		
		return buttons;
	}
	
	private ToolbarButton getAccessRightsButton(String interceptionPointCategory, String extraParameter, String returnAddress) throws Exception
	{
		return new ToolbarButton("ViewAccessRights.action?interceptionPointCategory=" + interceptionPointCategory + "&extraParameters=" + extraParameter +"&colorScheme=ManagementTool&returnAddress=" + returnAddress, getLocalizedString(locale, "images.managementtool.buttons.accessRights"), "tool.managementtool.accessRights.header");
	}

	private List<ToolbarButton> getRedirectsButtons() throws Exception
	{
		
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("CreateRedirect!input.action", getLocalizedString(locale, "images.managementtool.buttons.newRedirect"), "New Redirect"));	
		buttons.add(new ToolbarButton(true, "javascript:submitListForm('redirect');", getLocalizedString(locale, "images.managementtool.buttons.deleteRedirect"), "tool.managementtool.deleteRedirects.header"));
		return buttons;
	}
	
	private List<ToolbarButton> getRedirectDetailsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		String name = RedirectController.getController().getRedirectVOWithId(this.primaryKeyAsInteger).getUrl();
		buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteRedirect.header&yesDestination=" + URLEncoder.encode("DeleteRedirect.action?redirectId=" + primaryKeyAsInteger, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListWorkflowDefinition.action", "UTF-8") + "&message=tool.managementtool.deleteWorkflowDefinition.text&extraParameters=" + this.extraParameters, getLocalizedString(locale, "images.managementtool.buttons.deleteWorkflowDefinition"), "tool.managementtool.deleteWorkflowDefinition.header"));
		return buttons;				
	}

	private List<ToolbarButton> getPortletsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("UploadPortlet.action", getLocalizedString(locale, "images.managementtool.buttons.newPortlet"), "New Portlet"));	
		//buttons.add(new ToolbarButton(true, "javascript:submitListForm('workflowDefinition');", getLocalizedString(locale, "images.managementtool.buttons.deleteWorkflowDefinition"), "tool.managementtool.deleteWorkflowDefinitions.header"));
		return buttons;
	}

	
	private List<ToolbarButton> getWorkflowDefinitionsButtons() throws Exception
	{
		
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("CreateWorkflowDefinition!input.action", getLocalizedString(locale, "images.managementtool.buttons.newWorkflowDefinition"), "New WorkflowDefinition"));	
		buttons.add(new ToolbarButton(true, "javascript:submitListForm('workflowDefinition');", getLocalizedString(locale, "images.managementtool.buttons.deleteWorkflowDefinition"), "tool.managementtool.deleteWorkflowDefinitions.header"));
		return buttons;
	}
	
	private List<ToolbarButton> getWorkflowDefinitionDetailsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		String name = WorkflowDefinitionController.getController().getWorkflowDefinitionVOWithId(this.primaryKeyAsInteger).getName();
		buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteWorkflowDefinition.header&yesDestination=" + URLEncoder.encode("DeleteWorkflowDefinition.action?workflowDefinitionId=" + primaryKeyAsInteger, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListWorkflowDefinition.action", "UTF-8") + "&message=tool.managementtool.deleteWorkflowDefinition.text&extraParameters=" + this.extraParameters, getLocalizedString(locale, "images.managementtool.buttons.deleteWorkflowDefinition"), "tool.managementtool.deleteWorkflowDefinition.header"));
	    final String protectWorkflows = CmsPropertyHandler.getProtectWorkflows();
	    if(protectWorkflows != null && protectWorkflows.equalsIgnoreCase("true"))
	    {
			String returnAddress = URLEncoder.encode(URLEncoder.encode("ViewWorkflowDefinition.action?workflowDefinitionId=" + this.primaryKey, "UTF-8"), "UTF-8");
			final WorkflowDefinitionVO workflowDefinition = WorkflowDefinitionController.getController().getWorkflowDefinitionVOWithId(this.primaryKeyAsInteger);
			buttons.add(new ToolbarButton("ViewAccessRights.action?interceptionPointCategory=Workflow&extraParameters=" + workflowDefinition.getName() +"&colorScheme=ManagementTool&returnAddress=" + returnAddress, getLocalizedString(locale, "images.managementtool.buttons.accessRights"), "tool.managementtool.accessRights.header"));
	    }
		return buttons;				
	}

	private List<ToolbarButton> getServerNodesButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("CreateServerNode!input.action", getLocalizedString(locale, "images.managementtool.buttons.newServerNode"), "tool.managementtool.createServerNode.header"));	
		buttons.add(new ToolbarButton(true, "javascript:submitListForm('serverNode');", getLocalizedString(locale, "images.managementtool.buttons.deleteServerNode"), "tool.managementtool.deleteServerNodes.header"));
		buttons.add(new ToolbarButton("ViewServerNodeProperties.action?serverNodeId=-1", getLocalizedString(locale, "images.global.buttons.editProperties"), "Edit Properties", new Integer(22), new Integer(80)));
		
		return buttons;
	}
	
	private List<ToolbarButton> getServerNodeDetailsButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("Confirm.action?header=tool.managementtool.deleteServerNode.header&yesDestination=" + URLEncoder.encode("DeleteServerNode.action?serverNodeId=" + this.primaryKey, "UTF-8") + "&noDestination=" + URLEncoder.encode("ViewListServerNode.action?title=ServerNodes", "UTF-8") + "&message=tool.managementtool.deleteServerNode.text&extraParameters=" + this.extraParameters, getLocalizedString(locale, "images.managementtool.buttons.deleteServerNode"), "tool.managementtool.deleteServerNode.header"));
		buttons.add(new ToolbarButton("ViewServerNodeProperties.action?serverNodeId=" + this.primaryKey, getLocalizedString(locale, "images.global.buttons.editProperties"), "Edit Properties", new Integer(22), new Integer(80)));
		
		return buttons;				
	}

	private List<ToolbarButton> getMessageCenterButtons() throws Exception
	{
		List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
		buttons.add(new ToolbarButton("CreateEmail!inputChooseRecipients.action", getLocalizedString(locale, "images.managementtool.buttons.newEmail"), "tool.managementtool.createEmail.header"));
		
		return buttons;
	}
	*/
	
	/**
	 * Used by the view pages to determine if the current user has sufficient access rights
	 * to perform the action specific by the interception point name.
	 *
	 * @param interceptionPointName THe Name of the interception point to check access rights
	 * @return True is access is allowed, false otherwise
	 */
	public boolean hasAccessTo(InfoGluePrincipal principal, String interceptionPointName, boolean returnSuccessIfInterceptionPointNotDefined)
	{
		logger.info("Checking if " + principal.getName() + " has access to " + interceptionPointName);

		try
		{
			return AccessRightController.getController().getIsPrincipalAuthorized(principal, interceptionPointName, returnSuccessIfInterceptionPointNotDefined);
		}
		catch (SystemException e)
		{
		    logger.warn("Error checking access rights", e);
			return false;
		}
	}

	/**
	 * Used by the view pages to determine if the current user has sufficient access rights
	 * to perform the action specific by the interception point name.
	 *
	 * @param interceptionPointName THe Name of the interception point to check access rights
	 * @return True is access is allowed, false otherwise
	 */
	public boolean hasAccessTo(InfoGluePrincipal principal, String interceptionPointName, String extraParameter)
	{
		logger.info("Checking if " + principal.getName() + " has access to " + interceptionPointName + " with extraParameter " + extraParameter);

		try
		{
		    return AccessRightController.getController().getIsPrincipalAuthorized(principal, interceptionPointName, extraParameter);
		}
		catch (SystemException e)
		{
		    logger.warn("Error checking access rights", e);
			return false;
		}
	}
	
	public String getLocalizedString(Locale locale, String key) 
  	{
    	StringManager stringManager = StringManagerFactory.getPresentationStringManager("org.infoglue.cms.applications", locale);

    	return stringManager.getString(key);
  	}
}
