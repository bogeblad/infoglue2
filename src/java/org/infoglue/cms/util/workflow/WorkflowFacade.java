/**
 * $Id: WorkflowFacade.java,v 1.5 2005/01/04 01:39:26 jed Exp $
 * Created by jed on Dec 28, 2004
 */
package org.infoglue.cms.util.workflow;

import java.util.*;

import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.entities.mydesktop.*;
import com.opensymphony.workflow.*;
import com.opensymphony.workflow.spi.*;
import com.opensymphony.workflow.query.*;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.loader.*;
import com.opensymphony.module.propertyset.PropertySet;

/**
 * A facade to OSWorkflow that gives us a place to cache workflow data as we need it while interacting with it.
 * This class has kind of a strange interface due to the idiosyncracies of the OSWorkflow, particularly
 * the Workflow interface.  The idea is to encapsulate the interactions with OSWorkflow and eliminate the
 * need to pass a Workflow reference and the workflow ID all over the place when extracting data from OSWorkflow
 * @author <a href="mailto:jedprentice@gmail.com">Jed Prentice</a>
 * @version $Revision: 1.5 $ $Date: 2005/01/04 01:39:26 $
 */
public class WorkflowFacade
{
	/**
	 * The initial action ID.  It is hard-coded to 0, which implies that the initial action of any workflow nust
	 * also be 0.
	 * TODO: At some point we should find a way around this.
	 */
	private static final int INITIAL_ACTION = 0;

	private final Workflow workflow;

	private long workflowId;
	private WorkflowDescriptor workflowDescriptor;

	/**
	 * Constructs a WorkflowFacade with the given user principal
	 * @param userPrincipal an InfoGluePrincipal representing a system user
	 */
	public WorkflowFacade(InfoGluePrincipal userPrincipal)
	{
		workflow = new BasicWorkflow(userPrincipal.getName());
	}

	/**
	 * Constructs a WorkflowFacade for a user with the given workflow ID
	 * @param userPrincipal an InfoGluePrincipal representing a system user
	 * @param workflowId the ID representing an instance of the desired workflow
	 */
	public WorkflowFacade(InfoGluePrincipal userPrincipal, long workflowId)
	{
		this(userPrincipal);
		this.workflowId = workflowId;
	}

	/**
	 * Returns the name of the underlying workflow
	 * @return the name of the underlying workflow
	 */
	public String getName()
	{
		return workflow.getWorkflowName(workflowId);
	}

	/**
	 * Returns the workflow ID
	 * @return the workflow ID
	 */
	public long getWorkflowId()
	{
		return workflowId;
	}

	/**
	 * Initializes the workflow, setting workflowId as a side-effect.
	 * @param name the name of the workflow to initialize
	 * @throws org.infoglue.cms.exception.SystemException if a workflow error occurs.
	 */
	public void initialize(String name) throws SystemException
	{
		try
		{
			workflowId = workflow.initialize(name, INITIAL_ACTION, new HashMap());
		}
		catch (Exception e)
		{
			throw new SystemException(e);
		}
	}

	/**
	 * Performs an action using the given inputs
	 * @param actionId the ID of the action to perform
	 * @param inputs a map of inputs to the action
	 * @throws org.infoglue.cms.exception.SystemException if a workflow error occurs
	 */
	public void doAction(int actionId, Map inputs) throws SystemException
	{
		try
		{
			workflow.doAction(workflowId, actionId, inputs);
		}
		catch (Exception e)
		{
			throw new SystemException(e);
		}
	}

	/**
	 * Returns the workflow descriptor, lazily initializing it if necessary.
	 * @return the workflow descriptor associated with the workflow with workflowId.
	 */
	private WorkflowDescriptor getWorkflowDescriptor()
	{
		if (workflowDescriptor == null)
			workflowDescriptor = getWorkflowDescriptor(getName());

		return workflowDescriptor;
	}

	/**
	 * Returns the workflow descriptor for the workflow with the given name.
	 * @param name a workflow name
	 * @return the descriptor for the workflow with name
	 */
	private WorkflowDescriptor getWorkflowDescriptor(String name)
	{
		return workflow.getWorkflowDescriptor(name);
	}

	/**
	 * Returns the property set associated with the underlying workflow
	 * @return the property set associated with the underlying workflow
	 */
	public PropertySet getPropertySet()
	{
		return workflow.getPropertySet(workflowId);
	}

	/**
	 * Returns a list of all declared workflows, i.e., workflows defined in workflows.xml
	 * @return a list WorkflowVOs representing all declared workflows
	 */
	public List getDeclaredWorkflows()
	{
		String workflowNames[] = workflow.getWorkflowNames();
		List availableWorkflows = new ArrayList();

		for (int i = 0; i < workflowNames.length; i++)
		{
			CmsLogger.logInfo("workflowName:" + workflowNames[i]);
			if (workflow.canInitialize(workflowNames[i], INITIAL_ACTION))
				availableWorkflows.add(createWorkflowVO(workflowNames[i]));
		}

		return availableWorkflows;
	}

	/**
	 * Returns a list of all active workflows.
	 * @return a list of WorkflowVOs representing all active workflows
	 * @throws org.infoglue.cms.exception.SystemException if an error occurs finding the active workflows
	 */
	public List getActiveWorkflows() throws SystemException
	{
		List workflows = findActiveWorkflows();
		List workflowVOs = new ArrayList();

		for (Iterator iterator = workflows.iterator(); iterator.hasNext();)
		{
			workflowId = ((Long)iterator.next()).longValue();
			CmsLogger.logInfo("workflowId:" + workflowId);
			workflowVOs.add(createWorkflowVO());
		}

		return workflowVOs;
	}

	/**
	 * Finds all active workflows
	 * @return A list of workflowIds representing workflows that match the hard-wored query expression.
	 * @throws org.infoglue.cms.exception.SystemException if a workflow error occurs during the search
	 */
	private List findActiveWorkflows() throws SystemException
	{
		try
		{
			return workflow.query(new WorkflowExpressionQuery(new FieldExpression(FieldExpression.STATE,
										FieldExpression.ENTRY, FieldExpression.EQUALS, new Integer(WorkflowEntry.ACTIVATED))));
		}
		catch (WorkflowException e)
		{
			throw new SystemException(e);
		}
	}

	/**
	 * Returns all current steps for the workflow, i.e., steps that could be performed in the workflow's current state
	 * Steps are filtered according to ownership; if a step has an owner, it is only included if the ownser matches
	 * the caller or if the current user is an administrator.
	 * TODO: Make this configurable for either roles or users
	 * @return a list of WorkflowStepVOs representing the current steps of the workflow with workflowId
	 */
	public List getCurrentSteps()
	{
		return createStepVOs(workflow.getCurrentSteps(workflowId));
	}

	/**
	 * Returns all history steps for the workflow, i.e., all the steps that have already been performed.
	 * @return a list of WorkflowStepVOs representing all history steps for the workflow with workflowId
	 */
	public List getHistorySteps()
	{
		return createStepVOs(workflow.getHistorySteps(workflowId));
	}

	/**
	 * Returns all steps for a workflow definition.  These are the steps declared in the workfow descriptor; there is
	 * no knowledge of current or history steps at this point.
	 * @return a list of WorkflowStepVOs representing all steps in the workflow.
	 */
	public List getDeclaredSteps()
	{
		return getDeclaredSteps(getWorkflowDescriptor());
	}

	/**
	 * Creates a list of WorkflowStepVOs from the given list of steps
	 * @param steps a list of Steps
	 * @return a list of WorkflowStepVOs corresponding to all steps that pass the filter
	 */
	private List createStepVOs(List steps)
	{
		List stepVOs = new ArrayList();
		for (Iterator i = steps.iterator(); i.hasNext();)
			stepVOs.add(createStepVO((Step)i.next()));

		return stepVOs;
	}

	/**
	 * Returns all steps for a workflow definition.  These are the steps declared in the workfow descriptor; there is
	 * no knowledge of current or history steps at this point.
	 * @param descriptor a workflow descriptor from which to get current steps
	 * @return a list of WorkflowStepVOs representing all steps in the workflow.
	 */
	private List getDeclaredSteps(WorkflowDescriptor descriptor)
	{
		List steps = new ArrayList();
		for (Iterator i = descriptor.getSteps().iterator(); i.hasNext();)
			steps.add(createStepVO((StepDescriptor)i.next()));

		return steps;
	}

	/**
	 * Returns a list of global actions for a workflow
	 * @return a list of WorkflowActionVOs representing the global actions for the workflow with workflowId
	 */
	private List getGlobalActions()
	{
		return createActionVOs(getWorkflowDescriptor().getGlobalActions());
	}

	/**
	 * Creates a list of WorkflowActionVOs from a list of action descriptors
	 * @param actionDescriptors a list of ActionDescriptors
	 * @return a list of WorkflowActionVOs representing actionDescriptors
	 */
	private List createActionVOs(List actionDescriptors)
	{
		List actions = new ArrayList();
		for (Iterator i = actionDescriptors.iterator(); i.hasNext();)
			actions.add(createActionVO((ActionDescriptor)i.next()));

		return actions;

	}

	/**
	 * Creates a new WorkflowVO.  This represents a pretty complete workflow; you get all the current steps, history
	 * steps, available actions, and global actions.
	 * @return a WorkflowVO representing workflow, with workflowId
	 */
	public WorkflowVO createWorkflowVO()
	{
		WorkflowVO workflowVO = new WorkflowVO(new Long(workflowId), workflow.getWorkflowName(workflowId));
		workflowVO.setCurrentSteps(getCurrentSteps());
		workflowVO.setHistorySteps(getHistorySteps());
		workflowVO.setGlobalActions(getGlobalActions());

		return workflowVO;
	}

	/**
	 * Creates a new WorkflowVO from workflow with the given name.  The resulting workflow VO contains only a
	 * minimal amount of data because we don't have the workflow ID.  Basically all you get is all the steps.
	 * @param name the name of the desired workflow
	 * @return a new WorkflowVO representing workflow
	 */
	private WorkflowVO createWorkflowVO(String name)
	{
		WorkflowVO workflowVO = new WorkflowVO(null, name);
		workflowVO.setDeclaredSteps(getDeclaredSteps(workflow.getWorkflowDescriptor(name)));
		return workflowVO;
	}

	/**
	 * Creates a WorkflowStepVO from the given step
	 * @param step the desired step
	 * @return a new WorkflowStepVO representing step.
	 */
	private WorkflowStepVO createStepVO(Step step)
	{
		CmsLogger.logInfo("step:" + step + ":" + step.getId());
		CmsLogger.logInfo("Owner:" + step.getOwner());

		WorkflowStepVO stepVO = new WorkflowStepVO();
		stepVO.setId(new Integer((int)step.getId()));// Hope it doesn't get too big; we are stuck with an int thanks to BaseEntityVO
		stepVO.setStepId(new Integer(step.getStepId()));
		stepVO.setWorkflowId(new Long(workflowId));
		stepVO.setStatus(step.getStatus());
		stepVO.setStartDate(step.getStartDate());
		stepVO.setFinishDate(step.getFinishDate());
		stepVO.setOwner(step.getOwner());
		stepVO.setCaller(step.getCaller());

		StepDescriptor stepDescriptor = getWorkflowDescriptor().getStep(step.getStepId());
		stepVO.setName(stepDescriptor.getName());
		for (Iterator i = stepDescriptor.getActions().iterator(); i.hasNext();)
			stepVO.addAction(createActionVO((ActionDescriptor)i.next()));

		return stepVO;
	}

	/**
	 * Creates a WorkflowStepVO from a step descriptor.  Some of the step data, e.g., status, startDate,
	 * finishDate, etc. cannot be populated here because the step descriptor does not know about these things.
	 * @param stepDescriptor a step descriptor
	 * @return a WorkflowStepVO representing stepDescriptor
	 */
	private WorkflowStepVO createStepVO(StepDescriptor stepDescriptor)
	{
		WorkflowStepVO step = new WorkflowStepVO();
		step.setStepId(new Integer(stepDescriptor.getId()));
		step.setName(stepDescriptor.getName());
		step.setStatus("Not started");

		for (Iterator i = stepDescriptor.getActions().iterator(); i.hasNext();)
			step.addAction(createActionVO((ActionDescriptor)i.next()));

		return step;
	}

	/**
	 * Creates a WorkflowActionVO for the given action descriptor
	 * @param actionDescriptor an action descriptor
	 * @return a WorkflowActionVO representing actionDescriptor
	 */
	private WorkflowActionVO createActionVO(ActionDescriptor actionDescriptor)
	{
		CmsLogger.logInfo("Action:" + actionDescriptor.getId() + ":" + actionDescriptor.getName()
								+ ":" + actionDescriptor.getParent().getClass());

		WorkflowActionVO actionVO = new WorkflowActionVO();
		actionVO.setId(new Integer(actionDescriptor.getId()));
		actionVO.setWorkflowId(new Long(workflowId));
		actionVO.setName(actionDescriptor.getName());
		actionVO.setView(actionDescriptor.getView());
		actionVO.setAutoExecute(actionDescriptor.getAutoExecute());
		actionVO.setMetaAttributes(actionDescriptor.getMetaAttributes());
		return actionVO;
	}
}
