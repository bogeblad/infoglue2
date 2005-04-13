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

package org.infoglue.deliver.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.infoglue.deliver.controllers.kernel.impl.simple.BasicTemplateController;

/**
 * This is an attempt to make an TagLib for all actions against the JSP-writers.
 * 
 * <%@ taglib uri="infoglue" prefix="infoglue" %>
 * <infoglue:getContentAttribute contentId="1368" attributeName="Name"/>
 * <infoglue:getContentAttribute propertyName="Article" attributeName="Title"/>
 *
 * @author Mattias Bogeblad
 */

public class ContentAttributeTag extends TagSupport
{
    //One of these are used
    private String contentId 	= null;
    private String propertyName = null;
    
    private String attributeName;
    
    public ContentAttributeTag()
    {
        super();
    }
    
    public int doEndTag() throws JspTagException
    {
        try
        {
            System.out.println("propertyName:" + propertyName);
            System.out.println("contentId:" + contentId);
            System.out.println("attributeName:" + attributeName);
            BasicTemplateController templateLogic = (BasicTemplateController)this.pageContext.getRequest().getAttribute("org.infoglue.cms.deliver.templateLogic");
            if(contentId != null)
                pageContext.getOut().write(templateLogic.getContentAttribute(new Integer(contentId), attributeName));
            else if(propertyName != null)
                pageContext.getOut().write(templateLogic.getComponentLogic().getContentAttribute(propertyName, attributeName));
        }
        catch(java.io.IOException e)
        {
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        
        return EVAL_PAGE;
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }
    
    public void setContentId(String contentId)
    {
        this.contentId = contentId;
    }
    
    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
    }
}