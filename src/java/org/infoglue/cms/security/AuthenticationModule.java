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

import java.util.Map;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interface defines what a authenticationmodule has to fulfill.
 * 
 * @author Mattias Bogeblad
 */

public interface AuthenticationModule
{

	public String authenticateUser(HttpServletRequest request, HttpServletResponse response, FilterChain fc) throws Exception;

	public String authenticateUser(Map request) throws Exception;

	public String getAuthenticatorClass();

	public void setAuthenticatorClass(String authenticatorClass);

	public String getAuthorizerClass();

	public void setAuthorizerClass(String authorizerClass);

	public String getInvalidLoginUrl();

	public void setInvalidLoginUrl(String invalidLoginUrl);

	public String getLoginUrl();

	public void setLoginUrl(String loginUrl);

	public String getServerName();

	public void setServerName(String serverName);

	public Properties getExtraProperties();

	public void setExtraProperties(Properties properties);
	
	public String getCasRenew();

	public void setCasRenew(String casRenew);

	public String getCasServiceUrl();

	public void setCasServiceUrl(String casServiceUrl);

	public String getCasValidateUrl();

	public void setCasValidateUrl(String casValidateUrl);
	
	public String getCasAuthorizedProxy();

	public void setCasAuthorizedProxy(String casAuthorizedProxy);
	
}