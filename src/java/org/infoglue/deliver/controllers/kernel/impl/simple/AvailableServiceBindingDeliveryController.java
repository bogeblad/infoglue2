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

import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;

import org.infoglue.cms.exception.SystemException;

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
	
	public AvailableServiceBindingVO getAvailableServiceBinding(String availableServiceBindingName) throws SystemException, Exception
	{ 
		CmsLogger.logInfo("Going to look for availableServiceBindingName " + availableServiceBindingName);
		Database db = CastorDatabaseService.getDatabase();
        ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

        AvailableServiceBindingVO availableServiceBindingVO = null;

		beginTransaction(db);

        try
        {
    		OQLQuery oql = db.getOQLQuery( "SELECT asb FROM org.infoglue.cms.entities.management.impl.simple.AvailableServiceBindingImpl asb WHERE asb.name = $1");
        	oql.bind(availableServiceBindingName);
			
			QueryResults results = oql.execute(Database.ReadOnly);
			if (results.hasMore()) 
        	{
        		AvailableServiceBinding availableServiceBinding = (AvailableServiceBinding)results.next();
				availableServiceBindingVO = availableServiceBinding.getValueObject();
				CmsLogger.logInfo("Found availableServiceBinding:" + availableServiceBindingVO.getName());
        	}
            
			rollbackTransaction(db);
			//commitTransaction(db);
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not complete the transaction:" + e.getMessage(), e);
			rollbackTransaction(db);
            throw new SystemException(e.getMessage());
        }

        return availableServiceBindingVO;	
	}
	
    
}