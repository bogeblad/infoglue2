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

package org.infoglue.deliver.taglib.content;

import javax.servlet.jsp.JspException;

import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.deliver.taglib.component.ComponentLogicTag;

/**
 * Tag for org.infoglue.deliver.controllers.kernel.impl.simple.TemplateController.getContentAttribute(<String>, <Sring>, <boolean>);
 */
public class ContentTag extends ComponentLogicTag
{
	private static final long serialVersionUID = 3258135773294113587L;

	private Integer contentId;
	private String propertyName;
    private boolean useInheritance = true;
    
    public ContentTag()
    {
        super();
    }
    
    public int doEndTag() throws JspException
    {
		produceResult(getContent());
        return EVAL_PAGE;
    }

	private ContentVO getContent() throws JspException
	{
	    if(this.contentId != null)
	        return this.getController().getContent(this.contentId);
	    else if(this.propertyName != null)
	        return this.getComponentLogic().getBoundContent(propertyName, useInheritance);
	    else if(this.getController().getContentId() != null && this.getController().getContentId().intValue() > -1)
	        return this.getController().getContent();
	    else
	        return null;
	}
	
    public void setContentId(Integer contentId)
    {
        this.contentId = contentId;
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