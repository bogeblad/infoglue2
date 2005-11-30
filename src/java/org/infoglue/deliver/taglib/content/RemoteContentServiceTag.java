package org.infoglue.deliver.taglib.content;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.deliver.taglib.AbstractTag;
import org.infoglue.deliver.util.webservices.DynamicWebservice;


/**
 * This tag helps create a content in the cms from the delivery application.
 */

public class RemoteContentServiceTag extends AbstractTag 
{
	/**
	 * The universal version identifier.
	 */
	private static final long serialVersionUID = -1904980538720103871L;

	/**
	 * 
	 */
	private String name;
	
	/**
	 * 
	 */
	private String targetEndpointAddress;
	
	/**
	 * 
	 */
	private String operationName;
	
	/**
	 * 
	 */
	private InfoGluePrincipal principal;
	
	/**
	 * 
	 */
	public RemoteContentServiceTag() 
	{
		super();
	}

	/**
	 *
	 */
   public int doEndTag() throws JspException
   {
	   try
	   {
		   final DynamicWebservice ws = new DynamicWebservice(principal);
		  
		   ws.setTargetEndpointAddress(targetEndpointAddress);
		   ws.setOperationName(operationName);
		   ws.setReturnType(Boolean.class);
		   
		   //ws.addArgument("contentVO", contentVO);
		   //contentVO, int parentContentId, int contentTypeDefinitionId, int repositoryId
		   ws.callService();
		   setResultAttribute(ws.getResult());
	   }   
	   catch(Exception e)
	   {
		   e.printStackTrace();
		   throw new JspTagException(e.getMessage());
	   }
       return EVAL_PAGE;
   }
   
   /**
    * 
    */
   public void setTargetEndpointAddress(final String targetEndpointAddress) throws JspException
   {
	   this.targetEndpointAddress = evaluateString("remoteContentService", "targetEndpointAddress", targetEndpointAddress);
   }

   /**
    * 
    */
   public void setOperationName(final String operationName) 
   {
	   this.operationName = operationName;
   }

   /**
    * 
    */
   public void setPrincipal(final String principalString) throws JspException
   {
	   this.principal = (InfoGluePrincipal) this.evaluate("remoteContentService", "principal", principalString, InfoGluePrincipal.class);
   }

   /**
    * 
    */
   public void setWorkflowName(final String name) 
   {
	   this.name = name;
   }


}