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
import java.util.Hashtable;
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




/**
 * @author Mattias Bogeblad
 *
 * This authentication module authenticates an user against the ordinary infoglue database.
 */

public class OpenLDAPJNDIBasicAuthorizationModule extends JNDIBasicAuthorizationModule
{
    private final static Logger logger = Logger.getLogger(OpenLDAPJNDIBasicAuthorizationModule.class.getName());

	public Properties getExtraProperties()
	{
		return this.extraProperties;
	}

	public void setExtraProperties(Properties properties)
	{
		this.extraProperties = properties;
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
	
	protected List getRoles(String userName) throws NamingException 
	{
		logger.info("**************************************************");
		logger.info("*In OpenLDAP version							*");
		logger.info("**************************************************");
		logger.info("userName:" + userName);
		
		List roles = new ArrayList();
		
		DirContext ctx 		= null;
		
		String connectionURL 		= this.extraProperties.getProperty("connectionURL");
		String connectionName		= this.extraProperties.getProperty("connectionName");
		String connectionPassword	= this.extraProperties.getProperty("connectionPassword");
		String roleBase 			= this.extraProperties.getProperty("roleBase");
		String userBase				= this.extraProperties.getProperty("userBase");
		String userSearch			= this.extraProperties.getProperty("userSearch");
		String memberOfAttribute	= this.extraProperties.getProperty("memberOfAttribute");

		logger.info("connectionURL:" + connectionURL);
		logger.info("connectionName:" + connectionName);
		logger.info("connectionPassword:" + connectionPassword);
		logger.info("roleBase:" + roleBase);
		logger.info("userBase:" + userBase);
		
		// Create a Hashtable object.
		Hashtable env = new Hashtable();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, connectionURL);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, connectionName);
		env.put(Context.SECURITY_CREDENTIALS, connectionPassword);
		
		try 
		{
			ctx = new InitialDirContext(env); 

			String baseDN = userBase;
			String searchFilter = "(CN=" + userName +")";
			if(userSearch != null && userSearch.length() > 0)
				searchFilter = userSearch.replaceAll("\\{1\\}", userName);
			
			String memberfAttributeFilter = "memberOf";
			if(memberOfAttribute != null && memberOfAttribute.length() > 0)
				memberfAttributeFilter = memberOfAttribute;
			
			String[] attrID = memberfAttributeFilter.split(",");
			
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
				Attribute attribute = attributes.get("memberof");
				logger.info("attribute:" + attribute.toString());
				NamingEnumeration allEnum = attribute.getAll();
				while(allEnum.hasMore())
				{
					String groupName = (String)allEnum.next();
					logger.info("groupName:" + groupName);
					InfoGlueRole infoGlueRole = new InfoGlueRole(groupName, "");
					roles.add(infoGlueRole);
				}
				
			} 
			ctx.close();
		}
		catch (Exception e) 
		{
			logger.warn("Could not find Group for empID: " +userName +e);
			e.printStackTrace();
		}

		return roles;
	}

}
