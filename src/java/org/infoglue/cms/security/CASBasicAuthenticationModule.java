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

import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.infoglue.cms.util.CmsLogger;

import edu.yale.its.tp.cas.client.ProxyTicketValidator;
import edu.yale.its.tp.cas.client.Util;

/**
 * @author Mattias Bogeblad
 *
 * This authentication module authenticates an user against CAS (edu.yale.its.tp.cas)
 * which is a singe sign on service.
 */

public class CASBasicAuthenticationModule implements AuthenticationModule//, AuthorizationModule
{
	private String loginUrl 			= null;
	private String invalidLoginUrl 		= null;
	private String authenticatorClass 	= null;
	private String authorizerClass 		= null;
	private String serverName			= null;
	private String casValidateUrl		= null;
	private String casServiceUrl		= null;
	private String casAuthorizedProxy 	= null;
	private String casRenew				= null;
	private Properties extraProperties	= null;
	
	/**
	 * This method handles all of the logic for checking how to handle a login.
	 */
	
	public String authenticateUser(HttpServletRequest request, HttpServletResponse response, FilterChain fc) throws Exception
	{
		String authenticatedUserName = null;

		HttpSession session = ((HttpServletRequest)request).getSession();

		String ticket = request.getParameter("ticket");
		CmsLogger.logInfo("ticket:" + ticket);
		
		// no ticket?  abort request processing and redirect
		if (ticket == null || ticket.equals("")) 
		{
			if (loginUrl == null) 
			{
				throw new ServletException(
						"When InfoGlueFilter protects pages that do not receive a 'userName' " +
						"parameter, it needs a org.infoglue.cms.security.loginUrl " +
						"filter parameter");
			}
  
			String requestURI = request.getRequestURI();
			CmsLogger.logInfo("requestURI:" + requestURI);

			String redirectUrl = "";

			if(requestURI.indexOf("?") > 0)
				redirectUrl = loginUrl + "&service=" + getService(request) + ((casRenew != null && !casRenew.equals("")) ? "&renew="+ casRenew : "");
			else
				redirectUrl = loginUrl + "?service=" + getService(request) + ((casRenew != null && !casRenew.equals("")) ? "&renew="+ casRenew : "");
	
			CmsLogger.logInfo("redirectUrl:" + redirectUrl);
			response.sendRedirect(redirectUrl);

			return null;
		} 
	   	
		authenticatedUserName = authenticate(ticket);
		CmsLogger.logInfo("authenticatedUserName:" + authenticatedUserName);
		if(authenticatedUserName == null)
		{
			String requestURI = request.getRequestURI();
			CmsLogger.logInfo("requestURI:" + requestURI);
	
			String redirectUrl = "";
	
			if(requestURI.indexOf("?") > 0)
				redirectUrl = loginUrl + "&service=" + getService(request) + ((casRenew != null && !casRenew.equals("")) ? "&renew="+ casRenew : "");
			else
				redirectUrl = loginUrl + "?service=" + getService(request) + ((casRenew != null && !casRenew.equals("")) ? "&renew="+ casRenew : "");
		
			CmsLogger.logInfo("redirectUrl:" + redirectUrl);
			response.sendRedirect(redirectUrl);
	
			return null;
		}

		//fc.doFilter(request, response);
		return authenticatedUserName;
	}
	
	
	/**
	 * This method handles all of the logic for checking how to handle a login.
	 */
	
	public String authenticateUser(Map request) throws Exception
	{
		String authenticatedUserName = null;

		String ticket = (String)request.get("ticket");
		CmsLogger.logInfo("ticket:" + ticket);
		
		// no ticket?  abort request processing and redirect
		if (ticket == null || ticket.equals("")) 
		{
			return null;
		} 
	   	
		authenticatedUserName = authenticate(ticket);
		CmsLogger.logInfo("authenticatedUserName:" + authenticatedUserName);

		return authenticatedUserName;
	}
	
	/**
	 * This method authenticates against the infoglue extranet user database.
	 */
	
	private String authenticate(String ticket) throws Exception
	{
		CmsLogger.logInfo("ticket:" + ticket);
	
		TrustManager[] trustAllCerts = new TrustManager[]
		{
			new X509TrustManager() 
			{
				public java.security.cert.X509Certificate[] getAcceptedIssuers() 
				{
					return null;
				}
				
				public void checkClientTrusted
				(
					java.security.cert.X509Certificate[] certs, String authType) {
					CmsLogger.logInfo("Checking if client is trusted...");
				}
				/*
				public void checkServerTrusted(X509Certificate[] certs, String authType)
				{
					CmsLogger.logInfo("Checking if server is trusted...");				
				}
				*/
				
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
                {
                    // TODO Auto-generated method stub
                    
                }
			}
		};
		
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		
		String authenticatedUserName = null;
		
		/* instantiate a new ProxyTicketValidator */
		ProxyTicketValidator pv = new ProxyTicketValidator();
		
		/* set its parameters */
		pv.setCasValidateUrl(casValidateUrl);
		pv.setService(casServiceUrl);
		pv.setServiceTicket(ticket);

		/* 
		 * If we want to be able to acquire proxy tickets (requires callback servlet to be set up  
		 * in web.xml –- see below)
		 */
		//pv.setProxyCallbackUrl("https://gavin.adm.gu.se:9070/uPortal/CasProxyServlet");
		//pv.setProxyCallbackUrl("http://localhost:8080/infoglueCMSAuthDev/CasProxyServlet");

		//Properties properties = System.getProperties();
		//properties.list(System.out);
		//System.setProperty("javax.net.debug", "all");
		
		/* contact CAS and validate */
		pv.validate(); 

		/* if we want to look at the raw response, we can use getResponse() */
		String xmlResponse = pv.getResponse();
		CmsLogger.logInfo("xmlResponse:" + xmlResponse);
		
		/* read the response */
		if(pv.isAuthenticationSuccesful()) 
		{
			String user = pv.getUser();
			List proxyList = pv.getProxyList();
			authenticatedUserName = pv.getUser();
		} 
		else 
		{
			String errorCode = pv.getErrorCode();
			String errorMessage = pv.getErrorMessage();
			/* handle the error */
		}

		/* The user is now authenticated. */
		/* If we did set the proxy callback url, we can get proxy tickets with: */

		//String proxyTicket = edu.yale.its.tp.cas.proxy.ProxyTicketReceptor.getProxyTicket(pv.getPgtIou(), casServiceUrl);
		CmsLogger.logInfo("proxies:\n " + pv.getProxyList()); 
		
		return authenticatedUserName;
	} 

	/**
	 * Returns either the configured service or figures it out for the current
	 * request.  The returned service is URL-encoded.
	 */
	private String getService(HttpServletRequest request) throws ServletException, Exception 
	{
		// ensure we have a server name or service name
	  	if (serverName == null && casServiceUrl == null)
			throw new ServletException("need one of the following configuration "
		  	+ "parameters: edu.yale.its.tp.cas.client.filter.serviceUrl or "
		  	+ "edu.yale.its.tp.cas.client.filter.serverName");

	  	// use the given string if it's provided
	  	if (casServiceUrl != null)
			return URLEncoder.encode(casServiceUrl, "UTF-8");
	  	else
			// otherwise, return our best guess at the service
			return Util.getService(request, serverName);
	} 
	

	public String getAuthenticatorClass()
	{
		return authenticatorClass;
	}

	public void setAuthenticatorClass(String authenticatorClass)
	{
		this.authenticatorClass = authenticatorClass;
	}

	public String getAuthorizerClass()
	{
		return authorizerClass;
	}

	public void setAuthorizerClass(String authorizerClass)
	{
		this.authorizerClass = authorizerClass;
	}

	public String getInvalidLoginUrl()
	{
		return invalidLoginUrl;
	}

	public void setInvalidLoginUrl(String invalidLoginUrl)
	{
		this.invalidLoginUrl = invalidLoginUrl;
	}

	public String getLoginUrl()
	{
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl)
	{
		this.loginUrl = loginUrl;
	}
	
	public String getServerName()
	{
		return serverName;
	}

	public void setServerName(String string)
	{
		serverName = string;
	}

	public Properties getExtraProperties()
	{
		return this.extraProperties;
	}

	public void setExtraProperties(Properties properties)
	{
		this.extraProperties = properties;
	}
	
	public String getCasRenew()
	{
		return casRenew;
	}

	public void setCasRenew(String casRenew)
	{
		this.casRenew = casRenew;
	}

	public String getCasServiceUrl()
	{
		return casServiceUrl;
	}

	public void setCasServiceUrl(String casServiceUrl)
	{
		this.casServiceUrl = casServiceUrl;
	}

	public String getCasValidateUrl()
	{
		return casValidateUrl;
	}

	public void setCasValidateUrl(String casValidateUrl)
	{
		this.casValidateUrl = casValidateUrl;
	}

	public String getCasAuthorizedProxy()
	{
		return casAuthorizedProxy;
	}

	public void setCasAuthorizedProxy(String casAuthorizedProxy)
	{
		this.casAuthorizedProxy = casAuthorizedProxy;
	}

}
