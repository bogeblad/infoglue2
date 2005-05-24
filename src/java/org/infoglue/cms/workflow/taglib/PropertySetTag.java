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

import com.opensymphony.module.propertyset.PropertySet;

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
		final String value = getValueAsString();
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

	/**
	 * 
	 */
	private String getValueAsString() throws JspException {
		final PropertySet ps = WorkflowHelper.getPropertySet(pageContext.getSession(), pageContext.getRequest());
		
		if(!ps.exists(key))
			return "";
		
		switch(ps.getType(key)) {
			case PropertySet.BOOLEAN:
				return new Boolean(ps.getBoolean(key)).toString();
			case PropertySet.DATA:
				return WorkflowHelper.getPropertyData(ps, key);
			case PropertySet.DATE:
				return ps.getDate(key).toString();
			case PropertySet.DOUBLE:
				return new Double(ps.getDouble(key)).toString();
			case PropertySet.INT:
				return new Integer(ps.getInt(key)).toString();
			case PropertySet.LONG:
				return new Long(ps.getLong(key)).toString();
			case PropertySet.STRING:
				return ps.getString(key);
			case PropertySet.TEXT:
				return ps.getText(key);
			default:
				throw new JspTagException("PropertySetTag.getValueAsString() - unsupported type " + ps.getType(key) + ".");
		}
	}
	
	/**
	 * 
	 */
    public void setKey(String key) {
        this.key = key;
    }

	/**
	 * 
	 */
    public void setId(String id) {
        this.id = id;
    }
}
