package org.infoglue.cms.util.sorters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.deliver.controllers.kernel.impl.simple.TemplateController;

/**
 * 
 */
class SortComparable implements Comparable {
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
	public final int compareTo(Object o) {
		if(!(o instanceof SortComparable))
			throw new ClassCastException();
		final SortComparable other = (SortComparable) o;
		if(other.comparables.size() != comparables.size())
			throw new IllegalStateException("Trying to compare SortComparable with different number of elements.");
		for(int i=0; i<comparables.size(); i++) {
			final int result = compareTo(other, i);
			if(result != 0)
				return result;
		}
		
		return 0;
	}
	
	/**
	 * 
	 */
	private final int compareTo(final SortComparable other, final int index) {
		final Comparable c1      = (Comparable) comparables.get(index);
		final Comparable c2      = (Comparable) other.comparables.get(index);
		final Boolean ascending  = (Boolean) orders.get(index);
		if(ascending.booleanValue())
			return c1.compareTo(c2);
		else
			return c2.compareTo(c1);
	}
	
	/**
	 * 
	 */
	public final void add(final Comparable c, final boolean ascending) {
		comparables.add(c);
		orders.add(new Boolean(ascending));
	}
}

/**
 * 
 */
class SortStruct implements Comparable {
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
	SortStruct(final TemplateController controller, final ContentVO contentVO) {
		this.controller = controller;
		this.contentVO  = contentVO;
	}

	/**
	 * 
	 */
	SortStruct(final TemplateController controller, final ContentVersionVO contentVersionVO) {
		this.controller       = controller;
		this.contentVersionVO = contentVersionVO;
	}

	/**
	 * 
	 */
	public void addContentProperty(final String name, final boolean ascending) {
		add(getProperty(getContentVO(), name), ascending);
	}

	/**
	 * 
	 */
	public void addContentVersionProperty(final String name, final boolean ascending) {
		add(getProperty(getContentVersionVO(), name), ascending);
	}

	/**
	 * 
	 */
	public void addContentVersionAttribute(final String name, final boolean ascending) {
		add(controller.getContentAttribute(getContentVersionVO().getContentId(), controller.getLanguageId(), name), ascending);
	}
	
	/**
	 * 
	 */
	private Comparable getProperty(final Object o, final String name) {
		try {
			return (Comparable) PropertyUtils.getProperty(o, name);
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Illegal property [" + name + "] : " + e);
		}
	}
	
	/**
	 * 
	 */
	private void add(final Comparable c, final boolean ascending) {
		sortComparable.add(c, ascending);
	}
	
	/**
	 * 
	 */
	ContentVersionVO getContentVersionVO() {
		if(contentVersionVO == null) 
			contentVersionVO = controller.getContentVersion(contentVO.getContentId());
		return contentVersionVO;
	}
	
	/**
	 * 
	 */
	ContentVO getContentVO() {
		if(contentVO == null) 
			contentVO = controller.getContent(contentVersionVO.getContentId());
		return contentVO;
	}

	/**
	 * 
	 */
	public final int compareTo(Object o) {
		if(!(o instanceof SortStruct))
			throw new ClassCastException();
		final SortStruct other = (SortStruct) o;
		return sortComparable.compareTo(other.sortComparable);
	}
}

/**
 * 
 */
public class ContentSorter {
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
	private ContentSorter(final TemplateController controller) {
		this.controller = controller;
	}
	
	/**
	 * 
	 */
	public static final ContentSorter createUsingContentVO(final TemplateController controller, final List contentVOList) {
		final ContentSorter sorter = new ContentSorter(controller);
		for(final Iterator i=contentVOList.iterator(); i.hasNext(); )
			sorter.add((ContentVO) i.next());
		return sorter;
	}
	
	/**
	 * 
	 */
	public static final ContentSorter createUsingContentVersionVO(final TemplateController controller, final List contentVersionVOList) {
		final ContentSorter sorter = new ContentSorter(controller);
		for(final Iterator i=contentVersionVOList.iterator(); i.hasNext(); )
			sorter.add((ContentVersionVO) i.next());
		return sorter;
	}
	
	/**
	 * 
	 */
	public void addContentProperty(final String name, final boolean ascending) {
		for(final Iterator i=structs.iterator(); i.hasNext(); ) 
			((SortStruct) i.next()).addContentProperty(name, ascending);
	}
	
	/**
	 * 
	 */
	public void addContentVersionProperty(final String name, final boolean ascending) {
		for(final Iterator i=structs.iterator(); i.hasNext(); )
			((SortStruct) i.next()).addContentVersionProperty(name, ascending);
	}
	
	/**
	 * 
	 */
	public void addContentVersionAttribute(final String name, final boolean ascending) {
		for(final Iterator i=structs.iterator(); i.hasNext(); )
			((SortStruct) i.next()).addContentVersionAttribute(name, ascending);
	}
	
	/**
	 * 
	 */
	private void add(final ContentVO contentVO) {
		structs.add(new SortStruct(controller, contentVO));
	}
	
	/**
	 * 
	 */
	private void add(final ContentVersionVO contentVersionVO) {
		structs.add(new SortStruct(controller, contentVersionVO));
	}
	
	/**
	 * 
	 */
	public List getContentResult() {
		Collections.sort(structs);
		final List result = new ArrayList();
		for(final Iterator i=structs.iterator(); i.hasNext(); ) {
			final SortStruct struct = (SortStruct) i.next();
			result.add(struct.getContentVO());
		}
		return result;
	}

	/**
	 * 
	 */
	public List getContentVersionResult() {
		Collections.sort(structs);
		final List result = new ArrayList();
		for(final Iterator i=structs.iterator(); i.hasNext(); ) {
			final SortStruct struct = (SortStruct) i.next();
			result.add(struct.getContentVersionVO());
		}
		return result;
	}
}
