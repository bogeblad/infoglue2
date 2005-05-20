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
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 
 */
public abstract class ContentInputTag extends TagSupport {
	private String idAttr   = "";
	private String name     = "";
	private String cssClass = "";


	/**
	 * 
	 */
	public ContentInputTag() {
		super();
	}

	/**
	 * 
	 */
	protected void write(String text) throws JspException {
		try {
			pageContext.getOut().write(text);
		} catch(IOException e) {
			e.printStackTrace();
			throw new JspTagException("IO error: " + e.getMessage());
		}
	}

	/**
	 *
	 */
	protected String getContentValue() {
		return WorkflowHelper.getProperty(getName(), pageContext.getSession(), pageContext.getRequest());
	}

	/**
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 */
    public String getIdAttr() {
        return idAttr;
    }
    
	/**
	 * 
	 */
    public void setIdAttr(String idAttr) {
        this.idAttr = idAttr;
    }

	/**
	 * 
	 */
    public String getCssClass() {
        return cssClass;
    }
    
	/**
	 * 
	 */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
}
