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

package org.infoglue.deliver.controllers.kernel.impl.simple;

import org.infoglue.deliver.controllers.kernel.URLComposer;
import org.infoglue.cms.controllers.kernel.impl.simple.AccessRightController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.WorkflowController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.ContentTypeAttribute;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.structure.SiteNode;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.deliver.applications.databeans.DeliveryContext;
import org.infoglue.deliver.applications.databeans.WebPage;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.deliver.util.graphics.ImageRenderer;
import org.infoglue.cms.util.*;
import org.infoglue.deliver.util.graphics.ColorHelper;
import org.infoglue.deliver.util.graphics.FOPHelper;
import org.infoglue.deliver.util.graphics.FontHelper;
import org.infoglue.deliver.util.webservices.WebServiceHelper;
import org.infoglue.cms.exception.*;
import org.infoglue.deliver.util.charts.ChartHelper;
import org.infoglue.deliver.util.*;
import org.infoglue.cms.util.dom.DOMBuilder;
import org.infoglue.cms.applications.common.VisualFormatter;

import java.security.Principal;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.io.*;
import java.awt.Color;
import java.awt.Font;

import org.apache.oro.text.regex.*;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.servlet.http.HttpServletRequest;

/**
 * This is the most basic template controller supplying the templates using it with
 * methods to fetch contents, structure and other suff needed for a site. Mostly this class just acts as a
 * delegator to other more specialized classes.
 */

public class BasicTemplateController implements TemplateController
{
	private URLComposer urlComposer = null;

	protected static final String META_INFO_BINDING_NAME 				= "Meta information";
	protected static final String TEMPLATE_ATTRIBUTE_NAME   			= "Template";
	protected static final String TITLE_ATTRIBUTE_NAME     		 		= "Title";
	protected static final String NAV_TITLE_ATTRIBUTE_NAME 		 		= "NavigationTitle";
	/*
	protected static final String DISABLE_PAGE_CACHE_ATTRIBUTE_NAME		= "DisablePageCache";
	protected static final String PAGE_CONTENT_TYPE_ATTRIBUTE_NAME		= "ContentType";
	protected static final String ENABLE_PAGE_PROTECTION_ATTRIBUTE_NAME = "ProtectPage";
	protected static final String DISABLE_EDIT_ON_SIGHT_ATTRIBUTE_NAME	= "DisableEditOnSight";
	*/
	protected static final boolean USE_LANGUAGE_FALLBACK        = true;
	protected static final boolean DO_NOT_USE_LANGUAGE_FALLBACK = false;
	protected static final boolean USE_INHERITANCE 				= true;
	protected static final boolean DO_NOT_USE_INHERITANCE 		= false;

	protected Integer siteNodeId = null;
	protected Integer languageId = null;
	protected Integer contentId  = null;

	protected HttpServletRequest request = null;
	protected DeliveryContext deliveryContext = null;

	protected BrowserBean browserBean = null;

	protected NodeDeliveryController nodeDeliveryController = null;
	protected ContentDeliveryController contentDeliveryController = null;
	protected IntegrationDeliveryController integrationDeliveryController = null;

	protected ComponentLogic componentLogic = null;

	protected InfoGluePrincipal infoGluePrincipal = null;

	// For adding objects to be used in subsequent parsing
	// like getParsedContentAttribute, include, etc
	protected Map templateLogicContext = new HashMap();

	/**
	 * The constructor for the templateController. It should be used to initialize the
	 * templateController for efficient use.
	 */

	public BasicTemplateController(InfoGluePrincipal infoGluePrincipal)
	{
	    this.infoGluePrincipal = infoGluePrincipal;
	    this.urlComposer = URLComposer.getURLComposer();
	}

	/**
	 * Add objects to be used in subsequent parsing
	 * like getParsedContentAttribute, include, etc
	 */
	public void addToContext(String name, Object object)
	{
		templateLogicContext.put(name, object);
	}

	/**
	 * Gets objects from the context
	 */
	public Object getFromContext(String name)
	{
		return templateLogicContext.get(name);
	}

	/**
	 * Setter for the template to get all the parameters from the user.
	 */

	public void setStandardRequestParameters(Integer siteNodeId, Integer languageId, Integer contentId)
	{
		this.siteNodeId = siteNodeId;
		this.languageId = languageId;
		this.contentId  = contentId;
	}

	/**
	 * Setter for the template to get all the parameters from the user.
	 */

	public void setHttpRequest(HttpServletRequest request)
	{
		this.request = request;
	}


	/**
	 * Setter for the bean which contains information about the users browser.
	 */

	public void setBrowserBean(BrowserBean browserBean)
	{
		this.browserBean = browserBean;
	}

	/**
	 * Getter for the template attribute name.
	 */

	public String getTemplateAttributeName()
	{
		return TEMPLATE_ATTRIBUTE_NAME;
	}

	/**
	 * Getter for the siteNodeId
	 */

	public Integer getSiteNodeId()
	{
		return this.siteNodeId;
	}

	/**
	 * Getter for the languageId
	 */

	public Integer getLanguageId()
	{
		return this.languageId;
	}



	/**
	 * Getter for the contentId
	 */

	public Integer getContentId()
	{
		return this.contentId;
	}

	/**
	 * This method gets a component logic helper object.
	 */

	public ComponentLogic getComponentLogic()
	{
		return this.componentLogic;
	}

	/**
	 * This method gets a component logic helper object.
	 */

	public void setComponentLogic(ComponentLogic componentLogic)
	{
		this.componentLogic = componentLogic;
	}


	/**
	 * This method gets the formatter object that helps with formatting of data.
	 */

	public VisualFormatter getVisualFormatter()
	{
		return new VisualFormatter();
	}

	/**
	 * This method gets the color utility.
	 */

	public ColorHelper getColorHelper()
	{
		return new ColorHelper();
	}

	/**
	 * This method gets the color utility.
	 */

	public FontHelper getFontHelper()
	{
		return new FontHelper();
	}

	/**
	 * This method gets the math utility.
	 */

	public MathHelper getMathHelper()
	{
		return new MathHelper();
	}

	/**
	 * This method gets the math utility.
	 */

	public ChartHelper getChartHelper()
	{
		return new ChartHelper(this);
	}

	/**
	 * This method gets the webservice utility.
	 */

	public WebServiceHelper getWebServiceHelper()
	{
		return new WebServiceHelper();
	}

	/**
	 * This method gets the NumberFormat instance with the proper locale.
	 */
	public NumberFormat getNumberFormatHelper()
	{
	 	return NumberFormat.getInstance	(
	 			LanguageDeliveryController.getLanguageDeliveryController().getLocaleWithId(this.languageId)
	 		);
	}

	/**
	 * This method gets the object converter utility.
	 */

	public ObjectConverter getObjectConverter()
	{
		return new ObjectConverter();
	}


	/**
	 * This method delivers a map with all unparsed content attributes
	 */
	public Map getContentAttributes(Integer contentId)
	{
	    Map result = new HashMap();
	    ContentTypeDefinitionVO typeDefinitionVO = getContentTypeDefinitionVO(contentId);
	    List contentAttributes = getContentAttributes(typeDefinitionVO.getSchemaValue());

	    for(Iterator i=contentAttributes.iterator();i.hasNext();)
	    {
	        ContentTypeAttribute contentTypeAttribute = (ContentTypeAttribute) i.next();
	        String name = contentTypeAttribute.getName();
	        result.put(name, getContentAttribute(contentId, name));
	    }
	    return result;
	}

	/**
	 * This method delivers a map with all parsed content attributes
	 */
	public Map getParsedContentAttributes(Integer contentId)
	{
	    Map result = new HashMap();
	    ContentTypeDefinitionVO typeDefinitionVO = getContentTypeDefinitionVO(contentId);
	    List contentAttributes = getContentAttributes(typeDefinitionVO.getSchemaValue());

	    for(Iterator i=contentAttributes.iterator();i.hasNext();)
	    {
	        ContentTypeAttribute contentTypeAttribute = (ContentTypeAttribute) i.next();
	        String name = contentTypeAttribute.getName();
	        result.put(name, getParsedContentAttribute(contentId, name));
	    }
	    return result;
	}


	/**
	 * Getter for the current content
	 */

	public ContentVO getContent()
	{
		ContentVO content = null;

		try
		{
			content = ContentDeliveryController.getContentDeliveryController().getContentVO(this.contentId);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("An error occurred trying to get the current content:" + e.getMessage(), e);
		}

		return content;
	}

	/**
	 * Getter for the current content
	 */

	public ContentVO getContent(Integer contentId)
	{
		ContentVO content = null;

		try
		{
			content = ContentDeliveryController.getContentDeliveryController().getContentVO(contentId);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("An error occurred trying to get the content with id " + contentId + ":" + e.getMessage(), e);
		}

		return content;
	}


	public InfoGluePrincipal getPrincipal()
	{
	    return this.infoGluePrincipal;
		//return (InfoGluePrincipal) this.getHttpServletRequest().getSession().getAttribute("infogluePrincipal");
	}

	/**
	 * Getting a property for the current Principal - used for personalisation.
	 * This method starts with getting the property on the user and if it does not exist we check out the
	 * group-properties as well.
	 */

	public String getPrincipalPropertyValue(String propertyName)
	{
		return getPrincipalPropertyValue(propertyName, true);
	}

	/**
	 * Getting a property for the current Principal - used for personalisation.
	 * This method starts with getting the property on the user and if it does not exist we check out the
	 * group-properties as well.
	 */

	public Map getPrincipalPropertyHashValues(String propertyName)
	{
		return getPrincipalPropertyHashValues(propertyName, true);
	}

	/**
	 * Getting a property for the current Principal - used for personalisation.
	 * This method starts with getting the property on the user and if it does not exist we check out the
	 * group-properties as well.
	 */

	public String getPrincipalPropertyValue(InfoGluePrincipal infoGluePrincipal, String propertyName)
	{
		return getPrincipalPropertyValue(infoGluePrincipal, propertyName, true);
	}

	/**
	 * Getting a property for a Principal - used for personalisation.
	 * This method starts with getting the property on the user and if it does not exist we check out the
	 * group-properties as well.
	 */

	public Map getPrincipalPropertyHashValues(InfoGluePrincipal infoGluePrincipal, String propertyName)
	{
		return getPrincipalPropertyHashValues(infoGluePrincipal, propertyName, true);
	}


	/**
	 * Getting a property for a Principal - used for personalisation.
	 * This method starts with getting the property on the user and if it does not exist we check out the
	 * group-properties as well.
	 */

	public String getPrincipalPropertyValue(InfoGluePrincipal infoGluePrincipal, String propertyName, boolean escapeSpecialCharacters)
	{
		String value = "";

		try
		{
			value = ExtranetController.getController().getPrincipalPropertyValue(infoGluePrincipal, propertyName, this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK, escapeSpecialCharacters);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("An error occurred trying to get property " + propertyName + " from infoGluePrincipal:" + e.getMessage(), e);
		}

		return value;
	}


	/**
	 * Getting a property for the current Principal - used for personalisation.
	 * This method starts with getting the property on the user and if it does not exist we check out the
	 * group-properties as well.
	 */

	public String getPrincipalPropertyValue(String propertyName, boolean escapeSpecialCharacters)
	{
		String value = "";

		try
		{
		    InfoGluePrincipal infoGluePrincipal = this.getPrincipal();
		    value = ExtranetController.getController().getPrincipalPropertyValue(infoGluePrincipal, propertyName, this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK, escapeSpecialCharacters);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("An error occurred trying to get property " + propertyName + " from infoGluePrincipal:" + e.getMessage(), e);
		}

		return value;
	}


	/**
	 * Getting a property for a Principal - used for personalisation.
	 * This method starts with getting the property on the user and if it does not exist we check out the
	 * group-properties as well.
	 */

	public Map getPrincipalPropertyHashValues(InfoGluePrincipal infoGluePrincipal, String propertyName, boolean escapeSpecialCharacters)
	{
		Map value = new HashMap();

		try
		{
			value = ExtranetController.getController().getPrincipalPropertyHashValues(infoGluePrincipal, propertyName, this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK, escapeSpecialCharacters);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("An error occurred trying to get property " + propertyName + " from infoGluePrincipal:" + e.getMessage(), e);
		}

		return value;
	}


	/**
	 * Getting a property for the current Principal - used for personalisation.
	 * This method starts with getting the property on the user and if it does not exist we check out the
	 * group-properties as well.
	 */

	public Map getPrincipalPropertyHashValues(String propertyName, boolean escapeSpecialCharacters)
	{
		Map value = new HashMap();

		try
		{
			InfoGluePrincipal infoGluePrincipal = this.getPrincipal();
			value = ExtranetController.getController().getPrincipalPropertyHashValues(infoGluePrincipal, propertyName, this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK, escapeSpecialCharacters);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("An error occurred trying to get property " + propertyName + " from infoGluePrincipal:" + e.getMessage(), e);
		}

		return value;
	}

	/**
	 * Getter for request-object
	 */

	public HttpServletRequest getHttpServletRequest()
	{
		return this.request;
	}


	/**
	 * Getter for request-parameters
	 */

	public Enumeration getRequestParamenterNames()
	{
		return this.request.getParameterNames();
	}

	/**
	 * Getter for request-parameter
	 */
	public String getRequestParameter(String parameterName)
	{
		String value = "";
		try
		{
			value = this.request.getParameter(parameterName);
			if(value == null)
				value = "";
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("An error occurred trying to get parameterName " + parameterName + " from request:" + e.getMessage(), e);
		}

		return value;
	}

	/**
	 * Getter for request-parameters
	 */
	public String[] getRequestParameterValues(String parameterName)
	{
		String value[] = null;
		try
		{
			value = this.request.getParameterValues(parameterName);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("An error occurred trying to get parameterName " + parameterName + " from request:" + e.getMessage(), e);
		}

		return value;
	}

	/**
	 * Getter for the browserBean which supplies information about the users browser, OS and other stuff.
	 */

	public BrowserBean getBrowserBean()
	{
		return browserBean;
	}


	/**
	 * Setting to enable us to set initialized versions of the Node and Content delivery Controllers.
	 */
	public void setDeliveryControllers(NodeDeliveryController nodeDeliveryController, ContentDeliveryController contentDeliveryController, IntegrationDeliveryController integrationDeliveryController)
	{
		this.nodeDeliveryController        = nodeDeliveryController;
		this.contentDeliveryController     = contentDeliveryController;
		this.integrationDeliveryController = integrationDeliveryController;
	}

	/**
	 * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
	 * value if OnSiteEdit is on.
	 */

	public String getContentAttribute(String contentBindningName, String attributeName, boolean clean)
	{
		return getContentAttribute(contentBindningName, attributeName);
	}

	/**
	 * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
	 * value if OnSiteEdit is on.
	 */

	public String getContentAttribute(String attributeName, boolean clean)
	{
		return getContentAttribute(attributeName);
	}

	/**
	 * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
	 * value if OnSiteEdit is on.
	 */

	public String getContentAttribute(Integer contentId, String attributeName, boolean clean)
	{
		return getContentAttribute(contentId, attributeName);
	}

	/**
	 * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
	 * value if OnSiteEdit is on.
	 */

	public String getContentAttribute(Integer contentId, Integer langaugeId, String attributeName, boolean clean)
	{
	    return getContentAttribute(contentId, languageId, attributeName);
	}

	/**
	 * This method deliveres a String with the content-attribute asked for if it exists in the content
	 * defined in the url-parameter contentId.
	 */

	public String getContentAttribute(String attributeName)
	{
		String attributeValue = "";

		try
		{
		    attributeValue = ContentDeliveryController.getContentDeliveryController().getContentAttribute(this.contentId, this.languageId, attributeName, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get attributeName=" + attributeName + " on content " + this.contentId + ":" + e.getMessage(), e);
		}

		return attributeValue;
	}

	/**
	 * This method deliveres a String with the content-attribute asked for.
	 * As the siteNode can have multiple bindings as well as a content as a parameter this
	 * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getContentAttribute(String contentBindningName, String attributeName)
	{
		String attributeValue = "";

		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			if(contentVO != null)
			{
				attributeValue = ContentDeliveryController.getContentDeliveryController().getContentAttribute(contentVO.getContentId(), this.languageId, attributeName, this.siteNodeId, USE_LANGUAGE_FALLBACK);
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get attributeName=" + attributeName + " on contentBindning " + contentBindningName + ":" + e.getMessage(), e);
		}

		return attributeValue;
	}


	/**
	 * This method deliveres a String with the content-attribute asked for.
	 * As the siteNode can have multiple bindings as well as a content as a parameter this
	 * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getContentAttribute(Integer contentId, String attributeName)
	{
		String attributeValue = "";

		try
		{
			attributeValue = ContentDeliveryController.getContentDeliveryController().getContentAttribute(contentId, this.languageId, attributeName, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get attributeName=" + attributeName + " on content " + contentId + ":" + e.getMessage(), e);
		}

		return attributeValue;
	}

	/**
	 * This method deliveres a String with the content-attribute asked for in the language asked for.
	 * As the siteNode can have multiple bindings as well as a content as a parameter this
	 * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getContentAttribute(Integer contentId, Integer languageId, String attributeName)
	{
		String attributeValue = "";

		try
		{
		    attributeValue = ContentDeliveryController.getContentDeliveryController().getContentAttribute(contentId, languageId, attributeName, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get attributeName=" + attributeName + " on content " + contentId + ":" + e.getMessage(), e);
		}

		return attributeValue;
	}


	/**
	 * This method deliveres a String with the content-attribute asked for in the language asked for.
	 * If the attribute is not found in the language requested it fallbacks to the master language.
	 */

	public String getContentAttributeUsingLanguageFallback(Integer contentId, String attributeName, boolean disableEditOnSight)
	{
		String attributeValue = "";

		try
		{
		    attributeValue = this.getContentAttribute(contentId, attributeName, true);
		    if(attributeValue != null && attributeValue.trim().equals(""))
		    {
		        LanguageVO masteLanguageVO = LanguageDeliveryController.getLanguageDeliveryController().getMasterLanguageForSiteNode(this.siteNodeId);
		        attributeValue = this.getContentAttribute(contentId, masteLanguageVO.getLanguageId(), attributeName, disableEditOnSight);
		    }
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get attributeName=" + attributeName + " on content " + contentId + ":" + e.getMessage(), e);
		}

		return attributeValue;
	}

	/**
	 * This method deliveres a String with the content-attribute asked for after it has been parsed and all special tags have been converted.
	 * As the siteNode can have multiple bindings as well as a content as a parameter this
	 * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getParsedContentAttribute(String attributeName)
	{
		String attributeValue = "";

		try
		{
			if(this.contentId != null)
			{
				String unparsedAttributeValue = ContentDeliveryController.getContentDeliveryController().getContentAttribute(this.contentId, this.languageId, attributeName, this.siteNodeId, USE_LANGUAGE_FALLBACK);
				CmsLogger.logInfo("Found unparsedAttributeValue:" + unparsedAttributeValue);

				templateLogicContext.put("inlineContentId", this.contentId);

				Map context = new HashMap();
				context.put("inheritedTemplateLogic", this);
				context.put("templateLogic", getTemplateController(this.siteNodeId, this.languageId, this.contentId, this.request, this.infoGluePrincipal));

				// Add the templateLogicContext objects to this context. (SS - 040219)
				context.putAll(templateLogicContext);

				StringWriter cacheString = new StringWriter();
				PrintWriter cachedStream = new PrintWriter(cacheString);
				new VelocityTemplateProcessor().renderTemplate(context, cachedStream, unparsedAttributeValue);
				attributeValue = cacheString.toString();
				//CmsLogger.logInfo("result:" + result);
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get attributeName=" + attributeName + " on sent in content with id:" + this.contentId + ":" + e.getMessage(), e);
		}

		return attributeValue;
	}


	/**
	 * This method deliveres a String with the content-attribute asked for after it has been parsed and all special tags have been converted.
	 * As the siteNode can have multiple bindings as well as a content as a parameter this
	 * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getParsedContentAttribute(String contentBindningName, String attributeName)
	{
		CmsLogger.logInfo("getParsedContentAttribute:" + contentBindningName + ":" + attributeName);

		String attributeValue = "";

		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			if(contentVO != null)
			{
				CmsLogger.logInfo("contentVO:" + contentVO.getContentId());

				String unparsedAttributeValue = ContentDeliveryController.getContentDeliveryController().getContentAttribute(contentVO.getId(), this.languageId, attributeName, this.siteNodeId, USE_LANGUAGE_FALLBACK);
				CmsLogger.logInfo("Found unparsedAttributeValue:" + unparsedAttributeValue);

				templateLogicContext.put("inlineContentId", contentVO.getId());

				Map context = new HashMap();
				context.put("inheritedTemplateLogic", this);
				context.put("templateLogic", getTemplateController(this.siteNodeId, this.languageId, contentVO.getId(), this.request, this.infoGluePrincipal));

				// Add the templateLogicContext objects to this context. (SS - 040219)
				context.putAll(templateLogicContext);

				StringWriter cacheString = new StringWriter();
				PrintWriter cachedStream = new PrintWriter(cacheString);
				new VelocityTemplateProcessor().renderTemplate(context, cachedStream, unparsedAttributeValue);
				attributeValue = cacheString.toString();
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get attributeName=" + attributeName + " on contentBindning " + contentBindningName + ":" + e.getMessage(), e);
		}

		return attributeValue;
	}



	/**
	 * This method deliveres a String with the content-attribute asked for after it has been parsed and all special tags have been converted.
	 * The attribute is fetched from the specified content.
	 */

	public String getParsedContentAttribute(Integer contentId, String attributeName)
	{
		String attributeValue = "";

		try
		{
			if(contentId != null)
			{
				String unparsedAttributeValue = ContentDeliveryController.getContentDeliveryController().getContentAttribute(contentId, this.languageId, attributeName, this.siteNodeId, USE_LANGUAGE_FALLBACK);
				CmsLogger.logInfo("Found unparsedAttributeValue:" + unparsedAttributeValue);

				templateLogicContext.put("inlineContentId", contentId);

				Map context = new HashMap();
				context.put("inheritedTemplateLogic", this);
				context.put("templateLogic", getTemplateController(this.siteNodeId, this.languageId, contentId, this.request, this.infoGluePrincipal));

				// Add the templateLogicContext objects to this context. (SS - 040219)
				context.putAll(templateLogicContext);

				StringWriter cacheString = new StringWriter();
				PrintWriter cachedStream = new PrintWriter(cacheString);
				new VelocityTemplateProcessor().renderTemplate(context, cachedStream, unparsedAttributeValue);
				attributeValue = cacheString.toString();
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get attributeName=" + attributeName + " on content with id " + contentId + ":" + e.getMessage(), e);
		}

		return attributeValue;
	}

	/**
	 * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
	 * value if OnSiteEdit is on.
	 */

	public String getParsedContentAttribute(String attributeName, boolean clean)
	{
		return getParsedContentAttribute(attributeName);
	}

	/**
	 * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
	 * value if OnSiteEdit is on.
	 */

	public String getParsedContentAttribute(String contentBindningName, String attributeName, boolean clean)
	{
		return getParsedContentAttribute(contentBindningName, attributeName);
	}

	/**
	 * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
	 * value if OnSiteEdit is on.
	 */

	public String getParsedContentAttribute(Integer contentId, String attributeName, boolean clean)
	{
		return getParsedContentAttribute(contentId, attributeName);
	}

	/**
	 * This method deliveres a list of strings which represents all assetKeys for a content.
	 */

	public Collection getAssetKeys(String contentBindningName)
	{
		Collection assetKeys = new ArrayList();

		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			assetKeys = ContentDeliveryController.getContentDeliveryController().getAssetKeys(contentVO.getContentId(), this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetKeys on content with id: " + contentId + ":" + e.getMessage(), e);
		}

		return assetKeys;
	}

	/**
	 * This method deliveres a list of strings which represents all assetKeys for a content.
	 */

	public Collection getAssetKeys(Integer contentId)
	{
		Collection assetKeys = new ArrayList();

		try
		{
			assetKeys = ContentDeliveryController.getContentDeliveryController().getAssetKeys(contentId, this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetKeys on content with id: " + contentId + ":" + e.getMessage(), e);
		}

		return assetKeys;
	}

	/**
	 * This method deliveres a list of strings which represents all assetKeys defined for a contentTypeDefinition.
	 */

	public Collection getContentTypeDefinitionAssetKeys(String schemaValue)
	{
		Collection assetKeys = new ArrayList();

		try
		{
			assetKeys = ContentTypeDefinitionController.getController().getDefinedAssetKeys(schemaValue);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetKeys on content with id: " + contentId + ":" + e.getMessage(), e);
		}

		return assetKeys;
	}

	/**
	 * This method deliveres a String with the URL to the thumbnail for the digital asset asked for.
	 * This method assumes that the content sent in only has one asset attached.
	 */

	public String getAssetThumbnailUrl(Integer contentId, int width, int height)
	{
		String assetThumbnailUrl = "";

		try
		{
			assetThumbnailUrl = ContentDeliveryController.getContentDeliveryController().getAssetThumbnailUrl(contentId, this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK, width, height);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetThumbnailUrl on contentId " + contentId + ":" + e.getMessage(), e);
		}

		return assetThumbnailUrl;
	}

	/**
	 * This method deliveres a String with the URL to the thumbnail for the digital asset asked for.
	 * This method takes a key for the asset you want to make a thumbnail from.
	 */

	public String getAssetThumbnailUrl(Integer contentId, String assetKey, int width, int height)
	{
		String assetThumbnailUrl = "";

		try
		{
			assetThumbnailUrl = ContentDeliveryController.getContentDeliveryController().getAssetThumbnailUrl(contentId, this.languageId, assetKey, this.siteNodeId, USE_LANGUAGE_FALLBACK, width, height);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetThumbnailUrl on contentId " + contentId + ":" + e.getMessage(), e);
		}

		return assetThumbnailUrl;
	}

	/**
	 * This method deliveres a String with the URL to the thumbnail of the digital asset asked for.
	 * As the siteNode can have multiple bindings as well as a content as a parameter this
	 * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getAssetThumbnailUrl(String contentBindningName, int width, int height)
	{
		String assetUrl = "";

		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			assetUrl = ContentDeliveryController.getContentDeliveryController().getAssetThumbnailUrl(contentVO.getContentId(), this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK, width, height);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on contentBindningName " + contentBindningName + ":" + e.getMessage(), e);
		}

		return assetUrl;
	}

	/**
	 * This method deliveres a String with the URL to the thumbnail of the digital asset asked for.
	 * As the siteNode can have multiple bindings as well as a content as a parameter this
	 * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getAssetThumbnailUrl(String contentBindningName, String assetKey, int width, int height)
	{
		String assetThumbnailUrl = "";

		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			assetThumbnailUrl = ContentDeliveryController.getContentDeliveryController().getAssetThumbnailUrl(contentVO.getContentId(), this.languageId, assetKey, this.siteNodeId, USE_LANGUAGE_FALLBACK, width, height);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on contentBindningName " + contentBindningName + ":" + e.getMessage(), e);
		}

		return assetThumbnailUrl;
	}

	/**
	 * This method deliveres a String with the URL to the digital asset asked for.
	 * As the siteNode can have multiple bindings as well as a content as a parameter this
	 * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getAssetUrl(String contentBindningName)
	{
		String assetUrl = "";

		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			assetUrl = ContentDeliveryController.getContentDeliveryController().getAssetUrl(contentVO.getContentId(), this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on contentBindningName " + contentBindningName + ":" + e.getMessage(), e);
		}

		return assetUrl;
	}


	public String getEncodedUrl(String s, String enc)
	{
		String ret = "";
		try
		{
			ret = java.net.URLEncoder.encode(s, enc);
		}
		catch (UnsupportedEncodingException e)
		{
			CmsLogger.logSevere("An error occurred trying to encode the url: " + s + " with encoding: " + enc + ": " + e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * This method deliveres a String with the URL to the digital asset asked for.
	 */

	public String getAssetUrl(Integer contentId)
	{
		String assetUrl = "";

		try
		{
			assetUrl = ContentDeliveryController.getContentDeliveryController().getAssetUrl(contentId, this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on content with id: " + contentId + ":" + e.getMessage(), e);
		}

		return assetUrl;
	}


	/**
	 * This method deliveres a String with the URL to the digital asset asked for.
	 */

	public String getAssetUrl(Integer contentId, String assetKey)
	{
		String assetUrl = "";

		try
		{
			assetUrl = ContentDeliveryController.getContentDeliveryController().getAssetUrl(contentId, this.languageId, assetKey, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on content with id: " + contentId + " and assetKey:" + assetKey + " : " + e.getMessage(), e);
		}

		return assetUrl;
	}


	/**
	 * This method deliveres a String with the URL to the digital asset asked for.
	 * As the siteNode can have multiple bindings as well as a content as a parameter this
	 * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getAssetUrl(String contentBindningName, int index)
	{
		String assetUrl = "";

		try
		{
			List contentVOList = this.nodeDeliveryController.getBoundContents(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName, USE_INHERITANCE);
			if(contentVOList != null && contentVOList.size() > index)
			{
				ContentVO contentVO = (ContentVO)contentVOList.get(index);
				assetUrl = ContentDeliveryController.getContentDeliveryController().getAssetUrl(contentVO.getContentId(), this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK);
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on contentBindningName " + contentBindningName + ":" + e.getMessage(), e);
		}

		return assetUrl;
	}


	/**
	 * This method deliveres a String with the URL to the digital asset asked for.
	 * As the siteNode can have multiple bindings as well as a content as a parameter this
	 * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getAssetUrl(String contentBindningName, String assetKey)
	{
		String assetUrl = "";

		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			assetUrl = ContentDeliveryController.getContentDeliveryController().getAssetUrl(contentVO.getContentId(), this.languageId, assetKey, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on contentBindningName " + contentBindningName + " with assetKey " + assetKey + ":" + e.getMessage(), e);
		}

		return assetUrl;
	}


	/**
	 * This method deliveres a String with the URL to the digital asset asked for. In this special case the image
	 * is fetched from the article being generated. This means that this method only is of interest if you have attached
	 * assets to either a template or to an content and are useing parsedContentAttribute.
	 */

	public String getInlineAssetUrl(String assetKey)
	{
		String assetUrl = "";

		try
		{
		    Integer inlineContentId = this.contentId;
		    if(inlineContentId == null || inlineContentId.intValue() == -1)
		        inlineContentId = (Integer)this.templateLogicContext.get("inlineContentId");

			CmsLogger.logInfo("getInlineAssetUrl:" + inlineContentId + ":" + this.languageId + ":" + assetKey + ":" + this.siteNodeId);
			assetUrl = ContentDeliveryController.getContentDeliveryController().getAssetUrl(inlineContentId, this.languageId, assetKey, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on content with id: " + this.contentId + ":" + e.getMessage(), e);
		}

		return assetUrl;
	}


	/**
	 * This method deliveres a String with the URL to the digital asset asked for. In this special case the image
	 * is fetched from the article being generated. This means that this method only is of interest if you have attached
	 * assets to either a template or to an content and are useing parsedContentAttribute.
	 */

	public String getInlineAssetUrl(Integer contentId, String assetKey)
	{
		String assetUrl = "";

		try
		{
		    Integer inlineContentId = contentId;
			CmsLogger.logInfo("getInlineAssetUrl:" + inlineContentId + ":" + this.languageId + ":" + assetKey + ":" + this.siteNodeId);
			assetUrl = ContentDeliveryController.getContentDeliveryController().getAssetUrl(inlineContentId, this.languageId, assetKey, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on content with id: " + this.contentId + ":" + e.getMessage(), e);
		}

		return assetUrl;
	}

	/*
	 *  Provide the same interface for getting asset filesize as for getting url.
	 *  This should be refactored soon, to supply a assetVO instead.
	 *
	 */

	public Integer getAssetFileSize(Integer contentId)
	{
		Integer AssetFileSize = null;
		try
		{
			AssetFileSize = ContentDeliveryController.getContentDeliveryController().getAssetFileSize(contentId, this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get AssetFileSize on content with id: " + contentId + ":" + e.getMessage(), e);
		}
		return AssetFileSize;
	}

	public Integer getAssetFileSize(Integer contentId, String assetKey)
	{
		Integer AssetFileSize = null;
		try
		{
			AssetFileSize = ContentDeliveryController.getContentDeliveryController().getAssetFileSize(contentId, this.languageId, assetKey, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get AssetFileSize on content with id: " + contentId + " and assetKey:" + assetKey + " : " + e.getMessage(), e);
		}
		return AssetFileSize;
	}

	public Integer getAssetFileSize(String contentBindningName, int index)
	{
		Integer AssetFileSize = null;
		try
		{
			List contentVOList = this.nodeDeliveryController.getBoundContents(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName, USE_INHERITANCE);
			if(contentVOList != null && contentVOList.size() > index)
			{
				ContentVO contentVO = (ContentVO)contentVOList.get(index);
				AssetFileSize = ContentDeliveryController.getContentDeliveryController().getAssetFileSize(contentVO.getContentId(), this.languageId, this.siteNodeId, USE_LANGUAGE_FALLBACK);
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get AssetFileSize on contentBindningName " + contentBindningName + ":" + e.getMessage(), e);
		}
		return AssetFileSize;
	}

	public Integer getAssetFileSize(String contentBindningName, String assetKey)
	{
		Integer AssetFileSize = null;
		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			AssetFileSize = ContentDeliveryController.getContentDeliveryController().getAssetFileSize(contentVO.getContentId(), this.languageId, assetKey, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get AssetFileSize on contentBindningName " + contentBindningName + " with assetKey " + assetKey + ":" + e.getMessage(), e);
		}
		return AssetFileSize;
	}


	/**
	 * This method gets a List of related contents defined in an attribute as an xml-definition.
	 * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
	 * used to access other systems than our own.
	 */

	public List getRelatedContentsByQualifyer(String qualifyerXML)
	{
		List relatedContentVOList = new ArrayList();

		try
		{
			relatedContentVOList = this.getRelatedContentsFromXML(qualifyerXML);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get related contents from qualifyer: " + qualifyerXML + ":" + e.getMessage(), e);
		}

		return relatedContentVOList;
	}

	/**
	 * This method gets a List of related contents defined in an attribute as an xml-definition.
	 * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
	 * used to access other systems than our own.
	 */

	public List getRelatedContents(String attributeName)
	{
		List relatedContentVOList = new ArrayList();

		try
		{
			String qualifyerXML = this.getContentAttribute(attributeName, true);

			relatedContentVOList = this.getRelatedContentsFromXML(qualifyerXML);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get related contents on contentId " + this.contentId + " with relationName " + attributeName + ":" + e.getMessage(), e);
		}

		return relatedContentVOList;
	}

	/**
	 * This method gets a List of related contents defined in an attribute as an xml-definition.
	 * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
	 * used to access other systems than our own.
	 */

	public List getRelatedContents(String bindingName, String attributeName)
	{
		List relatedContentVOList = new ArrayList();

		try
		{
			String qualifyerXML = this.getContentAttribute(bindingName, attributeName, true);

			relatedContentVOList = this.getRelatedContentsFromXML(qualifyerXML);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get related contents on contentId " + this.contentId + " with relationName " + attributeName + ":" + e.getMessage(), e);
		}

		return relatedContentVOList;
	}



	/**
	 * This method gets a List of related contents defined in an attribute as an xml-definition.
	 * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
	 * used to access other systems than our own.
	 */

	public List getRelatedContents(Integer contentId, String attributeName)
	{
		List relatedContentVOList = new ArrayList();

		try
		{
			String qualifyerXML = this.getContentAttribute(contentId, attributeName, true);

			relatedContentVOList = getRelatedContentsFromXML(qualifyerXML);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get related contents on contentId " + this.contentId + " with relationName " + attributeName + ":" + e.getMessage(), e);
		}

		return relatedContentVOList;
	}

	/**
	 * This method gets the related contents from an XML.
	 */

	private List getRelatedContentsFromXML(String qualifyerXML)
	{
		List relatedContentVOList = new ArrayList();

		try
		{
			if(qualifyerXML != null && !qualifyerXML.equals(""))
			{
				Document document = new DOMBuilder().getDocument(qualifyerXML);

				List children = document.getRootElement().elements();
				Iterator i = children.iterator();
				while(i.hasNext())
				{
					Element child = (Element)i.next();

					String id = child.attributeValue("id");
					if(id == null || id.equals(""))
						id = child.getText();

					ContentVO contentVO = ContentDeliveryController.getContentDeliveryController().getContentVO(new Integer(id));
					if(ContentDeliveryController.getContentDeliveryController().isValidContent(contentVO.getId(), this.languageId, USE_LANGUAGE_FALLBACK, getPrincipal()))
						relatedContentVOList.add(contentVO);
				}
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get related contents from qualifyerXML " + qualifyerXML + ":" + e.getMessage(), e);
		}

		return relatedContentVOList;
	}

	/**
	 * This method gets the related pages from an XML.
	 */

	private List getRelatedPagesFromXML(String qualifyerXML)
	{
		List relatedPages = new ArrayList();

		try
		{
			if(qualifyerXML != null && !qualifyerXML.equals(""))
			{
				Document document = new DOMBuilder().getDocument(qualifyerXML);

				List children = document.getRootElement().elements();
				Iterator i = children.iterator();
				while(i.hasNext())
				{
					Element child = (Element)i.next();

					String id = child.attributeValue("id");
					if(id == null || id.equals(""))
						id = child.getText();

					SiteNodeVO siteNodeVO = this.nodeDeliveryController.getSiteNode(new Integer(id)).getValueObject();
					if(this.nodeDeliveryController.isValidSiteNode(siteNodeVO.getId()))
					{
						WebPage webPage = new WebPage();
						webPage.setSiteNodeId(siteNodeVO.getSiteNodeId());
						webPage.setLanguageId(this.languageId);
						webPage.setContentId(null);
						webPage.setNavigationTitle(this.nodeDeliveryController.getPageNavigationTitle(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null, META_INFO_BINDING_NAME, NAV_TITLE_ATTRIBUTE_NAME, USE_LANGUAGE_FALLBACK));
						webPage.setMetaInfoContentId(this.nodeDeliveryController.getMetaInfoContentId(this.getPrincipal(), siteNodeVO.getSiteNodeId(), META_INFO_BINDING_NAME, USE_INHERITANCE));
						webPage.setUrl(this.nodeDeliveryController.getPageUrl(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null));
						relatedPages.add(webPage);
					}
				}
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get related contents from qualifyerXML " + qualifyerXML + ":" + e.getMessage(), e);
		}

		return relatedPages;
	}
	/**
	 * This method gets a List of related siteNodes defined in an attribute as an xml-definition.
	 * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
	 * used to access other systems than our own.
	 */

	public List getRelatedPages(String attributeName)
	{
		List relatedPages = new ArrayList();

		try
		{
			String qualifyerXML = this.getContentAttribute(attributeName, true);

			relatedPages = getRelatedPagesFromXML(qualifyerXML);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get related contents on contentId " + this.contentId + " with relationName " + attributeName + ":" + e.getMessage(), e);
		}

		return relatedPages;
	}

	/**
	 * This method gets a List of related contents defined in an attribute as an xml-definition.
	 * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
	 * used to access other systems than our own.
	 */

	public List getRelatedPages(String bindingName, String attributeName)
	{
		List relatedPages = new ArrayList();

		try
		{
			String qualifyerXML = this.getContentAttribute(bindingName, attributeName, true);

			relatedPages = getRelatedPagesFromXML(qualifyerXML);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get related contents on contentId " + this.contentId + " with relationName " + attributeName + ":" + e.getMessage(), e);
		}

		return relatedPages;
	}


	/**
	 * This method gets a List of related contents defined in an attribute as an xml-definition.
	 * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
	 * used to access other systems than our own.
	 */

	public List getRelatedPages(Integer contentId, String attributeName)
	{
		List relatedPages = new ArrayList();

		try
		{
			String qualifyerXML = this.getContentAttribute(contentId, attributeName, true);

			relatedPages = getRelatedPagesFromXML(qualifyerXML);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get related contents on contentId " + this.contentId + " with relationName " + attributeName + ":" + e.getMessage(), e);
		}

		return relatedPages;
	}


	/**
	 * This method deliveres a String with the URL to the base path of the directory resulting from
	 * an unpacking of a uploaded zip-digitalAsset.
	 */

	public String getArchiveBaseUrl(String contentBindningName, String assetKey)
	{
		String assetUrl = "";

		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			assetUrl = ContentDeliveryController.getContentDeliveryController().getArchiveBaseUrl(contentVO.getContentId(), this.languageId, assetKey, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on contentBindningName " + contentBindningName + " with assetKey " + assetKey + ":" + e.getMessage(), e);
		}

		return assetUrl;
	}


	/**
	 * This method deliveres a String with the URL to the base path of the directory resulting from
	 * an unpacking of a uploaded zip-digitalAsset.
	 */

	public String getArchiveBaseUrl(Integer contentId, String assetKey)
	{
		String assetUrl = "";

		try
		{
			assetUrl = ContentDeliveryController.getContentDeliveryController().getArchiveBaseUrl(contentId, this.languageId, assetKey, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on content with id " + contentId + " with assetKey " + assetKey + ":" + e.getMessage(), e);
		}

		return assetUrl;
	}

	public Vector getArchiveEntries(Integer contentId, String assetKey)
	{
		Vector entries = null;

		try
		{
			entries = ContentDeliveryController.getContentDeliveryController().getArchiveEntries(contentId, this.languageId, assetKey, this.siteNodeId, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on content with id " + contentId + " with assetKey " + assetKey + ":" + e.getMessage(), e);
		}

		return entries;
	}


	/**
	 * This method deliveres a String with the URL to the base path of the directory resulting from
	 * an unpacking of a uploaded zip-digitalAsset.
	 */

	public String getArchiveBaseUrl(String contentBindningName, int index, String assetKey)
	{
		String assetUrl = "";

		try
		{
			List contentVOList = this.nodeDeliveryController.getBoundContents(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName, USE_INHERITANCE);
			if(contentVOList != null && contentVOList.size() > index)
			{
				ContentVO contentVO = (ContentVO)contentVOList.get(index);
				assetUrl = ContentDeliveryController.getContentDeliveryController().getArchiveBaseUrl(contentVO.getContentId(), this.languageId, assetKey, this.siteNodeId, USE_LANGUAGE_FALLBACK);
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get assetUrl on contentBindningName " + contentBindningName + " with assetKey " + assetKey + ":" + e.getMessage(), e);
		}

		return assetUrl;
	}



	/**
	 * This method uses the content-attribute to generate a pdf-file.
	 * The content-attribute is parsed before it is sent to the renderer, and the
	 * resulting string must follow the XSL-FO specification.
	 *
	 * The method checks if a previous file exists that has the same attributes as the wanted one
	 * and if so - we don't generate it again.
	 *
	 */
	public String getContentAttributeAsPDFUrl(String contentBindningName, String attributeName)
	{
		String pdfUrl = "";

		try
		{
			String template = getParsedContentAttribute(contentBindningName, attributeName, true);
			String uniqueId = siteNodeId + "_" + attributeName + "_" + contentBindningName + template.hashCode();
			String fileName = uniqueId + ".pdf";
			String filePath = CmsPropertyHandler.getProperty("digitalAssetPath");
			File pdfFile = new File(filePath + java.io.File.separator + fileName);
			if(!pdfFile.exists())
			{
				CmsLogger.logInfo("Creating a foprenderer");
				FOPHelper fop = new FOPHelper();
				fop.generatePDF(template, pdfFile);
			}

			SiteNode siteNode = this.nodeDeliveryController.getSiteNode(this.siteNodeId);
			String dnsName = CmsPropertyHandler.getProperty("webServerAddress");
			if(siteNode != null && siteNode.getRepository().getDnsName() != null && !siteNode.getRepository().getDnsName().equals(""))
				dnsName = siteNode.getRepository().getDnsName();

			//pdfUrl = dnsName + "/" + CmsPropertyHandler.getProperty("digitalAssetBaseUrl") + "/" + fileName;
			pdfUrl = urlComposer.composeDigitalAssetUrl(dnsName, fileName);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get ContentAttribute As PDFUrl:" + e.getMessage(), e);
		}

		return pdfUrl;
	}


	/**
	 * This method uses the content-attribute to generate a pdf-file.
	 * The content-attribute is parsed before it is sent to the renderer, and the
	 * resulting string must follow the XSL-FO specification.
	 *
	 * The method checks if a previous file exists that has the same attributes as the wanted one
	 * and if so - we don't generate it again.
	 *
	 */
	public String getContentAttributeAsPDFUrl(Integer contentId, String attributeName)
	{
		String pdfUrl = "";

		try
		{
			String template = getParsedContentAttribute(contentId, attributeName, true);
			String uniqueId = contentId + "_" + attributeName + template.hashCode();
			String fileName = uniqueId + ".pdf";
			String filePath = CmsPropertyHandler.getProperty("digitalAssetPath");
			File pdfFile = new File(filePath + java.io.File.separator + fileName);
			if(!pdfFile.exists())
			{
				CmsLogger.logInfo("Creating a foprenderer");
				FOPHelper fop = new FOPHelper();
				fop.generatePDF(template, pdfFile);
			}

			SiteNode siteNode = this.nodeDeliveryController.getSiteNode(this.siteNodeId);
			String dnsName = CmsPropertyHandler.getProperty("webServerAddress");
			if(siteNode != null && siteNode.getRepository().getDnsName() != null && !siteNode.getRepository().getDnsName().equals(""))
				dnsName = siteNode.getRepository().getDnsName();

			//pdfUrl = dnsName + "/" + CmsPropertyHandler.getProperty("digitalAssetBaseUrl") + "/" + fileName;
			pdfUrl = urlComposer.composeDigitalAssetUrl(dnsName, fileName);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get ContentAttribute As PDFUrl:" + e.getMessage(), e);
		}

		return pdfUrl;
	}

	/**
	 * This method deliveres a String with the content-attribute asked for generated as a gif-file.
	 * That is - the text is printed as an image. You can specify a number of things to control the
	 * generation. Just experiment and the names are pretty much self explainatory.
	 * The method checks if a previous file exists that has the same attributes as the wanted one
	 * and if so - we don't generate it again.
	 *
	 * TODO: consider implement a more general getTextAsImageUrl so we dont need a zillion different variants
	 * for different ways to access the contentAttribute. (we need to calculate a unique string from
	 * the text and all the other stuff.)
	 *
	 */

	public String getContentAttributeAsImageUrl(String contentBindningName, String attributeName, int canvasWidth, int canvasHeight)
	{
		// Set some default values and pass on
		return getContentAttributeAsImageUrl(contentBindningName, attributeName, canvasWidth, canvasHeight, 5, 20, canvasWidth - 10, canvasHeight - 10, "Verdana", Font.BOLD, 28, Color.black, Color.white);
	}

	public String getContentAttributeAsImageUrl(String contentBindningName,String attributeName,int canvasWidth,
													int canvasHeight,int textStartPosX,int textStartPosY,int textWidth,
													int textHeight,String fontName,int fontStyle,
													int fontSize,String foregroundColor,String backgroundColor)
	{
		// Using contentBindingName: Convert color parameters and pass on
		return getContentAttributeAsImageUrl(	contentBindningName,attributeName,canvasWidth,canvasHeight,textStartPosX,textStartPosY,textWidth,textHeight,fontName,fontStyle,fontSize,
												new Color(getMathHelper().hexToDecimal(foregroundColor)),
												new Color(getMathHelper().hexToDecimal(backgroundColor)));

	}

	public String getContentAttributeAsImageUrl(String contentBindningName,String attributeName,int canvasWidth,
														int canvasHeight,int textStartPosX,int textStartPosY,int textWidth,
														int textHeight,String fontName,int fontStyle,
														int fontSize,String foregroundColor,String backgroundColor, String backgroundImageUrl)
	{
		// Using contentBindingName: Convert color parameters and pass on
		return getContentAttributeAsImageUrl(	contentBindningName,attributeName,canvasWidth,canvasHeight,textStartPosX,textStartPosY,textWidth,textHeight,fontName,fontStyle,fontSize,
												new Color(getMathHelper().hexToDecimal(foregroundColor)),
												new Color(getMathHelper().hexToDecimal(backgroundColor)));

	}

	public String getContentAttributeAsImageUrl(Integer contentId,String attributeName,int canvasWidth,
													int canvasHeight,int textStartPosX,int textStartPosY,
													int textWidth,int textHeight,String fontName,int fontStyle,
													int fontSize,String foregroundColor,String backgroundColor)
	{
		// Using contentId: Convert color parameters and pass on
		return getContentAttributeAsImageUrl(contentId,attributeName,canvasWidth,canvasHeight,textStartPosX,textStartPosY,textWidth,textHeight,fontName,fontStyle,fontSize,
												new Color(getMathHelper().hexToDecimal(foregroundColor)),
												new Color(getMathHelper().hexToDecimal(backgroundColor)));
	}

	public String getContentAttributeAsImageUrl(String contentBindningName,String attributeName,int canvasWidth,int canvasHeight,int textStartPosX,int textStartPosY,int textWidth,int textHeight,String fontName,int fontStyle,int fontSize,Color foregroundColor,	Color backgroundColor)
	{
		// Get the contentId from the contentBindingName and pass on
		String assetUrl = "";
		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			if(contentVO != null)
				assetUrl = getContentAttributeAsImageUrl(contentVO.getContentId(),attributeName,canvasWidth,canvasHeight,textStartPosX,textStartPosY,textWidth,textHeight,fontName,fontStyle,fontSize,foregroundColor,backgroundColor);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get ContentAttribute As ImageUrl:" + e.getMessage(), e);
		}

		return assetUrl;
	}

	public String getContentAttributeAsImageUrl(String contentBindningName, String attributeName, int canvasWidth, int canvasHeight, int textStartPosX, int textStartPosY, int textWidth, int textHeight, String fontName, int fontStyle, int fontSize, Color foregroundColor, Color backgroundColor, String backgroundImageUrl)
	{
		// Get the contentId from the contentBindingName and pass on
		String assetUrl = "";
		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			if(contentVO != null)
				assetUrl = getContentAttributeAsImageUrl(contentVO.getContentId(),attributeName,canvasWidth,canvasHeight,textStartPosX,textStartPosY,textWidth,textHeight,fontName,fontStyle,fontSize,foregroundColor,backgroundColor, backgroundImageUrl);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get ContentAttribute As ImageUrl:" + e.getMessage(), e);
		}

		return assetUrl;
	}


	public String getContentAttributeAsImageUrl(Integer contentId,String attributeName,int canvasWidth,
													int canvasHeight,int textStartPosX,int textStartPosY,
													int textWidth,int textHeight,String fontName,
													int fontStyle,int fontSize,
													Color foregroundColor,Color backgroundColor)
	{
		return getContentAttributeAsImageUrl(contentId, attributeName, canvasWidth, canvasHeight, textStartPosX, textStartPosY, textWidth, textHeight, fontName, fontStyle, fontSize, foregroundColor, backgroundColor, null);
	}


	public String getContentAttributeAsImageUrl(Integer contentId,String attributeName,int canvasWidth,
													int canvasHeight,int textStartPosX,int textStartPosY,
													int textWidth,int textHeight,String fontName,
													int fontStyle,int fontSize,
													Color foregroundColor,Color backgroundColor, String backgroundImageUrl)
	{
		// This one actually does something.
		String assetUrl = "";

		try
		{
			ContentVersionVO contentVersionVO = ContentDeliveryController.getContentDeliveryController().getContentVersionVO(this.siteNodeId, contentId, this.languageId, USE_LANGUAGE_FALLBACK);

			String attribute = ContentDeliveryController.getContentDeliveryController().getContentAttribute(contentVersionVO, attributeName);

			String uniqueId = contentVersionVO.getId() + "_" + attributeName + canvasWidth + canvasHeight + textStartPosX + textStartPosY + textWidth + textHeight + fontName + fontStyle + fontSize + foregroundColor.getRed() + foregroundColor.getGreen() + foregroundColor.getBlue() + backgroundColor.getRed() + backgroundColor.getGreen() + backgroundColor.getBlue();

			String fileName = uniqueId + ".png";
			String filePath = CmsPropertyHandler.getProperty("digitalAssetPath");
			File imageFile = new File(filePath + java.io.File.separator + fileName);
			if(!imageFile.exists())
			{
				CmsLogger.logInfo("Creating a imagerenderer");
				ImageRenderer imageRenderer = new ImageRenderer();
				imageRenderer.setCanvasWidth(canvasWidth);
    			imageRenderer.setCanvasHeight(canvasHeight);
		    	imageRenderer.setTextStartPosX(textStartPosX);
		    	imageRenderer.setTextStartPosY(textStartPosY);
		    	imageRenderer.setTextWidth(textWidth);
		    	imageRenderer.setTextHeight(textHeight);
				imageRenderer.setFontName(fontName);
				imageRenderer.setFontStyle(fontStyle);
				imageRenderer.setFontSize(fontSize);
				imageRenderer.setForeGroundColor(foregroundColor);
				imageRenderer.setBackgroundColor(backgroundColor);
				imageRenderer.setBackgroundImageUrl(backgroundImageUrl);

				CmsLogger.logInfo("Created imageRenderer and printing to " + filePath + java.io.File.separator + fileName);
				imageRenderer.generateGifImageFromText(filePath + java.io.File.separator + fileName, attribute, LanguageDeliveryController.getLanguageDeliveryController().getLanguageVO(this.languageId).getCharset());
				CmsLogger.logInfo("Rendered in getContentAttributeAsImageUrl");
			}

			SiteNode siteNode = this.nodeDeliveryController.getSiteNode(this.siteNodeId);
			String dnsName = CmsPropertyHandler.getProperty("webServerAddress");
			if(siteNode != null && siteNode.getRepository().getDnsName() != null && !siteNode.getRepository().getDnsName().equals(""))
				dnsName = siteNode.getRepository().getDnsName();

			//assetUrl = dnsName + "/" + CmsPropertyHandler.getProperty("digitalAssetBaseUrl") + "/" + fileName;
			assetUrl = urlComposer.composeDigitalAssetUrl(dnsName, fileName);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get ContentAttribute As ImageUrl:" + e.getMessage(), e);
		}

		return assetUrl;
	}

	/**
	 * This method returns a list of elements/attributes based on the contentType sent in.
	 */

	public List getContentAttributes(String schemaValue)
	{
		return ContentTypeDefinitionController.getController().getContentTypeAttributes(schemaValue);
	}

	/**
	 * This method deliveres a String with the content-attribute asked for generated as a png-file.
	 * That is - the text is printed as an image. You can specify a number of things to control the
	 * generation. Just experiment and the names are pretty much self explainatory.
	 * The method checks if a previous file exists that has the same attributes as the wanted one
	 * and if so - we don't generate it again.
	 */
	public String getStringAsImageUrl(String text,
										   int canvasWidth,
										   int canvasHeight,
										   int textStartPosX,
										   int textStartPosY,
										   int textWidth,
										   int textHeight,
										   String fontName,
										   int fontStyle,
										   int fontSize,
										   String foregroundColor,
										   String backgroundColor)
		{
			return 	getStringAsImageUrl(text, canvasWidth, canvasHeight,textStartPosX, textStartPosY,textWidth,textHeight,fontName,fontStyle,fontSize,
			new Color(getMathHelper().hexToDecimal(foregroundColor)),
			new Color(getMathHelper().hexToDecimal(backgroundColor)));
		}
	public String getStringAsImageUrl(String text,
			   int canvasWidth,
			   int canvasHeight,
			   int textStartPosX,
			   int textStartPosY,
			   int textWidth,
			   int textHeight,
			   String fontName,
			   int fontStyle,
			   int fontSize,
			   String foregroundColor,
			   String backgroundColor, String backGroundImageUrl)
	{
		return 	getStringAsImageUrl(text, canvasWidth, canvasHeight,textStartPosX, textStartPosY,textWidth,textHeight,fontName,fontStyle,fontSize,
		new Color(getMathHelper().hexToDecimal(foregroundColor)),
		new Color(getMathHelper().hexToDecimal(backgroundColor)), backGroundImageUrl);
	}

	public String getStringAsImageUrl(String text,
			   int canvasWidth,
			   int canvasHeight,
			   int textStartPosX,
			   int textStartPosY,
			   int textWidth,
			   int textHeight,
			   String fontName,
			   int fontStyle,
			   int fontSize,
			   Color foregroundColor,
			   Color backgroundColor)
	{
		 return getStringAsImageUrl(text,canvasWidth,canvasHeight,textStartPosX,textStartPosY,textWidth,textHeight,fontName,fontStyle,fontSize,foregroundColor,backgroundColor,null);
	}

	public String getStringAsImageUrl(String text,
									   int canvasWidth,
									   int canvasHeight,
									   int textStartPosX,
									   int textStartPosY,
									   int textWidth,
									   int textHeight,
									   String fontName,
									   int fontStyle,
									   int fontSize,
									   Color foregroundColor,
									   Color backgroundColor, String backgroundImageUrl)
	{
		String assetUrl = "";

		try
		{
			String uniqueId = text.hashCode() + "_" + canvasWidth + canvasHeight + textStartPosX + textStartPosY + textWidth + textHeight + fontName + fontStyle + fontSize + foregroundColor.getRed() + foregroundColor.getGreen() + foregroundColor.getBlue() + backgroundColor.getRed() + backgroundColor.getGreen() + backgroundColor.getBlue();

			String fileName = uniqueId + ".png";
			String filePath = CmsPropertyHandler.getProperty("digitalAssetPath");
			File imageFile = new File(filePath + java.io.File.separator + fileName);
			if(!imageFile.exists())
			{
				CmsLogger.logInfo("Creating a imagerenderer");
				ImageRenderer imageRenderer = new ImageRenderer();
				imageRenderer.setCanvasWidth(canvasWidth);
    			imageRenderer.setCanvasHeight(canvasHeight);
		    	imageRenderer.setTextStartPosX(textStartPosX);
		    	imageRenderer.setTextStartPosY(textStartPosY);
		    	imageRenderer.setTextWidth(textWidth);
		    	imageRenderer.setTextHeight(textHeight);
				imageRenderer.setFontName(fontName);
				imageRenderer.setFontStyle(fontStyle);
				imageRenderer.setFontSize(fontSize);
				imageRenderer.setForeGroundColor(foregroundColor);
				imageRenderer.setBackgroundColor(backgroundColor);
				imageRenderer.setBackgroundImageUrl(backgroundImageUrl);

				CmsLogger.logInfo("Created imageRenderer and printing to " + filePath + java.io.File.separator + fileName);
				imageRenderer.generateGifImageFromText(filePath + java.io.File.separator + fileName, text, LanguageDeliveryController.getLanguageDeliveryController().getLanguageVO(this.languageId).getCharset());
				CmsLogger.logInfo("Rendered in getContentAttributeAsImageUrl");
			}

			SiteNode siteNode = this.nodeDeliveryController.getSiteNode(this.siteNodeId);
			String dnsName = CmsPropertyHandler.getProperty("webServerAddress");
			if(siteNode != null && siteNode.getRepository().getDnsName() != null && !siteNode.getRepository().getDnsName().equals(""))
				dnsName = siteNode.getRepository().getDnsName();

			//assetUrl = dnsName + "/" + CmsPropertyHandler.getProperty("digitalAssetBaseUrl") + "/" + fileName;
			assetUrl = urlComposer.composeDigitalAssetUrl(dnsName, fileName);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to render string as an image:" + e.getMessage(), e);
		}

		return assetUrl;
	}


	/**
	 * This method returns the base url for the digital assets.
	 */

	public String getDigitalAssetBaseUrl() throws Exception
	{
		String url = getRepositoryBaseUrl() + "/" + CmsPropertyHandler.getProperty("digitalAssetBaseUrl");

		return url;
	}

	/**
	 * This method returns the base url for the digital assets.
	 */

	public String getRepositoryBaseUrl() throws Exception
	{
		String url = "";

		SiteNode siteNode = this.nodeDeliveryController.getSiteNode(this.siteNodeId);
		String dnsName = CmsPropertyHandler.getProperty("webServerAddress");
		if(siteNode != null && siteNode.getRepository().getDnsName() != null && !siteNode.getRepository().getDnsName().equals(""))
			dnsName = siteNode.getRepository().getDnsName();

		url = dnsName;

		return url;
	}


	/**
	 * This method deliveres a String with the URL to the page asked for.
	 * As the siteNode can have multiple bindings the method requires a bindingName
	 * which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getPageUrl(String structureBindningName)
	{
		String pageUrl = "";

		try
		{
			SiteNodeVO siteNodeVO = this.nodeDeliveryController.getBoundSiteNode(this.siteNodeId, structureBindningName);
			if(siteNodeVO != null)
				pageUrl = this.nodeDeliveryController.getPageUrl(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get page url for structureBindningName " + structureBindningName + ":" + e.getMessage(), e);
		}

		return pageUrl;
	}


	/**
	 * This method just gets a new URL but with the given contentId in it.
	 */

	public String getPageUrl(WebPage webpage, Integer contentId)
	{
		String pageUrl = "";

		try
		{
			pageUrl = this.nodeDeliveryController.getPageUrl(this.getPrincipal(), webpage.getSiteNodeId(), webpage.getLanguageId(), contentId);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the transformed page url " + contentId + ":" + e.getMessage(), e);
		}

		return pageUrl;
	}


	/**
	 * This method deliveres a String with the URL to the page asked for.
	 * As the siteNode can have multiple bindings the method requires a bindingName
	 * which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getPageBaseUrl(String structureBindningName)
	{
		String pageUrl = "";

		try
		{
			pageUrl = this.nodeDeliveryController.getPageBaseUrl();
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get page url for structureBindningName " + structureBindningName + ":" + e.getMessage(), e);
		}

		return pageUrl;
	}


	/**
	 * Getter for the siteNodeId on a specific bound page
	 */

	public Integer getSiteNodeId(String structureBindningName)
	{
		Integer siteNodeId = null;

		try
		{
			SiteNodeVO siteNodeVO = this.nodeDeliveryController.getBoundSiteNode(this.siteNodeId, structureBindningName);
			siteNodeId = siteNodeVO.getSiteNodeId();
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get siteNodeId for structureBindningName " + structureBindningName + ":" + e.getMessage(), e);
		}

		return siteNodeId;
	}


	/**
	 * Getter for bound contentId for a binding
	 */

	public Integer getContentId(String contentBindningName)
	{
		Integer contentId = null;

		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			if(contentVO != null)
			{
				contentId = contentVO.getId();
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get contentId for contentBindningName " + contentBindningName + ":" + e.getMessage(), e);
		}

		return contentId;
	}

	/**
	 * This method gets the meta information of the current sitenode.
	 */

	public Integer getMetaInformationContentId()
	{
		return this.getContentId(META_INFO_BINDING_NAME);
	}

	/**
	 * This method gets the meta information of a particular sitenode.
	 */

	public Integer getMetaInformationContentId(Integer siteNodeId)
	{
		return this.getContentId(siteNodeId, META_INFO_BINDING_NAME);
	}

	/**
	 * This method gets the children of a content.
	 */

	public Collection getChildContents(Integer contentId, boolean includeFolders)
	{
		List childContents = null;

		try
		{
			childContents = ContentDeliveryController.getContentDeliveryController().getChildContents(this.getPrincipal(), contentId, this.languageId, USE_LANGUAGE_FALLBACK, includeFolders);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get childContents for contentId " + contentId + ":" + e.getMessage(), e);
		}

		return childContents;
	}


	/**
	 * Getter for bound contentId for a binding on a special siteNode
	 */

	public Integer getContentId(Integer siteNodeId, String contentBindningName)
	{
		Integer contentId = null;

		try
		{
			ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
			if(contentVO != null)
			{
				contentId = contentVO.getId();
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get contentId for contentBindningName " + contentBindningName + ":" + e.getMessage(), e);
		}

		return contentId;
	}


	/**
	 * This method deliveres a String with the URL to the page asked for.
	 * As the siteNode can have multiple bindings the method requires a bindingName
	 * which refers to the AvailableServiceBinding.name-attribute. This method also allows the user
	 * to specify that the content is important. This method is mostly used for master/detail-pages.
	 */

	public String getPageUrl(String structureBindningName, Integer contentId)
	{
		String pageUrl = "";

		try
		{
			SiteNodeVO siteNodeVO = this.nodeDeliveryController.getBoundSiteNode(this.siteNodeId, structureBindningName);
			pageUrl = this.nodeDeliveryController.getPageUrl(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, contentId);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get page url for structureBindningName " + structureBindningName + ":" + e.getMessage(), e);
		}

		return pageUrl;
	}


	/**
	 * This method deliveres a String with the URL to the page asked for.
	 * As the siteNode can have multiple bindings the method requires a bindingName and also allows the user to specify a
	 * special siteNode in an ordered collection.
	 * which refers to the AvailableServiceBinding.name-attribute.
	 */

	public String getPageUrlOnPosition(String structureBindningName, int position)
	{
		String pageUrl = "";

		try
		{
			SiteNodeVO siteNodeVO = this.nodeDeliveryController.getBoundSiteNode(this.siteNodeId, structureBindningName, position);
			pageUrl = this.nodeDeliveryController.getPageUrl(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get page url for structureBindningName " + structureBindningName + ":" + e.getMessage(), e);
		}

		return pageUrl;
	}


	/**
	 * This method deliveres a String with the URL to the page asked for.
	 * As the siteNode can have multiple bindings the method requires a bindingName and also allows the user to specify a
	 * special siteNode in an ordered collection.
	 * which refers to the AvailableServiceBinding.name-attribute. This method also allows the user
	 * to specify that the content is important. This method is mostly used for master/detail-pages.
	 */

	public String getPageUrl(String structureBindningName, int position, Integer contentId)
	{
		String pageUrl = "";

		try
		{
			SiteNodeVO siteNodeVO = this.nodeDeliveryController.getBoundSiteNode(this.siteNodeId, structureBindningName, position);
			pageUrl = this.nodeDeliveryController.getPageUrl(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, contentId);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get page url for structureBindningName " + structureBindningName + ":" + e.getMessage(), e);
		}

		return pageUrl;
	}


	/**
	 * This method deliveres a new url pointing to the same address as now but in the language
	 * corresponding to the code sent in.
	 */

	public String getCurrentPageUrl()
	{
		String pageUrl = "";

		try
		{
			pageUrl = this.nodeDeliveryController.getPageUrl(this.getPrincipal(), this.siteNodeId, this.languageId, this.contentId);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get current page url:" + e.getMessage(), e);
		}

		return pageUrl;
	}


	/**
	 * This method deliveres a new url pointing to the same address as now but with new parameters.
	 */

	public String getPageUrl(Integer siteNodeId, Integer languageId, Integer contentId)
	{
		String pageUrl = "";

		try
		{
			pageUrl = this.nodeDeliveryController.getPageUrl(this.getPrincipal(), siteNodeId, languageId, contentId);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get current page url:" + e.getMessage(), e);
		}

		return pageUrl;
	}

	/**
	 * This method constructs a string representing the path to the page with respect to where in the
	 * structure the page is. It also takes the page title into consideration.
	 */

	public String getCurrentPagePath()
	{
		String pagePath = "";

		try
		{
			pagePath = this.nodeDeliveryController.getPagePath(this.getPrincipal(), this.siteNodeId, this.languageId, this.contentId, META_INFO_BINDING_NAME, NAV_TITLE_ATTRIBUTE_NAME, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get current page url:" + e.getMessage(), e);
		}

		return pagePath;
	}

	/**
	 * This method returns the parent siteNode to the given siteNode.
	 */

	public SiteNodeVO getParentSiteNode(Integer siteNodeId)
	{
		SiteNodeVO siteNodeVO = null;

		try
		{
			siteNodeVO = this.nodeDeliveryController.getParentSiteNode(siteNodeId);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get parent siteNode:" + e.getMessage(), e);
		}

		return siteNodeVO;
	}

	/**
	 * This method deliveres a new url pointing to the same address as now but in the language
	 * corresponding to the code sent in.
	 */

	public String getPageUrlAfterLanguageChange(String languageCode)
	{
		String pageUrl = "";

		try
		{
			LanguageVO languageVO = LanguageDeliveryController.getLanguageDeliveryController().getLanguageWithCode(languageCode);
			//pageUrl = this.nodeDeliveryController.getPageUrl(this.siteNodeId, languageVO.getLanguageId(), this.contentId);
			pageUrl = this.nodeDeliveryController.getPageUrlAfterLanguageChange(this.getPrincipal(), this.siteNodeId, languageVO.getLanguageId(), this.contentId);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the new page-url after language-change:" + e.getMessage(), e);
		}

		return pageUrl;
	}


	/**
	 * This method deliveres a String with the Navigation title the page the user are on has.
	 * The navigation-title is fetched from the meta-info-content bound to the site node.
	 */

	public String getPageTitle()
	{
		String navTitle = "";

		try
		{
			navTitle = this.nodeDeliveryController.getPageNavigationTitle(this.getPrincipal(), this.siteNodeId, this.languageId, null, META_INFO_BINDING_NAME, TITLE_ATTRIBUTE_NAME, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the page navigationtitle:" + e.getMessage(), e);
		}

		return navTitle;
	}

	/**
	 * This method deliveres a String with the Navigation title the page asked for has.
	 * As the siteNode can have multiple bindings the method requires a bindingName
	 * which refers to the AvailableServiceBinding.name-attribute. The navigation-title is fetched
	 * from the meta-info-content bound to the site node.
	 */

	public String getPageNavTitle(String structureBindningName)
	{
		String navTitle = "";

		try
		{
			SiteNodeVO siteNodeVO = this.nodeDeliveryController.getBoundSiteNode(this.siteNodeId, structureBindningName);
			CmsLogger.logInfo(siteNodeVO.getName());
			navTitle = this.nodeDeliveryController.getPageNavigationTitle(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null, META_INFO_BINDING_NAME, NAV_TITLE_ATTRIBUTE_NAME, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the page navigationtitle:" + e.getMessage(), e);
		}

		return navTitle;
	}

	/**
	 * This method deliveres a String with the Navigation title the page asked for has.
	 * The navigation-title is fetched from the meta-info-content bound to the site node.
	 */

	public String getPageNavTitle(Integer siteNodeId)
	{
		String navTitle = "";

		try
		{
			navTitle = this.nodeDeliveryController.getPageNavigationTitle(this.getPrincipal(), siteNodeId, this.languageId, null, META_INFO_BINDING_NAME, NAV_TITLE_ATTRIBUTE_NAME, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the page navigationtitle:" + e.getMessage(), e);
		}

		return navTitle;
	}

	/**
	 * This method deliveres a String with the Navigation title the page asked for has.
	 * As the siteNode can have multiple bindings the method requires a bindingName and a collection index.
	 * The navigation-title is fetched from the meta-info-content bound to the site node.
	 */

	public String getPageNavTitle(String structureBindningName, int index)
	{
		String navTitle = "";

		try
		{
			SiteNodeVO siteNodeVO = this.nodeDeliveryController.getBoundSiteNode(this.siteNodeId, structureBindningName, index);
			CmsLogger.logInfo(siteNodeVO.getName());
			navTitle = this.nodeDeliveryController.getPageNavigationTitle(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null, META_INFO_BINDING_NAME, NAV_TITLE_ATTRIBUTE_NAME, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the page navigationtitle:" + e.getMessage(), e);
		}

		return navTitle;
	}


	/**
	 * This method returns true if the if the page in question (ie sitenode) has page-caching disabled.
	 * This is essential to turn off when you have a dynamic page like an external application or searchresult.
	 */

	public boolean getIsPageCacheDisabled()
	{
		boolean isPageCacheDisabled = false;

		try
		{
			isPageCacheDisabled = this.nodeDeliveryController.getIsPageCacheDisabled(this.siteNodeId);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("An error occurred trying to get if the siteNodeVersion has disabled pageCache:" + e.getMessage(), e);
		}

		return isPageCacheDisabled;
	}

	/**
	 * This method returns the contenttype this page should return. This is important when sending assets or css:contents.
	 */

	public String getPageContentType()
	{
		String pageContentType = "text/html";

		try
		{
			SiteNodeVersionVO latestSiteNodeVersionVO = this.nodeDeliveryController.getLatestActiveSiteNodeVersionVO(this.siteNodeId);
			if(latestSiteNodeVersionVO != null && latestSiteNodeVersionVO.getContentType() != null && latestSiteNodeVersionVO.getContentType().length() > 0)
				pageContentType = latestSiteNodeVersionVO.getContentType();
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("An error occurred trying to get the content type of the page:" + e.getMessage(), e);
		}

		return pageContentType;
	}

	/**
	 * This method returns true if the page in question (ie sitenode) has it's protected property enabled.
	 * This is essential when checking if we should authenticate users before allowing them access.
	 */

	public boolean getIsPageProtected()
	{
		boolean isPageProtected = false;

		try
		{
			isPageProtected = this.nodeDeliveryController.getIsPageProtected(this.siteNodeId);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("An error occurred trying to get if the siteNodeVersion has protect page:" + e.getMessage(), e);
		}

		return isPageProtected;
	}

	/**
	 * This method returns true if the page in question (ie sitenode) has page-caching disabled.
	 * This is essential to turn off when you have a dynamic page like an external application or searchresult.
	 */

	public boolean getIsEditOnSightDisabled()
	{
		boolean isEditOnSightDisabled = false;

		try
		{
			isEditOnSightDisabled = this.nodeDeliveryController.getIsEditOnSightDisabled(this.siteNodeId);
		}
		catch(Exception e)
		{
			CmsLogger.logWarning("An error occurred trying to get if the siteNodeVersion has disabled editOnSight:" + e.getMessage(), e);
		}

		return isEditOnSightDisabled;
	}


	/**
	 * This method returns a list of all languages available on the current site/repository.
	 */

	public List getAvailableLanguages()
	{
		List availableLanguages = new ArrayList();

		try
		{
			availableLanguages = LanguageDeliveryController.getLanguageDeliveryController().getAvailableLanguages(this.siteNodeId);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get all available languages:" + e.getMessage(), e);
		}

		return availableLanguages;
	}


	/**
	 * This method returns a list of all languages available on the current sitenode. The logic is that
	 * we check which languages are found in the meta-content in the current mode.
	 */

	public List getNodeAvailableLanguages()
	{
		return getNodeAvailableLanguages(this.siteNodeId);
	}

	public List getNodeAvailableLanguages(Integer siteNodeId)
	{
		List availableLanguages = new ArrayList();

		try
		{
			availableLanguages = LanguageDeliveryController.getLanguageDeliveryController().getAvailableLanguages(siteNodeId);
			Iterator languageIterator = availableLanguages.iterator();
			while(languageIterator.hasNext())
			{
				LanguageVO languageVO = (LanguageVO)languageIterator.next();
				ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, META_INFO_BINDING_NAME);
				ContentVersionVO contentVersionVO = null;
				if(contentVO != null)
				{
					contentVersionVO = ContentDeliveryController.getContentDeliveryController().getContentVersionVO(siteNodeId, contentVO.getId(), languageVO.getId(), false);
				}

				if(contentVO == null || contentVersionVO == null)
				{
					CmsLogger.logWarning("The meta-info did not have a version of " + languageVO.getName());
					languageIterator.remove();
				}
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get all available languages:" + e.getMessage(), e);
		}

		return availableLanguages;
	}


	/**
	 * The method returns a list of WebPage-objects that is the children of the current
	 * siteNode. The method is great for navigation-purposes on a structured site.
	 */

	public List getChildPages()
	{
		List childPages = new ArrayList();
		try
		{
			List childNodeVOList = this.nodeDeliveryController.getChildSiteNodes(this.siteNodeId);
			Iterator i = childNodeVOList.iterator();
			while(i.hasNext())
			{
				SiteNodeVO siteNodeVO = (SiteNodeVO)i.next();
				WebPage webPage = new WebPage();
				webPage.setSiteNodeId(siteNodeVO.getSiteNodeId());
				webPage.setLanguageId(this.languageId);
				webPage.setContentId(null);
				webPage.setNavigationTitle(this.nodeDeliveryController.getPageNavigationTitle(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null, META_INFO_BINDING_NAME, NAV_TITLE_ATTRIBUTE_NAME, USE_LANGUAGE_FALLBACK));
				webPage.setMetaInfoContentId(this.nodeDeliveryController.getMetaInfoContentId(this.getPrincipal(), siteNodeVO.getSiteNodeId(), META_INFO_BINDING_NAME, USE_INHERITANCE));
				webPage.setUrl(this.nodeDeliveryController.getPageUrl(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null));
				childPages.add(webPage);
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the page childPages:" + e.getMessage(), e);
		}

		return childPages;
	}


	/**
	 * The method returns a list of WebPage-objects that is the children of the given
	 * siteNode. The method is great for navigation-purposes on a structured site.
	 */

	public List getChildPages(String structureBindingName)
	{
		List childPages = new ArrayList();
		try
		{
			List childNodeVOList = this.nodeDeliveryController.getChildSiteNodes(this.getSiteNodeId(structureBindingName));
			Iterator i = childNodeVOList.iterator();
			while(i.hasNext())
			{
				SiteNodeVO siteNodeVO = (SiteNodeVO)i.next();
				WebPage webPage = new WebPage();
				webPage.setSiteNodeId(siteNodeVO.getSiteNodeId());
				webPage.setLanguageId(this.languageId);
				webPage.setContentId(null);
				webPage.setNavigationTitle(this.nodeDeliveryController.getPageNavigationTitle(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null, META_INFO_BINDING_NAME, NAV_TITLE_ATTRIBUTE_NAME, USE_LANGUAGE_FALLBACK));
				webPage.setMetaInfoContentId(this.nodeDeliveryController.getMetaInfoContentId(this.getPrincipal(), siteNodeVO.getSiteNodeId(), META_INFO_BINDING_NAME, USE_INHERITANCE));
				webPage.setUrl(this.nodeDeliveryController.getPageUrl(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null));
				childPages.add(webPage);
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the page childPages:" + e.getMessage(), e);
		}

		return childPages;
	}

	/**
	 * The method returns a list of WebPage-objects that is the children of the given
	 * siteNode. The method is great for navigation-purposes on a structured site.
	 */

	public List getChildPages(Integer siteNodeId)
	{
		List childPages = new ArrayList();
		try
		{
			List childNodeVOList = this.nodeDeliveryController.getChildSiteNodes(siteNodeId);
			Iterator i = childNodeVOList.iterator();
			while(i.hasNext())
			{
				SiteNodeVO siteNodeVO = (SiteNodeVO)i.next();
				WebPage webPage = new WebPage();
				webPage.setSiteNodeId(siteNodeVO.getSiteNodeId());
				webPage.setLanguageId(this.languageId);
				webPage.setContentId(null);
				webPage.setNavigationTitle(this.nodeDeliveryController.getPageNavigationTitle(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null, META_INFO_BINDING_NAME, NAV_TITLE_ATTRIBUTE_NAME, USE_LANGUAGE_FALLBACK));
				webPage.setMetaInfoContentId(this.nodeDeliveryController.getMetaInfoContentId(this.getPrincipal(), siteNodeVO.getSiteNodeId(), META_INFO_BINDING_NAME, USE_INHERITANCE));
				webPage.setUrl(this.nodeDeliveryController.getPageUrl(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null));
				childPages.add(webPage);
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the page childPages:" + e.getMessage(), e);
		}

		return childPages;
	}

	/**
	 * The method returns a list of WebPage-objects that is the bound sitenodes of named binding.
	 * The method is great for navigation-purposes on any site.
	 */

	private HashMap cachedBindings = new HashMap();

	public List getBoundPages(String structureBindningName)
	{
		//Checking for a read binding in this request...
		if(cachedBindings.containsKey(structureBindningName))
			return (List)cachedBindings.get(structureBindningName);

		List boundPages = new ArrayList();
		try
		{
			List siteNodeVOList = this.nodeDeliveryController.getBoundSiteNodes(this.siteNodeId, structureBindningName);

			Iterator i = siteNodeVOList.iterator();
			while(i.hasNext())
			{
				SiteNodeVO siteNodeVO = (SiteNodeVO)i.next();
				WebPage webPage = new WebPage();
				webPage.setSiteNodeId(siteNodeVO.getSiteNodeId());
				webPage.setLanguageId(this.languageId);
				webPage.setContentId(null);
				webPage.setNavigationTitle(this.nodeDeliveryController.getPageNavigationTitle(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null, META_INFO_BINDING_NAME, NAV_TITLE_ATTRIBUTE_NAME, USE_LANGUAGE_FALLBACK));
				webPage.setMetaInfoContentId(this.nodeDeliveryController.getMetaInfoContentId(this.getPrincipal(), siteNodeVO.getSiteNodeId(), META_INFO_BINDING_NAME, USE_INHERITANCE));
				webPage.setUrl(this.nodeDeliveryController.getPageUrl(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null));
				boundPages.add(webPage);
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the bound pages:" + e.getMessage(), e);
		}

		//Caching bindings
		cachedBindings.put(structureBindningName, boundPages);

		return boundPages;
	}


	/**
	 * This methods get a list of bound pages with the structureBindningName sent in which resides on the siteNodeId sent in.
	 */

	public List getBoundPages(Integer siteNodeId, String structureBindningName)
	{
		//Checking for a read binding in this request...
		if(cachedBindings.containsKey(siteNodeId + "_" + structureBindningName))
			return (List)cachedBindings.get(siteNodeId + "_" + structureBindningName);

		List boundPages = new ArrayList();
		try
		{
			List siteNodeVOList = this.nodeDeliveryController.getBoundSiteNodes(siteNodeId, structureBindningName);

			Iterator i = siteNodeVOList.iterator();
			while(i.hasNext())
			{
				SiteNodeVO siteNodeVO = (SiteNodeVO)i.next();
				WebPage webPage = new WebPage();
				webPage.setSiteNodeId(siteNodeVO.getSiteNodeId());
				webPage.setLanguageId(this.languageId);
				webPage.setContentId(null);
				webPage.setNavigationTitle(this.nodeDeliveryController.getPageNavigationTitle(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null, META_INFO_BINDING_NAME, NAV_TITLE_ATTRIBUTE_NAME, USE_LANGUAGE_FALLBACK));
				webPage.setMetaInfoContentId(this.nodeDeliveryController.getMetaInfoContentId(this.getPrincipal(), siteNodeVO.getSiteNodeId(), META_INFO_BINDING_NAME, USE_INHERITANCE));
				webPage.setUrl(this.nodeDeliveryController.getPageUrl(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null));
				boundPages.add(webPage);
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the bound pages:" + e.getMessage(), e);
		}

		//Caching bindings
		cachedBindings.put(siteNodeId + "_" + structureBindningName, boundPages);

		return boundPages;
	}



	/**
	 * The method returns a list of WebPage-objects that is the bound sitenodes of named binding.
	 * The method is great for navigation-purposes on any site.
	 * We also filter out all pages that don't have a localized version of the page meta-content.
	 */

	public List getLocalizedBoundPages(String structureBindningName)
	{
		//Checking for a read binding in this request...
		if(cachedBindings.containsKey(structureBindningName))
			return (List)cachedBindings.get(structureBindningName);

		List boundPages = new ArrayList();
		try
		{
			List siteNodeVOList = this.nodeDeliveryController.getBoundSiteNodes(this.siteNodeId, structureBindningName);

			Iterator i = siteNodeVOList.iterator();
			while(i.hasNext())
			{
				SiteNodeVO siteNodeVO = (SiteNodeVO)i.next();

				Integer metaInfoContentId = this.nodeDeliveryController.getMetaInfoContentId(this.getPrincipal(), siteNodeVO.getSiteNodeId(), META_INFO_BINDING_NAME, DO_NOT_USE_INHERITANCE);
				String navigationTitle = this.nodeDeliveryController.getPageNavigationTitle(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null, META_INFO_BINDING_NAME, NAV_TITLE_ATTRIBUTE_NAME, DO_NOT_USE_LANGUAGE_FALLBACK);
				if(metaInfoContentId != null && navigationTitle != null && !navigationTitle.equals(""))
				{
					WebPage webPage = new WebPage();
					webPage.setSiteNodeId(siteNodeVO.getSiteNodeId());
					webPage.setLanguageId(this.languageId);
					webPage.setContentId(null);
					webPage.setNavigationTitle(navigationTitle);
					webPage.setMetaInfoContentId(metaInfoContentId);
					webPage.setUrl(this.nodeDeliveryController.getPageUrl(this.getPrincipal(), siteNodeVO.getSiteNodeId(), this.languageId, null));
					boundPages.add(webPage);
				}
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the bound pages:" + e.getMessage(), e);
		}

		//Caching bindings
		cachedBindings.put(structureBindningName, boundPages);

		return boundPages;
	}



	/**
	 * The method returns a single ContentVO-objects that is the bound content of named binding.
	 * It's used for getting one content.
	 */

	public ContentVO getBoundContent(String structureBindningName)
	{
		ContentVO content = null;

		List contents = getBoundContents(structureBindningName);

		if(contents != null && contents.size() > 0)
			content = (ContentVO)contents.get(0);

		return content;
	}


	/**
	 * The method returns a list of ContentVO-objects that is the bound content of named binding.
	 * The method is great for collection-pages on any site.
	 */

	public List getBoundContents(String structureBindningName)
	{
		//Checking for a read binding in this request...
		if(cachedBindings.containsKey(structureBindningName))
			return (List)cachedBindings.get(structureBindningName);

		List boundContents = new ArrayList();
		try
		{
			boundContents = this.nodeDeliveryController.getBoundContents(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, structureBindningName, USE_INHERITANCE);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the bound contents:" + e.getMessage(), e);
		}

		//Caching bindings
		cachedBindings.put(structureBindningName, boundContents);

		return boundContents;
	}


	/**
	 * The method returns a list of ContentVO-objects that is children to the bound content of named binding.
	 * The method is great for collection-pages on any site where you want to bind to a folder containing all contents to list.
	 * You can also state if the method should recurse into subfolders and how the contents should be sorted.
	 * The recursion only deals with three levels at the moment for performance-reasons.
	 */

	public List getBoundFolderContents(String structureBindningName, boolean searchRecursive, String sortAttribute, String sortOrder)
	{
		//Checking for a read binding in this request...
		//if(cachedBindings.containsKey(structureBindningName))
		//{
		//	return (List)cachedBindings.get(structureBindningName);
		//}

		List boundContents = new ArrayList();
		try
		{
			boundContents = this.nodeDeliveryController.getBoundFolderContents(this.getPrincipal(), this.siteNodeId, this.languageId, structureBindningName, searchRecursive, new Integer(3), sortAttribute, sortOrder, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the bound contents:" + e.getMessage(), e);
		}

		//Caching bindings
		//cachedBindings.put(structureBindningName, boundContents);

		return boundContents;
	}


	/**
	 * The method returns a list of ContentVO-objects that is children to the bound content of named binding on the siteNode sent in.
	 * The method is great for collection-pages on any site where you want to bind to a folder containing all contents to list.
	 * You can also state if the method should recurse into subfolders and how the contents should be sorted.
	 * The recursion only deals with three levels at the moment for performance-reasons.
	 */

	public List getBoundFolderContents(Integer siteNodeId, String structureBindningName, boolean searchRecursive, String sortAttribute, String sortOrder)
	{
	    List boundContents = new ArrayList();
		try
		{
			boundContents = this.nodeDeliveryController.getBoundFolderContents(this.getPrincipal(), siteNodeId, this.languageId, structureBindningName, searchRecursive, new Integer(3), sortAttribute, sortOrder, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the bound contents:" + e.getMessage(), e);
		}

		return boundContents;
	}

	/**
	 * The method returns a list of ContentVO-objects that is children to the bound content sent in.
	 * The method is great for collection-pages on any site where you want to bind to a folder containing all contents to list.
	 * You can also state if the method should recurse into subfolders and how the contents should be sorted.
	 * The recursion only deals with three levels at the moment for performance-reasons.
	 */

	public List getChildContents(Integer contentId, boolean searchRecursive, String sortAttribute, String sortOrder)
	{
		List childContents = new ArrayList();
		try
		{
			childContents = this.nodeDeliveryController.getBoundFolderContents(this.getPrincipal(), contentId, this.languageId, searchRecursive, new Integer(3), sortAttribute, sortOrder, USE_LANGUAGE_FALLBACK);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the bound contents:" + e.getMessage(), e);
		}

		return childContents;
	}


	/**
	 * The method returns the ContentTypeVO-objects of the given contentId.
	 */

	public ContentTypeDefinitionVO getContentTypeDefinitionVO(Integer contentId)
	{
		ContentTypeDefinitionVO contentTypeDefinition = null;

		try
		{
			contentTypeDefinition = ContentDeliveryController.getContentDeliveryController().getContentTypeDefinitionVO(contentId);

		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the bound contents:" + e.getMessage(), e);
		}

		return contentTypeDefinition;
	}

	/**
	 * The method returns the ContentTypeVO-object with the given name.
	 */

	public ContentTypeDefinitionVO getContentTypeDefinitionVO(String name)
	{
		ContentTypeDefinitionVO contentTypeDefinition = null;

		try
		{
			contentTypeDefinition = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithName(name);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get the bound contents:" + e.getMessage(), e);
		}

		return contentTypeDefinition;
	}

	/**
	 * The method returns a list of WebPage-objects that is the bound sitenodes of named binding.
	 * The method is great for navigation-purposes on any site. Improve later so the list is cached
	 * once for every instance. Otherwise we fetch the whole list again and its not necessairy as
	 * this controller only concerns one request.
	 */

	public WebPage getBoundPage(String structureBindningName, int position)
	{
		List boundPages = getBoundPages(structureBindningName);

		if(boundPages.size() > position)
			return (WebPage)boundPages.get(position);
		else
			return null;
	}


	/**
	 * This method allows a user to get any string rendered as a template.
	 */

	public String renderString(String template)
	{
		String result = "";

		try
		{
			Map context = new HashMap();
			context.put("inheritedTemplateLogic", this);
			context.put("templateLogic", getTemplateController(this.siteNodeId, this.languageId, this.contentId, this.request, this.infoGluePrincipal));

			// Add the templateLogicContext objects to this context. (SS - 040219)
			context.putAll(templateLogicContext);

			StringWriter cacheString = new StringWriter();
			PrintWriter cachedStream = new PrintWriter(cacheString);
			new VelocityTemplateProcessor().renderTemplate(context, cachedStream, template);
			result = cacheString.toString();
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to do an include:" + e.getMessage(), e);
		}

		return result;
	}

	/**
	 * This method allows the current template to include another template which is also rendered
	 * in the current context as if it were a part. The method assumes that the result can be cached.
	 * Use the other include method if you wish to be able to control if the result is cached or not.
	 */


	public String include(String contentBindningName, String attributeName)
	{
		return include(contentBindningName, attributeName, true);
	}

	public String include(String contentBindningName, String attributeName, boolean cacheInclude)
	{
		return include(contentBindningName, attributeName, cacheInclude, null, null);
	}

	/**
	 * This method allows the current template to include another template which is also rendered
	 * in the current context as if it were a part.
	 * Use this method if you wish to be able to control if the result is cached or not.
	 */
	public String include(String contentBindningName, String attributeName, boolean cacheInclude, String cName, Object cObject)
	{
		String includeKey = "" + this.siteNodeId + "_" + this.languageId + "_" + this.contentId + "_" + browserBean.getUseragent() + "_" + contentBindningName + "_" + attributeName;
		CmsLogger.logInfo("includeKey:" + includeKey);
		String result = (String)CacheController.getCachedObject("includeCache", includeKey);
		if(result != null)
		{
			CmsLogger.logInfo("There was an cached include:" + result);
		}
		else
		{
			try
			{
				ContentVO contentVO = this.nodeDeliveryController.getBoundContent(this.getPrincipal(), this.siteNodeId, this.languageId, USE_LANGUAGE_FALLBACK, contentBindningName);
				if(contentVO != null)
				{
					String includedTemplate = ContentDeliveryController.getContentDeliveryController().getContentAttribute(contentVO.getContentId(), this.languageId, "Template", this.siteNodeId, USE_LANGUAGE_FALLBACK);
					CmsLogger.logInfo("Found included template:" + includedTemplate);

					Map context = new HashMap();
					context.put("inheritedTemplateLogic", this);
					context.put("templateLogic", getTemplateController(this.siteNodeId, this.languageId, this.contentId, this.request, this.infoGluePrincipal));

					// Add the templateLogicContext objects to this context. (SS - 040219)
					context.putAll(templateLogicContext);

					if (cName != null)
						context.put(cName, cObject);

					StringWriter cacheString = new StringWriter();
					PrintWriter cachedStream = new PrintWriter(cacheString);
					new VelocityTemplateProcessor().renderTemplate(context, cachedStream, includedTemplate);
					result = cacheString.toString();

					CmsLogger.logInfo("result:" + result);

					if(cacheInclude)
						CacheController.cacheObject("includeCache", includeKey, result);
				}
			}
			catch(Exception e)
			{
				CmsLogger.logSevere("An error occurred trying to do an include:" + e.getMessage(), e);
			}
		}

		return result;
	}


	/**
	 * This method fetches a given URL contents. This means that we can include a external url's contents
	 * in our application.
	 */

	public String getUrlContent(String url)
	{
		String contents = "";

		try
		{
			CmsLogger.logInfo("We are going to do an include on an external webpage: " + url);
			contents = this.integrationDeliveryController.getUrlContent(url, request, true);
			//CmsLogger.logInfo("The respons was: " + contents);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to do an include the url:" + url, e);
		}

		return contents;
	}

	/**
	 * This method fetches a given URL contents. This means that we can include a external url's contents
	 * in our application. This second method is used to not send extra params through.
	 */

	public String getUrlContent(String url, boolean includeRequest)
	{
		String contents = "";

		try
		{
			CmsLogger.logInfo("We are going to do an include on an external webpage: " + url);
			contents = this.integrationDeliveryController.getUrlContent(url, request, includeRequest);
			//CmsLogger.logInfo("The respons was: " + contents);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to do an include the url:" + url, e);
		}

		return contents;
	}

	/**
	 * This method fetches a given URL contents. This means that we can include a external url's contents
	 * in our application.
	 */

	public String getUrlContent(String url, String encoding)
	{
		String contents = "";

		try
		{
			CmsLogger.logInfo("We are going to do an include on an external webpage: " + url);
			contents = this.integrationDeliveryController.getUrlContent(url, request, true, encoding);
			//CmsLogger.logInfo("The respons was: " + contents);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to do an include the url:" + url, e);
		}

		return contents;
	}

	/**
	 * This method fetches a given URL contents. This means that we can include a external url's contents
	 * in our application. This second method is used to not send extra params through.
	 */

	public String getUrlContent(String url, boolean includeRequest, String encoding)
	{
		String contents = "";

		try
		{
			CmsLogger.logInfo("We are going to do an include on an external webpage: " + url);
			contents = this.integrationDeliveryController.getUrlContent(url, request, includeRequest, encoding);
			//CmsLogger.logInfo("The respons was: " + contents);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to do an include the url:" + url, e);
		}

		return contents;
	}


	public Object getObjectWithName(String classname)
	{
		try
		{
			return this.integrationDeliveryController.getObjectWithName(classname, request);
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * This method lets a user substitute a string located in the page by a regular expression with another
	 * string. Very useful in certain situations.
	 */

	public String replace(String originalString, String expressionToReplace, String newString)
	{
		return originalString.replaceAll(expressionToReplace, newString);
	}

	/**
	 * This method lets a user substitute a string located in the page by a regular expression with another
	 * string. This method also lets the user specify a subpart of the string to be able to be more
	 * specific. Very useful in certain situations.
	 */

	public String replace(String originalString, String substring, String stringToReplace, String newString)
	{
		StringBuffer result = new StringBuffer();
		int startIndex = 0;
		int stopIndex  = 0;
		int offset     = 0;

		try
		{
			List substrings = search(originalString, substring);

			Iterator substringsIterator = substrings.iterator();
			while(substringsIterator.hasNext())
			{
				String currentSubstring = (String)substringsIterator.next();
				String newSubstring = currentSubstring.replaceAll(stringToReplace, newString);
				startIndex = originalString.indexOf(currentSubstring, offset);
				stopIndex   = startIndex + currentSubstring.length();
				result.append(originalString.substring(offset, startIndex));
				result.append(originalString.substring(startIndex, stopIndex));
				result.append(newSubstring);
				offset = stopIndex;
			}

			if(offset < originalString.length())
			{
				result.append(originalString.substring(offset));
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("The replace function experienced an error:" + e.getMessage(), e);
		}

		return result.toString();
	}



	/**
	 * This method searches for matches to a special expression.
	 * TODO: Move to an utility class
	 * @param containsMatches
	 * @param regexp
	 * @return
	 */

	private List search(String containsMatches, String regexp) throws Exception
	{
		List foundMatches = new ArrayList();
		int matches = 0;

		PatternCompiler compiler = new Perl5Compiler();
		PatternMatcher matcher = new Perl5Matcher();
		Pattern pattern   = null;

		try
		{
			pattern = compiler.compile(regexp);
		}
		catch (MalformedPatternException e)
		{
			throw new Exception("A bad pattern was entered:" + e.getMessage());
		}

		PatternMatcherInput input = new PatternMatcherInput(containsMatches);

		while (matcher.contains(input, pattern))
		{
			MatchResult result = matcher.getMatch();
			++matches;
			foundMatches.add(result.toString());
		}

		return foundMatches;
	}



	/**
	 * This method helps us find out if the current site node is the same or a child to the sent in one.
	 * So if the current page is a child(in the entire hierarchy below) below the siteNode sent in the
	 * method returns true. Useful for navigational purposes.
	 */

	public boolean getIsParentToCurrent(Integer siteNodeId)
	{
		return getIsParentToCurrentRecursive(siteNodeId, this.siteNodeId);
	}

	/**
	 * This method helps us find out if the current site node is the same or a child to the sent in one.
	 */

	private boolean getIsParentToCurrentRecursive(Integer siteNodeId, Integer currentSiteNodeId)
	{
		boolean isParentToCurrent = false;

		try
		{
			if(currentSiteNodeId.intValue() == siteNodeId.intValue())
			{
				isParentToCurrent = true;
			}
			else
			{
				SiteNodeVO parentSiteNodeVO = this.nodeDeliveryController.getParentSiteNode(currentSiteNodeId);
				if(parentSiteNodeVO != null)
					isParentToCurrent = getIsParentToCurrentRecursive(siteNodeId, parentSiteNodeVO.getSiteNodeId());
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred:" + e.getMessage(), e);
		}

		return isParentToCurrent;
	}

	/**
	 * This method return true if a localized version with the current language exist
	 */

	public boolean getHasLocalizedVersion(Integer contentId)
	{
		boolean ret = false;
		try
		{
			ret = ContentDeliveryController.getContentDeliveryController().getContentVersionVO(this.siteNodeId, contentId, this.languageId, false) != null;
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get determine if content:" + contentId + " has a localized version:" + e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * This method return true if the user logged in has access to the siteNode sent in.
	 */

	public boolean getHasUserPageAccess(Integer siteNodeId)
	{
		boolean hasUserPageAccess = false;

		try
		{
		    Integer protectedSiteNodeVersionId = this.nodeDeliveryController.getProtectedSiteNodeVersionId(siteNodeId);
			if(protectedSiteNodeVersionId == null)
			{
				CmsLogger.logInfo("The page was not protected...");
				hasUserPageAccess = true;
			}
			else
			{
				CmsLogger.logInfo("The page was protected...");
				Principal principal = this.getPrincipal();
				CmsLogger.logInfo("Principal:" + principal);

				if(principal != null)
				{
					//SiteNodeVersionVO siteNodeVersionVO = this.nodeDeliveryController.getActiveSiteNodeVersionVO(siteNodeId);
					hasUserPageAccess = AccessRightController.getController().getIsPrincipalAuthorized((InfoGluePrincipal)principal, "SiteNodeVersion.Read", protectedSiteNodeVersionId.toString());
				}
			}

		    /*
			if(!this.nodeDeliveryController.getIsPageProtected(siteNodeId))
			{
				CmsLogger.logInfo("The page was not protected...");
				hasUserPageAccess = true;
			}
			else
			{
				CmsLogger.logInfo("The page was protected...");
				Principal principal = this.getPrincipal();
				CmsLogger.logInfo("Principal:" + principal);

				if(principal != null)
				{
					SiteNodeVersionVO siteNodeVersionVO = this.nodeDeliveryController.getActiveSiteNodeVersionVO(siteNodeId);
					hasUserPageAccess = AccessRightController.getController().getIsPrincipalAuthorized((InfoGluePrincipal)principal, "SiteNodeVersion.Read", siteNodeVersionVO.getId().toString());
					//hasUserPageAccess = ExtranetController.getController().getIsPrincipalAuthorized(principal, "SiteNode", "" + siteNodeId, this.nodeDeliveryController); }
				}
			}
			*/
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to get determine if content:" + contentId + " has a localized version:" + e.getMessage(), e);
		}

		return hasUserPageAccess;
	}

	/**
	 * This method return true if the user logged in has access to the siteNode sent in.
	 */

	public boolean getHasUserPageWriteAccess(Integer siteNodeId)
	{
		boolean hasUserPageWriteAccess = false;

		try
		{
		    Integer protectedSiteNodeVersionId = this.nodeDeliveryController.getProtectedSiteNodeVersionId(siteNodeId);
			if(protectedSiteNodeVersionId == null)
			{
				CmsLogger.logInfo("The page was not protected...");
				hasUserPageWriteAccess = true;
			}
			else
			{
				CmsLogger.logInfo("The page was protected...");
				Principal principal = this.getPrincipal();
				CmsLogger.logInfo("Principal:" + principal);

				if(principal != null)
				{
				    hasUserPageWriteAccess = AccessRightController.getController().getIsPrincipalAuthorized((InfoGluePrincipal)principal, "SiteNodeVersion.Write", protectedSiteNodeVersionId.toString());
				}
			}
		    /*
			if(!this.nodeDeliveryController.getIsPageProtected(siteNodeId))
			{
				CmsLogger.logInfo("The page was not protected...");
				hasUserPageWriteAccess = true;
			}
			else
			{
				CmsLogger.logInfo("The page was protected...");
				Principal principal = (Principal)this.request.getSession().getAttribute("infogluePrincipal");
				CmsLogger.logInfo("Principal:" + principal);
				if(principal != null)
				{
					SiteNodeVersionVO siteNodeVersionVO = this.nodeDeliveryController.getActiveSiteNodeVersionVO(siteNodeId);
					hasUserPageWriteAccess = AccessRightController.getController().getIsPrincipalAuthorized((InfoGluePrincipal)principal, "SiteNodeVersion.Write", siteNodeVersionVO.getId().toString());
					//hasUserPageWriteAccess = ExtranetController.getController().getIsPrincipalAuthorizedForWriteAccess(principal, "SiteNode", "" + siteNodeId, this.nodeDeliveryController);
				}
			}
			*/
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred trying to find out if the user had write access to page:" + siteNodeId + ": " + e.getMessage(), e);
		}

		return hasUserPageWriteAccess;
	}

	/**
	 * This method returns a list of form elements/attributes based on the schema sent in.
	 * These consitutes the entire form and a template can then be used to render it in the appropriate technique.
	 */

	public List getFormAttributes(String contentBindningName, String attributeName)
	{
		String formDefinition = getContentAttribute(contentBindningName, attributeName, true);
		return FormDeliveryController.getFormDeliveryController().getContentTypeAttributes(formDefinition);
	}

	/**
	 * This method returns a list of form elements/attributes based on the schema sent in.
	 * These consitutes the entire form and a template can then be used to render it in the appropriate technique.
	 */

	public List getFormAttributes(Integer contentId, String attributeName)
	{
		String formDefinition = getContentAttribute(contentId, attributeName, true);
		return FormDeliveryController.getFormDeliveryController().getContentTypeAttributes(formDefinition);
	}


	/**
	 * This method returns the full list of steps for a workflow.
	 */

	public List getWorkflowSteps(String workflowId)
	{
	    List workflowSteps = null;

	    try
	    {
	        InfoGluePrincipal infoGluePrincipal = this.getPrincipal();
	        if(infoGluePrincipal == null)
	        {
			    Map arguments = new HashMap();
			    arguments.put("j_username", "anonymous");
			    arguments.put("j_password", "anonymous");

	            infoGluePrincipal = (InfoGluePrincipal) ExtranetController.getController().getAuthenticatedPrincipal(arguments);
	        }

			WorkflowController workflowController = WorkflowController.getController();
			CmsLogger.logInfo("infoGluePrincipal:" + infoGluePrincipal);
			CmsLogger.logInfo("workflowId:" + workflowId);
			workflowSteps = workflowController.getAllSteps(infoGluePrincipal, new Long(workflowId).longValue());
	    }
	    catch(Exception e)
	    {
	        CmsLogger.logWarning("An error occurred when trying to get the steps available: " + e.getMessage(), e);
	    }

		return workflowSteps;
	}

	/**
	 * This method returns the list of hsitorical steps for a workflow instance.
	 */

	public List getWorkflowHistoricalSteps(String workflowId)
	{
	    List workflowSteps = null;

	    try
	    {
	        InfoGluePrincipal infoGluePrincipal = this.getPrincipal();
	        if(infoGluePrincipal == null)
	        {
	            Map arguments = new HashMap();
	            arguments.put("j_username", "anonymous");
			    arguments.put("j_password", "anonymous");

			    infoGluePrincipal = (InfoGluePrincipal) ExtranetController.getController().getAuthenticatedPrincipal(arguments);
	        }

			WorkflowController workflowController = WorkflowController.getController();
			workflowSteps = workflowController.getHistorySteps(infoGluePrincipal, new Long(workflowId).longValue());
	    }
	    catch(Exception e)
	    {
	        CmsLogger.logWarning("An error occurred when trying to get the steps available: " + e.getMessage(), e);
	    }

		return workflowSteps;
	}

	/**
	 * This method returns the list of hsitorical steps for a workflow instance.
	 */

	public List getWorkflowCurrentSteps(String workflowId)
	{
	    List workflowSteps = null;

	    try
	    {
	        InfoGluePrincipal infoGluePrincipal = this.getPrincipal();
	        if(infoGluePrincipal == null)
	        {
	            Map arguments = new HashMap();
	            arguments.put("j_username", "anonymous");
			    arguments.put("j_password", "anonymous");

		        infoGluePrincipal = (InfoGluePrincipal) ExtranetController.getController().getAuthenticatedPrincipal(arguments);
	        }

			WorkflowController workflowController = WorkflowController.getController();
			workflowSteps = workflowController.getCurrentSteps(infoGluePrincipal, new Long(workflowId).longValue());
	    }
	    catch(Exception e)
	    {
	        CmsLogger.logWarning("An error occurred when trying to get the steps available: " + e.getMessage(), e);
	    }

		return workflowSteps;
	}

	/**
	 * This method supplies a method to get the locale of the language sent in.
	 */

	public Locale getLanguageCode(Integer languageId)
	{
		return LanguageDeliveryController.getLanguageDeliveryController().getLocaleWithId(languageId);
	}

	/**
	 * This method supplies a method to get the locale of the language currently in use.
	 */

	public Locale getLocale()
	{
		return LanguageDeliveryController.getLanguageDeliveryController().getLocaleWithId(this.languageId);
	}

   	/**
	 * This method should be much more sophisticated later and include a check to see if there is a
	 * digital asset uploaded which is more specialized and can be used to act as serverside logic to the template.
	 */

	public TemplateController getTemplateController(Integer siteNodeId, Integer languageId, Integer contentId, InfoGluePrincipal infoGluePrincipal) throws SystemException, Exception
	{
		return getTemplateController(siteNodeId, languageId, contentId, this.request, infoGluePrincipal);
	}

	public TemplateController getTemplateController(Integer siteNodeId, Integer languageId, Integer contentId, HttpServletRequest request, InfoGluePrincipal infoGluePrincipal) throws SystemException, Exception
	{
		TemplateController templateController = null;
		templateController = new BasicTemplateController(infoGluePrincipal);
		templateController.setStandardRequestParameters(siteNodeId, languageId, contentId);
		templateController.setHttpRequest(request);
		templateController.setBrowserBean(this.browserBean);
		templateController.setDeliveryControllers(this.nodeDeliveryController, null, this.integrationDeliveryController);
		return templateController;
	}

	public String decoratePage(String page)
	{
		return page;
	}


}