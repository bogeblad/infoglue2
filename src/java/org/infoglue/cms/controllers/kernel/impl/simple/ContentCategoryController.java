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
 * $Id: ContentCategoryController.java,v 1.1 2004/12/01 23:40:28 frank Exp $
 */
package org.infoglue.cms.controllers.kernel.impl.simple;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.Category;
import org.infoglue.cms.entities.management.impl.simple.CategoryImpl;
import org.infoglue.cms.entities.content.ContentCategoryVO;
import org.infoglue.cms.entities.content.ContentCategory;
import org.infoglue.cms.entities.content.impl.simple.ContentCategoryImpl;
import org.infoglue.cms.exception.SystemException;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;

/**
 * The ContentCategoryController manages all actions related to persistence
 * and querying for ContentCategory relationships.
 *
 * TODO: When we convert have Hibernate manage all of these relationships, it will pull it
 * TODO: all back with one query and be a helluva lot faster than this basic implementation
 *
 * @author Frank Febbraro (frank@phase2technology.com)
 */
public class ContentCategoryController extends BaseController
{
	private static final ContentCategoryController instance = new ContentCategoryController();

	private static final String findByContentVersion = new StringBuffer("SELECT c ")
			.append("FROM org.infoglue.cms.entities.content.impl.simple.ContentCategoryImpl c ")
			.append("WHERE c.contentVersionId = $1").toString();

	private static final String findByContentVersionAttribute = new StringBuffer("SELECT c ")
			.append("FROM org.infoglue.cms.entities.content.impl.simple.ContentCategoryImpl c ")
			.append("WHERE c.attributeName = $1 ")
			.append("AND c.contentVersionId = $2")
			.append("ORDER BY c.category.name").toString();

	private static final String findByCategory = new StringBuffer("SELECT c ")
			.append("FROM org.infoglue.cms.entities.content.impl.simple.ContentCategoryImpl c ")
			.append("WHERE c.category.categoryId = $1 ").toString();

	public static ContentCategoryController getController()
	{ return instance; }

	private ContentCategoryController()
	{}

	/**
	 * Find a ContentCategory by it's identifier.
	 *
	 * @param	id The id of the Category to find
	 * @return	The CategoryVO identified by the provided id
	 * @throws	SystemException If an error happens
	 */
	public ContentCategoryVO findById(Integer id) throws SystemException
	{
		return (ContentCategoryVO)getVOWithId(ContentCategoryImpl.class, id);
	}

	/**
	 * Find a List of ContentCategories for the specific attribute and Content Version.
	 *
	 * @param	attribute The attribute name of the ContentCategory to find
	 * @param	id The Content Version id of the ContentCategory to find
	 * @return	A list of ContentCategoryVO that have the provided content version and attribute
	 * @throws	SystemException If an error happens
	 */
	public List findByContentVersionAttribute(String attribute, Integer id) throws SystemException
	{
		List params = new ArrayList();
		params.add(attribute);
		params.add(id);
		return executeQuery(findByContentVersionAttribute, params);
	}

	/**
	 * Find a List of ContentCategories for a Content Version.
	 *
	 * @param	id The Content Version id of the ContentCategory to find
	 * @return	A list of ContentCategoryVO that have the provided content version and attribute
	 * @throws	SystemException If an error happens
	 */
	public List findByContentVersion(Integer id) throws SystemException
	{
		List params = new ArrayList();
		params.add(id);
		return executeQuery(findByContentVersion, params);
	}

	/**
	 * Find a List of ContentCategories for the specific attribute and Content Version.
	 *
	 * @param	id The Category id of the ContentCategory to find
	 * @return	A list of ContentCategoryVO that have the provided category id
	 * @throws	SystemException If an error happens
	 */
	public List findByCategory(Integer id) throws SystemException
	{
		List params = new ArrayList();
		params.add(id);
		return executeQuery(findByCategory, params);
	}

	/**
	 * Saves a ContentCategoryVO whether it is new or not.
	 *
	 * @param	c The ContentCategoryVO to save
	 * @return	The saved ContentCategoryVO
	 * @throws	org.infoglue.cms.exception.SystemException If an error happens
	 */
	public ContentCategoryVO save(ContentCategoryVO c) throws SystemException
	{
		return (c.isUnsaved())
					? create(c)
					: (ContentCategoryVO)updateEntity(ContentCategoryImpl.class, c);
	}

	/**
	 * Creates a ContentCategory from a ContentCategoryVO
	 */
	private ContentCategoryVO create(ContentCategoryVO c) throws SystemException
	{
		ContentCategory contentCategory = null;

		Database db = beginTransaction();

		try
		{
			contentCategory = createWithDatabase(c, db);
			commitTransaction(db);
		}
		catch(Exception e)
		{
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}

        return contentCategory.getValueObject();
	}

	public ContentCategory createWithDatabase(ContentCategoryVO c, Database db) throws SystemException, PersistenceException
	{
		// Need this crappy hack to forge the relationship (castor completely sucks like this)
		// TODO: When hibernate comes, just save the VOs and if it has a child VO with an id set
		// TODO: it is used to make the relationship...ask me for clarification -frank
		Category category = (Category)getObjectWithId(CategoryImpl.class, c.getCategory().getId(), db);

		ContentCategory contentCategory = new ContentCategoryImpl();
		contentCategory.setValueObject(c);
		contentCategory.setCategory((CategoryImpl)category);
		db.create(contentCategory);
		return contentCategory;
	}


	/**
	 * Deletes a ContentCategory
	 *
	 * @param	id The id of the ContentCategory to delete
	 * @throws	org.infoglue.cms.exception.SystemException If an error happens
	 */
	public void delete(Integer id) throws SystemException
	{
		deleteEntity(ContentCategoryImpl.class, id);
	}

	/**
	 * Deletes all ContentCategories for a particular ContentVersion
	 *
	 * @param	id The id of the ContentCategory to delete
	 * @throws	org.infoglue.cms.exception.SystemException If an error happens
	 */
	public void deleteByContentVersion(Integer id) throws SystemException
	{

		Database db = beginTransaction();
		try
		{
			deleteByContentVersion(id, db);
			commitTransaction(db);
		}
		catch(Exception e)
		{
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}

	}

	/**
	 * Deletes all ContentCategories for a particular ContentVersion using the provided Database
	 *
	 * @param	id The id of the ContentCategory to delete
	 * @param	db The Database instance to use
	 * @throws	org.infoglue.cms.exception.SystemException If an error happens
	 */
	public void deleteByContentVersion(Integer id, Database db) throws SystemException
	{
		List found = findByContentVersion(id);
		for (Iterator iter = found.iterator(); iter.hasNext();)
		{
			ContentCategoryVO vo = (ContentCategoryVO) iter.next();
			deleteEntity(ContentCategoryImpl.class, vo.getId(), db);
		}
	}

	/**
	 * Deletes all ContentCategories for a particular Category
	 *
	 * @param	id The id of the ContentCategory to delete
	 * @throws	org.infoglue.cms.exception.SystemException If an error happens
	 */
	public void deleteByCategory(Integer id) throws SystemException
	{

		Database db = beginTransaction();
		try
		{
			deleteByCategory(id, db);
			commitTransaction(db);
		}
		catch(Exception e)
		{
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}

	}

	/**
	 * Deletes all ContentCategories for a particular Category using the provided Database
	 *
	 * @param	id The id of the Category to delete
	 * @param	db The Database instance to use
	 * @throws	org.infoglue.cms.exception.SystemException If an error happens
	 */
	public void deleteByCategory(Integer id, Database db) throws SystemException
	{
		List found = findByCategory(id);
		for (Iterator iter = found.iterator(); iter.hasNext();)
		{
			ContentCategoryVO vo = (ContentCategoryVO) iter.next();
			deleteEntity(ContentCategoryImpl.class, vo.getId(), db);
		}
	}

	/**
	 * Implemented for BaseController
	 */
	public BaseEntityVO getNewVO()
	{
		return new ContentCategoryVO();
	}
}
