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

package org.infoglue.deliver.controllers.kernel.impl.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;
import org.infoglue.cms.entities.management.Language;
import org.infoglue.cms.entities.management.impl.simple.LanguageImpl;
import org.infoglue.cms.entities.management.impl.simple.RepositoryImpl;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.management.Repository;
import org.infoglue.cms.entities.management.RepositoryLanguage;
import org.infoglue.cms.entities.structure.SiteNode;
import org.infoglue.cms.entities.structure.impl.simple.SiteNodeImpl;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.SystemException;

import org.infoglue.deliver.util.CacheController;


public class LanguageDeliveryController extends BaseDeliveryController
{

	/**
	 * Private constructor to enforce factory-use
	 */
	
	private LanguageDeliveryController()
	{
	}
	
	/**
	 * Factory method
	 */
	
	public static LanguageDeliveryController getLanguageDeliveryController()
	{
		return new LanguageDeliveryController();
	}
	
	
	/**
	 * This method return a LanguageVO
	 */
	
	public LanguageVO getLanguageVO(Database db, Integer languageId) throws SystemException, Exception
	{
		String key = "" + languageId;
		getLogger().info("key:" + key);
		LanguageVO languageVO = (LanguageVO)CacheController.getCachedObject("languageCache", key);
		if(languageVO != null)
		{
			getLogger().info("There was an cached languageVO:" + languageVO);
		}
		else
		{
			Language language = (Language)getObjectWithId(LanguageImpl.class, languageId, db);
				
			if(language != null)
				languageVO = language.getValueObject();
            
			CacheController.cacheObject("languageCache", key, languageVO);				
		}
				
		return languageVO;
	}

	/**
	 * This method returns all languages for a certain repository.
	 * 
	 * @param repositoryId
	 * @return
	 * @throws SystemException
	 * @throws Exception
	 */

	public List getAvailableLanguagesForRepository(Database db, Integer repositoryId) throws SystemException, Exception
    {
        List list = new ArrayList();

        Repository repository = (Repository) getObjectWithId(RepositoryImpl.class, repositoryId, db);
        if (repository != null) 
        {
            for (Iterator i = repository.getRepositoryLanguages().iterator();i.hasNext();) 
            {
                RepositoryLanguage repositoryLanguage = (RepositoryLanguage) i.next();
                Language language = repositoryLanguage.getLanguage();
                if (language != null)
                    list.add(language.getValueObject());
            }
        }
        
        return list;
    } 
	
	/**
	 * This method returns the languages assigned to a respository. 
	 */
	
	public List getAvailableLanguages(Database db, Integer siteNodeId) throws SystemException, Exception
	{ 
        List languageVOList = new ArrayList();

        System.out.println("siteNodeId:" + siteNodeId);

        SiteNode siteNode = (SiteNode)getObjectWithId(SiteNodeImpl.class, siteNodeId, db);
			
		if(siteNode != null)
		{
			Repository repository = siteNode.getRepository();
     		if(repository != null)
			{
     		    System.out.println("repository:" + repository.getName());

     		    Collection repositoryLanguages = repository.getRepositoryLanguages();
     		    System.out.println("repositoryLanguages:" + repositoryLanguages.size());
     			Iterator repositoryLanguagesIterator = repositoryLanguages.iterator();
     			while(repositoryLanguagesIterator.hasNext())
     			{
     				RepositoryLanguage repositoryLanguage = (RepositoryLanguage)repositoryLanguagesIterator.next();
     				Language language = repositoryLanguage.getLanguage();
     				if(language != null)
     				{
     					getLogger().info("Adding " + language.getName() + " to the list of available languages");
         				languageVOList.add(language.getValueObject());
     				}
     			}
			}
		}
	
        return languageVOList;	
	}


	/**
	 * This method returns the master language. 
	 * todo - add attribute on repositoryLanguage to be able to sort them... and then fetch the first
	 */
	
	public LanguageVO getMasterLanguage(Database db, String repositoryName) throws SystemException, Exception
	{ 
        Language language = null;

     	OQLQuery oql = db.getOQLQuery( "SELECT l FROM org.infoglue.cms.entities.management.impl.simple.LanguageImpl l WHERE l.repositoryLanguages.repository.name = $1 ORDER BY l.repositoryLanguages.sortOrder, l.languageId");
		oql.bind(repositoryName);
		
    	QueryResults results = oql.execute(Database.ReadOnly);
		
		if (results.hasMore()) 
        {
        	language = (Language)results.next();
        }
            
        return (language == null) ? null : language.getValueObject();	
	}
	

	/**
	 * This method returns the master language. 
	 * todo - add attribute on repositoryLanguage to be able to sort them... and then fetch the first
	 */
	
	public LanguageVO getMasterLanguageForRepository(Database db, Integer repositoryId) throws SystemException, Exception
	{ 
		String languageKey = "" + repositoryId;
		getLogger().info("languageKey in getMasterLanguageForRepository:" + languageKey);
		LanguageVO languageVO = (LanguageVO)CacheController.getCachedObject("masterLanguageCache", languageKey);
		if(languageVO != null)
		{
			getLogger().info("There was an cached master language:" + languageVO.getName());
		}
		else
		{
			OQLQuery oql = db.getOQLQuery( "SELECT l FROM org.infoglue.cms.entities.management.impl.simple.LanguageImpl l WHERE l.repositoryLanguages.repository.repositoryId = $1 ORDER BY l.repositoryLanguages.sortOrder, l.languageId");
			oql.bind(repositoryId);
			
			QueryResults results = oql.execute(Database.ReadOnly);
			
			if (results.hasMore()) 
			{
				Language language = (Language)results.next();
				languageVO = language.getValueObject();
			}
			
			CacheController.cacheObject("masterLanguageCache", languageKey, languageVO);
		}

		return languageVO;	
	}

	/**
	 * This method returns the master language. 
	 * todo - add attribute on repositoryLanguage to be able to sort them... and then fetch the first
	 */
	
	public LanguageVO getMasterLanguageForRepository(Integer repositoryId, Database db) throws SystemException, Exception
	{ 
		LanguageVO languageVO = null;

		String languageKey = "" + repositoryId;
		getLogger().info("languageKey in getMasterLanguageForRepository:" + languageKey);
		languageVO = (LanguageVO)CacheController.getCachedObject("masterLanguageCache", languageKey);
		if(languageVO != null)
		{
			getLogger().info("There was an cached master language:" + languageVO.getName());
		}
		else
		{
			OQLQuery oql = db.getOQLQuery( "SELECT l FROM org.infoglue.cms.entities.management.impl.simple.LanguageImpl l WHERE l.repositoryLanguages.repository.repositoryId = $1 ORDER BY l.repositoryLanguages.sortOrder, l.languageId");
			oql.bind(repositoryId);
			
			QueryResults results = oql.execute(Database.ReadOnly);
			
			if (results.hasMore()) 
			{
				Language language = (Language)results.next();
				languageVO = language.getValueObject();
			}
			
			CacheController.cacheObject("masterLanguageCache", languageKey, languageVO);
		}

		return languageVO;	
	}

	
	/**
	 * This method returns the master language. 
	 * todo - add attribute on repositoryLanguage to be able to sort them... and then fetch the first
	 */
	
	public LanguageVO getMasterLanguageForSiteNode(Database db, Integer siteNodeId) throws SystemException, Exception
	{ 
	    String languageKey = "siteNodeId_" + siteNodeId;
		getLogger().info("languageKey in getMasterLanguageForSiteNode:" + languageKey);
		LanguageVO languageVO = (LanguageVO)CacheController.getCachedObject("masterLanguageCache", languageKey);
		if(languageVO != null)
		{
		    getLogger().info("There was an cached master language:" + languageVO.getName());
		}
		else
		{
			SiteNode siteNode = (SiteNode)getObjectWithId(SiteNodeImpl.class, siteNodeId, db);
			Integer repositoryId = siteNode.getRepository().getRepositoryId();
         	
			OQLQuery oql = db.getOQLQuery( "SELECT l FROM org.infoglue.cms.entities.management.impl.simple.LanguageImpl l WHERE l.repositoryLanguages.repository.repositoryId = $1 ORDER BY l.repositoryLanguages.sortOrder, l.languageId");
			oql.bind(repositoryId);
			
        	QueryResults results = oql.execute(Database.ReadOnly);
			
			if (results.hasMore()) 
            {
				Language language = (Language)results.next();
				languageVO = language.getValueObject();
            }
			
			CacheController.cacheObject("masterLanguageCache", languageKey, languageVO);
		}
		
        return languageVO;	
	}
	

	/**
	 * This method returns language with the languageCode sent in. 
	 */
	
	public Locale getLocaleWithId(Database db, Integer languageId)
	{
		Locale locale = Locale.getDefault();
		
		if (languageId != null)
		{
			try 
			{
				LanguageVO languageVO = getLanguageVO(db, languageId);
				locale = new Locale(languageVO.getLanguageCode());
			} 
			catch (Exception e) 
			{
				getLogger().error("An error occurred in getLocaleWithId: getting locale with languageid:" + languageId + "," + e, e);
			}	
		}
		
		return locale; 
	}


	/**
	 * This method returns language with the languageCode sent in. 
	 */
	
	public LanguageVO getLanguageWithCode(Database db, String languageCode) throws SystemException, Exception
	{ 
		String key = "" + languageCode;
		getLogger().info("key:" + key);
		LanguageVO languageVO = (LanguageVO)CacheController.getCachedObject("languageCache", key);
		if(languageVO != null)
		{
			getLogger().info("There was an cached languageVO:" + languageVO);
		}
		else
		{
			Language language = null;
	
			OQLQuery oql = db.getOQLQuery( "SELECT l FROM org.infoglue.cms.entities.management.impl.simple.LanguageImpl l WHERE l.languageCode = $1");
			oql.bind(languageCode);
			
        	QueryResults results = oql.execute(Database.ReadOnly);
			
			if (results.hasMore()) 
            {
            	language = (Language)results.next();
				languageVO = language.getValueObject();
	        }
            
			CacheController.cacheObject("languageCache", key, languageVO);
		}
		
        return languageVO;	
	}


	/**
	 * This method returns language with the languageCode sent in if it is allowed/supported in the current repository. 
	 */
	
	public LanguageVO getLanguageIfRepositorySupportsIt(Database db, String languageCodes, Integer siteNodeId) throws SystemException, Exception
	{
		if (languageCodes == null) return null;
		int index = Integer.MAX_VALUE;
		int currentIndex = 0;
		getLogger().info("Coming in with languageCodes:" + languageCodes);
		
        Language language = null;

    	SiteNode siteNode = (SiteNode)getObjectWithId(SiteNodeImpl.class, siteNodeId, db);
		Repository repository = siteNode.getRepository();
		if(repository != null)
		{
			Collection languages = repository.getRepositoryLanguages();
			Iterator languageIterator = languages.iterator();
			while(languageIterator.hasNext())
			{
				RepositoryLanguage repositoryLanguage = (RepositoryLanguage)languageIterator.next();
				Language currentLanguage = repositoryLanguage.getLanguage();
				getLogger().info("CurrentLanguageCode:" + currentLanguage.getLanguageCode());
				currentIndex = languageCodes.toLowerCase().indexOf(currentLanguage.getLanguageCode().toLowerCase());
				if( currentIndex > -1 && currentIndex < index)
				{
					index = currentIndex;
					getLogger().info("Found the language in the list of supported languages for this site: " + currentLanguage.getName() + " - priority:" + index);
					language = currentLanguage;
					if (index==0) break; // Continue and try to find a better candidate unless index is 0 (first prio)
				}
			}
		}

		return (language == null) ? null : language.getValueObject();	
	}

    
}