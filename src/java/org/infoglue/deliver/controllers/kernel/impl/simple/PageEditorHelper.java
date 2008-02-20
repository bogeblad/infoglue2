package org.infoglue.deliver.controllers.kernel.impl.simple;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.controllers.kernel.impl.simple.AccessRightController;
import org.infoglue.cms.controllers.kernel.impl.simple.ComponentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.content.DigitalAssetVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.StringManager;
import org.infoglue.cms.util.StringManagerFactory;
import org.infoglue.cms.util.dom.DOMBuilder;
import org.infoglue.deliver.applications.actions.InfoGlueComponent;
import org.infoglue.deliver.applications.databeans.ComponentBinding;
import org.infoglue.deliver.applications.databeans.ComponentProperty;
import org.infoglue.deliver.applications.databeans.ComponentPropertyOption;
import org.infoglue.deliver.applications.databeans.Slot;
import org.infoglue.deliver.invokers.DecoratedComponentBasedHTMLPageInvoker;
import org.infoglue.deliver.util.CacheController;
import org.infoglue.deliver.util.Timer;
import org.infoglue.deliver.util.VelocityTemplateProcessor;

public class PageEditorHelper
{
	private final static DOMBuilder domBuilder = new DOMBuilder();
	private final static VisualFormatter formatter = new VisualFormatter();
	//protected NodeDeliveryController nodeDeliveryController = NodeDeliveryController.getNodeDeliveryController();

    private final static Logger logger = Logger.getLogger(PageEditorHelper.class.getName());


	public String getComponentPropertiesDiv(Database db, 
											 InfoGluePrincipal principal, 
											 HttpServletRequest request, 
											 Locale locale,
											 /*TemplateController templateController, */
											 Integer repositoryId, 
											 Integer siteNodeId, 
											 Integer languageId, 
											 Integer contentId, 
											 Integer componentId, 
											 Integer componentContentId, 
											 String slotName, 
											 String showSimple, 
											 String originalFullURL,
											 String showLegend,
											 String targetDiv) throws Exception
	{	
	    if(request.getParameter("skipPropertiesDiv") != null && request.getParameter("skipPropertiesDiv").equalsIgnoreCase("true"))
	        return "";

	    StringBuffer sb = new StringBuffer();
	    
		String componentEditorUrl = CmsPropertyHandler.getComponentEditorUrl();

		Document document = getComponentPropertiesDOM4JDocument(siteNodeId, languageId, componentContentId, db, principal); 

        ContentVO contentVO = ContentController.getContentController().getContentVOWithId(componentContentId, db);

		String componentName = contentVO.getName();
		if(componentName.length() > 20) 
			componentName = componentName.substring(0, 20) + "...";
		
		if(slotName.length() > 10) 
			slotName = slotName.substring(0, 10) + "...";

		sb.append("<div id=\"component" + componentId + "Properties\" class=\"componentProperties\">");
		sb.append("	<div id=\"component" + componentId + "PropertiesHandle\" class=\"componentPropertiesHandle\"><div id=\"leftPaletteHandleCompProps\">Properties - " + componentName + " - " + slotName + "</div><div id=\"rightPaletteHandle\"><a href=\"javascript:closeDiv('componentProperties');\" class=\"white\"><img src=\"" + componentEditorUrl + "/images/closeIcon.gif\" border=\"0\"/></a></div></div>");
		//sb.append("	<div id=\"component" + componentId + "PropertiesBody\" class=\"componentPropertiesBody\">");
		sb.append("	<div id=\"component" + componentId + "PropertiesBody\" class=\"componentPropertiesBody\">");
		
		sb.append("	<form id=\"componentPropertiesForm\" name=\"component" + componentId + "PropertiesForm\" action=\"" + componentEditorUrl + "ViewSiteNodePageComponents!updateComponentProperties.action\" method=\"POST\">");
		if(showLegend != null && showLegend.equals("true"))
		{
			sb.append("		<fieldset>");
			sb.append("		<legend>Component properties</legend>");
		}
		else
		{
			sb.append("		<fieldset style=\"border: 0px;\">");
		}
		
		sb.append("		<div class=\"propertyRow\">");

		sb.append("		<label for=\"languageId\">Language</label>");
		sb.append("		<select name=\"languageId\" onChange=\"javascript:changeLanguage(" + siteNodeId + ", this, " + contentId + ");\">");
		
		List languages = LanguageDeliveryController.getLanguageDeliveryController().getLanguagesForSiteNode(db, siteNodeId, principal);
			
		Iterator languageIterator = languages.iterator();
		int index = 0;
		int languageIndex = index;
		while(languageIterator.hasNext())
		{
			LanguageVO languageVO = (LanguageVO)languageIterator.next();
			if(languageVO.getLanguageId().intValue() == languageId.intValue())
			{
				sb.append("					<option value=\"" + languageVO.getLanguageId() + "\" selected>" + languageVO.getName() + "</option>");
				languageIndex = index;
			}
			else
			{
				sb.append("					<option value=\"" + languageVO.getLanguageId() + "\">" + languageVO.getName() + "</option>");
			}
			index++;
		}

		sb.append("		</select>");
		sb.append("		</div>");
		
		Collection componentProperties = getComponentProperties(componentId, document, siteNodeId, languageId, contentId, locale, db, principal);
		
		int propertyIndex = 0;
		Iterator componentPropertiesIterator = componentProperties.iterator();
		while(componentPropertiesIterator.hasNext())
		{
			ComponentProperty componentProperty = (ComponentProperty)componentPropertiesIterator.next();
			
			boolean hasAccessToProperty = AccessRightController.getController().getIsPrincipalAuthorized(db, principal, "ComponentPropertyEditor.EditProperty", "" + componentContentId + "_" + componentProperty.getName());
			
			if(componentProperty.getType().equalsIgnoreCase(ComponentProperty.BINDING))
			{
				String assignUrl = "";
				String createUrl = "";
				 
				if(componentProperty.getVisualizingAction() != null && !componentProperty.getVisualizingAction().equals(""))
				{
					assignUrl = componentEditorUrl + componentProperty.getVisualizingAction() + "?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple;
				}
				else
				{	
					if(componentProperty.getEntityClass().equalsIgnoreCase("Content"))
					{
					    String allowedContentTypeIdParameters = "";

					    if(componentProperty.getAllowedContentTypeNamesArray() != null && componentProperty.getAllowedContentTypeNamesArray().length > 0)
					    {
					        allowedContentTypeIdParameters = "&" + componentProperty.getAllowedContentTypeIdAsUrlEncodedString(db);
					    }
					    
						if(componentProperty.getIsMultipleBinding())
						{
							if(componentProperty.getIsAssetBinding())
								assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showContentTreeForMultipleAssetBinding.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + allowedContentTypeIdParameters + "&showSimple=" + showSimple;
							else
								assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showContentTreeForMultipleBinding.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + allowedContentTypeIdParameters + "&showSimple=" + showSimple;
						}
						else
						{
							if(componentProperty.getIsAssetBinding())
							{
								String assignedParameters = "";
								Iterator<ComponentBinding> bindingsIterator = componentProperty.getBindings().iterator();
								while(bindingsIterator.hasNext())
								{
									ComponentBinding componentBinding = bindingsIterator.next();
									assignedParameters = "&assignedContentId=" + componentBinding.getEntityId() + "&assignedAssetKey=" + componentBinding.getAssetKey() + "&assignedPath=" + formatter.encodeURI(componentProperty.getValue());
								}
								
								assignUrl = componentEditorUrl + "ViewContentVersion!viewAssetsForComponentBinding.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + allowedContentTypeIdParameters + "&showSimple=" + showSimple + assignedParameters;
							}
							else
								assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showContentTree.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + allowedContentTypeIdParameters + "&showSimple=" + showSimple;
						}
					}
					else if(componentProperty.getEntityClass().equalsIgnoreCase("SiteNode"))
					{
						if(componentProperty.getIsMultipleBinding())
							assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showStructureTreeForMultipleBinding.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple;
						else
							assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showStructureTree.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple;
					}
					else if(componentProperty.getEntityClass().equalsIgnoreCase("Category"))
					{
						if(componentProperty.getIsMultipleBinding())
							assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showCategoryTreeForMultipleBinding.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple;
						else
							assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showCategoryTree.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple;
					}
				}
					
				if(componentProperty.getCreateAction() != null && !componentProperty.getCreateAction().equals(""))
				{
					createUrl = componentEditorUrl + componentProperty.getCreateAction() + "?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple;
				}
				else
				{	
					if(componentProperty.getVisualizingAction() != null && !componentProperty.getVisualizingAction().equals(""))
					{
						createUrl = assignUrl;
					}
					else if(componentProperty.getEntityClass().equalsIgnoreCase("Content"))
					{
					    String allowedContentTypeIdParameters = "";

					    if(componentProperty.getAllowedContentTypeNamesArray() != null && componentProperty.getAllowedContentTypeNamesArray().length > 0)
					    {
					        allowedContentTypeIdParameters = "&" + componentProperty.getAllowedContentTypeIdAsUrlEncodedString(db);
					    }

					    String returnAddress = URLEncoder.encode("ViewSiteNodePageComponents!addComponentPropertyBinding.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=-1&entity=Content&entityId=#entityId&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&path=#path&showSimple=" + showSimple + "", "UTF-8");
						
				        String cancelKey = originalFullURL;
				        String cancelAddress = (String)CacheController.getCachedObjectFromAdvancedCache("encodedStringsCache", cancelKey);
				        if(cancelAddress == null)
				        {
				        	cancelAddress = URLEncoder.encode(cancelKey, "UTF-8");
				        	CacheController.cacheObjectInAdvancedCache("encodedStringsCache", cancelKey, cancelAddress);
				        }

						if(componentProperty.getIsMultipleBinding())
							createUrl = componentEditorUrl + "CreateContentWizardFinish.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + allowedContentTypeIdParameters + "&refreshAddress=" + returnAddress + "&cancelAddress=" + cancelAddress + "&showSimple=" + showSimple;
						else
							createUrl = componentEditorUrl + "CreateContentWizardFinish.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + allowedContentTypeIdParameters + "&refreshAddress=" + returnAddress + "&cancelAddress=" + cancelAddress + "&showSimple=" + showSimple;
					}
					else if(componentProperty.getEntityClass().equalsIgnoreCase("SiteNode"))
					{
					    String returnAddress = URLEncoder.encode("ViewSiteNodePageComponents!addComponentPropertyBinding.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=-1&entity=Content&entityId=#entityId&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&path=#path&showSimple=" + showSimple + "", "UTF-8");
						
				        String cancelKey = originalFullURL;
				        String cancelAddress = (String)CacheController.getCachedObjectFromAdvancedCache("encodedStringsCache", cancelKey);
				        if(cancelAddress == null)
				        {
				        	cancelAddress = URLEncoder.encode(cancelKey, "UTF-8");
				        	CacheController.cacheObjectInAdvancedCache("encodedStringsCache", cancelKey, cancelAddress);
				        }

						if(componentProperty.getIsMultipleBinding())
							createUrl = componentEditorUrl + "CreateSiteNodeWizardFinish.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&refreshAddress=" + returnAddress + "&cancelAddress=" + cancelAddress + "&showSimple=" + showSimple;
						else
							createUrl = componentEditorUrl + "CreateSiteNodeWizardFinish.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&refreshAddress=" + returnAddress + "&cancelAddress=" + cancelAddress + "&showSimple=" + showSimple;
					}
				}
								
				sb.append("		<div class=\"propertyRow\">");
				sb.append("			<label for=\"\">" + componentProperty.getName() + "</label>");
				//sb.append("			<td class=\"igtd\" width=\"16\"><img src=\"" + componentEditorUrl + "/images/questionMarkGrad.gif\" onMouseOver=\"javascript:showDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\" onMouseOut=\"javascript:hideDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\">" + helpSB + "</td>");
				
				if(hasAccessToProperty)
				{
					String warningText = getLocalizedString(locale, "deliver.editOnSight.dirtyWarning");
					sb.append("<a class=\"componentEditorLink\" href=\"javascript:if(checkDirty('" + warningText + "')){window.open('" + assignUrl + "','Assign','toolbar=no,status=yes,scrollbars=yes,location=no,menubar=no,directories=no,resizable=no,width=300,height=600,left=5,top=5')};\">");
				}

				sb.append("" + (componentProperty.getValue() == null || componentProperty.getValue().equalsIgnoreCase("") ? "Undefined" : componentProperty.getValue()) + (componentProperty.getIsAssetBinding() ? " (" + componentProperty.getAssetKey() + ")" : ""));
				
				if(hasAccessToProperty)
					sb.append("</a>");
				
				//sb.append("</td>");
				
				if(componentProperty.getValue() != null && componentProperty.getValue().equalsIgnoreCase("Undefined"))
				{	
					if(hasAccessToProperty && createUrl != null)
						sb.append("			<a class=\"componentEditorLink\" href=\"" + createUrl + "\"><img src=\"" + componentEditorUrl + "/images/createContent.gif\" border=\"0\" alt=\"Create new content to show\"></a>");
				}
				else
				{
					if(hasAccessToProperty)
						sb.append("			<a class=\"componentEditorLink\" href=\"" + componentEditorUrl + "ViewSiteNodePageComponents!deleteComponentPropertyValue.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple + "\"><img src=\"" + componentEditorUrl + "/images/delete.gif\" border=\"0\"/></a>");
				}
				
				sb.append("		</div>");
			}
			else if(componentProperty.getType().equalsIgnoreCase(ComponentProperty.TEXTFIELD))
			{
				StringBuffer helpSB = new StringBuffer();
				helpSB.append("<div class=\"tooltipDiv\" id=\"helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "\">");
				helpSB.append("" + (componentProperty.getDescription() == null || componentProperty.getDescription().equalsIgnoreCase("") ? "No description" : componentProperty.getDescription()) + "");
				helpSB.append("</div>");

				sb.append("	<div class=\"propertyRow\">");
				sb.append("		<label for=\"" + componentProperty.getName() + "\" onMouseOver=\"showDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\" onMouseOut=\"javascript:hideDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\">" + componentProperty.getName() + "</label>");
				
				if(hasAccessToProperty)
				{
					sb.append("	<input type=\"hidden\" name=\"" + propertyIndex + "_propertyName\" value=\"" + componentProperty.getName() + "\"/>");
					sb.append("	<input type=\"text\" class=\"propertytextfield\" name=\"" + componentProperty.getName() + "\" value=\"" + componentProperty.getValue() + "\" onkeydown=\"setDirty();\"/>");
				}
				else
					sb.append("	" + componentProperty.getValue() + "");
	
				if(hasAccessToProperty)
					sb.append("	<a class=\"componentEditorLink\" href=\"" + componentEditorUrl + "ViewSiteNodePageComponents!deleteComponentPropertyValue.action?siteNodeId=" + siteNodeId + "&amp;languageId=" + languageId + "&amp;contentId=" + contentId + "&amp;componentId=" + componentId + "&amp;propertyName=" + componentProperty.getName() + "&amp;showSimple=" + showSimple + "\"><img src=\"" + componentEditorUrl + "/images/delete.gif\" border=\"0\"/></a>");

				sb.append("	</div>");
				sb.append("	" + helpSB + "");

				if(hasAccessToProperty)
				    propertyIndex++;
			}
			else if(componentProperty.getType().equalsIgnoreCase(ComponentProperty.TEXTAREA))
			{
				StringBuffer helpSB = new StringBuffer();
				helpSB.append("<div class=\"tooltipDiv\" id=\"helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "\">");
				helpSB.append("" + (componentProperty.getDescription() == null || componentProperty.getDescription().equalsIgnoreCase("") ? "No description" : componentProperty.getDescription()) + "");
				helpSB.append("</div>");

				sb.append("	<div class=\"propertyRow\">");
				sb.append("		<label for=\"" + componentProperty.getName() + "\" onMouseOver=\"showDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\" onMouseOut=\"javascript:hideDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\">" + componentProperty.getName() + "</label>");
				
				if(hasAccessToProperty)
				{
					sb.append("	<input type=\"hidden\" name=\"" + propertyIndex + "_propertyName\" value=\"" + componentProperty.getName() + "\"/>");
					sb.append("	<textarea class=\"propertytextarea\" name=\"" + componentProperty.getName() + "\" onkeydown=\"setDirty();\">" + (componentProperty.getValue() == null ? "" : componentProperty.getValue()) + "</textarea>");
				}
				else
					sb.append("	" + componentProperty.getValue() + "");
	
				if(hasAccessToProperty)
					sb.append("	<a class=\"componentEditorLink\" href=\"" + componentEditorUrl + "ViewSiteNodePageComponents!deleteComponentPropertyValue.action?siteNodeId=" + siteNodeId + "&amp;languageId=" + languageId + "&amp;contentId=" + contentId + "&amp;componentId=" + componentId + "&amp;propertyName=" + componentProperty.getName() + "&amp;showSimple=" + showSimple + "\"><img src=\"" + componentEditorUrl + "/images/delete.gif\" border=\"0\"/></a>");
				
				sb.append("	</div>");
				sb.append("	" + helpSB + "");
				
				if(hasAccessToProperty)
				    propertyIndex++;
			}
			else if(componentProperty.getType().equalsIgnoreCase(ComponentProperty.SELECTFIELD))
			{
				StringBuffer helpSB = new StringBuffer();
				helpSB.append("<div class=\"tooltipDiv\" id=\"helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "\">");
				helpSB.append("" + (componentProperty.getDescription() == null || componentProperty.getDescription().equalsIgnoreCase("") ? "No description" : componentProperty.getDescription()) + "");
				helpSB.append("</div>");

				sb.append("		<tr class=\"igtr\">");
				sb.append("			<td class=\"igpropertylabel\" valign=\"top\" align=\"left\">" + componentProperty.getName() + "</td>");
				sb.append("			<td class=\"igtd\" width=\"16\"><img src=\"" + componentEditorUrl + "/images/questionMarkGrad.gif\" onMouseOver=\"showDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\" onMouseOut=\"javascript:hideDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\">" + helpSB + "</td>");
				
				if(hasAccessToProperty)
				{
					sb.append("			<td class=\"igpropertyvalue\" align=\"left\"><input type=\"hidden\" name=\"" + propertyIndex + "_propertyName\" value=\"" + componentProperty.getName() + "\"><select class=\"propertyselect\" name=\"" + componentProperty.getName() + "\" onchange=\"setDirty();\">");
					
					Iterator optionsIterator = componentProperty.getOptions().iterator();
					while(optionsIterator.hasNext())
					{
					    ComponentPropertyOption option = (ComponentPropertyOption)optionsIterator.next();
					    boolean isSame = false;
					    if(componentProperty != null && componentProperty.getValue() != null && option != null && option.getValue() != null)
					    	isSame = componentProperty.getValue().equals(option.getValue());
					    sb.append("<option value=\"" + option.getValue() + "\"" + (isSame ? " selected=\"1\"" : "") + ">" + option.getName() + "</option>");
					}
					
				    sb.append("</td>");
				}
				else
					sb.append("			<td class=\"igpropertyvalue\" align=\"left\">" + componentProperty.getName() + "</td>");
	
				if(hasAccessToProperty)
					sb.append("			<td class=\"igtd\" width=\"16\"><a class=\"componentEditorLink\" href=\"" + componentEditorUrl + "ViewSiteNodePageComponents!deleteComponentPropertyValue.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple + "\"><img src=\"" + componentEditorUrl + "/images/delete.gif\" border=\"0\"></a></td>");
				else
					sb.append("			<td class=\"igtd\" width=\"16\"></td>");
				
				sb.append("		</tr>");
				
				if(hasAccessToProperty)
				    propertyIndex++;
			}
			else if(componentProperty.getType().equalsIgnoreCase(ComponentProperty.CHECKBOXFIELD))
			{
				StringBuffer helpSB = new StringBuffer();
				helpSB.append("<div class=\"tooltipDiv\" id=\"helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "\">");
				helpSB.append("" + (componentProperty.getDescription() == null || componentProperty.getDescription().equalsIgnoreCase("") ? "No description" : componentProperty.getDescription()) + "");
				helpSB.append("</div>");

				sb.append("		<tr class=\"igtr\">");
				sb.append("			<td class=\"igpropertylabel\" valign=\"top\" align=\"left\">" + componentProperty.getName() + "</td>");
				sb.append("			<td class=\"igtd\" width=\"16\"><img src=\"" + componentEditorUrl + "/images/questionMarkGrad.gif\" onMouseOver=\"showDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\" onMouseOut=\"javascript:hideDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\">" + helpSB + "</td>");
				
				if(hasAccessToProperty)
				{
					sb.append("			<td class=\"igpropertyvalue\" align=\"left\"><input type=\"hidden\" name=\"" + propertyIndex + "_propertyName\" value=\"" + componentProperty.getName() + "\">");
					
					Iterator optionsIterator = componentProperty.getOptions().iterator();
					while(optionsIterator.hasNext())
					{
					    ComponentPropertyOption option = (ComponentPropertyOption)optionsIterator.next();
					    boolean isSame = false;
					    if(componentProperty != null && componentProperty.getValue() != null && option != null && option.getValue() != null)
					    {
					    	String[] values = componentProperty.getValue().split(",");
					    	for(int i=0; i<values.length; i++)
					    	{
					    		isSame = values[i].equals(option.getValue());
					    		if(isSame)
					    			break;
					    	}
					    }

					    sb.append("<input type=\"checkbox\" name=\"" + componentProperty.getName() + "\" value=\"" + option.getValue() + "\"" + (isSame ? " checked=\"1\"" : "") + " onclicked=\"setDirty();\"/>" + option.getName() + " ");
					}
					
				    sb.append("</td>");
				}
				else
					sb.append("			<td class=\"igpropertyvalue\" align=\"left\">" + componentProperty.getName() + "</td>");
	
				if(hasAccessToProperty)
					sb.append("			<td class=\"igtd\" width=\"16\"><a class=\"componentEditorLink\" href=\"" + componentEditorUrl + "ViewSiteNodePageComponents!deleteComponentPropertyValue.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple + "\"><img src=\"" + componentEditorUrl + "/images/delete.gif\" border=\"0\"></a></td>");
				else
					sb.append("			<td class=\"igtd\" width=\"16\"></td>");
				
				sb.append("			<!--<td>&nbsp;</td>-->");
				sb.append("		</tr>");
				
				if(hasAccessToProperty)
				    propertyIndex++;
			}
		}
		
		sb.append("		<div class=\"buttonRow\">");
		sb.append("			<input type=\"image\" style=\"width: 50px; height: 25px;\" src=\"" + componentEditorUrl + "" + getLocalizedString(locale, "images.contenttool.buttons.save") + "\" width=\"50\" height=\"25\" border=\"0\"/>");
		sb.append("			<a href=\"javascript:clearComponentPropertiesInDiv('" + targetDiv + "');\"><img src=\"" + componentEditorUrl + "" + getLocalizedString(locale, "images.contenttool.buttons.close") + "\" width=\"50\" height=\"25\" border=\"0\"/></a>");
		sb.append("		</div>");
		sb.append("		</fieldset>");
		sb.append("		<input type=\"hidden\" name=\"repositoryId\" value=\"" + repositoryId + "\"/>");
		sb.append("		<input type=\"hidden\" name=\"siteNodeId\" value=\"" + siteNodeId + "\"/>");
		sb.append("		<input type=\"hidden\" name=\"languageId\" value=\"" + languageId + "\"/>");
		sb.append("		<input type=\"hidden\" name=\"contentId\" value=\"" + contentId + "\"/>");
		sb.append("		<input type=\"hidden\" name=\"componentId\" value=\"" + componentId + "\"/>");
		sb.append("		<input type=\"hidden\" name=\"showSimple\" value=\"" + showSimple + "\"/>");
		sb.append("		</form>");
		sb.append("	</div>");

		sb.append("	</div>");

		sb.append("	<script type=\"text/javascript\">");
		sb.append("		var theHandle = document.getElementById(\"component" + componentId + "PropertiesHandle\");");
		sb.append("		var theRoot   = document.getElementById(\"component" + componentId + "Properties\");");
		
		sb.append("		componentId = \"" + componentId + "\";");
		sb.append("		activatedComponentId = QueryString(\"activatedComponentId\");");
		sb.append("		if(activatedComponentId && activatedComponentId == componentId)"); 
		sb.append("			openDiv(\"componentProperties\");"); 

		sb.append("		Drag.init(theHandle, theRoot);");
		sb.append("     theRoot.style.left = 160;");
		sb.append("     theRoot.style.top = 150;");
		
		sb.append("     floatDiv(\"component" + componentId + "Properties\", 200, 50).flt();");
		sb.append("	</script>");
		
		return sb.toString();
	}
	
	public String getComponentPropertiesDivOld(Database db, 
											 InfoGluePrincipal principal, 
											 HttpServletRequest request, 
											 Locale locale,
											 /*TemplateController templateController, */
											 Integer repositoryId, 
											 Integer siteNodeId, 
											 Integer languageId, 
											 Integer contentId, 
											 Integer componentId, 
											 Integer componentContentId, 
											 /*
											 Document document, 
											 InfoGlueComponent component, 
											 */
											 String showSimple, 
											 String originalFullURL,
											 String tabDescription) throws Exception
	{	
	    if(request.getParameter("skipPropertiesDiv") != null && request.getParameter("skipPropertiesDiv").equalsIgnoreCase("true"))
	        return "";

	    StringBuffer sb = new StringBuffer();
		Timer timer = new Timer();
		timer.setActive(false);

		timer.printElapsedTime("After locale");
	    
		String componentEditorUrl = CmsPropertyHandler.getComponentEditorUrl();

		Document document = getComponentPropertiesDOM4JDocument(siteNodeId, languageId, componentContentId, db, principal); 

		/*
		String componentName = component.getName();
		if(componentName.length() > 20) 
			componentName = componentName.substring(0, 20) + "...";
		
		String slotName = component.getSlotName();
		if(slotName.length() > 10) 
			slotName = slotName.substring(0, 10) + "...";
		*/
		
		/*
		sb.append("<div id=\"component" + componentId + "Properties\" class=\"componentProperties\" style=\"right:5px; top:5px; visibility:visible;\">");
		sb.append("	<div id=\"component" + componentId + "PropertiesHandle\" class=\"componentPropertiesHandle\"><div id=\"leftPaletteHandleCompProps\">Properties - " + tabDescription + "</div><div id=\"rightPaletteHandle\"><a href=\"javascript:hideDiv('component" + componentId + "Properties');\" class=\"white\"><img src=\"" + componentEditorUrl + "/images/closeIcon.gif\" border=\"0\"/></a></div></div>");
		*/
		sb.append("	<div id=\"component" + componentId + "PropertiesBody\" class=\"componentPropertiesBody\">");
		
		sb.append("	<form id=\"component" + componentId + "PropertiesForm\" name=\"component" + componentId + "PropertiesForm\" action=\"" + componentEditorUrl + "ViewSiteNodePageComponents!updateComponentProperties.action\" method=\"POST\">");
		sb.append("		<table class=\"igPropertiesTable\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\">");

		sb.append("		<tr class=\"igtr\">");
		sb.append("			<td class=\"igpropertylabel\" align=\"left\">Choose language</td>");
		sb.append("			<td class=\"igtd\">&nbsp;</td>");
		sb.append("			<td class=\"igpropertyvalue\" align=\"left\">");
	
		sb.append("			");
		sb.append("			<select class=\"mediumdrop\" name=\"languageId\" onChange=\"javascript:changeLanguage(" + siteNodeId + ", this, " + contentId + ");\">");
		
		List languages = LanguageDeliveryController.getLanguageDeliveryController().getLanguagesForSiteNode(db, siteNodeId, principal);
			
		Iterator languageIterator = languages.iterator();
		int index = 0;
		int languageIndex = index;
		while(languageIterator.hasNext())
		{
			LanguageVO languageVO = (LanguageVO)languageIterator.next();
			if(languageVO.getLanguageId().intValue() == languageId.intValue())
			{
				sb.append("					<option class=\"iglabel\" value=\"" + languageVO.getLanguageId() + "\" selected><span class=\"iglabel\">" + languageVO.getName() + "</span></option>");
				sb.append("					<script type=\"text/javascript\">");
				sb.append("					</script>");
				languageIndex = index;
			}
			else
			{
				sb.append("					<option value=\"" + languageVO.getLanguageId() + "\">" + languageVO.getName() + "</option>");
			}
			index++;
		}
		sb.append("			</select>");

		sb.append("			</td>");
		sb.append("			<td class=\"igtd\">&nbsp;</td>");
		sb.append("		</tr>");

		Collection componentProperties = getComponentProperties(componentId, document, siteNodeId, languageId, contentId, locale, db, principal);
		
		int propertyIndex = 0;
		Iterator componentPropertiesIterator = componentProperties.iterator();
		while(componentPropertiesIterator.hasNext())
		{
			ComponentProperty componentProperty = (ComponentProperty)componentPropertiesIterator.next();
		
		    String cmsUserName = (String)request.getSession().getAttribute("cmsUserName");
		    if(cmsUserName != null)
		    {
			    principal = UserControllerProxy.getController(db).getUser(cmsUserName);
		    }
		    
			boolean hasAccessToProperty = AccessRightController.getController().getIsPrincipalAuthorized(db, principal, "ComponentPropertyEditor.EditProperty", "" + componentContentId + "_" + componentProperty.getName());
			
			StringBuffer helpSB = new StringBuffer();
			helpSB.append("<div class=\"tooltipDiv\" id=\"helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "\">");
			helpSB.append("" + (componentProperty.getDescription() == null || componentProperty.getDescription().equalsIgnoreCase("") ? "No description" : componentProperty.getDescription()) + "");
			helpSB.append("</div>");

			if(componentProperty.getType().equalsIgnoreCase(ComponentProperty.BINDING))
			{
				String assignUrl = "";
				String createUrl = "";
				 
				if(componentProperty.getVisualizingAction() != null && !componentProperty.getVisualizingAction().equals(""))
				{
					assignUrl = componentEditorUrl + componentProperty.getVisualizingAction() + "?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple;
				}
				else
				{	
					if(componentProperty.getEntityClass().equalsIgnoreCase("Content"))
					{
					    String allowedContentTypeIdParameters = "";

					    if(componentProperty.getAllowedContentTypeNamesArray() != null && componentProperty.getAllowedContentTypeNamesArray().length > 0)
					    {
					        allowedContentTypeIdParameters = "&" + componentProperty.getAllowedContentTypeIdAsUrlEncodedString(db);
					    }
					    
						if(componentProperty.getIsMultipleBinding())
						{
							if(componentProperty.getIsAssetBinding())
								assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showContentTreeForMultipleAssetBinding.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + allowedContentTypeIdParameters + "&showSimple=" + showSimple;
							else
								assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showContentTreeForMultipleBinding.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + allowedContentTypeIdParameters + "&showSimple=" + showSimple;
						}
						else
						{
							if(componentProperty.getIsAssetBinding())
							{
								String assignedParameters = "";
								Iterator<ComponentBinding> bindingsIterator = componentProperty.getBindings().iterator();
								while(bindingsIterator.hasNext())
								{
									ComponentBinding componentBinding = bindingsIterator.next();
									assignedParameters = "&assignedContentId=" + componentBinding.getEntityId() + "&assignedAssetKey=" + componentBinding.getAssetKey() + "&assignedPath=" + formatter.encodeURI(componentProperty.getValue());
								}
								
								assignUrl = componentEditorUrl + "ViewContentVersion!viewAssetsForComponentBinding.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + allowedContentTypeIdParameters + "&showSimple=" + showSimple + assignedParameters;
							}
							else
								assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showContentTree.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + allowedContentTypeIdParameters + "&showSimple=" + showSimple;
						}
					}
					else if(componentProperty.getEntityClass().equalsIgnoreCase("SiteNode"))
					{
						if(componentProperty.getIsMultipleBinding())
							assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showStructureTreeForMultipleBinding.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple;
						else
							assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showStructureTree.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple;
					}
					else if(componentProperty.getEntityClass().equalsIgnoreCase("Category"))
					{
						if(componentProperty.getIsMultipleBinding())
							assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showCategoryTreeForMultipleBinding.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple;
						else
							assignUrl = componentEditorUrl + "ViewSiteNodePageComponents!showCategoryTree.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple;
					}
				}
					
				if(componentProperty.getCreateAction() != null && !componentProperty.getCreateAction().equals(""))
				{
					createUrl = componentEditorUrl + componentProperty.getCreateAction() + "?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple;
				}
				else
				{	
					if(componentProperty.getVisualizingAction() != null && !componentProperty.getVisualizingAction().equals(""))
					{
						createUrl = assignUrl;
					}
					else if(componentProperty.getEntityClass().equalsIgnoreCase("Content"))
					{
					    String allowedContentTypeIdParameters = "";

					    if(componentProperty.getAllowedContentTypeNamesArray() != null && componentProperty.getAllowedContentTypeNamesArray().length > 0)
					    {
					        allowedContentTypeIdParameters = "&" + componentProperty.getAllowedContentTypeIdAsUrlEncodedString(db);
					    }

					    String returnAddress = URLEncoder.encode("ViewSiteNodePageComponents!addComponentPropertyBinding.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=-1&entity=Content&entityId=#entityId&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&path=#path&showSimple=" + showSimple + "", "UTF-8");
						
				        String cancelKey = originalFullURL;
				        String cancelAddress = (String)CacheController.getCachedObjectFromAdvancedCache("encodedStringsCache", cancelKey);
				        if(cancelAddress == null)
				        {
				        	cancelAddress = URLEncoder.encode(cancelKey, "UTF-8");
				        	CacheController.cacheObjectInAdvancedCache("encodedStringsCache", cancelKey, cancelAddress);
				        }

						if(componentProperty.getIsMultipleBinding())
							createUrl = componentEditorUrl + "CreateContentWizardFinish.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + allowedContentTypeIdParameters + "&refreshAddress=" + returnAddress + "&cancelAddress=" + cancelAddress + "&showSimple=" + showSimple;
						else
							createUrl = componentEditorUrl + "CreateContentWizardFinish.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + allowedContentTypeIdParameters + "&refreshAddress=" + returnAddress + "&cancelAddress=" + cancelAddress + "&showSimple=" + showSimple;
					}
					else if(componentProperty.getEntityClass().equalsIgnoreCase("SiteNode"))
					{
					    String returnAddress = URLEncoder.encode("ViewSiteNodePageComponents!addComponentPropertyBinding.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=-1&entity=Content&entityId=#entityId&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&path=#path&showSimple=" + showSimple + "", "UTF-8");
						
				        String cancelKey = originalFullURL;
				        String cancelAddress = (String)CacheController.getCachedObjectFromAdvancedCache("encodedStringsCache", cancelKey);
				        if(cancelAddress == null)
				        {
				        	cancelAddress = URLEncoder.encode(cancelKey, "UTF-8");
				        	CacheController.cacheObjectInAdvancedCache("encodedStringsCache", cancelKey, cancelAddress);
				        }

						if(componentProperty.getIsMultipleBinding())
							createUrl = componentEditorUrl + "CreateSiteNodeWizardFinish.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&refreshAddress=" + returnAddress + "&cancelAddress=" + cancelAddress + "&showSimple=" + showSimple;
						else
							createUrl = componentEditorUrl + "CreateSiteNodeWizardFinish.action?repositoryId=" + repositoryId + "&siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&refreshAddress=" + returnAddress + "&cancelAddress=" + cancelAddress + "&showSimple=" + showSimple;
					}
				}
								
				sb.append("		<tr class=\"igtr\">");
				sb.append("			<td class=\"igpropertylabel\" valign=\"top\" align=\"left\">" + componentProperty.getName() + "</td>");
				sb.append("			<td class=\"igtd\" width=\"16\"><img src=\"" + componentEditorUrl + "/images/questionMarkGrad.gif\" onMouseOver=\"javascript:showDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\" onMouseOut=\"javascript:hideDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\">" + helpSB + "</td>");
				sb.append("			<td class=\"igpropertyvalue\" align=\"left\">");

				if(hasAccessToProperty)
				{
					String warningText = getLocalizedString(locale, "deliver.editOnSight.dirtyWarning");
					sb.append("<a class=\"componentEditorLink\" href=\"javascript:if(checkDirty('" + warningText + "')){window.open('" + assignUrl + "','Assign','toolbar=no,status=yes,scrollbars=yes,location=no,menubar=no,directories=no,resizable=no,width=300,height=600,left=5,top=5')};\">");
				}

				sb.append("" + (componentProperty.getValue() == null || componentProperty.getValue().equalsIgnoreCase("") ? "Undefined" : componentProperty.getValue()) + (componentProperty.getIsAssetBinding() ? " (" + componentProperty.getAssetKey() + ")" : ""));
				
				if(hasAccessToProperty)
					sb.append("</a>");
				
				sb.append("</td>");
				
				if(componentProperty.getValue() != null && componentProperty.getValue().equalsIgnoreCase("Undefined"))
				{	
					if(hasAccessToProperty && createUrl != null)
						sb.append("			<td class=\"igtd\" width=\"16\"><a class=\"componentEditorLink\" href=\"" + createUrl + "\"><img src=\"" + componentEditorUrl + "/images/createContent.gif\" border=\"0\" alt=\"Create new content to show\"></a></td>");
					else
						sb.append("			<td class=\"igtd\" width=\"16\">&nbsp;</td>");
				}
				else
				{
					if(hasAccessToProperty)
						sb.append("			<td class=\"igtd\" width=\"16\"><a class=\"componentEditorLink\" href=\"" + componentEditorUrl + "ViewSiteNodePageComponents!deleteComponentPropertyValue.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple + "\"><img src=\"" + componentEditorUrl + "/images/delete.gif\" border=\"0\"></a></td>");
				}
				
				sb.append("		</tr>");
			}
			else if(componentProperty.getType().equalsIgnoreCase(ComponentProperty.TEXTFIELD))
			{
				sb.append("		<tr class=\"igtr\">");
				sb.append("			<td class=\"igpropertylabel\" valign=\"top\" align=\"left\">" + componentProperty.getName() + "</td>");
				sb.append("			<td class=\"igtd\" width=\"16\"><img src=\"" + componentEditorUrl + "/images/questionMarkGrad.gif\" onMouseOver=\"showDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\" onMouseOut=\"javascript:hideDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\">" + helpSB + "</td>");
				
				if(hasAccessToProperty)
					sb.append("			<td class=\"igpropertyvalue\" align=\"left\"><input type=\"hidden\" name=\"" + propertyIndex + "_propertyName\" value=\"" + componentProperty.getName() + "\"><input type=\"text\" class=\"propertytextfield\" name=\"" + componentProperty.getName() + "\" value=\"" + componentProperty.getValue() + "\" onkeydown=\"setDirty();\"></td>");
				else
					sb.append("			<td class=\"igpropertyvalue\" align=\"left\">" + componentProperty.getValue() + "</td>");
	
				if(hasAccessToProperty)
					sb.append("			<td class=\"igtd\" width=\"16\"><a class=\"componentEditorLink\" href=\"" + componentEditorUrl + "ViewSiteNodePageComponents!deleteComponentPropertyValue.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple + "\"><img src=\"" + componentEditorUrl + "/images/delete.gif\" border=\"0\"></a></td>");
				else
					sb.append("			<td class=\"igtd\" width=\"16\"></td>");
				
				sb.append("			<!--<td>&nbsp;</td>-->");
				sb.append("		</tr>");
				
				if(hasAccessToProperty)
				    propertyIndex++;
			}
			else if(componentProperty.getType().equalsIgnoreCase(ComponentProperty.TEXTAREA))
			{
				sb.append("		<tr class=\"igtr\">");
				sb.append("			<td class=\"igpropertylabel\" valign=\"top\" align=\"left\">" + componentProperty.getName() + "</td>");
				sb.append("			<td class=\"igtd\" width=\"16\"><img src=\"" + componentEditorUrl + "/images/questionMarkGrad.gif\" onMouseOver=\"showDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\" onMouseOut=\"javascript:hideDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\">" + helpSB + "</td>");
				
				if(hasAccessToProperty)
					sb.append("			<td class=\"igpropertyvalue\" align=\"left\"><input type=\"hidden\" name=\"" + propertyIndex + "_propertyName\" value=\"" + componentProperty.getName() + "\"><textarea class=\"propertytextarea\" name=\"" + componentProperty.getName() + "\" onkeydown=\"setDirty();\">" + (componentProperty.getValue() == null ? "" : componentProperty.getValue()) + "</textarea></td>");
				else
					sb.append("			<td class=\"igpropertyvalue\" align=\"left\">" + componentProperty.getValue() + "</td>");
	
				if(hasAccessToProperty)
					sb.append("			<td class=\"igtd\" width=\"16\"><a class=\"componentEditorLink\" href=\"" + componentEditorUrl + "ViewSiteNodePageComponents!deleteComponentPropertyValue.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple + "\"><img src=\"" + componentEditorUrl + "/images/delete.gif\" border=\"0\"></a></td>");
				else
					sb.append("			<td class=\"igtd\" width=\"16\"></td>");
				
				sb.append("			<!--<td>&nbsp;</td>-->");
				sb.append("		</tr>");
				
				if(hasAccessToProperty)
				    propertyIndex++;
			}
			else if(componentProperty.getType().equalsIgnoreCase(ComponentProperty.SELECTFIELD))
			{
				sb.append("		<tr class=\"igtr\">");
				sb.append("			<td class=\"igpropertylabel\" valign=\"top\" align=\"left\">" + componentProperty.getName() + "</td>");
				sb.append("			<td class=\"igtd\" width=\"16\"><img src=\"" + componentEditorUrl + "/images/questionMarkGrad.gif\" onMouseOver=\"showDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\" onMouseOut=\"javascript:hideDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\">" + helpSB + "</td>");
				
				if(hasAccessToProperty)
				{
					sb.append("			<td class=\"igpropertyvalue\" align=\"left\"><input type=\"hidden\" name=\"" + propertyIndex + "_propertyName\" value=\"" + componentProperty.getName() + "\"><select class=\"propertyselect\" name=\"" + componentProperty.getName() + "\" onchange=\"setDirty();\">");
					
					Iterator optionsIterator = componentProperty.getOptions().iterator();
					while(optionsIterator.hasNext())
					{
					    ComponentPropertyOption option = (ComponentPropertyOption)optionsIterator.next();
					    boolean isSame = false;
					    if(componentProperty != null && componentProperty.getValue() != null && option != null && option.getValue() != null)
					    	isSame = componentProperty.getValue().equals(option.getValue());
					    sb.append("<option value=\"" + option.getValue() + "\"" + (isSame ? " selected=\"1\"" : "") + ">" + option.getName() + "</option>");
					}
					
				    sb.append("</td>");
				}
				else
					sb.append("			<td class=\"igpropertyvalue\" align=\"left\">" + componentProperty.getName() + "</td>");
	
				if(hasAccessToProperty)
					sb.append("			<td class=\"igtd\" width=\"16\"><a class=\"componentEditorLink\" href=\"" + componentEditorUrl + "ViewSiteNodePageComponents!deleteComponentPropertyValue.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple + "\"><img src=\"" + componentEditorUrl + "/images/delete.gif\" border=\"0\"></a></td>");
				else
					sb.append("			<td class=\"igtd\" width=\"16\"></td>");
				
				sb.append("			<!--<td>&nbsp;</td>-->");
				sb.append("		</tr>");
				
				if(hasAccessToProperty)
				    propertyIndex++;
			}
			else if(componentProperty.getType().equalsIgnoreCase(ComponentProperty.CHECKBOXFIELD))
			{
				sb.append("		<tr class=\"igtr\">");
				sb.append("			<td class=\"igpropertylabel\" valign=\"top\" align=\"left\">" + componentProperty.getName() + "</td>");
				sb.append("			<td class=\"igtd\" width=\"16\"><img src=\"" + componentEditorUrl + "/images/questionMarkGrad.gif\" onMouseOver=\"showDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\" onMouseOut=\"javascript:hideDiv('helpLayer" + componentProperty.getComponentId() + "_" + componentProperty.getName() + "');\">" + helpSB + "</td>");
				
				if(hasAccessToProperty)
				{
					sb.append("			<td class=\"igpropertyvalue\" align=\"left\"><input type=\"hidden\" name=\"" + propertyIndex + "_propertyName\" value=\"" + componentProperty.getName() + "\">");
					
					Iterator optionsIterator = componentProperty.getOptions().iterator();
					while(optionsIterator.hasNext())
					{
					    ComponentPropertyOption option = (ComponentPropertyOption)optionsIterator.next();
					    boolean isSame = false;
					    if(componentProperty != null && componentProperty.getValue() != null && option != null && option.getValue() != null)
					    {
					    	String[] values = componentProperty.getValue().split(",");
					    	for(int i=0; i<values.length; i++)
					    	{
					    		isSame = values[i].equals(option.getValue());
					    		if(isSame)
					    			break;
					    	}
					    }

					    sb.append("<input type=\"checkbox\" name=\"" + componentProperty.getName() + "\" value=\"" + option.getValue() + "\"" + (isSame ? " checked=\"1\"" : "") + " onclicked=\"setDirty();\"/>" + option.getName() + " ");
					}
					
				    sb.append("</td>");
				}
				else
					sb.append("			<td class=\"igpropertyvalue\" align=\"left\">" + componentProperty.getName() + "</td>");
	
				if(hasAccessToProperty)
					sb.append("			<td class=\"igtd\" width=\"16\"><a class=\"componentEditorLink\" href=\"" + componentEditorUrl + "ViewSiteNodePageComponents!deleteComponentPropertyValue.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + contentId + "&componentId=" + componentId + "&propertyName=" + componentProperty.getName() + "&showSimple=" + showSimple + "\"><img src=\"" + componentEditorUrl + "/images/delete.gif\" border=\"0\"></a></td>");
				else
					sb.append("			<td class=\"igtd\" width=\"16\"></td>");
				
				sb.append("			<!--<td>&nbsp;</td>-->");
				sb.append("		</tr>");
				
				if(hasAccessToProperty)
				    propertyIndex++;
			}
		}
		
		sb.append("		<tr class=\"igtr\">");
		sb.append("			<td colspan=\"4\"><img src=\"" + request.getContextPath() + "/images/trans.gif\" height=\"5\" width=\"1\"></td>");
		sb.append("		</tr>");
		sb.append("		<tr class=\"igtr\">");
		sb.append("			<td colspan=\"4\">");
		sb.append("				<a href=\"javascript:submitForm('component" + componentId + "PropertiesForm');\"><img src=\"" + componentEditorUrl + "" + getLocalizedString(locale, "images.contenttool.buttons.save") + "\" width=\"50\" height=\"25\" border=\"0\"></a>");
		sb.append("				<a href=\"javascript:hideDiv('component" + componentId + "Properties');\"><img src=\"" + componentEditorUrl + "" + getLocalizedString(locale, "images.contenttool.buttons.close") + "\" width=\"50\" height=\"25\" border=\"0\"></a>");
		sb.append("			</td>");
		sb.append("		</tr>");
		sb.append("		</table>");
		sb.append("		<input type=\"hidden\" name=\"repositoryId\" value=\"" + repositoryId + "\">");
		sb.append("		<input type=\"hidden\" name=\"siteNodeId\" value=\"" + siteNodeId + "\">");
		sb.append("		<input type=\"hidden\" name=\"languageId\" value=\"" + languageId + "\">");
		sb.append("		<input type=\"hidden\" name=\"contentId\" value=\"" + contentId + "\">");
		sb.append("		<input type=\"hidden\" name=\"componentId\" value=\"" + componentId + "\">");
		sb.append("		<input type=\"hidden\" name=\"showSimple\" value=\"" + showSimple + "\">");
		sb.append("		</form>");
		sb.append("	</div>");
		/*
		sb.append("	</div>");

		sb.append("	<script type=\"text/javascript\">");
		sb.append("		var theHandle = document.getElementById(\"component" + componentId + "PropertiesHandle\");");
		sb.append("		var theRoot   = document.getElementById(\"component" + componentId + "Properties\");");
		
		sb.append("		componentId = \"" + componentId + "\";");
		sb.append("		activatedComponentId = QueryString(\"activatedComponentId\");");
		sb.append("		if(activatedComponentId && activatedComponentId == componentId)"); 
		sb.append("			showDiv(\"component\" + componentId + \"Properties\");"); 

		sb.append("		Drag.init(theHandle, theRoot);");
		sb.append("     theRoot.style.left = 160;");
		sb.append("     theRoot.style.top = 150;");
		
		sb.append("     floatDiv(\"component" + componentId + "Properties\", 200, 50).flt();");
		sb.append("	</script>");
		*/
		
		return sb.toString();
	}

	
	/**
	 * This method creates an xml representing the page structure.
	 */
	
	public String getPageComponentStructureDivJavascript(TemplateController templateController, Integer siteNodeId, Integer languageId, InfoGlueComponent component) throws Exception
	{		
	    if(templateController.getRequestParameter("skipComponentStructure") != null && templateController.getRequestParameter("skipComponentStructure").equalsIgnoreCase("true"))
	        return "";
	    
		StringBuffer sb = new StringBuffer();
		
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/build/fonts/fonts-min.css?_yuiversion=2.4.1\" />\n");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/build/treeview/assets/skins/sam/treeview.css?_yuiversion=2.4.1\" />\n");
		sb.append("<script type=\"text/javascript\" src=\"" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/build/yahoo/yahoo.js?_yuiversion=2.4.1\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/build/event/event.js?_yuiversion=2.4.1\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/build/treeview/treeview.js?_yuiversion=2.4.1\"></script>\n");
		
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/examples/treeview/assets/css/folders/tree.css\"></link>\n");
		
		sb.append("<style type=\"text/css\">\n");
		sb.append(" #treewrapper {background: #fff; position:relative;}\n");
		sb.append("	#treediv {position:relative; width:250px; background: #fff; padding:1em;}\n");
		sb.append(" .icon-ppt { display:block; padding-left: 20px; background: transparent url(" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/examples/treeview/assets/img/icons.png) 0 0px no-repeat; }\n");
		sb.append(" .icon-dmg { display:block; padding-left: 20px; background: transparent url(" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/examples/treeview/assets/img/icons.png) 0 -36px no-repeat; }\n");
		sb.append(" .icon-prv { display:block; padding-left: 20px; background: transparent url(" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/examples/treeview/assets/img/icons.png) 0 -72px no-repeat; }\n");
		sb.append(" .icon-gen { display:block; padding-left: 20px; background: transparent url(" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/examples/treeview/assets/img/icons.png) 0 -108px no-repeat; }\n");
		sb.append(" .icon-doc { display:block; padding-left: 20px; background: transparent url(" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/examples/treeview/assets/img/icons.png) 0 -144px no-repeat; }\n");
		sb.append(" .icon-jar { display:block; padding-left: 20px; background: transparent url(" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/examples/treeview/assets/img/icons.png) 0 -180px no-repeat; }\n");
		sb.append(" .icon-zip { display:block; padding-left: 20px; background: transparent url(" + templateController.getHttpServletRequest().getContextPath() + "/script/yui/examples/treeview/assets/img/icons.png) 0 -216px no-repeat; }\n");
		sb.append("</style>\n");

		sb.append("<div id=\"treewrapper\">\n");
		sb.append("	<div id=\"treediv\"> </div>\n");
		sb.append("</div>\n");
		
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("	//Wrap our initialization code in an anonymous function\n");
		sb.append("	//to keep out of the global namespace:\n");
		sb.append("	(function(){\n");
		sb.append("		var init = function() {\n");
		sb.append("     alert('Initializing');\n"); 			
		sb.append("			//create the TreeView instance:\n");
		sb.append("			var tree = new YAHOO.widget.TreeView(\"treediv\");\n");
					
		sb.append("			//get a reusable reference to the root node:\n");
		sb.append("			var root = tree.getRoot();\n");
					
		sb.append("			//for Ahmed's documents, we'll use TextNodes.\n");
		sb.append("			//First, create a parent node for his documents:\n");
		sb.append("			var ahmedDocs = new YAHOO.widget.TextNode(\"Ahmed's Documents\", root, true);\n");
		sb.append("				//Create a child node for his Word document:\n");
		sb.append("				var ahmedMsWord = new YAHOO.widget.TextNode(\"Prospectus\", ahmedDocs, false);\n");
		sb.append("				//Now, apply the \"icon-doc\" style to this node's\n");
		sb.append("				//label:\n");
		sb.append("				ahmedMsWord.labelStyle = \"icon-doc\";\n");
		sb.append("				var ahmedPpt = new YAHOO.widget.TextNode(\"Presentation\", ahmedDocs, false);\n");
		sb.append("				ahmedPpt.labelStyle = \"icon-ppt\";\n");
		sb.append("				var ahmedPdf = new YAHOO.widget.TextNode(\"Prospectus-PDF version\", ahmedDocs, false);\n");
		sb.append("				ahmedPdf.labelStyle = \"icon-prv\";\n");
				
		sb.append("			//for Susheela's documents, we'll use HTMLNodes.\n");
		sb.append("			//First, create a parent node for her documents:\n");
		sb.append("			var sushDocs = new YAHOO.widget.TextNode(\"Susheela's Documents\", root, true);\n");
		sb.append("				//Create a child node for her zipped files:\n");
		sb.append("				var sushZip = new YAHOO.widget.HTMLNode(\"Zipped Files\", sushDocs, false, true);\n");
		sb.append("				//Now, apply the \"icon-zip\" style to this HTML node's\n");
		sb.append("				//content:\n");
		sb.append("				sushZip.contentStyle = \"icon-zip\";\n");
		sb.append("				var sushDmg = new YAHOO.widget.HTMLNode(\"Files -- .dmg version\", sushDocs, false, true);\n");
		sb.append("				sushDmg.contentStyle = \"icon-dmg\";\n");
		sb.append("				var sushGen = new YAHOO.widget.HTMLNode(\"Script -- text version\", sushDocs, false, true);\n");
		sb.append("				sushGen.contentStyle = \"icon-gen\";\n");
		sb.append("				var sushJar = new YAHOO.widget.HTMLNode(\"JAR file\", sushDocs, false, true);\n");
		sb.append("				sushJar.contentStyle = \"icon-jar\";\n");
				
		sb.append("			tree.draw();\n");
		sb.append("		}\n");
		sb.append("		//Add an onDOMReady handler to build the tree when the document is ready\n");
		sb.append("	    YAHOO.util.Event.onDOMReady(init);\n");
		sb.append("	})();\n");
		sb.append("</script>\n");
		
		/*
		String componentEditorUrl = CmsPropertyHandler.getComponentEditorUrl();

		sb.append("<div id=\"pageComponents\" style=\"right:5px; top:5px; visibility:hidden; display: none;\">");

		sb.append("	<div id=\"dragCorner\" style=\"position: absolute; width: 16px; height: 16px; background-color: white; bottom: 0px; right: 0px;\"><a href=\"javascript:expandWindow('pageComponents');\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/enlarge.gif\" border=\"0\" width=\"16\" height=\"16\"></a></div>");
			
		sb.append("		<div id=\"pageComponentsHandle\"><div id=\"leftPaletteHandle\">Page components</div><div id=\"rightPaletteHandle\"><a href=\"javascript:hideDiv('pageComponents');\" class=\"white\">close</a></div></div>");
		sb.append("		<div id=\"pageComponentsBody\"><table class=\"igtable\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");

		sb.append("		<tr class=\"igtr\">");
	    sb.append("			<td class=\"igtd\" colspan=\"20\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/tcross.png\" width=\"19\" height=\"16\"><span id=\"" + component.getSlotName() + "ClickableDiv\" class=\"iglabel\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/slotIcon.gif\" width=\"16\" height=\"16\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/trans.gif\" width=\"5\" height=\"1\">" + component.getName() + "</span><script type=\"text/javascript\">initializeSlotEventHandler('" + component.getSlotName() + "ClickableDiv', '" + componentEditorUrl + "ViewSiteNodePageComponents!listComponents.action?CCC=1&siteNodeId=" + templateController.getSiteNodeId() + "&languageId=" + templateController.getLanguageId() + "&contentId=" + templateController.getContentId() + "&parentComponentId=" + component.getId() + "&slotId=base&showSimple=" + templateController.getDeliveryContext().getShowSimple() + "', '', '', 'base', '" + component.getContentId() + "');</script></td>");
		sb.append("		</tr>");
		
		renderComponentTree(templateController, sb, component, 0, 0, 1);

		sb.append("		<tr class=\"igtr\">");
		for(int i=0; i<20; i++)
		{
			sb.append("<td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/trans.gif\" width=\"19\" height=\"1\"></td>");
		}
		sb.append("		</tr>");
		sb.append("		</table>");
		sb.append("		</div>");
		sb.append("	</div>");
		
		sb.append("	<script type=\"text/javascript\">");
		sb.append("		var theHandle = document.getElementById(\"pageComponentsHandle\");");
		sb.append("		var theRoot   = document.getElementById(\"pageComponents\");");
		sb.append("		Drag.init(theHandle, theRoot);");
		sb.append("     theRoot.style.left = 160;");
		sb.append("     theRoot.style.top = 150;");
		sb.append("	</script>");
		*/
		
		return sb.toString();
	}

	/**
	 * This method renders the component tree visually
	 */
	
	private void renderComponentTree(TemplateController templateController, StringBuffer sb, InfoGlueComponent component, int level, int position, int maxPosition) throws Exception
	{
		String componentEditorUrl = CmsPropertyHandler.getComponentEditorUrl();

		ContentVO componentContentVO = templateController.getContent(component.getContentId());
		
		int colspan = 20 - level;
		
		sb.append("		<tr class=\"igtr\">");
		sb.append("			<td class=\"igtd\"><img src=\"" + templateController.getHttpServletRequest() + "/images/trans.gif\" width=\"19\" height=\"16\"></td>");
		
		for(int i=0; i<level; i++)
		{
			sb.append("<td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/vline.png\" width=\"19\" height=\"16\"></td>");
		}
			
		String changeUrl = componentEditorUrl + "ViewSiteNodePageComponents!listComponentsForChange.action?siteNodeId=" + templateController.getSiteNodeId() + "&languageId=" + templateController.getLanguageId() + "&contentId=" + templateController.getContentId() + "&componentId=" + component.getId() + "&slotId=" + component.getId() + "&showSimple=" + templateController.getDeliveryContext().getShowSimple();
		sb.append("<td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/tcross.png\" width=\"19\" height=\"16\"></td><td class=\"igtd\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/componentIcon.gif\" width=\"16\" height=\"16\"></td><td class=\"igtd\" colspan=\"" + (colspan - 2) + "\"><span id=\"" + component.getId() + "\">" + componentContentVO.getName() + "</span><script type=\"text/javascript\">initializeComponentInTreeEventHandler('" + component.getId() + "', '" + component.getId() + "', '', '" + componentEditorUrl + "ViewSiteNodePageComponents!deleteComponent.action?siteNodeId=" + templateController.getSiteNodeId() + "&languageId=" + templateController.getLanguageId() + "&contentId=" + templateController.getContentId() + "&componentId=" + component.getId() + "&slotId=" + component.getId() + "&showSimple=" + templateController.getDeliveryContext().getShowSimple() + "', '" + changeUrl + "', '" + component.getSlotName() + "', 'APA');</script>");
		String upUrl = componentEditorUrl + "ViewSiteNodePageComponents!moveComponent.action?siteNodeId=" + templateController.getSiteNodeId() + "&languageId=" + templateController.getLanguageId() + "&contentId=" + templateController.getContentId() + "&componentId=" + component.getId() + "&direction=0&showSimple=" + templateController.getDeliveryContext().getShowSimple() + "";
		String downUrl = componentEditorUrl + "ViewSiteNodePageComponents!moveComponent.action?siteNodeId=" + templateController.getSiteNodeId() + "&languageId=" + templateController.getLanguageId() + "&contentId=" + templateController.getContentId() + "&componentId=" + component.getId() + "&direction=1&showSimple=" + templateController.getDeliveryContext().getShowSimple() + "";
		
		if(position > 0)
		    sb.append("<a href=\"" + upUrl + "\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/upArrow.gif\" border=\"0\" width=\"11\" width=\"10\"></a>");
		if(maxPosition > position)
		    sb.append("<a href=\"" + downUrl + "\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/downArrow.gif\" border=\"0\" width=\"11\" width=\"10\"></a>");
		
		sb.append("</td>");
		
		sb.append("		</tr>");
		
		//Properties
		/*
		
		sb.append("		<tr class=\"igtr\">");
		sb.append("			<td class=\"igtd\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/trans.gif\" width=\"19\" height=\"1\"></td><td class=\"igtd\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/vline.png\" width=\"19\" height=\"16\"></td>");
		for(int i=0; i<level; i++)
		{
			sb.append("<td class=\"igtd\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/vline.png\" width=\"19\" height=\"16\"></td>");
		}
		sb.append("<td class=\"igtd\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/tcross.png\" width=\"19\" height=\"16\"></td><td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/propertiesIcon.gif\" width=\"16\" height=\"16\" border=\"0\"></td><td class=\"igtd\" colspan=\"" + (colspan - 3) + "\"><span onclick=\"javascript:showComponentProperties('component" + component.getId() + "Properties');\" class=\"iglabel\">Properties</span></td>");
		sb.append("		</tr>");
		
		sb.append("		<tr class=\"igtr\">");
		sb.append("			<td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/trans.gif\" width=\"19\" height=\"1\"></td><td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/vline.png\" width=\"19\" height=\"16\"></td>");
		for(int i=0; i<level; i++)
		{
			sb.append("<td class=\"igtd\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/vline.png\" width=\"19\" height=\"16\"></td>");
		}
		sb.append("<td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/endline.png\" width=\"19\" height=\"16\"></td><td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/containerIcon.gif\" width=\"16\" height=\"16\"></td><td class=\"igtd\" colspan=\"" + (colspan - 4) + "\"><span class=\"iglabel\">Slots</span></td>");
		sb.append("</tr>");
		*/
		
		Iterator slotIterator = component.getSlotList().iterator();
		while(slotIterator.hasNext())
		{
			Slot slot = (Slot)slotIterator.next();
	
			sb.append("		<tr class=\"igtr\">");
			sb.append("			<td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/trans.gif\" width=\"19\" height=\"16\"></td><td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/vline.png\" width=\"19\" height=\"16\"></td>");
			for(int i=0; i<level; i++)
			{
				sb.append("<td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/vline.png\" width=\"19\" height=\"16\"></td>");
			}
			if(slot.getComponents().size() > 0)
				sb.append("<td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/tcross.png\" width=\"19\" height=\"16\"></td><td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/slotIcon.gif\" width=\"16\" height=\"16\"></td>");
			else
				sb.append("<td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/endline.png\" width=\"19\" height=\"16\"></td><td class=\"igtd\" width=\"19\"><img src=\"" + templateController.getHttpServletRequest().getContextPath() + "/images/slotIcon.gif\" width=\"16\" height=\"16\"></td>");

		    String allowedComponentNamesAsEncodedString = slot.getAllowedComponentsArrayAsUrlEncodedString();
		    String disallowedComponentNamesAsEncodedString = slot.getDisallowedComponentsArrayAsUrlEncodedString();
		    //System.out.println("allowedComponentNamesAsEncodedString:" + allowedComponentNamesAsEncodedString);
		    //System.out.println("disallowedComponentNamesAsEncodedString:" + disallowedComponentNamesAsEncodedString);
		    
		    sb.append("<td class=\"igtd\" colspan=\"" + (colspan - 4) + "\"><span id=\"" + slot.getId() + "ClickableDiv\" class=\"iglabel\">" + slot.getId() + "</span><script type=\"text/javascript\">initializeSlotEventHandler('" + slot.getId() + "ClickableDiv', '" + componentEditorUrl + "ViewSiteNodePageComponents!listComponents.action?ddd=1&siteNodeId=" + templateController.getSiteNodeId() + "&languageId=" + templateController.getLanguageId() + "&contentId=" + templateController.getContentId() + "&parentComponentId=" + component.getId() + "&slotId=" + slot.getId() + "&showSimple=" + templateController.getDeliveryContext().getShowSimple() + ((allowedComponentNamesAsEncodedString != null) ? "&" + allowedComponentNamesAsEncodedString : "") + ((disallowedComponentNamesAsEncodedString != null) ? "&" + disallowedComponentNamesAsEncodedString : "") + "', '', '', '" + slot.getId() + "', '" + component.getContentId() + "');</script></td>");
			
			sb.append("		</tr>");

			List slotComponents = slot.getComponents();
			//logger.info("Number of components in slot " + slot.getId() + ":" + slotComponents.size());

			if(slotComponents != null)
			{
				Iterator slotComponentIterator = slotComponents.iterator();
				int newPosition = 0;
				while(slotComponentIterator.hasNext())
				{
					InfoGlueComponent slotComponent = (InfoGlueComponent)slotComponentIterator.next();
					ContentVO componentContent = templateController.getContent(slotComponent.getContentId()); 
					
					String imageUrl = "" + templateController.getHttpServletRequest().getContextPath() + "/images/componentIcon.gif";
					//String imageUrlTemp = getDigitalAssetUrl(componentContent.getId(), "thumbnail");
					//if(imageUrlTemp != null && imageUrlTemp.length() > 0)
					//	imageUrl = imageUrlTemp;
		
					renderComponentTree(templateController, sb, slotComponent, level + 2, newPosition, slotComponents.size() - 1);

					newPosition++;
				}	
			}
		}
	}

	
	/**
	 * This method returns the contents that are of contentTypeDefinition "HTMLTemplate"
	 */
	
	public List getComponentContents(Database db) throws Exception
	{
		HashMap arguments = new HashMap();
		arguments.put("method", "selectListOnContentTypeName");
		
		List argumentList = new ArrayList();
		HashMap argument = new HashMap();
		argument.put("contentTypeDefinitionName", "HTMLTemplate");
		argumentList.add(argument);
		arguments.put("arguments", argumentList);
		
		return ContentController.getContentController().getContentVOList(arguments, db);
	}

	
	/*
	 * This method returns a bean representing a list of ComponentProperties that the component has.
	 */
	 
	private List getComponentProperties(Integer componentId, Document document, Integer siteNodeId, Integer languageId, Integer contentId, Locale locale, Database db, InfoGluePrincipal principal) throws Exception
	{
		//TODO - här kan vi säkert cache:a.
		
		//logger.info("componentPropertiesXML:" + componentPropertiesXML);
		List componentProperties = new ArrayList();
		Timer timer = new Timer();
		timer.setActive(false);

		try
		{
			if(document != null)
			{
				timer.printElapsedTime("Read document");

				String propertyXPath = "//property";
				//logger.info("propertyXPath:" + propertyXPath);
				List anl = document.selectNodes(propertyXPath);
				timer.printElapsedTime("Set property xpath");
				//logger.info("*********************************************************anl:" + anl.getLength());
				Iterator anlIterator = anl.iterator();
				while(anlIterator.hasNext())
				{
					Element binding = (Element)anlIterator.next();
					
					String name							 = binding.attributeValue("name");
					String description					 = binding.attributeValue("description");
					String type							 = binding.attributeValue("type");
					String allowedContentTypeNamesString = binding.attributeValue("allowedContentTypeDefinitionNames");
					String visualizingAction 			 = binding.attributeValue("visualizingAction");
					String createAction 				 = binding.attributeValue("createAction");
					//logger.info("name:" + name);
					//logger.info("type:" + type);

					ComponentProperty property = new ComponentProperty();
					property.setComponentId(componentId);
					property.setName(name);
					property.setDescription(description);
					property.setType(type);
					property.setVisualizingAction(visualizingAction);
					property.setCreateAction(createAction);
					if(allowedContentTypeNamesString != null && allowedContentTypeNamesString.length() > 0)
					{
					    String[] allowedContentTypeNamesArray = allowedContentTypeNamesString.split(",");
					    property.setAllowedContentTypeNamesArray(allowedContentTypeNamesArray);
					}
					
					if(type.equalsIgnoreCase(ComponentProperty.BINDING))
					{
						String entity 	= binding.attributeValue("entity");
						boolean isMultipleBinding = new Boolean(binding.attributeValue("multiple")).booleanValue();
						boolean isAssetBinding 	  = new Boolean(binding.attributeValue("assetBinding")).booleanValue();
						
						property.setEntityClass(entity);
						String value = getComponentPropertyValue(componentId, name, siteNodeId, languageId, contentId, locale, db, principal);
						timer.printElapsedTime("Set property1");

						property.setValue(value);
						property.setIsMultipleBinding(isMultipleBinding);
						property.setIsAssetBinding(isAssetBinding);
						List<ComponentBinding> bindings = getComponentPropertyBindings(componentId, name, siteNodeId, languageId, contentId, locale, db, principal);
						property.setBindings(bindings);
					}
					else if(type.equalsIgnoreCase(ComponentProperty.TEXTFIELD))	
					{		
						String value = getComponentPropertyValue(componentId, name, siteNodeId, languageId, contentId, locale, db, principal);
						timer.printElapsedTime("Set property2");
						//logger.info("value:" + value);
						property.setValue(value);
					}
					else if(type.equalsIgnoreCase(ComponentProperty.TEXTAREA))	
					{		
						String value = getComponentPropertyValue(componentId, name, siteNodeId, languageId, contentId, locale, db, principal);
						timer.printElapsedTime("Set property2");
						//logger.info("value:" + value);
						property.setValue(value);
					}
					else if(type.equalsIgnoreCase(ComponentProperty.SELECTFIELD))	
					{		
						String value = getComponentPropertyValue(componentId, name, siteNodeId, languageId, contentId, locale, db, principal);
						timer.printElapsedTime("Set property2");
						
						List optionList = binding.elements("option");
						Iterator optionListIterator = optionList.iterator();
						while(optionListIterator.hasNext())
						{
							Element option = (Element)optionListIterator.next();
							String optionName	= option.attributeValue("name");
							String optionValue	= option.attributeValue("value");
							ComponentPropertyOption cpo = new ComponentPropertyOption();
							cpo.setName(optionName);
							cpo.setValue(optionValue);
							property.getOptions().add(cpo);
						}
						
						//logger.info("value:" + value);
						property.setValue(value);
					}
					else if(type.equalsIgnoreCase(ComponentProperty.CHECKBOXFIELD))	
					{		
						String value = getComponentPropertyValue(componentId, name, siteNodeId, languageId, contentId, locale, db, principal);
						timer.printElapsedTime("Set property3");
						
						List optionList = binding.elements("option");
						Iterator optionListIterator = optionList.iterator();
						while(optionListIterator.hasNext())
						{
							Element option = (Element)optionListIterator.next();
							String optionName	= option.attributeValue("name");
							String optionValue	= option.attributeValue("value");
							ComponentPropertyOption cpo = new ComponentPropertyOption();
							cpo.setName(optionName);
							cpo.setValue(optionValue);
							property.getOptions().add(cpo);
						}
						
						//logger.info("value:" + value);
						property.setValue(value);
					}
					
					componentProperties.add(property);
				}
			}
		}
		catch(Exception e)
		{
			logger.warn("The component with id " + componentId + " had a incorrect xml defining it's properties:" + e.getMessage(), e);
		}
							
		return componentProperties;
	}

	/**
	 * This method returns a value for a property if it's set. The value is collected in the
	 * properties for the page.
	 */
	
	private String getComponentPropertyValue(Integer componentId, String name, Integer siteNodeId, Integer languageId, Integer contentId, Locale locale, Database db, InfoGluePrincipal principal) throws Exception
	{
		String value = "Undefined";
		
		Timer timer = new Timer();
		timer.setActive(false);
				
		Document document = getPageComponentsDOM4JDocument(db, siteNodeId, languageId, contentId, principal);
		System.out.println("document:" + document.asXML());
		System.out.println("locale.getLanguage():" + locale.getLanguage());
		
		String componentXPath = "//component[@id=" + componentId + "]/properties/property[@name='" + name + "']";
		System.out.println("componentXPath:" + componentXPath);
		List anl = document.selectNodes(componentXPath);
		System.out.println("anl:" + anl.size());
		Iterator anlIterator = anl.iterator();
		while(anlIterator.hasNext())
		{
			Element property = (Element)anlIterator.next();
			
			String id 			= property.attributeValue("type");
			String path 		= property.attributeValue("path");
			
			if(property.attribute("path_" + locale.getLanguage()) != null)
				path = property.attributeValue("path_" + locale.getLanguage());

			value = path;
		
			String separator = System.getProperty("line.separator");
			value = value.replaceAll("igbr", separator);
		}
		
		return value;
	}

  	public String getLocalizedString(Locale locale, String key) 
  	{
    	StringManager stringManager = StringManagerFactory.getPresentationStringManager("org.infoglue.cms.applications", locale);

    	return stringManager.getString(key);
  	}

	/**
	 * This method returns a value for a property if it's set. The value is collected in the
	 * properties for the page.
	 */
	
	private List<ComponentBinding> getComponentPropertyBindings(Integer componentId, String name, Integer siteNodeId, Integer languageId, Integer contentId, Locale locale, Database db, InfoGluePrincipal principal) throws Exception
	{
		List<ComponentBinding> componentBindings = new ArrayList<ComponentBinding>();
		
		Document document = getPageComponentsDOM4JDocument(db, siteNodeId, languageId, contentId, principal);
		
		String componentXPath = "//component[@id=" + componentId + "]/properties/property[@name='" + name + "']/binding";
		List anl = document.selectNodes(componentXPath);
		Iterator anlIterator = anl.iterator();
		while(anlIterator.hasNext())
		{
			Element property = (Element)anlIterator.next();
			
			String entity   = property.attributeValue("entity");
			String entityId = property.attributeValue("entityId");
			String assetKey = property.attributeValue("assetKey");
			
			ComponentBinding componentBinding = new ComponentBinding();
			componentBinding.setEntityClass(entity);
			componentBinding.setEntityId(new Integer(entityId));
			componentBinding.setAssetKey(assetKey);

			componentBindings.add(componentBinding);
		}
		
		return componentBindings;
	}

	protected org.dom4j.Document getPageComponentsDOM4JDocument(Database db, Integer siteNodeId, Integer languageId, Integer contentId, InfoGluePrincipal principal) throws SystemException, Exception
	{ 
		String cacheName 	= "componentEditorCache";
		String cacheKey		= "pageComponentDocument_" + siteNodeId + "_" + languageId + "_" + contentId;
		org.dom4j.Document cachedPageComponentsDocument = (org.dom4j.Document)CacheController.getCachedObject(cacheName, cacheKey);
		if(cachedPageComponentsDocument != null)
			return cachedPageComponentsDocument;
		
		org.dom4j.Document pageComponentsDocument = null;
   	
		try
		{
			String xml = getPageComponentsString(db, siteNodeId, languageId, contentId, principal);
			pageComponentsDocument = domBuilder.getDocument(xml);
			
			CacheController.cacheObject(cacheName, cacheKey, pageComponentsDocument);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return pageComponentsDocument;
	}

	/**
	 * This method fetches the pageComponent structure from the metainfo content.
	 */
	    
	protected String getPageComponentsString(Database db, Integer siteNodeId, Integer languageId, Integer contentId, InfoGluePrincipal principal) throws SystemException, Exception
	{
	    SiteNodeVO siteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(siteNodeId, db);
	    ContentVO contentVO = null;
	    if(siteNodeVO.getMetaInfoContentId() != null && siteNodeVO.getMetaInfoContentId().intValue() > -1)
	        contentVO = ContentController.getContentController().getContentVOWithId(siteNodeVO.getMetaInfoContentId(), db);
	    else
		    contentVO = NodeDeliveryController.getNodeDeliveryController(siteNodeId, languageId, contentId).getBoundContent(db, principal, siteNodeId, languageId, true, "Meta information", null);		

		if(contentVO == null)
			throw new SystemException("There was no Meta Information bound to this page which makes it impossible to render.");	

	    String cacheName 	= "componentEditorCache";
		String cacheKey		= "pageComponentString_" + siteNodeId + "_" + languageId + "_" + contentId;
		String versionKey 	= cacheKey + "_contentVersionId";

		String attributeName = "ComponentStructure";

	    String cachedPageComponentsString = (String)CacheController.getCachedObject(cacheName, cacheKey);
	    Set contentVersionId = (Set)CacheController.getCachedObjectFromAdvancedCache("contentVersionCache", versionKey);

		if(cachedPageComponentsString != null)
		{			
		    return cachedPageComponentsString;
		}
		
		String pageComponentsString = null;
   					
		Integer masterLanguageId = LanguageDeliveryController.getLanguageDeliveryController().getMasterLanguageForSiteNode(db, siteNodeId).getId();
		//pageComponentsString = ContentDeliveryController.getContentDeliveryController().getContentAttribute(db, contentVO.getContentId(), masterLanguageId, "ComponentStructure", siteNodeId, true, null, principal, false, true);
		pageComponentsString = ContentController.getContentController().getContentAttribute(db, contentVO.getContentId(), masterLanguageId, "ComponentStructure");
		
		if(pageComponentsString == null)
			throw new SystemException("There was no Meta Information bound to this page which makes it impossible to render.");	
				    		
		return pageComponentsString;
	}

	protected Document getComponentPropertiesDOM4JDocument(Integer siteNodeId, Integer languageId, Integer contentId, Database db, InfoGluePrincipal principal) throws SystemException, Exception
	{ 
		String cacheName 	= "componentEditorCache";
		String cacheKey		= "componentPropertiesDocument_" + siteNodeId + "_" + languageId + "_" + contentId;
		Document cachedComponentPropertiesDocument = (Document)CacheController.getCachedObject(cacheName, cacheKey);
		if(cachedComponentPropertiesDocument != null)
			return cachedComponentPropertiesDocument;
		
		Document componentPropertiesDocument = null;
   	
		try
		{
			String xml = this.getComponentPropertiesString(siteNodeId, languageId, contentId, db, principal);
			//logger.info("xml: " + xml);
			if(xml != null && xml.length() > 0)
			{
				componentPropertiesDocument = domBuilder.getDocument(xml);
				
				CacheController.cacheObject(cacheName, cacheKey, componentPropertiesDocument);
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return componentPropertiesDocument;
	}

	/**
	 * This method fetches the template-string.
	 */
   
	private String getComponentPropertiesString(Integer siteNodeId, Integer languageId, Integer contentId, Database db, InfoGluePrincipal principal) throws SystemException, Exception
	{
		String cacheName 	= "componentEditorCache";
		String cacheKey		= "componentPropertiesString_" + siteNodeId + "_" + languageId + "_" + contentId;
		String cachedComponentPropertiesString = (String)CacheController.getCachedObject(cacheName, cacheKey);
		if(cachedComponentPropertiesString != null)
			return cachedComponentPropertiesString;
			
		String componentPropertiesString = null;
   	
		try
		{
		    Integer masterLanguageId = LanguageDeliveryController.getLanguageDeliveryController().getMasterLanguageForSiteNode(db, siteNodeId).getId();
			System.out.println("contentId:" + contentId);
			System.out.println("masterLanguageId:" + masterLanguageId);
			componentPropertiesString = ContentController.getContentController().getContentAttribute(db, contentId, masterLanguageId, "ComponentProperties");
			//componentPropertiesString = ContentDeliveryController.getContentDeliveryController().getContentAttribute(db, contentId, masterLanguageId, "ComponentProperties", siteNodeId, true, null, principal, false, true);

			if(componentPropertiesString == null)
				throw new SystemException("There was no properties assigned to this content.");
		
			CacheController.cacheObject(cacheName, cacheKey, componentPropertiesString);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			throw e;
		}

		return componentPropertiesString;
	}

	/**
	 * This method fetches the template-string.
	 */
   
	private String getComponentTemplateString(Integer componentContentId, Integer languageId, Database db, InfoGluePrincipal principal) throws SystemException, Exception
	{
		String cacheName 	= "componentEditorCache";
		String cacheKey		= "componentTemplateString_" + componentContentId + "_" + languageId;
		String cachedComponentPropertiesString = (String)CacheController.getCachedObject(cacheName, cacheKey);
		if(cachedComponentPropertiesString != null)
			return cachedComponentPropertiesString;
			
		String templateString = null;
   	
		try
		{
			ContentVO contentVO = ContentController.getContentController().getContentVOWithId(componentContentId, db);
		    Integer masterLanguageId = LanguageDeliveryController.getLanguageDeliveryController().getMasterLanguageForRepository(contentVO.getRepositoryId(), db).getId();
			System.out.println("componentContentId:" + componentContentId);
			System.out.println("masterLanguageId:" + masterLanguageId);
			templateString = ContentController.getContentController().getContentAttribute(db, componentContentId, masterLanguageId, "Template");

			if(templateString == null)
				throw new SystemException("There was no template on the content: " + componentContentId);
		
			CacheController.cacheObject(cacheName, cacheKey, templateString);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			throw e;
		}

		return templateString;
	}

	
	
	
	public String getAvailableComponentsDiv(Database db, InfoGluePrincipal principal, Locale locale, Integer repositoryId, Integer languageId, Integer componentContentId, String slotName, String showLegend, String targetDiv)
	{
		StringBuffer sb = new StringBuffer();
		
	    try
	    {
	    	List<Slot> slots = getSlots(componentContentId, languageId, db, principal);
	    	Iterator<Slot> slotsIterator = slots.iterator();
	    	while(slotsIterator.hasNext())
	    	{
	    		Slot slot = slotsIterator.next();
	    		System.out.println("slot:" + slot.getId() + "-" + slot.getName());
	    		if(slot.getId().equals(slotName));
	    		{
	    	        String direction = "asc";
	    	        List componentVOList = ComponentController.getController().getComponentVOList("name", direction, slot.getAllowedComponentsArray(), slot.getDisallowedComponentsArray(), principal);
	    	        Iterator componentVOListIterator = componentVOList.iterator();

    	        	sb.append("<fieldset>");
    	        	sb.append("<legend>Drag component to slot</legend>");
    	        	
    	        	sb.append("<div id=\"availableComponents\">");
    	        	
    	        	int i = 0;
	    	        while(componentVOListIterator.hasNext())
	    	        {
	    	        	ContentVO componentContentVO = (ContentVO)componentVOListIterator.next();
	    	        	if(repositoryId != null && !componentContentVO.getRepositoryId().equals(repositoryId))
	    	        		continue;
	    	        	
	    	        	String imageUrl = getDigitalAssetUrl(componentContentVO.getId(), "thumbnail");
	    				if(imageUrl == null || imageUrl.length() == 0)
	    					imageUrl = "images/componentIcon.gif";

	    				sb.append("<div id=\"componentRow\" name=\"" +  componentContentVO.getId() + "\" class=\"dragable\">");
	    	    		
    					sb.append("	<div id=\"componentIcon\"><img src=\"" + imageUrl + "\" border=\"0\"/></div>");
	    				
	    				i++;
	    				
	    	        	sb.append("	<div id=\"componentName\">" + componentContentVO.getName() + "</div>");

	    	        	/*
    					<td valign="middle" rowspan="2" style="border-bottom: 1px solid #666666;">
    						#if($content.extraProperties.get("Description") != "Unknown" && $content.extraProperties.get("Description") != "")
    						<img src="images/questionMarkGrad.gif" onclick="toggleDiv('descLayer$content.id');" aonMouseOver="showDiv('descLayer$content.id');" aonMouseOut="hideDiv('descLayer$content.id');">
    						#else
    						&nbsp;
    						#end
    					</td>

    					<td colspan="3" style="height: 0px; border-bottom: 1px solid #666666;">
    						<span id="descLayer$content.id" style="display:none; padding-bottom: 4px;"><b>Description:</b><br/>
    						#if($content.extraProperties.get("Description"))
    							$content.extraProperties.get("Description")
    						#else
    							No description
    						#end
    						</span>
    					</td>
	    				#set($imageUrl = "")
	    	    		*/

	    	        	sb.append("</div>");
	    	        }
	    	        
		        	sb.append("</fieldset>");
	    	        break;
	    		}
	    	}
	        
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	    }
		/*
	    sb.append("<script type=\"text/javascript\">");
	    sb.append("<!--");
   		sb.append("alert('Problem...');");
   		sb.append("alert(\"Size:\" + $(\".dragable\").size());");
   		sb.append("$(\".dragable\").draggable({helper: 'clone'});");
	    */
	    /*
   		$(".block").draggable({helper: 'clone'});
	    	$(".drop").droppable({
	    		accept: ".block",
	    		activeClass: 'droppable-active',
	    		hoverClass: 'droppable-hover',
	    		drop: function(ev, ui) {
	    			$(this).append("<br>Dropped!");
	    		}
	    	  });
   		sb.append("});");
	    */
	    /*
   		sb.append("-->");
   		sb.append("</script>");
	    */
		return sb.toString();
	}

	
	public List<Slot> getSlots(Integer componentContentId, Integer languageId, Database db, InfoGluePrincipal principal) throws Exception
	{
		List<Slot> slots = new ArrayList<Slot>();
		
		String template = getComponentTemplateString(componentContentId, languageId, db, principal);
		
		int offset = 0;
		int slotStartIndex = template.indexOf("<ig:slot", offset);
		//logger.info("slotStartIndex:" + slotStartIndex);
		while(slotStartIndex > -1)
		{
			int slotStopIndex = template.indexOf("</ig:slot>", slotStartIndex);
			
			String slot = template.substring(slotStartIndex, slotStopIndex + 10);
			String id = slot.substring(slot.indexOf("id") + 4, slot.indexOf("\"", slot.indexOf("id") + 4));
			
			Slot slotBean = new Slot();
		    slotBean.setId(id);

		    String[] allowedComponentNamesArray = null;
		    int allowedComponentNamesIndex = slot.indexOf(" allowedComponentNames");
			if(allowedComponentNamesIndex > -1)
			{    
			    String allowedComponentNames = slot.substring(allowedComponentNamesIndex + 24, slot.indexOf("\"", allowedComponentNamesIndex + 24));
			    allowedComponentNamesArray = allowedComponentNames.split(",");
			    //System.out.println("allowedComponentNamesArray:" + allowedComponentNamesArray.length);
			    slotBean.setAllowedComponentsArray(allowedComponentNamesArray);
			}

			String[] disallowedComponentNamesArray = null;
			int disallowedComponentNamesIndex = slot.indexOf(" disallowedComponentNames");
			if(disallowedComponentNamesIndex > -1)
			{    
			    String disallowedComponentNames = slot.substring(disallowedComponentNamesIndex + 27, slot.indexOf("\"", disallowedComponentNamesIndex + 27));
			    disallowedComponentNamesArray = disallowedComponentNames.split(",");
			    //System.out.println("disallowedComponentNamesArray:" + disallowedComponentNamesArray.length);
			    slotBean.setDisallowedComponentsArray(disallowedComponentNamesArray);
			}

			boolean inherit = true;
			int inheritIndex = slot.indexOf("inherit");
			if(inheritIndex > -1)
			{    
			    String inheritString = slot.substring(inheritIndex + 9, slot.indexOf("\"", inheritIndex + 9));
			    inherit = Boolean.parseBoolean(inheritString);
			}
			slotBean.setInherit(inherit);
			
			boolean disableAccessControl = false;
			int disableAccessControlIndex = slot.indexOf("disableAccessControl");
			if(disableAccessControlIndex > -1)
			{    
			    String disableAccessControlString = slot.substring(disableAccessControlIndex + "disableAccessControl".length() + 2, slot.indexOf("\"", disableAccessControlIndex + "disableAccessControl".length() + 2));
			    disableAccessControl = Boolean.parseBoolean(disableAccessControlString);
			}

			String addComponentText = null;
			int addComponentTextIndex = slot.indexOf("addComponentText");
			if(addComponentTextIndex > -1)
			{    
			    addComponentText = slot.substring(addComponentTextIndex + "addComponentText".length() + 2, slot.indexOf("\"", addComponentTextIndex + "addComponentText".length() + 2));
			}

			String addComponentLinkHTML = null;
			int addComponentLinkHTMLIndex = slot.indexOf("addComponentLinkHTML");
			if(addComponentLinkHTMLIndex > -1)
			{    
			    addComponentLinkHTML = slot.substring(addComponentLinkHTMLIndex + "addComponentLinkHTML".length() + 2, slot.indexOf("\"", addComponentLinkHTMLIndex + "addComponentLinkHTML".length() + 2));
			}

			int allowedNumberOfComponentsInt = -1;
			int allowedNumberOfComponentsIndex = slot.indexOf("allowedNumberOfComponents");
			if(allowedNumberOfComponentsIndex > -1)
			{    
				String allowedNumberOfComponents = slot.substring(allowedNumberOfComponentsIndex + "allowedNumberOfComponents".length() + 2, slot.indexOf("\"", allowedNumberOfComponentsIndex + "allowedNumberOfComponents".length() + 2));
				try
				{
					allowedNumberOfComponentsInt = new Integer(allowedNumberOfComponents);
				}
				catch (Exception e) 
				{
					allowedNumberOfComponentsInt = -1;
				}
			}

			slotBean.setDisableAccessControl(disableAccessControl);
			slotBean.setAddComponentLinkHTML(addComponentLinkHTML);
		    slotBean.setAddComponentText(addComponentText);
		    slotBean.setAllowedNumberOfComponents(new Integer(allowedNumberOfComponentsInt));
		    
		    slots.add(slotBean);
		    
			offset = slotStopIndex + 10;
			slotStartIndex = template.indexOf("<ig:slot", offset);
		}
		
		return slots;
	}
	
		/**
	 * This method fetches an url to the asset for the component.
	 */
	
	public String getDigitalAssetUrl(Integer contentId, String key) throws Exception
	{
		String imageHref = null;
		try
		{
			LanguageVO masterLanguage = LanguageController.getController().getMasterLanguage(ContentController.getContentController().getContentVOWithId(contentId).getRepositoryId());
			ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(contentId, masterLanguage.getId());
			if(contentVersionVO != null)
			{
				List digitalAssets = DigitalAssetController.getDigitalAssetVOList(contentVersionVO.getId());
				Iterator i = digitalAssets.iterator();
				while(i.hasNext())
				{
					DigitalAssetVO digitalAssetVO = (DigitalAssetVO)i.next();
					if(digitalAssetVO.getAssetKey().equals(key))
					{
						imageHref = DigitalAssetController.getDigitalAssetUrl(digitalAssetVO.getId()); 
						break;
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.warn("We could not get the url of the digitalAsset: " + e.getMessage(), e);
			imageHref = e.getMessage();
		}
		
		return imageHref;
	}

}
