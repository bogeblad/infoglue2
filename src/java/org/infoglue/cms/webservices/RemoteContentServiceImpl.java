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

import java.math.BigInteger;
import java.util.Map;

import org.apache.log4j.Logger;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.WorkflowController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;


/**
 * This class is responsible for letting an external application call InfoGlue
 * API:s remotely. It handles api:s to manage contents and associated entities.
 * 
 * @author Mattias Bogeblad
 */

public class RemoteContentServiceImpl 
{
    private final static Logger logger = Logger.getLogger(RemoteContentServiceImpl.class.getName());

	/**
	 * The principal executing the workflow.
	 */
	private InfoGluePrincipal principal;

    private static ContentControllerProxy contentControllerProxy = ContentControllerProxy.getController();
    private static ContentVersionControllerProxy contentVersionControllerProxy = ContentVersionControllerProxy.getController();
    
    /**
     * Inserts a new Content including versions etc.
     */
    
    public int createContent(final String principalName, ContentVO contentVO, int parentContentId, int contentTypeDefinitionId, int repositoryId) 
    {
        int newContentId = 0;
        
        logger.info("***************************************");
        logger.info("Creating content through webservice....");
        logger.info("***************************************");
        
        logger.warn("parentContentId:" + parentContentId);
        logger.warn("contentTypeDefinitionId:" + contentTypeDefinitionId);
        logger.warn("repositoryId:" + repositoryId);
        
        try
        {
            initializePrincipal(principalName);
	        ContentVO newContentVO = contentControllerProxy.acCreate(this.principal, new Integer(parentContentId), new Integer(contentTypeDefinitionId), new Integer(repositoryId), contentVO);
	        newContentId = newContentVO.getId().intValue();
        }
        catch(Exception e)
        {
            logger.error("En error occurred when we tried to create a new content:" + e.getMessage(), e);
        }
        
        return newContentId;
    }
    
    /**
     * Inserts a new ContentVersion.
     */
    
    public int createContentVersion(final String principalName, ContentVersionVO contentVersionVO, int contentId, int languageId) 
    {
        int newContentVersionId = 0;
        
        logger.info("***************************************");
        logger.info("Creating content through webservice....");
        logger.info("***************************************");
        
        try
        {
            initializePrincipal(principalName);
	        ContentVersionVO newContentVersionVO = contentVersionControllerProxy.acCreate(this.principal, new Integer(contentId), new Integer(languageId), contentVersionVO);
	        newContentVersionId = newContentVersionVO.getId().intValue();
        }
        catch(Exception e)
        {
            logger.error("En error occurred when we tried to create a new contentVersion:" + e.getMessage(), e);
        }
        
        return newContentVersionId;
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
