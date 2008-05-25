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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.Subscription;
import org.infoglue.cms.entities.management.SubscriptionVO;
import org.infoglue.cms.entities.management.impl.simple.SubscriptionImpl;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;

public class SubscriptionController extends BaseController
{ 
    private final static Logger logger = Logger.getLogger(SubscriptionController.class.getName());

	/**
	 * Factory method
	 */

	public static SubscriptionController getController()
	{
		return new SubscriptionController();
	}
	
    public SubscriptionVO getSubscriptionVOWithId(Integer subscriptionId) throws SystemException, Bug
    {
		return (SubscriptionVO) getVOWithId(SubscriptionImpl.class, subscriptionId);
    }

    public Subscription getSubscriptionWithId(Integer subscriptionId, Database db) throws SystemException, Bug
    {
		return (Subscription) getObjectWithId(SubscriptionImpl.class, subscriptionId, db);
    }

    public List getSubscriptionVOList() throws SystemException, Bug
    {
		List subscriptionVOList = getAllVOObjects(SubscriptionImpl.class, "subscriptionId");

		return subscriptionVOList;
    }

    public List getSubscriptionVOList(Database db) throws SystemException, Bug
    {
		List subscriptionVOList = getAllVOObjects(SubscriptionImpl.class, "subscriptionId", db);

		return subscriptionVOList;
    }

    public SubscriptionVO create(SubscriptionVO subscriptionVO) throws ConstraintException, SystemException
    {
        Subscription subscription = new SubscriptionImpl();
        subscription.setValueObject(subscriptionVO);
        subscription = (Subscription) createEntity(subscription);
        return subscription.getValueObject();
    }

    public void delete(SubscriptionVO subscriptionVO) throws ConstraintException, SystemException
    {
    	deleteEntity(SubscriptionImpl.class, subscriptionVO.getSubscriptionId());
    }

    public SubscriptionVO update(SubscriptionVO subscriptionVO) throws ConstraintException, SystemException
    {
    	return (SubscriptionVO) updateEntity(SubscriptionImpl.class, subscriptionVO);
    }

    
	/**
	 * Gets matching subscriptions
	 */
	
	public List<SubscriptionVO> getSubscriptionVOList(Integer interceptionPointId, String entityName, String entityId, String userName, String userEmail) throws SystemException, Exception
	{
		List subscriptionVOList = new ArrayList();
		
		Database db = CastorDatabaseService.getDatabase();
		
		try 
		{
			beginTransaction(db);
			
			List subscriptionList = getSubscriptionList(interceptionPointId, entityName, entityId, userName, userEmail, db, true);
			subscriptionVOList = toVOList(subscriptionList);
			
	        commitTransaction(db);
		}
		catch (Exception e)		
		{
		    logger.warn("An error occurred so we should not complete the transaction:" + e);
		    rollbackTransaction(db);
		}
		
		return subscriptionVOList;
	}

	/**
	 * Gets matching subscriptions
	 */
	
	public List getSubscriptionList(Integer interceptionPointId, String entityName, String entityId, String userName, String userEmail, Database db, boolean readOnly) throws SystemException, Exception
	{
	    List subscriptionList = new ArrayList();
	    
	    StringBuffer sql = new StringBuffer("SELECT s FROM org.infoglue.cms.entities.management.impl.simple.SubscriptionImpl s WHERE ");
	    List bindings = new ArrayList();
	    int bindingIndex = 1;
	    
	    if(interceptionPointId != null)
	    {
	    	if(bindingIndex > 1)
	    		sql.append(" AND ");
	    	sql.append("s.interceptionPointId = $" + bindingIndex);
	    	bindings.add(interceptionPointId);
	    	bindingIndex++;
	    }

	    if(entityName != null)
	    {
	    	if(bindingIndex > 1)
	    		sql.append(" AND ");
	    	sql.append("s.entityName = $" + bindingIndex);
	    	bindings.add(entityName);
	    	bindingIndex++;
	    }

	    if(entityId != null)
	    {
	    	if(bindingIndex > 1)
	    		sql.append(" AND ");
	    	sql.append("s.entityId = $" + bindingIndex);
	    	bindings.add(entityId);
	    	bindingIndex++;
	    }

	    if(userName != null)
	    {
	    	if(bindingIndex > 1)
	    		sql.append(" AND ");
	    	sql.append("s.userName = $" + bindingIndex);
	    	bindings.add(userName);
	    	bindingIndex++;
	    }

	    if(userEmail != null)
	    {
	    	if(bindingIndex > 1)
	    		sql.append(" AND ");
	    	sql.append("s.userEmail = $" + bindingIndex);
	    	bindings.add(userEmail);
	    	bindingIndex++;
	    }

	    sql.append(" ORDER BY s.subscriptionId");
	    
	    System.out.println("sql: " + sql);
		OQLQuery oql = db.getOQLQuery(sql.toString());
		Iterator bindingsIterator = bindings.iterator();
		while(bindingsIterator.hasNext())
			oql.bind(bindingsIterator.next());
		
		QueryResults results = null;
		if(!readOnly)
			results = oql.execute();
		else
			results = oql.execute(Database.ReadOnly);
			
		while (results.hasMore()) 
        {
            Subscription subscription = (Subscription)results.next();
            subscriptionList.add(subscription);
        }            
		
		results.close();
		oql.close();

		return subscriptionList;		
	}
	
	

	public BaseEntityVO getNewVO()
	{
		return null; //new SubscriptionVO();
	}
	
}
 
