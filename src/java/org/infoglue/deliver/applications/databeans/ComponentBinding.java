package org.infoglue.deliver.applications.databeans;

/**
 *
 */

public class ComponentBinding
{
	private Integer id;
	private Integer componentId;
	private String entityClass;
	private Integer entityId;
	private String bindingPath;

	public Integer getId()
	{
		return this.id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Integer getComponentId()
	{
		return componentId;
	}

	public String getEntityClass()
	{
		return entityClass;
	}

	public Integer getEntityId()
	{
		return entityId;
	}

	public void setComponentId(Integer integer)
	{
		componentId = integer;
	}

	public void setEntityClass(String string)
	{
		entityClass = string;
	}

	public void setEntityId(Integer integer)
	{
		entityId = integer;
	}

	public String getBindingPath()
	{
		return bindingPath;
	}

	public void setBindingPath(String string)
	{
		bindingPath = string;
	}
}