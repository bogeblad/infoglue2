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

import java.io.*;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.*;
import javax.servlet.http.*;

import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.util.CacheController;

/**
 * This filter protects actions withing InfoGlue from access without authentication. 
 * It is very generic and can use any authentication module. The filter is responsible for reading the
 * settings and invoking the right authentication module.
 */

public class InfoGlueAuthenticationFilter implements Filter 
{
	public final static String INFOGLUE_FILTER_USER = "org.infoglue.cms.security.user";
	
 	public static String loginUrl 				= null;
	public static String invalidLoginUrl 		= null;
	public static String successLoginBaseUrl	= null;
	public static String authenticatorClass 	= null;
	public static String authorizerClass 		= null;
	public static String serverName	 			= null;
 	public static String authConstraint			= null;
	public static String extraParametersFile	= null;
 	public static Properties extraProperties	= null;
	public static String casValidateUrl			= null;
	public static String casServiceUrl			= null;
	public static String casRenew				= null;
 	
	public void init(FilterConfig config) throws ServletException 
	{
		loginUrl 			= config.getInitParameter("org.infoglue.cms.security.loginUrl");
		invalidLoginUrl 	= config.getInitParameter("org.infoglue.cms.security.invalidLoginUrl");
		successLoginBaseUrl = config.getInitParameter("org.infoglue.cms.security.successLoginBaseUrl");
		authenticatorClass 	= config.getInitParameter("org.infoglue.cms.security.authenticatorClass");
		authorizerClass 	= config.getInitParameter("org.infoglue.cms.security.authorizerClass");
		serverName  		= config.getInitParameter("org.infoglue.cms.security.serverName");
		authConstraint 		= config.getInitParameter("org.infoglue.cms.security.authConstraint");
		extraParametersFile	= config.getInitParameter("org.infoglue.cms.security.extraParametersFile");
		casValidateUrl		= config.getInitParameter("org.infoglue.cms.security.casValidateUrl");
		casServiceUrl		= config.getInitParameter("org.infoglue.cms.security.casServiceUrl");
		//casRenew			= config.getInitParameter("org.infoglue.cms.security.casRenew");
			    
		if(extraParametersFile != null)
		{
			try
			{
				extraProperties = new Properties();
				extraProperties.load(CmsPropertyHandler.class.getResourceAsStream("/" + extraParametersFile));	
			}	
			catch(Exception e)
			{
				CmsLogger.logSevere("Error loading properties from file " + "/" + extraParametersFile + ". Reason:" + e.getMessage());
				e.printStackTrace();
			}
		}
	}


    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws ServletException, IOException 
    {		
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
		
		String URI = httpServletRequest.getRequestURI();
		String URL = httpServletRequest.getRequestURL().toString();

		if(URI.indexOf(loginUrl) > -1 || URL.indexOf(loginUrl) > -1 || URI.indexOf(invalidLoginUrl) > -1 || URL.indexOf(invalidLoginUrl) > -1 || URI.indexOf("UpdateCache") > -1  || URI.indexOf("InvokeWorkflow") > -1)
		{
    	    fc.doFilter(request, response); 
			return;
   	 	}
						
		// make sure we've got an HTTP request
		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse))
		  throw new ServletException("InfoGlue Filter protects only HTTP resources");
	
		HttpSession session = ((HttpServletRequest)request).getSession();
		
		String sessionTimeout = CmsPropertyHandler.getProperty("session.timeout");
		if(sessionTimeout == null)
		    sessionTimeout = "1800";
		
		session.setMaxInactiveInterval(new Integer(sessionTimeout).intValue());

		// if our attribute's already present, don't do anything
		//CmsLogger.logInfo("User:" + session.getAttribute(INFOGLUE_FILTER_USER));
		if (session != null && session.getAttribute(INFOGLUE_FILTER_USER) != null) 
		{
		    //CmsLogger.logInfo("Found user in session:" + session.getAttribute(INFOGLUE_FILTER_USER));
		    //if(successLoginBaseUrl != null && !URL.startsWith(successLoginBaseUrl))
		    //{
		    //    checkSuccessRedirect(request, response, URL);
		    //}
		    //else
		    //{
			  	fc.doFilter(request, response);
			    return;
			//}
		}
		
		// otherwise, we need to authenticate somehow
		try
		{
		    String authenticatedUserName = authenticateUser(httpServletRequest, httpServletResponse, fc);
			
			if(authenticatedUserName != null)
			{	
				InfoGluePrincipal user = getAuthenticatedUser(authenticatedUserName);
				if(user == null || (!user.getIsAdministrator() && !hasAuthorizedRole(user)))
				{	
					//throw new Exception("This user is not authorized to log in...");
					httpServletResponse.sendRedirect("unauthorizedLogin.jsp");
					//fc.doFilter(request, response);
					return;
				}
				
				//TODO - we must fix so these caches are individual to the person - now a login will slow down for all
				CacheController.clearCache("authorizationCache");

				// Store the authenticated user in the session
				if(session != null)
					session.setAttribute(INFOGLUE_FILTER_USER, user);
				
			    if(successLoginBaseUrl != null && !URL.startsWith(successLoginBaseUrl))
			    {
			        checkSuccessRedirect(request, response, URL);
			    }
			    else
			    {
				  	fc.doFilter(request, response);
				    return;
			    }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
  	}


  	public void destroy() { }

  	private void checkSuccessRedirect(ServletRequest request, ServletResponse response, String URL) throws ServletException, IOException, UnsupportedEncodingException
  	{
	    String requestURI = ((HttpServletRequest)request).getRequestURI();
		
		String requestQueryString = ((HttpServletRequest)request).getQueryString();
		if(requestQueryString != null)
		    requestQueryString = "?" + requestQueryString;
		else
		    requestQueryString = "";
		
		String redirectUrl = "";			    
		    
		/*
		if(requestURI.indexOf("?") > 0)
			redirectUrl = loginUrl + "&referringUrl=" + URLEncoder.encode(requestURI + requestQueryString, "UTF-8");
		else
			redirectUrl = loginUrl + "?referringUrl=" + URLEncoder.encode(requestURI + requestQueryString, "UTF-8");
		*/
		if(requestURI.indexOf("?") > -1)
			redirectUrl = successLoginBaseUrl + requestURI + URLEncoder.encode(requestQueryString, "UTF-8");
		else
			redirectUrl = successLoginBaseUrl + requestURI + URLEncoder.encode(requestQueryString, "UTF-8");
		
		CmsLogger.logInfo("redirectUrl:" + redirectUrl);
		((HttpServletResponse)response).sendRedirect(redirectUrl);
	}

  	private boolean hasAuthorizedRole(InfoGluePrincipal user)
  	{
  	    boolean isAuthorized = false;

        CmsLogger.logInfo("authConstraint:" + authConstraint);

  	    if(authConstraint == null || authConstraint.equalsIgnoreCase(""))
  	        return true;
  	    
  	    Iterator rolesIterator = user.getRoles().iterator();
  	    while(rolesIterator.hasNext())
  	    {
  	        InfoGlueRole role = (InfoGlueRole)rolesIterator.next();
  	        CmsLogger.logInfo("role:" + role);
  	        if(role.getName().equalsIgnoreCase(authConstraint))
  	        {
  	            isAuthorized = true;
  	            break;
  	        }
  	    }
  	    
  	    return isAuthorized;
  	}

  	private String authenticateUser(HttpServletRequest request, HttpServletResponse response, FilterChain fc) throws ServletException, Exception 
  	{
  		String authenticatedUserName = null;
  		
  		AuthenticationModule authenticationModule = (AuthenticationModule)Class.forName(authenticatorClass).newInstance();
		authenticationModule.setAuthenticatorClass(authenticatorClass);
		authenticationModule.setAuthorizerClass(authorizerClass);
		authenticationModule.setInvalidLoginUrl(invalidLoginUrl);
		//authenticationModule.setSuccessLoginBaseUrl(successLoginBaseUrl);
		authenticationModule.setLoginUrl(loginUrl);
		authenticationModule.setServerName(serverName);
		authenticationModule.setExtraProperties(extraProperties);
		authenticationModule.setCasRenew(casRenew);
		authenticationModule.setCasServiceUrl(casServiceUrl);
		authenticationModule.setCasValidateUrl(casValidateUrl);
		
		authenticatedUserName = authenticationModule.authenticateUser(request, response, fc);
		
		return authenticatedUserName;
  	}
  	
  	
  	/**
  	 * This method fetches the roles and other stuff for the user by invoking the autorizer-module.
  	 */
  	
	private InfoGluePrincipal getAuthenticatedUser(String userName) throws ServletException, Exception 
	{
		AuthorizationModule authorizationModule = (AuthorizationModule)Class.forName(authorizerClass).newInstance();
		authorizationModule.setExtraProperties(extraProperties);
		CmsLogger.logInfo("authorizerClass:" + authorizerClass + ":" + authorizationModule.getClass().getName());
		
		InfoGluePrincipal infoGluePrincipal = authorizationModule.getAuthorizedInfoGluePrincipal(userName);
		CmsLogger.logInfo("infoGluePrincipal:" + infoGluePrincipal);
		CmsLogger.logInfo("roles:" + infoGluePrincipal.getRoles());
		CmsLogger.logInfo("groups:" + infoGluePrincipal.getGroups());
		
		return infoGluePrincipal;		
  	}

	
	//TODO - These getters are an ugly way of getting security properties unless initialized by the filter.
	//We should handle this different later on.
	
	public static void initializeProperties() throws SystemException
	{
		try
		{
		    authenticatorClass 	= CmsPropertyHandler.getProperty("authenticatorClass");
		    authorizerClass 	= CmsPropertyHandler.getProperty("authorizerClass");
		    invalidLoginUrl 	= CmsPropertyHandler.getProperty("invalidLoginUrl");
		    successLoginBaseUrl = CmsPropertyHandler.getProperty("successLoginBaseUrl");
		    loginUrl 			= CmsPropertyHandler.getProperty("loginUrl");
		    serverName 			= CmsPropertyHandler.getProperty("serverName");
		    casRenew 			= CmsPropertyHandler.getProperty("casRenew");
		    casServiceUrl 		= CmsPropertyHandler.getProperty("casServiceUrl");
		    casValidateUrl 		= CmsPropertyHandler.getProperty("casValidateUrl");
		    
		    String extraPropertiesFile = CmsPropertyHandler.getProperty("extraPropertiesFile");
		    
		    if(extraPropertiesFile != null)
			{
				try
				{
					extraProperties = new Properties();
					extraProperties.load(CmsPropertyHandler.class.getResourceAsStream("/" + extraPropertiesFile));	
				}	
				catch(Exception e)
				{
					CmsLogger.logSevere("Error loading properties from file " + "/" + extraPropertiesFile + ". Reason:" + e.getMessage());
					e.printStackTrace();
				}
			}
			    
		    CmsLogger.logInfo("authenticatorClass:" + authenticatorClass);
		    CmsLogger.logInfo("authorizerClass:" + authorizerClass);
		    CmsLogger.logInfo("invalidLoginUrl:" + invalidLoginUrl);
		    CmsLogger.logInfo("successLoginBaseUrl:" + successLoginBaseUrl);
		    CmsLogger.logInfo("loginUrl:" + loginUrl);
		    CmsLogger.logInfo("serverName:" + serverName);
		    CmsLogger.logInfo("casRenew:" + casRenew);
		    CmsLogger.logInfo("casServiceUrl:" + casServiceUrl);
		    CmsLogger.logInfo("casValidateUrl:" + casValidateUrl);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred so we should not complete the transaction:" + e, e);
			throw new SystemException("Setting the security parameters failed: " + e.getMessage(), e);
		}
	}

}
 