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
 *
 * $Id: RegistryController.java,v 1.2 2005/02/22 15:22:55 mattias Exp $
 */

package org.infoglue.cms.controllers.kernel.impl.simple;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;
import org.infoglue.cms.entities.content.Content;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.CategoryVO;
import org.infoglue.cms.entities.management.Registry;
import org.infoglue.cms.entities.management.RegistryVO;
import org.infoglue.cms.entities.management.impl.simple.RegistryImpl;
import org.infoglue.cms.entities.structure.SiteNode;
import org.infoglue.cms.entities.structure.SiteNodeVersion;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;

/**
 * The RegistryController manages the registry-parts of InfoGlue. 
 * The Registry is metadata for how things are related - especially to handle bindings and inline links etc
 * when looking them up in the model is to slow.
 *
 * @author Mattias Bogeblad
 */

public class RegistryController extends BaseController
{
	private static final RegistryController instance = new RegistryController();
	
	public static RegistryController getController()
	{ 
	    return instance; 
	}

	private RegistryController()
	{
	}
	
	/**
	 * This method creates a registry entity in the db.
	 * @param valueObject
	 * @return
	 * @throws ConstraintException
	 * @throws SystemException
	 */
    public RegistryVO create(RegistryVO valueObject, Database db) throws ConstraintException, SystemException, Exception
    {
        Registry registry = new RegistryImpl();
        registry.setValueObject(valueObject);
        db.create(registry);
        return registry.getValueObject();
    }     

    /**
     * This method updates a registry entry
     * @param vo
     * @return
     * @throws ConstraintException
     * @throws SystemException
     */
    public RegistryVO update(RegistryVO valueObject, Database db) throws ConstraintException, SystemException
    {
    	return (RegistryVO) updateEntity(RegistryImpl.class, (BaseEntityVO) valueObject, db);
    }    
    
	/**
	 * this method goes through all inline stuff and all relations if ordinary content 
	 * and all components and bindings if a metainfo.
	 * 
	 * @param contentVersionVO
	 * @throws SystemException
	 * @throws Exception
	 */
	
	public void updateContentVersion(ContentVersionVO contentVersionVO) throws ConstraintException, SystemException
	{
	    String versionValue = contentVersionVO.getVersionValue();
	    
	    Database db = CastorDatabaseService.getDatabase();
		
		try 
		{
			beginTransaction(db);
		
		    ContentVersion oldContentVersion = ContentVersionController.getContentVersionController().getContentVersionWithId(contentVersionVO.getContentVersionId(), db);
		    Content oldContent = oldContentVersion.getOwningContent();
		    
		    clearRegistryVOList(ContentVersion.class.getName(), oldContentVersion.getContentVersionId().toString(), db);
		    getInlineSiteNodes(oldContentVersion, versionValue, db);
		    getInlineContents(oldContentVersion, versionValue, db);
		    
		    if(oldContent.getContentTypeDefinition().getName().equalsIgnoreCase("Meta info"))
		    {
		        System.out.println("It was a meta info so lets check it for other stuff as well");
			    SiteNodeVersion siteNodeVersion = getSiteNodeVersion(oldContentVersion, db); 
			    
			    getComponents(siteNodeVersion, versionValue, db);
			    getComponentBindings(siteNodeVersion, versionValue, db);
		    }
		    
			commitTransaction(db);
		}
		catch (Exception e)		
		{
		    rollbackTransaction(db);
			throw new SystemException("An error occurred when we tried to fetch which sitenode uses a metainfo. Reason:" + e.getMessage(), e);			
		}
		
	}
	
	/**
	 * This method fetches all inline links from any text.
	 */
	
	public void getInlineSiteNodes(ContentVersion contentVersion, String versionValue, Database db) throws ConstraintException, SystemException, Exception
	{
	    Pattern pattern = Pattern.compile("\\$templateLogic\\.getPageUrl\\(.*?\\)");
	    Matcher matcher = pattern.matcher(versionValue);
	    while ( matcher.find() ) 
	    { 
	        String match = matcher.group();
	        System.out.println("Adding match to registry after some processing: " + match);
	        Integer siteNodeId;
	        
	        int siteNodeStartIndex = match.indexOf("(");
	        int siteNodeEndIndex = match.indexOf(",");
	        if(siteNodeStartIndex > 0 && siteNodeEndIndex > 0 && siteNodeEndIndex > siteNodeStartIndex)
	        {
	            siteNodeId = new Integer(match.substring(siteNodeStartIndex + 1, siteNodeEndIndex));
	            System.out.println("siteNodeId:" + siteNodeId);
	            RegistryVO registryVO = new RegistryVO();
	            registryVO.setEntityId(siteNodeId.toString());
	            registryVO.setEntityName(SiteNode.class.getName());
	            registryVO.setReferenceType(RegistryVO.INLINE_LINK);
	            registryVO.setReferencingEntityId(contentVersion.getContentVersionId().toString());
	            registryVO.setReferencingEntityName(ContentVersion.class.getName());
	            registryVO.setReferencingEntityCompletingId(contentVersion.getOwningContent().getContentId().toString());
	            registryVO.setReferencingEntityCompletingName(Content.class.getName());
	            
	            this.create(registryVO, db);
	        }
	    }
	}
	
	/**
	 * This method fetches all inline links from any text.
	 */
	
	public void getInlineContents(ContentVersion contentVersion, String versionValue, Database db) throws ConstraintException, SystemException, Exception
	{
	    Pattern pattern = Pattern.compile("\\$templateLogic\\.getInlineAssetUrl\\(.*?\\)");
	    Matcher matcher = pattern.matcher(versionValue);
	    while ( matcher.find() ) 
	    { 
	        String match = matcher.group();
	        System.out.println("Adding match to registry after some processing: " + match);
	        Integer contentId;
	        
	        int contentStartIndex = match.indexOf("(");
	        int contentEndIndex = match.indexOf(",");
	        if(contentStartIndex > 0 && contentEndIndex > 0 && contentEndIndex > contentStartIndex)
	        {
	            contentId = new Integer(match.substring(contentStartIndex + 1, contentEndIndex));
	            System.out.println("contentId:" + contentId);
	            
	            RegistryVO registryVO = new RegistryVO();
	            registryVO.setEntityId(contentId.toString());
	            registryVO.setEntityName(Content.class.getName());
	            registryVO.setReferenceType(RegistryVO.INLINE_ASSET);
	            registryVO.setReferencingEntityId(contentVersion.getContentVersionId().toString());
	            registryVO.setReferencingEntityName(ContentVersion.class.getName());
	            registryVO.setReferencingEntityCompletingId(contentVersion.getOwningContent().getContentId().toString());
	            registryVO.setReferencingEntityCompletingName(Content.class.getName());
	            
	            this.create(registryVO, db);
	        }
	    }
	}

	
	/**
	 * This method fetches all components and adds entries to the registry.
	 */
	
	public void getComponents(SiteNodeVersion siteNodeVersion, String versionValue, Database db) throws ConstraintException, SystemException, Exception
	{
	    Pattern pattern = Pattern.compile("contentId=\".*?\"");
	    Matcher matcher = pattern.matcher(versionValue);
	    while ( matcher.find() ) 
	    { 
	        String match = matcher.group();
	        System.out.println("Adding match to registry after some processing: " + match);
	        Integer contentId;
	        
	        int contentStartIndex = match.indexOf("\"");
	        int contentEndIndex = match.lastIndexOf("\"");
	        if(contentStartIndex > 0 && contentEndIndex > 0 && contentEndIndex > contentStartIndex)
	        {
	            contentId = new Integer(match.substring(contentStartIndex + 1, contentEndIndex));
	            System.out.println("contentId:" + contentId);
	            
	            RegistryVO registryVO = new RegistryVO();
	            registryVO.setEntityId(contentId.toString());
	            registryVO.setEntityName(Content.class.getName());
	            registryVO.setReferenceType(RegistryVO.PAGE_COMPONENT);
	            registryVO.setReferencingEntityId(siteNodeVersion.getSiteNodeVersionId().toString());
	            registryVO.setReferencingEntityName(SiteNodeVersion.class.getName());
	            registryVO.setReferencingEntityCompletingId(siteNodeVersion.getOwningSiteNode().getSiteNodeId().toString());
	            registryVO.setReferencingEntityCompletingName(SiteNode.class.getName());
	            
	            this.create(registryVO, db);
	        }
	    }
	}

	/**
	 * This method fetches all components and adds entries to the registry.
	 */

	public void getComponentBindings(SiteNodeVersion siteNodeVersion, String versionValue, Database db) throws ConstraintException, SystemException, Exception
	{
	    Pattern pattern = Pattern.compile("<binding entity=\".*?\" entityId=\".*?\">");
	    Matcher matcher = pattern.matcher(versionValue);
	    while ( matcher.find() ) 
	    { 
	        String match = matcher.group();
	        System.out.println("Adding match to registry after some processing: " + match);
	        String entityName;
	        String entityId;
	        
	        int entityNameStartIndex = match.indexOf("\"");
	        int entityNameEndIndex = match.indexOf("\"", entityNameStartIndex + 1);
	        System.out.println("entityNameStartIndex:" + entityNameStartIndex);
	        System.out.println("entityNameEndIndex:" + entityNameEndIndex);
	        if(entityNameStartIndex > 0 && entityNameEndIndex > 0 && entityNameEndIndex > entityNameStartIndex)
	        {
	            entityName = match.substring(entityNameStartIndex + 1, entityNameEndIndex);
	            System.out.println("entityName:" + entityName);

		        int entityIdStartIndex = match.indexOf("\"", entityNameEndIndex + 1);
		        int entityIdEndIndex = match.indexOf("\"", entityIdStartIndex + 1);
		        System.out.println("entityIdStartIndex:" + entityIdStartIndex);
		        System.out.println("entityIdEndIndex:" + entityIdEndIndex);
		        if(entityIdStartIndex > 0 && entityIdEndIndex > 0 && entityIdEndIndex > entityIdStartIndex)
		        {
		            entityId = match.substring(entityIdStartIndex + 1, entityIdEndIndex);
		            System.out.println("entityId:" + entityId);

		            RegistryVO registryVO = new RegistryVO();
	                registryVO.setEntityName(Content.class.getName());
		            registryVO.setEntityId(entityId);
		            registryVO.setReferenceType(RegistryVO.PAGE_COMPONENT_BINDING);
		            registryVO.setReferencingEntityId(siteNodeVersion.getSiteNodeVersionId().toString());
		            registryVO.setReferencingEntityName(SiteNodeVersion.class.getName());
		            registryVO.setReferencingEntityCompletingId(siteNodeVersion.getOwningSiteNode().getSiteNodeId().toString());
		            registryVO.setReferencingEntityCompletingName(SiteNode.class.getName());
		            
		            this.create(registryVO, db);
		        }
	        }
	    }
	}

	/**
	 * Implemented for BaseController
	 */
	public BaseEntityVO getNewVO()
	{
		return new CategoryVO();
	}

    /**
     * This method gets all referencing content versions
     * 
     * @param contentId
     * @return
     */
	
    public List getReferencingObjectsForContent(Integer contentId) throws SystemException
    {
        List referencingContentVersions = new ArrayList();
        
        List registryEntires = getMatchingRegistryVOList(Content.class.getName(), contentId.toString());
        Iterator registryEntiresIterator = registryEntires.iterator();
        while(registryEntiresIterator.hasNext())
        {
            RegistryVO registryVO = (RegistryVO)registryEntiresIterator.next();
            System.out.println("registryVO:" + registryVO.getReferencingEntityId() + ":" +  registryVO.getReferencingEntityCompletingId());
            if(registryVO.getReferencingEntityName().indexOf("Content") > -1)
            {
	            ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(new Integer(registryVO.getReferencingEntityId()));
	    		System.out.println("contentVersionVO:" + contentVersionVO.getContentVersionId());
	            referencingContentVersions.add(contentVersionVO);
            }
            else
            {
                SiteNodeVersionVO siteNodeVersionVO = SiteNodeVersionController.getController().getSiteNodeVersionVOWithId(new Integer(registryVO.getReferencingEntityId()));
	    		System.out.println("siteNodeVersionVO:" + siteNodeVersionVO.getSiteNodeVersionId());
	            referencingContentVersions.add(siteNodeVersionVO);
            }
        }

        return referencingContentVersions;
    }
    
    
    /**
     * This method gets all referencing sitenode versions
     * 
     * @param siteNodeId
     * @return
     */
	
    public List getReferencingObjectsForSiteNode(Integer siteNodeId) throws SystemException
    {
        List referencingSiteNodeVersions = new ArrayList();
        
        List registryEntires = getMatchingRegistryVOList(SiteNode.class.getName(), siteNodeId.toString());
        Iterator registryEntiresIterator = registryEntires.iterator();
        while(registryEntiresIterator.hasNext())
        {
            RegistryVO registryVO = (RegistryVO)registryEntiresIterator.next();
            SiteNodeVersionVO siteNodeVersionVO = SiteNodeVersionController.getController().getSiteNodeVersionVOWithId(new Integer(registryVO.getReferencingEntityId()));
            referencingSiteNodeVersions.add(siteNodeVersionVO);
        }

        return referencingSiteNodeVersions;
    }
    
	/**
	 * Gets matching references
	 */
	
	public List getMatchingRegistryVOList(String entityName, String entityId) throws SystemException, Bug
	{
	    List matchingRegistryVOList = new ArrayList();
	    
		Database db = CastorDatabaseService.getDatabase();
		
		try 
		{
			beginTransaction(db);
		
			OQLQuery oql = db.getOQLQuery("SELECT r FROM org.infoglue.cms.entities.management.impl.simple.RegistryImpl r WHERE r.entityName = $1 AND r.entityId = $2 ORDER BY r.registryId");
			oql.bind(entityName);
			oql.bind(entityId);
			
			QueryResults results = oql.execute(Database.ReadOnly);
			
			while (results.hasMore()) 
            {
                Registry registry = (Registry)results.next();
                RegistryVO registryVO = registry.getValueObject();
                
                matchingRegistryVOList.add(registryVO);
            }
            
			commitTransaction(db);
		}
		catch ( Exception e)		
		{
			throw new SystemException("An error occurred when we tried to fetch a list of roles in the repository. Reason:" + e.getMessage(), e);			
		}
		
		return matchingRegistryVOList;		
	}
	
	
	/**
	 * Gets matching references
	 */
	
	public List clearRegistryVOList(String referencingEntityName, String referencingEntityId, Database db) throws SystemException, Exception
	{
	    List matchingRegistryVOList = new ArrayList();
	    
		OQLQuery oql = db.getOQLQuery("SELECT r FROM org.infoglue.cms.entities.management.impl.simple.RegistryImpl r WHERE r.referencingEntityName = $1 AND r.referencingEntityId = $2 ORDER BY r.registryId");
		oql.bind(referencingEntityName);
		oql.bind(referencingEntityId);
		
		QueryResults results = oql.execute();
		
		while (results.hasMore()) 
        {
            Registry registry = (Registry)results.next();
            db.remove(registry);
        }
		
		return matchingRegistryVOList;		
	}
	
	
	
	/**
	 * Gets siteNodeVersion which uses the metainfo
	 */
	
	public SiteNodeVersion getSiteNodeVersion(ContentVersion contentVersion, Database db) throws SystemException, Exception
	{
	    SiteNodeVersion siteNodeVersion = null;
	    
	    OQLQuery oql = db.getOQLQuery("SELECT snv FROM org.infoglue.cms.entities.structure.impl.simple.SiteNodeVersionImpl snv WHERE snv.serviceBindings.bindingQualifyers.name = $1 AND snv.serviceBindings.bindingQualifyers.value = $2");
		oql.bind("contentId");
		oql.bind(contentVersion.getOwningContent().getId());
		
		QueryResults results = oql.execute();
		
		while (results.hasMore()) 
        {
		    siteNodeVersion = (SiteNodeVersion)results.next();
            System.out.println("siteNodeVersion:" + siteNodeVersion.getId());
        }
    	
		return siteNodeVersion;		
	}

}
