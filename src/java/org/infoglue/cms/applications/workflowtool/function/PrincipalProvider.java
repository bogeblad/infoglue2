package org.infoglue.cms.applications.workflowtool.function;

import java.util.Map;

import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.security.InfoGluePrincipal;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class PrincipalProvider extends InfoglueFunction {
	/**
	 * 
	 */
	public static final String PRINCIPAL_PARAMETER = "principal";

	/**
	 * 
	 */
	private static final String PRINCIPAL_REQUEST_NAME = "caller";

	
	
	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		populate(transientVars);
	}
	
	/**
	 * 
	 */
	private void populate(Map transientVars) throws WorkflowException {
		try {
			final String userName = (String) transientVars.get(PRINCIPAL_REQUEST_NAME);
			final InfoGluePrincipal principal = UserControllerProxy.getController().getUser(userName);
			transientVars.put(PRINCIPAL_PARAMETER, principal);
		} catch(Exception e) {
			e.printStackTrace();
			throw new WorkflowException("PrincipalProvider.populate()" + e);
		}
	}
}