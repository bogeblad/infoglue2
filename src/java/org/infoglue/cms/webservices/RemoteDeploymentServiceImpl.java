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

package org.infoglue.cms.webservices;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ServerNodeController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.deliver.util.webservices.DynamicWebserviceSerializer;


/**
 * This class is responsible for all deployment actions.
 * 
 * @author Mattias Bogeblad
 */

public class RemoteDeploymentServiceImpl extends RemoteInfoGlueService
{
    private final static Logger logger = Logger.getLogger(RemoteDeploymentServiceImpl.class.getName());

	/**
	 * The principal executing the workflow.
	 */
	private InfoGluePrincipal principal;

	private static ContentTypeDefinitionController contentTypeDefinitionController = ContentTypeDefinitionController.getController();
	//private static ContentControllerProxy contentControllerProxy = ContentControllerProxy.getController();
    //private static ContentVersionControllerProxy contentVersionControllerProxy = ContentVersionControllerProxy.getController();
    

	/**
     * Gets all content type definitions from the cms.
     */
    
    public List<ContentTypeDefinitionVO> getContentTypeDefinitions(final String principalName) 
    {
        if(!ServerNodeController.getController().getIsIPAllowed(getRequest()))
        {
            logger.error("A client with IP " + getRequest().getRemoteAddr() + " was denied access to the webservice. Could be a hack attempt or you have just not configured the allowed IP-addresses correct.");
            return null;
        }
        
        if(logger.isInfoEnabled())
        {
	        logger.info("******************************************************");
	        logger.info("* Getting content type definition through webservice *");
	        logger.info("******************************************************");
        }
	        
        List<ContentTypeDefinitionVO> contentTypeDefinitionVOList = new ArrayList<ContentTypeDefinitionVO>();
        
        try
        {
			final DynamicWebserviceSerializer serializer = new DynamicWebserviceSerializer();
	        
			if(logger.isInfoEnabled())
	        {
		        logger.info("principalName:" + principalName);
	        }
	        
            contentTypeDefinitionVOList = contentTypeDefinitionController.getContentTypeDefinitionVOList();
        }
        catch(Throwable t)
        {
            logger.error("En error occurred when we tried to get the contentVersionVO:" + t.getMessage(), t);
        }
        
        return contentTypeDefinitionVOList;
    }

    
	/**
	 * Checks if the principal exists and if the principal is allowed to create the workflow.
	 * 
	 * @param userName the name of the user.
	 * @param workflowName the name of the workflow to create.
	 * @throws SystemException if the principal doesn't exists or doesn't have permission to create the workflow.
	 */
	private void initializePrincipal(final String userName) throws SystemException 
	{
		try 
		{
			principal = UserControllerProxy.getController().getUser(userName);
		}
		catch(SystemException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new SystemException(e);
		}
		if(principal == null) 
		{
			throw new SystemException("No such principal [" + userName + "].");
		}
	}


}
