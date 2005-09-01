package org.infoglue.cms.applications.workflowtool.util;

import java.util.Map;

/**
 * 
 */
public class TransientVarsHelper {
	/**
	 *
	 */
	private TransientVarsHelper() {}

	/**
	 *
	 */
	public static String getRequestValue(final Map transientVars, final String name) {
		final Object value = transientVars.get(name);
		if(value == null) return null;
		
		if(value.getClass().isArray()) {
			String[] values = (String[]) value;
			return values.length == 1 ? values[0] : null;
		}
		return value.toString();
	}
}
