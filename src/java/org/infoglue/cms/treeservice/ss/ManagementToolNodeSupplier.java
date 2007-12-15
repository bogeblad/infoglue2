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

package org.infoglue.cms.treeservice.ss;

import java.util.ArrayList;
import java.util.Collection;

import org.infoglue.cms.controllers.kernel.impl.simple.AccessRightController;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;

import com.frovi.ss.Tree.BaseNodeSupplier;

/**
 * ContentNodeSupplier.java
 * Created on 2002-sep-30 
 * @author Stefan Sik, ss@frovi.com 
 * 
 * Provides tree menu for the management tool
 * 
 */
public class ManagementToolNodeSupplier extends BaseNodeSupplier
{

	private boolean showLeafs = true;
	private InfoGluePrincipal infogluePrincipal = null;

	public ManagementToolNodeSupplier(Integer repositoryId, InfoGluePrincipal infogluePrincipal) throws SystemException
	{
		this.infogluePrincipal = infogluePrincipal;
		
		if (repositoryId.intValue() == 0)
			setRootNode(new ManagementNodeImpl(repositoryId,"root", "ViewManagementToolStartPage.action"));
		else
			setRootNode(new ManagementNodeImpl(repositoryId,"root", "ViewRepository.action?repositoryId=" + repositoryId));
	}
	
	/**
	 * @see com.frovi.ss.Tree.BaseNodeSupplier#hasChildren()
	 */
	public boolean hasChildren()
	{
		if (showLeafs)
			return false;
		else
			return true;
	}

	
	/**
	 * @see com.frovi.ss.Tree.INodeSupplier#getChildContainerNodes(Integer)
	 */
	public Collection getChildContainerNodes(Integer parentNode)
	{
		int cnt = 1;
		ArrayList r = new ArrayList();
		ManagementNodeImpl node;
				
		if (parentNode.intValue() == 0)	
		{	
			if(hasAccessTo("ManagementToolMenu.Repositories", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Repositories", "ViewListRepository.action?title=Repositories"));

			if(hasAccessTo("ManagementToolMenu.SystemUsers", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "SystemUsers", "ViewListSystemUser.action?title=SystemUsers"));
			
			if(hasAccessTo("ManagementToolMenu.Roles", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Roles", "ViewListRole.action?title=Roles"));
			
			if(hasAccessTo("ManagementToolMenu.Groups", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Groups", "ViewListGroup.action?title=Groups"));
			
			if(hasAccessTo("ManagementToolMenu.Languages", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Languages", "ViewListLanguage.action?title=Languages"));

			if(hasAccessTo("ManagementToolMenu.InterceptionPoints", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "InterceptionPoints", "ViewListInterceptionPoint.action?title=InterceptionPoints"));

			if(hasAccessTo("ManagementToolMenu.Interceptors", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Interceptors", "ViewListInterceptor.action?title=Interceptors"));
			
			if(hasAccessTo("ManagementToolMenu.ServiceDefinitions", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "ServiceDefinitions", "ViewListServiceDefinition.action?title=ServiceDefinitions"));
			
			if(hasAccessTo("ManagementToolMenu.AvailableServiceBindings", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "AvailableServiceBindings", "ViewListAvailableServiceBinding.action?title=AvailableServiceBindings"));
			
			if(hasAccessTo("ManagementToolMenu.SiteNodeTypeDefinitions", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "SiteNodeTypeDefinitions", "ViewListSiteNodeTypeDefinition.action?title=SiteNodeTypeDefinitions"));
			
			if(hasAccessTo("ManagementToolMenu.Categories", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Categories", "CategoryManagement!list.action?title=ContentCategories"));
			
			if(hasAccessTo("ManagementToolMenu.ContentTypeDefinitions", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "ContentTypeDefinitions", "ViewListContentTypeDefinition.action?title=ContentTypeDefinitions"));
			
			//if(hasAccessTo("ManagementToolMenu.Languages", infogluePrincipal, true))
			if(hasAccessTo("ManagementToolMenu.TransactionHistory", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "TransactionHistory", "ViewListTransactionHistory.action?title=TransactionHistory"));
			
			if(hasAccessTo("ManagementToolMenu.Up2Date", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Up2Date", "ViewListUp2Date.action?title=InfoGlue Up2Date"));
			
			if(hasAccessTo("ManagementToolMenu.Workflows", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Workflows", "ViewListWorkflowDefinition.action"));
			
			if(hasAccessTo("ManagementToolMenu.Portlets", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Portlets", "ViewListPortlet.action"));
			
			if(hasAccessTo("ManagementToolMenu.Redirects", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Redirects", "ViewListRedirect.action"));
			
			if(hasAccessTo("ManagementToolMenu.ApplicationSettings", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Application settings", "ViewListServerNode.action"));
			
			if(hasAccessTo("ManagementToolMenu.MessageCenter", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Message center", "ViewMessageCenter.action"));
			
			if(hasAccessTo("ManagementToolMenu.Diagnostics", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "Diagnostics and status", "ViewDiagnosticCenter.action"));
			
			if(hasAccessTo("ManagementToolMenu.SystemTools", infogluePrincipal, true))
				r.add(new ManagementNodeImpl(cnt++, "System tools", "ViewSystemTools.action"));
		}
		/*else if(parentNode.intValue() > 100 || parentNode.intValue() < 0)
		{
			if(parentNode.intValue() > 100)
				return getExtranetRoleChildren(null);
			else
				return getExtranetRoleChildren(new Integer(-parentNode.intValue()));
		}*/
		else
		{
			//r.add(new ManagementNodeImpl(cnt++, "Permissions", "ViewPermission.action?repositoryId=" + parentNode +"&title=Permissions"));
			r.add(new ManagementNodeImpl(cnt++, "Permissions", "ViewAccessRights.action?interceptionPointCategory=Repository&extraParameters=" + parentNode +"&colorScheme=ManagementTool&returnAddress=ViewRepositoryOverview.action?repositoryId=" + parentNode));
			r.add(new ManagementNodeImpl(cnt++, "Languages", "ViewListRepositoryLanguage.action?repositoryId=" + parentNode + "&title=Languages"));			
		}
		
		return r;
	}

	/**
	 * @see com.frovi.ss.Tree.INodeSupplier#getChildLeafNodes(Integer)
	 */
	public Collection getChildLeafNodes(Integer parentNode)
	{
		ArrayList ret = new ArrayList();
		return ret;
	}

	/**
	 * Gets the children extranet roles
	 * @author Mattias Bogeblad
	 */
/*
	private List getExtranetRoleChildren(Integer parentExtranetRoleId)
	{
		List children = new ArrayList();
		
		List childrenList = null;
		try
		{
			childrenList = ExtranetRoleController.getController().getExtranetRoleChildren(parentExtranetRoleId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
		if(childrenList != null)
		{
			Iterator i = childrenList.iterator();
			while(i.hasNext())
			{
				ExtranetRoleVO vo = (ExtranetRoleVO) i.next();
				//if (vo.getIsBranch().booleanValue())
				//{
					BaseNode node =  new ManagementNodeImpl(vo.getId(), vo.getName(), "ViewListExtranetRole.action?title=ExtranetRoles&parentExtranetRoleId=" + vo.getId());
					node.setId(new Integer(-vo.getId().intValue()));
					node.setContainer(true);
				
					node.setChildren((vo.getChildCount().intValue() > 0)); // 
				
					node.setTitle(vo.getName());
				
					children.add(node);
				//}
				//else
				//{
				//	if (showLeafs)
				//	{
				//		BaseNode node =  new ContentNodeImpl();
				//		node.setId(vo.getId());
				//		node.setContainer(false);
				//		node.setTitle(vo.getName());
				//	
				//		cacheLeafs.add(node);				
				//	}
				//}
			
			}
		}
		
		return children;
	}
	*/
}
