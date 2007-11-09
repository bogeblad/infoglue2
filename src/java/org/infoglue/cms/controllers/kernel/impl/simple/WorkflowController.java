/* ===============================================================================
/* ===============================================================================
*
* Part of the InfoGlue Content Management Platform (www.infoglue.org)
*
* ===============================================================================
*
*  Copyright (C)
*
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU General Public License version 2, as published by the
* Free Software Foundation. See the file LICENSE.html for more information.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
* Place, Suite 330 / Boston, MA 02111-1307 / USA.
*
* ===============================================================================
*/

package org.infoglue.cms.controllers.kernel.impl.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.mydesktop.WorkflowActionVO;
import org.infoglue.cms.entities.mydesktop.WorkflowVO;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.workflow.WorkflowFacade;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * This controller acts as the api towards the OSWorkflow Workflow-engine.
 * @author Mattias Bogeblad
 * @author <a href="mailto:mattias.bogeblad@modul1.se">Mattias Bogeblad</a>
 */
public class WorkflowController extends BaseController
{
    private final static Logger logger = Logger.getLogger(UserPropertiesController.class.getName());

	private static final WorkflowController controller = new WorkflowController();

	private static SessionFactory hibernateSessionFactory;
	
	static
	{
		try
		{
			hibernateSessionFactory = new Configuration().configure().buildSessionFactory();
		}
		catch (HibernateException e)
		{
			logger.error("An exception occurred when we tried to initialize the hibernateSessionFactory", e);
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * Returns the WorkflowController singleton
	 * @return a reference to a WorkflowController
	 */
	public static WorkflowController getController()
	{
		return controller;
	}

	private WorkflowController() {}

	/**
	 * TODO: move; used by tests + CreateWorkflowInstanceAction 
	 */
	public static Map createWorkflowParameters(final HttpServletRequest request)
	{
		final Map parameters = new HashMap();
		parameters.putAll(request.getParameterMap());
		parameters.put("request", request);
		return parameters;
	}

	/**
	 * @param principal the user principal representing the desired user
	 * @param name the name of the workflow to create.
	 * @param actionId the ID of the initial action
	 * @param inputs the inputs to the workflow
	 * @return a WorkflowVO representing the newly created workflow instance
	 * @throws SystemException if an error occurs while initiaizing the workflow
	 */
	public WorkflowVO initializeWorkflow(InfoGluePrincipal principal, String name, int actionId, Map inputs) throws SystemException
	{
		WorkflowVO workflowVO = null;
		
		try
		{
			Session session = null;
			net.sf.hibernate.Transaction tx = null;

			try
			{
				session = hibernateSessionFactory.openSession();
				tx = session.beginTransaction();

				if(getIsAccessApproved(name, principal))
				{
					WorkflowFacade wf = new WorkflowFacade(principal, name, actionId, inputs, session);
					workflowVO = wf.createWorkflowVO();
					
					session.flush();

					tx.commit();
				}
				else
				{
					throw new Bug("You are not allowed to create " + name + " workflows.");
				}
			}
			catch (Exception e) 
			{
				logger.error("An error occurred when we tries to run initializeWorkflow():" + e.getMessage(), e);
				try
				{
					tx.rollback();
				}
				catch (HibernateException he)
				{
					logger.error("An error occurred when we tries to rollback transaction:" + he.getMessage(), he);
				}
			}
			finally 
			{
				try
				{
					session.close();
			    } 
				catch (HibernateException e)
				{
					logger.error("An error occurred when we tries to close:" + e.getMessage(), e);
				}
			}
		}
		catch (Exception e)
		{
			throw new SystemException(e);
		}			
			
		return workflowVO;
	}

	/**
	 * Returns a list of all available workflows, i.e., workflows defined in workflows.xml
	 * @param userPrincipal a user principal
	 * @return a list WorkflowVOs representing available workflows
	 */
	public List getAvailableWorkflowVOList(InfoGluePrincipal userPrincipal) throws SystemException
	{
		final List accessibleWorkflows = new ArrayList();

		Session session = null;
		net.sf.hibernate.Transaction tx = null;

		try
		{
			session = hibernateSessionFactory.openSession();

			tx = session.beginTransaction();
			
			WorkflowFacade wf = new WorkflowFacade(userPrincipal, session);
			final List allWorkflows = wf.getDeclaredWorkflows();
			
			for(final Iterator i = allWorkflows.iterator(); i.hasNext(); )
			{
				final WorkflowVO workflowVO = (WorkflowVO) i.next();
				if(getIsAccessApproved(workflowVO.getName(), userPrincipal))
				{
					accessibleWorkflows.add(workflowVO);
				}
			}
			
			session.flush();
			
			tx.commit();
		}
		catch (Exception e) 
		{
			logger.error("An error occurred when we tries to execute getAvailableWorkflowVOList():" + e.getMessage(), e);
			try
			{
				tx.rollback();
			}
			catch (HibernateException he)
			{
				logger.error("An error occurred when we tries to rollback transaction():" + he.getMessage(), he);
			}
		}
		finally 
		{
			try
			{
				session.close();
		    } 
			catch (HibernateException e)
			{
				logger.error("An error occurred when we tries to close session:" + e.getMessage(), e);
			}
		}
			
		return accessibleWorkflows;
	}

	/**
	 * This method returns true if the user should have access to the contentTypeDefinition sent in.
	 */
    
	public boolean getIsAccessApproved(String workflowName, InfoGluePrincipal infoGluePrincipal) throws SystemException
	{
	    final String protectWorkflows = CmsPropertyHandler.getProtectWorkflows();
	    if(protectWorkflows == null || !protectWorkflows.equalsIgnoreCase("true"))
	    {
	    	return true;
	    }
	    	
		logger.info("getIsAccessApproved for " + workflowName + " AND " + infoGluePrincipal);
		boolean hasAccess = false;
    	
		Database db = CastorDatabaseService.getDatabase();
		beginTransaction(db);

		try
		{ 
			hasAccess = AccessRightController.getController().getIsPrincipalAuthorized(db, infoGluePrincipal, "Workflow.Create", workflowName);
			commitTransaction(db);
		}
		catch(Exception e)
		{
			logger.error("An error occurred so we should not complete the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}
    
		return hasAccess;
	}

	/**
	 * Returns current workflows, i.e., workflows that are active.
	 * @param userPrincipal a user principal
	 * @return a list of WorkflowVOs representing all active workflows
	 * @throws SystemException if an error occurs while finding the current workflows
	 */
	public List getCurrentWorkflowVOList(InfoGluePrincipal userPrincipal) throws SystemException
	{
		List list = new ArrayList();
		
		Session session = null;
		net.sf.hibernate.Transaction tx = null;

		try
		{
			session = hibernateSessionFactory.openSession();

			tx = session.beginTransaction();

			WorkflowFacade wf = new WorkflowFacade(userPrincipal, session);
			list = wf.getActiveWorkflows();
			
			session.flush();

			tx.commit();
		}
		catch (Exception e) 
		{
			logger.error("An error occurred when we tries to execute getCurrentWorkflowVOList():" + e.getMessage(), e);
			try
			{
				tx.rollback();
			}
			catch (HibernateException he)
			{
				logger.error("An error occurred when we tries to rollback transaction:" + he.getMessage(), he);
			}
		}
		finally 
		{
			try
			{
				session.close();
		    } 
			catch (HibernateException e)
			{
				logger.error("An error occurred when we tries to close session:" + e.getMessage(), e);
			}
	        
		}
		
		return list;
	}


	/**
	 * Returns the workflows owned by the specified principal.
	 * 
	 * @param userPrincipal a user principal.
	 * @return a list of WorkflowVOs owned by the principal.
	 * @throws SystemException if an error occurs while finding the workflows
	 */
	public List getMyCurrentWorkflowVOList(InfoGluePrincipal userPrincipal) throws SystemException
	{
		List list = new ArrayList();
		
		Session session = null;
		net.sf.hibernate.Transaction tx = null;

		try
		{
			session = hibernateSessionFactory.openSession();

			tx = session.beginTransaction();

			WorkflowFacade wf = new WorkflowFacade(userPrincipal, session);
			list = wf.getMyActiveWorkflows(userPrincipal);
			
			session.flush();

			tx.commit();
		}
		catch (Exception e) 
		{
			logger.error("An error occurred when we tries to execute getMyCurrentWorkflowVOList():" + e.getMessage(), e);
			try
			{
				tx.rollback();
			}
			catch (HibernateException he)
			{
				logger.error("An error occurred when we tries to rollback transaction:" + he.getMessage(), he);
			}
		}
		finally 
		{
			try
			{
				session.close();
		    } 
			catch (HibernateException e)
			{
				logger.error("An error occurred when we tries to close session:" + e.getMessage(), e);
			}
		}
		
		return list;

		//return new WorkflowFacade(userPrincipal, true).getMyActiveWorkflows(userPrincipal);
	}
	

	/**
	 * Invokes an action on a workflow for a given user and request
	 * @param principal the user principal
	 * @param workflowId the ID of the desired workflow
	 * @param actionId the ID of the desired action
	 * @param inputs the inputs to the workflow 
	 * @return a WorkflowVO representing the current state of the workflow identified by workflowId
	 * @throws WorkflowException if a workflow error occurs
	 */
	public WorkflowVO invokeAction(InfoGluePrincipal principal, long workflowId, int actionId, Map inputs) throws WorkflowException
	{
		WorkflowVO workflowVO = null;

		Session session = null;
		net.sf.hibernate.Transaction tx = null;

		try
		{
			session = hibernateSessionFactory.openSession();

			tx = session.beginTransaction();

			WorkflowFacade wf = new WorkflowFacade(principal, workflowId, session);
			wf.doAction(actionId, inputs);

			session.flush();

			tx.commit();
		}
		catch (Exception e) 
		{
			logger.error("An error occurred when we tries to execute invokeAction():" + e.getMessage(), e);
			try
			{
				tx.rollback();
			}
			catch (HibernateException he)
			{
				logger.error("An error occurred when we tries to rollback transaction:" + he.getMessage(), he);
			}
		}
		finally 
		{
			try
			{
				session.close();
		    } 
			catch (HibernateException e)
			{
				logger.error("An error occurred when we tries to close session:" + e.getMessage(), e);
			}
		}
			
		try
		{
			session = hibernateSessionFactory.openSession();

			tx = session.beginTransaction();

			WorkflowFacade wf = new WorkflowFacade(principal, workflowId, session);

			workflowVO = wf.createWorkflowVO();
			
			session.flush();

			tx.commit();
		}
		catch (Exception e) 
		{
			logger.error("An error occurred when we tries to execute invokeAction():" + e.getMessage(), e);
			try
			{
				tx.rollback();
			}
			catch (HibernateException he)
			{
				logger.error("An error occurred when we tries to rollback transaction:" + he.getMessage(), he);
			}
		}
		finally 
		{
			try
			{
				session.close();
		    } 
			catch (HibernateException e)
			{
				logger.error("An error occurred when we tries to close session:" + e.getMessage(), e);
			}
		}

		return workflowVO;
	}

	/**
	 * Returns the workflow property set for a particular user and workflow
	 * @return the workflow property set for the workflow with workflowId and the user represented by userPrincipal
	 */
	public PropertySet getPropertySet(InfoGluePrincipal userPrincipal, long workflowId)
	{
		PropertySet propertySet = null;
		
		try
		{
			WorkflowFacade wf = new WorkflowFacade(userPrincipal, workflowId, false);
			propertySet = wf.getPropertySet();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally 
		{
		}

		return propertySet;
		//return new WorkflowFacade(userPrincipal, workflowId, false).getPropertySet();
	}

	/**
	 * Returns the contents of the PropertySet for a particular workflow
	 * @param userPrincipal a user principal
	 * @param workflowId the ID of the desired workflow
	 * @return a map containing the contents of the workflow property set
	 */
	public Map getProperties(InfoGluePrincipal userPrincipal, long workflowId)
	{
		if(logger.isDebugEnabled())
		{
			logger.info("userPrincipal:" + userPrincipal);
			logger.info("workflowId:" + workflowId);
		}
		
		PropertySet propertySet = getPropertySet(userPrincipal, workflowId);
		Map parameters = new HashMap();
		for (Iterator keys = getPropertySet(userPrincipal, workflowId).getKeys().iterator(); keys.hasNext();)
		{
			String key = (String)keys.next();
			parameters.put(key, propertySet.getString(key));
		}

		return parameters;
	}

	/**
	 * Returns all history steps for a workflow, i.e., all the steps that have already been performed.
	 * @param userPrincipal a user principal
	 * @param workflowId the ID of the desired workflow
	 * @return a list of WorkflowStepVOs representing all history steps for the workflow with workflowId
	 */
	public List getHistorySteps(InfoGluePrincipal userPrincipal, long workflowId)
	{
		List historySteps = new ArrayList();
		
		Session session = null;
		net.sf.hibernate.Transaction tx = null;

		try
		{
			session = hibernateSessionFactory.openSession();
			tx = session.beginTransaction();

			WorkflowFacade wf = new WorkflowFacade(userPrincipal, workflowId, session);
			historySteps = wf.getHistorySteps();
			
			session.flush();

			tx.commit();
		}
		catch (Exception e) 
		{
			logger.error("An error occurred when we tries to run getHistorySteps():" + e.getMessage(), e);
			try
			{
				tx.rollback();
			}
			catch (HibernateException he)
			{
				logger.error("An error occurred when we tries to rollback transaction:" + he.getMessage(), he);
			}
		}
		finally 
		{
			try
			{
				session.close();
		    } 
			catch (HibernateException e)
			{
				logger.error("An error occurred when we tries to close session:" + e.getMessage(), e);
			}
		}

		return historySteps;
		//return new WorkflowFacade(userPrincipal, workflowId, true).getHistorySteps();
	}

	/**
	 * Returns all current steps for a workflow, i.e., steps that could be performed in the workflow's current state
	 * @param userPrincipal a user principal
	 * @param workflowId the Id of the desired workflow
	 * @return a list of WorkflowStepVOs representing the current steps of the workflow with workflowId
	 */
	public List getCurrentSteps(InfoGluePrincipal userPrincipal, long workflowId)
	{
		List currentSteps = new ArrayList();
		
		Session session = null;
		net.sf.hibernate.Transaction tx = null;

		try
		{
			session = hibernateSessionFactory.openSession();
			tx = session.beginTransaction();

			WorkflowFacade wf = new WorkflowFacade(userPrincipal, workflowId, session);
			currentSteps = wf.getCurrentSteps();
			
			session.flush();

			tx.commit();
		}
		catch (Exception e) 
		{
			logger.error("An error occurred when we tries to run getCurrentSteps():" + e.getMessage(), e);
			try
			{
				tx.rollback();
			}
			catch (HibernateException he)
			{
				logger.error("An error occurred when we tries to rollback transaction:" + he.getMessage(), he);
			}
		}
		finally 
		{
			try
			{
				session.close();
		    } 
			catch (HibernateException e)
			{
				logger.error("An error occurred when we tries to close session:" + e.getMessage(), e);
			}
		}

		//WorkflowFacade wf = new WorkflowFacade(userPrincipal, workflowId, true);
		//List currentSteps = wf.getCurrentSteps();
		
		return currentSteps;
	}

	/**
	 * Returns all steps for a workflow definition.  These are the steps declared in the workfow descriptor; there is
	 * no knowledge of current or history steps at this point.
	 * @param userPrincipal an InfoGluePrincipal representing a system user
	 * @param workflowId a workflowId
	 * @return a list of WorkflowStepVOs representing all steps in the workflow.
	 */
	public List getAllSteps(InfoGluePrincipal userPrincipal, long workflowId)
	{
		List declaredSteps = new ArrayList();
		
		Session session = null;
		net.sf.hibernate.Transaction tx = null;

		try
		{
			session = hibernateSessionFactory.openSession();
			tx = session.beginTransaction();

			WorkflowFacade wf = new WorkflowFacade(userPrincipal, workflowId, session);
			declaredSteps = wf.getDeclaredSteps();
			
			session.flush();

			tx.commit();
		}
		catch (Exception e) 
		{
			logger.error("An error occurred when we tries to run getAllSteps():" + e.getMessage(), e);
			try
			{
				tx.rollback();
			}
			catch (HibernateException he)
			{
				logger.error("An error occurred when we tries to rollback transaction:" + he.getMessage(), he);
			}
		}
		finally 
		{
			try
			{
				session.close();
		    } 
			catch (HibernateException e)
			{
				logger.error("An error occurred when we tries to close session:" + e.getMessage(), e);
			}
		}

		return declaredSteps;
		//return new WorkflowFacade(userPrincipal, workflowId, true).getDeclaredSteps();
	}

	/**
	 * Returns true if the workflow has terminated; false otherwise.
	 * 
	 * @param workflowVO the workflow.
	 * @return true if the workflow has terminated; false otherwise.
	 */
	public boolean hasTerminated(InfoGluePrincipal userPrincipal, long workflowId) throws WorkflowException
	{
		boolean isFinished = false;
		
		Session session = null;
		net.sf.hibernate.Transaction tx = null;

		try
		{
			session = hibernateSessionFactory.openSession();
			tx = session.beginTransaction();

			WorkflowFacade wf = new WorkflowFacade(userPrincipal, workflowId, session);
			isFinished = wf.isFinished();
			
			session.flush();

			tx.commit();
		}
		catch (Exception e) 
		{
			logger.error("An error occurred when we tries to run hasTerminated:" + e.getMessage(), e);
			try
			{
				tx.rollback();
			}
			catch (HibernateException he)
			{
				logger.error("An error occurred when we tries to rollback transaction:" + he.getMessage(), he);
			}
		}
		finally 
		{
			try
			{
				session.close();
		    } 
			catch (HibernateException e)
			{
				logger.error("An error occurred when we tries to close session:" + e.getMessage(), e);
			}
		}
		
		return isFinished;
		//return new WorkflowFacade(userPrincipal, workflowId, true).isFinished();
	}

	/**
	 * Returns a new WorkflowActionVO.  This method is apparently unused, but is required by BaseController.  We don't
	 * use it internally because it requires a cast; it is simpler to just use <code>new</code> to create an instance.
	 * @return a new WorkflowActionVO.
	 */
	public BaseEntityVO getNewVO()
	{
		return new WorkflowActionVO();
	}
}
