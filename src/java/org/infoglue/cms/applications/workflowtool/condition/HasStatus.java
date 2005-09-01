package org.infoglue.cms.applications.workflowtool.condition;

import java.util.Map;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.workflowtool.function.InfoglueFunction;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.WorkflowException;

public class HasStatus implements Condition {
	/**
	 * 
	 */
    private final static Logger logger = Logger.getLogger(HasStatus.class.getName());
	/**
	 * 
	 */
	private final static String ARGUMENT_WANTED_STATUS = "status";
	
	/**
	 * 
	 */
	private String wantedStatus;
	
	/**
	 * 
	 */
	private String currentStatus;
	
	
	
	/**
	 * 
	 */
	public HasStatus() { super(); }

	/**
	 * 
	 */
	public boolean passesCondition(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		initialize(transientVars, args, ps);
		logger.debug("HasStatus.passesCondition() : " + wantedStatus.equals(currentStatus));
		return wantedStatus.equals(currentStatus);
	}

	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		if(!args.containsKey(ARGUMENT_WANTED_STATUS)) {
			final WorkflowException e = new WorkflowException("The wanted status argument is missing.");
			logger.error(e.toString());
			throw e;
		}
		wantedStatus  = (String) args.get(ARGUMENT_WANTED_STATUS);
		currentStatus = (String) (ps.exists(InfoglueFunction.PROPERTYSET_STATUS) ? ps.getString(InfoglueFunction.PROPERTYSET_STATUS) : null); 
	}
}
