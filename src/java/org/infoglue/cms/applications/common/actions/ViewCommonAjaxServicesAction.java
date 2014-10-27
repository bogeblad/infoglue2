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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.databeans.ProcessBean;
import org.infoglue.cms.applications.databeans.ReferenceBean;
import org.infoglue.cms.applications.databeans.ReferenceVersionBean;
import org.infoglue.cms.applications.structuretool.actions.ViewListSiteNodeVersionAction;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentCategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.EventController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.PublicationController;
import org.infoglue.cms.controllers.kernel.impl.simple.RegistryController;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeVersionController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.publishing.PublicationVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.entities.structure.SiteNodeVersion;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.entities.workflow.EventVO;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.util.HttpHelper;

/**
 * This class implements the action class for the framed page in the content tool.
 * 
 * @author Mattias Bogeblad  
 */

public class ViewCommonAjaxServicesAction extends InfoGlueAbstractAction
{
    private final static Logger logger = Logger.getLogger(ViewCommonAjaxServicesAction.class.getName());

	private static final long serialVersionUID = 1L;


	public String doContentPath() throws Exception
    {
		String contentPath = ContentController.getContentController().getContentPath(new Integer(getRequest().getParameter("contentId")), false, true);
		this.getResponse().setContentType("text/plain");
		this.getResponse().getWriter().print("" + contentPath);
		
		return NONE;
    }

	public String doAssetWeight() throws Exception
    {
		VisualFormatter formatter = new VisualFormatter();

		Map<Integer,Long> sizes = ContentController.getContentController().getContentWeight(new Integer(getRequest().getParameter("contentId")), true);
		Long totalSize = 0L;
		StringBuffer sb = new StringBuffer();
		
		for(Integer id : sizes.keySet())
		{
			totalSize = totalSize + sizes.get(id);
			if(sizes.get(id) > 100000)
			{
				String contentPath = ContentController.getContentController().getContentPath(id, false, true);
				sb.append("<br/>" + contentPath + "=" + formatter.formatFileSize(sizes.get(id)));
			}
		}
		
		this.getResponse().setContentType("text/plain");
		this.getResponse().getWriter().print("" + formatter.formatFileSize(totalSize) + ":" + sb.toString());
		
		return NONE;
    }
	
	public String doHeaviestContents() throws Exception
    {
		VisualFormatter formatter = new VisualFormatter();

		Map<Integer,Long> sizes = ContentController.getContentController().getHeaviestContents();
		Long totalSize = 0L;
		StringBuffer sb = new StringBuffer();
		
		for(Integer id : sizes.keySet())
		{
			totalSize = totalSize + sizes.get(id);
			if(sizes.get(id) > 100000)
			{
				String contentPath = ContentController.getContentController().getContentPath(id, false, true);
				sb.append("<br/><a href='ViewArchiveTool!cleanOldVersionsForContent.action?contentId=" + id + "&recurse=true' target='_blank'>" + contentPath + " (" + formatter.formatFileSizeWithDecimals(sizes.get(id)) + ")</a>");
			}
		}
		
		this.getResponse().setContentType("text/plain");
		this.getResponse().getWriter().print("" + formatter.formatFileSize(totalSize) + ":" + sb.toString());
		
		return NONE;
    }
	
	
	public String doExecute() throws Exception
    {
		
        return "success";
    }

}
