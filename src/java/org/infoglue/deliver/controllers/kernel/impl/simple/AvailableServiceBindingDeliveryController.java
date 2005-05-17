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

import org.infoglue.cms.entities.management.*;
import org.infoglue.cms.util.*;

import org.infoglue.cms.exception.SystemException;
import org.infoglue.deliver.util.CacheController;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;


public class AvailableServiceBindingDeliveryController extends BaseDeliveryController
{

	/**
	 * Private constructor to enforce factory-use
	 */
	
	private AvailableServiceBindingDeliveryController()
	{
	}
	
	/**
	 * Factory method
	 */
	
	public static AvailableServiceBindingDeliveryController getAvailableServiceBindingDeliveryController()
	{
		return new AvailableServiceBindingDeliveryController();
	}
	

	/**
	 * This method returns the available service binding with a specific name. 
	 */
	
	public AvailableServiceBindingVO getAvailableServiceBindingVO(String availableServiceBindingName, Database db) throws SystemException, Exception
	{ 
	    String key = "" + availableServiceBindingName;
		CmsLogger.logInfo("key:" + key);
		AvailableServiceBindingVO availableServiceBindingVO = (AvailableServiceBindingVO)CacheController.getCachedObject("availableServiceBindingCache", key);
		if(availableServiceBindingVO != null)
		{
		    CmsLogger.logInfo("There was an cached availableServiceBindingVO:" + availableServiceBindingVO);
		}
		else
		{
			CmsLogger.logInfo("Going to look for availableServiceBindingName " + availableServiceBindingName);
			
			//OQLQuery oql = db.getOQLQuery( "SELECT asb FROM org.infoglue.cms.entities.management.impl.simple.AvailableServiceBindingImpl asb WHERE asb.name = $1");
    		OQLQuery oql = db.getOQLQuery( "SELECT asb FROM org.infoglue.cms.entities.management.impl.simple.SmallAvailableServiceBindingImpl asb WHERE asb.name = $1");
    		//OQLQuery oql = db.getOQLQuery( "CALL SQL SELECT availableServiceBindingId, name, description, visualizationAction, isMandatory, isUserEditable, isInheritable FROM cmAvailableServiceBinding WHERE (name = $1) AS org.infoglue.cms.entities.management.impl.simple.AvailableServiceBindingImpl");
        	oql.bind(availableServiceBindingName);
			
			QueryResults results = oql.execute(Database.ReadOnly);
			if (results.hasMore()) 
        	{
        		AvailableServiceBinding availableServiceBinding = (AvailableServiceBinding)results.next();
				availableServiceBindingVO = availableServiceBinding.getValueObject();
				CmsLogger.logInfo("Found availableServiceBinding:" + availableServiceBindingVO.getName());
        	}
            else
            {
                CmsLogger.logInfo("Found no AvailableServiceBindingVO with name " + availableServiceBindingName);
            }
			
			CacheController.cacheObject("availableServiceBindingCache", key, availableServiceBindingVO);
		}
		
	    return availableServiceBindingVO;	
	}
	
	/**
	 * This method returns the available service binding with a specific name. 
	 */
	
	public AvailableServiceBinding getAvailableServiceBinding(String availableServiceBindingName, Database db) throws SystemException, Exception
	{ 
	    AvailableServiceBinding availableServiceBinding = null;
	    
		OQLQuery oql = db.getOQLQuery( "SELECT asb FROM org.infoglue.cms.entities.management.impl.simple.AvailableServiceBindingImpl asb WHERE asb.name = $1");
    	oql.bind(availableServiceBindingName);
		
		QueryResults results = oql.execute(Database.ReadOnly);
		if (results.hasMore()) 
    	{
    		availableServiceBinding = (AvailableServiceBinding)results.next();
			CmsLogger.logInfo("Found availableServiceBinding:" + availableServiceBinding.getName());
    	}
            
        return availableServiceBinding;	
	}
	
}