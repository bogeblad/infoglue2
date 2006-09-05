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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
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
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsPropertyHandler;

/**
 * @author Mattias Bogeblad
 *
 * This authentication module authenticates an user against the ordinary infoglue database.
 */

public class CombinedJNDIBasicAuthorizationModule extends JNDIBasicAuthorizationModule
{
    private final static Logger logger = Logger.getLogger(CombinedJNDIBasicAuthorizationModule.class.getName());
		
	private AuthorizationModule authorizationModule = null;

	private AuthorizationModule getFallbackAuthorizationModule() throws SystemException
	{
		try
    	{
			logger.info("InfoGlueAuthenticationFilter.authorizerClass:" + InfoGlueBasicAuthorizationModule.class.getName());
			authorizationModule = (AuthorizationModule)Class.forName(InfoGlueBasicAuthorizationModule.class.getName()).newInstance();
			logger.info("authorizationModule:" + authorizationModule);
			authorizationModule.setExtraProperties(this.extraProperties);
			authorizationModule.setTransactionObject(this.getTransactionObject());
			logger.info("InfoGlueAuthenticationFilter.extraProperties:" + this.extraProperties);
    	}
    	catch(Exception e)
    	{
    		logger.error("There was an error initializing the authorizerClass:" + e.getMessage(), e);
    		throw new SystemException("There was an error initializing the authorizerClass:" + e.getMessage(), e);
    	}
	   
		return authorizationModule;
	}

	/**
	 * Gets an authorized InfoGluePrincipal. If the user has logged in with the root-account
	 * we immediately return - otherwise we populate it.
	 */
	
	public InfoGluePrincipal getAuthorizedInfoGluePrincipal(String userName) throws Exception
	{
		InfoGluePrincipal infogluePrincipal = null;
		
		try
		{
			infogluePrincipal = super.getAuthorizedInfoGluePrincipal(userName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			infogluePrincipal = getFallbackAuthorizationModule().getAuthorizedInfoGluePrincipal(userName);
		}
		
		return infogluePrincipal;
	}

	/**
	 * Gets an authorized InfoGlueRole.
	 */

	public InfoGlueRole getAuthorizedInfoGlueRole(String roleName) throws Exception
	{
		InfoGlueRole role = null;
		
		try
		{
			role = super.getAuthorizedInfoGlueRole(roleName);
		}
		catch(Exception e)
		{
			role = getFallbackAuthorizationModule().getAuthorizedInfoGlueRole(roleName);
		}
			
		return role;
	}

	/**
	 * Gets an authorized InfoGlueGroup.
	 */

	public InfoGlueGroup getAuthorizedInfoGlueGroup(String groupName) throws Exception
	{
		InfoGlueGroup group = null;

		try
		{
			group = super.getAuthorizedInfoGlueGroup(groupName);
		}
		catch(Exception e)
		{
			group = getFallbackAuthorizationModule().getAuthorizedInfoGlueGroup(groupName);
		}
			
		return group;
	}

	
	/**
	 * This method gets a users roles
	 */

	public List authorizeUser(String userName) throws Exception
	{
		List roles = new ArrayList();
		
		try
		{
			roles.addAll(super.authorizeUser(userName));
		}		
		catch(Exception e)
		{
		}

		try
		{
			roles.addAll(getFallbackAuthorizationModule().authorizeUser(userName));
		}		
		catch(Exception e)
		{
		}

		return roles;
	}

	
	/**
	 * This method gets a list of roles
	 */
	
	public List getRoles() throws Exception
	{
		List roles = new ArrayList();

		try
		{
			roles.addAll(super.getRoles());
		}		
		catch(Exception e)
		{
		}

		try
		{
			roles.addAll(getFallbackAuthorizationModule().getRoles());
		}		
		catch(Exception e)
		{
		}
		
		return roles;
	}

	/**
	 * This method gets a list of groups
	 */

    public List getGroups() throws Exception
    {
		List groups = new ArrayList();

		try
		{
			groups.addAll(super.getGroups());
		}		
		catch(Exception e)
		{
		}

		try
		{
			groups.addAll(getFallbackAuthorizationModule().getGroups());
		}		
		catch(Exception e)
		{
		}
		
		return groups;
    }
    
	/**
	 * This method gets a list of users
	 */

	public List getUsers() throws Exception
	{
		List users = new ArrayList();
		
		try
		{
			users.addAll(super.getUsers());
		}		
		catch(Exception e)
		{
		}

		try
		{
			users.addAll(getFallbackAuthorizationModule().getUsers());
		}		
		catch(Exception e)
		{
		}

		return users;
	}
/*
	public List getFilteredUsers(String firstName, String lastName, String userName, String email, String[] roleIds) throws Exception
	{
		throw new Exception("Unsupported operation");
		//return null;
	}
*/
	
	public List getUsers(String roleName) throws Exception
	{
		return getRoleUsers(roleName);
	}

    public List getRoleUsers(String roleName) throws Exception
    {
		List users = new ArrayList();
		
		try
		{
			users.addAll(super.getRoleUsers(roleName));
		}		
		catch(Exception e)
		{
		}

		try
		{
			users.addAll(getFallbackAuthorizationModule().getRoleUsers(roleName));
		}		
		catch(Exception e)
		{
		}
    	
    	return users;
	}

    public List getGroupUsers(String groupName) throws Exception
    {
		List users = new ArrayList();
		
		try
		{
			users.addAll(super.getGroupUsers(groupName));
		}		
		catch(Exception e)
		{
		}

		try
		{
			users.addAll(getFallbackAuthorizationModule().getGroupUsers(groupName));
		}		
		catch(Exception e)
		{
		}

    	return users;
    }

    /*
	public void createInfoGluePrincipal(SystemUserVO systemUserVO) throws Exception
	{
	}

	public void updateInfoGluePrincipal(SystemUserVO systemUserVO, String[] roleNames, String[] groupNames) throws Exception
	{
	}
	*/

	/**
	 * This method is used to send out a newpassword to an existing users.  
	 */

	public void updateInfoGluePrincipalPassword(String userName) throws Exception
	{
	}
	
	/**
	 * This method is used to let a user update his password by giving his/her old one first.  
	 */

	public void updateInfoGluePrincipalPassword(String userName, String oldPassword, String newPassword) throws Exception
	{
	}
	
	public void deleteInfoGluePrincipal(String userName) throws Exception
	{
	}

	public void createInfoGlueRole(RoleVO roleVO) throws Exception
	{
	}

	public void updateInfoGlueRole(RoleVO roleVO, String[] userNames) throws Exception
	{
	}

	public void deleteInfoGlueRole(String roleName) throws Exception
	{
	}

	public void createInfoGlueGroup(GroupVO groupVO) throws Exception
	{
	}

	public void updateInfoGlueGroup(GroupVO groupVO, String[] userNames) throws Exception
	{
	}

	public void deleteInfoGlueGroup(String groupName) throws Exception
	{
	}

}
