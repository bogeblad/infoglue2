/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.infoglue.cms.util.workflow;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.cms.util.CmsLogger;

import webwork.action.ActionContext;

import webwork.dispatcher.GenericDispatcher;

import java.security.Principal;

import java.util.*;

import javax.servlet.http.HttpServletRequest;


/**
 * Executes a WebWork function and restores the old ActionContext when finished
 * (but does not provide chaining support yet). The following conversion is done:
 * <ul>
 *  <li>inputs -> ActionContext#parameters</li>
 *  <li>variables -> ActionContext#session</li>
 *  <li>args -> ActionContext#application</li>
 * </ul>
 * <p>
 *
 * <ul>
 *  <li><b>action.name</b> - the actionName to ask from the ActionFactory</li>
 * </ul>
 */
public class CustomClassExecutor implements FunctionProvider 
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(CustomClassExecutor.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException 
    {
    	CmsLogger.logInfo("CustomClassExecutor.execute........");
        final WorkflowContext wfContext = (WorkflowContext) transientVars.get("context");

        String className = (String) args.get("customClass.name");
        HttpServletRequest request = (HttpServletRequest) transientVars.get("request");
        CmsLogger.logInfo("className:" + className);
        
        Iterator paramsIterator = transientVars.keySet().iterator();
	    while(paramsIterator.hasNext())
	    {
	        String key = (String)paramsIterator.next();
	        CmsLogger.logInfo("transientVars key:" + key);
	        Object value = args.get(key);
	        CmsLogger.logInfo("transientVars value:" + value);
	    }
	    
        Map params = new HashMap(transientVars);
        params.putAll(args);
        ActionContext.setParameters(Collections.unmodifiableMap(params));
        
        CustomWorkflowAction customWorkflowAction = getCustomWorkflowActionWithName(className);
        if(customWorkflowAction != null)
            customWorkflowAction.invokeAction(wfContext.getCaller(), request, Collections.unmodifiableMap(params), ps);        
        else
        {
            CmsLogger.logWarning("Could not find custom class " + className + ". Is it in the classpath?");
            throw new WorkflowException("Could not find custom class " + className + ". Is it in the classpath?");
        }
    }
    
    /**
	 * This method instansiate a new object of the given class
	 */
	
	public CustomWorkflowAction getCustomWorkflowActionWithName(String className)
	{
		try 
		{
			Class theClass = null;
			try 
			{
				theClass = Thread.currentThread().getContextClassLoader().loadClass( className );
			}
			catch (ClassNotFoundException e) 
			{
				theClass = getClass().getClassLoader().loadClass( className );
			}
			return (CustomWorkflowAction)theClass.newInstance();
			
		} 
		catch (InstantiationException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
} 