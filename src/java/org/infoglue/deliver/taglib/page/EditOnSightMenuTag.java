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

import javax.servlet.jsp.JspException;

import org.infoglue.cms.entities.content.Content;
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
    
    //Anv�nda
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
		    	String returnAddress = "" + componentEditorUrl + "ViewInlineOperationMessages.action?returnAddress=" + URLEncoder.encode(this.getController().getCurrentPageUrl(), "utf-8");
		    	String extraText = URLEncoder.encode("<a href='" + this.getController().getOriginalFullURL() + "'>Klicka h�r f�r att komma till sidan</a>", "iso-8859-1");
		    	System.out.println("componentEditorUrl:" + componentEditorUrl);
		    	
	    		sb.append("<script type=\"text/javascript\" src=\"script/jquery/jquery-1.2.3.min.js\"></script>");
		    	sb.append("<script type=\"text/javascript\" src=\"script/jqueryplugins/thickbox/thickbox-compressed.js\"></script>");
		    	sb.append("<style type=\"text/css\" media=\"all\">");
		    	sb.append("	@import \"script/jqueryplugins/thickbox/thickbox.css\";");
		    	sb.append("</style>");

		    	sb.append("<script type=\"text/javascript\" src=\"script/jqueryplugins/menu/jquery.menu.js\"></script>");
		    	sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"script/jqueryplugins/menu/style.css\" />");
		    	
		    	sb.append("<script type='text/javascript'>\n");
		    	sb.append("function openDiv(menuItem, text, url) { \n");
		    	sb.append("		var metaDataUrl 		= '" + componentEditorUrl + "ViewAndCreateContentForServiceBinding.action?siteNodeId=1131&repositoryId=47&siteNodeVersionId=2109&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true';");
		    	sb.append("		var createSiteNodeUrl 	= '" + componentEditorUrl + "CreateSiteNode!inputV3.action?repositoryId=47&parentSiteNodeId=" + this.getController().getSiteNodeId() + "&languageId=" + this.getController().getLanguageId() + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true';");
		    	sb.append("		var contentVersionUrl 	= '" + componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchorName=contentVersionBlock&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true';");
		    	sb.append("		var categoriesUrl 		= '" + componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchor=categoriesBlock&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true';");
		    	sb.append("		var publishUrl 			= '" + componentEditorUrl + "ViewListSiteNodeVersion.action?siteNodeId=" + this.getController().getSiteNodeId() + "&repositoryId=" + this.getController().getSiteNode().getRepositoryId() + "&recurseSiteNodes=false&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true';");
		    	sb.append("		var notifyUrl 			= '" + componentEditorUrl + "CreateEmail!inputChooseRecipientsV3.action?extraText=" + extraText + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true';");
		    	sb.append("		var subscriptionUrl 	= '" + componentEditorUrl + "Subscriptions!input.action?interceptionPointCategory=Content&entityName=" + Content.class.getName() + "&entityId=" + this.contentId + "&extraParameters=" + this.contentId + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true';");
		    	sb.append("		var pageSubscriptionUrl = '" + componentEditorUrl + "Subscriptions!input.action?interceptionPointCategory=SiteNodeVersion&entityName=" + SiteNode.class.getName() + "&entityId=" + this.getController().getSiteNodeId() + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true';");
		    	sb.append("		var translateUrl 		= '" + componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchorName=contentVersionBlock&translate=true&fromLanguageId=3&toLanguageId=1&KeepThis=true&TB_iframe=true&height=700&width=1000&modal=true';");
		    	
		    	sb.append("		alert('you clicked item \"' + text + url + '\"');\n");
		    	sb.append("		if(text == '�ndra sidans metadata') {\n");
		    	sb.append("			tb_show('Redigera', metaDataUrl, 'Redigera');\n");
		    	sb.append("		} \n");
		    	sb.append("		else if(text == 'Skapa undersida till nuvarande') {\n");
		    	sb.append("			tb_show('Subpage', createSiteNodeUrl, 'Subpage');\n");
		    	sb.append("		} \n");
		    	sb.append("		else if(text == 'Redigera artikel') {\n");
		    	sb.append("			tb_show('Redigera', contentVersionUrl, 'Redigera');\n");
		    	sb.append("		} \n");
		    	sb.append("} \n");
		    	sb.append("$(document).ready(function(){\n");
		    	sb.append("		var options = {minWidth: 120, arrowSrc: 'script/jqueryplugins/menu/arrow_right.gif', copyClassAttr: true, onClick: function(e, menuItem){ openDiv('', $(this).text(), $(this).src); }};\n");
		    		
		    	sb.append("		$('#editOnSightDiv" + this.getComponentLogic().getInfoGlueComponent().getId() + "').menu(options);\n");
		    	sb.append("});\n");
		    	sb.append("</script>\n");
		    					
		    	sb.append("<div id=\"editOnSightDiv" + this.getComponentLogic().getInfoGlueComponent().getId() + "\">");
		    		
		        sb.append("InfoGlue&nbsp;actions");
		    	
		    	sb.append("    <ul>");
		    	sb.append("        <li><a href='#' class=\"editOnSightHref linkMetadata thickbox\" rel=\"metaInfo\">�ndra sidans metadata</a></li>");
		    	sb.append("        <li><a href='#' class=\"editOnSightHref linkCreatePage thickbox\" rel=\"subpage\">Skapa undersida till nuvarande</a></li>");

		    	if(contentId != null)
		    	{
			    	sb.append("        <li><a href=\"#\" class=\"editOnSightHref linkEditArticle thickbox\" rel=\"editContent\">Redigera artikel</a></li>");
			    	sb.append("        <li><a href=\"#\" class=\"editOnSightHref linkCategorizeArticle thickbox\" rel=\"categorize\">Kategorisera artikel</a></li>");
		    	}
			    sb.append("        <li><a href=\"#\" class=\"editOnSightHref linkPublish thickbox\" rel=\"publish\">Publicera</a></li>");
		    	sb.append("        <li><a href=\"#\" class=\"editOnSightHref linkNotify thickbox\" rel=\"notifications\">Notifiera</a></li>");
		    	
		    	if(contentId != null)
		    	{
		    		sb.append("        <li><a href=\"#\" class=\"editOnSightHref linkTakeContent thickbox\" rel=\"subscribe\">Prenumerera p� inneh�llet</a></li>");
		    	}
		    	
		    	sb.append("        <li><a href=\"#\" class=\"editOnSightHref linkTakePage thickbox\">Prenumerera p� sidan</a></li>");
		    	sb.append("        <li><a href=\"#\" class=\"editOnSightHref linkTranslate thickbox\" rel=\"editContent\">�vers�tt</a></li>");

		    	sb.append("        <li id=\"choice1\"><a href=\"#\" class=\"editOnSightHref linkTranslate thickbox\">�vers�tt</a>");
		    	sb.append("        	<ul>");
		    	sb.append("        		<li>");
		    	sb.append("        			<a href=\"#\" class=\"editOnSightHref linkTranslate thickbox\">Till Engelska</a>");
		    	sb.append("        		</li>");
		    	sb.append("        		<li>");
		    	sb.append("        			<a href=\"#\" class=\"editOnSightHref linkTranslate thickbox\">Till Franska</a>");
		    	sb.append("        		</li>");
		    	sb.append("        </ul>");
		    	sb.append("        </li>");
			
		    	sb.append("        <li><a href=\"#\" class=\"editOnSightHref linkCreateNews\">Skapa nyhet om denna artikel</a></li>");
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
		    	String extraText = URLEncoder.encode("<a href='" + this.getController().getOriginalFullURL() + "'>Klicka h�r f�r att komma till sidan</a>", "iso-8859-1");
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
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "ViewAndCreateContentForServiceBinding.action?siteNodeId=1131&repositoryId=47&siteNodeVersionId=2109&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"linkMetadata thickbox\" rel=\"metaInfo\">�ndra sidans metadata</a></li>");
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
		    		sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "Subscriptions!input.action?interceptionPointCategory=Content&entityName=" + Content.class.getName() + "&entityId=" + this.contentId + "&extraParameters=" + this.contentId + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"linkTakeContent thickbox\" rel=\"subscribe\">Prenumerera p� inneh�llet</a></li>");
		    	}
		    	
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "Subscriptions!input.action?interceptionPointCategory=SiteNodeVersion&entityName=" + SiteNode.class.getName() + "&entityId=" + this.getController().getSiteNodeId() + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"linkTakePage thickbox\">Prenumerera p� sidan</a></li>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchorName=contentVersionBlock&translate=true&fromLanguageId=3&toLanguageId=1&KeepThis=true&TB_iframe=true&height=700&width=1000&modal=true\" class=\"linkTranslate thickbox\" rel=\"editContent\">�vers�tt</a></li>");

		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;' id=\"choice1\"><a href=\"#\" class=\"linkTranslate\">�vers�tt</a>");
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