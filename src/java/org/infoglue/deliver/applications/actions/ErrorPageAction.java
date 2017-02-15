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


package org.infoglue.deliver.applications.actions;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.controllers.kernel.impl.simple.RepositoryDeliveryController;
import org.infoglue.deliver.util.HttpHelper;


/**
 * This is an error page action. Used to send out the right error codes and the right html
 *
 * @author Mattias Bogeblad
 */

public class ErrorPageAction extends InfoGlueAbstractAction 
{
	private static final long serialVersionUID = 8789782748698688734L;

	private final static Logger logger = Logger.getLogger(ErrorPageAction.class.getName());

	private int responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

	private String getErrorUrl(Integer repositoryId, LanguageVO currentLanguage) throws Exception
	{
		String errorUrl = CmsPropertyHandler.getErrorUrl();

		String isErrorPage = getRequest().getParameter("isErrorPage");

		if (isErrorPage == null || isErrorPage.equals(""))
		{
			String repositoryErrorUrl = RepositoryDeliveryController.getRepositoryDeliveryController().getExtraPropertyValue(repositoryId, "errorUrl_" + currentLanguage.getLanguageCode());
			if (repositoryErrorUrl == null || repositoryErrorUrl.equals(""))
			{
				logger.info("Found no language specific error page. Checking for non-suffixed version.");
				repositoryErrorUrl = RepositoryDeliveryController.getRepositoryDeliveryController().getExtraPropertyValue(repositoryId, "errorUrl");
			}
			if (repositoryErrorUrl != null && !repositoryErrorUrl.equals(""))
			{
				errorUrl = repositoryErrorUrl;
			}

			if(errorUrl != null)
			{
				errorUrl = errorUrl + (errorUrl.indexOf("?") > -1 ? "&" : "?") + "isErrorPage=true";
			}
		}
		
		return errorUrl;
	}

	private Set<RepositoryVO> getRepositoryId(HttpServletRequest request) throws ServletException, SystemException, Exception
	{
		String serverName = request.getServerName();
		String portNumber = new Integer(request.getServerPort()).toString();
		String repositoryName = request.getParameter("repositoryName");
		String requestUri = (String)request.getAttribute("javax.servlet.forward.request_uri");
		if (requestUri == null)
		{
			requestUri = request.getRequestURI();
		}

		Set<RepositoryVO> repositories = RepositoryDeliveryController.getRepositoryDeliveryController().getRepositoryVOListFromServerName(serverName, portNumber, repositoryName, requestUri);

		return repositories;
	}

	/**
	 * This is the excecute method - it will send the right error codes and also show the right error message.
	 */

	public String doExecute() throws Exception
	{
		try
		{
			String responseCodeAttribute = (String)this.getRequest().getAttribute("responseCode");
			if(responseCodeAttribute != null)
				responseCode = Integer.parseInt(responseCodeAttribute);

			String responseCodeParameter = (String)this.getRequest().getParameter("responseCode");
			if(responseCodeParameter != null)
				responseCode = Integer.parseInt(responseCodeParameter);

			String errorUrlAttribute = (String)this.getRequest().getAttribute("errorUrl");
			String errorUrlParameter = (String)this.getRequest().getParameter("errorUrl");

			Exception e = (Exception)this.getRequest().getAttribute("error");
			if(e != null)
			{
				setError(e, e.getCause());
			}

			this.getResponse().setContentType("text/html; charset=UTF-8");
			this.getResponse().setStatus(responseCode);
	
			String errorUrl = CmsPropertyHandler.getErrorUrl();
			if(errorUrlAttribute != null && !errorUrlAttribute.equals(""))
			{
				errorUrl = errorUrlAttribute;
			}
			else if(errorUrlParameter != null && !errorUrlParameter.equals(""))
			{
				errorUrl = errorUrlParameter;
			}

			Set<RepositoryVO> repositoryVOList = getRepositoryId(this.getRequest());
			LanguageVO currentLanguage = null;
			if(repositoryVOList != null && repositoryVOList.size() > 0)
			{
				if (repositoryVOList.size() > 1)
				{
					logger.info("Found more than repository for the error URL <" + this.getRequest().getRequestURI() + ">. List: " + repositoryVOList.toString());
				}
				RepositoryVO repositoryVO = (RepositoryVO)repositoryVOList.toArray()[0];
				Integer currentLanguageId = (Integer)this.getRequest().getAttribute("languageId");
				if (currentLanguageId != null)
				{
					try
					{
						currentLanguage = LanguageController.getController().getLanguageVOWithId(currentLanguageId);
					}
					catch (Exception ex)
					{
						logger.warn("Failed to get the current language for error URL. Falling back to repository master language. Current language id: " + currentLanguageId);
					}
				}
				if (currentLanguage == null)
				{
					currentLanguage = LanguageController.getController().getMasterLanguage(repositoryVO.getRepositoryId());
				}


				String localErrorUrl = getErrorUrl(repositoryVO.getId(), currentLanguage);
				if(localErrorUrl != null)
				{
					errorUrl = localErrorUrl;
				}
			}

			if(errorUrl == null || errorUrl.indexOf("@errorUrl@") > -1)
			{
				logger.error("No valid error url was defined:" + errorUrl + ". You should fix this. Defaulting to /error.jsp");
				errorUrl = "/error.jsp";
			}

			Integer closestExistingParentSiteNodeId = (Integer)this.getRequest().getAttribute("closestExistingParentSiteNodeId");
			if(errorUrl != null && errorUrl.indexOf("@errorUrl@") == -1)
			{
				if(errorUrl.indexOf("http") > -1)
				{
					if(CmsPropertyHandler.getResponseMethodOnFullErrorURL().equalsIgnoreCase("include"))
					{
						HttpHelper helper = new HttpHelper();
						Map<String,String> requestParameters = new HashMap<String,String>();
						if(e != null)
						{
							requestParameters.put("errorMessage", e.getMessage());
						}
						requestParameters.put("errorURL", errorUrl);
						if (currentLanguage != null)
						{
							requestParameters.put("languageId", currentLanguage.getLanguageId().toString());
						}
						if (closestExistingParentSiteNodeId != null)
						{
							requestParameters.put("closestExistingParentSiteNodeId", closestExistingParentSiteNodeId.toString());
						}
						String urlContent = helper.getUrlContent(errorUrl, new HashMap<String, String>(), requestParameters, "utf-8", 5000);

						getResponse().setContentType("text/html; charset=utf-8");
						getResponse().setStatus(404);

						PrintWriter out = getResponse().getWriter();
						out.println(urlContent);
					}
					else
					{
						this.getResponse().sendRedirect(errorUrl);
					}
				}
				else
				{
					try
					{
						RequestDispatcher dispatch = this.getRequest().getRequestDispatcher(errorUrl);
						this.getRequest().setAttribute("error", e);
						if (closestExistingParentSiteNodeId != null)
						{
							this.getRequest().setAttribute("closestExistingParentSiteNodeId", closestExistingParentSiteNodeId);
						}
						if (currentLanguage != null)
						{
							this.getRequest().setAttribute("language", currentLanguage);
						}
						dispatch.include(this.getRequest(), this.getResponse());
					}
					catch(Exception e2)
					{
						e2.printStackTrace();
						return SUCCESS;
					}
				}

				return NONE;
			}
			else
			{
				logger.error("No valid error url was defined:" + errorUrl + ". You should fix this.");
				return SUCCESS;
			}
		}
		catch(Throwable t)
		{
			logger.error("Error executing ErrorPage action:" + t.getMessage());
			if (logger.isDebugEnabled())
			{
				logger.debug("Error executing ErrorPage action:" + t.getMessage(), t);
			}

			return SUCCESS;
		}
	}

	/**
	 * This is the busy method - it will send the right error codes and also show the right error message.
	 */
	public String doBusy() throws Exception
	{
		String responseCodeAttribute = (String)this.getRequest().getAttribute("responseCode");
		if(responseCodeAttribute != null)
		{
			responseCode = Integer.parseInt(responseCodeAttribute);
		}

		String responseCodeParameter = (String) this.getRequest().getParameter("responseCode");
		if (responseCodeParameter != null)
		{
			responseCode = Integer.parseInt(responseCodeParameter);
		}

		Exception e = (Exception) this.getRequest().getAttribute("error");
		if (e != null)
		{
			setError(e, e.getCause());
		}

		this.getResponse().setContentType("text/html; charset=UTF-8");
		this.getResponse().setStatus(responseCode);

		String errorUrl = CmsPropertyHandler.getErrorBusyUrl();
		if (errorUrl != null && errorUrl.indexOf("@errorBusyUrl@") == -1)
		{
			if (errorUrl.indexOf("http") > -1)
			{
				this.getResponse().sendRedirect(errorUrl);
			}
			else
			{
				RequestDispatcher dispatch = this.getRequest().getRequestDispatcher(errorUrl);
				this.getRequest().setAttribute("error", e);
				// dispatch.forward(this.getRequest(), this.getResponse());
				dispatch.include(this.getRequest(), this.getResponse());
			}

			return NONE;
		}
		else
		{
			return SUCCESS;
		}
	}

	public int getResponseCode()
	{
		return responseCode;
	}
}
