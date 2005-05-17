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

package org.infoglue.cms.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.exolab.castor.jdo.Database;
import org.infoglue.cms.controllers.kernel.impl.simple.BaseController;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.GroupController;
import org.infoglue.cms.controllers.kernel.impl.simple.RoleController;
import org.infoglue.cms.controllers.kernel.impl.simple.SystemUserController;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.Group;
import org.infoglue.cms.entities.management.GroupVO;
import org.infoglue.cms.entities.management.Role;
import org.infoglue.cms.entities.management.RoleVO;
import org.infoglue.cms.entities.management.SystemUser;
import org.infoglue.cms.entities.management.SystemUserVO;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;

/**
 * @author Mattias Bogeblad
 *
 * This authentication module authenticates an user against the ordinary infoglue database.
 */

public class InfoGlueBasicAuthorizationModule extends BaseController implements AuthorizationModule
{
	private Properties extraProperties = null;
	private Database transactionObject 	= null;
	
	/**
	 * Gets is the implementing class can update as well as read 
	 */
	
	public boolean getSupportUpdate() 
	{
		return true;
	}

	/**
	 * Gets is the implementing class can delete as well as read 
	 */
	
	public boolean getSupportDelete()
	{
		return true;
	}
	
	/**
	 * Gets is the implementing class can create as well as read 
	 */
	
	public boolean getSupportCreate()
	{
		return true;
	}

	/**
	 * Gets an authorized InfoGluePrincipal. If the user has logged in with the root-account
	 * we immediately return - otherwise we populate it.
	 */
	
	public InfoGluePrincipal getAuthorizedInfoGluePrincipal(String userName) throws Exception
	{
		InfoGluePrincipal infogluePrincipal = null;
		
		String administratorUserName = CmsPropertyHandler.getProperty("administratorUserName");
		String administratorEmail 	 = CmsPropertyHandler.getProperty("administratorEmail");
		
		final boolean isAdministrator = (userName != null && userName.equalsIgnoreCase(administratorUserName)) ? true : false;
		if(isAdministrator)
		{
			infogluePrincipal = new InfoGluePrincipal(userName, "System", "Administrator", administratorEmail, new ArrayList(), new ArrayList(), isAdministrator);
		}
		else
		{	
			List roles = new ArrayList();
			List groups = new ArrayList();
			
			if(transactionObject == null)
			{
				Database db = CastorDatabaseService.getDatabase();
	
				try 
				{
					beginTransaction(db);
					
					SystemUser systemUser = SystemUserController.getController().getSystemUserWithName(userName, db);
					Iterator roleListIterator = systemUser.getRoles().iterator();
					while(roleListIterator.hasNext())
					{
						Role role = (Role)roleListIterator.next();
						InfoGlueRole infoGlueRole = new InfoGlueRole(role.getRoleName(), role.getDescription());
						roles.add(infoGlueRole);
					}
	
					Iterator groupListIterator = systemUser.getGroups().iterator();
					while(groupListIterator.hasNext())
					{
					    Group group = (Group)groupListIterator.next();
						InfoGlueGroup infoGlueGroup = new InfoGlueGroup(group.getGroupName(), group.getDescription());
						groups.add(infoGlueGroup);
					}
	
					infogluePrincipal = new InfoGluePrincipal(userName, systemUser.getFirstName(), systemUser.getLastName(), systemUser.getEmail(), roles, groups, isAdministrator);
					
					commitTransaction(db);
				} 
				catch (Exception e) 
				{
					CmsLogger.logInfo("An error occurred so we should not complete the transaction:" + e);
					rollbackTransaction(db);
					throw new SystemException(e.getMessage());
				}
			}
			else
			{
			    SystemUser systemUser = SystemUserController.getController().getSystemUserWithName(userName, transactionObject);
				Iterator roleListIterator = systemUser.getRoles().iterator();
				while(roleListIterator.hasNext())
				{
					Role role = (Role)roleListIterator.next();
					InfoGlueRole infoGlueRole = new InfoGlueRole(role.getRoleName(), role.getDescription());
					roles.add(infoGlueRole);
				}

				Iterator groupListIterator = systemUser.getGroups().iterator();
				while(groupListIterator.hasNext())
				{
				    Group group = (Group)groupListIterator.next();
					InfoGlueGroup infoGlueGroup = new InfoGlueGroup(group.getGroupName(), group.getDescription());
					groups.add(infoGlueGroup);
				}

				infogluePrincipal = new InfoGluePrincipal(userName, systemUser.getFirstName(), systemUser.getLastName(), systemUser.getEmail(), roles, groups, isAdministrator);
			}
		}
		
		return infogluePrincipal;
	}

	/**
	 * Gets an authorized InfoGlueRole.
	 */
	
	public InfoGlueRole getAuthorizedInfoGlueRole(String roleName) throws Exception
	{
		InfoGlueRole infoglueRole = null;

		RoleVO roleVO = RoleController.getController().getRoleVOWithId(roleName);
		
		infoglueRole = new InfoGlueRole(roleVO.getRoleName(), roleVO.getDescription());
				
		return infoglueRole;
	}

	/**
	 * Gets an authorized InfoGlueGroup.
	 */
	
	public InfoGlueGroup getAuthorizedInfoGlueGroup(String groupName) throws Exception
	{
		InfoGlueGroup infoglueGroup = null;

		GroupVO groupVO = GroupController.getController().getGroupVOWithId(groupName);
		
		infoglueGroup = new InfoGlueGroup(groupVO.getGroupName(), groupVO.getDescription());
				
		return infoglueGroup;
	}

	
	/**
	 * This method gets a users roles
	 */
	
	public List authorizeUser(String userName) throws Exception
	{
		List roles = new ArrayList();
		List groups = new ArrayList();
		
		String administratorUserName = CmsPropertyHandler.getProperty("administratorUserName");
		
		boolean isAdministrator = userName.equalsIgnoreCase(administratorUserName) ? true : false;
		if(isAdministrator)
			return roles;
		
		List roleVOList = RoleController.getController().getRoleVOList(userName);
		Iterator roleVOListIterator = roleVOList.iterator();
		while(roleVOListIterator.hasNext())
		{
			RoleVO roleVO = (RoleVO)roleVOListIterator.next();
			InfoGlueRole infoGlueRole = new InfoGlueRole(roleVO.getRoleName(), roleVO.getDescription());
			roles.add(infoGlueRole);
		}

		List groupVOList = GroupController.getController().getGroupVOList(userName);
		Iterator groupVOListIterator = groupVOList.iterator();
		while(groupVOListIterator.hasNext())
		{
		    GroupVO groupVO = (GroupVO)groupVOListIterator.next();
			InfoGlueGroup infoGlueGroup = new InfoGlueGroup(groupVO.getGroupName(), groupVO.getDescription());
			groups.add(infoGlueGroup);
		}

		return groups;
	}

	/**
	 * This method gets a list of roles
	 */
	
	public List getRoles() throws Exception
	{
		List roles = new ArrayList();
		
		List roleVOList = RoleController.getController().getRoleVOList();
		Iterator roleVOListIterator = roleVOList.iterator();
		while(roleVOListIterator.hasNext())
		{
			RoleVO roleVO = (RoleVO)roleVOListIterator.next();
			InfoGlueRole infoGlueRole = new InfoGlueRole(roleVO.getRoleName(), roleVO.getDescription());
			roles.add(infoGlueRole);
		}
		
		return roles;
	}

    public List getGroups() throws Exception
    {
        List groups = new ArrayList();
		
		List groupVOList = GroupController.getController().getGroupVOList();
		Iterator groupVOListIterator = groupVOList.iterator();
		while(groupVOListIterator.hasNext())
		{
		    GroupVO groupVO = (GroupVO)groupVOListIterator.next();
			InfoGlueGroup infoGlueGroup = new InfoGlueGroup(groupVO.getGroupName(), groupVO.getDescription());
			groups.add(infoGlueGroup);
		}
		
		return groups;
    }

    
	/**
	 * This method gets a list of users
	 */
	
	public List getUsers() throws Exception
	{
		List users = new ArrayList();
		
		List systemUserVOList = SystemUserController.getController().getSystemUserVOList();
		Iterator systemUserVOListIterator = systemUserVOList.iterator();
		while(systemUserVOListIterator.hasNext())
		{
			SystemUserVO systemUserVO = (SystemUserVO)systemUserVOListIterator.next();

			List roles = new ArrayList();
			Collection roleVOList = RoleController.getController().getRoleVOList(systemUserVO.getUserName());
			Iterator roleVOListIterator = roleVOList.iterator();
			while(roleVOListIterator.hasNext())
			{
				RoleVO roleVO = (RoleVO)roleVOListIterator.next();
				InfoGlueRole infoGlueRole = new InfoGlueRole(roleVO.getRoleName(), roleVO.getDescription());
				roles.add(infoGlueRole);
			}
			
			List groups = new ArrayList();
			Collection groupVOList = GroupController.getController().getGroupVOList(systemUserVO.getUserName());
			Iterator groupVOListIterator = groupVOList.iterator();
			while(groupVOListIterator.hasNext())
			{
			    GroupVO groupVO = (GroupVO)groupVOListIterator.next();
				InfoGlueGroup infoGlueGroup = new InfoGlueGroup(groupVO.getGroupName(), groupVO.getDescription());
				roles.add(infoGlueGroup);
			}
			
			InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(systemUserVO.getUserName(), systemUserVO.getFirstName(), systemUserVO.getLastName(), systemUserVO.getEmail(), roles, groups, false);
			
			users.add(infoGluePrincipal);
		}
		
		return users;
	}

	public List getFilteredUsers(String firstName, String lastName, String userName, String email, String[] roleIds) throws SystemException, Bug
	{
		List users = new ArrayList();
		
		List systemUserVOList = SystemUserController.getController().getFilteredSystemUserVOList(firstName, lastName, userName, email, roleIds);
		Iterator systemUserVOListIterator = systemUserVOList.iterator();
		while(systemUserVOListIterator.hasNext())
		{
			SystemUserVO systemUserVO = (SystemUserVO)systemUserVOListIterator.next();
			
			List roles = new ArrayList();
			Collection roleVOList = RoleController.getController().getRoleVOList(systemUserVO.getUserName());
			Iterator roleVOListIterator = roleVOList.iterator();
			while(roleVOListIterator.hasNext())
			{
				RoleVO roleVO = (RoleVO)roleVOListIterator.next();
				InfoGlueRole infoGlueRole = new InfoGlueRole(roleVO.getRoleName(), roleVO.getDescription());
				roles.add(infoGlueRole);
			}
			
			List groups = new ArrayList();
			Collection groupVOList = GroupController.getController().getGroupVOList(systemUserVO.getUserName());
			Iterator groupVOListIterator = groupVOList.iterator();
			while(groupVOListIterator.hasNext())
			{
			    GroupVO groupVO = (GroupVO)groupVOListIterator.next();
				InfoGlueGroup infoGlueGroup = new InfoGlueGroup(groupVO.getGroupName(), groupVO.getDescription());
				groups.add(infoGlueGroup);
			}
			
			InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(systemUserVO.getUserName(), systemUserVO.getFirstName(), systemUserVO.getLastName(), systemUserVO.getEmail(), roles, groups, false);
			users.add(infoGluePrincipal);
		}
		
		return users;
	}
	
	public List getUsers(String roleName) throws Exception
	{
		return getRoleUsers(roleName);
	}

    public List getRoleUsers(String roleName) throws Exception
    {
        CmsLogger.logInfo("roleName:" + roleName);
		List users = new ArrayList();
		
		List systemUserVOList = RoleController.getController().getRoleSystemUserVOList(roleName);
		Iterator systemUserVOListIterator = systemUserVOList.iterator();
		while(systemUserVOListIterator.hasNext())
		{
			SystemUserVO systemUserVO = (SystemUserVO)systemUserVOListIterator.next();
			InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(systemUserVO.getUserName(), systemUserVO.getFirstName(), systemUserVO.getLastName(), systemUserVO.getEmail(), new ArrayList(), new ArrayList(), false);
			users.add(infoGluePrincipal);
		}
		
		return users;
	}

    public List getGroupUsers(String groupName) throws Exception
    {
        CmsLogger.logInfo("groupName:" + groupName);
		List users = new ArrayList();
		
		List systemUserVOList = GroupController.getController().getGroupSystemUserVOList(groupName);
		Iterator systemUserVOListIterator = systemUserVOList.iterator();
		while(systemUserVOListIterator.hasNext())
		{
			SystemUserVO systemUserVO = (SystemUserVO)systemUserVOListIterator.next();
			InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(systemUserVO.getUserName(), systemUserVO.getFirstName(), systemUserVO.getLastName(), systemUserVO.getEmail(), new ArrayList(), new ArrayList(), false);
			users.add(infoGluePrincipal);
		}
		
		return users;
    }

	public void createInfoGluePrincipal(SystemUserVO systemUserVO) throws Exception
	{
		SystemUserController.getController().create(systemUserVO);
	}

	public void updateInfoGluePrincipal(SystemUserVO systemUserVO, String[] roleNames, String[] groupNames) throws Exception
	{
		SystemUserController.getController().update(systemUserVO, roleNames, groupNames);
	}

	/**
	 * This method is used to send out a newpassword to an existing users.  
	 */

	public void updateInfoGluePrincipalPassword(String userName) throws Exception
	{
		SystemUserController.getController().updatePassword(userName);
	}
	
	/**
	 * This method is used to let a user update his password by giving his/her old one first.  
	 */

	public void updateInfoGluePrincipalPassword(String userName, String oldPassword, String newPassword) throws Exception
	{
		SystemUserController.getController().updatePassword(userName, oldPassword, newPassword);
	}
	
	public void deleteInfoGluePrincipal(String userName) throws Exception
	{
		SystemUserController.getController().delete(userName);
	}

	public void createInfoGlueRole(RoleVO roleVO) throws Exception
	{
		RoleController.getController().create(roleVO);
	}

	public void updateInfoGlueRole(RoleVO roleVO, String[] userNames) throws Exception
	{
		RoleController.getController().update(roleVO, userNames);
	}

	public void deleteInfoGlueRole(String roleName) throws Exception
	{
		RoleController.getController().delete(roleName);
	}

	public void createInfoGlueGroup(GroupVO groupVO) throws Exception
	{
	    GroupController.getController().create(groupVO);
	}

	public void updateInfoGlueGroup(GroupVO groupVO, String[] userNames) throws Exception
	{
	    GroupController.getController().update(groupVO, userNames);
	}

	public void deleteInfoGlueGroup(String groupName) throws Exception
	{
		GroupController.getController().delete(groupName);
	}

	public Properties getExtraProperties()
	{
		return extraProperties;
	}

	public void setExtraProperties(Properties extraProperties)
	{
		this.extraProperties = extraProperties;
	}
	
    public Object getTransactionObject()
    {
        return this.transactionObject;
    }

    public void setTransactionObject(Object transactionObject)
    {
        this.transactionObject = (Database)transactionObject; 
    }

	public BaseEntityVO getNewVO()
	{
		return null;
	}

}
