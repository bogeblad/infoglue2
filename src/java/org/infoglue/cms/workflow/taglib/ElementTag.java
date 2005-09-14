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
package org.infoglue.cms.workflow.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public abstract class ElementTag extends WorkflowTag 
{
	/**
	 * 
	 */
	private Element element;
	
	/**
	 * 
	 */
	ElementTag()
	{
		super();
	}
	
	/**
	 * 
	 */
	public final int doEndTag() throws JspException 
	{
		process();
		write();
		element = null;
		return EVAL_PAGE;
	}
	
	/**
	 * 
	 */
	protected void process()
	{
	}
	
	/**
	 * 
	 */
	protected void write() throws JspException 
	{
		try 
		{
			pageContext.getOut().write(getElement().toString());
		} 
		catch(IOException e) 
		{
			e.printStackTrace();
			throw new JspTagException(e.getMessage());
		}
	}

	/**
	 * 
	 */
	protected Element getElement()
	{
		if(element == null)
		{
			element = createElement();
		}
		return element;
	}
	
	/**
	 * 
	 */
	protected abstract Element createElement();

	// -------------------------------------------------------------------------	
	// --- core html attributes ------------------------------------------------
	// -------------------------------------------------------------------------	
	
	/**
	 * 
	 */
    public void setIdAttr(final String id) 
    {
    	getElement().attribute("id", id);
    }

	/**
	 * 
	 */
    public void setCssClass(final String css) 
    {
    	getElement().attribute("class", css);
    }

    /**
	 * 
	 */
    public void setTitle(final String title) 
    {
    	getElement().attribute("title", title);
    }
}
