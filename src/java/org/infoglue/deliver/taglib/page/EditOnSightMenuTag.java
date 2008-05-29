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
		    	String returnAddress = "" + componentEditorUrl + "ViewInlineOperationMessages.action?returnAddress=" + URLEncoder.encode(this.getController().getCurrentPageUrl(), "utf-8");
		    	String extraText = URLEncoder.encode("<a href='" + this.getController().getOriginalFullURL() + "'>Klicka här för att komma till sidan</a>", "iso-8859-1");
		    	System.out.println("componentEditorUrl:" + componentEditorUrl);
		    	
	    		sb.append("<script type=\"text/javascript\" src=\"script/jquery/jquery-1.2.3.min.js\"></script>");
		    	sb.append("<script type=\"text/javascript\" src=\"script/jqueryplugins/thickbox/thickbox-compressed.js\"></script>");
		    	sb.append("<style type=\"text/css\" media=\"all\">");
		    	sb.append("	@import \"script/jqueryplugins/thickbox/thickbox.css\";");
		    	sb.append("</style>");

		    	sb.append("<div id=\"editOnSightDiv" + this.getComponentLogic().getInfoGlueComponent().getId() + "\" class=\"popup_menu\" style=\"position: absolute; display: none; background-color: white;\">");

		    	sb.append("    <ul class=\"popupMenuLinks\" style='margin: 0px; margin-right: 0px; padding: 0px; padding-right: 0px;'>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "ViewAndCreateContentForServiceBinding.action?siteNodeId=1131&repositoryId=47&siteNodeVersionId=2109&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"editOnSightImageLink linkMetadata thickbox\" rel=\"metaInfo\">Ändra sidans metadata</a></li>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "CreateSiteNode!inputV3.action?repositoryId=47&parentSiteNodeId=" + this.getController().getSiteNodeId() + "&languageId=" + this.getController().getLanguageId() + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"editOnSightImageLink linkCreatePage thickbox\" rel=\"subpage\">Skapa undersida till nuvarande</a></li>");

		    	if(contentId != null)
		    	{
			    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchorName=contentVersionBlock&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"editOnSightImageLink linkEditArticle thickbox\" rel=\"editContent\">Redigera artikel</a></li>");
			    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "ViewContentVersion!standalone.action?contentId=" + this.contentId + "&languageId=" + getController().getLanguageId() + "&anchor=categoriesBlock&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"editOnSightImageLink linkCategorizeArticle thickbox\" rel=\"categorize\">Kategorisera artikel</a></li>");
		    	}
			    sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "ViewListSiteNodeVersion.action?siteNodeId=" + this.getController().getSiteNodeId() + "&repositoryId=" + this.getController().getSiteNode().getRepositoryId() + "&recurseSiteNodes=false&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"editOnSightImageLink linkPublish thickbox\" rel=\"publish\">Publicera</a></li>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "CreateEmail!inputChooseRecipientsV3.action?extraText=" + extraText + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"editOnSightImageLink linkNotify thickbox\" rel=\"notifications\">Notifiera</a></li>");
		    	
		    	if(contentId != null)
		    	{
		    		sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "Subscriptions!input.action?interceptionPointCategory=Content&entityName=" + Content.class.getName() + "&entityId=" + this.contentId + "&extraParameters=" + this.contentId + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"editOnSightImageLink linkTakeContent thickbox\" rel=\"subscribe\">Prenumerera på innehållet</a></li>");
		    	}
		    	
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"" + componentEditorUrl + "Subscriptions!input.action?interceptionPointCategory=Content&entityName=" + SiteNode.class.getName() + "&entityId=" + this.getController().getSiteNodeId() + "&returnAddress=" + URLEncoder.encode(returnAddress, "utf-8") + "&KeepThis=true&TB_iframe=true&height=700&width=750&modal=true\" class=\"editOnSightImageLink linkTakePage thickbox\">Prenumerera på sidan</a></li>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"#\" class=\"linkTranslate\">Översätt</a></li>");
		    	sb.append("        <li style='margin: 0px; margin-left: 4px; padding: 2px 0px 2px 2px; list-style: none;'><a href=\"#\" class=\"linkCreateNews\">Skapa nyhet om denna artikel</a></li>");
		    	sb.append("    </ul>");

		    	sb.append("</div>");

		        sb.append("<a id=\"editOnSightButton" + this.getComponentLogic().getInfoGlueComponent().getId() + "\" href=\"javascript:positionDivAtElement('editOnSightDiv" + this.getComponentLogic().getInfoGlueComponent().getId() + "', 'editOnSightButton" + this.getComponentLogic().getInfoGlueComponent().getId() + "'); openCloseDiv('editOnSightDiv" + this.getComponentLogic().getInfoGlueComponent().getId() + "');\" class=\"editOnSightMenuButton\">InfoGlue&nbsp;actions&nbsp;</a>");
	    	
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