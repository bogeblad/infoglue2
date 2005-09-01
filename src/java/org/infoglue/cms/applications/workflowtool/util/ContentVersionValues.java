package org.infoglue.cms.applications.workflowtool.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class ContentVersionValues {
	/**
	 * 
	 */
	private final Map values = new HashMap();

	
	
	/**
	 * 
	 */
	public ContentVersionValues() { super(); }
	
	/**
	 * 
	 */
	public String get(final String name) { return (String) values.get(name); }

	/**
	 * 
	 */
	public void set(final String name, final String value) { values.put(name, value); }

	/**
	 * 
	 */
	public boolean contains(final String name) { return values.containsKey(name); }
}
