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
 * This is an attempt to make an TagLib for attempts to get a AssetUrl:s from a content referenced by a component
 * in a JSP.
 * 
 * <%@ taglib uri="infoglue" prefix="infoglue" %>
 * 
 * <infoglue:component.AssetUrl propertyName="Logotype" assetKey="logotype"/>
 *
 * @author Mattias Bogeblad
 */

public class AssetUrlTag extends TagSupport
{
    private String propertyName;
    private String assetKey;
    private boolean useInheritance		= true;
    
    public AssetUrlTag()
    {
        super();
    }
    
    public int doEndTag() throws JspTagException
    {
        try
        {
            BasicTemplateController templateLogic = (BasicTemplateController)this.pageContext.getRequest().getAttribute("org.infoglue.cms.deliver.templateLogic");
            
            if(assetKey != null)
                pageContext.getOut().write(templateLogic.getComponentLogic().getAssetUrl(propertyName, assetKey, useInheritance));
            else
                pageContext.getOut().write(templateLogic.getComponentLogic().getAssetUrl(propertyName, useInheritance));    
        }
        catch(java.io.IOException e)
        {
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        catch(Exception e)
        {
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        
        return EVAL_PAGE;
    }

    public void setAssetKey(String assetKey)
    {
        this.assetKey = assetKey;
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }
    
    public void setUseInheritance(boolean useInheritance)
    {
        this.useInheritance = useInheritance;
    }
}