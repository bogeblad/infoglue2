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
package org.infoglue.deliver.taglib.content;

import javax.servlet.jsp.JspTagException;

import org.infoglue.deliver.taglib.AbstractTag;

/**
 * 
 */
abstract public class AbstractSortArgumentTag extends AbstractTag {
	/**
	 * 
	 */
	private String name;
	
	/**
	 * 
	 */
	private boolean ascending;
	
	
	/**
	 * 
	 */
	public AbstractSortArgumentTag() 
	{
		super();
	}
	
	/**
	 * 
	 */
	protected ContentSortTag getContentSortParent() throws JspTagException
	{
		final ContentSortTag parent = (ContentSortTag) findAncestorWithClass(this, ContentSortTag.class);
		if(parent == null)
			throw new JspTagException("SortContentVersionPropertyTag must be used inside a ContentSortTag");
		return parent;
	}
	
	/**
	 * 
	 */
	protected String getName() 
	{
		return name;
	}
	
	/**
	 * 
	 */
	protected boolean getAscending()
	{
		return ascending;
	}
	
    /**
	 * 
	 */
	public void setName(final String name) 
	{
		this.name = name;
	}
	
	/**
	 * 
	 */
	public void setAscending(final boolean ascending) 
	{
		this.ascending = ascending;
	}
}
