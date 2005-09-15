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

import org.infoglue.deliver.taglib.AbstractTag;

/**
 * 
 */
public class SublistTag extends AbstractTag 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8053523983744317359L;

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
	private Integer count;
	
	
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
		{
			throw new JspTagException("List is null.");
		}
		if(startIndex < 0 || (!list.isEmpty() && startIndex >= list.size()))
		{
			throw new JspTagException("Illegal startIndex [0<=" + startIndex + "<" + list.size() + "].");
		}
		if(count.intValue() < 0)
		{
			throw new JspTagException("Illegal count; must be a non-negative integer.");
		}
	}
	
	/**
	 * 
	 */
	private List getSublist() throws JspException
	{
		final List result = new ArrayList();
		for(int i=startIndex; i<getRealCount(); ++i)
		{
			result.add(list.get(i));
		}
		return result;
	}
	
	/**
	 * 
	 */
	private int getRealCount() {
		return (count.intValue() == 0 || count.intValue() > list.size() - startIndex) ? list.size() - startIndex : count.intValue();
	}
	
    public void setList(final String list) throws JspException
    {
        this.list = evaluateList("sublistTag", "list", list);
    }

    public void setStartIndex(final int startIndex)
    {
        this.startIndex = startIndex;
    }

    public void setCount(final String count) throws JspException
    {
        this.count = evaluateInteger("sublistTag", "count", count);
    }
}
