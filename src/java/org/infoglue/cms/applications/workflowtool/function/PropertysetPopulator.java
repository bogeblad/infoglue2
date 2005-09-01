package org.infoglue.cms.applications.workflowtool.function;

import java.util.Iterator;
import java.util.Map;

import org.infoglue.cms.applications.workflowtool.util.PropertysetHelper;
import org.infoglue.cms.applications.workflowtool.util.TransientVarsHelper;


import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class PropertysetPopulator extends InfoglueFunction {
	/**
	 * 
	 */
	private static final String ARGUMENT_PREFIX_ATTRIBUTE = "prefix";

	/**
	 * 
	 */
	private String prefix;

	
	
	/**
	 * 
	 */
	public PropertysetPopulator() { super(); }

	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		cleanPropertySet(ps);
		populate(transientVars, ps);
	}
	
	/**
	 * 
	 */
	private void populate(final Map transientVars, final PropertySet ps) throws WorkflowException {
		for(Iterator i=transientVars.keySet().iterator(); i.hasNext(); ) {
			String key = (String) i.next();
			if(key.startsWith(prefix))
				new PropertysetHelper(ps).setData(key, TransientVarsHelper.getRequestValue(transientVars, key));
		}
	}
	
	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		super.initialize(transientVars, args, ps);
		prefix = (String) args.get(ARGUMENT_PREFIX_ATTRIBUTE);
	}
	
	/**
	 * 
	 */
	private void cleanPropertySet(final PropertySet ps) {
		new PropertysetHelper(ps).removeKeys(prefix);
	}
}
