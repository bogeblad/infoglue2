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

package org.infoglue.cms.applications.workflowtool.actions.examples;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.entities.management.SystemUserVO;
import org.infoglue.cms.util.workflow.CustomWorkflowAction;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

public class CreateUser implements CustomWorkflowAction
{
	public CreateUser()
	{
	}
	
	
	public void invokeAction(String callerUserName, HttpServletRequest request, Map args, PropertySet ps) throws WorkflowException
	{
	    String firstName = (String)request.getParameter("firstName");
	    String lastName = (String)request.getParameter("lastName");
	    String userName = (String)request.getParameter("userName");
	    String password = (String)request.getParameter("password");
	    String email = (String)request.getParameter("email");
        
	    SystemUserVO systemUserVO = new SystemUserVO();
	    systemUserVO.setFirstName(firstName);
	    systemUserVO.setLastName(lastName);
	    systemUserVO.setUserName(userName);
	    systemUserVO.setPassword(password);
	    systemUserVO.setEmail(email);
	    
	    try
        {
            UserControllerProxy.getController().createUser(systemUserVO);
        } 
	    catch (Exception e)
        {
            e.printStackTrace();
            throw new WorkflowException(e);
        }
	}

}
