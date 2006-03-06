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

package org.infoglue.deliver.applications.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoGlueComponent
{	
	private Integer id							= null;
	private Integer contentId 					= null;
	private String name 	 					= null;
	private String slotName						= null;
	private boolean isInherited 				= false;
	private Map properties     					= new HashMap();
	private Map tasks     						= new HashMap();
	private List slotList 						= new ArrayList();
	private List restrictions 					= new ArrayList();
	private Map slots 							= new HashMap();
	private Map components 						= new HashMap();
	private InfoGlueComponent parentComponent 	= null;
	
	public InfoGlueComponent()
	{
	}
	
	public Map getComponents()
	{
		return this.components;
	}

	public Integer getContentId()
	{
		return this.contentId;
	}

	public String getName()
	{
		return this.name;
	}

	public void setComponents(Map components)
	{
		this.components = components;
	}

	public void setContentId(Integer contentId)
	{
		this.contentId = contentId;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Integer getId()
	{
		return this.id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Map getSlots()
	{
		return this.slots;
	}

	public void setSlots(Map slots)
	{
		this.slots = slots;
	}

	public Map getProperties()
	{
		return this.properties;
	}

	public void setProperties(Map properties)
	{
		this.properties = properties;
	}

	public Map getTasks()
	{
		return this.tasks;
	}

	public void setTasks(Map tasks)
	{
		this.tasks = tasks;
	}

	public boolean getIsInherited()
	{
		return isInherited;
	}

	public void setIsInherited(boolean isInherited)
	{
		this.isInherited = isInherited;
	}

	public List getSlotList()
	{
		return slotList;
	}

	public void setSlotList(List list)
	{
		slotList = list;
	}

	public InfoGlueComponent getParentComponent()
	{
		return parentComponent;
	}

	public void setParentComponent(InfoGlueComponent component)
	{
		parentComponent = component;
	}

    public String getSlotName()
    {
        return slotName;
    }
    
    public void setSlotName(String slotName)
    {
        this.slotName = slotName;
    }
    
    public List getRestrictions()
    {
        return restrictions;
    }
}