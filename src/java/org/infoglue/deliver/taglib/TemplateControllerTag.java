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

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import org.infoglue.deliver.controllers.kernel.impl.simple.TemplateController;

/**
 * Base class for all Tags operating on the TemplateController.
 */
public abstract class TemplateControllerTag extends AbstractTag {
	private String id;
	
	
	protected TemplateControllerTag()
	{
		super();
	}
	
    public void setId(String id)
    {
        this.id = id;
    }

	protected void setResultAttribute(Object value)
	{
		if(value == null)
			pageContext.removeAttribute(id);
		else
			pageContext.setAttribute(id, value);
		
	}
	
	protected void produceResult(Object value) throws JspTagException
	{
	    if(id == null)
			write(value.toString());
		else
			setResultAttribute(value);
	}

	/**
	 * Note! Do not called this function before the PageContext is initialized.
	 */
	protected TemplateController getController() throws JspTagException
	{
	    TemplateController controller;
    	
	    try 
		{
			controller = (TemplateController) this.pageContext.getRequest().getAttribute("org.infoglue.cms.deliver.templateLogic");
			if(controller == null)
				throw new NullPointerException("No TemplateController found in context.");
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			throw new JspTagException("PageContext error: " + e.getMessage());
		}
			
		return controller;
	}
	
	protected void write(String s) throws JspTagException
	{
		try 
		{
			pageContext.getOut().write(s);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw new JspTagException("IO error: " + e.getMessage());
		}
	}
}
