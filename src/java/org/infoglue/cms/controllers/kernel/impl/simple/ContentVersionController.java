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

package org.infoglue.cms.controllers.kernel.impl.simple;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.entities.kernel.*;
import org.infoglue.cms.entities.management.*;
import org.infoglue.cms.entities.management.impl.simple.*;
import org.infoglue.cms.entities.content.*;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.content.impl.simple.ContentVersionImpl;
import org.infoglue.cms.entities.content.impl.simple.ContentImpl;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.deliver.applications.actions.ViewPageAction;

import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.io.*;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.CDATASection;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Mattias Bogeblad
 *
 */

public class ContentVersionController extends BaseController 
{
	private static final ContentCategoryController contentCategoryController = ContentCategoryController.getController();

	/**
	 * Factory method to get object
	 */
	
	public static ContentVersionController getContentVersionController()
	{
		return new ContentVersionController();
	}

    public ContentVersionVO getContentVersionVOWithId(Integer contentVersionId) throws SystemException, Bug
    {
		return (ContentVersionVO) getVOWithId(ContentVersionImpl.class, contentVersionId);
    }

    public ContentVersion getContentVersionWithId(Integer contentVersionId, Database db) throws SystemException, Bug
    {
		return (ContentVersion) getObjectWithId(ContentVersionImpl.class, contentVersionId, db);
    }

    public ContentVersion getReadOnlyContentVersionWithId(Integer contentVersionId, Database db) throws SystemException, Bug
    {
		return (ContentVersion) getObjectWithIdAsReadOnly(ContentVersionImpl.class, contentVersionId, db);
    }

    public List getContentVersionVOList() throws SystemException, Bug
    {
        return getAllVOObjects(ContentVersionImpl.class, "contentVersionId");
    }

	/* Recursive methods to get all contentVersions of a given state
	 * under the specified parent content.
	 */ 
	
    public List getContentVersionVOWithParentRecursive(Integer contentId, Integer stateId) throws ConstraintException, SystemException
	{
		return getContentVersionVOWithParentRecursive(contentId, stateId, new ArrayList());
	}
	
	private List getContentVersionVOWithParentRecursive(Integer contentId, Integer stateId, List resultList) throws ConstraintException, SystemException
	{
		// Get the versions of this content.
		resultList.addAll(getLatestContentVersionVOWithParent(contentId, stateId));
		
		// Get the children of this content and do the recursion
		List childContentList = ContentController.getContentController().getContentChildrenVOList(contentId);
		Iterator cit = childContentList.iterator();
		while (cit.hasNext())
		{
			ContentVO contentVO = (ContentVO) cit.next();
			getContentVersionVOWithParentRecursive(contentVO.getId(), stateId, resultList);
		}
	
		return resultList;
	}

	public List getContentVersionVOWithParent(Integer contentId) throws SystemException, Bug
    {
        ArrayList resultList = new ArrayList();
    	Database db = CastorDatabaseService.getDatabase();
    	ContentVersionVO contentVersionVO = null;

        beginTransaction(db);

        try
        {
           
            OQLQuery oql = db.getOQLQuery( "SELECT cv FROM org.infoglue.cms.entities.content.impl.simple.ContentVersionImpl cv WHERE cv.owningContent.contentId = $1 ORDER BY cv.contentVersionId desc");
        	oql.bind(contentId);
        	
        	QueryResults results = oql.execute(Database.ReadOnly);
			
			while (results.hasMore()) 
            {
            	ContentVersion contentVersion = (ContentVersion)results.next();
            	CmsLogger.logInfo("found one:" + contentVersion.getValueObject());
            	contentVersionVO = contentVersion.getValueObject();
            	resultList.add(contentVersionVO);
            }
            
            commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
    	
		return resultList;
    }

	/**
	 * This method returns a list of active contentversions, and only one / language in the specified state
	 * 
	 * @param contentId The content to look for versions in
	 * @param stateId  The state of the versions
	 * @return A list of the latest versions matching the given state
	 * @throws SystemException
	 * @throws Bug
	 */

	public List getLatestContentVersionVOWithParent(Integer contentId, Integer stateId) throws SystemException, Bug
	{
		ArrayList resultList = new ArrayList();
		ArrayList langCheck = new ArrayList();
		
		Database db = CastorDatabaseService.getDatabase();
		ContentVersionVO contentVersionVO = null;

		beginTransaction(db);

		try
		{
           
			OQLQuery oql = db.getOQLQuery( "SELECT cv FROM org.infoglue.cms.entities.content.impl.simple.ContentVersionImpl cv WHERE cv.owningContent.contentId = $1 ORDER BY cv.contentVersionId desc");
			oql.bind(contentId);
			// oql.bind(stateId);
        	
			QueryResults results = oql.execute(Database.ReadOnly);

			/* Original with bug :) 			
			while (results.hasMore()) 
			{
				ContentVersion contentVersion = (ContentVersion)results.next();
				contentVersionVO = contentVersion.getValueObject();
				if (contentVersionVO.getIsActive().booleanValue() && contentVersionVO.getStateId().compareTo(stateId)==0)
					if (!langCheck.contains(contentVersionVO.getLanguageId()))
						resultList.add(contentVersionVO);

				langCheck.add(contentVersionVO.getLanguageId());
			}
			*/
			
			// New improved
			while (results.hasMore()) 
			{
				ContentVersion contentVersion = (ContentVersion)results.next();
				contentVersionVO = contentVersion.getValueObject();
				if (contentVersionVO.getIsActive().booleanValue())
				{
					if ( (contentVersionVO.getStateId().compareTo(stateId)==0) && 
					(!langCheck.contains(contentVersionVO.getLanguageId())))
						resultList.add(contentVersionVO);
	
					langCheck.add(contentVersionVO.getLanguageId());
				}
			}

            
			commitTransaction(db);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
    	
		return resultList;
	}
    
    
    /**
     * This method returns the latest active content version.
     */
    
   	public ContentVersionVO getLatestActiveContentVersionVO(Integer contentId, Integer languageId) throws SystemException, Bug
    {
    	Database db = CastorDatabaseService.getDatabase();
    	ContentVersionVO contentVersionVO = null;

        beginTransaction(db);

        try
        {
        	ContentVersion contentVersion = null;
        	
			contentVersion = getLatestActiveContentVersion(contentId, languageId, db);
            /*
            Collection contentVersions = content.getContentVersions();
            
            Iterator i = contentVersions.iterator();
            
            while(i.hasNext())
            {
            	ContentVersion currentContentVersion = (ContentVersion)i.next();
            	CmsLogger.logInfo("found one candidate:" + currentContentVersion.getValueObject());
				if(contentVersion == null || (currentContentVersion.getId().intValue() > contentVersion.getId().intValue()))
				{
					if(currentContentVersion.getIsActive().booleanValue() &&  currentContentVersion.getLanguage().getId().intValue() == languageId.intValue())
						contentVersion = currentContentVersion;
				}
            }
            */
            
            if(contentVersion != null)
	            contentVersionVO = contentVersion.getValueObject();
            
            commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
    	
		return contentVersionVO;
    }


   	/**
	 * This method returns the latest active content version.
	 */
    
	public ContentVersion getLatestActiveContentVersion(Integer contentId, Integer languageId, Database db) throws SystemException, Bug
	{
		ContentVersion contentVersion = null;
    	
    	Content content = ContentController.getContentController().getContentWithId(contentId, db);
    	CmsLogger.logInfo("contentId:" + contentId);
    	CmsLogger.logInfo("languageId:" + languageId);
    	CmsLogger.logInfo("content:" + content.getName());
		Collection contentVersions = content.getContentVersions();
		CmsLogger.logInfo("contentVersions:" + contentVersions.size());
        
		Iterator i = contentVersions.iterator();
        while(i.hasNext())
		{
			ContentVersion currentContentVersion = (ContentVersion)i.next();
			CmsLogger.logInfo("found one candidate:" + currentContentVersion.getValueObject());
			if(contentVersion == null || (currentContentVersion.getId().intValue() > contentVersion.getId().intValue()))
			{
				CmsLogger.logInfo("currentContentVersion:" + currentContentVersion.getIsActive());
				CmsLogger.logInfo("currentContentVersion:" + currentContentVersion.getLanguage().getId());
				if(currentContentVersion.getIsActive().booleanValue() &&  currentContentVersion.getLanguage().getId().intValue() == languageId.intValue())
					contentVersion = currentContentVersion;
			}
		}
        
		return contentVersion;
	}
    

	public ContentVersionVO getLatestContentVersionVO(Integer contentId, Integer languageId) throws SystemException, Bug
    {
    	Database db = CastorDatabaseService.getDatabase();
    	ContentVersionVO contentVersionVO = null;

        beginTransaction(db);

        try
        {
           
            OQLQuery oql = db.getOQLQuery( "SELECT cv FROM org.infoglue.cms.entities.content.impl.simple.ContentVersionImpl cv WHERE cv.owningContent.contentId = $1 AND cv.language.languageId = $2 ORDER BY cv.contentVersionId desc");
        	oql.bind(contentId);
        	oql.bind(languageId);
        	
        	QueryResults results = oql.execute(Database.ReadOnly);
			
			if (results.hasMore()) 
            {
            	ContentVersion contentVersion = (ContentVersion)results.next();
            	CmsLogger.logInfo("found one:" + contentVersion.getValueObject());
            	contentVersionVO = contentVersion.getValueObject();
            }
            
            commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
    	
        
		return contentVersionVO;
    }


	public ContentVersion getContentVersionWithId(Integer contentVersionId) throws SystemException, Bug
    {
    	Database db = CastorDatabaseService.getDatabase();
    	ContentVersion contentVersion = null;

        beginTransaction(db);

        try
        {
           	contentVersion = getContentVersionWithId(contentVersionId, db);
            
            commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
    	
		return contentVersion;
    }


	public ContentVersion getLatestContentVersion(Integer contentId, Integer languageId, Database db) throws SystemException, Bug, Exception
    {
        ContentVersion contentVersion = null;
        
        OQLQuery oql = db.getOQLQuery( "SELECT cv FROM org.infoglue.cms.entities.content.impl.simple.ContentVersionImpl cv WHERE cv.owningContent.contentId = $1 AND cv.language.languageId = $2 ORDER BY cv.contentVersionId desc");
    	oql.bind(contentId);
    	oql.bind(languageId);
    	
    	QueryResults results = oql.execute(Database.ReadOnly);
		
		if (results.hasMore()) 
        {
        	contentVersion = (ContentVersion)results.next();
        }
            
		return contentVersion;
    }

   	
	/**
	 * This method created a new contentVersion in the database.
	 */
	
    public ContentVersionVO create(Integer contentId, Integer languageId, ContentVersionVO contentVersionVO, Integer oldContentVersionId) throws ConstraintException, SystemException
    {
		Database db = CastorDatabaseService.getDatabase();
        ContentVersion contentVersion = null;

        beginTransaction(db);
		try
        {
			contentVersion = create(contentId, languageId, contentVersionVO, oldContentVersionId, db);
			commitTransaction(db);
		}
        catch(Exception e)
        {
        	CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
        
        return contentVersion.getValueObject();
    }     

	/**
	 * This method created a new contentVersion in the database. It also updates the owning content
	 * so it recognises the change. 
	 */
	
    public ContentVersion create(Integer contentId, Integer languageId, ContentVersionVO contentVersionVO, Integer oldContentVersionId, Database db) throws ConstraintException, SystemException, Exception
    {
		Content content   = ContentController.getContentController().getContentWithId(contentId, db);
    	Language language = LanguageController.getController().getLanguageWithId(languageId, db);
		return create(content, language, contentVersionVO, oldContentVersionId, db);
    }     
    
	/**
	 * This method created a new contentVersion in the database. It also updates the owning content
	 * so it recognises the change. 
	 */
	
    public ContentVersion create(Content content, Language language, ContentVersionVO contentVersionVO, Integer oldContentVersionId, Database db) throws ConstraintException, SystemException, Exception
    {
		ContentVersion contentVersion = new ContentVersionImpl();
		contentVersion.setLanguage((LanguageImpl)language);
		CmsLogger.logInfo("Content:" + content.getContentId() + ":" + db.isPersistent(content));
		contentVersion.setOwningContent((ContentImpl)content);
		
		if(oldContentVersionId != null)
			contentVersion.setDigitalAssets(getContentVersionWithId(oldContentVersionId, db).getDigitalAssets());
		
		contentVersion.setValueObject(contentVersionVO);
        db.create(contentVersion); 
		content.getContentVersions().add(contentVersion);
		
        return contentVersion;
    }     

	/**
	 * This method deletes an contentversion and notifies the owning content.
	 */
	
    public void delete(ContentVersionVO contentVersionVO) throws ConstraintException, SystemException
    {
    	Database db = CastorDatabaseService.getDatabase();
        beginTransaction(db);
		try
        {
			delete(contentVersionVO, db);
			commitTransaction(db);
		}
        catch(Exception e)
        {
        	CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
    }        
	
	/**
	 * This method deletes an contentversion and notifies the owning content.
	 */
	
    public void delete(ContentVersionVO contentVersionVO, Database db) throws ConstraintException, SystemException, Exception
    {
		ContentVersion contentVersion = getContentVersionWithId(contentVersionVO.getContentVersionId(), db);
		delete(contentVersion, db);
    }        

	/**
	 * This method deletes an contentversion and notifies the owning content.
	 */
	
 	public void delete(ContentVersion contentVersion, Database db) throws ConstraintException, SystemException, Exception
	{
		// Check if deleteable
		// Initial thought was to only allow working copies to be deleted, but after checking
		// rules for deleting content, the same rules is applied on this method for deleting
		// a single contentVersion. TODO: Consider only deleting content that has working copies.
		if (contentVersion.getStateId().intValue() == ContentVersionVO.PUBLISHED_STATE.intValue() && contentVersion.getIsActive().booleanValue() == true)
			throw new ConstraintException("ContentVersion.stateId", "3300");

		Content content = contentVersion.getOwningContent();

		content.getContentVersions().remove(contentVersion);
		db.remove(contentVersion);
		contentCategoryController.deleteByContentVersion(contentVersion.getId(), db);
	}
	



	/**
	 * This method deletes all contentVersions for the content sent in.
	 * The contentVersion is related to digital assets but we don't remove the asset itself in case 
	 * other versions or contents reference the same asset.
	 */
	
	public void deleteVersionsForContent(Content content, Database db) throws ConstraintException, SystemException, Bug, Exception
    {
        Collection contentVersions = Collections.synchronizedCollection(content.getContentVersions());
       	Iterator contentVersionIterator = contentVersions.iterator();
			
		while (contentVersionIterator.hasNext()) 
        {
        	ContentVersion contentVersion = (ContentVersion)contentVersionIterator.next();
			
        	Collection digitalAssetList = contentVersion.getDigitalAssets();
			Iterator assets = digitalAssetList.iterator();
			while (assets.hasNext()) 
            {
            	DigitalAsset digitalAsset = (DigitalAsset)assets.next();
				assets.remove();
				db.remove(digitalAsset);
			}
			
        	CmsLogger.logInfo("Deleting contentVersion:" + contentVersion.getContentVersionId());
        	contentVersionIterator.remove();
        	delete(contentVersion, db);
        }
        content.setContentVersions(new ArrayList());
    }


    /**
     * This method updates the contentversion.
     */
    
    public ContentVersionVO update(Integer contentId, Integer languageId, ContentVersionVO contentVersionVO) throws ConstraintException, SystemException
    {
    	ContentVersionVO realContentVersionVO = contentVersionVO;
		contentVersionVO.setModifiedDateTime(new Date());
		
    	if(contentVersionVO.getId() == null)
    	{
    		CmsLogger.logInfo("Creating the entity because there was no version at all for: " + contentId + " " + languageId);
    		realContentVersionVO = create(contentId, languageId, contentVersionVO, null);
    	}
    	
    	return (ContentVersionVO) updateEntity(ContentVersionImpl.class, realContentVersionVO);
    }        


	
	public ContentVersion getLatestPublishedContentVersion(Integer contentId, Integer languageId) throws SystemException, Bug, Exception
    {
        ContentVersion contentVersion = null;
        
        Database db = CastorDatabaseService.getDatabase();
        beginTransaction(db);
        try
        {        
	        OQLQuery oql = db.getOQLQuery( "SELECT cv FROM org.infoglue.cms.entities.content.impl.simple.ContentVersionImpl cv WHERE cv.owningContent.contentId = $1 AND cv.language.languageId = $2 AND cv.stateId = $3 AND cv.isActive = $4 ORDER BY cv.contentVersionId desc");
	    	oql.bind(contentId);
	    	oql.bind(languageId);
	    	oql.bind(ContentVersionVO.PUBLISHED_STATE);
	    	oql.bind(true);
	    	
	    	QueryResults results = oql.execute(Database.ReadOnly);
			
			if (results.hasMore()) 
	        {
	        	contentVersion = (ContentVersion)results.next();
	        }
			
            commitTransaction(db);            
        }
        catch(Exception e)
        {
        	CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
            
		return contentVersion;
    }



	public ContentVersion getLatestPublishedContentVersion(Integer contentId, Integer languageId, Database db) throws SystemException, Bug, Exception
    {
        ContentVersion contentVersion = null;
        
        OQLQuery oql = db.getOQLQuery( "SELECT cv FROM org.infoglue.cms.entities.content.impl.simple.ContentVersionImpl cv WHERE cv.owningContent.contentId = $1 AND cv.language.languageId = $2 AND cv.stateId = $3 AND cv.isActive = $4 ORDER BY cv.contentVersionId desc");
    	oql.bind(contentId);
    	oql.bind(languageId);
    	oql.bind(ContentVersionVO.PUBLISHED_STATE);
    	oql.bind(true);
    	
    	QueryResults results = oql.execute();
		
		if (results.hasMore()) 
        {
        	contentVersion = (ContentVersion)results.next();
        }
            
		return contentVersion;
    }


	/**
	 * This method returns the version previous to the one sent in.
	 */
	
	public ContentVersionVO getPreviousContentVersionVO(Integer contentId, Integer languageId, Integer contentVersionId) throws SystemException, Bug
    {
    	Database db = CastorDatabaseService.getDatabase();
    	ContentVersionVO contentVersionVO = null;

        beginTransaction(db);

        try
        {           
            OQLQuery oql = db.getOQLQuery( "SELECT cv FROM org.infoglue.cms.entities.content.impl.simple.ContentVersionImpl cv WHERE cv.owningContent.contentId = $1 AND cv.language.languageId = $2 AND cv.contentVersionId < $3 ORDER BY cv.contentVersionId desc");
        	oql.bind(contentId);
        	oql.bind(languageId);
        	oql.bind(contentVersionId);
        	
        	QueryResults results = oql.execute(Database.ReadOnly);
			
			if (results.hasMore()) 
            {
            	ContentVersion contentVersion = (ContentVersion)results.next();
            	CmsLogger.logInfo("found one:" + contentVersion.getValueObject());
            	contentVersionVO = contentVersion.getValueObject();
            }
            
            commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
    	
		return contentVersionVO;
    }


	/**
	 * This method returns the version previous to the one sent in.
	 */
	
	public ContentVersionVO getPreviousActiveContentVersionVO(Integer contentId, Integer languageId, Integer contentVersionId) throws SystemException, Bug
    {
    	Database db = CastorDatabaseService.getDatabase();
    	ContentVersionVO contentVersionVO = null;

        beginTransaction(db);

        try
        {           
            OQLQuery oql = db.getOQLQuery( "SELECT cv FROM org.infoglue.cms.entities.content.impl.simple.ContentVersionImpl cv WHERE cv.owningContent.contentId = $1 AND cv.language.languageId = $2 AND cv.isActive = $3 AND cv.contentVersionId < $4 ORDER BY cv.contentVersionId desc");
        	oql.bind(contentId);
        	oql.bind(languageId);
        	oql.bind(new Boolean(true));
        	oql.bind(contentVersionId);
        	
        	QueryResults results = oql.execute(Database.ReadOnly);
			
			if (results.hasMore()) 
            {
            	ContentVersion contentVersion = (ContentVersion)results.next();
            	CmsLogger.logInfo("found one:" + contentVersion.getValueObject());
            	contentVersionVO = contentVersion.getValueObject();
            }
            
            commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
    	
		return contentVersionVO;
    }


	/**
	 * This method deletes the relation to a digital asset - not the asset itself.
	 */
	public void deleteDigitalAssetRelation(Integer contentVersionId, Integer digitalAssetId) throws SystemException, Bug
    {
    	Database db = CastorDatabaseService.getDatabase();
        beginTransaction(db);

        try
        {           
        	ContentVersion contentVersion = getContentVersionWithId(contentVersionId, db);
			DigitalAsset digitalAsset = DigitalAssetController.getDigitalAssetWithId(digitalAssetId, db);			
			contentVersion.getDigitalAssets().remove(digitalAsset);
            digitalAsset.getContentVersions().remove(contentVersion);
            commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }
    }
    
	
	/**
	 * This method deletes the relation to a digital asset - not the asset itself.
	 */
	public void deleteDigitalAssetRelation(Integer contentVersionId, DigitalAsset digitalAsset, Database db) throws SystemException, Bug
    {
    	ContentVersion contentVersion = getContentVersionWithId(contentVersionId, db);
		contentVersion.getDigitalAssets().remove(digitalAsset);
        digitalAsset.getContentVersions().remove(contentVersion);
    }
    
	/**
	 * This method fetches a value from the xml that is the contentVersions Value. If the 
	 * contentVersioVO is null the contentVersion has not been created yet and no values are present.
	 */
	public String getAttributeValue(Integer contentVersionId, String attributeName, boolean escapeHTML) throws SystemException
	{
		String value = "";
		ContentVersionVO contentVersionVO = getContentVersionVOWithId(contentVersionId);
		
		if(contentVersionVO != null)
		{
			try
			{
				CmsLogger.logInfo("attributeName:" + attributeName);
				CmsLogger.logInfo("VersionValue:"  + contentVersionVO.getVersionValue());
				value = getAttributeValue(contentVersionVO, attributeName, escapeHTML);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		//CmsLogger.logInfo("value:" + value);
		return value;
	}

	/**
	 * Returns an attribute value from the ContentVersionVO
	 *
	 * @param contentVersionVO The version on which to find the value
	 * @param attributeName THe name of the attribute whose value is wanted
	 * @param escapeHTML A boolean indicating if the result should be escaped
	 * @return The String vlaue of the attribute, or blank if it doe snot exist.
	 */
	public String getAttributeValue(ContentVersionVO contentVersionVO, String attributeName, boolean escapeHTML)
	{
		String value = "";
		String xml = contentVersionVO.getVersionValue();

		int startTagIndex = xml.indexOf("<" + attributeName + ">");
		int endTagIndex   = xml.indexOf("]]></" + attributeName + ">");

		if(startTagIndex > 0 && startTagIndex < xml.length() && endTagIndex > startTagIndex && endTagIndex <  xml.length())
		{
			value = xml.substring(startTagIndex + attributeName.length() + 11, endTagIndex);
			if(escapeHTML)
				value = new VisualFormatter().escapeHTML(value);
		}		

		return value;
	}


	/**
	 * This method fetches a value from the xml that is the contentVersions Value. If the 
	 * contentVersioVO is null the contentVersion has not been created yet and no values are present.
	 */
	 
	public void updateAttributeValue(Integer contentVersionId, String attributeName, String attributeValue, InfoGluePrincipal infogluePrincipal) throws SystemException, Bug
	{
		ContentVersionVO contentVersionVO = getContentVersionVOWithId(contentVersionId);
		
		if(contentVersionVO != null)
		{
			try
			{
				CmsLogger.logInfo("attributeName:"  + attributeName);
				CmsLogger.logInfo("versionValue:"   + contentVersionVO.getVersionValue());
				CmsLogger.logInfo("attributeValue:" + attributeValue);
				InputSource inputSource = new InputSource(new StringReader(contentVersionVO.getVersionValue()));
				
				DOMParser parser = new DOMParser();
				parser.parse(inputSource);
				Document document = parser.getDocument();
				
				NodeList nl = document.getDocumentElement().getChildNodes();
				Node attributesNode = nl.item(0);
				
				boolean existed = false;
				nl = attributesNode.getChildNodes();
				for(int i=0; i<nl.getLength(); i++)
				{
					Node n = nl.item(i);
					if(n.getNodeName().equalsIgnoreCase(attributeName))
					{
						if(n.getFirstChild() != null && n.getFirstChild().getNodeValue() != null)
						{
							n.getFirstChild().setNodeValue(attributeValue);
							existed = true;
							break;
						}
						else
						{
							CDATASection cdata = document.createCDATASection(attributeValue);
							n.appendChild(cdata);
							existed = true;
							break;
						}
					}
				}
				
				if(existed == false)
				{
					org.w3c.dom.Element attributeElement = document.createElement(attributeName);
					attributesNode.appendChild(attributeElement);
					CDATASection cdata = document.createCDATASection(attributeValue);
					attributeElement.appendChild(cdata);
				}
				
				StringBuffer sb = new StringBuffer();
				org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
				CmsLogger.logInfo("sb:" + sb);
				contentVersionVO.setVersionValue(sb.toString());
				contentVersionVO.setVersionModifier(infogluePrincipal.getName());
				update(contentVersionVO.getContentId(), contentVersionVO.getLanguageId(), contentVersionVO);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * This is a method that gives the user back an newly initialized ValueObject for this entity that the controller
	 * is handling.
	 */

	public BaseEntityVO getNewVO()
	{
		return new ContentVersionVO();
	}

}
