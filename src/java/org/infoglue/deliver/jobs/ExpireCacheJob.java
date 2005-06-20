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
package org.infoglue.deliver.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.CmsJDOCallback;
import org.infoglue.cms.entities.content.impl.simple.SmallContentImpl;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.deliver.util.CacheController;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author mattias
 *
 * This jobs searches for expiring contents or sitenodes and clears caches if found.
 */

public class ExpireCacheJob implements Job
{

    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        try
        {
            CacheController.evictWaitingCache();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        CmsLogger.logInfo("---" + context.getJobDetail().getFullName() + " executing.[" + new Date() + "]");
        
        try
        {
            Date firstExpireDateTime = CacheController.expireDateTime;
            CmsLogger.logInfo("firstExpireDateTime:" + firstExpireDateTime);
            Date now = new Date();
            
            if(firstExpireDateTime != null && now.after(firstExpireDateTime))
            {
                CacheController.clearCaches(null, null);
                CacheController.clearCastorCaches();
            }

            Date firstPublishDateTime = CacheController.publishDateTime;
            CmsLogger.logInfo("firstPublishDateTime:" + firstPublishDateTime);
            
            if(firstPublishDateTime != null && now.after(firstPublishDateTime))
            {
                CacheController.clearCaches(null, null);
                CacheController.clearCastorCaches();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    

}
