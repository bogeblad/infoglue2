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

package org.infoglue.cms.applications.managementtool.actions;

import java.util.Iterator;
import java.util.List;

import org.infoglue.cms.controllers.kernel.impl.simple.GroupControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.RedirectController;
import org.infoglue.cms.controllers.kernel.impl.simple.RoleControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.entities.management.RedirectVO;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.mail.MailServiceFactory;

public class CreateEmailAction extends InfoGlueAbstractAction
{
	private List users = null;
	private List roles = null;
	private List groups = null;
	
	private String[] userNames;
	private String[] roleNames;
	private String[] groupNames;
	
	private String usersAddresses = "";
	
	private String recipients;
	private String from;
	private String subject;
	private String message;
	
	private String errorMessage = "";
	
    public String doExecute() throws Exception
    {
    	if(recipients != null && recipients.length() > 0 && subject != null && subject.length() > 0 && message != null && message.length() > 0)
    	{
			if(from == null  || from.length() == 0)
			{
    			String systemEmailSender = CmsPropertyHandler.getProperty("systemEmailSender");
    			if(systemEmailSender == null || systemEmailSender.equalsIgnoreCase(""))
    				systemEmailSender = "InfoGlueCMS@" + CmsPropertyHandler.getProperty("mail.smtp.host");

    			from = systemEmailSender;
			}
			
			message = "<div>" + message.replaceAll("\n", "<br/>") + "</div>";
			
			MailServiceFactory.getService().sendEmail(from, from, recipients, subject, message, "utf-8");
    	}
    	else
    	{
    		errorMessage = "Must enter information in all fields below.";
    		return "inputCreateEmail";
    	}
    	
        return "success";
    }
        
    public String doInputChooseRecipients() throws Exception
    {
    	users 	= UserControllerProxy.getController().getAllUsers();
    	roles 	= RoleControllerProxy.getController().getAllRoles();
    	groups 	= GroupControllerProxy.getController().getAllGroups();
    	
    	return "inputChooseRecipients";
    }

    public String doInputCreateEmail() throws Exception
    {
    	userNames 	= getRequest().getParameterValues("userName");
    	roleNames 	= getRequest().getParameterValues("roleName");
    	groupNames 	= getRequest().getParameterValues("groupName");

    	if(userNames != null)
    	{
	    	for(int i=0; i<userNames.length; i++)
	    	{
	    		String userName = userNames[i];
	    		InfoGluePrincipal principal = UserControllerProxy.getController().getUser(userName);
	    		if(usersAddresses.indexOf(principal.getEmail()) == -1)
	    		{
		    		if(usersAddresses.length() > 0)
		    			usersAddresses += ";";
		    		
		    		usersAddresses += principal.getEmail();
	    		}
	    	}
    	}
    	
    	if(roleNames != null)
    	{
	    	for(int i=0; i<roleNames.length; i++)
	    	{
	    		String roleName = roleNames[i];
	    		
	    		List principals = RoleControllerProxy.getController().getInfoGluePrincipals(roleName);
	    		Iterator principalsIterator = principals.iterator();
	    		while(principalsIterator.hasNext())
	    		{
		    		InfoGluePrincipal principal = (InfoGluePrincipal)principalsIterator.next();
		    		if(usersAddresses.indexOf(principal.getEmail()) == -1)
		    		{
			    		if(usersAddresses.length() > 0)
			    			usersAddresses += ";";
			    		
			    		usersAddresses += principal.getEmail();
		    		}
	    		}
	    	}
    	}
    	
    	if(groupNames != null)
    	{	
	    	for(int i=0; i<groupNames.length; i++)
	    	{
	    		String groupName = groupNames[i];
	    		
	    		List principals = GroupControllerProxy.getController().getInfoGluePrincipals(groupName);
	    		Iterator principalsIterator = principals.iterator();
	    		while(principalsIterator.hasNext())
	    		{
		    		InfoGluePrincipal principal = (InfoGluePrincipal)principalsIterator.next();
		    		if(usersAddresses.indexOf(principal.getEmail()) == -1)
		    		{
			    		if(usersAddresses.length() > 0)
			    			usersAddresses += ";";
			    		
			    		usersAddresses += principal.getEmail();
		    		}
	    		}
	    	}
    	}
    	
    	return "inputCreateEmail";
    }

	public List getGroups() 
	{
		return groups;
	}

	public List getRoles() 
	{
		return roles;
	}

	public List getUsers() 
	{
		return users;
	}

	public String getUsersAddresses()
	{
		return usersAddresses;
	}

	public String getMessage() 
	{
		return message;
	}

	public void setMessage(String message) 
	{
		this.message = message;
	}

	public String getRecipients() 
	{
		return recipients;
	}

	public void setRecipients(String recipients) 
	{
		this.recipients = recipients;
	}

	public String getSubject() 
	{
		return subject;
	}

	public void setSubject(String subject) 
	{
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getErrorMessage() {
		return errorMessage;
	}    
}
