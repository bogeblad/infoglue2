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

import java.util.Map;

import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.entities.content.ContentVO;

/**
 * This class is responsible for letting an external application call InfoGlue
 * API:s remotely. It handles api:s to manage contents and associated entities.
 * 
 * @author Mattias Bogeblad
 */

public class RemoteContentServiceImpl 
{
    private static ContentController contentController = ContentController.getContentController();
    
    /**
     * Inserts a new Content including versions etc.
     */
    
    public String createContent(Map parameters) 
    {
        System.out.println("***************************************");
        System.out.println("Creating content through webservice....");
        System.out.println("***************************************");
        try
        {
            String name 							= (String)parameters.get("name");
            String userName 						= (String)parameters.get("userName");
            String parentContentIdString			= (String)parameters.get("parentContentId");
            String contentTypeDefinitionIdString 	= (String)parameters.get("contentTypeDefinitionId");
            String repositoryIdString 				= (String)parameters.get("repositoryId");
            
            Integer parentContentId = new Integer(parentContentIdString);
            Integer contentTypeDefinitionId = new Integer(contentTypeDefinitionIdString);
            Integer repositoryId = new Integer(repositoryIdString);
            
            System.out.println("name:" + name);
            System.out.println("userName:" + userName);
            System.out.println("parentContentId:" + parentContentId);
            System.out.println("contentTypeDefinitionId:" + contentTypeDefinitionId);
            System.out.println("repositoryId:" + repositoryId);
            
	        ContentVO contentVO = new ContentVO();
	        contentVO.setName(name);
	        contentVO.setCreatorName(userName);
	        
	        contentController.create(parentContentId, contentTypeDefinitionId, repositoryId, contentVO);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return "ERROR";
        }
        
        return "SUCCESS";
    }

}
