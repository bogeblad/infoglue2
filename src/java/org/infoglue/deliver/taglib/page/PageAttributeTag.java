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

import org.apache.log4j.Logger;
import org.infoglue.deliver.taglib.TemplateControllerTag;

/**
 *
 */
public class PageAttributeTag extends TemplateControllerTag
{
    private final static Logger logger = Logger.getLogger(PageAttributeTag.class.getName());

	private static final long serialVersionUID = 3905242346756059449L;
	private String name;
	private Object value;
	private boolean isError = false;

	public int doEndTag() throws JspException
    {
		if(value == null && !isError) //Read operation
			produceResult(getController().getDeliveryContext().getPageAttributes().get(name));
		else if(!isError)
			getController().getDeliveryContext().getPageAttributes().put(name, value);
			
		name = null;
		value = null;
		isError = false;
		
		return EVAL_PAGE;
    }	
	
	/**
	 * Sets the name attribute.
	 * 
	 * @param name the name to use.
	 * @throws JspException if an error occurs while evaluating name parameter.
	 */
	public void setName(final String name) throws JspException
	{
		this.name = evaluateString("PageAttribute", "name", name);
	}

	/**
	 * Sets the value attribute.
	 * 
	 * @param value the value to use.
	 * @throws JspException if an error occurs while evaluating value parameter.
	 */
	public void setValue(final String value) throws JspException
	{
		if(value != null && !value.equals(""))
		{
			try
			{
				this.value = evaluate("PageAttribute", "value", value, Object.class);
			}
			catch (Exception e) 
			{
				logger.warn("PageAttributeTag was send a null reference in expression \"" + value + "\": " + getController().getOriginalFullURL() + " - component:" + getController().getComponentLogic().getInfoGlueComponent().getName());
			}
		}
		else
			this.isError = true;
	}

}
