package org.infoglue.cms.applications.workflowtool.util;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.infoglue.cms.applications.common.Session;

/**
 * 
 */
public class RequestHelper {
	/**
	 * 
	 */
	private static final String REQUEST_KEY = "request";

	/**
	 * 
	 */
	private HttpServletRequest delegate;

	
	
	/**
	 * 
	 */
	public RequestHelper(final Map transientVars) {
		this.delegate =  (HttpServletRequest) transientVars.get(REQUEST_KEY);
	}
	
	/**
	 * 
	 */
	public Locale getLocale() {
		return (delegate == null) ? Locale.getDefault() : new Session(delegate.getSession()).getLocale();
	}
}
