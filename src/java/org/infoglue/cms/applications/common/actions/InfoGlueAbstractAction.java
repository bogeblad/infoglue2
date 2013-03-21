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

package org.infoglue.cms.applications.common.actions;

import java.awt.Color;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.applications.databeans.LinkBean;
import org.infoglue.cms.applications.databeans.ProcessBean;
import org.infoglue.cms.controllers.kernel.impl.simple.AccessRightController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.InfoGluePrincipalControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.RegistryController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeVersionControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.ToolbarController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.DigitalAssetVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.AuthenticationModule;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.controllers.kernel.impl.simple.ExtranetController;
import org.infoglue.deliver.util.CacheController;
import org.infoglue.deliver.util.LiveInstanceMonitor;
import org.infoglue.deliver.util.graphics.ColorHelper;
import org.infoglue.deliver.util.ioqueue.PublicationQueue;
import org.infoglue.deliver.util.ioqueue.PublicationQueueBean;

import webwork.action.ActionContext;
import webwork.config.Configuration;

/**
 * @author Mattias Bogeblad
 *
 * This is an abstract action used for all InfoGlue actions. Just to not have to put to much in the WebworkAbstractAction.
 */

public abstract class InfoGlueAbstractAction extends WebworkAbstractAction
{
    private final static Logger logger = Logger.getLogger(InfoGlueAbstractAction.class.getName());

    protected String colorScheme = null; 
    
	/**
	 * This method lets the velocity template get hold of all actions inheriting.
	 * 
	 * @return The action object currently invoked 
	 */
	
	public InfoGlueAbstractAction getThis()
	{
		return this;
	}
	
	/**
	 * This method returns the logout url.
	 * @author Mattias Bogeblad
	 */
	
	public String getLogoutURL() throws Exception
	{
		AuthenticationModule authenticationModule = AuthenticationModule.getAuthenticationModule(null, null, this.getRequest(), false);
	    return authenticationModule.getLogoutUrl();
	}


	/**
	 * This method returns the actions url base.
	 * @author Mattias Bogeblad
	 */
	
	public String getURLBase()
	{
	    return this.getRequest().getContextPath();
	}

	/**
	 * This method returns the current url.
	 * @author Mattias Bogeblad
	 */
	
	public String getCurrentURL()
	{
		return this.getRequest().getRequestURL() + (this.getRequest().getQueryString() == null ? "" : "?" + this.getRequest().getQueryString());
	}

	public String getOriginalFullURL()
	{
    	String originalRequestURL = this.getRequest().getParameter("originalRequestURL");
    	if(originalRequestURL == null || originalRequestURL.length() == 0)
    		originalRequestURL = this.getRequest().getRequestURL().toString();

    	String originalQueryString = this.getRequest().getParameter("originalQueryString");
    	if(originalQueryString == null || originalQueryString.length() == 0)
    		originalQueryString = this.getRequest().getQueryString();

    	return originalRequestURL + (originalQueryString == null ? "" : "?" + originalQueryString);
	}

	/**
	 * This method returns the session timeout value.
	 */
	
	public int getSessionTimeout()
	{
	    return this.getHttpSession().getMaxInactiveInterval();
	}

	/**
	 * Gets a list of tool languages
	 */

	public List getToolLocales()
	{
		return CmsPropertyHandler.getToolLocales();
	}

	public List getToolbarButtons(String toolbarKey, String primaryKey, String extraParameters)
	{
		ToolbarController toolbarController = new ToolbarController();
		return toolbarController.getToolbarButtons(toolbarKey, getInfoGluePrincipal(), getLocale(), primaryKey, extraParameters);
	}

	public List getRightToolbarButtons(String toolbarKey, String primaryKey, String extraParameters, boolean disableCloseButton)
	{
		ToolbarController toolbarController = new ToolbarController();
		return toolbarController.getRightToolbarButtons(toolbarKey, getInfoGluePrincipal(), getLocale(), primaryKey, extraParameters, disableCloseButton);
	}

	public List getFooterToolbarButtons(String toolbarKey, String primaryKey, String extraParameters, boolean disableCloseButton)
	{
		ToolbarController toolbarController = new ToolbarController();
		return toolbarController.getFooterToolbarButtons(toolbarKey, getInfoGluePrincipal(), getLocale(), primaryKey, extraParameters, disableCloseButton);
	}

	/**
	 * This method returns a propertyValue for the logged in user.
	 * 
	 * @author Mattias Bogeblad
	 */
	
	public String getPrincipalPropertyValue(String propertyName, boolean escapeSpecialCharacters)
	{		
		return getPrincipalPropertyValue(propertyName, escapeSpecialCharacters, false);
	}

	/**
	 * This method returns a propertyValue for the logged in user.
	 * 
	 * @author Mattias Bogeblad
	 */
	
	public String getPrincipalPropertyValue(InfoGluePrincipal infoGluePrincipal, String propertyName, boolean escapeSpecialCharacters, boolean findLargestValue)
	{
		logger.info("propertyName: " + propertyName);
		logger.info("escapeSpecialCharacters: " + escapeSpecialCharacters);
		logger.info("findLargestValue: " + findLargestValue);
	    
		String value = "";
		
		try
		{
		    LanguageVO languageVO = (LanguageVO)LanguageController.getController().getLanguageVOList().get(0);
		    value = InfoGluePrincipalControllerProxy.getController().getPrincipalPropertyValue(infoGluePrincipal, propertyName, languageVO.getId(), null, false, escapeSpecialCharacters, findLargestValue);
		}
		catch(Exception e)
		{
		    logger.warn("An error occurred trying to get property " + propertyName + " from infoGluePrincipal:" + e.getMessage(), e);
		}
		
		return value;
	}

	/**
	 * This method returns a propertyValue for the logged in user.
	 * 
	 * @author Mattias Bogeblad
	 */
	
	public String getPrincipalPropertyValue(String propertyName, boolean escapeSpecialCharacters, boolean findLargestValue)
	{
		return getPrincipalPropertyValue(propertyName, escapeSpecialCharacters, findLargestValue, false);
	}
	
	/**
	 * This method returns a propertyValue for the logged in user.
	 * 
	 * @author Mattias Bogeblad
	 */
	
	public String getPrincipalPropertyValue(String propertyName, boolean escapeSpecialCharacters, boolean findLargestValue, boolean findPrioValue)
	{
		logger.info("propertyName: " + propertyName);
		logger.info("escapeSpecialCharacters: " + escapeSpecialCharacters);
		logger.info("findLargestValue: " + findLargestValue);
	    
		String value = "";
		
		try
		{
		    InfoGluePrincipal infoGluePrincipal = this.getInfoGluePrincipal();
		    LanguageVO languageVO = (LanguageVO)LanguageController.getController().getLanguageVOList().get(0);
		    value = InfoGluePrincipalControllerProxy.getController().getPrincipalPropertyValue(infoGluePrincipal, propertyName, languageVO.getId(), null, false, escapeSpecialCharacters, findLargestValue, findPrioValue);
		}
		catch(Exception e)
		{
		    logger.warn("An error occurred trying to get property " + propertyName + " from infoGluePrincipal:" + e.getMessage(), e);
		}
		
		return value;
	}

	/**
	 * Getting a property for a Principal - used for personalisation. 
	 * This method starts with getting the property on the user and if it does not exist we check out the
	 * group-properties as well.
	 */
	
	public Map getPrincipalPropertyHashValues(String propertyName, boolean escapeSpecialCharacters)
	{
		Map value = new HashMap();
		
		try
		{
		    InfoGluePrincipal infoGluePrincipal = this.getInfoGluePrincipal();
		    LanguageVO languageVO = (LanguageVO)LanguageController.getController().getLanguageVOList().get(0);
			value = InfoGluePrincipalControllerProxy.getController().getPrincipalPropertyHashValues(infoGluePrincipal, propertyName, languageVO.getId(), null, false, escapeSpecialCharacters);
		}
		catch(Exception e)
		{
		    logger.warn("An error occurred trying to get property " + propertyName + " from infoGluePrincipal:" + e.getMessage(), e);
		}
		
		return value;
	}	

	public Principal getAnonymousPrincipal() throws SystemException
	{
	    Principal principal = null;
		try
		{
			principal = (Principal)CacheController.getCachedObject("userCache", "anonymous");
			if(principal == null)
			{
			    Map arguments = new HashMap();
			    arguments.put("j_username", CmsPropertyHandler.getAnonymousUser());
			    arguments.put("j_password", CmsPropertyHandler.getAnonymousPassword());
			    arguments.put("ticket", this.getHttpSession().getAttribute("ticket"));

			    principal = ExtranetController.getController().getAuthenticatedPrincipal(arguments, getRequest());
				
				if(principal != null)
					CacheController.cacheObject("userCache", "anonymous", principal);
			}			
		}
		catch(Exception e) 
		{
		    logger.warn("There was no anonymous user found in the system. There must be - add the user anonymous/anonymous and try again.", e);
		    throw new SystemException("There was no anonymous user found in the system. There must be - add the user anonymous/anonymous and try again.", e);
		}

		return principal;
	}
	
	public Principal getInfoGluePrincipal(String userName) throws SystemException
	{
		Principal principal = null;
		try
		{
			principal = (Principal)CacheController.getCachedObject("userCache", userName);
			if(principal == null)
			{
				principal = UserControllerProxy.getController().getUser(userName);
				
				if(principal != null)
					CacheController.cacheObject("userCache", userName, principal);
			}			
		}
		catch(Exception e) 
		{
		    logger.warn("There was no anonymous user found in the system. There must be - add the user anonymous/anonymous and try again.", e);
		    throw new SystemException("There was no anonymous user found in the system. There must be - add the user anonymous/anonymous and try again.", e);
		}

		return principal;
	}
	
	public Principal getInfoGluePrincipal(String userName, Database db) throws SystemException
	{
		Principal principal = null;
		try
		{
			principal = (Principal)CacheController.getCachedObject("userCache", userName);
			if(principal == null)
			{
				principal = UserControllerProxy.getController(db).getUser(userName);
				
				if(principal != null)
					CacheController.cacheObject("userCache", userName, principal);
			}			
		}
		catch(Exception e) 
		{
		    logger.warn("There was no anonymous user found in the system. There must be - add the user anonymous/anonymous and try again.", e);
		    throw new SystemException("There was no anonymous user found in the system. There must be - add the user anonymous/anonymous and try again.", e);
		}

		return principal;
	}
	
	/**
	 * Used by the view pages to determine if the current user has sufficient access rights
	 * to perform the action specific by the interception point name.
	 *
	 * @param interceptionPointName THe Name of the interception point to check access rights
	 * @return True is access is allowed, false otherwise
	 */
	public boolean hasAccessTo(String interceptionPointName)
	{
		logger.info("Checking if " + getUserName() + " has access to " + interceptionPointName);

		try
		{
			return AccessRightController.getController().getIsPrincipalAuthorized(this.getInfoGluePrincipal(), interceptionPointName);
		}
		catch (SystemException e)
		{
		    logger.warn("Error checking access rights", e);
			return false;
		}
	}

	/**
	 * Used by the view pages to determine if the current user has sufficient access rights
	 * to perform the action specific by the interception point name.
	 *
	 * @param interceptionPointName THe Name of the interception point to check access rights
	 * @return True is access is allowed, false otherwise
	 */
	public boolean hasAccessTo(String interceptionPointName, boolean returnSuccessIfInterceptionPointNotDefined)
	{
		logger.info("Checking if " + getUserName() + " has access to " + interceptionPointName);

		try
		{
			return AccessRightController.getController().getIsPrincipalAuthorized(this.getInfoGluePrincipal(), interceptionPointName, returnSuccessIfInterceptionPointNotDefined);
		}
		catch (SystemException e)
		{
		    logger.warn("Error checking access rights", e);
			return false;
		}
	}

	/**
	 * Used by the view pages to determine if the current user has sufficient access rights
	 * to perform the action specific by the interception point name.
	 *
	 * @param interceptionPointName THe Name of the interception point to check access rights
	 * @return True is access is allowed, false otherwise
	 */
	public boolean hasAccessTo(String interceptionPointName, String extraParameter)
	{
		logger.info("Checking if " + getUserName() + " has access to " + interceptionPointName + " with extraParameter " + extraParameter);

		try
		{
		    return AccessRightController.getController().getIsPrincipalAuthorized(this.getInfoGluePrincipal(), interceptionPointName, extraParameter);
		}
		catch (SystemException e)
		{
		    logger.warn("Error checking access rights", e);
			return false;
		}
	}
		
	/**
	 * Gets a protected content id (if any).
	 */
	
	public Integer getProtectedContentId(Integer parentContentId)
	{
		return ContentControllerProxy.getController().getProtectedContentId(parentContentId);
	}

	/**
	 * Gets a protected content id (if any).
	 */
	
	public Integer getProtectedSiteNodeVersionId(Integer parentSiteNodeVersionId)
	{
		return SiteNodeVersionControllerProxy.getSiteNodeVersionControllerProxy().getProtectedSiteNodeVersionId(parentSiteNodeVersionId);
	}
	
	/**
	 * Get the username for the currently logged in user
	 */
	public String getUserName()
	{
		return getInfoGluePrincipal().getName();
	}

	/**
	 * Get a single parameter from the ActionContext (hides Servlet implementation)
	 */
	protected final String getSingleParameter(String parameterName)
	{
		return (String) ActionContext.getSingleValueParameters().get(parameterName);
	}

	/**
	 * Get a parameter (could possibly be an array) from the ActionContext (hides Servlet implementation)
	 */
	protected final String getParameter(String parameterName)
	{
		return (String) ActionContext.getParameters().get(parameterName);
	}

	public final Integer getUserUploadMaxSize()
	{
		String userUploadMaxSize = getPrincipalPropertyValue("fileUploadMaximumSize", false, true);
		if (userUploadMaxSize != null && !userUploadMaxSize.equals("") && !userUploadMaxSize.equals("-1"))
		{
			try
			{
				Integer userUploadMaxSizeInteger = new Integer(userUploadMaxSize);
				return userUploadMaxSizeInteger;
			} 
			catch (Exception e)
			{
				return getUploadMaxSize();
			}
		} 
		else
		{
			return getUploadMaxSize();
		}
	}

	public static InfoGluePrincipal getSessionInfoGluePrincipal()
	{
		InfoGluePrincipal infoGluePrincipal = null;
		try
		{
			if(ActionContext.getRequest() != null && ActionContext.getRequest().getSession() != null)
				infoGluePrincipal = (InfoGluePrincipal)ActionContext.getRequest().getSession().getAttribute("org.infoglue.cms.security.user");
		}
		catch (Exception e) 
		{
			logger.warn("Problem getting principal from session:" + e.getMessage());
		}
		
		return infoGluePrincipal;
	}

	public final Integer getUploadMaxSize()
	{
		Integer maxSize = new Integer(Integer.MAX_VALUE);
		try
		{
			String maxSizeStr = Configuration.getString("webwork.multipart.maxSize");
			if (maxSizeStr != null)
			{
				maxSize = new Integer(maxSizeStr);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return maxSize;
	}
	
    public String getColorScheme()
    {
        return colorScheme;
    }
    
    public void setColorScheme(String colorScheme)
    {
        this.colorScheme = colorScheme;
    }

    public String encode(String value)
    {
        return this.getResponse().encodeUrl(value);
    }

    public String getComponentRendererUrl()
    {
        return CmsPropertyHandler.getComponentRendererUrl();
    }
    
    public String getComponentRendererAction()
    {
        return CmsPropertyHandler.getComponentRendererAction();
    }
    
    public String getCMSBaseUrl()
    {
        return CmsPropertyHandler.getCmsBaseUrl();
    }

    public String getDisableImageEditor()
    {
        return CmsPropertyHandler.getDisableImageEditor();
    }

	public String getDisableCustomIcons()
	{
	    return CmsPropertyHandler.getDisableCustomIcons();
	}

	public String getWorkingStyleInformation()
	{
	    return CmsPropertyHandler.getWorkingStyleInformation();
	}

	public String getFinalStyleInformation()
	{
	    return CmsPropertyHandler.getFinalStyleInformation();
	}

	public String getPublishStyleInformation()
	{
	    return CmsPropertyHandler.getPublishStyleInformation();
	}

	public String getPublishedStyleInformation()
	{
	    return CmsPropertyHandler.getPublishedStyleInformation();
	}

	public Map getCustomContentTypeIcons()
	{
	    return CmsPropertyHandler.getCustomContentTypeIcons();
	}
	
    public String getEnableDateTimeDirectEditing()
    {
        return CmsPropertyHandler.getEnableDateTimeDirectEditing();
    }

    public String getAllowPublicationEventFilter()
    {
    	return CmsPropertyHandler.getAllowPublicationEventFilter();
    }
    
    /**
     * Getter for if the system should allow a user to override the 
     * version modifyer upon publication. 
     */
	public String getAllowOverrideModifyer()
	{
	    return CmsPropertyHandler.getAllowOverrideModifyer();
	}

	public String getFastSearchIncludedContentTypes()
	{
	    return CmsPropertyHandler.getFastSearchIncludedContentTypes();
	}

	public String getGACode()
	{
		return CmsPropertyHandler.getGACode();
	}

    public Locale getLocale()
    {
        return this.getSession().getLocale();
    }

    public List<LanguageVO> getLanguages() throws SystemException, Bug
    {
    	return LanguageController.getController().getLanguageVOList();
    }

    public Integer getToolId()
    {
        return this.getSession().getToolId();
    }

    public String getLanguageCode()
    {
        return this.getSession().getLocale().getLanguage();
    }
    
	public void setLanguageCode(String languageCode)
	{
		this.getSession().setLocale(new java.util.Locale(languageCode));
	}

	public void setToolId(Integer toolId)
	{
		this.getSession().setToolId(toolId);
	}

	/**
	 * Helper method to get the instance status defined by the delivery-base-url.
	 */
	public Boolean getInstanceStatus(String baseUrl)
	{
		return LiveInstanceMonitor.getInstance().getServerStatus(baseUrl);
	}

	/**
	 * This method returns a map of all the registered deliver instances and their current state.
	 */
	public Map<String,Boolean> getInstanceStatusMap()
	{
		return LiveInstanceMonitor.getInstance().getInstanceStatusMap();
	}

	/**
	 * This method returns a set of all queued publication beans divided inte a map where each set represents a certain deliver instance.
	 * So it returns all the queues so to speak.
	 */
	public Map<String, Set<PublicationQueueBean>> getInstancePublicationQueueBeans()
	{
		return PublicationQueue.getPublicationQueue().getInstancePublicationQueueBeans();
	}

	//--------------------------------------------------------------------------
	// Database/Transaction specific operations
	//--------------------------------------------------------------------------

	/**
	 * Begins a transaction on the supplied database
	 */
	
	public void beginTransaction(Database db) throws SystemException
	{
		try
		{
			db.begin();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new SystemException("An error occurred when we tried to begin an transaction. Reason:" + e.getMessage(), e);    
		}
	}
       
	/**
	 * Rollbacks a transaction on the named database
	 */
     
	public void closeTransaction(Database db) throws SystemException
	{
	    //if(db != null && !db.isClosed() && db.isActive())
	        //commitTransaction(db);
	        rollbackTransaction(db);
	}

	
	/**
	 * Ends a transaction on the named database
	 */
	
    public void commitTransaction(Database db) throws SystemException
	{
		try
		{
		    if (db.isActive())
		    {
			    db.commit();
			    RegistryController.notifyTransactionCommitted();
			}
		}
		catch(Exception e)
		{
		    throw new SystemException("An error occurred when we tried to commit an transaction. Reason:" + e.getMessage(), e);    
		}
		finally
		{
		    closeDatabase(db);
		}
	}
	
 
	/**
	 * Rollbacks a transaction on the named database
	 */
     
	public void rollbackTransaction(Database db) throws SystemException
	{
		try
		{
			if (db.isActive())
			{
			    db.rollback();
			}
		}
		catch(Exception e)
		{
			logger.warn("An error occurred when we tried to rollback an transaction. Reason:" + e.getMessage());
		}
		finally
		{
		    closeDatabase(db);
		}
	}

	/**
	 * Close the database
	 */
     
	public void closeDatabase(Database db) throws SystemException
	{
		try
		{
			db.close();
		}
		catch(Exception e)
		{
			logger.warn("An error occurred when we closed the database. Reason:" + e.getMessage());
			throw new SystemException("An error occurred when we tried to close a database. Reason:" + e.getMessage(), e);    
		}
	}

	public List<LinkBean> getActionLinks(String aUserSessionKey)
	{
		String key = aUserSessionKey + "_actionLinks";
		return (List<LinkBean>)getRequest().getSession().getAttribute(key);
	}

	public void setActionLinks(String aUserSessionKey, List<LinkBean> actionLinks)
	{
		String key = aUserSessionKey + "_actionLinks";
		getRequest().getSession().setAttribute(key, actionLinks);
	}
	
	public void addActionLink(String aUserSessionKey, LinkBean aLinkBean)
	{
		List<LinkBean> actionLinks = getActionLinks(aUserSessionKey);
		
		
		if (actionLinks == null)
		{			
			actionLinks = new ArrayList<LinkBean>();
		}
		
		actionLinks.add(aLinkBean);
		
		setActionLinks(aUserSessionKey, actionLinks);
	}

	public void setActionMessage(String aUserSessionKey, String actionMessage)
	{
		String key = aUserSessionKey + "_actionMessage";
		getRequest().getSession().setAttribute(key, actionMessage);
	}

	public String getActionMessage(String aUserSessionKey)
	{
		String key = aUserSessionKey + "_actionMessage";
		return (String)getRequest().getSession().getAttribute(key);
	}

	public void setActionExtraData(String aUserSessionKey, String extraDataKey, String extraData)
	{
		String key = aUserSessionKey + "_" + extraDataKey;
		getRequest().getSession().setAttribute(key, extraData);
	}

	public String getActionExtraData(String aUserSessionKey, String extraDataKey)
	{
		String key = aUserSessionKey + "_" + extraDataKey;
		return (String)getRequest().getSession().getAttribute(key);
	}

	public boolean getDisableCloseButton()
	{
		String disableCloseButton = this.getRequest().getParameter("disableCloseButton");
		if(disableCloseButton != null && !disableCloseButton.equals(""))
		{	
			return Boolean.parseBoolean(disableCloseButton);
		}
		else
			return false;
	}

	//TODO - make other base action for asset aware actions
	/**
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetUrl(Integer digitalAssetId) throws Exception
	{
		String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetUrl(digitalAssetId);
		}
		catch(Exception e)
		{
			logger.warn("We could not get the url of the digitalAsset: " + e.getMessage(), e);
			imageHref = e.getMessage();
		}
		
		return imageHref;
	}
	
	
	/**
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetThumbnailUrl(Integer digitalAssetId, int canvasWidth, int canvasHeight, String canvasColorHexCode, String alignment, String valignment, int width, int height, int quality) throws Exception
	{
		String imageHref = null;
		try
		{
			ColorHelper ch = new ColorHelper();
			Color canvasColor = ch.getHexColor(canvasColorHexCode);
       		imageHref = DigitalAssetController.getDigitalAssetThumbnailUrl(digitalAssetId, canvasWidth, canvasHeight, canvasColor, alignment, valignment, width, height, quality);
		}
		catch(Exception e)
		{
			logger.warn("We could not get the url of the thumbnail: " + e.getMessage(), e);
			imageHref = e.getMessage();
		}
		
		return imageHref;
	}

	/**
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetThumbnailUrl(Integer contentId, Integer languageId, String assetKey, boolean useLanguageFallback, int canvasWidth, int canvasHeight, String canvasColorHexCode, String alignment, String valignment, int width, int height, int quality) throws Exception
	{
		String imageHref = null;
		try
		{
			ColorHelper ch = new ColorHelper();
			Color canvasColor = ch.getHexColor(canvasColorHexCode);
			DigitalAssetVO assetVO = DigitalAssetController.getDigitalAssetVO(contentId, languageId, assetKey, useLanguageFallback);
       		
			imageHref = DigitalAssetController.getDigitalAssetThumbnailUrl(assetVO.getId(), canvasWidth, canvasHeight, canvasColor, alignment, valignment, width, height, quality);
		}
		catch(Exception e)
		{
			logger.warn("We could not get the url of the thumbnail: " + e.getMessage(), e);
			imageHref = e.getMessage();
		}
		
		return imageHref;
	}

	
	/**
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetUrl(Integer contentId, Integer languageId) throws Exception
	{
		String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetUrl(contentId, languageId);
		}
		catch(Exception e)
		{
			logger.warn("We could not get the url of the digitalAsset: " + e.getMessage(), e);
			imageHref = e.getMessage();
		}
		
		return imageHref;
	}
	
	
	/**
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetThumbnailUrl(Integer contentId, Integer languageId) throws Exception
	{
		String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetThumbnailUrl(contentId, languageId);
		}
		catch(Exception e)
		{
			logger.warn("We could not get the url of the thumbnail: " + e.getMessage(), e);
			imageHref = e.getMessage();
		}
		
		return imageHref;
	}


	public Integer getDigitalAssetContentId(Integer digitalAssetId) throws Exception
	{
		return DigitalAssetController.getController().getContentId(digitalAssetId);
	}

	public LanguageVO getLanguageVO(Integer languageId) throws Exception
	{
		LanguageVO languageVO = LanguageController.getController().getLanguageVOWithId(languageId);

		return languageVO;
	}

	public ContentVO getContentVO(Integer contentId) throws Exception
	{
		ContentVO contentVO = ContentController.getContentController().getContentVOWithId(contentId);

		return contentVO;
	}
	
	public String getContentPath(Integer contentId) throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		ContentVO contentVO = ContentController.getContentController().getContentVOWithId(contentId);
		sb.insert(0, contentVO.getName());
		while(contentVO.getParentContentId() != null)
		{
			contentVO = ContentController.getContentController().getContentVOWithId(contentVO.getParentContentId());
			sb.insert(0, contentVO.getName() + "/");
		}
		sb.insert(0, "/");
		
		return sb.toString();
	}
	
	public String getContentPath(Integer contentId, Database db) throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		ContentVO contentVO = ContentController.getContentController().getContentVOWithId(contentId, db);
		sb.insert(0, contentVO.getName());
		while(contentVO.getParentContentId() != null)
		{
			contentVO = ContentController.getContentController().getContentVOWithId(contentVO.getParentContentId(), db);
			sb.insert(0, contentVO.getName() + "/");
		}
		sb.insert(0, "/");
		
		return sb.toString();
	}

	public String getSiteNodePath(Integer siteNodeId) throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		SiteNodeVO siteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(siteNodeId);
		while(siteNodeVO != null)
		{
			sb.insert(0, "/" + siteNodeVO.getName());
			if(siteNodeVO.getParentSiteNodeId() != null)
				siteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(siteNodeVO.getParentSiteNodeId());
			else
				siteNodeVO = null;
		}
		
		return sb.toString();
	}

	public String getSiteNodePath(Integer siteNodeId, Database db) throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		SiteNodeVO siteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(siteNodeId, db);
		while(siteNodeVO != null)
		{
			sb.insert(0, "/" + siteNodeVO.getName());
			if(siteNodeVO.getParentSiteNodeId() != null)
				siteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(siteNodeVO.getParentSiteNodeId(), db);
			else
				siteNodeVO = null;
		}
		
		return sb.toString();
	}

	public String getMemoryUsageAsText()
	{
		return "" + (((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024)) + " MB used of " + ((Runtime.getRuntime().maxMemory() / 1024 / 1024)) + " MB";
	}
	
	public ProcessBean getProcessBean()
	{
		return ProcessBean.getProcessBean(this.getClass().getName(), ""+getInfoGluePrincipal().getName());
	}
	
	public String getStatusAsJSON()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><style>body {font-family: arial; font-size: 11px;}</style></head><body>");
		
		try
		{
			ProcessBean processBean = getProcessBean();
			if(processBean != null && processBean.getStatus() != ProcessBean.FINISHED)
			{
				sb.append("<h2>" + getLocalizedString(getLocale(), "tool.structuretool.publicationProcess.publicationProcessInfo") + "</h2>");

				sb.append("<ol>");
				for(String event : processBean.getProcessEvents())
					sb.append("<li>" + event + "</li>");
				sb.append("</ol>");
				sb.append("<div style='position: absolute; top:10px; right: 10px;'><img src='images/v3/loadingAnimation.gif' /></div>");
			}
			else
			{
				sb.append("<script type='text/javascript'>hideProcessStatus();</script>");
			}
		}
		catch (Throwable t)
		{
			logger.error("Error when generating repository export status report as JSON.", t);
			sb.append(t.getMessage());
		}
		sb.append("</body></html>");
				
		return sb.toString();
	}

	public String doShowProcessesAsJSON() throws Exception
	{
		return "successShowProcessesAsJSON";
	}

}

