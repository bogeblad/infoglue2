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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.FormEntry;
import org.infoglue.cms.entities.management.FormEntryVO;
import org.infoglue.cms.entities.management.Repository;
import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.entities.management.impl.simple.FormEntryImpl;
import org.infoglue.cms.entities.management.impl.simple.RepositoryImpl;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.deliver.util.CacheController;


/**
 * @author Mattias Bogeblad
 */

public class FormEntryController extends BaseController
{
    private final static Logger logger = Logger.getLogger(FormEntryController.class.getName());

	/**
	 * Factory method
	 */

	public static FormEntryController getController()
	{
		return new FormEntryController();
	}

    public FormEntryVO getFormEntryVOWithId(Integer redirectId) throws SystemException, Bug
    {
		return (FormEntryVO) getVOWithId(FormEntryImpl.class, redirectId);
    }

    public FormEntry getFormEntryWithId(Integer redirectId, Database db) throws SystemException, Bug
    {
		return (FormEntry) getObjectWithId(FormEntryImpl.class, redirectId, db);
    }

    public List getFormEntryVOList() throws SystemException, Bug
    {
		List redirectVOList = getAllVOObjects(FormEntryImpl.class, "formEntryId");

		return redirectVOList;
    }

    public List getFormEntryVOList(Database db) throws SystemException, Bug
    {
		List redirectVOList = getAllVOObjects(FormEntryImpl.class, "formEntryId", db);

		return redirectVOList;
    }

	/**
	 * Returns the RepositoryVO with the given name.
	 * 
	 * @param name
	 * @return
	 * @throws SystemException
	 * @throws Bug
	 */
	
	public List getFormEntryVOList(String formContentId) throws SystemException, Bug
	{
		List formEntryVOList = null;
		
		Database db = CastorDatabaseService.getDatabase();

		try 
		{
			beginTransaction(db);

			Collection formEntryList = getFormEntryList(formContentId, db);
			formEntryVOList = toVOList(formEntryList);
				
			commitTransaction(db);
		} 
		catch (Exception e) 
		{
			logger.info("An error occurred so we should not complete the transaction:" + e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
		
		return formEntryVOList;	
	}
	
	/**
	 * Returns the Repository with the given name fetched within a given transaction.
	 * 
	 * @param name
	 * @param db
	 * @return
	 * @throws SystemException
	 * @throws Bug
	 */

	public List getFormEntryList(String formContentId, Database db) throws SystemException, Bug
	{
		List formEntryList = new ArrayList();
		
		try
		{
			OQLQuery oql = db.getOQLQuery("SELECT f FROM org.infoglue.cms.entities.management.impl.simple.FormEntryImpl f WHERE f.formContentId = $1 order by formEntryId");
			oql.bind(formContentId);
			
			QueryResults results = oql.execute();

			while (results.hasMore()) 
			{
				formEntryList.add(results.next());
			}
			
			results.close();
			oql.close();
		}
		catch(Exception e)
		{
			throw new SystemException("An error occurred when we tried to fetch a list of form entries. Reason:" + e.getMessage(), e);    
		}
		
		return formEntryList;		
	}

    public FormEntryVO create(FormEntryVO redirectVO) throws ConstraintException, SystemException
    {
        FormEntry formEntry = new FormEntryImpl();
        formEntry.setValueObject(redirectVO);
        formEntry = (FormEntry) createEntity(formEntry);
        return formEntry.getValueObject();
    }

    public void delete(FormEntryVO formEntryVO) throws ConstraintException, SystemException
    {
    	deleteEntity(FormEntryImpl.class, formEntryVO.getFormEntryId());
    }

    public FormEntryVO update(FormEntryVO formEntryVO) throws ConstraintException, SystemException
    {
    	return (FormEntryVO) updateEntity(FormEntryImpl.class, formEntryVO);
    }
    
	/**
	 * This method removes a Repository from the system and also cleans out all depending repositoryLanguages.
	 */
	/*
    public void delete(RepositoryVO repositoryVO, String userName, boolean forceDelete, InfoGluePrincipal infoGluePrincipal) throws ConstraintException, SystemException
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
			{
				if(forceDelete)
					ContentController.getContentController().delete(contentVO, db, true, true, true, infoGluePrincipal);
				else
					ContentController.getContentController().delete(contentVO, infoGluePrincipal, db);
			}
			
			SiteNodeVO siteNodeVO = SiteNodeController.getController().getRootSiteNodeVO(repositoryVO.getRepositoryId());
			if(siteNodeVO != null)
			{
				if(forceDelete)
					SiteNodeController.getController().delete(siteNodeVO, db, true, infoGluePrincipal);
				else
					SiteNodeController.getController().delete(siteNodeVO, db, infoGluePrincipal);
			}
			
			deleteEntity(RepositoryImpl.class, repositoryVO.getRepositoryId(), db);
	
			//If any of the validations or setMethods reported an error, we throw them up now before create.
			ceb.throwIfNotEmpty();
    
			commitTransaction(db);
		}
		catch(ConstraintException ce)
		{
			logger.warn("An error occurred so we should not completes the transaction:" + ce, ce);
			rollbackTransaction(db);
			throw ce;
		}
		catch(Exception e)
		{
			logger.error("An error occurred so we should not completes the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
    } 
	*/
    
	/**
	 * This is a method that gives the user back an newly initialized ValueObject for this entity that the controller
	 * is handling.
	 */

	public BaseEntityVO getNewVO()
	{
		return new ContentTypeDefinitionVO();
	}
}
