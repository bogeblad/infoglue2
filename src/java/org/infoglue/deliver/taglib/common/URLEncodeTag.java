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

package org.infoglue.deliver.taglib.common;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.TemplateControllerTag;

public class URLEncodeTag extends TemplateControllerTag {
	private static final long serialVersionUID = 4050206323348354355L;
	
	private String value;
	
    public URLEncodeTag()
    {
        super();
    }

	public int doEndTag() throws JspException
    {
		try
        {
		    System.out.println("Encoding:" + value);
            setResultAttribute(this.getController().getVisualFormatter().encodeURI(value));
        } 
		catch (Exception e)
        {
            e.printStackTrace();
        }
		
        return EVAL_PAGE;
    }

    public void setValue(String value) throws JspException
    {
        this.value = evaluateString("URLEncode", "value", value);
    }
}
