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

}