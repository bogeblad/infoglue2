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

import org.exolab.castor.jdo.JDO;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.exception.SystemException;

public class CastorDatabaseService //extends DatabaseService
{
    private static JDO jdo = null;

    public static JDO getJDO() throws SystemException
    {
        if(jdo != null)
            return jdo;

        try
        {
            jdo = new JDO();
            //jdo.setLogWriter(new Logger( System.out ).setPrefix( "INFOGLUE_CMS" ));
            jdo.setDatabaseName("INFOGLUE_CMS");
            jdo.setConfiguration(CastorDatabaseService.class.getResource("/database.xml").toString());
            jdo.setClassLoader(CastorDatabaseService.class.getClassLoader());
            jdo.setCallbackInterceptor(new CmsJDOCallback());
        }
        catch(Exception e)
        {
            throw new SystemException("An error occurred while trying to get a JDO object. Castor message:" + e, e);
        }

        return jdo;
    }

    public static Database getDatabase() throws SystemException
    {
        try
        {
            return getJDO().getDatabase();
        }
        catch(Exception e)
        {
            throw new SystemException("An error occurred while trying to get a Database object. Castor message:" + e, e);
        }
    }
}