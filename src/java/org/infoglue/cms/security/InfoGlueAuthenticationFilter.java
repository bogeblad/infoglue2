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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.Session;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.util.CacheController;

/**
 * This filter protects actions withing InfoGlue from access without authentication. 
 * It is very generic and can use any authentication module. The filter is responsible for reading the
 * settings and invoking the right authentication module.
 */

public class InfoGlueAuthenticationFilter implements Filter 
{
    private final static Logger logger = Logger.getLogger(InfoGlueAuthenticationFilter.class.getName());

	public final static String INFOGLUE_FILTER_USER = "org.infoglue.cms.security.user";
	
 	public static String loginUrl 				= null;
 	public static String logoutUrl 				= null;
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
	public static String casLogoutUrl			= null;
	public static String casRenew				= null;
 	
	public void init(FilterConfig config) throws ServletException 
	{
		loginUrl 			= config.getInitParameter("org.infoglue.cms.security.loginUrl");
		logoutUrl 			= config.getInitParameter("org.infoglue.cms.security.logoutUrl");
		invalidLoginUrl 	= config.getInitParameter("org.infoglue.cms.security.invalidLoginUrl");
		successLoginBaseUrl = config.getInitParameter("org.infoglue.cms.security.successLoginBaseUrl");
		authenticatorClass 	= config.getInitParameter("org.infoglue.cms.security.authenticatorClass");
		authorizerClass 	= config.getInitParameter("org.infoglue.cms.security.authorizerClass");
		serverName  		= config.getInitParameter("org.infoglue.cms.security.serverName");
		authConstraint 		= config.getInitParameter("org.infoglue.cms.security.authConstraint");
		extraParametersFile	= config.getInitParameter("org.infoglue.cms.security.extraParametersFile");
		casValidateUrl		= config.getInitParameter("org.infoglue.cms.security.casValidateUrl");
		casServiceUrl		= config.getInitParameter("org.infoglue.cms.security.casServiceUrl");
		casLogoutUrl		= config.getInitParameter("org.infoglue.cms.security.casLogoutUrl");
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
				logger.error("Error loading properties from file " + "/" + extraParametersFile + ". Reason:" + e.getMessage());
				e.printStackTrace();
			}
		}

		try
		{
			initializeCMSProperties();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws ServletException, IOException 
    {		
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
		
		String URI = httpServletRequest.getRequestURI();
		String URL = httpServletRequest.getRequestURL().toString();

		if(URI.indexOf(loginUrl) > -1 || URL.indexOf(loginUrl) > -1 || URI.indexOf(invalidLoginUrl) > -1 || URL.indexOf(invalidLoginUrl) > -1 || URI.indexOf(logoutUrl) > -1 || URL.indexOf(logoutUrl) > -1 || URI.indexOf("UpdateCache") > -1)
		{
    	    fc.doFilter(request, response); 
			return;
   	 	}
						
		// make sure we've got an HTTP request
		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse))
		  throw new ServletException("InfoGlue Filter protects only HTTP resources");
	
		HttpSession session = ((HttpServletRequest)request).getSession();
		
		String sessionTimeout = CmsPropertyHandler.getSessionTimeout();
		if(sessionTimeout == null)
		    sessionTimeout = "1800";
		
		session.setMaxInactiveInterval(new Integer(sessionTimeout).intValue());

		// if our attribute's already present, don't do anything
		//getLogger().info("User:" + session.getAttribute(INFOGLUE_FILTER_USER));
		if (session != null && session.getAttribute(INFOGLUE_FILTER_USER) != null) 
		{
		    //getLogger().info("Found user in session:" + session.getAttribute(INFOGLUE_FILTER_USER));
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
			boolean isAdministrator = false;

			String userName = request.getParameter("j_username");
			String password = request.getParameter("j_password");
			
			if(userName != null && password != null)
			{
				String administratorUserName = CmsPropertyHandler.getAdministratorUserName();
				String administratorPassword = CmsPropertyHandler.getAdministratorPassword();
				isAdministrator = (userName.equalsIgnoreCase(administratorUserName) && password.equalsIgnoreCase(administratorPassword)) ? true : false;
			}
			
			String authenticatedUserName = userName;
			if(!isAdministrator)
				authenticatedUserName = authenticateUser(httpServletRequest, httpServletResponse, fc);
			
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
				{
					session.setAttribute(INFOGLUE_FILTER_USER, user);
					setUserProperties(session, user);
				}
				
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

    /**
     * Here we set all user preferences given.
     * @param session
     * @param user
     */

    private void setUserProperties(HttpSession session, InfoGluePrincipal user)
	{
		String preferredLanguageCode = CmsPropertyHandler.getPreferredLanguageCode(user.getName());
	    if(preferredLanguageCode != null && preferredLanguageCode.length() > 0)
			session.setAttribute(Session.LOCALE, new java.util.Locale(preferredLanguageCode));
	    else
	        session.setAttribute(Session.LOCALE, java.util.Locale.ENGLISH);
	
		String preferredToolId = CmsPropertyHandler.getPreferredToolId(user.getName());
	    if(preferredToolId != null && preferredToolId.length() > 0)
			session.setAttribute(Session.TOOL_ID, new Integer(preferredToolId));
	    else
	        session.setAttribute(Session.TOOL_ID, new Integer(0));
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
		
		logger.info("redirectUrl:" + redirectUrl);
		((HttpServletResponse)response).sendRedirect(redirectUrl);
	}

  	private boolean hasAuthorizedRole(InfoGluePrincipal user)
  	{
  	    boolean isAuthorized = false;

        logger.info("authConstraint:" + authConstraint);

  	    if(authConstraint == null || authConstraint.equalsIgnoreCase(""))
  	        return true;
  	    
  	    Iterator rolesIterator = user.getRoles().iterator();
  	    while(rolesIterator.hasNext())
  	    {
  	        InfoGlueRole role = (InfoGlueRole)rolesIterator.next();
  	        logger.info("role:" + role);
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
  		
  		AuthenticationModule authenticationModule = AuthenticationModule.getAuthenticationModule(null, null);
  		
  		/*
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
		*/
  		
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
		logger.info("authorizerClass:" + authorizerClass + ":" + authorizationModule.getClass().getName());
		
		InfoGluePrincipal infoGluePrincipal = authorizationModule.getAuthorizedInfoGluePrincipal(userName);
		logger.info("infoGluePrincipal:" + infoGluePrincipal);
		if(infoGluePrincipal != null)
		{
			logger.info("roles:" + infoGluePrincipal.getRoles());
			logger.info("groups:" + infoGluePrincipal.getGroups());
		}
		
		return infoGluePrincipal;		
  	}

	
	//TODO - These getters are an ugly way of getting security properties unless initialized by the filter.
	//We should handle this different later on.
	
	public static void initializeProperties() throws SystemException
	{
	    try
		{
		    authenticatorClass 	= CmsPropertyHandler.getServerNodeProperty("deliver", "authenticatorClass", true, null);
		    authorizerClass 	= CmsPropertyHandler.getServerNodeProperty("deliver", "authorizerClass", true, null);
		    invalidLoginUrl 	= CmsPropertyHandler.getServerNodeProperty("deliver", "invalidLoginUrl", true, null);
		    successLoginBaseUrl = CmsPropertyHandler.getServerNodeProperty("deliver", "successLoginBaseUrl", true, null);
		    loginUrl 			= CmsPropertyHandler.getServerNodeProperty("deliver", "loginUrl", true, null);
		    logoutUrl 			= CmsPropertyHandler.getServerNodeProperty("deliver", "logoutUrl", true, null);
		    serverName 			= CmsPropertyHandler.getServerNodeProperty("deliver", "serverName", true, null);
		    casRenew 			= CmsPropertyHandler.getServerNodeProperty("deliver", "casRenew", true, null);
		    casServiceUrl 		= CmsPropertyHandler.getServerNodeProperty("deliver", "casServiceUrl", true, null);
		    casValidateUrl 		= CmsPropertyHandler.getServerNodeProperty("deliver", "casValidateUrl", true, null);
		    casLogoutUrl 		= CmsPropertyHandler.getServerNodeProperty("deliver", "casLogoutUrl", true, null);
		    
		    /*
		    System.out.println("loginUrl:" + loginUrl);
		    System.out.println("authenticatorClass:" + authenticatorClass);
		    System.out.println("authorizerClass:" + authorizerClass);
		    */
		    
		    String extraPropertiesFile = CmsPropertyHandler.getProperty("extraParametersFile");
		    
		    if(extraPropertiesFile != null)
			{
				try
				{
					extraProperties = new Properties();
					extraProperties.load(CmsPropertyHandler.class.getResourceAsStream("/" + extraPropertiesFile));	
				}	
				catch(Exception e)
				{
				    logger.error("Error loading properties from file " + "/" + extraPropertiesFile + ". Reason:" + e.getMessage());
					e.printStackTrace();
				}
			}
			    
		    logger.info("authenticatorClass:" + authenticatorClass);
		    logger.info("authorizerClass:" + authorizerClass);
		    logger.info("invalidLoginUrl:" + invalidLoginUrl);
		    logger.info("successLoginBaseUrl:" + successLoginBaseUrl);
		    logger.info("loginUrl:" + loginUrl);
		    logger.info("logoutUrl:" + logoutUrl);
		    logger.info("serverName:" + serverName);
		    logger.info("casRenew:" + casRenew);
		    logger.info("casServiceUrl:" + casServiceUrl);
		    logger.info("casValidateUrl:" + casValidateUrl);
		    logger.info("casLogoutUrl:" + casLogoutUrl);
		}
		catch(Exception e)
		{
		    logger.error("An error occurred so we should not complete the transaction:" + e, e);
			throw new SystemException("Setting the security parameters failed: " + e.getMessage(), e);
		}
	}

	//TODO - These getters are an ugly way of getting security properties unless initialized by the filter.
	//We should handle this different later on.
	
	public static void initializeCMSProperties() throws SystemException
	{
	    try
		{
		    String authenticatorClass 	= CmsPropertyHandler.getServerNodeProperty("deliver", "authenticatorClass", true, null);
		    String authorizerClass		= CmsPropertyHandler.getServerNodeProperty("deliver", "authorizerClass", true, null);
		    String invalidLoginUrl 		= CmsPropertyHandler.getServerNodeProperty("deliver", "invalidLoginUrl", true, null);
		    String successLoginBaseUrl 	= CmsPropertyHandler.getServerNodeProperty("deliver", "successLoginBaseUrl", true, null);
		    String loginUrl 			= CmsPropertyHandler.getServerNodeProperty("deliver", "loginUrl", true, null);
		    String logoutUrl 			= CmsPropertyHandler.getServerNodeProperty("deliver", "logoutUrl", true, null);
		    String serverName 			= CmsPropertyHandler.getServerNodeProperty("deliver", "serverName", true, null);
		    String casRenew 			= CmsPropertyHandler.getServerNodeProperty("deliver", "casRenew", true, null);
		    String casServiceUrl 		= CmsPropertyHandler.getServerNodeProperty("deliver", "casServiceUrl", true, null);
		    String casValidateUrl 		= CmsPropertyHandler.getServerNodeProperty("deliver", "casValidateUrl", true, null);
		    String casLogoutUrl 		= CmsPropertyHandler.getServerNodeProperty("deliver", "casLogoutUrl", true, null);
		    String authConstraint		= CmsPropertyHandler.getServerNodeProperty("deliver", "authConstraint", true, null);
		    
		    if(authenticatorClass != null)
		    	InfoGlueAuthenticationFilter.authenticatorClass = authenticatorClass;
		    if(authorizerClass != null)
		    	InfoGlueAuthenticationFilter.authorizerClass = authorizerClass;
		    if(invalidLoginUrl != null)
		    	InfoGlueAuthenticationFilter.invalidLoginUrl = invalidLoginUrl;
		    if(successLoginBaseUrl != null)
		    	InfoGlueAuthenticationFilter.successLoginBaseUrl = successLoginBaseUrl;
		    if(loginUrl != null)
		    	InfoGlueAuthenticationFilter.loginUrl = loginUrl;
		    if(logoutUrl != null)
		    	InfoGlueAuthenticationFilter.logoutUrl = logoutUrl;
		    if(serverName != null)
		    	InfoGlueAuthenticationFilter.serverName = serverName;
		    if(casRenew != null)
		    	InfoGlueAuthenticationFilter.casRenew = casRenew;
		    if(authConstraint != null)
		    	InfoGlueAuthenticationFilter.authConstraint = authConstraint;
		    
		    if(casServiceUrl != null)
		    	InfoGlueAuthenticationFilter.casServiceUrl = casServiceUrl;
		    if(casValidateUrl != null)
		    	InfoGlueAuthenticationFilter.casValidateUrl = casValidateUrl;
		    if(casLogoutUrl != null)
		    	InfoGlueAuthenticationFilter.casLogoutUrl = casLogoutUrl;

			/*
		    System.out.println("loginUrl:" + loginUrl);
		    System.out.println("authenticatorClass:" + authenticatorClass);
		    System.out.println("authorizerClass:" + authorizerClass);
		    */
		    
		    String extraPropertiesString = CmsPropertyHandler.getServerNodeDataProperty("deliver", "extraSecurityParameters", true, null);
		    //System.out.println("extraPropertiesString:" + extraPropertiesString);
		    if(extraPropertiesString != null)
			{
		    	try
				{
		    		extraProperties = new Properties();
					extraProperties.load(new ByteArrayInputStream(extraPropertiesString.getBytes("UTF-8")));
					//extraProperties.list(System.out);
				}	
				catch(Exception e)
				{
				    logger.error("Error loading properties from string. Reason:" + e.getMessage());
					e.printStackTrace();
				}
			}
			    
		    logger.info("authenticatorClass:" + authenticatorClass);
		    logger.info("authorizerClass:" + authorizerClass);
		    logger.info("invalidLoginUrl:" + invalidLoginUrl);
		    logger.info("successLoginBaseUrl:" + successLoginBaseUrl);
		    logger.info("loginUrl:" + loginUrl);
		    logger.info("logoutUrl:" + logoutUrl);
		    logger.info("serverName:" + serverName);
		    logger.info("authConstraint:" + authConstraint);
		    logger.info("casRenew:" + casRenew);
		    logger.info("casServiceUrl:" + casServiceUrl);
		    logger.info("casValidateUrl:" + casValidateUrl);
		    logger.info("casLogoutUrl:" + casLogoutUrl);
		}
		catch(Exception e)
		{
		    logger.error("An error occurred so we should not complete the transaction:" + e, e);
			throw new SystemException("Setting the security parameters failed: " + e.getMessage(), e);
		}
	}

}
 