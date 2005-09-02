package org.infoglue.cms.applications.workflowtool.function;

import java.util.HashMap;
import java.util.Map;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public abstract class CategoryProvider extends InfoglueFunction {
	/**
	 * 
	 */
	public static final String CATEGORIES_PARAMETER = "categories";

	/**
	 * 
	 */
	private Map categories;

	
	
	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		super.initialize(transientVars, args, ps);
		initializeTransientVars(transientVars);
		initializeArguments(args);
	}
	
	/**
	 * 
	 */
	protected void initializeTransientVars(final Map transientVars) throws WorkflowException {
		if(transientVars.containsKey(CATEGORIES_PARAMETER))
			categories = (Map) transientVars.get(CATEGORIES_PARAMETER);
		else {
			categories = new HashMap();
			transientVars.put(CATEGORIES_PARAMETER, categories);
		}
	}

	/**
	 * 
	 */
	protected void initializeArguments(final Map args) throws WorkflowException {}

	/**
	 * 
	 */
	protected Map getCategories() { return categories; }
}