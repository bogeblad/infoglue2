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

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.entities.management.SystemUserVO;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.ConstraintExceptionBuffer;


/**
 * This class represents the create user action
 * 
 * @author Mattias Bogeblad
 */


public class CreateSystemUserAction extends WebworkAbstractAction
{
	private ConstraintExceptionBuffer ceb;
	private SystemUserVO systemUserVO;
	private InfoGluePrincipal infoGluePrincipal;
  	//private java.lang.Integer systemUserId;
    /*private java.lang.String userName;
    private java.lang.String password;
    private java.lang.String firstName;
    private java.lang.String lastName;
    private java.lang.String email;*/


	public CreateSystemUserAction()
	{
		this(new SystemUserVO());
	}
	
	public CreateSystemUserAction(SystemUserVO systemUserVO)
	{
		this.systemUserVO = systemUserVO;	
		this.ceb = new ConstraintExceptionBuffer();
	}
	
	public String doInput() throws Exception
    {
    	return "input";
    }
	
	protected String doExecute() throws Exception 
	{
		ceb = this.systemUserVO.validate();
    	ceb.throwIfNotEmpty();		
		
		this.infoGluePrincipal = UserControllerProxy.getController().createUser(this.systemUserVO);

		return "success";
	}
	
	public void setuserName(String userName)
	{
		this.systemUserVO.setUserName(userName);	
	}

    public String getUserName()
    {
        return this.systemUserVO.getUserName();
    }
        
    public java.lang.String getFirstName()
    {
        return this.systemUserVO.getFirstName();
    }
        
    public void setFirstName(java.lang.String firstName)
    {
        this.systemUserVO.setFirstName(firstName);
    }

    public java.lang.String getLastName()
    {
        return this.systemUserVO.getLastName();
    }
        
    public void setLastName(java.lang.String lastName)
    {
        this.systemUserVO.setLastName(lastName);
    }
    
    public java.lang.String getEmail()
    {
    	return this.systemUserVO.getEmail();
    }
    
    public void setEmail(java.lang.String email)
    {
    	this.systemUserVO.setEmail(email);
    }
    
    public java.lang.String getPassword()
    {
    	return this.systemUserVO.getPassword();
    }
    
    public void setPassword(java.lang.String password)
    {
    	this.systemUserVO.setPassword(password);
    }
    
}
