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

package org.infoglue.deliver.taglib.page;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.TemplateControllerTag;

/**
 * Tag for org.infoglue.deliver.controllers.kernel.impl.simple.TemplateController.getContentAttribute(<String>, <Sring>, <boolean>);
 */
public class ContentAttributeTag extends TemplateControllerTag
{
	private static final long serialVersionUID = 3258135773294113587L;

	private String propertyName;
    private String attributeName;
	private boolean clean = false;
    
    public ContentAttributeTag()
    {
        super();
    }
    
    public int doEndTag() throws JspException
    {
		produceResult(getContentAttributeValue());
        return EVAL_PAGE;
    }

	private String getContentAttributeValue() throws JspException
	{
		if(propertyName == null)
			return getController().getContentAttribute(attributeName, clean);
		else
			return getController().getContentAttribute(propertyName, attributeName, clean);
	}
	
	public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }
    
    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
    }

    public void setClean(boolean clean)
    {
        this.clean = clean;
    }
}