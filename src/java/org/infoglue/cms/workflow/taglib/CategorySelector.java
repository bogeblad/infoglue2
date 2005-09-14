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

import java.util.Iterator;

import javax.servlet.jsp.JspTagException;

import org.infoglue.cms.controllers.kernel.impl.simple.CategoryController;
import org.infoglue.cms.entities.management.CategoryVO;

/**
 * 
 */
public class CategorySelector extends ElementTag 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6909207400354758841L;
	
	/**
	 * 
	 */
	private String selectedValue;

	/**
	 * 
	 */
	public CategorySelector() 
	{
		super();
	}

	/**
	 * 
	 */
	protected Element createElement()
	{
		return new Element("select");
	}
	
	/**
	 * 
	 */
	public void setName(final String name) 
	{
		getElement().attribute("name", name);
		selectedValue = getElementValue(name);
	}

	/**
	 * 
	 */
	public void setDefaultLabel(final String defaultLabel) 
	{
		getElement().child("option", 0).text(defaultLabel);
	}
	
	/**
	 * 
	 */
	public void setCategoryPath(final String path) throws JspTagException
	{
		for(final Iterator i = getRootCategory(path).getChildren().iterator(); i.hasNext();) {
			final CategoryVO categoryVO = (CategoryVO) i.next();
			final String name           = categoryVO.getName();
			final String value          = categoryVO.getId().toString();
			
			getElement().child("option")
				.text(name)
				.attribute("value", value)
				.attribute("selected", "selected", value != null && selectedValue != null && value.equals(selectedValue));
		}
	}

	/**
	 * 
	 */
	private CategoryVO getRootCategory(final String path) throws JspTagException {
		try {
			CategoryVO categoryVO = CategoryController.getController().findByPath(path);
			return CategoryController.getController().findWithChildren(categoryVO.getId());
		} catch(Exception e) {
			e.printStackTrace();
			throw new JspTagException("CategorySelector.getRootCategory() : " + e);
		}
	}
}
