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

import org.infoglue.cms.exception.AccessConstraintException;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConfigurationError;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGlueAuthenticationFilter;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsLogger;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import webwork.action.Action;
import webwork.action.CommandDriven;
import webwork.action.ResultException;
import webwork.action.ServletRequestAware;
import webwork.action.ServletResponseAware;

import org.infoglue.deliver.util.BrowserBean;
import org.infoglue.cms.util.StringManager;
import org.infoglue.cms.util.StringManagerFactory;

import org.infoglue.cms.applications.common.Session;

import org.apache.log4j.Logger;

import java.util.Locale;
import java.net.URLEncoder;

/**
 * @author Mattias Bogeblad
 */

public abstract class WebworkAbstractAction implements Action, ServletRequestAware, ServletResponseAware, CommandDriven
{
	private final String ACCESS_DENIED = "accessDenied";

	private Error error;
  	private Errors errors = new Errors();

  	private HttpServletRequest request;
  	private HttpServletResponse response;
  	private String commandName;


	/**
     *
     */
  	public Error getError()
  	{
		CmsLogger.logInfo("Fetching error from error-template:" + this.error);
    	return this.error;
  	}

    /**
     *
     */
  	public Errors getErrors()
  	{
    	CmsLogger.logInfo("Errors:" + this.errors);
    	return errors;
  	}

    /**
     *
     */
	public String doDefault() throws Exception
    {
        return INPUT;
    }



  /**
   *
   */
    public String execute() throws Exception
    {
        try
        {
            return isCommand() ? invokeCommand() : doExecute();
        }
        catch(ResultException e)
        {
        	CmsLogger.logSevere("ResultException " + e, e);
            return e.getResult();
        }
		catch(AccessConstraintException e)
		{
			CmsLogger.logWarning("AccessConstraintException " + e, e);
			setErrors(e);
			return ACCESS_DENIED;
		}
        catch(ConstraintException e)
        {
        	CmsLogger.logWarning("ConstraintException " + e, e);
            setErrors(e);
            return INPUT;
        }
        catch(Bug e)
        {
        	CmsLogger.logSevere("Bug " + e);
            setError(e, e.getCause());
            return ERROR;
        }
        catch(ConfigurationError e)
        {
         	CmsLogger.logSevere("ConfigurationError " + e);
             setError(e, e.getCause());
            return ERROR;
        }
        catch(SystemException e)
        {
            CmsLogger.logSevere("SystemException " + e, e);
            setError(e, e.getCause());
            return ERROR;
        }
        catch(Throwable e)
        {
            CmsLogger.logSevere("Throwable " + e);
            final Bug bug = new Bug("Uncaught exception!", e);
            setError(bug, bug.getCause());
            return ERROR;
        }
    }

	/**
	 * This method returns the url to the current page.
	 * Could be used in case of reload for example or for logging reasons.
	 */

	public String getCurrentUrl() throws Exception
	{
		String urlBase = getRequest().getRequestURL().toString();
		String urlParameters = getRequest().getQueryString();

		return URLEncoder.encode(urlBase + "?" + urlParameters, "UTF-8");
	}

    /**
     *
     */
    public void setCommand(String commandName) {
      this.commandName = commandName;
    }

  	/**
  	 * This method is here (ugly) to supply the user interface with the correct charset.
  	 */

/*  	public void getCharset()
  	{
  		return org.infoglue.cms.controllers.kernel.impl.simple.LanguageController.getLanguageWithId()
  	}
*/

    /**
     *
     */
    public void setServletRequest(HttpServletRequest request) {
      this.request = request;
    }



    /**
     *
     */
    public void setServletResponse(HttpServletResponse response) {
      this.response = response;
    }



  	/**
   	 *
     */
	private void setError(Throwable throwable, Throwable cause)
  	{
    	this.error = new Error(throwable, cause);
  	}


	/**
	 *
	 */
	private void setErrors(ConstraintException exception)
	{
		final Locale locale = getSession().getLocale();
		for (ConstraintException ce = exception;
			ce != null;
			ce = ce.getChainedException())
		{
			final String fieldName = ce.getFieldName();
			final String errorCode = ce.getErrorCode();
			final String localizedErrorMessage = getLocalizedErrorMessage(locale, errorCode);
			getErrors().addError(fieldName, localizedErrorMessage);
		}
		getLogger().debug(getErrors().toString());
	}

	/**
     * <todo>Move to a ConstraintExceptionHelper class?</todo>
     */

  	private String getLocalizedErrorMessage(Locale locale, String errorCode)
  	{
    	// <todo>fetch packagename from somewhere</todo>
    	StringManager stringManager = StringManagerFactory.getPresentationStringManager("org.infoglue.cms.entities", locale);

	    // check if a specific error message exists - <todo/>
  	  	// nah, use the general error message
    	return stringManager.getString(errorCode);
  	}


  	public String getLocalizedString(Locale locale, String key)
  	{
    	StringManager stringManager = StringManagerFactory.getPresentationStringManager("org.infoglue.cms.applications", locale);

    	return stringManager.getString(key);
  	}

   /**
   	*
   	*/

	private boolean isCommand()
  	{
    	return this.commandName != null && commandName.trim().length() > 0 && (this instanceof CommandDriven);
  	}

  	/**
   	 *
   	 */

  	private String invokeCommand() throws Exception
  	{
    	final StringBuffer methodName = new StringBuffer("do" + this.commandName);
    	methodName.setCharAt(2, Character.toUpperCase(methodName.charAt(2)));

    	try
    	{
      		final Method method = getClass().getMethod(methodName.toString(), new Class[0]);
      		return (String) method.invoke(this, new Object[0]);
    	}
    	catch(Exception ie)
    	{
			ie.printStackTrace();
    	    CmsLogger.logSevere("Exception " + ie, ie);

			try
			{
				throw ie.getCause();
			}
			catch(ResultException e)
			{
				e.printStackTrace();
				CmsLogger.logSevere("ResultException " + e, e);
				return e.getResult();
			}
			catch(AccessConstraintException e)
			{
				e.printStackTrace();
				CmsLogger.logWarning("AccessConstraintException " + e, e);
				setErrors(e);
				return ACCESS_DENIED;
			}
			catch(ConstraintException e)
			{
				e.printStackTrace();
				CmsLogger.logWarning("ConstraintException " + e, e);
				setErrors(e);
				return INPUT;
			}
			catch(Bug e)
			{
				e.printStackTrace();
				CmsLogger.logSevere("Bug " + e);
				setError(e, e.getCause());
				return ERROR;
			}
			catch(ConfigurationError e)
			{
				e.printStackTrace();
				CmsLogger.logSevere("ConfigurationError " + e);
				setError(e, e.getCause());
				return ERROR;
			}
			catch(SystemException e)
			{
				e.printStackTrace();
				CmsLogger.logSevere("SystemException " + e, e);
				setError(e, e.getCause());
				return ERROR;
			}
			catch(Throwable e)
			{
				e.printStackTrace();
				CmsLogger.logSevere("Throwable " + e);
				final Bug bug = new Bug("Uncaught exception!", e);
				setError(bug, bug.getCause());
				return ERROR;
			}
    	}
  	}

    public final String getRoot()
    {
    	return request.getContextPath();
    }

    /**
     * This method returns a logged in principal if existing.
     */

    public final InfoGluePrincipal getInfoGluePrincipal()
    {
    	return (InfoGluePrincipal)this.getHttpSession().getAttribute(InfoGlueAuthenticationFilter.INFOGLUE_FILTER_USER);
    }

	/**
	 *
	 */

	protected abstract String doExecute() throws Exception;

	/**
	 *
	 */

	public final BrowserBean getBrowserBean()
	{
		BrowserBean browserBean = new BrowserBean();
		browserBean.setRequest(this.request);
		return browserBean;
	}

	/**
	 *
	 */

	protected final HttpServletRequest getRequest()
	{
		return this.request;
	}

	/**
	 *
	 */

	protected final HttpServletResponse getResponse()
	{
		return this.response;
	}

	/**
	 *
	 */
	protected final HttpSession getHttpSession()
	{
		return getRequest().getSession();
	}

    /**
     *
     */

    public final Session getSession()
    {
        return new Session(getHttpSession());
    }

    /**
     *
     */

    protected Logger getLogger()
    {
        return Logger.getLogger(getClass().getName());
    }

}