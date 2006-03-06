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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.exolab.castor.jdo.Database;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.InterceptionPoint;
import org.infoglue.cms.entities.management.Interceptor;
import org.infoglue.cms.entities.management.InterceptorVO;
import org.infoglue.cms.entities.management.impl.simple.InterceptorImpl;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

/**
 * This class is a helper class for the use case handle Interceptor
 *
 * @author Mattias Bogeblad
 */

public class InterceptorController extends BaseController
{
	
	/**
	 * Factory method
	 */

	public static InterceptorController getController()
	{
		return new InterceptorController();
	}
	
	public Interceptor getInterceptorWithId(Integer interceptorId, Database db) throws SystemException, Bug
	{
		return (Interceptor) getObjectWithId(InterceptorImpl.class, interceptorId, db);
	}
    
	public InterceptorVO getInterceptorVOWithId(Integer interceptorId) throws SystemException, Bug
	{
		return (InterceptorVO) getVOWithId(InterceptorImpl.class, interceptorId);
	}
  
	public List getInterceptorVOList() throws SystemException, Bug
	{
		return getAllVOObjects(InterceptorImpl.class, "interceptorId");
	}

	public List getInterceptionPointVOList(Integer interceptorId) throws SystemException, Bug
	{
		List interceptionPointVOList = null;
		
		Database db = CastorDatabaseService.getDatabase();

		try 
		{
			beginTransaction(db);

			interceptionPointVOList = getInterceptionPointVOList(interceptorId, db);

			commitTransaction(db);
		} 
		catch (Exception e) 
		{
			getLogger().info("An error occurred so we should not complete the transaction:" + e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
		
		return interceptionPointVOList;	
	}
	
	public List getInterceptionPointVOList(Integer interceptorId, Database db)  throws SystemException, Bug
	{
		Interceptor interceptor = this.getInterceptorWithId(interceptorId, db);
		
		Collection interceptionPoints = interceptor.getInterceptionPoints();
		
		return toVOList(interceptionPoints);		
	}
	
	/**
	 * Creates a new InterceptorVO
	 * 
	 * @param interceptorVO
	 * @return
	 * @throws ConstraintException
	 * @throws SystemException
	 */
	
	public InterceptorVO create(InterceptorVO interceptorVO) throws ConstraintException, SystemException
	{
		InterceptorVO newinterceptorVO = null;
		
		Database db = CastorDatabaseService.getDatabase();

		try 
		{
			beginTransaction(db);

			newinterceptorVO = create(interceptorVO, db);
				
			commitTransaction(db);
		} 
		catch (Exception e) 
		{
			getLogger().info("An error occurred so we should not complete the transaction:" + e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
		
		return newinterceptorVO;
	}		
	
	/**
	 * Creates a new InterceptorVO within a transaction
	 * 
	 * @param interceptorVO
	 * @return
	 * @throws ConstraintException
	 * @throws SystemException
	 */
	
	public InterceptorVO create(InterceptorVO interceptorVO, Database db) throws SystemException, Exception
	{
		Interceptor interceptor = new InterceptorImpl();
		interceptor.setValueObject(interceptorVO);
		
		db.create(interceptor);
					
		return interceptor.getValueObject();
	}     

	
	public InterceptorVO update(InterceptorVO interceptorVO) throws ConstraintException, SystemException
	{
		return (InterceptorVO) updateEntity(InterceptorImpl.class, (BaseEntityVO)interceptorVO);
	}        

	public void update(InterceptorVO interceptorVO, String[] values) throws ConstraintException, SystemException
	{
		Database db = CastorDatabaseService.getDatabase();

		try 
		{
			beginTransaction(db);
			
			ConstraintExceptionBuffer ceb = interceptorVO.validate();
			ceb.throwIfNotEmpty();
			
			getLogger().info("InterceptorId:" + interceptorVO.getInterceptorId());
			Interceptor interceptor = this.getInterceptorWithId(interceptorVO.getInterceptorId(), db);

			interceptor.setValueObject(interceptorVO);
			
			Collection interceptionPoints = interceptor.getInterceptionPoints();
			Iterator interceptionPointsIterator = interceptionPoints.iterator();
			while(interceptionPointsIterator.hasNext())
			{
				InterceptionPoint interceptionPoint = (InterceptionPoint)interceptionPointsIterator.next();
				interceptionPoint.getInterceptors().remove(interceptor);
			}
			
			interceptor.getInterceptionPoints().clear();
	    	
			if(values != null)
			{
				for(int i=0; i<values.length; i++)
				{
					String interceptionPointId = values[i];
					InterceptionPoint interceptionPoint = InterceptionPointController.getController().getInterceptionPointWithId(new Integer(interceptionPointId), db);
					interceptor.getInterceptionPoints().add(interceptionPoint);
					interceptionPoint.getInterceptors().add(interceptor);
				}
			}
			
			commitTransaction(db);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			getLogger().info("An error occurred so we should not complete the transaction:" + e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
	}			
	
	public void delete(InterceptorVO interceptorVO) throws ConstraintException, SystemException
	{
		deleteEntity(InterceptorImpl.class, interceptorVO.getInterceptorId());
	}        

	/*
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
	*/

	/**
	 * This is a method that gives the user back an newly initialized ValueObject for this entity that the controller
	 * is handling.
	 */

	public BaseEntityVO getNewVO()
	{
		return new InterceptorVO();
	}

}
 