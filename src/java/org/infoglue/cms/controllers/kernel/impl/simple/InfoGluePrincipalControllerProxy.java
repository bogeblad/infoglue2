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
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.AuthorizationModule;
import org.infoglue.cms.security.InfoGlueAuthenticationFilter;
import org.infoglue.cms.security.InfoGluePrincipal;

/**
 * @author Mattias Bogeblad
 * 
 * This class acts as the proxy for getting a principal from the right source. The source may vary depending
 * on security setup.
 */

public class InfoGluePrincipalControllerProxy extends BaseController 
{

	public static InfoGluePrincipalControllerProxy getController()
	{
		return new InfoGluePrincipalControllerProxy();
	}
	
	/**
	 * This method returns a specific content-object
	 */
	
    public InfoGluePrincipal getInfoGluePrincipal(String userName) throws ConstraintException, SystemException
    {
		InfoGluePrincipal infoGluePrincipal = null;
    	
    	try
    	{
			AuthorizationModule authorizationModule = (AuthorizationModule)Class.forName(InfoGlueAuthenticationFilter.authorizerClass).newInstance();
			authorizationModule.setExtraProperties(InfoGlueAuthenticationFilter.extraProperties);
			
			infoGluePrincipal = authorizationModule.getAuthorizedInfoGluePrincipal(userName);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return infoGluePrincipal;
    }
 
    
	public BaseEntityVO getNewVO()
	{
		return null;
	}
}
