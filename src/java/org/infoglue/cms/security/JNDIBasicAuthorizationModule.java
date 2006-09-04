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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.infoglue.cms.entities.management.GroupVO;
import org.infoglue.cms.entities.management.RoleVO;
import org.infoglue.cms.entities.management.SystemUserVO;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsPropertyHandler;

/**
 * @author Mattias Bogeblad
 *
 * This authentication module authenticates an user against the ordinary infoglue database.
 */

public class JNDIBasicAuthorizationModule implements AuthorizationModule
{
    private final static Logger logger = Logger.getLogger(JNDIBasicAuthorizationModule.class.getName());

	protected Properties extraProperties = null;
	
	/**
	 * Gets is the implementing class can update as well as read 
	 */
	
	public boolean getSupportUpdate() 
	{
		return false;
	}
	
	/**
	 * Gets is the implementing class can delete as well as read 
	 */
	
	public boolean getSupportDelete()
	{
		return false;
	}
	
	/**
	 * Gets is the implementing class can create as well as read 
	 */
	
	public boolean getSupportCreate()
	{
		return false;
	}
	
	/**
	 * This method gets a Context - either by an anonymous bind or a real bind
	 */
	
	public DirContext getContext() throws Exception
	{
		String connectionURL 		= this.extraProperties.getProperty("connectionURL");
		String ldapVersion			= this.extraProperties.getProperty("ldapVersion");
		String socketFactory		= this.extraProperties.getProperty("socketFactory");
		String authenticationMethod	= this.extraProperties.getProperty("authenticationMethod");
		String connectionName		= this.extraProperties.getProperty("connectionName");
		String connectionPassword	= this.extraProperties.getProperty("connectionPassword");

		// Create a Hashtable object.
		Hashtable env = new Hashtable();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

		env.put(Context.PROVIDER_URL, connectionURL);
		if(ldapVersion != null && !ldapVersion.equals("3"))
			env.put("java.naming.ldap.version", ldapVersion); 		
		else
			env.put("java.naming.ldap.version", "3"); 
		
		if(socketFactory != null && !socketFactory.equals(""))
			env.put("java.naming.ldap.factory.socket", "org.infoglue.cms.security.DummySSLSocketFactory");
		
		if(authenticationMethod != null && authenticationMethod.equals("none"))
		{
			env.put(Context.SECURITY_AUTHENTICATION, "none");
		}
		else
		{
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, connectionName);
			env.put(Context.SECURITY_CREDENTIALS, connectionPassword);
		}
				
		DirContext ctx = new InitialDirContext(env); 
		
		return ctx;
	}
	
	/**
	 * Gets an authorized InfoGluePrincipal 
	 */
	
	public InfoGluePrincipal getAuthorizedInfoGluePrincipal(String userName) throws Exception
	{
		InfoGluePrincipal infogluePrincipal = null;
		
		String administratorUserName = CmsPropertyHandler.getAdministratorUserName();
		String administratorEmail 	 = CmsPropertyHandler.getAdministratorEmail();
		//String administratorUserName = CmsPropertyHandler.getProperty("administratorUserName");
		//String administratorEmail 	 = CmsPropertyHandler.getProperty("administratorEmail");
		
		final boolean isAdministrator = userName.equalsIgnoreCase(administratorUserName) ? true : false;
		if(isAdministrator)
		{
			infogluePrincipal = new InfoGluePrincipal(userName, "System", "Administrator", administratorEmail, new ArrayList(), new ArrayList(), isAdministrator);
		}
		else
		{	
			DirContext ctx = getContext();
			
			Map userAttributes = getUserAttributes(userName, ctx);
			List roles = getRoles(userName, ctx);
			List groups = getGroups(userName, ctx);
			
			infogluePrincipal = new InfoGluePrincipal(userName, (String)userAttributes.get("firstName"), (String)userAttributes.get("lastName"), (String)userAttributes.get("mail"), roles, groups, isAdministrator);
			
			ctx.close();
		}
		
		return infogluePrincipal;
	}

	/**
	 * Gets an authorized InfoGlueRole.
	 */
	
	public InfoGlueRole getAuthorizedInfoGlueRole(String roleName) throws Exception
	{
		InfoGlueRole role = null;
		
		DirContext ctx = getContext();
		
		role = getAuthorizedInfoGlueRole(roleName, ctx);
		
		ctx.close();
		
		return role;
	}
	
	/**
	 * Gets an authorized InfoGlueRole.
	 */
	
	public InfoGlueRole getAuthorizedInfoGlueRole(String roleName, DirContext ctx) throws Exception
	{
		logger.info("\n\n\n ---------- getAuthorizedInfoGlueRole starting ---------\n\n\n");
		
		InfoGlueRole infoglueRole = null;

		String roleBase 			= this.extraProperties.getProperty("roleBase").toLowerCase().trim();
		String rolesFilter 			= this.extraProperties.getProperty("rolesFilter");
		String rolesAttributeFilter = this.extraProperties.getProperty("rolesAttributesFilter");
		String roleNameAttribute 	= this.extraProperties.getProperty("roleNameAttribute");
		String roleSearchScope 		= this.extraProperties.getProperty("roleSearchScope");

		try 
		{
			logger.info("Connected...");

			String baseDN = roleBase;
			String searchFilter = "(cn=" + roleName + ")";
			if(roleName.indexOf("cn=") > -1)
				searchFilter = "(" + roleName + ")";
				
			logger.info("searchFilter:" + searchFilter);
			logger.info("roleSearchScope:" + roleSearchScope);
			
			String rolesAttribute = "distinguishedName";
			if(rolesAttributeFilter != null && rolesAttributeFilter.length() > 0)
				rolesAttribute = rolesAttributeFilter;
	
			String[] attrID = rolesAttribute.split(",");
			logger.info("attrID:" + attrID);
			
			SearchControls ctls = new SearchControls(); 

			int roleSearchScopeInt = SearchControls.SUBTREE_SCOPE;
			if(roleSearchScope != null && roleSearchScope.equalsIgnoreCase("ONELEVEL_SCOPE"))
			    roleSearchScopeInt = SearchControls.ONELEVEL_SCOPE;
			else if(roleSearchScope != null && roleSearchScope.equalsIgnoreCase("OBJECT_SCOPE"))
			    roleSearchScopeInt = SearchControls.OBJECT_SCOPE;
			    
		    ctls.setSearchScope(roleSearchScopeInt);
			ctls.setReturningAttributes(attrID);
	
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 

			if(!answer.hasMore())
				throw new Exception("The was no groups found in the JNDI Data Source.");
		
			logger.info("-----------------------\n");
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				logger.info("Role:" + sr.toString() + "\n");
				
				Attributes attributes = sr.getAttributes();
				logger.info("attributes:" + attributes.toString());
				logger.info("roleNameAttribute:" + roleNameAttribute);
				Attribute attribute = attributes.get(roleNameAttribute);
				logger.info("attribute:" + attribute.toString());
				NamingEnumeration allEnum = attribute.getAll();
				while(allEnum.hasMore())
				{
					String groupName = (String)allEnum.next();
					logger.info("roleName:" + groupName);
					
					infoglueRole = new InfoGlueRole(groupName, "Not available from JNDI-source");
				}
				
			} 
			logger.info("-----------------------\n");
		}
		catch (Exception e) 
		{
			logger.info("Could not find Role: " + e.getMessage());
		}

		return infoglueRole;
	}
	
	/**
	 * This method gets a users roles
	 */
	
	public List authorizeUser(String userName) throws Exception
	{
		return getRoles(userName);
	}


	/**
	 * Returns an attribute set which this user has. 
	 *
	 * @param context The directory context we are searching
	 * @param user The User to be checked
	 *
	 * @exception NamingException if a directory server error occurs
	 */
	
	protected Map getUserAttributes(String userName) throws NamingException, Exception
	{
		Map attributes = null;
		
		DirContext ctx = getContext();
		attributes = getUserAttributes(userName, getContext());
		
		ctx.close();
		
		return attributes;
	}
	
	/**
	 * Returns an attribute set which this user has. 
	 *
	 * @param context The directory context we are searching
	 * @param user The User to be checked
	 *
	 * @exception NamingException if a directory server error occurs
	 */
	
	protected Map getUserAttributes(String userName, DirContext ctx) throws NamingException, Exception
	{
		Map userAttributes = new HashMap();
		
		String roleBase 			= this.extraProperties.getProperty("roleBase");
		String userBase				= this.extraProperties.getProperty("userBase");
		String userSearch			= this.extraProperties.getProperty("userSearch");
		String userAttributesFilter	= this.extraProperties.getProperty("userAttributesFilter");
		
		String userNameAttributeFilter	= this.extraProperties.getProperty("userNameAttributeFilter", "name");
		String userFirstNameAttributeFilter	= this.extraProperties.getProperty("userFirstNameAttributeFilter", "givenName");
		String userLastNameAttributeFilter	= this.extraProperties.getProperty("userLastNameAttributeFilter", "sn");
		String userMailAttributeFilter	= this.extraProperties.getProperty("userMailAttributeFilter", "mail");
		String memberOfAttributeFilter	= this.extraProperties.getProperty("memberOfAttributeFilter", "memberOf");
		String roleFilter				= this.extraProperties.getProperty("roleFilter", "InfoGlue");
				
		try 
		{
			//DirContext ctx = getContext(); 

			String baseDN = userBase;
			
	        String anonymousUserName = CmsPropertyHandler.getAnonymousUser();
	        if(userName.equals(anonymousUserName))
	        {
	            baseDN = this.extraProperties.getProperty("anonymousUserBase");
	        }

			String searchFilter = "(CN=" + userName +")";
			if(userSearch != null && userSearch.length() > 0)
				searchFilter = userSearch.replaceAll("\\{1\\}", userName);
			
			String attributesFilter = "name, givenName, sn, mail, memberOf";
			if(userAttributesFilter != null && userAttributesFilter.length() > 0)
				attributesFilter = userAttributesFilter;
			
			String[] attrID = attributesFilter.split(",");
			
			logger.info("baseDN:" + baseDN);
			logger.info("searchFilter:" + searchFilter);
			logger.info("attrID" + attrID);
						
			SearchControls ctls = new SearchControls(); 
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(attrID);
			
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 
			if(!answer.hasMore())
				throw new Exception("The user with userName=" + userName + " was not found in the JNDI Data Source.");
				
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				logger.info("Person:" + sr.toString() + "\n");
				Attributes attributes = sr.getAttributes();
				logger.info("attributes:" + attributes + "\n");
				
				for(int i=0; i<attrID.length; i++)
				{
					logger.info("attrID[i]:" + attrID[i]);
					Attribute attribute = attributes.get(attrID[i]);
					if(attribute == null)
					{
						throw new Exception("The attribute " + attrID[i] + " was not found among the user attributes. [" + attributes + "]");
					}
					
					logger.info("attribute:" + attribute.toString());
					NamingEnumeration allEnum = attribute.getAll();
					while(allEnum.hasMore())
					{
						String value = (String)allEnum.next();
						logger.info("value:" + value);
						userAttributes.put(attrID[i], value);
					}
				}	
				
				Attribute userNameAttribute = attributes.get(userNameAttributeFilter);
				logger.info("userNameAttribute:" + userNameAttribute.toString());
				Attribute userFirstNameAttribute = attributes.get(userFirstNameAttributeFilter);
				logger.info("userFirstNameAttribute:" + userFirstNameAttribute.toString());
				Attribute userLastNameAttribute = attributes.get(userLastNameAttributeFilter);
				logger.info("userLastNameAttribute:" + userLastNameAttribute.toString());
				Attribute userMailAttribute = attributes.get(userMailAttributeFilter);
				logger.info("userMailAttribute:" + userMailAttribute.toString());
				
				userAttributes.put("firstName", userFirstNameAttribute.get().toString());
				userAttributes.put("lastName", userLastNameAttribute.get().toString());
				//userAttributes.put("firstName", userFirstNameAttribute);
				//userAttributes.put("firstName", userFirstNameAttribute);
				//Attribute memberOfAttribute = attributes.get(memberOfAttributeFilter);
				//logger.info("memberOfAttribute:" + memberOfAttribute.toString());

			} 
		}
		catch (Exception e) 
		{
			logger.info(e);
			throw e;
		}

		return userAttributes;
	}

	/**
	 * Return a List of roles associated with the given User. Any
	 * roles present in the user's directory entry are supplemented by
	 * a directory search. If no roles are associated with this user,
	 * a zero-length List is returned.
	 *
	 * @param context The directory context we are searching
	 * @param user The User to be checked
	 *
	 * @exception NamingException if a directory server error occurs
	 */
	
	protected List getRoles(String userName) throws NamingException, Exception 
	{
		List roles = null;
		
		DirContext ctx = getContext();
		
		roles = getRoles(userName, ctx);
	
		ctx.close();
		
		return roles;
	}
	
	/**
	 * Return a List of roles associated with the given User. Any
	 * roles present in the user's directory entry are supplemented by
	 * a directory search. If no roles are associated with this user,
	 * a zero-length List is returned.
	 *
	 * @param context The directory context we are searching
	 * @param user The User to be checked
	 *
	 * @exception NamingException if a directory server error occurs
	 */
	
	protected List getRoles(String userName, DirContext ctx) throws NamingException, Exception 
	{
		logger.info("**************************************************");
		logger.info("*In JNDI version								 *");
		logger.info("**************************************************");
		logger.info("userName:" + userName);
		
		List roles = new ArrayList();
		
		String roleBase 			= this.extraProperties.getProperty("roleBase").toLowerCase().trim();
		String userBase				= this.extraProperties.getProperty("userBase").toLowerCase().trim();
		String userSearch			= this.extraProperties.getProperty("userSearch");
		String memberOfAttribute	= this.extraProperties.getProperty("memberOfAttributeFilter");
		String rolesAttributeFilter = this.extraProperties.getProperty("rolesAttributesFilter");
		String roleNameAttribute 	= this.extraProperties.getProperty("roleNameAttribute");
		String roleFilter			= this.extraProperties.getProperty("roleFilter", "InfoGlue");
		
		try 
		{
			String baseDN = userBase;
			
			String anonymousUserName = CmsPropertyHandler.getAnonymousUser();
	        if(userName.equals(anonymousUserName))
	        {
	            baseDN = this.extraProperties.getProperty("anonymousUserBase");
	        }

			String searchFilter = "(CN=" + userName +")";
			if(userSearch != null && userSearch.length() > 0)
				searchFilter = userSearch.replaceAll("\\{1\\}", userName);
			searchFilter = searchFilter.toLowerCase().trim();
			
			String memberOfAttributeFilter = "memberOf";
			if(memberOfAttribute != null && memberOfAttribute.length() > 0)
			    memberOfAttributeFilter = memberOfAttribute;
			memberOfAttributeFilter = memberOfAttributeFilter.toLowerCase().trim();
			
			String[] attrID = memberOfAttributeFilter.split(",");
			
			String rolesAttribute = "distinguishedName";
			if(rolesAttributeFilter != null && rolesAttributeFilter.length() > 0)
				rolesAttribute = rolesAttributeFilter;
			rolesAttribute = rolesAttribute.toLowerCase().trim();

			logger.info("baseDN:" + baseDN);
			logger.info("searchFilter:" + searchFilter);
			logger.info("attrID" + attrID);
			logger.info("roleBase:" + roleBase);

			SearchControls ctls = new SearchControls(); 
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(attrID);
			
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 
			if(!answer.hasMore())
				throw new Exception("The user with userName=" + userName + " was not found in the JNDI Data Source.");
			
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				logger.info("Person:" + sr.toString() + "\n");
				Attributes attributes = sr.getAttributes();
				
				Attribute attribute = attributes.get(memberOfAttributeFilter);
				logger.info("..................attribute:" + attribute.toString());
				NamingEnumeration allEnum = attribute.getAll();
				while(allEnum.hasMore())
				{
					Object roleNameObject = allEnum.next();
					
					String fullRoleName = roleNameObject.toString().toLowerCase().trim();
					String roleName = fullRoleName;
					logger.info("roleName:" + fullRoleName);
					
					logger.info("indexOf:" + fullRoleName.indexOf(roleBase));
					if(roleBase != null && fullRoleName.indexOf(roleBase) > -1)
					{
					    roleName = roleName.substring(0, roleName.indexOf(roleBase));
					    roleName = roleName.substring(0, roleName.lastIndexOf(","));
					}
					else
					{
					    continue;
					}
					
					logger.info("roleNameAttribute:" + roleNameAttribute);
					logger.info("roleName:" + roleName);
					logger.info("indexOf:" + roleName.indexOf(roleNameAttribute));
					
					if(roleNameAttribute != null && roleName.indexOf(roleNameAttribute) > -1)
					{
					    roleName = roleName.substring(roleName.indexOf(roleNameAttribute) + roleNameAttribute.length() + 1);
					}
					
					logger.info("*****************************");
					logger.info("roleName:" + roleName);
					logger.info("roleBase:" + roleBase);
					logger.info("*****************************");
					
					if(roleFilter.equalsIgnoreCase("*") || roleName.indexOf(roleFilter) > -1)
					{
						InfoGlueRole infoGlueRole = getAuthorizedInfoGlueRole(roleName);
					    //InfoGlueRole infoGlueRole = new InfoGlueRole(roleName, "Not available from JNDI-source");
						logger.info("Adding role.................:" + fullRoleName);
						roles.add(infoGlueRole);
					}
				}
				
			} 
		}
		catch (Exception e) 
		{
			logger.info("Could not find Group for empID: " + userName + e);
			throw e;
		}

		return roles;
	}

	
	/**
	 * Return a List of roles associated with the given User. Any
	 * roles present in the user's directory entry are supplemented by
	 * a directory search. If no roles are associated with this user,
	 * a zero-length List is returned.
	 *
	 * @param context The directory context we are searching
	 * @param user The User to be checked
	 *
	 * @exception NamingException if a directory server error occurs
	 */
	
	protected List getGroups(String userName) throws NamingException, Exception 
	{
		List groups = null;
		
		DirContext ctx = getContext();
		
		groups = getGroups(userName, ctx);
		
		ctx.close();
		
		return groups;
	}
	
	/**
	 * Return a List of roles associated with the given User. Any
	 * roles present in the user's directory entry are supplemented by
	 * a directory search. If no roles are associated with this user,
	 * a zero-length List is returned.
	 *
	 * @param context The directory context we are searching
	 * @param user The User to be checked
	 *
	 * @exception NamingException if a directory server error occurs
	 */
	
	protected List getGroups(String userName, DirContext ctx) throws NamingException, Exception 
	{
		logger.info("**************************************************");
		logger.info("*In JNDI version								  *");
		logger.info("**************************************************");
		logger.info("userName:" + userName);
		
		List groups = new ArrayList();
		
		String groupBase 			= this.extraProperties.getProperty("groupBase");
		String userBase				= this.extraProperties.getProperty("userBase");
		String userSearch			= this.extraProperties.getProperty("userSearch");
		String memberOfAttribute	= this.extraProperties.getProperty("memberOfAttributeFilter");
		String groupsAttributeFilter = this.extraProperties.getProperty("groupsAttributesFilter");
		String groupNameAttribute 	= this.extraProperties.getProperty("groupNameAttribute");
		String groupFilter			= this.extraProperties.getProperty("groupFilter", "InfoGlue");

		logger.info("groupBase:" + groupBase);
		logger.info("userBase:" + userBase);
		
		try 
		{
			String baseDN = userBase;
			
			String anonymousUserName = CmsPropertyHandler.getAnonymousUser();
	        if(userName.equals(anonymousUserName))
	        {
	            baseDN = this.extraProperties.getProperty("anonymousUserBase");
	        }

			String searchFilter = "(CN=" + userName +")";
			if(userSearch != null && userSearch.length() > 0)
				searchFilter = userSearch.replaceAll("\\{1\\}", userName);
			
			String memberOfAttributeFilter = "memberOf";
			if(memberOfAttribute != null && memberOfAttribute.length() > 0)
			    memberOfAttributeFilter = memberOfAttribute;
			
			String[] attrID = memberOfAttributeFilter.split(",");
			
			String groupsAttribute = "distinguishedName";
			if(groupsAttributeFilter != null && groupsAttributeFilter.length() > 0)
				groupsAttribute = groupsAttributeFilter;
			
			logger.info("baseDN:" + baseDN);
			logger.info("searchFilter:" + searchFilter);
			logger.info("attrID" + attrID);
			
			SearchControls ctls = new SearchControls(); 
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(attrID);
			
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 
			if(!answer.hasMore())
				throw new Exception("The user with userName=" + userName + " was not found in the JNDI Data Source.");
				
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				logger.info("Person:" + sr.toString() + "\n");
				Attributes attributes = sr.getAttributes();
				
				Attribute attribute = attributes.get(memberOfAttributeFilter);
				logger.info("..................attribute:" + attribute.toString());
				NamingEnumeration allEnum = attribute.getAll();
				while(allEnum.hasMore())
				{
					Object groupNameObject = allEnum.next();
					
					String groupName = groupNameObject.toString();
					logger.info("groupName:" + groupName);
					logger.info("groupBase:" + groupBase);
					logger.info("indexOf:" + groupName.indexOf(groupBase));
					if(groupBase != null && groupName.indexOf(groupBase) > -1)
					{
					    groupName = groupName.substring(0, groupName.indexOf(groupBase));
					    groupName = groupName.substring(0, groupName.lastIndexOf(","));
					}
					else
					{
					    continue;
					}

					logger.info("groupNameAttribute:" + groupNameAttribute);
					logger.info("groupName:" + groupName);
					logger.info("indexOf:" + groupName.indexOf(groupNameAttribute));
					if(groupNameAttribute != null && groupName.indexOf(groupNameAttribute) > -1)
					{
					    groupName = groupName.substring(groupName.indexOf(groupNameAttribute) + groupNameAttribute.length() + 1);
					}
					
					logger.info("groupName:" + groupName);
					logger.info("groupName:" + groupName);
					if(groupFilter.equalsIgnoreCase("*") || groupName.indexOf(groupFilter) > -1)
					{
					    InfoGlueRole infoGlueRole = new InfoGlueRole(groupName, "Not available from JNDI-source");
						logger.info("Adding group.................:" + groupName);
						groups.add(infoGlueRole);
					}
				}
				
			} 
			ctx.close();
		}
		catch (Exception e) 
		{
			logger.info("Could not find Group for empID: " +userName +e);
			throw e;
		}

		return groups;
	}
	
	/**
	 * This method gets a list of roles
	 */
	
	public List getRoles() throws Exception
	{
	    logger.info("getRoles start....");
		List roles = new ArrayList();

		String roleBase 			= this.extraProperties.getProperty("roleBase");
		String rolesFilter 			= this.extraProperties.getProperty("rolesFilter");
		String rolesAttributeFilter = this.extraProperties.getProperty("rolesAttributesFilter");
		String roleNameAttribute 	= this.extraProperties.getProperty("roleNameAttribute");
		String roleSearchScope 		= this.extraProperties.getProperty("roleSearchScope");

		try 
		{
			DirContext ctx = getContext();
			
			logger.info("Connected...");

			String baseDN = roleBase;
			String searchFilter = "(cn=InfoGlue*)";
			if(rolesFilter != null && rolesFilter.length() > 0)
				searchFilter = rolesFilter;
			
			logger.info("searchFilter:" + searchFilter);
			logger.info("roleSearchScope:" + roleSearchScope);
			
			String rolesAttribute = "distinguishedName";
			if(rolesAttributeFilter != null && rolesAttributeFilter.length() > 0)
				rolesAttribute = rolesAttributeFilter;
	
			String[] attrID = rolesAttribute.split(",");
			logger.info("attrID:" + attrID);
			
			SearchControls ctls = new SearchControls(); 

			int roleSearchScopeInt = SearchControls.SUBTREE_SCOPE;
			if(roleSearchScope != null && roleSearchScope.equalsIgnoreCase("ONELEVEL_SCOPE"))
			    roleSearchScopeInt = SearchControls.ONELEVEL_SCOPE;
			else if(roleSearchScope != null && roleSearchScope.equalsIgnoreCase("OBJECT_SCOPE"))
			    roleSearchScopeInt = SearchControls.OBJECT_SCOPE;
			    
		    ctls.setSearchScope(roleSearchScopeInt);
			ctls.setReturningAttributes(attrID);
	
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 

			if(!answer.hasMore())
				throw new Exception("The was no groups found in the JNDI Data Source.");
		
			logger.info("-----------------------\n");
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				logger.info("Group:" + sr.toString() + "\n");
				
				Attributes attributes = sr.getAttributes();
				logger.info("attributes:" + attributes.toString());
				logger.info("roleNameAttribute:" + roleNameAttribute);
				Attribute attribute = attributes.get(roleNameAttribute);
				logger.info("attribute:" + attribute.toString());
				NamingEnumeration allEnum = attribute.getAll();
				while(allEnum.hasMore())
				{
					String groupName = (String)allEnum.next();
					logger.info("groupName:" + groupName);
					
					InfoGlueRole infoGlueRole = new InfoGlueRole(groupName, "Not available from JNDI-source");
					roles.add(infoGlueRole);
				}
				
			} 
			logger.info("-----------------------\n");

			ctx.close();
		}
		catch (Exception e) 
		{
			logger.info("Could not find Roles: " + e.getMessage());
		}
	    logger.info("getRoles end....");

		return roles;
	}

	/**
	 * This method gets a list of users
	 */
	
	public List getUsers() throws Exception
	{
	    logger.info("getUsers start...");
	    
		List users = new ArrayList();
		
		String roleBase 				= this.extraProperties.getProperty("roleBase");
		String userBase					= this.extraProperties.getProperty("userBase");
		String userListSearch			= this.extraProperties.getProperty("userListSearch");
		String userAttributesFilter		= this.extraProperties.getProperty("userAttributesFilter");
		String userNameAttributeFilter	= this.extraProperties.getProperty("userNameAttributeFilter", "name");
		String userFirstNameAttributeFilter	= this.extraProperties.getProperty("userFirstNameAttributeFilter", "givenName");
		String userLastNameAttributeFilter	= this.extraProperties.getProperty("userLastNameAttributeFilter", "sn");
		String userMailAttributeFilter	= this.extraProperties.getProperty("userMailAttributeFilter", "mail");
		String memberOfAttributeFilter	= this.extraProperties.getProperty("memberOfAttributeFilter", "memberOf");
		String roleFilter				= this.extraProperties.getProperty("roleFilter", "InfoGlue");
		String roleNameAttribute 		= this.extraProperties.getProperty("roleNameAttribute");
		String userSearchScope 			= this.extraProperties.getProperty("userSearchScope");

		try 
		{
			DirContext ctx = getContext();
		
			String baseDN = userBase;
			String searchFilter = "(CN=*)";
			if(userListSearch != null && userListSearch.length() > 0)
				searchFilter = userListSearch;
			
			String attributesFilter = "name, givenName, sn, mail, memberOf";
			if(userAttributesFilter != null && userAttributesFilter.length() > 0)
				attributesFilter = userAttributesFilter;
						
			String[] attrID = attributesFilter.split(",");

			logger.info("attributesFilter" + attributesFilter);
			logger.info("baseDN:" + baseDN);
			logger.info("searchFilter:" + searchFilter);
			//logger.info("attrID" + attrID);
						
			SearchControls ctls = new SearchControls(); 

			int userSearchScopeInt = SearchControls.SUBTREE_SCOPE;
			if(userSearchScope != null && userSearchScope.equalsIgnoreCase("ONELEVEL_SCOPE"))
			    userSearchScopeInt = SearchControls.ONELEVEL_SCOPE;
			else if(userSearchScope != null && userSearchScope.equalsIgnoreCase("OBJECT_SCOPE"))
			    userSearchScopeInt = SearchControls.OBJECT_SCOPE;
			    
		    ctls.setSearchScope(userSearchScopeInt);
			ctls.setReturningAttributes(attrID);
	
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 

			if(!answer.hasMore())
				throw new Exception("The was no users found in the JNDI Data Source.");
		
			while (answer.hasMore()) 
			{
				try
				{
					SearchResult sr = (SearchResult)answer.next();
					logger.info("Person:" + sr.toString() + "\n");
					
					Attributes attributes = sr.getAttributes();
					logger.info("attributes:" + attributes.toString());
					Attribute userNameAttribute = attributes.get(userNameAttributeFilter);
					Attribute userFirstNameAttribute = attributes.get(userFirstNameAttributeFilter);
					Attribute userLastNameAttribute = attributes.get(userLastNameAttributeFilter);
					Attribute userMailAttribute = attributes.get(userMailAttributeFilter);
					Attribute memberOfAttribute = attributes.get(memberOfAttributeFilter);
					Attribute memberOfGroupsAttribute = attributes.get(memberOfAttributeFilter);
					
					if(userFirstNameAttribute == null || userLastNameAttribute == null || userMailAttribute == null)
					    throw new SystemException("The user " + userNameAttribute + " did not have firstName, lastName or email attribute which InfoGlue requires");
					    
					logger.info("userNameAttribute:" + userNameAttribute.toString());
					logger.info("userFirstNameAttribute:" + userFirstNameAttribute.toString());
					logger.info("userLastNameAttribute:" + userLastNameAttribute.toString());
					logger.info("userMailAttribute:" + userMailAttribute.toString());
					
					List roles = new ArrayList();
					List groups = new ArrayList();

					if(memberOfAttribute != null)
					{
						logger.info("memberOfAttribute:" + memberOfAttribute.toString());
					
						NamingEnumeration allEnum = memberOfAttribute.getAll();
						while(allEnum.hasMore())
						{
							String groupName = (String)allEnum.next();
							logger.info("groupName:" + groupName);
							logger.info("roleBase:" + roleBase);
							if(roleBase != null && groupName.indexOf(roleBase) > -1)
							{
							    groupName = groupName.substring(0, groupName.indexOf(roleBase));
							    groupName = groupName.substring(0, groupName.lastIndexOf(","));
							}
							
							logger.info("groupName:" + groupName);
							if(roleFilter.equalsIgnoreCase("*") || groupName.indexOf(roleFilter) > -1)
							{
							    logger.info("roleNameAttribute:" + roleNameAttribute);
								logger.info("groupName:" + groupName);
								logger.info("indexOf:" + groupName.indexOf(roleNameAttribute));
								if(roleNameAttribute != null && groupName.indexOf(roleNameAttribute) > -1)
								{
								    groupName = groupName.substring(groupName.indexOf(roleNameAttribute) + roleNameAttribute.length() + 1);
								}
								
							    InfoGlueRole infoGlueRole = new InfoGlueRole(groupName, "Not available from JNDI-source");
							    roles.add(infoGlueRole);
							}
						}
					}
					else
					{
						logger.info("No memberOfAttribute named :" + memberOfAttributeFilter + " was found.");
					}

					if(memberOfGroupsAttribute != null)
					{
						NamingEnumeration allGroupsEnum = memberOfGroupsAttribute.getAll();
						while(allGroupsEnum.hasMore())
						{
							String groupName = (String)allGroupsEnum.next();
							logger.info("groupName:" + groupName);
							logger.info("roleBase:" + roleBase);
							if(roleBase != null && groupName.indexOf(roleBase) > -1)
							{
							    groupName = groupName.substring(0, groupName.indexOf(roleBase));
							    groupName = groupName.substring(0, groupName.lastIndexOf(","));
							}
							
							logger.info("groupName:" + groupName);
							if(roleFilter.equalsIgnoreCase("*") || groupName.indexOf(roleFilter) > -1)
							{
							    logger.info("roleNameAttribute:" + roleNameAttribute);
								logger.info("groupName:" + groupName);
								logger.info("indexOf:" + groupName.indexOf(roleNameAttribute));
								if(roleNameAttribute != null && groupName.indexOf(roleNameAttribute) > -1)
								{
								    groupName = groupName.substring(groupName.indexOf(roleNameAttribute) + roleNameAttribute.length() + 1);
								}
								
								InfoGlueGroup infoGlueGroup = new InfoGlueGroup(groupName, "Not available from JNDI-source");
							    groups.add(infoGlueGroup);
							}
						}
					}
					else
					{
						logger.info("No memberOfGroupsAttribute named :" + memberOfAttributeFilter + " was found.");
					}

					InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(userNameAttribute.get().toString(), userFirstNameAttribute.get().toString(), userLastNameAttribute.get().toString(), userMailAttribute.get().toString(), roles, groups, false);
					users.add(infoGluePrincipal);
				}
				catch(Exception e)
				{
					logger.info("An error occurred when we tried to read user: " + e.getMessage(), e);
				}
			} 
			ctx.close();
		}
		catch (Exception e) 
		{
			logger.info("Could not find Groups: " + e.getMessage(), e);
		}
	    logger.info("getUsers end...");

		return users;
	}
	
	public List getFilteredUsers(String firstName, String lastName, String userName, String email, String[] roleIds) throws SystemException, Bug
	{
		List users = new ArrayList();
		//TODO		
		return users;
	}

	
	public List getUsers(String roleName) throws Exception
	{
		List users = null;
		
		DirContext ctx = getContext();
		
		users = getUsers(roleName, ctx);
		
		ctx.close();
		
		return users;
	}
	
	public List getUsers(String roleName, DirContext ctx) throws Exception
	{
		List users = new ArrayList();
		
		String roleBase 			= this.extraProperties.getProperty("roleBase").toLowerCase().trim();
		String rolesFilter 			= this.extraProperties.getProperty("rolesFilter");
		String rolesAttributeFilter = this.extraProperties.getProperty("rolesAttributesFilter");
		String roleNameAttribute	= this.extraProperties.getProperty("roleNameAttribute");
		String usersAttributeFilter = this.extraProperties.getProperty("usersAttributesFilter");
		String userNameAttribute	= this.extraProperties.getProperty("userNameAttributeFilter");
		String userBase 			= this.extraProperties.getProperty("userBase").toLowerCase().trim();
		
		try 
		{
		    logger.info("roleName:" + roleName);

			String baseDN = roleBase;
			String searchFilter = "(cn=" + roleName + ")";
			if(roleName.indexOf("cn=") > -1)
				searchFilter = "(" + roleName + ")";

		    logger.info("searchFilter:" + searchFilter);
		    logger.info("baseDN:" + baseDN);
			
			String rolesAttribute = "distinguishedName";
			if(rolesAttributeFilter != null && rolesAttributeFilter.length() > 0)
				rolesAttribute = rolesAttributeFilter;
	
			String[] attrID = rolesAttribute.split(",");

		    logger.info("Before search...");

			SearchControls ctls = new SearchControls(); 
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(attrID);
	
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 

		    logger.info("After search...");

			if(!answer.hasMore())
				throw new Exception("The was no roles found in the JNDI Data Source.");
		
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				logger.info("Role:" + sr.toString() + "\n");
				
				Attributes attributes = sr.getAttributes();
				logger.info("attributes:" + attributes.toString());
				logger.info("roleNameAttribute:" + roleNameAttribute);
				Attribute attribute = attributes.get(roleNameAttribute);
				logger.info("attribute:" + attribute.toString());
				NamingEnumeration allEnum = attribute.getAll();
				while(allEnum.hasMore())
				{
					String roleNameCandidate = (String)allEnum.next();
					logger.info("roleNameCandidate:" + roleNameCandidate);
					
					if(roleNameCandidate.equals(roleName))
					{
					    Attribute usersAttribute = attributes.get(usersAttributeFilter);
						logger.info("usersAttribute:" + usersAttribute.toString());
						
						List roles = new ArrayList();
						NamingEnumeration allUsersEnum = usersAttribute.getAll();
						while(allUsersEnum.hasMore())
						{
							String userName = (String)allUsersEnum.next();
							logger.info("userName:" + userName);
							logger.info("userBase:" + userBase);
							
							if(roleBase != null && userName.indexOf(userBase) > -1)
							{
							    userName = userName.substring(0, userName.indexOf(userBase));
							    userName = userName.substring(0, userName.lastIndexOf(","));
							}
							
							logger.info("userNameAttribute:" + userNameAttribute);
							logger.info("userName:" + userName);
							logger.info("indexOf:" + userName.indexOf(userNameAttribute));

							if(roleNameAttribute != null && userName.indexOf(userNameAttribute) > -1)
							{
							    userName = userName.substring(userName.indexOf(userNameAttribute) + userNameAttribute.length() + 1);
							}
							
							if(userName.indexOf("cn=") > -1)
								userName = userName.substring(userName.indexOf("cn=") + 3);
							
							//InfoGluePrincipal infoGluePrincipal = this.getAuthorizedInfoGluePrincipal(userName, false, ctx):
							InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(userName, "", "", "", new ArrayList(), new ArrayList(), false);
						    users.add(infoGluePrincipal);
						}
					}
				}
				
			} 
		}
		catch (Exception e) 
		{
			logger.info("Could not find users for role: " + e.getMessage());
		}
	    logger.info("--------------------END---------------------");

		return users;
	}

	public void createInfoGluePrincipal(SystemUserVO systemUserVO) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support creation of users yet...");
	}

	public void updateInfoGluePrincipal(SystemUserVO systemUserVO, String[] roleNames) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support updating of users yet...");
	}

	public void updateInfoGluePrincipalPassword(String userName) throws Exception 
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support updates of users yet...");
	}

	public void updateInfoGluePrincipalPassword(String userName, String oldPassword, String newPassword) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support updates of user password yet...");
	}
	
	public void deleteInfoGluePrincipal(String userName) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support deletion of users yet...");
	}
	
	public void createInfoGlueRole(RoleVO roleVO) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support creation of users yet...");
	}

	public void updateInfoGlueRole(RoleVO roleVO, String[] userNames) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support updates of users yet...");
	}

	public void deleteInfoGlueRole(String roleName) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support deletion of roles yet...");
	}
	
	public Properties getExtraProperties()
	{
		return this.extraProperties;
	}

	public void setExtraProperties(Properties properties)
	{
		this.extraProperties = properties;
	}

    public void setTransactionObject(Object transactionObject)
    {
    }

    public Object getTransactionObject()
    {
        return null;
    }

    
    /* (non-Javadoc)
     * @see org.infoglue.cms.security.AuthorizationModule#getAuthorizedInfoGlueGroup(java.lang.String)
     */
    public InfoGlueGroup getAuthorizedInfoGlueGroup(String groupName) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * This method returns a list of all groups available to InfoGlue.
     */
    public List getGroups() throws Exception
    {
    	List groups = null;
    	
		DirContext ctx = getContext();
		
		groups = getGroups(ctx);
		
		ctx.close();

		return groups;
    }
    
    /**
     * This method returns a list of all groups available to InfoGlue.
     */
    public List getGroups(DirContext ctx) throws Exception
    {
	    logger.info("getGroups start....");
		List groups = new ArrayList();
		
		String groupBase 			= this.extraProperties.getProperty("groupBase");
		String groupsFilter 		= this.extraProperties.getProperty("groupsFilter");
		String groupsAttributeFilter= this.extraProperties.getProperty("groupsAttributesFilter");
		String groupNameAttribute 	= this.extraProperties.getProperty("groupNameAttribute");
		String groupSearchScope 	= this.extraProperties.getProperty("groupSearchScope");
		
		try 
		{
			String baseDN = groupBase;
			String searchFilter = "(cn=InfoGlue*)";
			if(groupsFilter != null && groupsFilter.length() > 0)
				searchFilter = groupsFilter;
			
			logger.info("searchFilter:" + searchFilter);
			logger.info("groupSearchScope:" + groupSearchScope);
			
			String groupsAttribute = "distinguishedName";
			if(groupsAttributeFilter != null && groupsAttributeFilter.length() > 0)
				groupsAttribute = groupsAttributeFilter;
	
			String[] attrID = groupsAttribute.split(",");
			logger.info("attrID:" + attrID);
			
			SearchControls ctls = new SearchControls(); 

			int groupSearchScopeInt = SearchControls.SUBTREE_SCOPE;
			if(groupSearchScope != null && groupSearchScope.equalsIgnoreCase("ONELEVEL_SCOPE"))
			    groupSearchScopeInt = SearchControls.ONELEVEL_SCOPE;
			else if(groupSearchScope != null && groupSearchScope.equalsIgnoreCase("OBJECT_SCOPE"))
			    groupSearchScopeInt = SearchControls.OBJECT_SCOPE;
			    
		    ctls.setSearchScope(groupSearchScopeInt);
			ctls.setReturningAttributes(attrID);
	
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 

			if(!answer.hasMore())
				throw new Exception("The was no groups found in the JNDI Data Source.");
		
			logger.info("-----------------------\n");
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				logger.info("Group:" + sr.toString() + "\n");
				
				Attributes attributes = sr.getAttributes();
				logger.info("attributes:" + attributes.toString());
				logger.info("groupNameAttribute:" + groupNameAttribute);
				Attribute attribute = attributes.get(groupNameAttribute);
				logger.info("attribute:" + attribute.toString());
				NamingEnumeration allEnum = attribute.getAll();
				while(allEnum.hasMore())
				{
					String groupName = (String)allEnum.next();
					logger.info("groupName:" + groupName);
					
					InfoGlueRole infoGlueRole = new InfoGlueRole(groupName, "Not available from JNDI-source");
					groups.add(infoGlueRole);
				}
				
			} 
			logger.info("-----------------------\n");
		}
		catch (Exception e) 
		{
			logger.info("Could not find Groups: " + e.getMessage());
		}
	    logger.info("getRoles end....");

		return groups;
    }

    /* (non-Javadoc)
     * @see org.infoglue.cms.security.AuthorizationModule#getRoleUsers(java.lang.String)
     */
    public List getRoleUsers(String roleName) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * Gets a list of users which is memebers of the given group
     */
    public List getGroupUsers(String groupName) throws Exception
    {
		List users = null;
		
		DirContext ctx = getContext();
		
		users = getGroupUsers(groupName, ctx);
		
		ctx.close();
	
		return users;
    }

    /** 
     * Gets a list of users which is memebers of the given group
     */
    public List getGroupUsers(String groupName, DirContext ctx) throws Exception
    {
	    logger.info("--------getGroupUsers(String groupName) start---------------");
		List users = new ArrayList();

		String groupBase 			= this.extraProperties.getProperty("groupBase");
		String groupsFilter 			= this.extraProperties.getProperty("groupsFilter");
		String groupsAttributeFilter = this.extraProperties.getProperty("groupsAttributesFilter");
		String groupNameAttribute	= this.extraProperties.getProperty("groupNameAttribute");
		String usersAttributeFilter = this.extraProperties.getProperty("usersAttributesFilter");
		String userNameAttribute	= this.extraProperties.getProperty("userNameAttributeFilter");
		String userBase 			= this.extraProperties.getProperty("userBase");
		
		try 
		{
			String baseDN = groupBase;
			String searchFilter = "(cn=InfoGlue*)";
			if(groupsFilter != null && groupsFilter.length() > 0)
				searchFilter = groupsFilter;
			
			String groupsAttribute = "distinguishedName";
			if(groupsAttributeFilter != null && groupsAttributeFilter.length() > 0)
				groupsAttribute = groupsAttributeFilter;
	
			String[] attrID = groupsAttribute.split(",");
			
			SearchControls ctls = new SearchControls(); 
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(attrID);
	
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 

			if(!answer.hasMore())
				throw new Exception("The was no groups found in the JNDI Data Source.");
		
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				logger.info("Group:" + sr.toString() + "\n");
				
				Attributes attributes = sr.getAttributes();
				logger.info("attributes:" + attributes.toString());
				logger.info("groupNameAttribute:" + groupNameAttribute);
				Attribute attribute = attributes.get(groupNameAttribute);
				logger.info("attribute:" + attribute.toString());
				NamingEnumeration allEnum = attribute.getAll();
				while(allEnum.hasMore())
				{
					String foundGroupName = (String)allEnum.next();
					logger.info("foundGroupName:" + foundGroupName);
					
					logger.info(foundGroupName + "=" + groupName);
					if(foundGroupName.equals(groupName))
					{
					    Attribute usersAttribute = attributes.get(usersAttributeFilter);
						logger.info("usersAttribute:" + usersAttribute.toString());
						
						List groups = new ArrayList();
						NamingEnumeration allUsersEnum = usersAttribute.getAll();
						while(allUsersEnum.hasMore())
						{
							String userName = (String)allUsersEnum.next();
							logger.info("userName:" + userName);
							logger.info("userBase:" + userBase);
							
							if(groupBase != null && userName.indexOf(userBase) > -1)
							{
							    userName = userName.substring(0, userName.indexOf(userBase));
							    userName = userName.substring(0, userName.lastIndexOf(","));
							}
							
							logger.info("userNameAttribute:" + userNameAttribute);
							logger.info("groupName:" + userName);
							logger.info("indexOf:" + userName.indexOf(userNameAttribute));
							if(groupNameAttribute != null && userName.indexOf(userNameAttribute) > -1)
							{
							    userName = userName.substring(userName.indexOf(userNameAttribute) + userNameAttribute.length() + 1);
							}
							
							InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(userName, "", "", "", new ArrayList(), new ArrayList(), false);
						    users.add(infoGluePrincipal);
						}
						
					    //InfoGlueRole infoGlueRole = new InfoGlueRole(groupName, "Not available from JNDI-source");
						//users.add(infoGluePrincipal);
					}
				}
				
			} 

		}
		catch (Exception e) 
		{
			logger.info("Could not find Groups: " + e.getMessage());
		}
	    logger.info("--------------------END---------------------");

		return users;
	}

    /* (non-Javadoc)
     * @see org.infoglue.cms.security.AuthorizationModule#updateInfoGluePrincipal(org.infoglue.cms.entities.management.SystemUserVO, java.lang.String[], java.lang.String[])
     */
    public void updateInfoGluePrincipal(SystemUserVO systemUserVO, String[] roleNames, String[] groupNames) throws Exception
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.infoglue.cms.security.AuthorizationModule#createInfoGlueGroup(org.infoglue.cms.entities.management.GroupVO)
     */
    public void createInfoGlueGroup(GroupVO groupVO) throws Exception
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.infoglue.cms.security.AuthorizationModule#updateInfoGlueGroup(org.infoglue.cms.entities.management.GroupVO, java.lang.String[])
     */
    public void updateInfoGlueGroup(GroupVO roleVO, String[] userNames) throws Exception
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.infoglue.cms.security.AuthorizationModule#deleteInfoGlueGroup(java.lang.String)
     */
    public void deleteInfoGlueGroup(String groupName) throws Exception
    {
        // TODO Auto-generated method stub
        
    }


}
