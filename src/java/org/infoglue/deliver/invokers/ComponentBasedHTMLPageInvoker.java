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

package org.infoglue.deliver.invokers;

import org.infoglue.cms.util.*;
import org.infoglue.cms.util.dom.DOMBuilder;
import org.infoglue.cms.exception.*;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.deliver.applications.actions.InfoGlueComponent;
import org.infoglue.deliver.applications.databeans.Slot;
import org.infoglue.deliver.controllers.kernel.impl.simple.ComponentLogic;
import org.infoglue.deliver.controllers.kernel.impl.simple.ContentDeliveryController;
import org.infoglue.deliver.controllers.kernel.impl.simple.IntegrationDeliveryController;
import org.infoglue.deliver.controllers.kernel.impl.simple.LanguageDeliveryController;
import org.infoglue.deliver.controllers.kernel.impl.simple.NodeDeliveryController;
import org.infoglue.deliver.controllers.kernel.impl.simple.TemplateController;
import org.infoglue.deliver.util.CacheController;
import org.infoglue.deliver.util.VelocityTemplateProcessor;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.exolab.castor.jdo.Database;

/**
* @author Mattias Bogeblad
*
* This class delivers a normal html page by using the component-based method.
*/

public class ComponentBasedHTMLPageInvoker extends PageInvoker
{
   
   /**
	 * This method should return an instance of the class that should be used for page editing inside the tools or in working. 
	 * Makes it possible to have an alternative to the ordinary delivery optimized class.
	 */
	
   public PageInvoker getDecoratedPageInvoker() throws SystemException
	{
	    return new DecoratedComponentBasedHTMLPageInvoker();
	}

	/**
	 * This is the method that will render the page. It uses the new component based structure. 
	 */ 

	public void invokePage() throws SystemException, Exception
	{
		String pageContent = "";
		
		NodeDeliveryController nodeDeliveryController			    = NodeDeliveryController.getNodeDeliveryController(this.getDeliveryContext());
		IntegrationDeliveryController integrationDeliveryController = IntegrationDeliveryController.getIntegrationDeliveryController(this.getDeliveryContext());
		
		Integer repositoryId = nodeDeliveryController.getSiteNode(db, this.getDeliveryContext().getSiteNodeId()).getRepository().getId();

		String componentXML = getPageComponentsString(db, this.getTemplateController(), this.getDeliveryContext().getSiteNodeId(), this.getDeliveryContext().getLanguageId(), this.getDeliveryContext().getContentId());
		
		if(componentXML != null && componentXML.length() != 0)
		{
			Document document = new DOMBuilder().getDocument(componentXML);

			List pageComponents = getPageComponents(db, document.getRootElement(), "base", this.getTemplateController(), null);

			InfoGlueComponent baseComponent = null;
			if(pageComponents.size() > 0)
			{
				baseComponent = (InfoGlueComponent)pageComponents.get(0);
			}
			
			if(baseComponent != null)
			{
				ContentVO metaInfoContentVO = nodeDeliveryController.getBoundContent(db, this.getTemplateController().getPrincipal(), this.getDeliveryContext().getSiteNodeId(), this.getDeliveryContext().getLanguageId(), true, "Meta information");
				pageContent = renderComponent(db, baseComponent, this.getTemplateController(), repositoryId, this.getDeliveryContext().getSiteNodeId(), this.getDeliveryContext().getLanguageId(), this.getDeliveryContext().getContentId(), metaInfoContentVO.getId());
			}
		}

		Map context = getDefaultContext();
		StringWriter cacheString = new StringWriter();
		PrintWriter cachedStream = new PrintWriter(cacheString);
		new VelocityTemplateProcessor().renderTemplate(context, cachedStream, pageContent);
		
		String pageString = cacheString.toString();
			
		pageString = this.getTemplateController().decoratePage(pageString);
		
		this.setPageString(pageString);

	}
	
	
	/**
	 * This method fetches the pageComponent structure from the metainfo content.
	 */
	    
	protected String getPageComponentsString(Database db, TemplateController templateController, Integer siteNodeId, Integer languageId, Integer contentId) throws SystemException, Exception
	{ 
	    String cacheName 	= "componentEditorCache";
		String cacheKey		= "pageComponentString_" + siteNodeId + "_" + languageId + "_" + contentId;
		String cachedPageComponentsString = (String)CacheController.getCachedObject(cacheName, cacheKey);
		if(cachedPageComponentsString != null)
		{
		    return cachedPageComponentsString;
		}
		
		String pageComponentsString = null;
   	
		ContentVO contentVO = NodeDeliveryController.getNodeDeliveryController(siteNodeId, languageId, contentId).getBoundContent(db, templateController.getPrincipal(), siteNodeId, languageId, true, "Meta information");		
		
		if(contentVO == null)
			throw new SystemException("There was no Meta Information bound to this page which makes it impossible to render.");	
		
		//CmsLogger.logInfo("contentVO in getPageComponentsString: " + contentVO.getContentId());
		Integer masterLanguageId = LanguageDeliveryController.getLanguageDeliveryController().getMasterLanguageForSiteNode(this.db, siteNodeId).getId();
	    pageComponentsString = templateController.getContentAttribute(contentVO.getContentId(), masterLanguageId, "ComponentStructure", true);
		
		if(pageComponentsString == null)
			throw new SystemException("There was no Meta Information bound to this page which makes it impossible to render.");	
				    
		CmsLogger.logInfo("pageComponentsString: " + pageComponentsString);
	
		CacheController.cacheObject(cacheName, cacheKey, pageComponentsString);
		
		return pageComponentsString;
	}


	/**
	 * This method fetches the pageComponent structure as a document.
	 */
	    
	protected org.w3c.dom.Document getPageComponentsDocument(Database db, TemplateController templateController, Integer siteNodeId, Integer languageId, Integer contentId) throws SystemException, Exception
	{ 
		String cacheName 	= "componentEditorCache";
		String cacheKey		= "pageComponentDocument_" + siteNodeId + "_" + languageId + "_" + contentId;
		org.w3c.dom.Document cachedPageComponentsDocument = (org.w3c.dom.Document)CacheController.getCachedObject(cacheName, cacheKey);
		if(cachedPageComponentsDocument != null)
			return cachedPageComponentsDocument;
		
		org.w3c.dom.Document pageComponentsDocument = null;
   	
		try
		{
			String xml = this.getPageComponentsString(db, templateController, siteNodeId, languageId, contentId);
			pageComponentsDocument = XMLHelper.readDocumentFromByteArray(xml.getBytes("UTF-8"));
			
			CacheController.cacheObject(cacheName, cacheKey, pageComponentsDocument);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere(e.getMessage(), e);
			throw e;
		}
		
		return pageComponentsDocument;
	}

	
	/**
	 * This method gets a Map of the components available on the page.
	 */

	protected Map getComponents(Database db, Element element, TemplateController templateController, InfoGlueComponent parentComponent) throws Exception
	{
		InfoGlueComponent component = null;

		Locale locale = LanguageDeliveryController.getLanguageDeliveryController().getLocaleWithId(db, templateController.getLanguageId());
		
		Map components = new HashMap();
		
		String componentXPath = "component";
		List componentNodeList = element.selectNodes(componentXPath);
		Iterator componentNodeListIterator = componentNodeList.iterator();
		while(componentNodeListIterator.hasNext())
		{
			Element child 		= (Element)componentNodeListIterator.next();
			Integer id 			= new Integer(child.attributeValue("id"));
			Integer contentId 	= new Integer(child.attributeValue("contentId"));
			String name 	  	= child.attributeValue("name");
	
			component = new InfoGlueComponent();
			component.setId(id);
			component.setContentId(contentId);
			component.setName(name);
			component.setSlotName(name);
			component.setParentComponent(parentComponent);

			List propertiesNodeList = child.selectNodes("properties");
			//CmsLogger.logInfo("propertiesNodeList:" + propertiesNodeList.getLength());
			if(propertiesNodeList.size() > 0)
			{
				Element propertiesElement = (Element)propertiesNodeList.get(0);
				
				List propertyNodeList = propertiesElement.selectNodes("property");
				//CmsLogger.logInfo("propertyNodeList:" + propertyNodeList.getLength());
				Iterator propertyNodeListIterator = propertyNodeList.iterator();
				while(propertyNodeListIterator.hasNext())
				{
					Element propertyElement = (Element)propertyNodeListIterator.next();
					
					String propertyName = propertyElement.attributeValue("name");
					String type = propertyElement.attributeValue("type");
					String path = propertyElement.attributeValue("path");

					if(path == null)
					{
						LanguageVO langaugeVO = LanguageDeliveryController.getLanguageDeliveryController().getMasterLanguageForSiteNode(this.db, templateController.getSiteNodeId());
						if(propertyElement.attributeValue("path_" + langaugeVO.getLanguageCode()) != null)
							path = propertyElement.attributeValue("path_" + langaugeVO.getLanguageCode());
					}

					//CmsLogger.logInfo("path:" + "path_" + locale.getLanguage() + ":" + propertyElement.attributeValue("path_" + locale.getLanguage()));
					if(propertyElement.attributeValue("path_" + locale.getLanguage()) != null)
						path = propertyElement.attributeValue("path_" + locale.getLanguage());
					//CmsLogger.logInfo("path:" + path);

					Map property = new HashMap();
					property.put("name", propertyName);
					property.put("path", path);
					property.put("type", type);
					
					List bindings = new ArrayList();
					List bindingNodeList = propertyElement.selectNodes("binding");
					//CmsLogger.logInfo("bindingNodeList:" + bindingNodeList.getLength());
					Iterator bindingNodeListIterator = bindingNodeList.iterator();
					while(bindingNodeListIterator.hasNext())
					{
						Element bindingElement = (Element)bindingNodeListIterator.next();
						String entity = bindingElement.attributeValue("entity");
						String entityId = bindingElement.attributeValue("entityId");
						//CmsLogger.logInfo("Binding found:" + entity + ":" + entityId);
						if(entity.equalsIgnoreCase("Content"))
						{
							bindings.add(entityId);
						}
						else
						{
							bindings.add(entityId); 
						} 
					}
	
					property.put("bindings", bindings);
					
					component.getProperties().put(propertyName, property);
				}
			}
			
			
			//Getting slots for the component
			String componentString = this.getComponentString(templateController, contentId);
			//CmsLogger.logInfo("Getting the slots for component.......");
			//CmsLogger.logInfo("componentString:" + componentString);
			int offset = 0;
			int slotStartIndex = componentString.indexOf("<ig:slot", offset);
			while(slotStartIndex > -1)
			{
				int slotStopIndex = componentString.indexOf("</ig:slot>", slotStartIndex);
				String slotString = componentString.substring(slotStartIndex, slotStopIndex + 10);
				String slotId = slotString.substring(slotString.indexOf("id") + 4, slotString.indexOf("\"", slotString.indexOf("id") + 4));

			  	Slot slot = new Slot();
			  	slot.setId(slotId);

			  	List subComponents = getComponents(db, templateController, component, templateController.getSiteNodeId(), slotId);
			  	slot.setComponents(subComponents);

			  	component.getSlotList().add(slot);

			  	offset = slotStopIndex; // + 10;
				slotStartIndex = componentString.indexOf("<ig:slot", offset);
			}
			
			
			List anl = child.selectNodes("components");
			if(anl.size() > 0)
			{
				Element componentsElement = (Element)anl.get(0);
				component.setComponents(getComponents(db, componentsElement, templateController, component));
			}
			
			components.put(name, component);
		}
		
		
		return components;
	}

	/**
	 * This method gets a specific component.
	 */

	protected Map getComponent(Database db, Element element, String componentName, TemplateController templateController, InfoGlueComponent parentComponent) throws Exception
	{
		//System.out.println("Getting component with name:" + componentName);
		InfoGlueComponent component = null;

		Locale locale = LanguageDeliveryController.getLanguageDeliveryController().getLocaleWithId(db, templateController.getLanguageId());

		Map components = new HashMap();
		
		String componentXPath = getComponentXPath(parentComponent) + "/components/component[@name='" + componentName + "']";
		
		//System.out.println("componentXPath:" + componentXPath);
		List componentNodeList = element.selectNodes(componentXPath);
		Iterator componentNodeListIterator = componentNodeList.iterator();
		while(componentNodeListIterator.hasNext())
		{
			Element child 		= (Element)componentNodeListIterator.next();
			Integer id 			= new Integer(child.attributeValue("id"));
			Integer contentId 	= new Integer(child.attributeValue("contentId"));
			String name 	  	= child.attributeValue("name");
	
			component = new InfoGlueComponent();
			component.setId(id);
			component.setContentId(contentId);
			component.setName(name);
			component.setSlotName(name);
			component.setParentComponent(parentComponent);
			////CmsLogger.logInfo("Name:" + name);

			List propertiesNodeList = child.selectNodes("properties");
			////CmsLogger.logInfo("propertiesNodeList:" + propertiesNodeList.getLength());
			if(propertiesNodeList.size() > 0)
			{
				Element propertiesElement = (Element)propertiesNodeList.get(0);
				
				List propertyNodeList = propertiesElement.selectNodes("property");
				////CmsLogger.logInfo("propertyNodeList:" + propertyNodeList.getLength());
				Iterator propertyNodeListIterator = propertyNodeList.iterator();
				while(propertyNodeListIterator.hasNext())
				{
					Element propertyElement = (Element)propertyNodeListIterator.next();
					
					String propertyName = propertyElement.attributeValue("name");
					String type = propertyElement.attributeValue("type");
					String path = propertyElement.attributeValue("path");

					if(path == null)
					{
						LanguageVO langaugeVO = LanguageDeliveryController.getLanguageDeliveryController().getMasterLanguageForSiteNode(this.db, templateController.getSiteNodeId());
						if(propertyElement.attributeValue("path_" + langaugeVO.getLanguageCode()) != null)
							path = propertyElement.attributeValue("path_" + langaugeVO.getLanguageCode());
					}

					//CmsLogger.logInfo("path:" + "path_" + locale.getLanguage() + ":" + propertyElement.attributeValue("path_" + locale.getLanguage()));
					if(propertyElement.attributeValue("path_" + locale.getLanguage()) != null)
						path = propertyElement.attributeValue("path_" + locale.getLanguage());
					//CmsLogger.logInfo("path:" + path);

					Map property = new HashMap();
					property.put("name", propertyName);
					property.put("path", path);
					property.put("type", type);
					
					List bindings = new ArrayList();
					List bindingNodeList = propertyElement.selectNodes("binding");
					////CmsLogger.logInfo("bindingNodeList:" + bindingNodeList.getLength());
					Iterator bindingNodeListIterator = bindingNodeList.iterator();
					while(bindingNodeListIterator.hasNext())
					{
						Element bindingElement = (Element)bindingNodeListIterator.next();
						String entity = bindingElement.attributeValue("entity");
						String entityId = bindingElement.attributeValue("entityId");
						////CmsLogger.logInfo("Binding found:" + entity + ":" + entityId);
						if(entity.equalsIgnoreCase("Content"))
						{
							////CmsLogger.logInfo("Content added:" + entity + ":" + entityId);
							bindings.add(entityId);
						}
						else
						{
							////CmsLogger.logInfo("SiteNode added:" + entity + ":" + entityId);
							bindings.add(entityId); 
						} 
					}
	
					property.put("bindings", bindings);
					
					component.getProperties().put(propertyName, property);
				}
			}
			
			List anl = child.selectNodes("components");
			////CmsLogger.logInfo("Components NL:" + anl.getLength());
			if(anl.size() > 0)
			{
				Element componentsElement = (Element)anl.get(0);
				component.setComponents(getComponents(db, componentsElement, templateController, component));
			}
			
			List componentList = new ArrayList();
			if(components.containsKey(name))
				componentList = (List)components.get(name);
				
			componentList.add(component);
			
			components.put(name, componentList);
		}
		
		return components;
	}
	
	
	/**
	 * This method renders the base component and all it's children.
	 */

	private String renderComponent(Database db, InfoGlueComponent component, TemplateController templateController, Integer repositoryId, Integer siteNodeId, Integer languageId, Integer contentId, Integer metainfoContentId) throws Exception
	{
		String decoratedComponent = "";

		String componentEditorUrl = CmsPropertyHandler.getProperty("componentEditorUrl");
		
		try
		{
			String componentString = getComponentString(templateController, component.getContentId()); 
			
			templateController.setComponentLogic(new ComponentLogic(templateController, component));
			Map context = getDefaultContext();
			context.put("templateLogic", templateController);
			StringWriter cacheString = new StringWriter();
			PrintWriter cachedStream = new PrintWriter(cacheString);
			new VelocityTemplateProcessor().renderTemplate(context, cachedStream, componentString);
			componentString = cacheString.toString();
		
			int offset = 0;
			int slotStartIndex = componentString.indexOf("<ig:slot", offset);
			int slotStopIndex = 0;
			
			while(slotStartIndex > -1)
			{
				if(offset > 0)
					decoratedComponent += componentString.substring(offset + 10, slotStartIndex);
				else
					decoratedComponent += componentString.substring(offset, slotStartIndex);
				
				//decoratedComponent += componentString.substring(offset, slotStartIndex);
				slotStopIndex = componentString.indexOf("</ig:slot>", slotStartIndex);
				//CmsLogger.logInfo("slotStopIndex:" + slotStopIndex);
				
				String slot = componentString.substring(slotStartIndex, slotStopIndex + 10);
				String id = slot.substring(slot.indexOf("id") + 4, slot.indexOf("\"", slot.indexOf("id") + 4));
				//System.out.println("slot:" + slot);
				//System.out.println("id:" + id);
				
				List subComponents = getInheritedComponents(db, templateController, component, templateController.getSiteNodeId(), id);
				Iterator subComponentsIterator = subComponents.iterator();
				while(subComponentsIterator.hasNext())
				{
					InfoGlueComponent subComponent = (InfoGlueComponent)subComponentsIterator.next();
					String subComponentString = "";
					if(subComponent != null)
					{
						subComponentString = renderComponent(db, subComponent, templateController, repositoryId, siteNodeId, languageId, contentId, metainfoContentId);
					}
					decoratedComponent += subComponentString.trim();	
					//CmsLogger.logInfo("subComponentString:" + subComponentString);
				}
				
				//CmsLogger.logInfo("slotStopIndex in loop:" + slotStopIndex);
				offset = slotStopIndex;
				//CmsLogger.logInfo("offset in loop:" + offset);
				//CmsLogger.logInfo("Left: " + componentString.substring(offset));
				slotStartIndex = componentString.indexOf("<ig:slot", offset);
			}
			
			
			//CmsLogger.logInfo("offset:" + offset);
			if(offset > 0)
			{	
				//CmsLogger.logInfo("APA:" + componentString.substring(offset + 10));
				decoratedComponent += componentString.substring(offset + 10);
			}
			else
			{	
				//CmsLogger.logInfo("BEPA:" + componentString.substring(offset));
				decoratedComponent += componentString.substring(offset);
			}
		}
		catch(Exception e)
		{		
			CmsLogger.logWarning("An component with either an empty template or with no template in the sitelanguages was found:" + e.getMessage(), e);	
		}    	
		
		//CmsLogger.logInfo("decoratedComponent:" + decoratedComponent);
		
		return decoratedComponent;
	}


	/**
	 * This method fetches the component template as a string.
	 */
   
	protected String getComponentString(TemplateController templateController, Integer contentId) throws SystemException, Exception
	{
		String template = null;
   	
		try
		{
			template = templateController.getContentAttribute(contentId, templateController.getTemplateAttributeName(), true);
			
			if(template == null)
				throw new SystemException("There was no template available on the content with id " + contentId + ". Check so that the templates language are active on your site.");	
		}
		catch(Exception e)
		{
			CmsLogger.logSevere(e.getMessage(), e);
			throw e;
		}

		return template;
	}
	
	/**
	 * This method fetches a subcomponent from either the current page or from a parent node if it's not defined.
	 */
   
	protected List getInheritedComponents(Database db, TemplateController templateController, InfoGlueComponent component, Integer siteNodeId, String id) throws Exception
	{
	    //CmsLogger.logInfo("slotId");
	    //CmsLogger.logInfo("getInheritedComponents with " + component.getName() + ":" + component.getSlotName() + ":" + component.getId());
		
		List inheritedComponents = new ArrayList();
		
		NodeDeliveryController nodeDeliveryController = NodeDeliveryController.getNodeDeliveryController(templateController.getSiteNodeId(), templateController.getLanguageId(), templateController.getContentId());
		
		//CmsLogger.logInfo("AAAAAAAAAAAAAA " + component.getSlotList().size());
			
		Iterator slotIterator = component.getSlotList().iterator();
		while(slotIterator.hasNext())
		{
			Slot slot = (Slot)slotIterator.next();
			//CmsLogger.logInfo("Slot for component " + component.getName() + ":" + slot.getId());
			//CmsLogger.logInfo("Slot for component " + id + ":" + slot.getId() + ":" + slot.getName());
			if(slot.getId().equalsIgnoreCase(id))
			{
				Iterator subComponentIterator = slot.getComponents().iterator();
				while(subComponentIterator.hasNext())
				{
					InfoGlueComponent infoGlueComponent = (InfoGlueComponent)subComponentIterator.next();
					//CmsLogger.logInfo("Adding not inherited component " + infoGlueComponent.getName() + " to list...");
					inheritedComponents.add(infoGlueComponent);
				}
			}
		}
		
		SiteNodeVO parentSiteNodeVO = nodeDeliveryController.getParentSiteNode(db, siteNodeId);
		
		//System.out.println("inheritedComponents:" + inheritedComponents);
		//System.out.println("parentSiteNodeVO:" + parentSiteNodeVO);
		while(inheritedComponents.size() == 0 && parentSiteNodeVO != null)
		{
		    //System.out.println("*********************************************");
		    //System.out.println("*         INHERITING COMPONENTS             *");
		    //System.out.println("*********************************************");
			String componentXML = this.getPageComponentsString(db, templateController, parentSiteNodeVO.getId(), templateController.getLanguageId(), component.getContentId());
			//System.out.println("componentXML:" + componentXML);
			//System.out.println("id:" + id);
		
			Document document = new DOMBuilder().getDocument(componentXML);
						
			Map components = getComponent(db, document.getRootElement(), id, templateController, component);
			System.out.println("components:" + components.size());
			
			if(components.containsKey(id))
			{
				inheritedComponents = (List)components.get(id);
				Iterator inheritedComponentIterator = inheritedComponents.iterator();
				while(inheritedComponentIterator.hasNext())
				{
					InfoGlueComponent infoGlueComponent = (InfoGlueComponent)inheritedComponentIterator.next();
				    infoGlueComponent.setIsInherited(true);
				}
			}
						
			parentSiteNodeVO = nodeDeliveryController.getParentSiteNode(db, parentSiteNodeVO.getId());
		}
			
		//CmsLogger.logInfo("*************************STOP**********************");
   	
		return inheritedComponents;
	}
	
	/**
	 * This method returns a path to the component so one does not mix them up.
	 */
	
	private String getComponentXPath(InfoGlueComponent infoGlueComponent)
	{	    
	    String path = "";
	    String parentPath = "";
	    
	    InfoGlueComponent parentInfoGlueComponent = infoGlueComponent.getParentComponent();
	    //CmsLogger.logInfo("infoGlueComponent.getParentComponent():" + parentInfoGlueComponent);
	    if(parentInfoGlueComponent != null && parentInfoGlueComponent.getId().intValue() != infoGlueComponent.getId().intValue())
	    {
	        //CmsLogger.logInfo("Had parent component...:" + parentInfoGlueComponent.getId() + ":" + parentInfoGlueComponent.getName());
	        parentPath = getComponentXPath(parentInfoGlueComponent);
	        //CmsLogger.logInfo("parentPath:" + parentPath);
	    }
	    
	    //System.out.println("infoGlueComponent:" + infoGlueComponent.getSlotName());
	    path = parentPath + "/components/component[@name='" + infoGlueComponent.getSlotName() + "']";
	    //CmsLogger.logInfo("returning path:" + path);
	    
	    return path;
	}
	
	/**
	 * This method fetches a subcomponent from either the current page or from a parent node if it's not defined.
	 */
   
	protected InfoGlueComponent getComponent(Database db, TemplateController templateController, InfoGlueComponent component, Integer siteNodeId, String id) throws Exception
	{
		//CmsLogger.logInfo("Inside getComponent");
		//CmsLogger.logInfo("component:" + component.getName());
		//CmsLogger.logInfo("siteNodeId:" + siteNodeId);
		//CmsLogger.logInfo("id:" + id);
		
		NodeDeliveryController nodeDeliveryController = NodeDeliveryController.getNodeDeliveryController(templateController.getSiteNodeId(), templateController.getLanguageId(), templateController.getContentId());

		String componentXML = this.getPageComponentsString(db, templateController, siteNodeId, templateController.getLanguageId(), component.getContentId());
		//CmsLogger.logInfo("componentXML:" + componentXML);

		Document document = new DOMBuilder().getDocument(componentXML);
			
		Map components = getComponent(db, document.getRootElement(), id, templateController, component);
		
		InfoGlueComponent infoGlueComponent = (InfoGlueComponent)components.get(id);
		//CmsLogger.logInfo("infoGlueComponent:" + infoGlueComponent);
					
		SiteNodeVO parentSiteNodeVO = nodeDeliveryController.getParentSiteNode(db, siteNodeId);
		//CmsLogger.logInfo("parentSiteNodeVO:" + parentSiteNodeVO);

		while(infoGlueComponent == null && parentSiteNodeVO != null)
		{
			componentXML = this.getPageComponentsString(db, templateController, parentSiteNodeVO.getId(), templateController.getLanguageId(), component.getContentId());
			//CmsLogger.logInfo("componentXML:" + componentXML);
		
			document = new DOMBuilder().getDocument(componentXML);
						
			components = getComponent(db, document.getRootElement(), id, templateController, component);
			
			infoGlueComponent = (InfoGlueComponent)components.get(id);
			//CmsLogger.logInfo("infoGlueComponent:" + infoGlueComponent);
			if(infoGlueComponent != null)
				infoGlueComponent.setIsInherited(true);
			
			parentSiteNodeVO = nodeDeliveryController.getParentSiteNode(db, parentSiteNodeVO.getId());
			//CmsLogger.logInfo("parentSiteNodeVO:" + parentSiteNodeVO);	
		}
			
		//CmsLogger.logInfo("*************************STOP**********************");
   	
		return infoGlueComponent;
	}


	/**
	 * This method fetches a subcomponent from either the current page or from a parent node if it's not defined.
	 */
   
	protected List getComponents(Database db, TemplateController templateController, InfoGlueComponent component, Integer siteNodeId, String id) throws Exception
	{
		//CmsLogger.logInfo("Inside getComponents");
		//CmsLogger.logInfo("component:" + component.getName());
		//CmsLogger.logInfo("siteNodeId:" + siteNodeId);
		//CmsLogger.logInfo("id:" + id);
		
		List subComponents = new ArrayList();

		try
		{
		
		NodeDeliveryController nodeDeliveryController = NodeDeliveryController.getNodeDeliveryController(templateController.getSiteNodeId(), templateController.getLanguageId(), templateController.getContentId());

		String componentStructureXML = this.getPageComponentsString(db, templateController, siteNodeId, templateController.getLanguageId(), component.getContentId());
		//CmsLogger.logInfo("componentStructureXML:" + componentStructureXML);

		Document document = new DOMBuilder().getDocument(componentStructureXML);
			
		Map components = getComponent(db, document.getRootElement(), id, templateController, component);
		
		if(components.containsKey(id))
			subComponents = (List)components.get(id);
		
		SiteNodeVO parentSiteNodeVO = nodeDeliveryController.getParentSiteNode(db, siteNodeId);
		//CmsLogger.logInfo("parentSiteNodeVO:" + parentSiteNodeVO);

		while((subComponents == null || subComponents.size() == 0) && parentSiteNodeVO != null)
		{
			//CmsLogger.logInfo("parentSiteNodeVO:" + parentSiteNodeVO);
			//CmsLogger.logInfo("component:" + component);
			componentStructureXML = this.getPageComponentsString(db, templateController, parentSiteNodeVO.getId(), templateController.getLanguageId(), component.getContentId());
			//CmsLogger.logInfo("componentStructureXML:" + componentStructureXML);
		
			document = new DOMBuilder().getDocument(componentStructureXML);
						
			components = getComponent(db, document.getRootElement(), id, templateController, component);
			
			if(components.containsKey(id))
				subComponents = (List)components.get(id);
				
			if(subComponents != null)
			{
				//CmsLogger.logInfo("infoGlueComponent:" + infoGlueComponent);
				Iterator inheritedComponentsIterator = subComponents.iterator();
				while(inheritedComponentsIterator.hasNext())
				{
					InfoGlueComponent infoGlueComponent = (InfoGlueComponent)inheritedComponentsIterator.next();
					infoGlueComponent.setIsInherited(true);
				}
			}
			
			parentSiteNodeVO = nodeDeliveryController.getParentSiteNode(db, parentSiteNodeVO.getId());
			//CmsLogger.logInfo("parentSiteNodeVO:" + parentSiteNodeVO);	
		}
			
		//CmsLogger.logInfo("*************************STOP**********************");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw e;
		}
		
		return subComponents;
	}


	/**
	 * This method gets the component structure on the page.
	 *
	 * @author mattias
	 */

	protected List getPageComponents(Database db, Element element, String slotName, TemplateController templateController, InfoGlueComponent parentComponent) throws Exception
	{
		List components = new ArrayList();
		
		Locale locale = LanguageDeliveryController.getLanguageDeliveryController().getLocaleWithId(db, templateController.getLanguageId());

		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter( System.out, format );
		//writer.write( element );
						
		String componentXPath = "component[@name='" + slotName + "']";
		List componentElements = element.selectNodes(componentXPath);
		//CmsLogger.logInfo("componentElements:" + componentElements.size());
		Iterator componentIterator = componentElements.iterator();
		while(componentIterator.hasNext())
		{
			Element componentElement = (Element)componentIterator.next();
		
			Integer id 			= new Integer(componentElement.attributeValue("id"));
			Integer contentId 	= new Integer(componentElement.attributeValue("contentId"));
			String name 	  	= componentElement.attributeValue("name");
			
			try
			{
			    ContentVO contentVO = ContentDeliveryController.getContentDeliveryController().getContentVO(contentId, db);
			
				InfoGlueComponent component = new InfoGlueComponent();
				component.setId(id);
				component.setContentId(contentId);
				component.setName(contentVO.getName());
				component.setSlotName(name);
				component.setParentComponent(parentComponent);
		
				List propertiesNodeList = componentElement.selectNodes("properties");
				if(propertiesNodeList.size() > 0)
				{
					Element propertiesElement = (Element)propertiesNodeList.get(0);
					
					List propertyNodeList = propertiesElement.selectNodes("property");
					Iterator propertyNodeListIterator = propertyNodeList.iterator();
					while(propertyNodeListIterator.hasNext())
					{
						Element propertyElement = (Element)propertyNodeListIterator.next();
						
						String propertyName = propertyElement.attributeValue("name");
						String type = propertyElement.attributeValue("type");
						String path = propertyElement.attributeValue("path");
		
						if(path == null)
						{
							LanguageVO langaugeVO = LanguageDeliveryController.getLanguageDeliveryController().getMasterLanguageForSiteNode(this.db, templateController.getSiteNodeId());
							if(propertyElement.attributeValue("path_" + langaugeVO.getLanguageCode()) != null)
								path = propertyElement.attributeValue("path_" + langaugeVO.getLanguageCode());
						}
							
						if(propertyElement.attributeValue("path_" + locale.getLanguage()) != null)
							path = propertyElement.attributeValue("path_" + locale.getLanguage());
				
						Map property = new HashMap();
						property.put("name", propertyName);
						property.put("path", path);
						property.put("type", type);
						
						List bindings = new ArrayList();
						List bindingNodeList = propertyElement.selectNodes("binding");
						Iterator bindingNodeListIterator = bindingNodeList.iterator();
						while(bindingNodeListIterator.hasNext())
						{
							Element bindingElement = (Element)bindingNodeListIterator.next();
							String entity = bindingElement.attributeValue("entity");
							String entityId = bindingElement.attributeValue("entityId");
							if(entity.equalsIgnoreCase("Content"))
							{
								bindings.add(entityId);
							}
							else
							{
								bindings.add(entityId); 
							} 
						}
		
						property.put("bindings", bindings);
						
						component.getProperties().put(propertyName, property);
					}
				}
				
				//Getting slots for the component
				try
				{
					String componentString = this.getComponentString(templateController, contentId);
					int offset = 0;
					int slotStartIndex = componentString.indexOf("<ig:slot", offset);
					while(slotStartIndex > -1)
					{
						int slotStopIndex = componentString.indexOf("</ig:slot>", slotStartIndex);
						String slotString = componentString.substring(slotStartIndex, slotStopIndex + 10);
						String slotId = slotString.substring(slotString.indexOf("id") + 4, slotString.indexOf("\"", slotString.indexOf("id") + 4));
			
						Slot slot = new Slot();
						slot.setId(slotId);
						
						Element componentsElement = (Element)componentElement.selectSingleNode("components");
						
						List subComponents = getPageComponents(db, componentsElement, slotId, templateController, component);
						System.out.println("subComponents:" + subComponents);
						slot.setComponents(subComponents);
						
						component.getSlotList().add(slot);
				
						offset = slotStopIndex;
						slotStartIndex = componentString.indexOf("<ig:slot", offset);
					}
				}
				catch(Exception e)
				{		
					CmsLogger.logWarning("An component with either an empty template or with no template in the sitelanguages was found:" + e.getMessage(), e);	
				}
				
				components.add(component);
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			}
			

		}		
		
		return components;
	}

	
	
}
