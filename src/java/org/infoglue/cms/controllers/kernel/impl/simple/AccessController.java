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

import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.Access;
import org.infoglue.cms.entities.management.AccessVO;
import org.infoglue.cms.entities.management.impl.simple.AccessImpl;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.exception.*;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.security.InfoGlueRole;
import org.infoglue.cms.util.CmsLogger;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

/**
 * This class is a helper class for the use case handle Accesss
 *
 * @author Mattias Bogeblad
 */

public class AccessController extends BaseController
{
	
	/**
	 * Factory method
	 */

	public static AccessController getController()
	{
		return new AccessController();
	}
	
	public Access getAccessWithId(Integer AccessId, Database db) throws SystemException, Bug
	{
		return (Access) getObjectWithId(AccessImpl.class, AccessId, db);
	}
    
	public AccessVO getAccessVOWithId(Integer AccessId) throws SystemException, Bug
	{
		return (AccessVO) getVOWithId(AccessImpl.class, AccessId);
	}
  
	public List getAccessVOList() throws SystemException, Bug
	{
		return getAllVOObjects(AccessImpl.class, "accessId");
	}

	public List getAccessVOList(String name, String value) throws SystemException, Bug
	{
		List AccessVOList = null;
		Database db = CastorDatabaseService.getDatabase();

		try 
		{
			beginTransaction(db);

			AccessVOList = getAccessVOList(name, value, db);

			commitTransaction(db);
		} 
		catch (Exception e) 
		{
			CmsLogger.logInfo("An error occurred so we should not complete the transaction:" + e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
		return AccessVOList;	
	}
	
	public List getAccessVOList(String name, String value, Database db)  throws SystemException, Bug
	{
		ArrayList AccessVOList = new ArrayList();
		try
		{
			CmsLogger.logInfo("name:" + name);
			CmsLogger.logInfo("value:" + value);
			OQLQuery oql = db.getOQLQuery("SELECT f FROM org.infoglue.cms.entities.management.impl.simple.AccessImpl f WHERE f.name = $1 AND f.value = $2");
			oql.bind(name);
			oql.bind(value);
			
			QueryResults results = oql.execute();
			while (results.hasMore()) 
			{
				Access Access = (Access)results.next();
				AccessVOList.add(Access.getValueObject());
			}
		}
		catch(Exception e)
		{
			throw new SystemException("An error occurred when we tried to fetch a list of Function. Reason:" + e.getMessage(), e);    
		}
		
		return AccessVOList;		
	}

	public List getAccessList(String name, String value, Database db)  throws SystemException, Bug
	{
		ArrayList AccessList = new ArrayList();
		try
		{
			OQLQuery oql = db.getOQLQuery("SELECT f FROM org.infoglue.cms.entities.management.impl.simple.AccessImpl f WHERE f.name = $1 AND f.value = $2");
			oql.bind(name);
			oql.bind(value);
			
			QueryResults results = oql.execute();
			while (results.hasMore()) 
			{
				Access Access = (Access)results.next();
				AccessList.add(Access);
			}
		}
		catch(Exception e)
		{
			throw new SystemException("An error occurred when we tried to fetch a list of Function. Reason:" + e.getMessage(), e);    
		}
		
		return AccessList;		
	}

	/**
	 * This method creates an access note.
	 * 
	 * @param accessVO
	 * @param db
	 * @return
	 * @throws SystemException
	 * @throws Exception
	 */
	
	public AccessVO create(AccessVO accessVO, Database db) throws SystemException, Exception
	{
		Access access = new AccessImpl();
		access.setValueObject(accessVO);
		
		db.create(access);
					
		return access.getValueObject();
	}     

	
	public AccessVO update(AccessVO AccessVO) throws ConstraintException, SystemException
	{
		return (AccessVO) updateEntity(AccessImpl.class, (BaseEntityVO) AccessVO);
	}        

	
	public void update(String name, String value, String roleName) throws ConstraintException, SystemException
	{
		Database db = CastorDatabaseService.getDatabase();

		try 
		{
			beginTransaction(db);

			delete(name, value, db);
	    	
			String hasReadAccess  = "true";
			String hasWriteAccess = "true";
		
			if(hasReadAccess != null || hasWriteAccess != null)
			{
				AccessVO AccessVO = new AccessVO();
				AccessVO.setRoleName(roleName);
				AccessVO.setName(name);
				AccessVO.setValue(value);
				AccessVO.setHasReadAccess(new Boolean(hasReadAccess));
				AccessVO.setHasWriteAccess(new Boolean(hasWriteAccess));
			
				AccessController.getController().create(AccessVO, db);
			}
				
			commitTransaction(db);
		} 
		catch (Exception e) 
		{
			CmsLogger.logInfo("An error occurred so we should not complete the transaction:" + e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
	}		
	
	public void update(String name, String value, HttpServletRequest request) throws ConstraintException, SystemException
	{
		Database db = CastorDatabaseService.getDatabase();

		try 
		{
			beginTransaction(db);

			delete(name, value, db);
	    	
			int roleIndex = 0;
			String roleName = request.getParameter(roleIndex + "_roleName");
			while(roleName != null)
			{
				String hasReadAccess  = request.getParameter(roleName + "_hasRoleReadAccess");
				String hasWriteAccess = request.getParameter(roleName + "_hasRoleWriteAccess");
			
				if(hasReadAccess != null || hasWriteAccess != null)
				{
					AccessVO AccessVO = new AccessVO();
					AccessVO.setRoleName(roleName);
					AccessVO.setName(name);
					AccessVO.setValue(value);
					AccessVO.setHasReadAccess(new Boolean(hasReadAccess));
					AccessVO.setHasWriteAccess(new Boolean(hasWriteAccess));
				
					AccessController.getController().create(AccessVO, db);
				}
				
				roleIndex++;
				roleName = request.getParameter(roleIndex + "_roleName");
			}
			commitTransaction(db);
		} 
		catch (Exception e) 
		{
			CmsLogger.logInfo("An error occurred so we should not complete the transaction:" + e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
	}			
	
	
	public void delete(AccessVO AccessVO) throws ConstraintException, SystemException
	{
		deleteEntity(AccessImpl.class, AccessVO.getAccessId());
	}        

	/**
	 * This method deletes all occurrencies of Access which has the name and value(ie concerning a special entity).
	 * 
	 * @param name
	 * @param value
	 * @throws ConstraintException
	 * @throws SystemException
	 */

	public void delete(String name, String value, Database db) throws SystemException, Exception
	{
		List AccessList = getAccessList(name, value, db);
		Iterator i = AccessList.iterator();
		while(i.hasNext())
		{
			Access Access = (Access)i.next();
			db.remove(Access);
		}
		
	}        

	
	/**
	 * This method checks if a role has access to an entity. It takes name and id of the entity. 
	 */
	
	public boolean getIsPrincipalAuthorized(InfoGluePrincipal infoGluePrincipal, String name, String value) throws Exception
	{
		if(infoGluePrincipal.getIsAdministrator())
			return true;
			
		boolean isPrincipalAuthorized = false;
		
		Database db = CastorDatabaseService.getDatabase();

		try 
		{
			beginTransaction(db);
			
			isPrincipalAuthorized = getIsPrincipalAuthorized(db, infoGluePrincipal, name, value);
		
			commitTransaction(db);
		} 
		catch (Exception e) 
		{
			CmsLogger.logInfo("An error occurred so we should not complete the transaction:" + e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
					
		return isPrincipalAuthorized;
	}
	
	
	/**
	 * This method checks if a role has access to an entity. It takes name and id of the entity. 
	 */
	
	public boolean getIsPrincipalAuthorized(Database db, InfoGluePrincipal infoGluePrincipal, String name, String value) throws Exception
	{		
		if(infoGluePrincipal.getIsAdministrator())
			return true;

		boolean isPrincipalAuthorized = false;
		
		Collection roles = infoGluePrincipal.getRoles();
		CmsLogger.logInfo("roles: " + roles);
			
		Iterator i = roles.iterator();
		while(i.hasNext())
		{
			InfoGlueRole role = (InfoGlueRole)i.next();
			CmsLogger.logInfo("InfoGlueRole:" + role.getName());
			CmsLogger.logInfo("name:" + name);
			CmsLogger.logInfo("value:" + value);
			
			OQLQuery oql = db.getOQLQuery("SELECT f FROM org.infoglue.cms.entities.management.impl.simple.AccessImpl f WHERE f.name = $1 AND f.value = $2 AND f.roleName = $3");
			oql.bind(name);
			oql.bind(value);
			oql.bind(role.getName());

			QueryResults results = oql.execute();
			CmsLogger.logInfo("Anything:" + results.hasMore());
			if (results.hasMore()) 
			{
				isPrincipalAuthorized = true;
				break;
			}
		}
		
		if(!isPrincipalAuthorized)
		{
			if(name.equals("SiteNode"))
			{
				SiteNodeVO parentSiteNodeVO = SiteNodeController.getParentSiteNode(new Integer(value));
				if(parentSiteNodeVO != null)
					isPrincipalAuthorized = getIsPrincipalAuthorized(db, infoGluePrincipal, name, parentSiteNodeVO.getSiteNodeId().toString());
			}
			if(name.equals("Content"))
			{
				ContentVO parentContentVO = ContentController.getParentContent(new Integer(value));
				if(parentContentVO != null)
					isPrincipalAuthorized = getIsPrincipalAuthorized(db, infoGluePrincipal, name, parentContentVO.getId().toString());
			}
		}
		
		return isPrincipalAuthorized;
	}

	/**
	 * This is a method that gives the user back an newly initialized ValueObject for this entity that the controller
	 * is handling.
	 */

	public BaseEntityVO getNewVO()
	{
		return new AccessVO();
	}

}
 