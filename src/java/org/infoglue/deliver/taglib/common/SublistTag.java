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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.infoglue.deliver.taglib.TemplateControllerTag;

/**
 * 
 */
public class SublistTag extends TemplateControllerTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3617579309963752240L;

	/**
	 * 
	 */
	private List list;
	
	/**
	 * 
	 */
	private int startIndex;
	
	/**
	 * 
	 */
	private int count;
	
	
	/**
	 * 
	 */
	public SublistTag() 
	{
		super();
	}
	
	public int doEndTag() throws JspException
    {
		checkArguments();
	    setResultAttribute(getSublist());
        return EVAL_PAGE;
    }

	/**
	 * 
	 */
	private void checkArguments() throws JspException
	{
		if(list == null)
			throw new JspTagException("List is null.");
		if(startIndex < 0 || startIndex >= list.size())
			throw new JspTagException("Illegal startIndex [0<=" + startIndex + "<" + list.size() + "].");
		if(count < 0 || count >= (list.size() - startIndex))
			throw new JspTagException("Illegal count [0<=" + count + "<" + (list.size() - startIndex) + "].");
	}
	
	/**
	 * 
	 */
	private List getSublist() throws JspException
	{
		final List result = new ArrayList();
		for(int i=startIndex; i<getRealCount(); ++i)
			result.add(list.get(i));
		return result;
	}
	
	/**
	 * 
	 */
	private int getRealCount() {
		return count == 0 ? list.size() - startIndex : count;
	}
	
    public void setList(final String list) throws JspException
    {
        this.list = evaluateList("sublistTag", "list", list);
    }

    public void setStartIndex(final int startIndex)
    {
        this.startIndex = startIndex;
    }

    public void setCount(final int count)
    {
        this.count = count;
    }
}
