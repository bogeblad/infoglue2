package org.infoglue.cms.entities.management;

import org.infoglue.cms.util.DomainUtils;

/**
 * This is used to represent the XMLSchema values assocaited with a CategoryAttribute
 *
 * @author Frank Febbraro (frank@phase2technology.com)
 */
public class CategoryAttribute
{
	private String value;
	private String title;
	private String description;
	private Integer categoryId;
	private String categoryName;

	public CategoryAttribute(String value)
	{
		this(value, null);
	}

	public CategoryAttribute(String value, String categoryId)
	{
		this.value = value;
		this.categoryId = (categoryId != null) ? new Integer(categoryId) : null;
	}

	public CategoryAttribute(String value, String categoryId, String title, String desc)
	{
		this.value = value;
		this.categoryId = (categoryId != null) ? new Integer(categoryId) : null;
		this.title = title;
		this.description = desc;
	}

	public String getValue()		{ return value; }
	public void setValue(String s)	{ value = s; }

	public String getTitle()		{ return title; }
	public void setTitle(String s)	{ title = s; }

	public String getDescription()			{ return description; }
	public void setDescription(String s)	{ description = s; }

	public Integer getCategoryId()		{ return categoryId; }
	public void setCategoryId(String s)	{ categoryId = (s != null) ? new Integer(s) : null; }

	public String getCategoryName()			{ return categoryName; }
	public void setCategoryName(String s)	{ categoryName = s; }

	public boolean equals(Object o)
	{
		CategoryAttribute other = (CategoryAttribute)o;
		return DomainUtils.equals(value, other.value)
				&& DomainUtils.equals(title, other.title)
				&& DomainUtils.equals(description, other.description)
				&& DomainUtils.equals(categoryId, other.categoryId);
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("value=").append(value)
			.append(" title=").append(title)
			.append(" description=").append(description)
			.append(" categoryId=").append(categoryId);
		return sb.toString();
	}
}

