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

import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;

/**
 * This class is responsible for letting an external application call InfoGlue
 * API:s remotely. It handles api:s to manage contents and associated entities.
 * 
 * @author Mattias Bogeblad
 */

public class RemoteContentServiceImpl 
{
    private static ContentController contentController = ContentController.getContentController();
    private static ContentVersionController contentVersionController = ContentVersionController.getContentVersionController();
    
    /**
     * Inserts a new Content including versions etc.
     */
    
    public int createContent(ContentVO contentVO, int parentContentId, int contentTypeDefinitionId, int repositoryId) 
    {
        int newContentId = 0;
        
        System.out.println("***************************************");
        System.out.println("Creating content through webservice....");
        System.out.println("***************************************");
        
        try
        {
	        ContentVO newContentVO = contentController.create(new Integer(parentContentId), new Integer(contentTypeDefinitionId), new Integer(repositoryId), contentVO);
	        newContentId = newContentVO.getId().intValue();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return newContentId;
    }
    
    /**
     * Inserts a new ContentVersion.
     */
    
    public int createContentVersion(ContentVersionVO contentVersionVO, int contentId, int languageId) 
    {
        int newContentVersionId = 0;
        
        System.out.println("***************************************");
        System.out.println("Creating content through webservice....");
        System.out.println("***************************************");
        
        try
        {
	        ContentVersionVO newContentVersionVO = contentVersionController.create(new Integer(contentId), new Integer(languageId), contentVersionVO, null);
	        newContentVersionId = newContentVersionVO.getId().intValue();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return newContentVersionId;
    }
    
 

}
