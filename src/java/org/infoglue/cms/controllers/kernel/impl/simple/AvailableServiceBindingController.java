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
import org.infoglue.cms.entities.management.AvailableServiceBinding;
import org.infoglue.cms.entities.management.AvailableServiceBindingVO;
import org.infoglue.cms.entities.management.SiteNodeTypeDefinition;
import org.infoglue.cms.entities.management.impl.simple.AvailableServiceBindingImpl;
import org.infoglue.cms.entities.management.impl.simple.ServiceDefinitionImpl;

import org.infoglue.cms.exception.*;

import org.infoglue.cms.util.ConstraintExceptionBuffer;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import java.util.List;
import java.util.Collection;

/**
 * This controller handles all available service bindings persistence and logic
 * 
 * @author mattias
 */

public class AvailableServiceBindingController extends BaseController
{
	/*
	 * Factory method
	 */
	
	public static AvailableServiceBindingController getController()
	{
		return new AvailableServiceBindingController();
	}
	
    public AvailableServiceBindingVO getAvailableServiceBindingVOWithId(Integer availableServiceBindingId) throws SystemException, Bug
    {
		return (AvailableServiceBindingVO)getVOWithId(AvailableServiceBindingImpl.class, availableServiceBindingId);
    }

	
    public AvailableServiceBindingVO create(AvailableServiceBindingVO vo) throws ConstraintException, SystemException
    {
        AvailableServiceBinding ent = new AvailableServiceBindingImpl();
        ent.setValueObject(vo);
        ent = (AvailableServiceBinding) createEntity(ent);
        return ent.getValueObject();
    }     

	/**
	 * This method deletes an available service binding but only as long as 
	 * there are no siteNodes which has serviceBindings referencing it.
	 */
	
    public void delete(AvailableServiceBindingVO availableServiceBindingVO) throws ConstraintException, SystemException
    {
    	Database db = CastorDatabaseService.getDatabase();
        ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
		
        beginTransaction(db);

        try
        { 
        	AvailableServiceBinding availableServiceBinding = getAvailableServiceBindingWithId(availableServiceBindingVO.getAvailableServiceBindingId(), db);
    		if(availableServiceBinding.getServiceBindings() != null && availableServiceBinding.getServiceBindings().size() > 0)
    		{
    			throw new ConstraintException("AvailableServiceBinding.deleteAction", "3100");
    		}	
        }
        catch(ConstraintException ce)
        {
        	throw ce;
        }
        catch(SystemException se)
        {
        	throw se;
        }
        catch(Exception e)
        {
        	throw new SystemException("An error occurred in AvailableServiceBindingController.delete(). Reason:" + e.getMessage(), e);
        }
        finally
        {
        	commitTransaction(db);
        }
        
    	deleteEntity(AvailableServiceBindingImpl.class, availableServiceBindingVO.getAvailableServiceBindingId());
    }        


    public AvailableServiceBinding getAvailableServiceBindingWithId(Integer availableServiceBindingId, Database db) throws SystemException, Bug
    {
		return (AvailableServiceBinding) getObjectWithId(AvailableServiceBindingImpl.class, availableServiceBindingId, db);
    }


    public AvailableServiceBinding getReadOnlyAvailableServiceBindingWithId(Integer availableServiceBindingId, Database db) throws SystemException, Bug
    {
		return (AvailableServiceBinding) getObjectWithIdAsReadOnly(AvailableServiceBindingImpl.class, availableServiceBindingId, db);
    }

    public List getAvailableServiceBindingVOList() throws SystemException, Bug
    {
        return getAllVOObjects(AvailableServiceBindingImpl.class, "availableServiceBindingId");
    }
    
 
    /**
     * This method fetches an available service binding with the given name.
     * 
     * @throws SystemException
     * @throws Bug
     */
    
	public AvailableServiceBindingVO getAvailableServiceBindingVOWithName(String name) throws SystemException, Bug
	{
		AvailableServiceBindingVO availableServiceBindingVO = null;
		
		Database db = CastorDatabaseService.getDatabase();
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

		beginTransaction(db);

		try
		{
			AvailableServiceBinding AvailableServiceBinding = getAvailableServiceBindingWithName(name, db, true);
			if(AvailableServiceBinding != null)
				availableServiceBindingVO = AvailableServiceBinding.getValueObject();
			
			commitTransaction(db);
		}
		catch(Exception e)
		{
			rollbackTransaction(db);
			throw new SystemException("An error occurred when we tried to fetch a list of AvailableServiceBinding. Reason:" + e.getMessage(), e);    
		}
    
		return availableServiceBindingVO;
	}

	
	/**
	 * Returns the AvailableServiceBinding with the given name fetched within a given transaction.
	 * 
	 * @param name
	 * @param database
	 * @return
	 * @throws SystemException
	 * @throws Bug
	 */

	public AvailableServiceBinding getAvailableServiceBindingWithName(String name, Database db, boolean readOnly) throws SystemException, Bug
	{
		AvailableServiceBinding availableServiceBinding = null;
		
		try
		{
			OQLQuery oql = db.getOQLQuery("SELECT a FROM org.infoglue.cms.entities.management.impl.simple.AvailableServiceBindingImpl a WHERE a.name = $1");
			oql.bind(name);
						
			QueryResults results = null;
			
			if(readOnly)
			    results = oql.execute(Database.ReadOnly);
			else
			{
				//this.getLogger().warn("Fetching entity in read/write mode:" + name);
				results = oql.execute();
			}
			
			if(results.hasMore()) 
			{
				availableServiceBinding = (AvailableServiceBinding)results.next();
			}

		}
		catch(Exception e)
		{
			throw new SystemException("An error occurred when we tried to fetch a named AvailableServiceBinding. Reason:" + e.getMessage(), e);    
		}
		
		return availableServiceBinding;		
	}
	
	
	/**
	 * This method returns a List of all assigned AvailableServiceBindings available for a certain Repository.
	 */

	public List getAssignedAvailableServiceBindings(Integer siteNodeTypeDefinitionId) throws ConstraintException, SystemException
	{
		Database db = CastorDatabaseService.getDatabase();
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

		List assignedAvailableServiceBindingVOList = null;

		beginTransaction(db);

		try
		{
			SiteNodeTypeDefinition siteNodeTypeDefinition = SiteNodeTypeDefinitionController.getController().getSiteNodeTypeDefinitionWithId(siteNodeTypeDefinitionId, db);
			Collection assignedAvailableServiceBinding = siteNodeTypeDefinition.getAvailableServiceBindings();
			assignedAvailableServiceBindingVOList = AvailableServiceBindingController.toVOList(assignedAvailableServiceBinding);
        	
			//If any of the validations or setMethods reported an error, we throw them up now before create.
			ceb.throwIfNotEmpty();
            
			commitTransaction(db);
		}
		catch(ConstraintException ce)
		{
			getLogger().warn("An error occurred so we should not complete the transaction:" + ce, ce);
			rollbackTransaction(db);
			throw ce;
		}
		catch(Exception e)
		{
			getLogger().error("An error occurred so we should not complete the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}

		return assignedAvailableServiceBindingVOList;
	}
	
    public AvailableServiceBindingVO update(AvailableServiceBindingVO availableServiceBindingVO) throws ConstraintException, SystemException
    {
        Database db = CastorDatabaseService.getDatabase();
        ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

        AvailableServiceBinding availableServiceBinding = null;

        beginTransaction(db);

        try
        {
            //add validation here if needed
            availableServiceBinding = getAvailableServiceBindingWithId(availableServiceBindingVO.getAvailableServiceBindingId(), db);
            availableServiceBinding.setValueObject(availableServiceBindingVO);

            //If any of the validations or setMethods reported an error, we throw them up now before create.
            ceb.throwIfNotEmpty();
            
            commitTransaction(db);
        }
        catch(ConstraintException ce)
        {
            getLogger().warn("An error occurred so we should not complete the transaction:" + ce, ce);
            rollbackTransaction(db);
            throw ce;
        }
        catch(Exception e)
        {
            getLogger().error("An error occurred so we should not complete the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }


        return availableServiceBinding.getValueObject();
    }        
    
    public AvailableServiceBindingVO update(AvailableServiceBindingVO availableServiceBindingVO, String[] values) throws ConstraintException, SystemException
    {
    	return (AvailableServiceBindingVO) updateEntity(AvailableServiceBindingImpl.class, (BaseEntityVO)availableServiceBindingVO, "setServiceDefinitions", ServiceDefinitionImpl.class, values );
    }        	

	/**
	 * This method returns a list of ServiceDefinitionVO-objects which are available for the
	 * availableServiceBinding sent in.
	 */
	
	public List getServiceDefinitionVOList(Integer availableServiceBindingId) throws ConstraintException, SystemException
	{
		Database db = CastorDatabaseService.getDatabase();
        ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

        List serviceDefinitionVOList = null;

        beginTransaction(db);

        try
        {
            serviceDefinitionVOList = getServiceDefinitionVOList(db, availableServiceBindingId);
        	
            //If any of the validations or setMethods reported an error, we throw them up now before create.
            ceb.throwIfNotEmpty();
            
            commitTransaction(db);
        }
        catch(ConstraintException ce)
        {
            getLogger().warn("An error occurred so we should not complete the transaction:" + ce, ce);
            rollbackTransaction(db);
            throw ce;
        }
        catch(Exception e)
        {
            getLogger().error("An error occurred so we should not complete the transaction:" + e, e);
            rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }

        return serviceDefinitionVOList;
	}
	
	/**
	 * This method returns a list of ServiceDefinitionVO-objects which are available for the
	 * availableServiceBinding sent in.
	 */
	
	public List getServiceDefinitionVOList(Database db, Integer availableServiceBindingId) throws ConstraintException, SystemException
	{
        List serviceDefinitionVOList = null;

        AvailableServiceBinding availableServiceBinding = getReadOnlyAvailableServiceBindingWithId(availableServiceBindingId, db);
        Collection serviceDefinitionList = availableServiceBinding.getServiceDefinitions();
        serviceDefinitionVOList = toVOList(serviceDefinitionList);

        return serviceDefinitionVOList;
	}
	
	/**
	 * This is a method that gives the user back an newly initialized ValueObject for this entity that the controller
	 * is handling.
	 */

	public BaseEntityVO getNewVO()
	{
		return new AvailableServiceBindingVO();
	}

}
 
