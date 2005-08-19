/*
 * WebWork, Web Application Framework
 *
 * Distributable under Apache license.
 * See terms of license at opensource.org
 */
package org.infoglue.deliver.portal.dispatcher;

import java.io.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoglue.cms.security.InfoGluePrincipal;

import webwork.action.Action;
import webwork.action.ActionContext;
import webwork.action.ServletActionContext;
import webwork.dispatcher.ActionResult;
import webwork.dispatcher.GenericDispatcher;
import webwork.dispatcher.ServletDispatcher;
import webwork.util.ServletValueStack;

/**
 * Main dispatcher servlet. It works in three phases: first propagate all
 * parameters to the command JavaBean. Second, call execute() to let the
 * JavaBean create the result data. Third, delegate to the JSP that corresponds to
 * the result state that was chosen by the JavaBean.
 *
 * The command JavaBeans can be found in a package prefixed with either
 * of the package names in the comma-separated "packages" servlet init parameter.
 *
 * Modified by Raymond Lai (alpha2_valen@yahoo.com) on 1 Nov 2003:
 * modified wrapRequest() to set the character encoding of HttpServletRequest
 * using the parameter "webwork.i18n.encoding" in webwork.properties.
 *
 * @author Rickard �berg (rickard@middleware-company.com)
 * @author Matt Baldree (matt@smallleap.com)
 * @version $Revision: 1.3 $
 */
public class DeliveryServletDispatcher extends ServletDispatcher
{
    private String actionExtension = ".action";
    
   /**
    * Service a request.
    * The request is first checked to see if it is a multi-part. If it is, then the request
    * is wrapped so WW will be able to work with the multi-part as if it was a normal request.
    * Next, we will process all actions until an action returns a non-action which is usually
    * a view. For each action in a chain, the action's context will be first set and then the
    * action will be instantiated. Next, the previous action if this action isn't the first in
    * the chain will have its attributes copied to the current action.
    *
    * @param   aRequest
    * @param   aResponse
    * @exception   ServletException
    */
   public void service(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException
   {
       //wrap request if needed
       if(aRequest.getContextPath().indexOf("infoglueCMS") > -1)
       {
           super.service(aRequest, aResponse);
           return;
       }
       
      //aRequest = wrapRequest(aRequest);

      // Get action
      String servletPath = (String) aRequest.getAttribute("javax.servlet.include.servlet_path");
      if (servletPath == null)
         servletPath = aRequest.getServletPath();
      System.out.println("******************************************ServletPath:" + servletPath);
      System.out.println("******************************************getRequestURI:" + aRequest.getRequestURI());
      System.out.println("******************************************getRequestURL:" + aRequest.getRequestURL());

      String actionName = getActionName(servletPath);
      GenericDispatcher gd = new GenericDispatcher(actionName, false);
      ActionContext context = gd.prepareContext();
      
      InfoGluePrincipal principal = (InfoGluePrincipal)aRequest.getSession().getAttribute("infogluePrincipal");
      if(principal != null)
          aRequest.setAttribute("infoglueRemoteUser", principal.getName());

      aRequest.setAttribute("webwork.request_url", aRequest.getRequestURL());
      
      ServletActionContext.setContext(aRequest, aResponse, getServletContext(), actionName);
      gd.prepareValueStack();
      ActionResult ar = null;
      try 
      {
           gd.executeAction();
           ar = gd.finish();
      } 
      catch (Throwable e) 
      {
          log.error("Could not execute action", e);
          try 
          {
              aResponse.sendError(404, "Could not execute action [" + actionName + "]:" + e.getMessage() + getHTMLErrorMessage(e));
          } 
          catch (IOException e1) 
          {
          }
      }

      if (ar != null && ar.getActionException() != null) 
      {
          log.error("Could not execute action", ar.getActionException());
          try 
          {
              aResponse.sendError(500, ar.getActionException().getMessage() + getHTMLErrorMessage(ar.getActionException()));
          } 
          catch (IOException e1) 
          {
          }
      }

      // check if no view exists
      if (ar != null && ar.getResult() != null && ar.getView() == null && !ar.getResult().equals(Action.NONE)) {
          try 
          {
              aResponse.sendError(404, "No view for result [" + ar.getResult() + "] exists for action [" + actionName + "]");
          } 
          catch (IOException e) 
          {
          }
      }

      if (ar != null && ar.getView() != null && ar.getActionException() == null) 
      {
          String view = ar.getView().toString();
          log.debug("Result:" + view);

          RequestDispatcher dispatcher = null;
          try 
          {
               dispatcher = aRequest.getRequestDispatcher(view);
          } 
          catch (Throwable e) 
          {
              // Ignore
          }

          if (dispatcher == null)
              throw new ServletException("No presentation file with name '" + view + "' found!");

          try 
          {
              // If we're included, then include the view
              // Otherwise do forward
              // This allow the page to, for example, set content type
              if (aRequest.getAttribute("javax.servlet.include.servlet_path") == null) 
              {
                   aRequest.setAttribute("webwork.view_uri", view);
                   aRequest.setAttribute("webwork.request_uri", aRequest.getRequestURI());
                   aRequest.setAttribute("webwork.request_url", aRequest.getRequestURL());
                   //aRequest.setAttribute("webwork.contextPath",aRequest.getContextPath());

                   dispatcher.forward(aRequest, aResponse);
              } 
              else 
              {
                   //aRequest.setAttribute("webwork.request_uri",aRequest.getAttribute("javax.servlet.include.request_uri"));
                   //aRequest.setAttribute("webwork.contextPath",aRequest.getAttribute("javax.servlet.include.context_path"));
                   dispatcher.include(aRequest, aResponse);
              }
          } 
          catch (IOException e) 
          {
              throw new ServletException(e);
          } 
          finally 
          {
              // Get last action from stack and and store it in request attribute STACK_HEAD
              // It is then popped from the stack.
              aRequest.setAttribute(STACK_HEAD, ServletValueStack.getStack(aRequest).popValue());
          }
      }

      gd.finalizeContext();
   }

   /**
    * Determine action name by extracting last string and removing
    * extension. (/.../.../Foo.action -> Foo)
    */
   private String getActionName(String name)
   {
      // Get action name ("Foo.action" -> "Foo" action)
      int beginIdx = name.lastIndexOf("/");
      int endIdx = name.lastIndexOf(actionExtension);
      return name.substring((beginIdx == -1 ? 0 : beginIdx + 1),
            endIdx == -1 ? name.length() : endIdx);
   }

}
