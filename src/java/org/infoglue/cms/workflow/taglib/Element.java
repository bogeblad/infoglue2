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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
class Element 
{
	/**
	 * 
	 */
	private static final String ATTRIBUTE = "{0}=\"{1}\"";
	
	/**
	 * 
	 */
	private static final String CHILDREN_AND_ATTRIBUTES = "<{0} {1}>{2}</{0}>";
	
	/**
	 * 
	 */
	private static final String CHILDREN_AND_NO_ATTRIBUTES = "<{0}>{1}</{0}>";
	
	/**
	 * 
	 */
	private static final String NO_CHILDREN_NO_ATTRIBUTES = "<{0}></{0}>";
	
	/**
	 * 
	 */
	private static final String NO_CHILDREN_AND_ATTRIBUTES = "<{0} {1}></{0}>";
	
	/**
	 * 
	 */
	private final String name;
	
	/**
	 * 
	 */
	private Element parent;
	
	/**
	 * 
	 */
	private final Map attributes = new HashMap();
	
	/**
	 * 
	 */
	private final List children = new ArrayList();
	
	/**
	 * 
	 */
	public Element(final String name) 
	{
		this(null, name);
	}
	
	/**
	 * 
	 */
	public Element(final Element parent, final String name) 
	{
		super();
		this.parent = parent;
		this.name = name;
	}
	
	/**
	 * 
	 */
	public Element attribute(final String name, final String value)
	{
		return attribute(name, value, true);
	}
	
	/**
	 * 
	 */
	public Element attribute(final String name, final String value, final boolean condition)
	{
		if(condition && value != null)
		{
			attributes.put(name, value);
		}
		return this;
	}
	
	/**
	 * 
	 */
	public Element child(final String name) 
	{
		final Element child = new Element(this, name);
		children.add(child);
		return child;
	}

	/**
	 * 
	 */
	public Element child(final String name, final int index) 
	{
		final Element child = new Element(this, name);
		children.add(index, child);
		return child;
	}
	
	/**
	 * 
	 */
	public Element child(final Element child) 
	{
		if(child != null)
		{
			child.parent = this;
			children.add(child);
		}
		return this;
	}
	
	/**
	 * 
	 */
	public Element text(final String text)
	{
		if(text != null && text.length() > 0)
		{
			children.add(text);
		}
		return this;
	}
	
	/**
	 * 
	 */
	public Element pop()
	{
		return parent;
	}
	
	/**
	 * 
	 */
	public String toString()
	{
		if(hasAttributes() && hasChildren())
		{
			return MessageFormat.format(CHILDREN_AND_ATTRIBUTES, new Object[] { name, attributesString(), childrenString()});
		}
		if(hasAttributes() && !hasChildren())
		{
			return MessageFormat.format(NO_CHILDREN_AND_ATTRIBUTES, new Object[] { name, attributesString() });
		}
		if(!hasAttributes() && hasChildren())
		{
			return MessageFormat.format(CHILDREN_AND_NO_ATTRIBUTES, new Object[] { name, childrenString() });
		}
		return MessageFormat.format(NO_CHILDREN_NO_ATTRIBUTES, new Object[] { name });
	}
	
	/**
	 * 
	 */
	private boolean hasAttributes()
	{
		return !attributes.isEmpty();
	}

	/**
	 * 
	 */
	private boolean hasChildren()
	{
		return !children.isEmpty();
	}
	
	/**
	 * 
	 */
	private String attributesString()
	{
		final StringBuffer sb = new StringBuffer();
		for(final Iterator i = attributes.keySet().iterator(); i.hasNext(); )
		{
			final Object key   = i.next();
			final Object value = attributes.get(key);
			sb.append((sb.length() > 0 ? " " : "") + MessageFormat.format(ATTRIBUTE, new Object[] { key, value }));
		}
		return sb.toString();
	}

	/**
	 * 
	 */
	private String childrenString()
	{
		final StringBuffer sb = new StringBuffer();
		for(final Iterator i = children.iterator(); i.hasNext(); )
		{
			sb.append(i.next());
		}
		return sb.toString();
	}
}