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


package org.infoglue.deliver.applications.actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.*;
import org.infoglue.deliver.controllers.kernel.impl.simple.ExtranetController;
import org.infoglue.deliver.util.HttpRequestForwarder;


import webwork.action.ActionContext;


/**
* This is the action that takes care of all invokations of workflow actions.
*
* @author Mattias Bogeblad
*/

public class InvokeWorkflowAction extends InfoGlueAbstractAction 
{
    private Long workflowId;
    private Integer actionId;
    
	/**
	 * The constructor for this action - contains nothing right now.
	 */
   
    public InvokeWorkflowAction() 
    {
	
    }
   
   
   /**
    * This method is the application entry-point. The parameters has been set through the setters
    * and now we just have to render the appropriate output. 
    */
        
    public String doExecute() throws Exception
    {
        getLogger().info("****************************************");
        getLogger().info("*     DELIVER InvokeWorkflowAction     *");
        getLogger().info("workflowId:" + workflowId);
        getLogger().info("actionId:" + actionId);
        getLogger().info("email:" + ActionContext.getParameters().get("email"));
        getLogger().info("****************************************");
       
        getLogger().info("Redirecting Invokation of workflow action to cms instead as that is where the original is");
        
        InfoGluePrincipal infoGluePrincipal = this.getInfoGluePrincipal();
        if(infoGluePrincipal == null)
        {
            Map arguments = new HashMap();
            arguments.put("j_username", "anonymous");
		    arguments.put("j_password", "anonymous");
		    
            infoGluePrincipal = (InfoGluePrincipal) ExtranetController.getController().getAuthenticatedPrincipal(arguments);
        }
        
        getLogger().info("Starting remote invokation");
        HttpClient client = new HttpClient();
        //HttpURL destination = new HttpURL("http://localhost:8080/infoglueCMS2Dev/InvokeWorkflow.action");
        HttpRequestForwarder method = new HttpRequestForwarder(this.getRequest(), "http://localhost:8080/infoglueCMS2Dev/InvokeWorkflow.action");
        int status = client.executeMethod(method);
        getLogger().info(status + "\n" + method.getResponseBodyAsString());
        method.releaseConnection();
        getLogger().info("Ending remote invokation");

        //HttpUtilities.postToUrl("http://localhost:8080/infoglueCMS2Dev/InvokeWorkflow.action", HttpUtilities.requestToHashtable(this.getRequest()));
        
        //WorkflowController.getController().invokeAction(infoGluePrincipal, ActionContext.getRequest(), workflowId, actionId);

        /*
        List currentWorkflowActionVOList = WorkflowController.getController().getCurrentWorkflowActionVOList(infoGluePrincipal, workflowId.longValue());
        if(currentWorkflowActionVOList.size() > 0)
        {
            WorkflowActionVO workflowActionVO = (WorkflowActionVO)currentWorkflowActionVOList.get(0);
            String url = "";
            if(workflowActionVO.getView().indexOf("?") > -1)
                url = workflowActionVO.getView() + "&workflowId=" + workflowId + "&actionId=" + workflowActionVO.getId() + "&returnAddress=" + URLEncoder.encode(this.getURLBase() + "/InvokeWorkflow.action", "UTF-8");
            else
                url = workflowActionVO.getView() + "?workflowId=" + workflowId + "&actionId=" + workflowActionVO.getId() + "&returnAddress=" + URLEncoder.encode(this.getURLBase() + "/InvokeWorkflow.action", "UTF-8");
                
            getLogger().info("Url in doInvoke:" + url);
            getResponse().sendRedirect(url);
           
            return NONE;
        }
        */
        
       	getLogger().info("No new action...");
       
		return "workflowDone";   
		
    }
   
    public Integer getActionId()
    {
        return actionId;
    }
   
    public void setActionId(Integer actionId)
    {
        this.actionId = actionId;
    }
   
    public String getWorkflowId()
    {
        return workflowId.toString();
    }
   
    public void setWorkflowId(String workflowId)
    {
        this.workflowId = new Long(workflowId);
    }
}
