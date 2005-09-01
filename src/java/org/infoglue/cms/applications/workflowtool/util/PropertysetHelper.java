package org.infoglue.cms.applications.workflowtool.util;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class PropertysetHelper {
	/**
	 * 
	 */
	private final PropertySet delegate;

	
	
	/**
	 * 
	 */
	public PropertysetHelper(final PropertySet delegate) {
		this.delegate = delegate;
	}
	
	/**
	 * 
	 */
	public void setData(final String name, final String value) throws WorkflowException {
		try {
			delegate.setData(name, value.getBytes("utf-8"));
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new WorkflowException("PropertySetHelper.setData() : " + e);
		}
	}

	
	/**
	 * 
	 */
	public void removeKeys(final String prefix) {
		for(Iterator i = delegate.getKeys(prefix).iterator(); i.hasNext(); )
			delegate.remove((String) i.next());
	}
}
