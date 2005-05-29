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

package org.infoglue.cms.applications.managementtool.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.RegistryController;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.entities.content.Content;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.content.impl.simple.ContentImpl;
import org.infoglue.cms.entities.management.Language;
import org.infoglue.cms.entities.management.Repository;
import org.infoglue.cms.entities.management.impl.simple.InfoGlueExportImpl;
import org.infoglue.cms.entities.structure.SiteNode;
import org.infoglue.cms.entities.structure.SiteNodeVersion;
import org.infoglue.cms.entities.structure.impl.simple.SiteNodeImpl;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;


/**
 * This class handles Exporting of a repository to an XML-file.
 * 
 * @author mattias
 */

public class RebuildRegistryAction extends WebworkAbstractAction
{
	private Integer repositoryId = null;
	
	private String fileUrl 	= "";
	private String fileName = "";

	/**
	 * This shows the dialog before export.
	 * @return
	 * @throws Exception
	 */	

	public String doInput() throws Exception
	{
		return "input";
	}
	
	/**
	 * This handles the actual exporting.
	 */
	
	protected String doExecute() throws Exception 
	{
	    RegistryController registryController = RegistryController.getController();
		
		Database db = CastorDatabaseService.getDatabase();
		
		try 
		{
			db.begin();

			
			//Checks the relations from sitenodes
			List siteNodes = SiteNodeController.getController().getRepositorySiteNodes(this.repositoryId, db);
			
			Iterator siteNodesIterator = siteNodes.iterator();
			while(siteNodesIterator.hasNext())
			{
			    SiteNode siteNode = (SiteNode)siteNodesIterator.next();
			    CmsLogger.logInfo("Going to index all versions of " + siteNode.getName());
			    
			    Iterator siteNodeVersionsIterator = siteNode.getSiteNodeVersions().iterator();
				while(siteNodeVersionsIterator.hasNext())
				{
				    SiteNodeVersion siteNodeVersion = (SiteNodeVersion)siteNodeVersionsIterator.next();
				    registryController.updateSiteNodeVersion(siteNodeVersion, db);
				}
			}

			//Checks the relations from contents
			List languages = LanguageController.getController().getLanguageList(this.repositoryId, db);
			List contents = ContentController.getContentController().getRepositoryContents(this.repositoryId, db);
			
			Iterator iterator = contents.iterator();
			while(iterator.hasNext())
			{
			    Content content = (Content)iterator.next();
			    CmsLogger.logInfo("Going to index all version of " + content.getName());
			    
			    Iterator versionsIterator = content.getContentVersions().iterator();
				while(versionsIterator.hasNext())
				{
				    ContentVersion contentVersion = (ContentVersion)versionsIterator.next();
				    registryController.updateContentVersion(contentVersion, db);
				}
			}
						
			db.commit();
			db.close();

		} 
		catch (Exception e) 
		{
			CmsLogger.logSevere("An error was found rebuilding the registry: " + e.getMessage(), e);
			db.rollback();
			db.close();
		}
		
		return "success";
	}


	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	public Integer getRepositoryId()
	{
		return repositoryId;
	}

}
