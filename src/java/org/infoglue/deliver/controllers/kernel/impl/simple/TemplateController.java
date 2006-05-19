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

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.exolab.castor.jdo.Database;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.DesEncryptionHelper;
import org.infoglue.deliver.applications.databeans.DatabaseWrapper;
import org.infoglue.deliver.applications.databeans.DeliveryContext;
import org.infoglue.deliver.applications.databeans.WebPage;
import org.infoglue.deliver.util.BrowserBean;
import org.infoglue.deliver.util.MathHelper;
import org.infoglue.deliver.util.ObjectConverter;
import org.infoglue.deliver.util.charts.ChartHelper;
import org.infoglue.deliver.util.graphics.ColorHelper;
import org.infoglue.deliver.util.graphics.FontHelper;
import org.infoglue.deliver.util.rss.RssHelper;
import org.infoglue.deliver.util.webservices.WebServiceHelper;


/**
 * @author Mattias Bogeblad
 */
public interface TemplateController
{
    //public void getDatabaseStatus(String debugMessage);
    
    /**
     * Sets the transaction the controller should work within. This is to limit the number of connections we use. 
     */
    public abstract Database getDatabase() throws SystemException;
    
	public abstract DatabaseWrapper getDatabaseWrapper();

	/**
     * Commits and reopens a database object so we don't have to long transaction. 
     */
	
    public abstract void commitDatabase() throws SystemException;

    /** 
     * Add objects to be used in subsequent parsing
     * like getParsedContentAttribute, include, etc 
     */
    public abstract void addToContext(String name, Object object);

    /**
     * Setter for the template to get all the parameters from the user.
     */
    public abstract void setStandardRequestParameters(Integer siteNodeId, Integer languageId, Integer contentId);

    /**
     * Setter for the template to get all the parameters from the user.
     */
    public abstract void setHttpRequest(HttpServletRequest request);

    /**
     * Setter for the bean which contains information about the users browser.
     */
    public abstract void setBrowserBean(BrowserBean browserBean);

    /**
     * Getter for the template attribute name.
     */
    public abstract String getTemplateAttributeName();

    /**
     * Getter for the siteNodeId
     */
    public abstract Integer getSiteNodeId();

    /**
     * Getter for the languageId
     */
    public abstract Integer getLanguageId();

    /**
     * Getter for the contentId
     */
    public abstract Integer getContentId();

    /**
     * This method gets a component logic helper object.
     */
    public abstract ComponentLogic getComponentLogic();

    /**
     * This method gets a component logic helper object.
     */
    public abstract void setComponentLogic(ComponentLogic componentLogic);

    /**
     * This method gets the formatter object that helps with formatting of data.
     */
    public abstract VisualFormatter getVisualFormatter();

    /**
     * This method gets the color utility.
     */
    public abstract ColorHelper getColorHelper();

    /**
     * This method gets the color utility.
     */
    public abstract FontHelper getFontHelper();

    /**
     * This method gets the math utility.
     */
    public abstract MathHelper getMathHelper();

    /**
     * This method gets the math utility.
     */
    public abstract ChartHelper getChartHelper();

    /**
     * This method gets the webservice utility.
     */
    public abstract WebServiceHelper getWebServiceHelper();

    /**
     * This method gets the NumberFormat instance with the proper locale.
     */
    public abstract NumberFormat getNumberFormatHelper() throws SystemException;

	/**
	 * This method gets the DesEncryptionHelper.
	 */
	public DesEncryptionHelper getDesEncryptionHelper();

	/**
	 * This method gets the rss utility.
	 */
	public RssHelper getRssHelper();

    /**
     * This method gets the object converter utility.
     */
    public abstract ObjectConverter getObjectConverter();

    /**
     * Getter for the current content
     */
    public abstract ContentVO getContent();

    /**
     * Getter for the current content
     */
    public abstract ContentVO getContent(Integer contentId);

    public abstract InfoGluePrincipal getPrincipal();

    /**
     * This method returns the InfoGlue Principal requested
     * 
     * @param userName
     */
    
    public abstract InfoGluePrincipal getPrincipal(String userName);
    
    /**
     * Getting a property for the current Principal - used for personalisation. 
     * This method starts with getting the property on the user and if it does not exist we check out the
     * group-properties as well.
     */
    public abstract String getPrincipalPropertyValue(String propertyName);

    /**
     * Getting a property for the current Principal - used for personalisation. 
     * This method starts with getting the property on the user and if it does not exist we check out the
     * group-properties as well.
     */
    public abstract Map getPrincipalPropertyHashValues(String propertyName);

    /**
     * Getting a property for the current Principal - used for personalisation. 
     * This method starts with getting the property on the user and if it does not exist we check out the
     * group-properties as well.
     */
    public abstract String getPrincipalPropertyValue(
            InfoGluePrincipal infoGluePrincipal, String propertyName);

    /**
     * Getting a property for a Principal - used for personalisation. 
     * This method starts with getting the property on the user and if it does not exist we check out the
     * group-properties as well.
     */
    public abstract Map getPrincipalPropertyHashValues(
            InfoGluePrincipal infoGluePrincipal, String propertyName);

    /**
     * Getting a property for a Principal - used for personalisation. 
     * This method starts with getting the property on the user and if it does not exist we check out the
     * group-properties as well.
     */
    public abstract String getPrincipalPropertyValue(
            InfoGluePrincipal infoGluePrincipal, String propertyName,
            boolean escapeSpecialCharacters);

    /**
     * Getting a property for the current Principal - used for personalisation. 
     * This method starts with getting the property on the user and if it does not exist we check out the
     * group-properties as well.
     */
    public abstract String getPrincipalPropertyValue(String propertyName,
            boolean escapeSpecialCharacters);

    /**
     * Getting a property for a Principal - used for personalisation. 
     * This method starts with getting the property on the user and if it does not exist we check out the
     * group-properties as well.
     */
    public abstract Map getPrincipalPropertyHashValues(
            InfoGluePrincipal infoGluePrincipal, String propertyName,
            boolean escapeSpecialCharacters);

    /**
     * Getting a property for the current Principal - used for personalisation. 
     * This method starts with getting the property on the user and if it does not exist we check out the
     * group-properties as well.
     */
    public abstract Map getPrincipalPropertyHashValues(String propertyName,
            boolean escapeSpecialCharacters);

    /**
     * Getter for request-object
     */
    public abstract HttpServletRequest getHttpServletRequest();


    /**
     * Getter for request-parameters
     */
    public abstract Enumeration getRequestParamenterNames();

    /**
     * Getter for request-parameter
     */
    public abstract String getRequestParameter(String parameterName);

    /**
     * Getter for request-parameters
     */
    public abstract String[] getRequestParameterValues(String parameterName);

    /**
     * Getter for the browserBean which supplies information about the users browser, OS and other stuff.
     */
    public abstract BrowserBean getBrowserBean();

    /**
     * Setting to enable us to set initialized versions of the Node and Content delivery Controllers.
     */
    public abstract void setDeliveryControllers(
            NodeDeliveryController nodeDeliveryController,
            ContentDeliveryController contentDeliveryController,
            IntegrationDeliveryController integrationDeliveryController);

    /**
     * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
     * value if OnSiteEdit is on.
     *      */
    public abstract String getContentAttribute(String contentBindningName, String attributeName, boolean clean);

    /**
     * This method is just a dummy method used to ensure that we can ensure to
     * not get a decorated attribute value if OnSiteEdit is on.
     */
    public abstract String getContentAttribute(String attributeName,
            boolean clean);

    /**
     * This method deliveres a String with the content-attribute asked for a
     * specific content and ensure not to get decorated attributes if EditOnSite is
     * turned on.
     * 
     * @param contentId
     *            the contentId of a content
     * @param attributeName
     *            the attribute name in the content. (ie. Title, Leadin etc)
     * @param clean
     *            true if the content should be decorated in the editonsite
     *            working mode. No decoration is made if content-attribute is
     *            empty.
     * @return the contentAttribute or empty string if none found.
     */
    public abstract String getContentAttribute(Integer contentId,
            String attributeName, boolean clean);

    /**
     * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
     * value if OnSiteEdit is on.
     */
    
    public abstract String getContentAttribute(Integer contentId,
            Integer langaugeId, String attributeName, boolean clean);

    /**
     * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
     * value if OnSiteEdit is on.
     */

	public String getContentAttributeWithReturningId(Integer contentId, 
	        Integer languageId, String attributeName, boolean clean, List contentVersionId);

    /**
     * This method deliveres a String with the content-attribute asked for if it exists in the content
     * defined in the url-parameter contentId.
     */
    public abstract String getContentAttribute(String attributeName);

    /**
     * This method deliveres a String with the content-attribute asked for.
     * As the siteNode can have multiple bindings as well as a content as a parameter this
     * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getContentAttribute(String contentBindningName,
            String attributeName);

    /**
     * This method deliveres a String with the content-attribute asked for.
     * As the siteNode can have multiple bindings as well as a content as a parameter this
     * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getContentAttribute(Integer contentId,
            String attributeName);

    /**
     * This method deliveres a String with the content-attribute asked for in the language asked for.
     * As the siteNode can have multiple bindings as well as a content as a parameter this
     * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getContentAttribute(Integer contentId,
            Integer languageId, String attributeName);
	 

    /**
     * This method deliveres a String with the content-attribute asked for after it has been parsed and all special tags have been converted.
     * As the siteNode can have multiple bindings as well as a content as a parameter this
     * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getParsedContentAttribute(String attributeName);

    /**
     * This method deliveres a String with the content-attribute asked for after it has been parsed and all special tags have been converted.
     * As the siteNode can have multiple bindings as well as a content as a parameter this
     * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getParsedContentAttribute(
            String contentBindningName, String attributeName);

    /**
     * This method deliveres a String with the content-attribute asked for after it has been parsed and all special tags have been converted.
     * The attribute is fetched from the specified content.
     */
    public abstract String getParsedContentAttribute(Integer contentId,
            String attributeName);

    /**
     * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
     * value if OnSiteEdit is on.
     */
    public abstract String getParsedContentAttribute(String attributeName,
            boolean clean);

    /**
     * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
     * value if OnSiteEdit is on.
     */
    public abstract String getParsedContentAttribute(
            String contentBindningName, String attributeName, boolean clean);
    
    /**
     * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
     * value if OnSiteEdit is on.
     */
    public abstract String getParsedContentAttribute(
            Integer contentId, String attributeName, boolean clean);

    /**
     * This method is just a dummy method used to ensure that we can ensure to not get a decorated attribute
     * value if OnSiteEdit is on.
     */
    public abstract String getParsedContentAttribute(
            Integer contentId, Integer languageId, String attributeName, boolean clean);

    /**
     * This method deliveres a list of strings which represents all assetKeys for a content.
     */
    public abstract Collection getAssetKeys(String contentBindningName);

    /**
     * This method deliveres a list of strings which represents all assetKeys for a content.
     */
    public abstract Collection getAssetKeys(Integer contentId);

    /**
     * This method deliveres a String with the URL to the thumbnail for the digital asset asked for.
     * This method assumes that the content sent in only has one asset attached.
     */
    public abstract String getAssetThumbnailUrl(Integer contentId, int width,
            int height);

    /**
     * This method deliveres a String with the URL to the thumbnail for the digital asset asked for.
     * This method takes a key for the asset you want to make a thumbnail from.
     */
    public abstract String getAssetThumbnailUrl(Integer contentId,
            String assetKey, int width, int height);

    /**
     * This method deliveres a String with the URL to the thumbnail of the digital asset asked for.
     * As the siteNode can have multiple bindings as well as a content as a parameter this
     * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getAssetThumbnailUrl(String contentBindningName,
            int width, int height);

    /**
     * This method deliveres a String with the URL to the thumbnail of the digital asset asked for.
     * As the siteNode can have multiple bindings as well as a content as a parameter this
     * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getAssetThumbnailUrl(String contentBindningName,
            String assetKey, int width, int height);

    /**
     * This method deliveres a String with the URL to the digital asset asked for.
     * As the siteNode can have multiple bindings as well as a content as a parameter this
     * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getAssetUrl(String contentBindningName);

    public abstract String getEncodedUrl(String s, String enc);

    /**
     * This method deliveres a String with the URL to the digital asset asked for.
     */
    public abstract String getAssetUrl(Integer contentId);

    /**
     * This method deliveres a String with the URL to the digital asset asked for.
     */
    public abstract String getAssetUrl(Integer contentId, String assetKey);

    /**
     * This method deliveres a String with the URL to the digital asset asked for.
     * As the siteNode can have multiple bindings as well as a content as a parameter this
     * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getAssetUrl(String contentBindningName, int index);

    /**
     * This method deliveres a String with the URL to the digital asset asked for.
     * As the siteNode can have multiple bindings as well as a content as a parameter this
     * parameter requires a bindingName which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getAssetUrl(String contentBindningName,
            String assetKey);

    /**
     * This method deliveres a String with the URL to the digital asset asked for. In this special case the image
     * is fetched from the article being generated. This means that this method only is of interest if you have attached
     * assets to either a template or to an content and are useing parsedContentAttribute.
     */
    public abstract String getInlineAssetUrl(String assetKey);

    /*
     *  Provide the same interface for getting asset filesize as for getting url. 
     *  This should be refactored soon, to supply a assetVO instead.   
     *
     */public abstract Integer getAssetFileSize(Integer contentId);

    public abstract Integer getAssetFileSize(Integer contentId, String assetKey);

    public abstract Integer getAssetFileSize(String contentBindningName,
            int index);

    public abstract Integer getAssetFileSize(String contentBindningName,
            String assetKey);

    /**
     * This method gets a List of related contents defined in an attribute as an xml-definition.
     * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
     * used to access other systems than our own.
     */
    public abstract List getRelatedContentsByQualifyer(String qualifyerXML);

    /**
     * This method gets a List of related contents defined in an attribute as an xml-definition.
     * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
     * used to access other systems than our own.
     */
    public abstract List getRelatedContents(String attributeName);

    /**
     * This method gets a List of related contents defined in an attribute as an xml-definition.
     * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
     * used to access other systems than our own.
     */
    public abstract List getRelatedContents(String bindingName,
            String attributeName);

    /**
     * This method gets a List of related contents defined in an attribute as an xml-definition.
     * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
     * used to access other systems than our own.
     */
    public abstract List getRelatedContents(Integer contentId,
            String attributeName);

    /**
     * This method gets a List of related siteNodes defined in an attribute as an xml-definition.
     * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
     * used to access other systems than our own.
     */
    public abstract List getRelatedPages(String attributeName);

    /**
     * This method gets a List of related contents defined in an attribute as an xml-definition.
     * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
     * used to access other systems than our own.
     */
    public abstract List getRelatedPages(String bindingName,
            String attributeName);

    /**
     * This method gets a List of related contents defined in an attribute as an xml-definition.
     * This is an ugly method right now. Later we should have xmlDefinitions that are fully qualified so it can be
     * used to access other systems than our own.
     */
    public abstract List getRelatedPages(Integer contentId, String attributeName);

    /**
     * This method deliveres a String with the URL to the base path of the directory resulting from 
     * an unpacking of a uploaded zip-digitalAsset.
     */
    public abstract String getArchiveBaseUrl(String contentBindningName,
            String assetKey);

    /**
     * This method deliveres a String with the URL to the base path of the directory resulting from 
     * an unpacking of a uploaded zip-digitalAsset.
     */
    public abstract String getArchiveBaseUrl(Integer contentId, String assetKey);

    public abstract Vector getArchiveEntries(Integer contentId, String assetKey);

    /**
     * This method deliveres a String with the URL to the base path of the directory resulting from 
     * an unpacking of a uploaded zip-digitalAsset.
     */
    public abstract String getArchiveBaseUrl(String contentBindningName,
            int index, String assetKey);

    /**
     * This method uses the content-attribute to generate a pdf-file.
     * The content-attribute is parsed before it is sent to the renderer, and the
     * resulting string must follow the XSL-FO specification.
     * 
     * The method checks if a previous file exists that has the same attributes as the wanted one
     * and if so - we don't generate it again.
     * 
     */
    public abstract String getContentAttributeAsPDFUrl(
            String contentBindningName, String attributeName);

	/**
	 * This method returns a list of elements/attributes based on the contentType sent in. 
	 */
	
	public abstract List getContentAttributes(String schemaValue);

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
    public abstract String getContentAttributeAsImageUrl(
            String contentBindningName, String attributeName, int canvasWidth,
            int canvasHeight);

    public abstract String getContentAttributeAsImageUrl(
            String contentBindningName, String attributeName, int canvasWidth,
            int canvasHeight, int textStartPosX, int textStartPosY,
            int textWidth, int textHeight, String fontName, int fontStyle,
            int fontSize, String foregroundColor, String backgroundColor);

    public abstract String getContentAttributeAsImageUrl(
            String contentBindningName, String attributeName, int canvasWidth,
            int canvasHeight, int textStartPosX, int textStartPosY,
            int textWidth, int textHeight, String fontName, int fontStyle,
            int fontSize, String foregroundColor, String backgroundColor,
            String backgroundImageUrl);

    public abstract String getContentAttributeAsImageUrl(Integer contentId,
            String attributeName, int canvasWidth, int canvasHeight,
            int textStartPosX, int textStartPosY, int textWidth,
            int textHeight, String fontName, int fontStyle, int fontSize,
            String foregroundColor, String backgroundColor);

    public abstract String getContentAttributeAsImageUrl(
            String contentBindningName, String attributeName, int canvasWidth,
            int canvasHeight, int textStartPosX, int textStartPosY,
            int textWidth, int textHeight, String fontName, int fontStyle,
            int fontSize, Color foregroundColor, Color backgroundColor);

    public abstract String getContentAttributeAsImageUrl(
            String contentBindningName, String attributeName, int canvasWidth,
            int canvasHeight, int textStartPosX, int textStartPosY,
            int textWidth, int textHeight, String fontName, int fontStyle,
            int fontSize, Color foregroundColor, Color backgroundColor,
            String backgroundImageUrl);

    public abstract String getContentAttributeAsImageUrl(Integer contentId,
            String attributeName, int canvasWidth, int canvasHeight,
            int textStartPosX, int textStartPosY, int textWidth,
            int textHeight, String fontName, int fontStyle, int fontSize,
            Color foregroundColor, Color backgroundColor);

    public abstract String getContentAttributeAsImageUrl(Integer contentId,
            String attributeName, int canvasWidth, int canvasHeight,
            int textStartPosX, int textStartPosY, int textWidth,
            int textHeight, String fontName, int fontStyle, int fontSize,
            Color foregroundColor, Color backgroundColor,
            String backgroundImageUrl);

    /**
     * This method deliveres a String with the content-attribute asked for generated as a png-file.
     * That is - the text is printed as an image. You can specify a number of things to control the 
     * generation. Just experiment and the names are pretty much self explainatory.
     * The method checks if a previous file exists that has the same attributes as the wanted one
     * and if so - we don't generate it again.
     */
    public abstract String getStringAsImageUrl(String text, int canvasWidth,
            int canvasHeight, int textStartPosX, int textStartPosY,
            int textWidth, int textHeight, String fontName, int fontStyle,
            int fontSize, String foregroundColor, String backgroundColor);

    public abstract String getStringAsImageUrl(String text, int canvasWidth,
            int canvasHeight, int textStartPosX, int textStartPosY,
            int textWidth, int textHeight, String fontName, int fontStyle,
            int fontSize, Color foregroundColor, Color backgroundColor);

    /**
     * This method returns the base url for the digital assets.
     */
    public abstract String getDigitalAssetBaseUrl() throws Exception;

	/**
	 * This method returns the Id the digital assets.
	 */
	
	public Integer getDigitalAssetId(Integer contentId, String assetKey) throws Exception;

	/**
	 * This method returns the Id the digital assets.
	 */
	
	public Integer getDigitalAssetId(Integer contentId, Integer languageId, String assetKey) throws Exception;

	/**
	 * This method returns the parent repositoryId if any for the given repository.
	 */
	
	public Integer getParentRepositoryId(Integer repositoryId);

	/**
	 * This method returns the parent repositoryId if any for the given repository.
	 */
	
	public Integer getParentRepositoryId();

	/**
	 * This method returns the root node for the current repository.
	 */
	
	public SiteNodeVO getRepositoryRootSiteNode() throws Exception;

	/**
	 * This method returns the root node for the current repository.
	 */
	
	public SiteNodeVO getRepositoryRootSiteNode(Integer repositoryId) throws Exception;

    /**
     * This method returns the base url for the digital assets.
     */
    public abstract String getRepositoryBaseUrl() throws Exception;

    /**
     * This method deliveres a String with the URL to the page asked for.
     * As the siteNode can have multiple bindings the method requires a bindingName 
     * which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getPageUrl(String structureBindningName);

    /**
     * This method just gets a new URL but with the given contentId in it.
     */
    public abstract String getPageUrl(WebPage webpage, Integer contentId);

    /**
     * This method deliveres a String with the URL to the page asked for.
     * As the siteNode can have multiple bindings the method requires a bindingName 
     * which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getPageBaseUrl(String structureBindningName);

    /**
     * Getter for the siteNodeId on a specific bound page
     */
    public abstract Integer getSiteNodeId(String structureBindningName);

    /**
	 * This method fetches the given siteNode
	 */
	public abstract SiteNodeVO getSiteNode(Integer siteNodeId);

    /**
	 * This method fetches the given siteNode
	 */
	public abstract SiteNodeVO getSiteNode();

    /**
     * Getter for bound contentId for a binding
     */
    public abstract Integer getContentId(String contentBindningName);

	/**
	 * This method gets the meta information of the current sitenode.
	 */
	
	public Integer getMetaInformationContentId();
	
	/**
	 * This method gets the meta information of a particular sitenode.
	 */
	
	public Integer getMetaInformationContentId(Integer siteNodeId);

    /**
     * This method gets the children of a content.
     */
    public abstract Collection getChildContents(Integer contentId,
            boolean includeFolders);

    /**
     * Getter for bound contentId for a binding on a special siteNode
     */
    public abstract Integer getContentId(Integer siteNodeId,
            String contentBindningName);

    /**
     * This method deliveres a String with the URL to the page asked for.
     * As the siteNode can have multiple bindings the method requires a bindingName 
     * which refers to the AvailableServiceBinding.name-attribute. This method also allows the user
     * to specify that the content is important. This method is mostly used for master/detail-pages.
     */
    public abstract String getPageUrl(String structureBindningName,
            Integer contentId);

    /**
     * This method deliveres a String with the URL to the page asked for.
     * As the siteNode can have multiple bindings the method requires a bindingName and also allows the user to specify a 
     * special siteNode in an ordered collection. 
     * which refers to the AvailableServiceBinding.name-attribute. 
     */
    public abstract String getPageUrlOnPosition(String structureBindningName,
            int position);

    /**
     * This method deliveres a String with the URL to the page asked for.
     * As the siteNode can have multiple bindings the method requires a bindingName and also allows the user to specify a 
     * special siteNode in an ordered collection. 
     * which refers to the AvailableServiceBinding.name-attribute. This method also allows the user
     * to specify that the content is important. This method is mostly used for master/detail-pages.
     */
    public abstract String getPageUrl(String structureBindningName,
            int position, Integer contentId);

    /**
     * This method deliveres a new url pointing to the same address as now but in the language 
     * corresponding to the code sent in.
     */
    public abstract String getCurrentPageUrl();

	/**
	 * This method returns the exact full url from the original request - not modified
	 * @return
	 */
	
	public String getOriginalFullURL();

    /**
     * This method deliveres a new url pointing to the same address as now but with new parameters.
     */
    public abstract String getPageUrl(Integer siteNodeId, Integer languageId,
            Integer contentId);

    /**
     * This method constructs a string representing the path to the page with respect to where in the
     * structure the page is. It also takes the page title into consideration.
     */
    public abstract String getCurrentPagePath();

    /**
     * This method returns the parent siteNode to the given siteNode.
     */
    public abstract SiteNodeVO getParentSiteNode(Integer siteNodeId);

    /**
     * This method deliveres a new url pointing to the same address as now but in the language 
     * corresponding to the code sent in.
     */
    public abstract String getPageUrlAfterLanguageChange(String languageCode);

    /**
     * This method deliveres a String with the Navigation title the page the user are on has.
     * The navigation-title is fetched from the meta-info-content bound to the site node.
     */
    public abstract String getPageTitle();

    /**
     * This method deliveres a String with the Navigation title the page asked for has.
     * As the siteNode can have multiple bindings the method requires a bindingName 
     * which refers to the AvailableServiceBinding.name-attribute. The navigation-title is fetched
     * from the meta-info-content bound to the site node.
     */
    public abstract String getPageNavTitle(String structureBindningName);

    /**
     * This method deliveres a String with the Navigation title the page asked for has.
     * The navigation-title is fetched from the meta-info-content bound to the site node.
     */
    public abstract String getPageNavTitle(Integer siteNodeId);

    /**
     * This method deliveres a String with the Navigation title the page asked for has.
     * As the siteNode can have multiple bindings the method requires a bindingName and a collection index. 
     * The navigation-title is fetched from the meta-info-content bound to the site node.
     */
    public abstract String getPageNavTitle(String structureBindningName,
            int index);

    /**
     * This method returns true if the if the page in question (ie sitenode) has page-caching disabled.
     * This is essential to turn off when you have a dynamic page like an external application or searchresult.
     */
    public abstract boolean getIsPageCacheDisabled();

    /**
     * This method returns the contenttype this page should return. This is important when sending assets or css:contents.
     */
    public abstract String getPageContentType();

    /**
     * This method returns true if the page in question (ie sitenode) has it's protected property enabled.
     * This is essential when checking if we should authenticate users before allowing them access.
     */
    public abstract boolean getIsPageProtected();

    /**
     * This method returns true if the page in question (ie sitenode) has page-caching disabled.
     * This is essential to turn off when you have a dynamic page like an external application or searchresult.
     */
    public abstract boolean getIsEditOnSightDisabled();

	/**
	 * This method returns true if the user is in component editor mode.
	 */
	public boolean getIsInPageComponentMode();

    /**
     * This method returns a list of all languages available on the current site/repository.
     */
    public abstract List getAvailableLanguages();

    /**
     * This method returns a list of all languages available on the current sitenode. The logic is that 
     * we check which languages are found in the meta-content in the current mode.
     * @deprecated - use getPageLanguages() instead
     */
    public abstract List getNodeAvailableLanguages();

    /**
     * This method returns a list of all languages available on the current sitenode. The logic is that 
     * we check which languages are found in the meta-content in the current mode.
     * @deprecated - use getPageLanguages(Integer siteNodeId) instead
     */
    public abstract List getNodeAvailableLanguages(Integer siteNodeId);

	/**
	 * This method returns a list of all languages available on the current sitenode. This method will return all languages enabled for this repository minus 
	 * any disabled languages for the siteNode.
	 */
	public List getPageLanguages();

	/**
	 * This method returns a list of all languages available on the current sitenode. This method will return all languages enabled for this repository minus 
	 * any disabled languages for the siteNode.
	 */
	public List getPageLanguages(Integer siteNodeId);

	/**
     * The method returns a list of WebPage-objects that is the children of the current 
     * siteNode. The method is great for navigation-purposes on a structured site. 
     */
    public abstract List getChildPages();

    /**
     * The method returns a list of WebPage-objects that is the children of the given 
     * siteNode. The method is great for navigation-purposes on a structured site. 
     */
    public abstract List getChildPages(String structureBindingName);

    /**
     * The method returns a list of WebPage-objects that is the children of the given 
     * siteNode. The method is great for navigation-purposes on a structured site. 
     */
    public abstract List getChildPages(Integer siteNodeId, boolean escapeHTML, boolean hideUnauthorizedPages);

    /**
     * The method returns a list of WebPage-objects that is the children of the given 
     * siteNode. The method is great for navigation-purposes on a structured site. 
     */
    public abstract List getChildPages(Integer siteNodeId);

    
    public abstract List getBoundPages(String structureBindningName);

    /**
     * This methods get a list of bound pages with the structureBindningName sent in which resides on the siteNodeId sent in.
     */
    public abstract List getBoundPages(Integer siteNodeId, String structureBindningName);

    /**
     * The method returns a list of WebPage-objects that is the bound sitenodes of named binding. 
     * The method is great for navigation-purposes on any site. 
     * We also filter out all pages that don't have a localized version of the page meta-content.
     */
    public abstract List getLocalizedBoundPages(String structureBindningName);

    /**
     * The method returns a single ContentVO-objects that is the bound content of named binding. 
     * It's used for getting one content. 
     */
    public abstract ContentVO getBoundContent(String structureBindningName);

    /**
     * The method returns a list of ContentVO-objects that is the bound content of named binding. 
     * The method is great for collection-pages on any site. 
     */
    public abstract List getBoundContents(String structureBindningName);

    /**
     * The method returns a list of ContentVO-objects that is children to the bound content of named binding. 
     * The method is great for collection-pages on any site where you want to bind to a folder containing all contents to list.
     * You can also state if the method should recurse into subfolders and how the contents should be sorted.
     * The recursion only deals with three levels at the moment for performance-reasons. 
     */
    public abstract List getBoundFolderContents(String structureBindningName,
            boolean searchRecursive, String sortAttribute, String sortOrder);

    /**
     * The method returns a list of ContentVO-objects that is children to the bound content of named binding on the siteNode sent in. 
     * The method is great for collection-pages on any site where you want to bind to a folder containing all contents to list.
     * You can also state if the method should recurse into subfolders and how the contents should be sorted.
     * The recursion only deals with three levels at the moment for performance-reasons. 
     */
    public abstract List getBoundFolderContents(Integer siteNodeId,
            String structureBindningName, boolean searchRecursive,
            String sortAttribute, String sortOrder);

    /**
     * The method returns a list of ContentVO-objects that is children to the bound content sent in. 
     * The method is great for collection-pages on any site where you want to bind to a folder containing all contents to list.
     * You can also state if the method should recurse into subfolders and how the contents should be sorted.
     * The recursion only deals with three levels at the moment for performance-reasons. 
     */
    public abstract List getChildContents(Integer contentId,
            boolean searchRecursive, String sortAttribute, String sortOrder);

	/**
	 * The method returns a list of ContentVO-objects that is children to the bound content sent in. 
	 * The method is great for collection-pages on any site where you want to bind to a folder containing all contents to list.
	 * You can also state if the method should recurse into subfolders and how the contents should be sorted.
	 * The recursion only deals with three levels at the moment for performance-reasons. 
	 */
	
	public List getChildContents(Integer contentId, boolean searchRecursive, String sortAttribute, String sortOrder, boolean includeFolders);

	/**
	 * Getter for the most recent contentVersion on a content
	 */
	
	public ContentVersionVO getContentVersion(Integer contentId);
	
	/**
	 * Getter for the most recent contentVersion on a content
	 */
	
	public ContentVersionVO getContentVersion(Integer contentId, Integer languageId, boolean useLanguageFallback);
	
    /**
	 * Finds a list of ContentVersionVOs that are related to the provided category under the given attribute name.
	 * @param categoryId The id of the Category
	 * @param attributeName The ContentTypeDefinition attribute name of the Category relationship.
	 * @return A list of relevant CategoryVersionVOs
	 */
	public abstract List getContentVersionsByCategory(Integer categoryId, String attributeName);

	/**
	 * This method searches for all contents matching
	 */
	
	public List getMatchingContents(String contentTypeDefinitionNamesString, String categoryConditionString, boolean useLanguageFallback);

	/**
	 * This method returns which mode the delivery-engine is running in.
	 * The mode is important to be able to show working, preview and published data separate.
	 */
	
	public Integer getOperatingMode();
	
    /**
     * The method returns the ContentTypeVO-objects of the given contentId. 
     */
    
	public abstract ContentTypeDefinitionVO getContentTypeDefinitionVO(Integer contentId);

	/**
	 * The method returns the ContentTypeVO-object with the given name. 
	 */
	
	public abstract ContentTypeDefinitionVO getContentTypeDefinitionVO(String name);

	/**
	 * This method deliveres a list of strings which represents all assetKeys defined for a contentTypeDefinition.
	 */
	 
	public Collection getContentTypeDefinitionAssetKeys(String schemaValue); 

    /**
     * The method returns a list of WebPage-objects that is the bound sitenodes of named binding. 
     * The method is great for navigation-purposes on any site. Improve later so the list is cached
     * once for every instance. Otherwise we fetch the whole list again and its not necessairy as
     * this controller only concerns one request.
     */
    public abstract WebPage getBoundPage(String structureBindningName,
            int position);

    /**
     * This method allows a user to get any string rendered as a template.
     */
    public abstract String renderString(String template);

    /**
     * This method allows a user to get any string rendered as a template.
     */
    public abstract String renderString(String template, boolean useSubContext);

    /**
     * This method allows the current template to include another template which is also rendered 
     * in the current context as if it were a part. The method assumes that the result can be cached.
     * Use the other include method if you wish to be able to control if the result is cached or not.
     */
    public abstract String include(String contentBindningName,
            String attributeName);

    public abstract String include(String contentBindningName,
            String attributeName, boolean cacheInclude);

    /**
     * This method allows the current template to include another template which is also rendered 
     * in the current context as if it were a part.
     * Use this method if you wish to be able to control if the result is cached or not.
     */
    public abstract String include(String contentBindningName,
            String attributeName, boolean cacheInclude, String cName,
            Object cObject);

    /**
     * This method fetches a given URL contents. This means that we can include a external url's contents
     * in our application.
     */
    public abstract String getUrlContent(String url);

    /**
     * This method fetches a given URL contents. This means that we can include a external url's contents
     * in our application. This second method is used to not send extra params through.
     */
    public abstract String getUrlContent(String url, boolean includeRequest);

    /**
     * This method fetches a given URL contents. This means that we can include a external url's contents
     * in our application.
     */
    public abstract String getUrlContent(String url, String encoding);

    /**
     * This method fetches a given URL contents. This means that we can include a external url's contents
     * in our application. This second method is used to not send extra params through.
     */
    public abstract String getUrlContent(String url, boolean includeRequest,
            String encoding);

    public abstract Object getObjectWithName(String classname);

    /**
     * This method lets a user substitute a string located in the page by a regular expression with another
     * string. Very useful in certain situations.
     */
    public abstract String replace(String originalString,
            String expressionToReplace, String newString);

    /**
     * This method lets a user substitute a string located in the page by a regular expression with another
     * string. This method also lets the user specify a subpart of the string to be able to be more
     * specific. Very useful in certain situations.
     */
    public abstract String replace(String originalString, String substring,
            String stringToReplace, String newString);

	/**
	 * This method helps us find out if the current site node is the same as the one sent in.
	 */
    public boolean getIsCurrentSiteNode(Integer siteNodeId);

    /**
     * This method helps us find out if the current site node is the same or a child to the sent in one.
     * So if the current page is a child(in the entire hierarchy below) below the siteNode sent in the 
     * method returns true. Useful for navigational purposes.  
     */
    public abstract boolean getIsParentToCurrent(Integer siteNodeId);

    /**
     * This method return true if a localized version with the current language exist
     */
    public abstract boolean getHasLocalizedVersion(Integer contentId);

    /**
     * This method return true if the user logged in has access to the siteNode sent in.
     */
    public abstract boolean getHasUserPageAccess(Integer siteNodeId);

    /**
     * This method return true if the user logged in has access to the siteNode sent in.
     */
    public abstract boolean getHasUserPageAccess(Integer siteNodeId, String interceptionPointName);

    /**
     * This method return true if the user logged in has access to the siteNode sent in.
     */
    public abstract boolean getHasUserPageWriteAccess(Integer siteNodeId);

    /**
     * This method returns a list of form elements/attributes based on the schema sent in. 
     * These consitutes the entire form and a template can then be used to render it in the appropriate technique.
     */
    public abstract List getFormAttributes(String contentBindningName,
            String attributeName);

    /**
     * This method returns a list of form elements/attributes based on the schema sent in. 
     * These consitutes the entire form and a template can then be used to render it in the appropriate technique.
     */
    public abstract List getFormAttributes(Integer contentId,
            String attributeName);

    /**
	 * This method supplies a method to get the locale of the language currently in use.
	 */
	public LanguageVO getLanguage(Integer languageId);
	
	/**
	 * This method supplies a method to get the locale of the language currently in use.
	 */
	public LanguageVO getLanguage(String languageCode);

    /**
     * This method supplies a method to get the locale of the language sent in.
     */
    public abstract Locale getLanguageCode(Integer languageId) throws SystemException;

    /**
     * This method supplies a method to get the locale of the language currently in use.
     */
    public abstract Locale getLocale() throws SystemException;

    /**
	 * This method sets a cookie.
	 * 
	 * @param cookieName
	 * @param value
	 * @param domain
	 * @param path
	 * @param maxAge
	 */

	public void setCookie(String cookieName, String value, String domain, String path, Integer maxAge);
	
    /**
	 * This method gets a cookie.
	 * 
	 * @param cookieName
	 */

	public String getCookie(String cookieName);

	
    public DeliveryContext getDeliveryContext(); 

    public void setDeliveryContext(DeliveryContext deliveryContext);
    
    /**
     * This method should be much more sophisticated later and include a check to see if there is a 
     * digital asset uploaded which is more specialized and can be used to act as serverside logic to the template.
     */
    public abstract TemplateController getTemplateController(
            Integer siteNodeId, Integer languageId, Integer contentId, InfoGluePrincipal infoGluePrincipal, DeliveryContext deliveryContext)
            throws SystemException, Exception;

    public abstract TemplateController getTemplateController(
            Integer siteNodeId, Integer languageId, Integer contentId,
            HttpServletRequest request, InfoGluePrincipal infoGluePrincipal, DeliveryContext deliveryContext) throws SystemException, Exception;

    public abstract String decoratePage(String page);

	/**
	 * This method adds the neccessairy html to a output for it to be editable.
	 */	

	public String getEditOnSightTag(Integer contentId, Integer languageId, String attributeName, String html, boolean showInPublishedMode);

	/**
	 * This method returns the neccessairy html to assign by klicking on a link.
	 * @param propertyName
	 * @param html
	 * @param showInPublishedMode
	 * @return
	 */
	public String getAssignPropertyBindingTag(String propertyName, boolean createNew, String html, boolean showInPublishedMode);

	
    public boolean getThreatFoldersAsContents();
    
    public void setThreatFoldersAsContents(boolean threatFoldersAsContents);
    
    /**
     * Renders a text from values configured in a content, iterates over the
     * contenttype defenition names and look for font properties.
     * @param contentId a content id containing attributes to match preferences 
     * of the textrenderer. ie fontsize, fontname.
     * @param text the text to render
     * @param renderAttributes render attributes in a map to override the
     *            content settings
     * @return the asseturl or empty string if something is wrong
     * @author Per Jonsson per.jonsson@it-huset.se
     */
    public String getRenderedTextUrl( Integer contentId, String text, Map renderAttributes );

    /**
     * Renders a text from configuration stored in the propertyfile or in the
     * map.
     * @param text the text to render
     * @param renderAttributes render attributes in a map to override the
     *            default or propertyfile settings
     * @return the asseturl or empty string if something is wrong
     * @author Per Jonsson - per.jonsson@it-huset.se
     */
    public String getRenderedTextUrl( String text, Map renderAttributes );
    
    /**
     * A method to check if the current pagenode is decorated with EditOnSight
     * or not. Checks if it's the BasicTemplateController or the
     * EditOnSiteBasicTemplateController which is used as a render.
     * 
     * @return true if the pagenode is rendered with EditOnSight decoration.
     */
    public boolean getIsDecorated();

}