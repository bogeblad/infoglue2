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

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.infoglue.deliver.taglib.AbstractTag;
import org.infoglue.deliver.util.Slots;

/**
 * 
 */
public class SlotsTag extends AbstractTag {
	/**
	 * 
	 */
	private String visibleElementsId;
	
	/**
	 * 
	 */
	private String visibleSlotsId;
	
	/**
	 * 
	 */
	private String lastSlotId;
	
	/**
	 * 
	 */
	private List elements;

	/**
	 * 
	 */
	private int currentSlot = 1;
	
	/**
	 * 
	 */
	private int slotSize;
	
	/**
	 * 
	 */
	private int slotCount;
	
	
	
	/**
	 * 
	 */
	public int doEndTag() throws JspException
    {
		calculateSlots();
        return EVAL_PAGE;
    }
	
	/**
	 * 
	 */
	private void calculateSlots() throws JspException
	{
		try 
		{
			Slots slots = new Slots(elements, currentSlot, slotSize, slotCount);
			setResultAttribute(visibleElementsId, slots.getVisibleElements());
			setResultAttribute(visibleSlotsId, slots.getVisibleSlots());
			setResultAttribute(lastSlotId, slots.getLastSlot());
			
			System.out.println(visibleElementsId + "=" + slots.getVisibleElements().toString());
			System.out.println(visibleSlotsId + "=" + slots.getVisibleSlots().toString());
			System.out.println(lastSlotId + "=" + slots.getLastSlot().toString());
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			throw new JspTagException(e.getMessage());
		}
	}

	/**
	 * 
	 */
	protected void setResultAttribute(final String id, final Object value)
	{
		if(value == null)
			pageContext.removeAttribute(id);
		else
			pageContext.setAttribute(id, value);
	}

	/**
	 * 
	 */
	public void setVisibleElementsId(final String id) 
	{
		this.visibleElementsId = id;
	}
	
	/**
	 * 
	 */
	public void setVisibleSlotsId(final String id) 
	{
		this.visibleSlotsId = id;
	}
	
	/**
	 * 
	 */
	public void setLastSlotId(final String id) 
	{
		this.lastSlotId = id;
	}
	
	/**
	 *
	 */
	public void setElements(final String elements) throws JspException
	{
		this.elements = evaluateList("slotsTag", "elements", elements);
	}

	/**
	 *
	 */
	public void setCurrentSlot(final String currentSlot) throws JspException
	{
		this.currentSlot = evaluateInteger("slotsTag", "currentSlot", currentSlot).intValue();
	}

	/**
	 *
	 */
	public void setSlotSize(final int slotSize)
	{
		this.slotSize = slotSize;
	}

	/**
	 *
	 */
	public void setSlotCount(final int slotCount) 
	{
		this.slotCount = slotCount;
	}
}
