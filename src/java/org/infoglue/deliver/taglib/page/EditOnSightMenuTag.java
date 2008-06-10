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

package org.infoglue.deliver.taglib.page;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.infoglue.cms.entities.content.Content;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.structure.SiteNode;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.taglib.component.ComponentLogicTag;

/**
 * This taglib creates the nice InfoGlue functions-icon with it's menu.
 * 
 * @author Mattias Bogeblad
 */

public class EditOnSightMenuTag extends ComponentLogicTag
{
	private static final long serialVersionUID = 3257850991142318897L;
	
	private String html = null;
    private boolean showInPublishedMode = false;
    
    //Använda
    private Integer contentId = null;
    
    
    private boolean showEditInline = true;
    private boolean showEditPopup = true;
    private boolean showChooseArticle = true;
    private boolean showCreateNewArticle = true;
    private boolean showPublishArticle = true;
    private boolean showTranslateArticle = true;

    public int doEndTag() throws JspException
    {
        if(this.getController().getOperatingMode().intValue() != 3 || showInPublishedMode)
        {
	    	StringBuffer sb = new StringBuffer();
	        
	    	try
	    	{
	    		String componentEditorUrl = CmsPropertyHandler.getComponentEditorUrl();
		    	String returnAddress = "" + componentEditorUrl + "ViewInlineOperationMessages.action";
		    	String originalUrl = URLEncoder.encode(this.getController().getOriginalFullURL(), "iso-8859-1");
		    	System.out.println("componentEditorUrl:" + componentEditorUrl);
		    	
		    	String metaDataUrl 			= componentEditorUrl + "ViewAndCreateContentForServiceBinding.action?siteNodeId=" + this.getController().getSiteNodeId() + "&repositoryId=" + this.getController().getSiteNode().getRepositoryId() + "&asiteNodeVersionId=2109&changeStateToWorking=true"; //&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String createSiteNodeUrl 	= componentEditorUrl + "CreateSiteNode!inputV3.action?isBranch=true&repositoryId=" + this.getController().getSiteNode().getRepositoryId() + "&parentSiteNodeId=" + this.getController().getSiteNodeId() + "&languageId=" + this.getController().getLanguageId() + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&originalAddress=" + URLEncoder.encode(this.getController().getCurrentPageUrl(), "utf-8"); // + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String contentVersionUrl 	= componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchorName=contentVersionBlock"; //&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String categoriesUrl 		= componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchor=categoriesBlock"; //&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String publishUrl 			= componentEditorUrl + "ViewListSiteNodeVersion!v3.action?siteNodeId=" + this.getController().getSiteNodeId() + "&repositoryId=" + this.getController().getSiteNode().getRepositoryId() + "&recurseSiteNodes=false&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&originalAddress=" + URLEncoder.encode(this.getController().getCurrentPageUrl(), "utf-8"); // + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String notifyUrl 			= componentEditorUrl + "CreateEmail!inputChooseRecipientsV3.action?originalUrl=" + originalUrl + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&extraTextProperty=tool.managementtool.createEmailNotificationPageExtraText.text"; //"&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String subscriptionUrl 		= componentEditorUrl + "Subscriptions!input.action?interceptionPointCategory=Content&entityName=" + Content.class.getName() + "&entityId=" + this.contentId + "&extraParameters=" + this.contentId + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8"); // + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String pageSubscriptionUrl 	= componentEditorUrl + "Subscriptions!input.action?interceptionPointCategory=SiteNodeVersion&entityName=" + SiteNode.class.getName() + "&entityId=" + this.getController().getSiteNodeId() + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8"); // + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String newsFlowUrl 			= componentEditorUrl + "ViewMyDesktopToolStartPage!startWorkflow.action?workflowName=Skapa+nyhet&finalReturnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + ""; //"&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String mySettingsUrl 		= componentEditorUrl + "ViewMySettings.action"; //"&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    			    	
		    	sb.append("<p id='igMenuButton" + getComponentId() + "'><a class='igButton' href=\"#\" onclick=\"showIGMenu('editOnSightDiv" + getComponentId() + "', event);\"><span class='igButtonOuterSpan'><span class='linkInfoGlueFunctions'>Administration</span></span></a></p>");
		    	
		    	sb.append("<div id=\"editOnSightDiv" + getComponentId() + "\" class=\"editOnSightMenuDiv\" style=\"padding: 0px; margin: 0px; padding-top: 0; min-width: 240px; position: absolute; top: 20px; display: none; background-color: white; border: 1px solid #555;\">");

		    	sb.append("    <ul class='editOnSightUL'>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style-type:none;'><a href=\"javascript:openInlineDiv('" + metaDataUrl + "', 700, 750, true);\" class=\"editOnSightHref linkMetadata\">Ändra sidans metadata</a></li>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style-type:none;'><a href=\"javascript:openInlineDiv('" + createSiteNodeUrl + "', 700, 750, true);\" class=\"editOnSightHref linkCreatePage\">Skapa undersida till nuvarande</a></li>");

		    	if(contentId != null)
		    	{
			    	sb.append("    <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style-type:none;'><a href=\"javascript:editInline(" + this.getController().getSiteNode().getRepositoryId() + ", " + this.contentId + ", " + this.getController().getLanguageId() + ", true);\" class=\"editOnSightHref linkEditArticle\">Redigera artikeln i sidan</a></li>");
			    	sb.append("    <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style-type:none;'><a href=\"javascript:openInlineDiv('" + contentVersionUrl + "', 700, 750, true);\" class=\"editOnSightHref linkEditArticle\">Redigera artikel</a></li>");
			    	sb.append("    <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style-type:none;'><a href=\"javascript:openInlineDiv('" + categoriesUrl + "', 700, 750, true);\" class=\"editOnSightHref linkCategorizeArticle\">Kategorisera artikel</a></li>");
		    	}
			    sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style-type:none;'><a href=\"javascript:openInlineDiv('" + publishUrl + "', 700, 750, true);\" class=\"editOnSightHref linkPublish\">Publicera</a></li>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style-type:none;'><a href=\"javascript:openInlineDiv('" + notifyUrl + "', 700, 750, true);\" class=\"editOnSightHref linkNotify\">Notifiera</a></li>");
		    	
		    	if(contentId != null)
		    	{
		    		sb.append("    <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style-type:none;'><a href=\"javascript:openInlineDiv('" + subscriptionUrl + "', 700, 750, true);\" class=\"editOnSightHref linkTakeContent\">Prenumerera på innehållet</a></li>");
		    	}
		    	
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style-type:none;'><a href=\"javascript:openInlineDiv('" + pageSubscriptionUrl + "', 700, 750, true);\" class=\"editOnSightHref linkTakePage\">Prenumerera på sidan</a></li>");
		    			    	
		    	ContentVersionVO contentVersionVO = this.getController().getContentVersion(contentId, this.getController().getLanguageId(), true);
		    	if(contentVersionVO != null)
		    	{
			    	System.out.println("Current contentVersionVO:" + contentVersionVO.getLanguageName() + ":" + contentVersionVO.getLanguageId());
			    	List languages = this.getController().getPageLanguages();
			    	
			    	Iterator languagesIterator = languages.iterator();
			    	while(languagesIterator.hasNext())
			    	{
			    		LanguageVO languageVO = (LanguageVO)languagesIterator.next();
			    		if(!contentVersionVO.getLanguageId().equals(languageVO.getId()))
			    		{
				    		String translateUrl = componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + languageVO.getLanguageId() + "&anchorName=contentVersionBlock&translate=true&fromLanguageId=" + contentVersionVO.getLanguageId() + "&toLanguageId=" + languageVO.getId(); // + "&KeepThis=true&TB_iframe=true&height=700&width=1000&modal=true";
							
					    	sb.append("	<li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style-type:none;'>");
					    	sb.append("    	<a href=\"javascript:openInlineDiv('" + translateUrl + "', 700, 1000, true);\" class=\"editOnSightHref linkTranslate\">Översätt till &quot;" + languageVO.getLocalizedDisplayLanguage() + "&quot;</a>");
					    	sb.append(" </li>");
			    		}
			    	}
		    	}
		    	
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style-type:none;'><a href=\"javascript:openInlineDiv('" + newsFlowUrl + "', 700, 750, true);\" class=\"editOnSightHref linkCreateNews\">Skapa nyhet om denna artikel</a></li>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style-type:none;'><a href=\"javascript:openInlineDiv('" + mySettingsUrl + "', 700, 750, true);\" class=\"editOnSightHref linkMySettings\">Mina inställningar</a></li>");
		    	sb.append("    </ul>");

		    	sb.append("</div>");
	    	
				
		        produceResult(sb.toString());
	    	}
	    	catch (Exception e) 
	    	{
	    		e.printStackTrace();
			}
        }
        
        html = null;
        contentId = null;
        
        return EVAL_PAGE;
    }

    /*
    public int doEndTag() throws JspException
    {
        if(this.getController().getOperatingMode().intValue() != 3 || showInPublishedMode)
        {
	    	StringBuffer sb = new StringBuffer();
	        
	    	try
	    	{
	    		String componentEditorUrl = CmsPropertyHandler.getComponentEditorUrl();
		    	String returnAddress = "" + componentEditorUrl + "ViewInlineOperationMessages.action";
		    	String originalUrl = URLEncoder.encode(this.getController().getOriginalFullURL(), "iso-8859-1");
		    	System.out.println("componentEditorUrl:" + componentEditorUrl);
		    	
		    	String metaDataUrl 			= componentEditorUrl + "ViewAndCreateContentForServiceBinding.action?siteNodeId=" + this.getController().getSiteNodeId() + "&repositoryId=" + this.getController().getSiteNode().getRepositoryId() + "&asiteNodeVersionId=2109&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String createSiteNodeUrl 	= componentEditorUrl + "CreateSiteNode!inputV3.action?repositoryId=" + this.getController().getSiteNode().getRepositoryId() + "&parentSiteNodeId=" + this.getController().getSiteNodeId() + "&languageId=" + this.getController().getLanguageId() + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&originalAddress=" + URLEncoder.encode(this.getController().getCurrentPageUrl(), "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String contentVersionUrl 	= componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchorName=contentVersionBlock&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String categoriesUrl 		= componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchor=categoriesBlock&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String publishUrl 			= componentEditorUrl + "ViewListSiteNodeVersion.action?siteNodeId=" + this.getController().getSiteNodeId() + "&repositoryId=" + this.getController().getSiteNode().getRepositoryId() + "&recurseSiteNodes=false&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String notifyUrl 			= componentEditorUrl + "CreateEmail!inputChooseRecipientsV3.action?originalUrl=" + originalUrl + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String subscriptionUrl 		= componentEditorUrl + "Subscriptions!input.action?interceptionPointCategory=Content&entityName=" + Content.class.getName() + "&entityId=" + this.contentId + "&extraParameters=" + this.contentId + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String pageSubscriptionUrl 	= componentEditorUrl + "Subscriptions!input.action?interceptionPointCategory=SiteNodeVersion&entityName=" + SiteNode.class.getName() + "&entityId=" + this.getController().getSiteNodeId() + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    	String newsFlowUrl 			= componentEditorUrl + "ViewMyDesktopToolStartPage!startWorkflow.action?workflowName=Skapa+nyhet&finalReturnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true";
		    			    	
	    		sb.append("<script type=\"text/javascript\" src=\"script/jquery/jquery-1.2.3.min.js\"></script>");
		    	sb.append("<script type=\"text/javascript\" src=\"script/jqueryplugins/thickbox/thickbox-compressed.js\"></script>");
		    	sb.append("<style type=\"text/css\" media=\"all\">");
		    	sb.append("	@import \"script/jqueryplugins/thickbox/thickbox.css\";");
		    	sb.append("</style>");

		    	sb.append("<script type=\"text/javascript\" src=\"script/jqueryplugins/menu/jquery.menu.js\"></script>");
		    	sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"script/jqueryplugins/menu/style.css\" />");
		    	
		    	sb.append("<script type='text/javascript'>\n");
		    	sb.append("function openDiv(url) { \n");
		    	
				//sb.append("		alert('you clicked item \"' + url + '\"');\n");
		    	sb.append("		tb_show('Redigera', url, 'Redigera');\n");

		    	sb.append("} \n");
		    	sb.append("$(document).ready(function(){\n");
		    	sb.append("		var options = {minWidth: 260, arrowSrc: 'script/jqueryplugins/menu/arrow_right.gif', copyClassAttr: true, onClick: function(e, menuItem){ $.Menu.closeAll(); } };\n");
		    	//sb.append("		$('#igMenuButton" + this.getComponentLogic().getInfoGlueComponent().getId() + ">a').menu(options, '#editOnSightDiv" + this.getComponentLogic().getInfoGlueComponent().getId() + "');\n");
		    	sb.append("		$('#editOnSightDiv" + this.getComponentLogic().getInfoGlueComponent().getId() + "').menu(options);\n");
		    	sb.append("});\n");
		    	sb.append("</script>\n");
		    		
		    	//sb.append("<p id='igMenuButton" + this.getComponentLogic().getInfoGlueComponent().getId() + "'><a class='igButton' href='#'><span class='igButtonOuterSpan'><span class='linkInfoGlueFunctions'>InfoGlue&nbsp;actions</span></span></a></p>");
		    	
		    	sb.append("<div style=\"min-width: 260px;\" id=\"editOnSightDiv" + this.getComponentLogic().getInfoGlueComponent().getId() + "\">");

		    	sb.append("<a class='igButton' href='#'><span class='igButtonOuterSpan'><span class='linkInfoGlueFunctions'>InfoGlue&nbsp;actions</span></span></a>");
		    	
		    	sb.append("    <ul>");
		    	sb.append("        <li><a href=\"javascript:openDiv('" + metaDataUrl + "');\" class=\"editOnSightHref linkMetadata\">Ändra sidans metadata</a></li>");
		    	sb.append("        <li><a href=\"javascript:openDiv('" + createSiteNodeUrl + "');\" class=\"editOnSightHref linkCreatePage\">Skapa undersida till nuvarande</a></li>");

		    	if(contentId != null)
		    	{
			    	sb.append("        <li><a href=\"javascript:editInline(" + this.getController().getSiteNode().getRepositoryId() + ", " + this.contentId + ", " + this.getController().getLanguageId() + ", true);\" class=\"editOnSightHref linkEditArticle\">Redigera artikeln i sidan</a></li>");
			    	sb.append("        <li><a href=\"javascript:openDiv('" + contentVersionUrl + "');\" class=\"editOnSightHref linkEditArticle\">Redigera artikel</a></li>");
			    	sb.append("        <li><a href=\"javascript:openDiv('" + categoriesUrl + "');\" class=\"editOnSightHref linkCategorizeArticle\">Kategorisera artikel</a></li>");
		    	}
			    sb.append("        <li><a href=\"javascript:openDiv('" + publishUrl + "');\" class=\"editOnSightHref linkPublish\">Publicera</a></li>");
		    	sb.append("        <li><a href=\"javascript:openDiv('" + notifyUrl + "');\" class=\"editOnSightHref linkNotify\">Notifiera</a></li>");
		    	
		    	if(contentId != null)
		    	{
		    		sb.append("        <li><a href=\"javascript:openDiv('" + subscriptionUrl + "');\" class=\"editOnSightHref linkTakeContent\">Prenumerera på innehållet</a></li>");
		    	}
		    	
		    	sb.append("        <li><a href=\"javascript:openDiv('" + pageSubscriptionUrl + "');\" class=\"editOnSightHref linkTakePage\">Prenumerera på sidan</a></li>");
		    	//sb.append("        <li><a href=\"javascript:openDiv('" + translateUrl + "');\" class=\"editOnSightHref linkTranslate\">Översätt</a></li>");

		    	sb.append("        <li id=\"choice1\"><a href=\"javascript:void();\" class=\"editOnSightHref linkTranslate\">Översätt till</a>");
		    	sb.append("        	<ul>");
		    	
		    	ContentVersionVO contentVersionVO = this.getController().getContentVersion(contentId, this.getController().getLanguageId(), true);
		    	System.out.println("Current contentVersionVO:" + contentVersionVO.getLanguageName() + ":" + contentVersionVO.getLanguageId());
		    	List languages = this.getController().getPageLanguages();
		    	
		    	Iterator languagesIterator = languages.iterator();
		    	while(languagesIterator.hasNext())
		    	{
		    		LanguageVO languageVO = (LanguageVO)languagesIterator.next();
		    		if(!contentVersionVO.getLanguageId().equals(languageVO.getId()))
		    		{
			    		String translateUrl = componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + languageVO.getLanguageId() + "&anchorName=contentVersionBlock&translate=true&fromLanguageId=" + contentVersionVO.getLanguageId() + "&toLanguageId=" + languageVO.getId() + "&KeepThis=true&TB_iframe=true&height=700&width=1000&modal=true";
						
				    	sb.append("        		<li>");
				    	sb.append("        			<a href=\"javascript:openDiv('" + translateUrl + "');\" class=\"editOnSightHref linkTranslate\">" + languageVO.getLocalizedDisplayLanguage() + "</a>");
				    	sb.append("        		</li>");
		    		}
		    	}

		    	sb.append("        </ul>");
		    	sb.append("        </li>");
			
		    	sb.append("        <li><a href=\"javascript:openDiv('" + newsFlowUrl + "');\" class=\"editOnSightHref linkCreateNews\">Skapa nyhet om denna artikel</a></li>");
		    	sb.append("    </ul>");

		    	sb.append("</div>");
	    	
				
		        produceResult(sb.toString());
	    	}
	    	catch (Exception e) 
	    	{
	    		e.printStackTrace();
			}
	        
	    	//produceResult(this.getController().getEditOnSightTag(propertyName, createNew, html, showInPublishedMode, showDecorated, extraParameters));
        }
        
        html = null;
        contentId = null;
        
        return EVAL_PAGE;
    }
*/
    /*
    public int doEndTag() throws JspException
    {
        if(this.getController().getOperatingMode().intValue() != 3 || showInPublishedMode)
        {
	    	StringBuffer sb = new StringBuffer();
	        
	    	try
	    	{
	    		String componentEditorUrl = CmsPropertyHandler.getComponentEditorUrl();
		    	String returnAddress = "" + componentEditorUrl + "ViewInlineOperationMessages.action?returnAddress=" + URLEncoder.encode(this.getController().getCurrentPageUrl(), "utf-8");
		    	String extraText = URLEncoder.encode("<a href='" + this.getController().getOriginalFullURL() + "'>Klicka här för att komma till sidan</a>", "iso-8859-1");
		    	System.out.println("componentEditorUrl:" + componentEditorUrl);
		    	
	    		sb.append("<script type=\"text/javascript\" src=\"script/jquery/jquery-1.2.3.min.js\"></script>");
		    	sb.append("<script type=\"text/javascript\" src=\"script/jqueryplugins/thickbox/thickbox-compressed.js\"></script>");
		    	sb.append("<style type=\"text/css\" media=\"all\">");
		    	sb.append("	@import \"script/jqueryplugins/thickbox/thickbox.css\";");
		    	sb.append("</style>");

		    	sb.append("<script type=\"text/javascript\" src=\"script/jqueryplugins/menu/jquery.menu.js\"></script>");
		    	sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"script/jqueryplugins/menu/style.css\" />");
		    	
		    	sb.append("<script type='text/javascript'>\n");
		    	sb.append("$(document).ready(function(){\n");
		    	sb.append("		var options = {minWidth: 120, arrowSrc: 'script/jqueryplugins/menu/arrow_right.gif', copyClassAttr: true};\n");
		    	sb.append("		$('#editOnSightDiv" + this.getComponentLogic().getInfoGlueComponent().getId() + "').menu(options);\n");
		    	sb.append("});\n");
		    	sb.append("</script>\n");
		    					
		    	sb.append("<div id=\"editOnSightDiv" + this.getComponentLogic().getInfoGlueComponent().getId() + "\" class=\"popup_menu\" astyle=\"position: absolute; display: none; background-color: white;\">");
		    	//sb.append("<div id=\"editOnSightDiv" + this.getComponentLogic().getInfoGlueComponent().getId() + "\" style=\"display:none;\">");
		    		
		        sb.append("InfoGlue&nbsp;actions&nbsp;");
		    	//sb.append("<span id=\"editOnSightButton" + this.getComponentLogic().getInfoGlueComponent().getId() + "\" class=\"editOnSightMenuButton\">InfoGlue&nbsp;actions&nbsp;</span>");
		    	
		    	sb.append("    <ul id=\"menufivelist\" aclass=\"popupMenuLinks\" astyle='margin: 0px; margin-right: 0px; padding: 0px; padding-right: 0px;'>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "ViewAndCreateContentForServiceBinding.action?siteNodeId=1131&repositoryId=47&siteNodeVersionId=2109&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"linkMetadata thickbox\" rel=\"metaInfo\">Ändra sidans metadata</a></li>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "CreateSiteNode!inputV3.action?repositoryId=47&parentSiteNodeId=" + this.getController().getSiteNodeId() + "&languageId=" + this.getController().getLanguageId() + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"linkCreatePage thickbox\" rel=\"subpage\">Skapa undersida till nuvarande</a></li>");

		    	if(contentId != null)
		    	{
			    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchorName=contentVersionBlock&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"linkEditArticle thickbox\" rel=\"editContent\">Redigera artikel</a></li>");
			    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchor=categoriesBlock&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"linkCategorizeArticle thickbox\" rel=\"categorize\">Kategorisera artikel</a></li>");
		    	}
			    sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "ViewListSiteNodeVersion.action?siteNodeId=" + this.getController().getSiteNodeId() + "&repositoryId=" + this.getController().getSiteNode().getRepositoryId() + "&recurseSiteNodes=false&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"linkPublish thickbox\" rel=\"publish\">Publicera</a></li>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "CreateEmail!inputChooseRecipientsV3.action?extraText=" + extraText + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"linkNotify thickbox\" rel=\"notifications\">Notifiera</a></li>");
		    	
		    	if(contentId != null)
		    	{
		    		sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "Subscriptions!input.action?interceptionPointCategory=Content&entityName=" + Content.class.getName() + "&entityId=" + this.contentId + "&extraParameters=" + this.contentId + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"linkTakeContent thickbox\" rel=\"subscribe\">Prenumerera på innehållet</a></li>");
		    	}
		    	
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "Subscriptions!input.action?interceptionPointCategory=SiteNodeVersion&entityName=" + SiteNode.class.getName() + "&entityId=" + this.getController().getSiteNodeId() + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"linkTakePage thickbox\">Prenumerera på sidan</a></li>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchorName=contentVersionBlock&translate=true&fromLanguageId=3&toLanguageId=1&KeepThis=true&TB_iframe=true&height=700&width=1000&modal=true\" class=\"linkTranslate thickbox\" rel=\"editContent\">Översätt</a></li>");

		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;' id=\"choice1\"><a href=\"#\" class=\"linkTranslate\">Översätt</a>");
		    	sb.append("        	<ul>");
		    	sb.append("        		<li>");
		    	sb.append("        			<a href=\"javascript: alert('Engelska');\">Till Engelska</a>");
		    	sb.append("        		</li>");
		    	sb.append("        		<li>");
		    	sb.append("        			<a href=\"javascript: alert('Franska');\">Till Franska</a>");
		    	sb.append("        		</li>");
		    	sb.append("        </ul>");
		    	sb.append("        </li>");
			
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"#\" class=\"linkCreateNews\">Skapa nyhet om denna artikel</a></li>");
		    	sb.append("    </ul>");

		    	sb.append("</div>");
	    	
				
		        produceResult(sb.toString());
	    	}
	    	catch (Exception e) 
	    	{
	    		e.printStackTrace();
			}
	        
	    	//produceResult(this.getController().getEditOnSightTag(propertyName, createNew, html, showInPublishedMode, showDecorated, extraParameters));
        }
        
        html = null;
        contentId = null;
        
        return EVAL_PAGE;
    }

     */
    public void setHtml(final String html) throws JspException
    {
        this.html = evaluateString("EditOnSightMenuTag", "html", html);
    }

    public void setShowInPublishedMode(boolean showInPublishedMode)
    {
        this.showInPublishedMode = showInPublishedMode;
    }

	public void setContentId(final String contentId) throws JspException
	{
        this.contentId = evaluateInteger("EditOnSightMenuTag", "contentId", contentId);
	}
    
}