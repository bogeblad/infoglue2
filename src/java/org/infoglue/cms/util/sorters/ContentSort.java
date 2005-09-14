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
package org.infoglue.cms.util.sorters;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.infoglue.cms.entities.content.Content;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.deliver.controllers.kernel.impl.simple.TemplateController;

/**
 * 
 */
class SortComparable implements Comparable 
{
	/**
	 * 
	 */
	private final List comparables = new ArrayList();

	/**
	 * 
	 */
	private final List orders = new ArrayList();
	
	/**
	 * 
	 */
	public final int compareTo(final Object o) 
	{
		if(!(o instanceof SortComparable))
		{
			throw new ClassCastException();
		}
		final SortComparable other = (SortComparable) o;
		if(other.comparables.size() != comparables.size())
		{
			throw new IllegalStateException("Trying to compare SortComparable with different number of elements.");
		}
		for(int i=0; i<comparables.size(); i++) 
		{
			final int result = compareTo(other, i);
			if(result != 0)
			{
				return result;
			}
		}
		return 0;
	}
	
	/**
	 * 
	 */
	private final int compareTo(final SortComparable other, final int index) 
	{
		final Comparable c1      = (Comparable) comparables.get(index);
		final Comparable c2      = (Comparable) other.comparables.get(index);
		final Boolean ascending  = (Boolean) orders.get(index);
		return ascending.booleanValue() ? c1.compareTo(c2) : c2.compareTo(c1);
	}
	
	/**
	 * 
	 */
	public final void add(final Comparable c, final boolean ascending) 
	{
		comparables.add(c);
		orders.add(new Boolean(ascending));
	}
}

/**
 * 
 */
class SortStruct implements Comparable 
{
	/**
	 * 
	 */
	private final TemplateController controller;
	
	/**
	 * 
	 */
	private ContentVersionVO contentVersionVO;

	/**
	 * 
	 */
	private ContentVO contentVO;
	
	
	/**
	 * 
	 */
	private final SortComparable sortComparable = new SortComparable(); 

	/**
	 * 
	 */
	SortStruct(final TemplateController controller, final ContentVO contentVO) 
	{
		this.controller = controller;
		this.contentVO  = contentVO;
	}

	/**
	 * 
	 */
	SortStruct(final TemplateController controller, final ContentVersionVO contentVersionVO) 
	{
		this.controller       = controller;
		this.contentVersionVO = contentVersionVO;
	}

	/**
	 * 
	 */
	public void addContentProperty(final String name, final boolean ascending) 
	{
		add(getProperty(getContentVO(), name), ascending);
	}

	/**
	 * 
	 */
	public void addContentVersionProperty(final String name, final boolean ascending) 
	{
		add(getProperty(getContentVersionVO(), name), ascending);
	}

	/**
	 * 
	 */
	public void addContentVersionAttribute(final String name, final Class clazz, final boolean ascending) 
	{
		final Integer contentId  = getContentId();
		final String stringValue = controller.getContentAttribute(contentId, controller.getLanguageId(), name); 
		
		add(castAttribute(name, clazz, stringValue), ascending);
	}

	/**
	 * 
	 */
	private Comparable castAttribute(final String name, final Class clazz, final String stringValue) 
	{
		if(String.class.equals(clazz))
		{
			return stringValue;
		}
		try 
		{
			if(clazz.isAssignableFrom(Comparable.class))
			{
				throw new IllegalArgumentException(clazz.getName() + " is not comparable.");
			}
			final Constructor ctor = clazz.getConstructor(new Class[] { String.class });
			final String s = (Number.class.isAssignableFrom(clazz) && stringValue.equals("")) ? "0" : stringValue;
			return (Comparable) ctor.newInstance(new Object[] { s });
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Unable to cast [" + name + "] to [" + clazz.getName() + "].");
		}
	}
	
	/**
	 * 
	 */
	private Integer getContentId() 
	{
		return (contentVO != null) ? getContentVO().getContentId() : getContentVersionVO().getContentId();
	}
	
	/**
	 * 
	 */
	private Comparable getProperty(final Object o, final String name) 
	{
		try 
		{
			return (Comparable) PropertyUtils.getProperty(o, name);
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Illegal property [" + name + "] : " + e);
		}
	}
	
	/**
	 * 
	 */
	private void add(final Comparable c, final boolean ascending) 
	{
		sortComparable.add(c, ascending);
	}
	
	/**
	 * 
	 */
	ContentVersionVO getContentVersionVO() 
	{
		if(contentVersionVO == null) 
		{
			contentVersionVO = controller.getContentVersion(contentVO.getContentId());
		}
		return contentVersionVO;
	}
	
	/**
	 * 
	 */
	ContentVO getContentVO() 
	{
		if(contentVO == null)
		{
			contentVO = controller.getContent(contentVersionVO.getContentId());
		}
		return contentVO;
	}

	/**
	 * 
	 */
	public final int compareTo(Object o) 
	{
		if(!(o instanceof SortStruct))
		{
			throw new ClassCastException();
		}
		final SortStruct other = (SortStruct) o;
		return sortComparable.compareTo(other.sortComparable);
	}
}

/**
 * 
 */
public class ContentSort 
{
	/**
	 * 
	 */
	TemplateController controller;
	
	/**
	 * 
	 */
	private final List structs = new ArrayList();
	
	
	/**
	 * 
	 */
	public ContentSort(final TemplateController controller, final Collection elements) 
	{
		this.controller = controller;
		addElements(elements);
	}
	
	/**
	 * 
	 */
	private final void addElements(final Collection elements) 
	{
		if(elements == null || elements.isEmpty())
		{
			return;
		}
		final Object element = elements.toArray()[0];
		if(element instanceof Content)
		{
			initializeWithContent(elements);
		}
		if(element instanceof ContentVO)
		{
			initializeWithContentVO(elements);
		}
		if(element instanceof ContentVersion)
		{
			initializeWithContentVersion(elements);
		}
		if(element instanceof ContentVersionVO)
		{
			initializeWithContentVersionVO(elements);
		}
	}
	
	/**
	 * 
	 */
	private final void initializeWithContent(final Collection elements) 
	{
		for(final Iterator i=elements.iterator(); i.hasNext(); )
		{
			addElement(((Content) i.next()).getValueObject());
		}
	}
	
	/**
	 * 
	 */
	private final void initializeWithContentVO(final Collection elements) 
	{
		for(final Iterator i=elements.iterator(); i.hasNext(); )
		{
			addElement((ContentVO) i.next());
		}
	}
	
	/**
	 * 
	 */
	private final void  initializeWithContentVersion(final Collection elements) 
	{
		for(final Iterator i=elements.iterator(); i.hasNext(); )
		{
			addElement(((ContentVersion) i.next()).getValueObject());
		}
	}
	
	/**
	 * 
	 */
	private final void  initializeWithContentVersionVO(final Collection elements) 
	{
		for(final Iterator i=elements.iterator(); i.hasNext(); )
		{
			addElement((ContentVersionVO) i.next());
		}
	}
	
	/**
	 * 
	 */
	public void addContentProperty(final String name, final boolean ascending) 
	{
		for(final Iterator i=structs.iterator(); i.hasNext(); )
		{
			((SortStruct) i.next()).addContentProperty(name, ascending);
		}
	}
	
	/**
	 * 
	 */
	public void addContentVersionProperty(final String name, final boolean ascending) 
	{
		for(final Iterator i=structs.iterator(); i.hasNext(); )
		{
			((SortStruct) i.next()).addContentVersionProperty(name, ascending);
		}
	}
	
	/**
	 * 
	 */
	public void addContentVersionAttribute(final String name, final boolean ascending) 
	{
		addContentVersionAttribute(name, String.class, ascending);
	}
	
	/**
	 * 
	 */
	public void addContentVersionAttribute(final String name, final String className, final boolean ascending) 
	{
		try 
		{
			addContentVersionAttribute(name, Class.forName(className), ascending);
		} 
		catch(ClassNotFoundException e) 
		{
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	/**
	 * 
	 */
	public void addContentVersionAttribute(final String name, final Class c, final boolean ascending) 
	{
		for(final Iterator i=structs.iterator(); i.hasNext(); )
		{
			((SortStruct) i.next()).addContentVersionAttribute(name, c, ascending);
		}
	}

	/**
	 * 
	 */
	private void addElement(final ContentVO contentVO) 
	{
		structs.add(new SortStruct(controller, contentVO));
	}
	
	/**
	 * 
	 */
	private void addElement(final ContentVersionVO contentVersionVO) 
	{
		structs.add(new SortStruct(controller, contentVersionVO));
	}
	
	/**
	 * 
	 */
	public List getContentResult() 
	{
		Collections.sort(structs);
		final List result = new ArrayList();
		for(final Iterator i=structs.iterator(); i.hasNext(); ) 
		{
			final SortStruct struct = (SortStruct) i.next();
			result.add(struct.getContentVO());
		}
		return result;
	}

	/**
	 * 
	 */
	public List getContentVersionResult() 
	{
		Collections.sort(structs);
		final List result = new ArrayList();
		for(final Iterator i=structs.iterator(); i.hasNext(); ) 
		{
			final SortStruct struct = (SortStruct) i.next();
			result.add(struct.getContentVersionVO());
		}
		return result;
	}
}
