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

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.deliver.applications.databeans.DeliveryContext;
import org.infoglue.deliver.controllers.kernel.impl.simple.*;
import org.infoglue.deliver.invokers.ComponentBasedHTMLPageInvoker;
import org.infoglue.deliver.invokers.DecoratedComponentBasedHTMLPageInvoker;
import org.infoglue.deliver.invokers.HTMLPageInvoker;
import org.infoglue.deliver.invokers.PageInvoker;
import org.infoglue.deliver.services.StatisticsService;
import org.infoglue.deliver.util.BrowserBean;
import org.infoglue.deliver.util.CacheController;
import org.infoglue.cms.controllers.kernel.impl.simple.AccessRightController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeVersionControllerProxy;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.services.*;
import org.infoglue.cms.util.*;
import org.infoglue.cms.exception.*;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.entities.management.SiteNodeTypeDefinitionVO;

import java.net.URLEncoder;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This is the main delivery action. Gets called when the user clicks on a link that goes inside the site.
 *
 * @author Mattias Bogeblad
 */

public class ViewPageAction extends InfoGlueAbstractAction 
{
	
	//These are the standard parameters which uniquely defines which page to show.
	private Integer siteNodeId = null;
	private Integer contentId  = null; 
	private Integer languageId = null;
	
	//This parameter are set if you want to access a certain repository startpage
	private String repositoryName = null;
	
	//A cached nodeDeliveryController
	protected NodeDeliveryController nodeDeliveryController					= null;
	protected IntegrationDeliveryController integrationDeliveryController 	= null;
	protected TemplateController templateController 						= null;
		
	private static final boolean USE_LANGUAGE_FALLBACK        			= true;
	private static final boolean DO_NOT_USE_LANGUAGE_FALLBACK 			= false;
	
	//The browserbean
	private BrowserBean browserBean = null;
	private Principal principal = null;
		
	//A possibility to set the referer address
	private String referer = null;

	/**
	 * The constructor for this action - contains nothing right now.
	 */
    
    public ViewPageAction() 
    {
    }
    
    /**
     * This method is the application entry-point. The parameters has been set through the setters
     * and now we just have to render the appropriate output. 
     */
         
    public String doExecute() throws Exception
    {
    	long start			= new Date().getTime();
    	long elapsedTime 	= 0;
    	
    	CmsLogger.logInfo("************************************************");
    	CmsLogger.logInfo("* ViewPageAction was called....                *");
    	CmsLogger.logInfo("************************************************");
    	
    	validateAndModifyInputParameters();
    	
    	String pageKey  = "" + this.siteNodeId + "_" + this.languageId + "_" + this.contentId + "_" + browserBean.getUseragent();
    	CmsLogger.logInfo("pageKey:" + pageKey);
    	String pagePath	= null;
    	
    	this.nodeDeliveryController			= NodeDeliveryController.getNodeDeliveryController(this.siteNodeId, this.languageId, this.contentId);
		this.integrationDeliveryController	= IntegrationDeliveryController.getIntegrationDeliveryController(this.siteNodeId, this.languageId, this.contentId);
		this.templateController 			= getTemplateController(getSiteNodeId(), getLanguageId(), getContentId(), getRequest(), (InfoGluePrincipal)this.principal);
		
		boolean isUserRedirected = false;
		Integer protectedSiteNodeVersionId = this.nodeDeliveryController.getProtectedSiteNodeVersionId(siteNodeId);
		CmsLogger.logInfo("protectedSiteNodeVersionId:" + protectedSiteNodeVersionId);
		if(protectedSiteNodeVersionId != null)
			isUserRedirected = handleExtranetLogic(protectedSiteNodeVersionId);
	
		CmsLogger.logInfo("handled extranet users");

		if(!isUserRedirected)
		{	
			CmsLogger.logInfo("this.templateController.getPrincipal():" + this.templateController.getPrincipal());
			DeliveryContext deliveryContext = DeliveryContext.getDeliveryContext(/*(InfoGluePrincipal)this.principal*/);
			deliveryContext.setRepositoryName(this.repositoryName);
			deliveryContext.setSiteNodeId(this.siteNodeId);
			deliveryContext.setContentId(this.contentId);
			deliveryContext.setLanguageId(this.languageId);
			deliveryContext.setPageKey(pageKey);
			deliveryContext.setSession(this.getSession());
			deliveryContext.setWebworkAbstractAction(this);
			deliveryContext.setHttpServletRequest(this.getRequest());
			deliveryContext.setHttpServletResponse(this.getResponse());
			
			Integer siteNodeTypeDefinitionId = SiteNodeController.getSiteNodeVOWithId(this.siteNodeId).getSiteNodeTypeDefinitionId();
			SiteNodeTypeDefinitionVO siteNodeTypeDefinitionVO = SiteNodeTypeDefinitionController.getController().getSiteNodeTypeDefinitionVOWithId(siteNodeTypeDefinitionId);
			String invokerClassName = siteNodeTypeDefinitionVO.getInvokerClassName();
			
			PageInvoker pageInvoker = new HTMLPageInvoker(this.getRequest(), this.getResponse(), this.templateController, deliveryContext);
			if(invokerClassName.equals("org.infoglue.deliver.invokers.ComponentBasedHTMLPageInvoker"))
			{
				pageInvoker = new ComponentBasedHTMLPageInvoker(this.getRequest(), this.getResponse(), this.templateController, deliveryContext);
			} 
			
			pageInvoker.deliverPage();
		}
		
		elapsedTime = new Date().getTime() - start;
		CmsLogger.logWarning("The page delivery took " + elapsedTime + "ms");
		CmsLogger.logWarning("The memory consumption was " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + "(" + Runtime.getRuntime().totalMemory() + "/" + Runtime.getRuntime().maxMemory() + ") bytes");
        
        StatisticsService.getStatisticsService().registerRequest(getRequest(), getResponse(), pagePath, elapsedTime);
		
        return NONE;
    }
    


	/**
	 * This method the renderer for the component editor. 
	 */
         
	public String doRenderDecoratedPage() throws Exception
	{
		long start			= new Date().getTime();
		long elapsedTime 	= 0;
    	
		CmsLogger.logInfo("************************************************");
		CmsLogger.logInfo("* ViewPageAction was called....                *");
		CmsLogger.logInfo("************************************************");
    	
		validateAndModifyInputParameters();
    	
		String pageKey  = "" + this.siteNodeId + "_" + this.languageId + "_" + this.contentId + "_" + browserBean.getUseragent() + "_pagecomponentDecorated";
		CmsLogger.logInfo("A pageKey:" + pageKey);
		String pagePath	= null;
    	
		this.nodeDeliveryController			= NodeDeliveryController.getNodeDeliveryController(this.siteNodeId, this.languageId, this.contentId);
		this.integrationDeliveryController	= IntegrationDeliveryController.getIntegrationDeliveryController(this.siteNodeId, this.languageId, this.contentId);
		this.templateController 			= getTemplateController(getSiteNodeId(), getLanguageId(), getContentId(), getRequest(), (InfoGluePrincipal)this.principal);

		boolean isUserRedirected = false;
		Integer protectedSiteNodeVersionId = this.nodeDeliveryController.getProtectedSiteNodeVersionId(siteNodeId);
		CmsLogger.logInfo("protectedSiteNodeVersionId:" + protectedSiteNodeVersionId);
		if(protectedSiteNodeVersionId != null)
			isUserRedirected = handleExtranetLogic(protectedSiteNodeVersionId);
	
		CmsLogger.logInfo("handled extranet users");

		if(!isUserRedirected)
		{	
			CmsLogger.logInfo("this.templateController.getPrincipal():" + this.templateController.getPrincipal());
	
			DeliveryContext deliveryContext = DeliveryContext.getDeliveryContext(/*this.templateController.getPrincipal()*/);
			deliveryContext.setRepositoryName(this.repositoryName);
			deliveryContext.setSiteNodeId(this.siteNodeId);
			deliveryContext.setLanguageId(this.languageId);
			deliveryContext.setContentId(this.contentId);
			deliveryContext.setPageKey(pageKey);
			deliveryContext.setSession(this.getSession());
			deliveryContext.setWebworkAbstractAction(this);
			deliveryContext.setHttpServletRequest(this.getRequest());
			deliveryContext.setHttpServletResponse(this.getResponse());

			Integer siteNodeTypeDefinitionId = SiteNodeController.getSiteNodeVOWithId(this.siteNodeId).getSiteNodeTypeDefinitionId();
			SiteNodeTypeDefinitionVO siteNodeTypeDefinitionVO = SiteNodeTypeDefinitionController.getController().getSiteNodeTypeDefinitionVOWithId(siteNodeTypeDefinitionId);
			String invokerClassName = siteNodeTypeDefinitionVO.getInvokerClassName();
			
			PageInvoker pageInvoker = new HTMLPageInvoker(this.getRequest(), this.getResponse(), this.templateController, deliveryContext);
			if(invokerClassName.equals("org.infoglue.deliver.invokers.ComponentBasedHTMLPageInvoker"))
			{
				pageInvoker = new DecoratedComponentBasedHTMLPageInvoker(this.getRequest(), this.getResponse(), this.templateController, deliveryContext);
			} 
			
			pageInvoker.deliverPage();
		}
		
		elapsedTime = new Date().getTime() - start;
		CmsLogger.logWarning("The page delivery took " + elapsedTime + "ms");
        
		StatisticsService.getStatisticsService().registerRequest(getRequest(), getResponse(), pagePath, elapsedTime);
		
		return NONE;
	}
    
  
    
   	/**
	 * This method should be much more sophisticated later and include a check to see if there is a 
	 * digital asset uploaded which is more specialized and can be used to act as serverside logic to the template.
	 * The method also consideres wheter or not to invoke the preview-version with administrative functioality or the 
	 * normal site-delivery version.
	 */
	
	public TemplateController getTemplateController(Integer siteNodeId, Integer languageId, Integer contentId, HttpServletRequest request, InfoGluePrincipal infoGluePrincipal) throws SystemException, Exception
	{
		TemplateController templateController = new BasicTemplateController(infoGluePrincipal);
		templateController.setStandardRequestParameters(siteNodeId, languageId, contentId);	
		templateController.setHttpRequest(request);	
		templateController.setBrowserBean(browserBean);
		templateController.setDeliveryControllers(this.nodeDeliveryController, null, this.integrationDeliveryController);	
		
		String operatingMode = CmsPropertyHandler.getProperty("operatingMode");
		String editOnSite = CmsPropertyHandler.getProperty("editOnSite");
		boolean isEditOnSightDisabled = templateController.getIsEditOnSightDisabled();
		if(!isEditOnSightDisabled && operatingMode != null && (operatingMode.equals("0") || operatingMode.equals("1") || operatingMode.equals("2")) && editOnSite != null && editOnSite.equalsIgnoreCase("true"))
		{
			templateController = new EditOnSiteBasicTemplateController(infoGluePrincipal);
			templateController.setStandardRequestParameters(siteNodeId, languageId, contentId);	
			templateController.setHttpRequest(request);	
			templateController.setBrowserBean(browserBean);
			templateController.setDeliveryControllers(this.nodeDeliveryController, null, this.integrationDeliveryController);	
		}
		
		return templateController;		
	}


	/**
	 * Here we do all modifications needed on the request. For example we read the startpage if no
	 * siteNodeId is given and stuff like that. Also a good place to put url-rewriting.
	 * Rules so far includes: defaulting to the first repository if not specified and also defaulting to
	 * masterlanguage for that site if not specifying.
	 */
	 
	private void validateAndModifyInputParameters() throws SystemException, Exception
	{
		this.browserBean = new BrowserBean();
		this.browserBean.setRequest(getRequest());
		
		if(getSiteNodeId() == null)
		{
			if(getRepositoryName() == null)
			{
				setRepositoryName(RepositoryDeliveryController.getRepositoryDeliveryController().getMasterRepository().getName());
			}
			
			SiteNodeVO rootSiteNodeVO = NodeDeliveryController.getRootSiteNode(getRepositoryName());	
			if(rootSiteNodeVO == null)
				throw new SystemException("There was no repository called " + getRepositoryName() + " or no pages were available in that repository");
			
			setSiteNodeId(rootSiteNodeVO.getSiteNodeId());
		} 
		
		if(getLanguageId() == null)
		{
			LanguageVO browserLanguageVO = LanguageDeliveryController.getLanguageDeliveryController().getLanguageIfRepositorySupportsIt(browserBean.getLanguageCode(), getSiteNodeId());
			if(browserLanguageVO != null)
			{
				setLanguageId(browserLanguageVO.getLanguageId());
			}
			else
			{
				LanguageVO masterLanguageVO = LanguageDeliveryController.getLanguageDeliveryController().getMasterLanguageForSiteNode(this.getSiteNodeId());
				if(masterLanguageVO == null)
					throw new SystemException("There was no master language for the siteNode " + getSiteNodeId());
	
				setLanguageId(masterLanguageVO.getLanguageId());				
			}
		}
		
		this.principal = (Principal)this.getHttpSession().getAttribute("infogluePrincipal");
		if(this.principal == null)
		{
			try
			{
				this.principal = (Principal)CacheController.getCachedObject("userCache", "anonymous");
				if(this.principal == null)
				{
				    Map arguments = new HashMap();
				    arguments.put("j_username", "anonymous");
				    arguments.put("j_password", "anonymous");
					this.principal = ExtranetController.getController().getAuthenticatedPrincipal(arguments);
					if(principal != null)
						CacheController.cacheObject("userCache", "anonymous", this.principal);
				}
				//this.principal = ExtranetController.getController().getAuthenticatedPrincipal("anonymous", "anonymous");
				
			}
			catch(Exception e) 
			{
			    throw new SystemException("There was no anonymous user found in the system. There must be - add the user anonymous/anonymous and try again.", e);
			}
		}
	}

	/**
	 * This method validates that the current page is accessible to the requesting user.
	 * It fetches information from the page metainfo about if the page is protected and if it is 
	 * validates the users credentials against the extranet database,
	 */
	
	public boolean handleExtranetLogic(Integer protectedSiteNodeVersionId) throws SystemException, Exception
	{
		boolean isRedirected = false;
		
		try
		{
		    String referer = this.getRequest().getHeader("Referer");
			CmsLogger.logInfo("referer:" + referer);

			if(referer == null)
				referer = "/"; 
			
			Principal principal = (Principal)this.getHttpSession().getAttribute("infogluePrincipal");
			CmsLogger.logInfo("principal:" + principal);
			if(principal == null)
			{
				try
				{
					principal = (Principal)CacheController.getCachedObject("userCache", "anonymous");
					if(principal == null)
					{
					    Map arguments = new HashMap();
					    arguments.put("j_username", "anonymous");
					    arguments.put("j_password", "anonymous");

						principal = ExtranetController.getController().getAuthenticatedPrincipal(arguments);
						if(principal != null)
							CacheController.cacheObject("userCache", "anonymous", principal);
					}
					
					if(principal != null)
					{
					    this.getHttpSession().setAttribute("infogluePrincipal", principal);
					    
						SiteNodeVersionVO siteNodeVersionVO = this.nodeDeliveryController.getActiveSiteNodeVersionVO(siteNodeId);
						boolean isAuthorized = AccessRightController.getController().getIsPrincipalAuthorized((InfoGluePrincipal)principal, "SiteNodeVersion.Read", siteNodeVersionVO.getId().toString());
						if(!isAuthorized)
						{	
							CmsLogger.logInfo("SiteNode is protected and user was not found - sending him to login page.");
							String url = "ExtranetLogin!loginForm.action?returnAddress=" + URLEncoder.encode(this.getRequest().getRequestURL().toString() + "?" + this.getRequest().getQueryString() + "&referer=" + URLEncoder.encode(referer, "UTF-8") + "&date=" + System.currentTimeMillis(), "UTF-8");
							getResponse().sendRedirect(url);
							isRedirected = true;
						}
					}
				}
				catch(Exception e) 
				{
				    throw new SystemException("There was no anonymous user found in the system. There must be - add the user anonymous/anonymous and try again.", e);
				}
			}
			else
			{
				CmsLogger.logInfo("protectedSiteNodeVersionId:" + protectedSiteNodeVersionId);
				//Integer protectedSiteNodeVersionId = this.nodeDeliveryController.getProtectedSiteNodeVersionId(siteNodeId);
				
				Map arguments = new HashMap();
				arguments.put("j_username", "anonymous");
			    arguments.put("j_password", "anonymous");
			    
				if(protectedSiteNodeVersionId != null && !AccessRightController.getController().getIsPrincipalAuthorized((InfoGluePrincipal)principal, "SiteNodeVersion.Read", protectedSiteNodeVersionId.toString()) &&  !AccessRightController.getController().getIsPrincipalAuthorized((InfoGluePrincipal)ExtranetController.getController().getAuthenticatedPrincipal(arguments), "SiteNodeVersion.Read", protectedSiteNodeVersionId.toString()))
				{	
					if(this.referer == null)
						this.referer = this.getRequest().getHeader("Referer");
					
					if(principal.getName().equals("anonymous"))
					{
						CmsLogger.logInfo("SiteNode is protected and user was anonymous - sending him to login page.");
						String url = "ExtranetLogin!loginForm.action?returnAddress=" + URLEncoder.encode(this.getRequest().getRequestURL().toString() + "?" + this.getRequest().getQueryString() + "&referer=" + URLEncoder.encode(referer, "UTF-8") + "&date=" + System.currentTimeMillis(), "UTF-8");
						getResponse().sendRedirect(url);
						isRedirected = true;
					}
					else
					{
						CmsLogger.logInfo("SiteNode is protected and user has no access - sending him to no access page.");
						//final String url = "ExtranetLogin!noAccess.action?returnAddress=" + java.net.URLEncoder.encode(this.getRequest().getRequestURL().toString() + "?" + this.getRequest().getQueryString(), "UTF-8");
						String url = "ExtranetLogin!noAccess.action?referer=" + URLEncoder.encode(this.referer, "UTF-8") + "&date=" + System.currentTimeMillis();
						getResponse().sendRedirect(url);
						isRedirected = true;
					}
				}
			}
		}
		catch(SystemException se)
		{
			se.printStackTrace();
			throw se;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return isRedirected;
	}
	
	/**
	 * Setters and getters for all things sent to the page in the request
	 */
	
    public java.lang.Integer getSiteNodeId()
    {
        return this.siteNodeId;
    }
        
    public void setSiteNodeId(Integer siteNodeId)
    {
        this.siteNodeId = siteNodeId;
	}

    public Integer getContentId()
    {
        return this.contentId;
    }
        
    public void setContentId(Integer contentId)
    {
    	this.contentId = contentId;
    }

    public Integer getLanguageId()
    {
        return this.languageId;
    }
        
    public void setLanguageId(Integer languageId)
    {
		this.languageId = languageId;   
	}

    public String getRepositoryName()
    {
        return this.repositoryName;
    }
        
    public void setRepositoryName(String repositoryName)
    {
	    this.repositoryName = repositoryName;
    }
    
	public String getReferer()
	{
		return referer;
	}

	public void setReferer(String referer)
	{
		this.referer = referer;
	}

}
