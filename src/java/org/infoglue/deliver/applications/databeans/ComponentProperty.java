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

package org.infoglue.deliver.applications.databeans;

import java.util.*;

/**
 * 
 */

public class ComponentProperty
{
	public static final String BINDING 		= "binding";
	public static final String TEXTFIELD 	= "textfield";
	
	private Integer id;
	private String name;
	private String type;
	private Integer componentId;
	private String entityClass;
	private Integer entityId;
	private String value;
	private boolean isMultipleBinding = false;
	private String visualizingAction = null;
	private String createAction = null;
		
	private List contentBindings = new ArrayList();
	private List siteNodeBindings = new ArrayList();
		
		
	public Integer getComponentId()
	{
		return componentId;
	}

	public List getContentBindings()
	{
		return contentBindings;
	}

	public String getEntityClass()
	{
		return entityClass;
	}

	public Integer getEntityId()
	{
		return entityId;
	}

	public Integer getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public List getSiteNodeBindings()
	{
		return siteNodeBindings;
	}

	public String getValue()
	{
		return value;
	}

	public void setComponentId(Integer integer)
	{
		componentId = integer;
	}

	public void setContentBindings(List list)
	{
		contentBindings = list;
	}

	public void setEntityClass(String string)
	{
		entityClass = string;
	}

	public void setEntityId(Integer integer)
	{
		entityId = integer;
	}

	public void setId(Integer integer)
	{
		id = integer;
	}

	public void setName(String string)
	{
		name = string;
	}

	public void setSiteNodeBindings(List list)
	{
		siteNodeBindings = list;
	}

	public void setValue(String string)
	{
		value = string;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String string)
	{
		type = string;
	}

	public boolean getIsMultipleBinding()
	{
		return this.isMultipleBinding;
	}

	public void setIsMultipleBinding(boolean isMultipleBinding)
	{
		this.isMultipleBinding = isMultipleBinding;
	}

	public String getVisualizingAction()
	{
		return visualizingAction;
	}

	public void setVisualizingAction(String visualizingAction)
	{
		this.visualizingAction = visualizingAction;
	}

	public String getCreateAction()
	{
		return this.createAction;
	}

	public void setCreateAction(String createAction)
	{
		this.createAction = createAction;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("id=").append(id)
			.append(" name=").append(name)
			.append(" type=").append(type)
			.append(" componentId=").append(componentId)
			.append(" entityClass=").append(entityClass)
			.append(" entityId=").append(entityId)
			.append(" value=").append(value)
			.append(" isMultipleBinding=").append(isMultipleBinding)
			.append(" visualizingAction=").append(visualizingAction)
			.append(" createAction=").append(createAction)
			.append(" contentBindings.size=").append(contentBindings.size())
			.append(" siteNodeBindings.size=").append(siteNodeBindings.size())
			.append(" categoryBindings.size=").append("not implemented");
		return sb.toString();
	}
}