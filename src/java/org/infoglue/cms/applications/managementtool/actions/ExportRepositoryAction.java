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
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.AccessRightController;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.CategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.InterceptionPointController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeVersionController;
import org.infoglue.cms.entities.content.Content;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.DigitalAsset;
import org.infoglue.cms.entities.content.DigitalAssetVO;
import org.infoglue.cms.entities.content.impl.simple.ContentImpl;
import org.infoglue.cms.entities.content.impl.simple.ExportContentVersionImpl;
import org.infoglue.cms.entities.management.AccessRight;
import org.infoglue.cms.entities.management.InterceptionPointVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.management.Repository;
import org.infoglue.cms.entities.management.impl.simple.InfoGlueExportImpl;
import org.infoglue.cms.entities.structure.SiteNode;
import org.infoglue.cms.entities.structure.SiteNodeVersion;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.entities.structure.impl.simple.SiteNodeImpl;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.handlers.DigitalAssetBytesHandler;
import org.infoglue.deliver.util.CompressionHelper;
import org.infoglue.deliver.util.Timer;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;


/**
 * This class handles Exporting of a repository to an XML-file.
 * 
 * @author mattias
 */

public class ExportRepositoryAction extends InfoGlueAbstractAction
{
    private final static Logger logger = Logger.getLogger(ExportRepositoryAction.class.getName());

	private Integer repositoryId = null;
	private List repositories = new ArrayList();
	
	private String fileUrl 	= "";
	private String fileName = "";
	private String exportFileName = null;
	private String exportFormat = "2";
	private Boolean onlyPublishedVersions = false;
	private int assetMaxSize = -1;
	
	/**
	 * This shows the dialog before export.
	 * @return
	 * @throws Exception
	 */	

	public String doInput() throws Exception
	{
		repositories = RepositoryController.getController().getRepositoryVOList();
		
		return "input";
	}
	
	
	/**
	 * This handles the actual exporting.
	 */
	
	protected String doExecute() throws Exception 
	{
		String exportFormat = CmsPropertyHandler.getExportFormat();
		if(exportFormat.equalsIgnoreCase("3") || this.exportFormat.equals("3"))
		{
			logger.info("exportFormat:" + exportFormat);
			logger.info("this.exportFormat:" + this.exportFormat);
			return doExecuteV3();
		}
		
		Database db = CastorDatabaseService.getDatabase();
		
		try 
		{
			Mapping map = new Mapping();

			if(exportFormat.equalsIgnoreCase("2"))
			{
				logger.info("MappingFile:" + CastorDatabaseService.class.getResource("/xml_mapping_site_2.5.xml").toString());
				map.loadMapping(CastorDatabaseService.class.getResource("/xml_mapping_site_2.5.xml").toString());
			}
			else
			{
				logger.info("MappingFile:" + CastorDatabaseService.class.getResource("/xml_mapping_site.xml").toString());
				map.loadMapping(CastorDatabaseService.class.getResource("/xml_mapping_site.xml").toString());
			}
			
			// All ODMG database access requires a transaction
			db.begin();

			List<SiteNode> siteNodes = new ArrayList<SiteNode>();
			List<Content> contents = new ArrayList<Content>();
			Hashtable<String,String> allRepositoryProperties = new Hashtable<String,String>();
			Hashtable<String,String> allSiteNodeProperties = new Hashtable<String,String>();
			Hashtable<String,String> allContentProperties = new Hashtable<String,String>();
			List<AccessRight> allAccessRights = new ArrayList<AccessRight>();
			//List<AccessRight> allAccessRights = AccessRightController.getController().getAllAccessRightListForExportReadOnly(db);
			
			//TEST
			Map args = new HashMap();
		    args.put("globalKey", "infoglue");
		    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
		    //END TEST
			
			String names = "";
			String[] repositories = getRequest().getParameterValues("repositoryId");
			for(int i=0; i<repositories.length; i++)
			{
				Integer repositoryId = new Integer(repositories[i]);
				Repository repository 	= RepositoryController.getController().getRepositoryWithId(repositoryId, db);
				SiteNode siteNode 		= SiteNodeController.getController().getRootSiteNode(repositoryId, db);
				Content content 		= ContentController.getContentController().getRootContent(repositoryId, db);

			    InterceptionPointVO interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Repository.Read", db);
			    if(interceptionPointVO != null)
			    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), repository.getId().toString(), db));

			    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Repository.Write", db);
			    if(interceptionPointVO != null)
			    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), repository.getId().toString(), db));

			    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Repository.ReadForBinding", db);
			    if(interceptionPointVO != null)
			    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), repository.getId().toString(), db));

				getContentPropertiesAndAccessRights(ps, allContentProperties, allAccessRights, content, db);
				getSiteNodePropertiesAndAccessRights(ps, allSiteNodeProperties, allAccessRights, siteNode, db);
				
				siteNodes.add(siteNode);
				contents.add(content);
				names = names + "_" + repository.getName();
				allRepositoryProperties.putAll(getRepositoryProperties(ps, repositoryId));
			}
			
			List contentTypeDefinitions = ContentTypeDefinitionController.getController().getContentTypeDefinitionList(db);
			List categories = CategoryController.getController().getAllActiveCategories();
			
			InfoGlueExportImpl infoGlueExportImpl = new InfoGlueExportImpl();
			
			VisualFormatter visualFormatter = new VisualFormatter();
			names = new VisualFormatter().replaceNonAscii(names, '_');

			if(repositories.length > 2 || names.length() > 40)
				names = "" + repositories.length + "_repositories";
			
			String fileName = "Export_" + names + "_" + visualFormatter.formatDate(new Date(), "yyyy-MM-dd_HHmm") + ".xml";
			if(exportFileName != null && !exportFileName.equals(""))
				fileName = exportFileName;
			
			String filePath = CmsPropertyHandler.getDigitalAssetPath();
			String fileSystemName =  filePath + File.separator + fileName;
						
			fileUrl = CmsPropertyHandler.getWebServerAddress() + "/" + CmsPropertyHandler.getDigitalAssetBaseUrl() + "/" + fileName;
			this.fileName = fileName;
						
			String encoding = "UTF-8";
			File file = new File(fileSystemName);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
            Marshaller marshaller = new Marshaller(osw);
            marshaller.setMapping(map);
			marshaller.setEncoding(encoding);
			DigitalAssetBytesHandler.setMaxSize(assetMaxSize);

			infoGlueExportImpl.getRootContent().addAll(contents);
			infoGlueExportImpl.getRootSiteNode().addAll(siteNodes);
			
			infoGlueExportImpl.setContentTypeDefinitions(contentTypeDefinitions);
			infoGlueExportImpl.setCategories(categories);

			infoGlueExportImpl.setRepositoryProperties(allRepositoryProperties);
			infoGlueExportImpl.setContentProperties(allContentProperties);
			infoGlueExportImpl.setSiteNodeProperties(allSiteNodeProperties);
			infoGlueExportImpl.setAccessRights(allAccessRights);
			
			marshaller.marshal(infoGlueExportImpl);
			
			osw.flush();
			osw.close();
			
			db.rollback();
			db.close();

		} 
		catch (Exception e) 
		{
			logger.error("An error was found exporting a repository: " + e.getMessage(), e);
			db.rollback();
		}
		
		return "success";
	}

	/**
	 * This handles the actual exporting.
	 */
	
	protected String doExecuteV3() throws Exception 
	{
		Timer t = new Timer();
		
		VisualFormatter visualFormatter = new VisualFormatter();
		
		String folderName = CmsPropertyHandler.getDigitalAssetPath() + File.separator + "Export_" + visualFormatter.formatDate(new Date(), "yyyy-MM-dd_HHmm") + "Archive";
		File folder = new File(folderName);
		folder.mkdirs();

		Database db = CastorDatabaseService.getDatabase();
		
		try 
		{
			Mapping map = new Mapping();
			logger.info("MappingFile:" + CastorDatabaseService.class.getResource("/xml_mapping_site_3.0.xml").toString());
			map.loadMapping(CastorDatabaseService.class.getResource("/xml_mapping_site_3.0.xml").toString());
			
			// All ODMG database access requires a transaction
			db.begin();

			//List<SiteNode> siteNodes = new ArrayList<SiteNode>();
			//List<Content> contents = new ArrayList<Content>();
			Hashtable<String,String> allRepositoryProperties = new Hashtable<String,String>();
			Hashtable<String,String> allSiteNodeProperties = new Hashtable<String,String>();
			Hashtable<String,String> allContentProperties = new Hashtable<String,String>();
			List<AccessRight> allAccessRights = new ArrayList<AccessRight>();
			//List<AccessRight> allAccessRights = AccessRightController.getController().getAllAccessRightListForExportReadOnly(db);
			//List<SiteNode> allSiteNodes = new ArrayList<SiteNode>();
			//List<SiteNodeVersion> allSiteNodeVersions = new ArrayList<SiteNodeVersion>();
			//List<Content> allContents = new ArrayList<Content>();
			//List<ContentVersion> allContentVersions = new ArrayList<ContentVersion>();
			
			//TEST
			Map args = new HashMap();
		    args.put("globalKey", "infoglue");
		    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
		    //END TEST
			Collection keys = ps.getKeys();
			logger.info("keys:" + keys.size());
			
			String names = "";
			String[] repositories = getRequest().getParameterValues("repositoryId");
			
		    t.printElapsedTime("Intro done...");

			exportHeavyEntities(repositories, "Contents", folderName, assetMaxSize);
		    t.printElapsedTime("Contents done...");
			exportHeavyEntities(repositories, "ContentVersions", folderName, assetMaxSize);
		    t.printElapsedTime("ContentVersions done...");
			exportHeavyEntities(repositories, "SiteNodes", folderName, assetMaxSize);
		    t.printElapsedTime("SiteNodes done...");
			exportHeavyEntities(repositories, "SiteNodeVersions", folderName, assetMaxSize);
		    t.printElapsedTime("SiteNodeVersions done...");
			exportHeavyEntities(repositories, "DigitalAssets", folderName, assetMaxSize);
		    t.printElapsedTime("DigitalAssets done...");
			
		    List<Repository> repositoryList = new ArrayList<Repository>();

			for(int i=0; i<repositories.length; i++)
			{
				Integer repositoryId = new Integer(repositories[i]);
				Repository repository 	= RepositoryController.getController().getRepositoryWithId(repositoryId, db);
				logger.info("Read repo");
				
				/*
				SiteNode siteNode 		= SiteNodeController.getController().getRootSiteNode(repositoryId, db);
				logger.info("Read siteNode");
				Content content 		= ContentController.getContentController().getRootContent(repositoryId, db);
				logger.info("Read content");
				*/

				InterceptionPointVO interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Repository.Read", db);
			    if(interceptionPointVO != null)
			    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), repository.getId().toString(), db));
			    logger.info("Read allAccessRights 1");

			    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Repository.Write", db);
			    if(interceptionPointVO != null)
			    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), repository.getId().toString(), db));
			    logger.info("Read allAccessRights 2");

			    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Repository.ReadForBinding", db);
			    if(interceptionPointVO != null)
			    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), repository.getId().toString(), db));
			    logger.info("Read allAccessRights 3:" + allAccessRights.size());

			    allAccessRights.addAll(AccessRightController.getController().getContentAccessRightListOnlyReadOnly(repository.getId(), db));
			    logger.info("Read allAccessRights 4:" + allAccessRights.size());

			    allAccessRights.addAll(AccessRightController.getController().getSiteNodeAccessRightListOnlyReadOnly(repository.getId(), db));
			    logger.info("Read allAccessRights 5:" + allAccessRights.size());
				
				//getContentPropertiesAndAccessRights(ps, allContentProperties, allAccessRights, keys, content, db);
				//logger.info("getContentPropertiesAndAccessRights");
				//getSiteNodePropertiesAndAccessRights(ps, allSiteNodeProperties, allAccessRights, keys, siteNode, db);
				//logger.info("getSiteNodePropertiesAndAccessRights");
				
				//siteNodes.add(siteNode);
				//contents.add(content);
				names = names + "_" + repository.getName();
				allRepositoryProperties.putAll(getRepositoryProperties(ps, repositoryId));
				
				repositoryList.add(repository);
			}
			
			List languages = LanguageController.getController().getLanguageList(db);
			List contentTypeDefinitions = ContentTypeDefinitionController.getController().getContentTypeDefinitionList(db);
			logger.info("contentTypeDefinitions");
			List categories = CategoryController.getController().getAllActiveCategories();
			logger.info("categories");

			t.printElapsedTime("Rest done...");
			
			InfoGlueExportImpl infoGlueExportImpl = new InfoGlueExportImpl();
			
			names = new VisualFormatter().replaceNonAscii(names, '_');

			if(repositories.length > 2 || names.length() > 40)
				names = "" + repositories.length + "_repositories";
			
			String fileName = "ExportMain.xml";
			if(exportFileName != null && !exportFileName.equals(""))
				fileName = exportFileName;
			
			//String filePath = CmsPropertyHandler.getDigitalAssetPath();
			String fileSystemName =  folderName + File.separator + fileName;
			String archiveFileSystemName =  CmsPropertyHandler.getDigitalAssetPath() + File.separator + fileName;
						
			String encoding = "UTF-8";
			File file = new File(fileSystemName);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
            Marshaller marshaller = new Marshaller(osw);
            marshaller.setMapping(map);
			marshaller.setEncoding(encoding);
			marshaller.setValidation(false);
			DigitalAssetBytesHandler.setMaxSize(assetMaxSize);

		    t.printElapsedTime("Marshall begun...");

			//infoGlueExportImpl.getRootContent().addAll(contents);
			//infoGlueExportImpl.getRootSiteNode().addAll(siteNodes);
			
			logger.info("repositoryList:" + repositoryList.size());
			infoGlueExportImpl.setRepositories(repositoryList);
			infoGlueExportImpl.setLanguages(languages);
			infoGlueExportImpl.setContentTypeDefinitions(contentTypeDefinitions);
			infoGlueExportImpl.setCategories(categories);

			infoGlueExportImpl.setRepositoryProperties(allRepositoryProperties);
			infoGlueExportImpl.setContentProperties(allContentProperties);
			infoGlueExportImpl.setSiteNodeProperties(allSiteNodeProperties);
			infoGlueExportImpl.setAccessRights(allAccessRights);

			//infoGlueExportImpl.setContents(allContents);
			//infoGlueExportImpl.setContentVersions(allContentVersions);
			//infoGlueExportImpl.setSiteNodes(allSiteNodes);
			//infoGlueExportImpl.setSiteNodeVersions(allSiteNodeVersions);
			
			marshaller.marshal(infoGlueExportImpl);
		    t.printElapsedTime("Marshall done...");

			osw.flush();
			osw.close();
			
			db.rollback();
			db.close();

			//Here we zip the dir
			fileUrl = CmsPropertyHandler.getWebServerAddress() + "/" + CmsPropertyHandler.getDigitalAssetBaseUrl() + "/" + fileName + "_" + names + ".zip";
			this.fileName = fileName + "_" + names + ".zip";
			
			try 
			{ 
			    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(archiveFileSystemName + "_" + names + ".zip")); 
				CompressionHelper ch = new CompressionHelper();
				ch.zipFolder(folderName, zos);
			    zos.close(); 
			} 
			catch(Exception e) 
			{ 
				e.printStackTrace();
			} 
		    t.printElapsedTime("Archiving done...");
		} 
		catch (Exception e) 
		{
			logger.error("An error was found exporting a repository: " + e.getMessage(), e);
			db.rollback();
		}
		
		return "success";
	}

	
	public static void getContentPropertiesAndAccessRights(PropertySet ps, Hashtable<String, String> allContentProperties, List<AccessRight> allAccessRights, Content content, Database db) throws SystemException
	{
		String allowedContentTypeNames = ps.getString("content_" + content.getId() + "_allowedContentTypeNames");
		if ( allowedContentTypeNames != null && !allowedContentTypeNames.equals(""))
	    {
        	allContentProperties.put("content_" + content.getId() + "_allowedContentTypeNames", allowedContentTypeNames);
	    }

		if(ps.exists("content_" + content.getId() + "_defaultContentTypeName"))
			allContentProperties.put("content_" + content.getId() + "_defaultContentTypeName", "" + ps.getString("content_" + content.getId() + "_defaultContentTypeName"));
	    if(ps.exists("content_" + content.getId() + "_initialLanguageId"))
	    	allContentProperties.put("content_" + content.getId() + "_initialLanguageId", "" + ps.getString("content_" + content.getId() + "_initialLanguageId"));

	    InterceptionPointVO interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.Read", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.Write", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.Create", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.Delete", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.Move", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.SubmitToPublish", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));
	    
	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.ChangeAccessRights", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.CreateVersion", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("ContentVersion.Delete", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("ContentVersion.Write", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("ContentVersion.Read", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("ContentVersion.Publish", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));
        
        Iterator childContents = content.getChildren().iterator();
        while(childContents.hasNext())
        {
        	Content childContent = (Content)childContents.next();
        	getContentPropertiesAndAccessRights(ps, allContentProperties, allAccessRights, childContent, db);
        }
	}

	public static void getSiteNodePropertiesAndAccessRights(PropertySet ps, Hashtable<String, String> allSiteNodeProperties, List<AccessRight> allAccessRights, SiteNode siteNode, Database db) throws SystemException, Exception
	{
	    String disabledLanguagesString = "" + ps.getString("siteNode_" + siteNode.getId() + "_disabledLanguages");
	    String enabledLanguagesString = "" + ps.getString("siteNode_" + siteNode.getId() + "_enabledLanguages");

	    if(disabledLanguagesString != null && !disabledLanguagesString.equals("") && !disabledLanguagesString.equals("null"))
	    	allSiteNodeProperties.put("siteNode_" + siteNode.getId() + "_disabledLanguages", disabledLanguagesString);
	    if(enabledLanguagesString != null && !enabledLanguagesString.equals("") && !enabledLanguagesString.equals("null"))
		    allSiteNodeProperties.put("siteNode_" + siteNode.getId() + "_enabledLanguages", enabledLanguagesString);
        
        SiteNodeVersionVO latestSiteNodeVersionVO = SiteNodeVersionController.getController().getLatestActiveSiteNodeVersionVO(db, siteNode.getId());
        
        if(latestSiteNodeVersionVO != null)
        {
	        InterceptionPointVO interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.Read", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.Write", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.CreateSiteNode", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.DeleteSiteNode", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.MoveSiteNode", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.SubmitToPublish", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.ChangeAccessRights", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.Publish", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
        }
        
        Iterator childSiteNodes = siteNode.getChildSiteNodes().iterator();
        while(childSiteNodes.hasNext())
        {
        	SiteNode childSiteNode = (SiteNode)childSiteNodes.next();
        	getSiteNodePropertiesAndAccessRights(ps, allSiteNodeProperties, allAccessRights, childSiteNode, db);
        }
	}
	
	

	
	
	protected String exportHeavyEntities(String[] repositories, String type, String folderName, Integer assetMaxSize) throws Exception 
	{
		Mapping map = new Mapping();
		logger.info("MappingFile:" + CastorDatabaseService.class.getResource("/xml_mapping_site_3.0.xml").toString());
		map.loadMapping(CastorDatabaseService.class.getResource("/xml_mapping_site_3.0.xml").toString());
		
		// All ODMG database access requires a transaction
		for(int i=0; i<repositories.length; i++)
		{
			Database db = CastorDatabaseService.getDatabase();
			try 
			{
				db.begin();
				
				Integer repositoryId = new Integer(repositories[i]);
				Repository repository = RepositoryController.getController().getRepositoryWithId(repositoryId, db);
				logger.info("Read repo");

				List<SiteNode> allSiteNodes = new ArrayList<SiteNode>();
				List<SiteNodeVersion> allSiteNodeVersions = new ArrayList<SiteNodeVersion>();
				List<Content> allContents = new ArrayList<Content>();
				List<ExportContentVersionImpl> allContentVersions = new ArrayList<ExportContentVersionImpl>();
				List<DigitalAsset> allDigitalAssets = new ArrayList<DigitalAsset>();

				if(type.equals("Contents"))
				{
					List<Content> contents = ContentController.getContentController().getContentList(repositoryId, 0, 5000, db);
					while(contents.size() > 0)
					{
						allContents.addAll(contents);
						contents = ContentController.getContentController().getContentList(repositoryId, contents.get(contents.size()-1).getContentId(), 5000, db);
						System.out.print(".");
					}
					logger.info("Read all contents");
				}
				if(type.equals("ContentVersions"))
				{
					List<ExportContentVersionImpl> contentVersions = ContentVersionController.getContentVersionController().getContentVersionList(repositoryId, 0, 5000, onlyPublishedVersions, db);
					while(contentVersions.size() > 0)
					{
						allContentVersions.addAll(contentVersions);
						contentVersions = ContentVersionController.getContentVersionController().getContentVersionList(repositoryId, contentVersions.get(contentVersions.size()-1).getContentVersionId(), 5000, onlyPublishedVersions, db);
						System.out.print(".");
					}
					logger.info("Read all content versions");
				}

				if(type.equals("SiteNodes"))
				{
					List<SiteNode> siteNodes = SiteNodeController.getController().getSiteNodeList(repositoryId, 0, 5000, db);
					while(siteNodes.size() > 0)
					{
						allSiteNodes.addAll(siteNodes);
						siteNodes = SiteNodeController.getController().getSiteNodeList(repositoryId, siteNodes.get(siteNodes.size()-1).getSiteNodeId(), 5000, db);
						System.out.print(".");
					}
					logger.info("Read all siteNodes");
				}
				
				if(type.equals("SiteNodeVersions"))
				{
					List<SiteNodeVersion> siteNodeVersions = SiteNodeVersionController.getController().getSiteNodeVersionList(repositoryId, 0, 5000, onlyPublishedVersions, db);
					while(siteNodeVersions.size() > 0)
					{
						allSiteNodeVersions.addAll(siteNodeVersions);
						siteNodeVersions = SiteNodeVersionController.getController().getSiteNodeVersionList(repositoryId, siteNodeVersions.get(siteNodeVersions.size()-1).getSiteNodeVersionId(), 5000, onlyPublishedVersions, db);
						System.out.print(".");
					}
				}
				if(type.equals("DigitalAssets"))
				{
					List<DigitalAssetVO> assets = DigitalAssetController.getController().dumpDigitalAssetList(repositoryId, 0, 50, assetMaxSize, onlyPublishedVersions, folderName);
					while(assets.size() > 0)
					{
						//allDigitalAssets.addAll(assets);
						assets = DigitalAssetController.getController().dumpDigitalAssetList(repositoryId, assets.get(assets.size()-1).getDigitalAssetId(), 50, assetMaxSize, onlyPublishedVersions, folderName);
						System.out.print(".");
					}
				}
				
				if(!type.equals("DigitalAssets"))
				{
					InfoGlueExportImpl infoGlueExportImpl = new InfoGlueExportImpl();
					
					String name = new VisualFormatter().replaceNonAscii(repository.getName(), '_');
					
					String fileName = type + ".xml";
					if(exportFileName != null && !exportFileName.equals(""))
						fileName = exportFileName;
					
					String fileSystemName =  folderName + File.separator + fileName;
								
					String encoding = "UTF-8";
					File file = new File(fileSystemName);
		            FileOutputStream fos = new FileOutputStream(file);
		            OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
		            Marshaller marshaller = new Marshaller(osw);
		            marshaller.setMapping(map);
					marshaller.setEncoding(encoding);
					marshaller.setValidation(false);
					if(type.equals("ContentVersions"))
						DigitalAssetBytesHandler.setMaxSize(0);
					else
						DigitalAssetBytesHandler.setMaxSize(assetMaxSize);
							
					infoGlueExportImpl.setContents(allContents);
					infoGlueExportImpl.setContentVersions(allContentVersions);
					infoGlueExportImpl.setSiteNodes(allSiteNodes);
					infoGlueExportImpl.setSiteNodeVersions(allSiteNodeVersions);
					infoGlueExportImpl.setDigitalAssets(allDigitalAssets);
					
					marshaller.marshal(infoGlueExportImpl);
					
					osw.flush();
					osw.close();
				}
				
				db.rollback();
			} 
			catch (Exception e) 
			{
				logger.error("An error was found exporting a repository: " + e.getMessage(), e);
				db.rollback();
			}
			finally
			{
				db.close();
			}
		}
		
		return "success";
	}
	

	public static void getContentPropertiesAndAccessRights(PropertySet ps, Hashtable<String, String> allContentProperties, List<AccessRight> allAccessRights, Collection keys, Content content, Database db) throws SystemException
	{
		String allowedContentTypeNames = ps.getString("content_" + content.getId() + "_allowedContentTypeNames");
		if ( allowedContentTypeNames != null && !allowedContentTypeNames.equals(""))
	    {
        	allContentProperties.put("content_" + content.getId() + "_allowedContentTypeNames", allowedContentTypeNames);
	    }

		//if(ps.exists("content_" + content.getId() + "_defaultContentTypeName"))
		if(keys.contains("content_" + content.getId() + "_defaultContentTypeName"))
			allContentProperties.put("content_" + content.getId() + "_defaultContentTypeName", "" + ps.getString("content_" + content.getId() + "_defaultContentTypeName"));
	    //if(ps.exists("content_" + content.getId() + "_initialLanguageId"))
	    if(keys.contains("content_" + content.getId() + "_initialLanguageId"))
	    	allContentProperties.put("content_" + content.getId() + "_initialLanguageId", "" + ps.getString("content_" + content.getId() + "_initialLanguageId"));

	    /*
	    InterceptionPointVO interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.Read", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.Write", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.Create", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.Delete", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.Move", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.SubmitToPublish", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));
	    
	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.ChangeAccessRights", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("Content.CreateVersion", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("ContentVersion.Delete", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("ContentVersion.Write", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("ContentVersion.Read", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));

	    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("ContentVersion.Publish", db);
	    if(interceptionPointVO != null)
	    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), content.getId().toString(), db));
        */
	    
        Iterator childContents = content.getChildren().iterator();
        while(childContents.hasNext())
        {
        	Content childContent = (Content)childContents.next();
        	getContentPropertiesAndAccessRights(ps, allContentProperties, allAccessRights, keys, childContent, db);
        }
	}

	public static void getSiteNodePropertiesAndAccessRights(PropertySet ps, Hashtable<String, String> allSiteNodeProperties, List<AccessRight> allAccessRights, Collection keys, SiteNode siteNode, Database db) throws SystemException, Exception
	{
		if(keys.contains("siteNode_" + siteNode.getId() + "_disabledLanguages"))
		{
		    String disabledLanguagesString = "" + ps.getString("siteNode_" + siteNode.getId() + "_disabledLanguages");
			if(disabledLanguagesString != null && !disabledLanguagesString.equals("") && !disabledLanguagesString.equals("null"))
		    	allSiteNodeProperties.put("siteNode_" + siteNode.getId() + "_disabledLanguages", disabledLanguagesString);
		}   
		
		if(keys.contains("siteNode_" + siteNode.getId() + "_enabledLanguages"))
		{
			String enabledLanguagesString = "" + ps.getString("siteNode_" + siteNode.getId() + "_enabledLanguages");
		    if(enabledLanguagesString != null && !enabledLanguagesString.equals("") && !enabledLanguagesString.equals("null"))
			    allSiteNodeProperties.put("siteNode_" + siteNode.getId() + "_enabledLanguages", enabledLanguagesString);
		}
		
	    /*
        SiteNodeVersionVO latestSiteNodeVersionVO = SiteNodeVersionController.getController().getLatestActiveSiteNodeVersionVO(db, siteNode.getId());
        
        if(latestSiteNodeVersionVO != null)
        {
	        InterceptionPointVO interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.Read", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.Write", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.CreateSiteNode", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.DeleteSiteNode", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.MoveSiteNode", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.SubmitToPublish", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.ChangeAccessRights", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
	
		    interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName("SiteNodeVersion.Publish", db);
		    if(interceptionPointVO != null)
		    	allAccessRights.addAll(AccessRightController.getController().getAccessRightListOnlyReadOnly(interceptionPointVO.getId(), latestSiteNodeVersionVO.getId().toString(), db));
        }
        */
	    
        Iterator childSiteNodes = siteNode.getChildSiteNodes().iterator();
        while(childSiteNodes.hasNext())
        {
        	SiteNode childSiteNode = (SiteNode)childSiteNodes.next();
        	getSiteNodePropertiesAndAccessRights(ps, allSiteNodeProperties, allAccessRights, keys, childSiteNode, db);
        }
	}

	public static Hashtable<String,String> getRepositoryProperties(PropertySet ps, Integer repositoryId) throws Exception
	{
		Hashtable<String,String> properties = new Hashtable<String,String>();
			    
	    byte[] WYSIWYGConfigBytes = ps.getData("repository_" + repositoryId + "_WYSIWYGConfig");
	    if(WYSIWYGConfigBytes != null)
	    	properties.put("repository_" + repositoryId + "_WYSIWYGConfig", new String(WYSIWYGConfigBytes, "utf-8"));

	    byte[] StylesXMLBytes = ps.getData("repository_" + repositoryId + "_StylesXML");
	    if(StylesXMLBytes != null)
	    	properties.put("repository_" + repositoryId + "_StylesXML", new String(StylesXMLBytes, "utf-8"));

	    byte[] extraPropertiesBytes = ps.getData("repository_" + repositoryId + "_extraProperties");
	    if(extraPropertiesBytes != null)
	    	properties.put("repository_" + repositoryId + "_extraProperties", new String(extraPropertiesBytes, "utf-8"));
	    
	    if(ps.exists("repository_" + repositoryId + "_defaultFolderContentTypeName"))
	    	properties.put("repository_" + repositoryId + "_defaultFolderContentTypeName", "" + ps.getString("repository_" + repositoryId + "_defaultFolderContentTypeName"));
	    if(ps.exists("repository_" + repositoryId + "_defaultTemplateRepository"))
		    properties.put("repository_" + repositoryId + "_defaultTemplateRepository", "" + ps.getString("repository_" + repositoryId + "_defaultTemplateRepository"));
	    if(ps.exists("repository_" + repositoryId + "_parentRepository"))
		    properties.put("repository_" + repositoryId + "_parentRepository", "" + ps.getString("repository_" + repositoryId + "_parentRepository"));

		return properties;
	}

	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	public String getFileName()
	{
		return fileName;
	}

	public String getFileUrl()
	{
		return fileUrl;
	}

	public Integer getRepositoryId()
	{
		return repositoryId;
	}

	public List getRepositories() 
	{
		return repositories;
	}

	public String getExportFileName()
	{
		return exportFileName;
	}

	public void setExportFileName(String exportFileName)
	{
		this.exportFileName = exportFileName;
	}

	public int getAssetMaxSize()
	{
		return assetMaxSize;
	}

	public void setAssetMaxSize(int assetMaxSize)
	{
		this.assetMaxSize = assetMaxSize;
	}

	public String getExportFormat()
	{
		return exportFormat;
	}

	public void setExportFormat(String exportFormat)
	{
		this.exportFormat = exportFormat;
	}

	public Boolean getOnlyPublishedVersions()
	{
		return this.onlyPublishedVersions;
	}

	public void setOnlyPublishedVersions(Boolean onlyPublishedVersions)
	{
		this.onlyPublishedVersions = onlyPublishedVersions;
	}

}
