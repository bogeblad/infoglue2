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
public class PropertySetTag extends TagSupport {
	private static final long serialVersionUID = 3906372631774179380L;

	private String id;
	private String key;
	
	
    public PropertySetTag() {
        super();
    }

	/**
	 * 
	 */
	public int doEndTag() throws JspException {
		String value = WorkflowHelper.getProperty(key, pageContext.getSession(), pageContext.getRequest());
		if(value != null) {
			try {
				if(id != null)
					pageContext.setAttribute(id, value);
				else
					pageContext.getOut().write(value);
			} catch(IOException e) {
				e.printStackTrace();
				throw new JspTagException("IO error: " + e.getMessage());
			}
		}
        return EVAL_PAGE;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setId(String id) {
        this.id = id;
    }
}
