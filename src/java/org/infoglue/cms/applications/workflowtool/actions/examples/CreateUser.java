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

import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.management.SystemUserVO;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.applications.common.VisualFormatter;

import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.workflow.CustomWorkflowAction;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

public class CreateUser implements CustomWorkflowAction
{
	public CreateUser()
	{
	    System.out.println("AAAAAAAAAAAA - yes - inside CreateUser");
	}
	
	
	public void invokeAction(String callerUserName, HttpServletRequest request, Map args, PropertySet ps) throws WorkflowException
	{
	    System.out.println("BBBBBBBBBBBB - yes - inside CreateUser.invokeAction");
	    System.out.println("callerUserName:" + callerUserName);
	    
	    Iterator paramsIterator = request.getParameterMap().keySet().iterator();
	    while(paramsIterator.hasNext())
	    {
	        String key = (String)paramsIterator.next();
	        System.out.println("key:" + key);
	        Object value = request.getParameterMap().get(key);
	        System.out.println("value:" + value);
	    }
	    
	    Iterator psIterator = ps.getKeys().iterator();
	    while(psIterator.hasNext())
	    {
	        String key = (String)psIterator.next();
	        System.out.println("key:" + key);
	        Object value = ps.getObject(key);
	        System.out.println("value:" + value);
	    }
	    
	    String firstName = (String)request.getParameter("firstName");
	    String lastName = (String)request.getParameter("lastName");
	    String userName = (String)request.getParameter("userName");
	    String password = (String)request.getParameter("password");
	    String email = (String)request.getParameter("email");
	    
	    System.out.println("firstName:" + firstName);
        System.out.println("lastName:" + lastName);
        System.out.println("userName:" + userName);
        System.out.println("password:" + password);
        System.out.println("email:" + email);
        
	    SystemUserVO systemUserVO = new SystemUserVO();
	    systemUserVO.setFirstName(firstName);
	    systemUserVO.setLastName(lastName);
	    systemUserVO.setUserName(userName);
	    systemUserVO.setPassword(password);
	    systemUserVO.setEmail(email);
	    
	    try
        {
	        System.out.println("firstName:" + systemUserVO.getFirstName());
	        System.out.println("lastName:" + systemUserVO.getLastName());
	        System.out.println("userName:" + systemUserVO.getUserName());
	        System.out.println("password:" + systemUserVO.getPassword());
	        System.out.println("email:" + systemUserVO.getEmail());
            UserControllerProxy.getController().createUser(systemUserVO);
        } 
	    catch (Exception e)
        {
            e.printStackTrace();
            throw new WorkflowException(e);
        }
	}

}
