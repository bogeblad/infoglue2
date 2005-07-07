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

package org.infoglue.cms.applications.databeans;

/**
 * This bean represents a Asset Key definition. Used mostly by the content type definition editor.
 * 
 * @author Mattias Bogeblad
 */

public class ComponentPropertyDefinition
{
    public final static String BINDING 		= "binding";
    public final static String TEXTFIELD 	= "textfield";
    
    private String name;
    private String type;
    private String entity;
    private String allowedContentTypeNames;
    private Boolean multiple;
    
    public ComponentPropertyDefinition(String name, String type, String entity, Boolean multiple)
    {
        this.name 		= name;
        this.type 		= type;
        this.entity 	= entity;
        this.multiple 	= multiple;
    }
    
    public String getAllowedContentTypeNames()
    {
        return allowedContentTypeNames;
    }
    
    public String getEntity()
    {
        return entity;
    }
    
    public Boolean getMultiple()
    {
        return multiple;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getType()
    {
        return type;
    }
}
