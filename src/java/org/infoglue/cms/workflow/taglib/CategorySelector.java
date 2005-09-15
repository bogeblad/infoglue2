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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.infoglue.cms.controllers.kernel.impl.simple.CategoryController;
import org.infoglue.cms.entities.management.CategoryVO;

/**
 * 
 */
public class CategorySelector extends ElementTag 
{
	/**
	 * The universal version identifier.
	 */
	private static final long serialVersionUID = 8780937592170697001L;

	/**
	 * 
	 */
	private CategoryVO rootCategoryVO;
	
	/**
	 * 
	 */
	private String elementValue;
	
	/**
	 * Default constructor.
	 */
	public CategorySelector() 
	{
		super();
	}

	/**
	 * 
	 */
	public int doEndTag() throws JspException 
	{
		for(final Iterator i = rootCategoryVO.getChildren().iterator(); i.hasNext();) {
			final CategoryVO categoryVO = (CategoryVO) i.next();
			final String name           = categoryVO.getName();
			final String value          = categoryVO.getId().toString();
			
			getElement().addChild("option")
				.addText(name)
				.addAttribute("value", value)
				.addAttribute("selected", "selected", value != null && elementValue != null && value.equals(elementValue));
		}
		rootCategoryVO = null;
		elementValue   = null;
		return super.doEndTag();
	}
	
	/**
	 * Creates the element to use when constructing this tag.
	 * 
	 * @return the element to use when constructing this tag.
	 */
	protected Element createElement()
	{
		return new Element("select");
	}
	
	/**
	 * 
	 */
	public void setDefaultLabel(final String label) 
	{
		getElement().addChildFirst("option").addText(label);
	}
	
	/**
	 * 
	 */
	public void setCategoryPath(final String path) throws JspTagException
	{
		rootCategoryVO = getRootCategory(path);
	}

	/**
	 * 
	 */
	public void setName(final String name) 
	{
		getElement().addAttribute("name", name);
		this.elementValue = getPropertySet().getDataString(name);
	}
	
	/**
	 * 
	 */
	private CategoryVO getRootCategory(final String path) throws JspTagException {
		try 
		{
			final CategoryVO categoryVO = CategoryController.getController().findByPath(path);
			return CategoryController.getController().findWithChildren(categoryVO.getId());
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			throw new JspTagException(e.getMessage());
		}
	}
}
