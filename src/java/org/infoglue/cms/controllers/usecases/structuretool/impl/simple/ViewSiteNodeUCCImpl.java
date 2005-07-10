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

package org.infoglue.cms.controllers.usecases.structuretool.impl.simple;

import org.infoglue.cms.controllers.usecases.structuretool.ViewSiteNodeUCC;

import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;

import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.entities.structure.SiteNode;

import org.infoglue.cms.exception.*;
import org.infoglue.cms.util.*;

import org.exolab.castor.jdo.Database;

public class ViewSiteNodeUCCImpl extends BaseUCCController implements ViewSiteNodeUCC
{
        
    public SiteNodeVO viewSiteNode(Integer siteNodeId) throws ConstraintException, SystemException
    {
        Database db = CastorDatabaseService.getDatabase();
        ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

        SiteNode siteNode = null;

        beginTransaction(db);

        try
        {
            siteNode = SiteNodeController.getSiteNodeWithId(siteNodeId, db);
        
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

        return siteNode.getValueObject();
    }        
}
        
