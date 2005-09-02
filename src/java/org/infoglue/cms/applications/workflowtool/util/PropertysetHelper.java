package org.infoglue.cms.applications.workflowtool.util;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class PropertysetHelper {
	/**
	 * 
	 */
    private final static Logger logger = Logger.getLogger(PropertysetHelper.class.getName());

	/**
	 * 
	 */
	private static final String UTF8_ENCODING = "utf-8";
	
	/**
	 * 
	 */
	private final PropertySet delegate;

	
	
	/**
	 * 
	 */
	public PropertysetHelper(final PropertySet delegate) { this.delegate = delegate; }
	
	/**
	 * 
	 */
	public void setData(final String name, final String value) throws WorkflowException {
		if(value != null)
			try {
				logger.debug("PropertysetHelper.setData(\"" + name + "\",\"" + value + "\")");
				delegate.setData(name, value.getBytes(UTF8_ENCODING));
			} catch(UnsupportedEncodingException e) {
				e.printStackTrace();
				throw new WorkflowException("PropertySetHelper.setData() : " + e);
			}
	}

	/**
	 * 
	 */
	public String getData(final String name) throws WorkflowException {
		try {
			final byte[] b = delegate.getData(name);
			final String value = (b == null) ? null : new String(b, UTF8_ENCODING); 
			logger.debug("PropertysetHelper.getData(\"" + name + "\")=\"" + (value == null ? "NULL" : value) + "\"");
			return value;
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
