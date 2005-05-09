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

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.Language;
import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.entities.management.Repository;
import org.infoglue.cms.entities.management.RepositoryLanguage;
import org.infoglue.cms.entities.management.impl.simple.RepositoryImpl;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;

import org.infoglue.cms.exception.*;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.*;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.deliver.util.CacheController;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class RepositoryController extends BaseController
{
	
	/**
	 * Factory method
	 */

	public static RepositoryController getController()
	{
		return new RepositoryController();
	}
	
    public RepositoryVO create(RepositoryVO vo) throws ConstraintException, SystemException
    {
        Repository ent = new RepositoryImpl();
        ent.setValueObject(vo);
        ent = (Repository) createEntity(ent);
        return ent.getValueObject();
    }     

	/**
	 * This method removes a Repository from the system and also cleans out all depending repositoryLanguages.
	 */
	
    public void delete(RepositoryVO repositoryVO, String userName) throws ConstraintException, SystemException
    {
		Database db = CastorDatabaseService.getDatabase();
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

		Repository repository = null;
	
		beginTransaction(db);

		try
		{
			repository = getRepositoryWithId(repositoryVO.getRepositoryId(), db);
			
			RepositoryLanguageController.getController().deleteRepositoryLanguages(repository, db);
			
			ContentVO contentVO = ContentControllerProxy.getController().getRootContentVO(repositoryVO.getRepositoryId(), userName, false);
			if(contentVO != null)
			    ContentController.getContentController().delete(contentVO, db);
			
			SiteNodeVO siteNodeVO = SiteNodeController.getController().getRootSiteNodeVO(repositoryVO.getRepositoryId());
			if(siteNodeVO != null)
				SiteNodeController.delete(siteNodeVO, db);
			
			deleteEntity(RepositoryImpl.class, repositoryVO.getRepositoryId(), db);
	
			//If any of the validations or setMethods reported an error, we throw them up now before create.
			ceb.throwIfNotEmpty();
    
			commitTransaction(db);
		}
		catch(ConstraintException ce)
		{
			CmsLogger.logWarning("An error occurred so we should not completes the transaction:" + ce, ce);
			rollbackTransaction(db);
			throw ce;
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
    } 
    
    
    public RepositoryVO update(RepositoryVO vo) throws ConstraintException, SystemException
    {
    	return (RepositoryVO) updateEntity(RepositoryImpl.class, (BaseEntityVO) vo);
    }        
    
    public RepositoryVO update(RepositoryVO repositoryVO, String[] languageValues) throws ConstraintException, SystemException
    {
    	Database db = CastorDatabaseService.getDatabase();
        ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

        beginTransaction(db);

        try
        {
        	Repository repository = RepositoryController.getController().getRepositoryWithId(repositoryVO.getRepositoryId(), db);
        	
        	RepositoryLanguageController.getController().deleteRepositoryLanguages(repository, db);

        	//add validation here if needed   			
            List repositoryLanguageList = new ArrayList();
            if(languageValues != null)
			{
				for (int i=0; i < languageValues.length; i++)
	            {
	            	Language language = LanguageController.getController().getLanguageWithId(new Integer(languageValues[i]), db);
	            	RepositoryLanguage repositoryLanguage = RepositoryLanguageController.getController().create(repositoryVO.getRepositoryId(), new Integer(languageValues[i]), new Integer(i), db);
	            	repositoryLanguageList.add(repositoryLanguage);
					language.getRepositoryLanguages().add(repositoryLanguage);
	            }
			}
			
			repository.setValueObject(repositoryVO);
			repository.setRepositoryLanguages(repositoryLanguageList);
			
			repositoryVO = repository.getValueObject();
			
            //If any of the validations or setMethods reported an error, we throw them up now before create.
            ceb.throwIfNotEmpty();
            
            commitTransaction(db);
        }
        catch(ConstraintException ce)
        {
            CmsLogger.logWarning("An error occurred so we should not completes the transaction:" + ce, ce);
            rollbackTransaction(db);
            throw ce;
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not completes the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }

        return repositoryVO;
    }        
    
	// Singe object
    public Repository getRepositoryWithId(Integer id, Database db) throws SystemException, Bug
    {
		return (Repository) getObjectWithId(RepositoryImpl.class, id, db);
    }

    public RepositoryVO getRepositoryVOWithId(Integer repositoryId) throws ConstraintException, SystemException, Bug
    {
		return  (RepositoryVO) getVOWithId(RepositoryImpl.class, repositoryId);        
    }
	
	/**
	 * This method can be used by actions and use-case-controllers that only need to have simple access to the
	 * functionality. They don't get the transaction-safety but probably just wants to show the info.
	 */	
    
    public List getRepositoryVOList() throws ConstraintException, SystemException, Bug
    {   
		String key = "repositoryVOList";
		CmsLogger.logInfo("key:" + key);
		List cachedRepositoryVOList = (List)CacheController.getCachedObject("repositoryCache", key);
		if(cachedRepositoryVOList != null)
		{
			CmsLogger.logInfo("There was an cached authorization:" + cachedRepositoryVOList.size());
			return cachedRepositoryVOList;
		}
				
		List repositoryVOList = getAllVOObjects(RepositoryImpl.class, "repositoryId");

		CacheController.cacheObject("repositoryCache", key, repositoryVOList);
			
		return repositoryVOList;
    }

    
	/**
	 * This method can be used by actions and use-case-controllers that only need to have simple access to the
	 * functionality. They don't get the transaction-safety but probably just wants to show the info.
	 */	
	
	public List getAuthorizedRepositoryVOList(InfoGluePrincipal infoGluePrincipal) throws ConstraintException, SystemException, Bug
	{    	
		List accessableRepositories = new ArrayList();
    	
		List allRepositories = this.getRepositoryVOList(); //getAllVOObjects(RepositoryImpl.class, "repositoryId");
		Iterator i = allRepositories.iterator();
		while(i.hasNext())
		{
			RepositoryVO repositoryVO = (RepositoryVO)i.next();
			if(getIsAccessApproved(repositoryVO.getRepositoryId(), infoGluePrincipal))
			{
				accessableRepositories.add(repositoryVO);
			}
		}
    	
		return accessableRepositories;
	}



	
	/**
	 * Return the first of all repositories.
	 */
	
	public RepositoryVO getFirstRepositoryVO()  throws SystemException, Bug
	{
		Database db = CastorDatabaseService.getDatabase();
		RepositoryVO repositoryVO = null;
		
		try 
		{
			beginTransaction(db);
		
			OQLQuery oql = db.getOQLQuery("SELECT r FROM org.infoglue.cms.entities.management.impl.simple.RepositoryImpl r ORDER BY r.repositoryId");
        	QueryResults results = oql.execute();
			
			if (results.hasMore()) 
            {
                Repository repository = (Repository)results.next();
                repositoryVO = repository.getValueObject();
            }
            
			commitTransaction(db);
		}
		catch ( Exception e)		
		{
			throw new SystemException("An error occurred when we tried to fetch a list of roles in the repository. Reason:" + e.getMessage(), e);			
		}
		return repositoryVO;		
	}



	/**
	 * This method deletes the Repository sent in from the system.
	 */	
	public void delete(Integer repositoryId, Database db) throws SystemException, Bug
	{
		try
		{
			db.remove(getRepositoryWithId(repositoryId, db));
		}
		catch(Exception e)
		{
			throw new SystemException("An error occurred when we tried to delete Repository in the database. Reason: " + e.getMessage(), e);
		}	
	} 


	/**
	 * This method returns true if the user should have access to the repository sent in.
	 */
    
	public boolean getIsAccessApproved(Integer repositoryId, InfoGluePrincipal infoGluePrincipal) throws SystemException
	{
		CmsLogger.logInfo("getIsAccessApproved for " + repositoryId + " AND " + infoGluePrincipal);
		boolean hasAccess = false;
    	
		Database db = CastorDatabaseService.getDatabase();
       
		beginTransaction(db);

		try
		{ 
			hasAccess = AccessRightController.getController().getIsPrincipalAuthorized(db, infoGluePrincipal, "Repository.Read", repositoryId.toString());
		
			commitTransaction(db);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred so we should not complete the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
		
		//System.out.println("hasAccess:" + hasAccess);
		
		return hasAccess;
	}	
    
	
	/**
	 * This is a method that gives the user back an newly initialized ValueObject for this entity that the controller
	 * is handling.
	 */

	public BaseEntityVO getNewVO()
	{
		return new RepositoryVO();
	}
		
}
 
