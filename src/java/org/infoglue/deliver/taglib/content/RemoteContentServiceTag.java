package org.infoglue.deliver.taglib.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.deliver.taglib.TemplateControllerTag;
import org.infoglue.deliver.util.webservices.DynamicWebservice;


/**
 * This tag helps create a content in the cms from the delivery application.
 */

public class RemoteContentServiceTag extends TemplateControllerTag 
{
	/**
	 * The universal version identifier.
	 */
	private static final long serialVersionUID = -1904980538720103871L;

	/**
	 * The list of contents that should be stored.
	 */
	private List contents = new ArrayList();	
	
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
	 * Initializes the parameters to make it accessible for the children tags (if any).
	 * 
	 * @return indication of whether to evaluate the body or not.
	 * @throws JspException if an error occurred while processing this tag.
	 */
	public int doStartTag() throws JspException 
	{
	    return EVAL_BODY_INCLUDE;
	}

	/**
	 *
	 */
   public int doEndTag() throws JspException
   {
	   try
	   {
	       if(this.principal == null)
	           this.principal = this.getController().getPrincipal();
	       
		   final DynamicWebservice ws = new DynamicWebservice(principal);
		  
		   ws.setTargetEndpointAddress(targetEndpointAddress);
		   ws.setOperationName(operationName);
		   ws.setReturnType(Boolean.class);
		   
	       //List contents = new ArrayList();
		   //Map content = new HashMap();
		   //content.put("name", "Mattias Testar");
		   //contents.add(content);
	       
		   ws.addArgument("contents", contents);
		   
		   ws.callService();
		   setResultAttribute(ws.getResult());
	   }   
	   catch(Exception e)
	   {
		   e.printStackTrace();
		   throw new JspTagException(e.getMessage());
	   }
	   
	   contents.clear();

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
    * Add the content the child tag generated to the list of contents that are to be persisted.
    */
   /*
   public void addContent(final ContentVO contentVO) 
   {
       //System.out.println("ADDING CONTENT");
       //Map contentMap = new HashMap();
       //contentMap.put("name", "Kalle" + contentVO.getName());
       //this.contents.add(contentMap);
	   this.contents.add(contentVO);
   }
   */
   
   /**
    * Add the content the child tag generated to the list of contents that are to be persisted.
    */
   public void addContentMap(final Map contentMap) 
   {
	   this.contents.add(contentMap);
   }

}