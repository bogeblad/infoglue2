package org.infoglue.cms.webservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.workflowtool.function.InfoglueFunction;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.WorkflowController;
import org.infoglue.cms.entities.mydesktop.WorkflowVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.workflow.WorkflowFacade;

/**
 * This service is used for creating workflows from an external application.
 */
public class RemoteWorkflowServiceImpl {
	/**
	 * The class logger.
	 */
    private final static Logger logger = Logger.getLogger(RemoteWorkflowServiceImpl.class.getName());
	
    /**
     * The principal executing the workflow.
     */
    private InfoGluePrincipal principal;
    
    /**
     * The inputs to the workflow.
     */
    private Map inputs;
    
	/**
	 * Default constructor.
	 */
	public RemoteWorkflowServiceImpl() 
	{
		super();
	}

	/**
	 * Creates the specified workflow running as the specified principal.
	 * To determine if the workflow executed successfully, the state of the workflow is checked. 
	 * A terminated workflow is interpreted as a failure, meaning that all workflows that could
	 * be started from an external application, should terminate directly if an error occurs. 
	 * 
	 * @param principalName the name of the principal that should execute the workflow. Must have permission to create the workflow.
	 * @param languageId the language to use when executing the workflow.
	 * @param workflowName the name of the workflow.
	 * @param inputsArray the inputs to the workflow.
	 * @return true if the workflow executed sucessfully; false otherwise.
	 */
	public Boolean start(final String principalName, final Integer languageId, final String workflowName, final Object[] inputsArray)
	{
		try 
		{
			initializePrincipal(principalName, workflowName);
			initializeInputs(arrayToMap(inputsArray), languageId);
			
			logger.debug("start(" + principalName + "," + workflowName + "," + languageId + "," + inputs + ")");
			
			final WorkflowVO workflowVO = WorkflowController.getController().initializeWorkflow(principal, workflowName, 0, inputs);
			if(hasTerminated(workflowVO)) 
			{
				logger.debug("The workflow has terminated.");
				return Boolean.FALSE;
			}
		} 
		catch(Throwable t) 
		{
			System.out.println(t);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * Returns true if the workflow has terminated; false otherwise.
	 * 
	 * @param workflowVO the workflow.
	 * @return true if the workflow has terminated; false otherwise.
	 */
	private boolean hasTerminated(final WorkflowVO workflowVO)
	{
		return new WorkflowFacade(principal, workflowVO.getIdAsPrimitive()).isFinished();
	}
	
	/**
	 * As you are discouraged to use <code>java.util.Collection</code> objects as arguments,
	 * arrays are used instead. 
	 * <code>java.util.Map</code> objects requires a special syntax where each key/value is
	 * stored after each other in the array.
	 * <p>
	 * Example: the map <code>{ key1 => value1, key2 => value2 }</code> will be sent
	 * as the array <code>{ key1, value1, key2, value2 }</code>.
	 * </p> 
	 * 
	 * @param array the array to convert to a <code>Map</code>.
	 * @throws SystemException if the length of the array is odd.
	 */
	private Map arrayToMap(final Object[] array) throws SystemException
	{
		if(array == null)
		{
			return new HashMap();
		}
		if(array.length % 2 != 0)
		{
			throw new SystemException("Illegal input array sent - uneven number of elements.");
		}
		final Map map = new HashMap();
		for(int i=0; i<array.length; i += 2)
		{
			map.put(array[i], array[i+1]);
		}
		return map;
	}

	/**
	 * Initializes the inputs to the workflow.
	 * 
	 * @param callerInputs the inputs sent in from the caller of this service. 
	 * @param languageId the locale to use when running the workflow.
	 */
	private void initializeInputs(final Map callerInputs, final Integer languageId) throws SystemException
	{
		inputs = callerInputs;
		inputs.put(InfoglueFunction.PRINCIPAL_PARAMETER, principal);
		inputs.put(InfoglueFunction.LOCALE_PARAMETER,    LanguageController.getController().getLocaleWithId(languageId));
	}
	
	/**
	 * Checks if the principal exists and if the principal is allowed to create the workflow.
	 * 
	 * @param userName the name of the user.
	 * @param workflowName the name of the workflow to create.
	 * @throws SystemException if the principal doesn't exists or doesn't have permission to create the workflow.
	 */
	private void initializePrincipal(final String userName, final String workflowName) throws SystemException 
	{
		try 
		{
			principal = UserControllerProxy.getController().getUser(userName);
		}
		catch(SystemException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new SystemException(e);
		}
		if(principal == null) 
		{
			throw new SystemException("No such principal [" + userName + "].");
		}
		if(!WorkflowController.getController().getIsAccessApproved(workflowName, principal))
		{
			throw new SystemException("The principal [" + userName + "] is not allowed to create the [" + workflowName + "] workflow.");
		}
	}
}
