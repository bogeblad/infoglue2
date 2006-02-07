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


package org.infoglue.cms.controllers.kernel.impl.simple;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exolab.castor.jdo.Database;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.SystemUserVO;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.AuthorizationModule;
import org.infoglue.cms.security.InfoGlueAuthenticationFilter;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.controllers.kernel.impl.simple.ExtranetController;
import org.infoglue.deliver.util.CacheController;


/**
 * @author Mattias Bogeblad
 * 
 * This class acts as the proxy for getting the right roles.
 */

public class UserControllerProxy extends BaseController 
{
	private AuthorizationModule authorizationModule = null;
	private Database transactionObject = null;
	
	public UserControllerProxy(Database transactionObject)
	{
	    this.transactionObject = transactionObject;
	}
	
	public static UserControllerProxy getController()
	{
		return new UserControllerProxy(null);
	}	
	
	public static UserControllerProxy getController(Database transactionObject)
	{
	    return new UserControllerProxy(transactionObject);
	}
	
	/**
	 * This method instantiates the AuthorizationModule.
	 */
	
	public AuthorizationModule getAuthorizationModule() throws SystemException
	{
		//if(authorizationModule == null)
	    //{
			try
	    	{
			    getLogger().info("InfoGlueAuthenticationFilter.authorizerClass:" + InfoGlueAuthenticationFilter.authorizerClass);
				authorizationModule = (AuthorizationModule)Class.forName(InfoGlueAuthenticationFilter.authorizerClass).newInstance();
				getLogger().info("authorizationModule:" + authorizationModule);
				authorizationModule.setExtraProperties(InfoGlueAuthenticationFilter.extraProperties);
				authorizationModule.setTransactionObject(this.transactionObject);
				getLogger().info("InfoGlueAuthenticationFilter.extraProperties:" + InfoGlueAuthenticationFilter.extraProperties);
	    	}
	    	catch(Exception e)
	    	{
	    		//e.printStackTrace();
	    		getLogger().error("There was an error initializing the authorizerClass:" + e.getMessage(), e);
	    		throw new SystemException("There was an error initializing the authorizerClass:" + e.getMessage(), e);
	    	}
	    //}
	   
		return authorizationModule;
	}
	
	/**
	 * This method return whether the module in question supports updates to the values.
	 */
	
	public boolean getSupportUpdate() throws ConstraintException, SystemException, Exception
	{
		return getAuthorizationModule().getSupportUpdate();
	}

	/**
	 * This method return whether the module in question supports deletes of users.
	 */
	
	public boolean getSupportDelete() throws ConstraintException, SystemException, Exception
	{
		return getAuthorizationModule().getSupportDelete();
	}

	/**
	 * This method return whether the module in question supports creation of new users.
	 */
	
	public boolean getSupportCreate() throws ConstraintException, SystemException, Exception
	{
		return getAuthorizationModule().getSupportCreate();
	}

	/**
	 * This method returns a complete list of available users
	 */
	
    public List getAllUsers() throws ConstraintException, SystemException, Exception
    {
    	List users = new ArrayList();
    	
		users = getAuthorizationModule().getUsers();
    	
    	return users;
    }

	/**
	 * This method returns a list of all sought for users
	 */
	
    public List getFilteredUsers(String firstName, String lastName, String userName, String email, String[] roleNames) throws Exception
    {
    	List users = new ArrayList();
    	
		users = getAuthorizationModule().getFilteredUsers(firstName, lastName, userName, email, roleNames);
    	
    	return users;
    }
    
	/**
	 * This method returns a certain user
	 */
	
    public InfoGluePrincipal getUser(String userName) throws ConstraintException, SystemException, Exception
    {
    	//InfoGluePrincipal infoGluePrincipal = null;
    	
    	InfoGluePrincipal infoGluePrincipal = (InfoGluePrincipal)CacheController.getCachedObjectFromAdvancedCache("principalCache", userName, 60);
		if(infoGluePrincipal == null)
		{
			infoGluePrincipal = getAuthorizationModule().getAuthorizedInfoGluePrincipal(userName);
		   
			if(infoGluePrincipal != null)
				CacheController.cacheObjectInAdvancedCache("principalCache", userName, infoGluePrincipal, new String[]{}, false);
				//CacheController.cacheObject("principalCache", userName, infoGluePrincipal);
		}
    	
		//infoGluePrincipal = getAuthorizationModule().getAuthorizedInfoGluePrincipal(userName);
    	
    	return infoGluePrincipal;
    }
    
    
	/**
	 * This method creates a new user
	 */
	
	public InfoGluePrincipal createUser(SystemUserVO systemUserVO) throws ConstraintException, SystemException, Exception
	{
		InfoGluePrincipal infoGluePrincipal = null;
    	
		getAuthorizationModule().createInfoGluePrincipal(systemUserVO);
    	
		return getUser(systemUserVO.getUserName());
	}

	/**
	 * This method updates an existing user
	 */
	
	public void updateUser(SystemUserVO systemUserVO, String[] roleNames, String[] groupNames) throws ConstraintException, SystemException, Exception
	{
		getAuthorizationModule().updateInfoGluePrincipal(systemUserVO, roleNames, groupNames);
	}

	/**
	 * This method makes a new password and sends it to the user
	 */
	
	public void updateUserPassword(String userName) throws ConstraintException, SystemException, Exception
	{
		getAuthorizationModule().updateInfoGluePrincipalPassword(userName);
	}

	/**
	 * This method makes a new password and sends it to the user
	 */
	
	public void updateUserPassword(String userName, String oldPassword, String newPassword) throws ConstraintException, SystemException, Exception
	{
		getAuthorizationModule().updateInfoGluePrincipalPassword(userName, oldPassword, newPassword);
	}

	/**
	 * This method deletes an existing user
	 */
	
	public void deleteUser(String userName) throws ConstraintException, SystemException, Exception
	{
		getAuthorizationModule().deleteInfoGluePrincipal(userName);
	}
	
	public BaseEntityVO getNewVO()
	{
		return null;
	}
 
}
