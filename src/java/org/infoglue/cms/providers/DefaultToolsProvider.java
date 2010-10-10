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

package org.infoglue.cms.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.infoglue.cms.applications.databeans.InfoglueTool;
import org.infoglue.cms.controllers.kernel.impl.simple.AccessRightController;
import org.infoglue.cms.controllers.kernel.impl.simple.LabelController;
import org.infoglue.cms.security.InfoGluePrincipal;

public class DefaultToolsProvider implements ToolsProvider 
{

	@Override
	public List<InfoglueTool> getTools(InfoGluePrincipal principal, Locale locale) 
	{
		List<InfoglueTool> tools = new ArrayList<InfoglueTool>();
		
		//AccessRightController.getController()
		
		tools.add(new InfoglueTool("structure", "ViewStructureTool.action", "", LabelController.getController(locale).getLocalizedString(locale, "tool.common.structureTool.name"), "A place to manage your sites", "structure", ""));
		tools.add(new InfoglueTool("content", "ViewStructureTool.action", "", LabelController.getController(locale).getLocalizedString(locale, "tool.common.contentTool.name"), "A place to manage your sites", "content", ""));
		tools.add(new InfoglueTool("management", "ViewStructureTool.action", "", LabelController.getController(locale).getLocalizedString(locale, "tool.common.managementTool.name"), "A place to manage your sites", "management", ""));
		tools.add(new InfoglueTool("mydesktop", "ViewStructureTool.action", "", LabelController.getController(locale).getLocalizedString(locale, "tool.common.myDesktopTool.name"), "A place to manage your sites", "mydesktop", ""));
		tools.add(new InfoglueTool("publishing", "ViewStructureTool.action", "", LabelController.getController(locale).getLocalizedString(locale, "tool.common.publishingTool.name"), "A place to manage your sites", "publishing", ""));
		tools.add(new InfoglueTool("formeditor", "ViewStructureTool.action", "", "Form editor", "A place to manage your sites", "formeditor", ""));
		
		/*
		#if($this.hasAccessTo("StructureTool.Read"))
			<li id="structureMarkupLink"><a href="#" class="structure" onclick="return activateTool('structureMarkup', '$ui.getString('tool.common.structureTool.name')', '$ui.getString("tool.common.adminTool.header")', true, '$ui.getString("tool.common.pageTabLabelPrefix")');">$ui.getString('tool.common.structureTool.name')</a></li>
			#end
			#if($this.hasAccessTo("ContentTool.Read"))
			<li id="contentMarkupLink"><a href="#" class="content" onclick="return activateTool('contentMarkup', '$ui.getString('tool.common.contentTool.name')', '$ui.getString("tool.common.adminTool.header")', true, '$ui.getString("tool.common.contentTabLabelPrefix")');">$ui.getString('tool.common.contentTool.name')</a></li>
			#end
			#if($this.hasAccessTo("ManagementTool.Read"))
			<li id="managementMarkupLink"><a href="#" class="management" onclick="return activateTool('managementMarkup', '$ui.getString('tool.common.managementTool.name')', '$ui.getString("tool.common.adminTool.header")', true, '$ui.getString("tool.common.managementTabLabelPrefix")');">$ui.getString('tool.common.managementTool.name')</a></li>
			#end
			#if($this.hasAccessTo("PublishingTool.Read"))
			<li id="publishingMarkupLink"><a href="#" class="publishing" onclick="return activateTool('publishingMarkup', '$ui.getString('tool.common.publishingTool.name')', '$ui.getString("tool.common.adminTool.header")', true, '$ui.getString("tool.common.publishingTabLabelPrefix")');">$ui.getString('tool.common.publishingTool.name')</a></li>
			#end
			#if($this.hasAccessTo("MyDesktopTool.Read"))
			<li id="mydesktopMarkupLink"><a href="#" class="mydesktop" onclick="return activateTool('mydesktopMarkup', '$ui.getString('tool.common.myDesktopTool.name')', '$ui.getString("tool.common.adminTool.header")', true, '$ui.getString("tool.common.mydesktopTabLabelPrefix")');">$ui.getString('tool.common.myDesktopTool.name')</a></li>
			#end
			#if($this.hasAccessTo("FormEditor.Read", true))
			<li id="formeditorMarkupLink"><a href="#" class="formeditor" onclick="window.open('/infoglueDeliverWorking/formeditor','Form editor','');">$ui.getString('tool.common.formEditorTool.name')</a></li>
			#end
		*/	
		return tools;
	}

}
