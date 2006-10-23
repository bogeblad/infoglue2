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

package org.infoglue.deliver.taglib.structure;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.component.ComponentLogicTag;

public class ChildPagesTag extends ComponentLogicTag {
	private static final long serialVersionUID = 4050206323348354355L;

	private Integer siteNodeId;
	private String propertyName;
	private boolean useInheritance = true;
	private boolean escapeHTML = false;
	private boolean hideUnauthorizedPages = false;
		
	
    public ChildPagesTag()
    {
        super();
    }

	public int doEndTag() throws JspException
    {
	    if(this.siteNodeId != null)
	        setResultAttribute(this.getController().getChildPages(this.siteNodeId, this.escapeHTML, this.hideUnauthorizedPages));
        else if(this.propertyName != null)
            setResultAttribute(getComponentLogic().getChildPages(propertyName, useInheritance, this.escapeHTML, this.hideUnauthorizedPages));
        else
            throw new JspException("You must state either propertyName or siteNodeId");
	    
	    return EVAL_PAGE;
    }

    public void setPropertyName(String propertyName) throws JspException
    {
        this.propertyName = evaluateString("childPages", "propertyName", propertyName);
    }
	
    public void setSiteNodeId(String siteNodeId) throws JspException
    {
        this.siteNodeId = evaluateInteger("ChildPagesTag", "siteNodeId", siteNodeId);
    }
    
    public void setUseInheritance(boolean useInheritance)
    {
        this.useInheritance = useInheritance;
    }

	public void setEscapeHTML(boolean escapeHTML) 
	{
		this.escapeHTML = escapeHTML;
	}

	public void setHideUnauthorizedPages(boolean hideUnauthorizedPages) 
	{
		this.hideUnauthorizedPages = hideUnauthorizedPages;
	}
}
