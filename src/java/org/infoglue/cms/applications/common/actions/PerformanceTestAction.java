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

package org.infoglue.cms.applications.common.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exolab.castor.jdo.Database;
import org.infoglue.cms.controllers.kernel.impl.simple.AccessRightController;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.entities.management.AvailableServiceBindingVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.management.ServiceDefinitionVO;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.controllers.kernel.impl.simple.AvailableServiceBindingDeliveryController;
import org.infoglue.deliver.controllers.kernel.impl.simple.ExtranetController;
import org.infoglue.deliver.controllers.kernel.impl.simple.NodeDeliveryController;
import org.infoglue.deliver.util.Timer;

import webwork.action.Action;
import webwork.action.ActionContext;

/**
 * @author Mattias Bogeblad
 *
 * Here we put all performance tests
 */

public class PerformanceTestAction extends WebworkAbstractAction
{

    public String doExecute() throws Exception
    {
        Integer siteNodeId = new Integer(34);
        Integer languageId = new Integer(1);
        Integer contentId = new Integer(-1);
        String availableServiceBindingName = "Meta information";
     
        Timer timer = new Timer();
        Database db = CastorDatabaseService.getDatabase();
        
        db.begin();

        try
        {
            timer.printElapsedTime("Transaction begun..");
            
	        AvailableServiceBindingVO availableServiceBindingVO = AvailableServiceBindingDeliveryController.getAvailableServiceBindingDeliveryController().getAvailableServiceBindingVO(availableServiceBindingName, db);
	    	System.out.println("availableServiceBindingVO:" + availableServiceBindingVO.getName());
	    	
	    	timer.printElapsedTime("availableServiceBindingVO fetched..");
	    	
	        NodeDeliveryController nodeDeliveryController = NodeDeliveryController.getNodeDeliveryController(siteNodeId, languageId, contentId);

	        List qualifyerList = new ArrayList();
	    	ServiceDefinitionVO serviceDefinitionVO = nodeDeliveryController.getInheritedServiceDefinition(qualifyerList, siteNodeId, availableServiceBindingVO, db, true);
			System.out.println("qualifyerList:" + qualifyerList);
			System.out.println("serviceDefinitionVO:" + serviceDefinitionVO);
			
			//nodeDeliveryController.getBoundContent()
	        //nodeDeliveryController.getInheritedServiceBinding(siteNodeId, availableServiceBindingVO, db, true);

	        db.commit();
        }
        catch(Exception e)
        {
            CmsLogger.logSevere("An error occurred so we should not complete the transaction:" + e, e);
            db.rollback();
            throw new SystemException(e.getMessage());
        }
        finally
        {
            db.close();
        }
        
        timer.printElapsedTime("Getting metainfo");
        
        return Action.NONE;
    }
}

