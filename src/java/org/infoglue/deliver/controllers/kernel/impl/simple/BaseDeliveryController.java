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
import java.util.Collections;
import java.util.List;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.kernel.IBaseEntity;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsLogger;

/**
 * BaseDeliveryController.java
 *
 * Baseclass for Castor Controller Classes.
 * Various methods to handle transactions and so on
 *
 */

public abstract class BaseDeliveryController
{

	/**
	 * This method fetches one object / entity within a transaction.
	 **/

	protected Object getObjectWithId(Class arg, Integer id, Database db) throws SystemException, Bug
	{
		Object object = null;
		try
		{
			object = db.load(arg, id, Database.ReadOnly);
		}
		catch(Exception e)
		{
			throw new SystemException("An error occurred when we tried to fetch the object " + arg.getName() + ". Reason:" + e.getMessage(), e);
		}

		if(object == null)
		{
			throw new Bug("The object with id [" + id + "] was not found. This should never happen.");
		}
		return object;
	}


	/**
	 * This method fetches one object in read only mode and returns it's value object.
	 */

	protected BaseEntityVO getVOWithId(Class arg, Integer id, Database db) throws SystemException, Bug
	{
		IBaseEntity vo = null;
		try
		{
			vo = (IBaseEntity)db.load(arg, id, Database.ReadOnly);
		}
		catch(Exception e)
		{
			throw new SystemException("An error occurred when we tried to fetch the object " + arg.getName() + ". Reason:" + e.getMessage(), e);
		}

		if(vo == null)
		{
			throw new Bug("The object with id [" + id + "] was not found. This should never happen.");
		}

		return vo.getVO();
	}


	/**
	 * This method fetches all object in read only mode and returns a list of value objects.
	 */

	public List getAllVOObjects(Class arg, Database db) throws SystemException, Bug
	{
		ArrayList resultList = new ArrayList();

		try
		{
        	CmsLogger.logInfo("BaseHelper::GetAllObjects for " + arg.getName());
			OQLQuery oql = db.getOQLQuery( "SELECT u FROM " + arg.getName() + " u" );
			QueryResults results = oql.execute(Database.ReadOnly);

			while (results.hasMore())
			{
				Object o = results.next();

				// Om metoden getValueObject saknas, kastas ett undantag.
				resultList.add(o.getClass().getDeclaredMethod("getValueObject", new Class[0]).invoke(o, new Object[0]));
			}
		}
		catch(NoSuchMethodException e)
		{
			throw new Bug("The object [" + arg.getName() + "] is of the wrong type. This should never happen.", e);
		}
		catch(Exception e)
		{
			throw new SystemException("An error occurred when we tried to fetch " + arg.getName() + " Reason:" + e.getMessage(), e);
		}

		return Collections.unmodifiableList(resultList);
	}

	/**
	 * This method fetches all object in read only mode and returns a list of value objects.
	 */

	public List getAllVOObjects(Class arg, String orderByField, String direction, Database db) throws SystemException, Bug
	{
		ArrayList resultList = new ArrayList();

		try
		{

			CmsLogger.logInfo("BaseHelper::GetAllObjects for " + arg.getName());
			OQLQuery oql = db.getOQLQuery( "SELECT u FROM " + arg.getName() + " u ORDER BY u." + orderByField + " " + direction);
			QueryResults results = oql.execute(Database.ReadOnly);

			while (results.hasMore())
			{
				Object o = results.next();

				// Om metoden getValueObject saknas, kastas ett undantag.
				resultList.add(o.getClass().getDeclaredMethod("getValueObject", new Class[0]).invoke(o, new Object[0]));
			}
		}
		catch(NoSuchMethodException e)
		{
			throw new Bug("The object [" + arg.getName() + "] is of the wrong type. This should never happen.", e);
		}
		catch(Exception e)
		{
			throw new SystemException("An error occurred when we tried to fetch " + arg.getName() + " Reason:" + e.getMessage(), e);
		}

		return Collections.unmodifiableList(resultList);
	}



	/**
	 * Begins a transaction on the named database
	 */

	public static void beginTransaction(Database db) throws SystemException
	{
		try
		{
			db.begin();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new SystemException("An error occurred when we tried to begin an transaction. Reason:" + e.getMessage(), e);
		}
	}

	/**
	 * Ends a transaction on the named database
	 */

	public static void commitTransaction(Database db) throws SystemException
	{
		try
		{
			db.commit();
			db.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new SystemException("An error occurred when we tried to commit an transaction. Reason:" + e.getMessage(), e);
		}
	}


	/**
	 * Rollbacks a transaction on the named database
	 */

	public static void rollbackTransaction(Database db) throws SystemException
	{
		try
		{
			if (db.isActive())
			{
				db.rollback();
				db.close();
			}
		}
		catch(Exception e)
		{
			CmsLogger.logInfo("An error occurred when we tried to rollback an transaction. Reason:" + e.getMessage());
			//throw new SystemException("An error occurred when we tried to rollback an transaction. Reason:" + e.getMessage(), e);
		}
	}

	/**
	 * Close the database
	 */

	public static void closeDatabase(Database db) throws SystemException
	{
		try
		{
			db.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new SystemException("An error occurred when we tried to close a database. Reason:" + e.getMessage(), e);
		}
	}
}