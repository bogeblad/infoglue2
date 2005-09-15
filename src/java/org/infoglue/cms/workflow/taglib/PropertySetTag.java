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

import javax.servlet.jsp.JspException;

/**
 * 
 */
public class PropertySetTag extends WorkflowTag 
{
	/**
	 * The universal version identifier.
	 */
	private static final long serialVersionUID = -8111517888511388857L;

	/**
	 * 
	 */
	private String id;
	
	/**
	 * 
	 */
	private String key;
	
	/**
	 * Default constructor.
	 */
    public PropertySetTag() 
    {
        super();
    }

	/**
	 * 
	 */
	public int doEndTag() throws JspException {
		final String value = getPropertySet().getAsString(key);
		if(value != null && id != null)
		{
			pageContext.setAttribute(id, value);
		}
		if(value != null && id == null)
		{
			write(value);
		}
        return EVAL_PAGE;
    }

	/**
	 * 
	 */
    public void setKey(final String key) 
    {
        this.key = key;
    }

	/**
	 * 
	 */
    public void setId(final String id) 
    {
        this.id = id;
    }
}
