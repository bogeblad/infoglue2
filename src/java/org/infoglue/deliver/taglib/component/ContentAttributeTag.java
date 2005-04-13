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

package org.infoglue.deliver.taglib.component;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.infoglue.deliver.controllers.kernel.impl.simple.BasicTemplateController;

/**
 * This is an attempt to make an TagLib for attempts to get a ContentAttribute from a content referenced by a component
 * in a JSP.
 * 
 * <%@ taglib uri="infoglue" prefix="infoglue" %>
 * 
 * <infoglue:component.ContentAttribute propertyName="Article" attributeName="Title"/>
 *
 * @author Mattias Bogeblad
 */

public class ContentAttributeTag extends TagSupport
{
    private String propertyName;
    private String attributeName;
    private boolean disableEditOnSight 	= false;
    private boolean useInheritance		= true;
    
    public ContentAttributeTag()
    {
        super();
    }
    
    public int doEndTag() throws JspTagException
    {
        try
        {
            BasicTemplateController templateLogic = (BasicTemplateController)this.pageContext.getRequest().getAttribute("org.infoglue.cms.deliver.templateLogic");
            pageContext.getOut().write(templateLogic.getComponentLogic().getContentAttribute(propertyName, attributeName, disableEditOnSight, useInheritance));
        }
        catch(java.io.IOException e)
        {
            e.printStackTrace();
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        
        return EVAL_PAGE;
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }
    
    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
    }
    
    public void setDisableEditOnSight(boolean disableEditOnSight)
    {
        this.disableEditOnSight = disableEditOnSight;
    }
    
    public void setUseInheritance(boolean useInheritance)
    {
        this.useInheritance = useInheritance;
    }
}