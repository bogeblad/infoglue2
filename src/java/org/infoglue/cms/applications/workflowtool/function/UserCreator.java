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
package org.infoglue.cms.applications.workflowtool.function;

import java.util.List;

import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.entities.management.SystemUserVO;
import org.infoglue.cms.security.InfoGlueGroup;
import org.infoglue.cms.security.InfoGlueRole;

import com.opensymphony.workflow.WorkflowException;

public class UserCreator extends InfoglueFunction 
{
	/**
	 * 
	 */
	private static final String STATUS_OK = "status.user.ok";
	
	/**
	 * 
	 */
	private static final String STATUS_NOK = "status.user.nok";

	/**
	 * 
	 */
	private SystemUserVO systemUserVO;
	
	
	/**
	 * 
	 */
	private String[] roleNames;
	
	/**
	 * 
	 */
	private String[] groupNames;
	
	/**
	 * 
	 */
	public UserCreator()
	{
		super();
	}
	
	
	/**
	 * Executes this function.
	 * 
	 * @throws WorkflowException if an error occurs during the execution.
	 */
	protected void execute() throws WorkflowException
	{
		setFunctionStatus(STATUS_NOK);
		if(systemUserVO.validate().isEmpty())
		{
			try
			{
				final UserControllerProxy controller = UserControllerProxy.getController();
				controller.createUser(systemUserVO);
				controller.updateUser(systemUserVO, roleNames, groupNames);
				setFunctionStatus(STATUS_OK);
			}
			catch(Exception e)
			{
				System.out.println(e.getClass().getName());
				//throwException(e);
			}
		}
	}

	/**
	 * 
	 */
	private String[] getRoleNames(final List roles)
	{
		final String[] names = new String[roles.size()];
		for(int i=0; i<roles.size(); ++i)
		{
			final InfoGlueRole role = (InfoGlueRole) roles.get(i);
			names[i] = role.getName();
		}
		return names;
	}
	
	/**
	 * 
	 */
	private String[] getGroupNames(final List groups)
	{
		final String[] names = new String[groups.size()];
		for(int i=0; i<groups.size(); ++i)
		{
			final InfoGlueGroup group = (InfoGlueGroup) groups.get(i);
			names[i] = group.getName();
		}
		return names;
	}
	
	/**
	 * Method used for initializing the function; will be called before <code>execute</code> is called.
	 * <p><strong>Note</strong>! You must call <code>super.initialize()</code> first.</p>
	 * 
	 * @throws WorkflowException if an error occurs during the initialization.
	 */
	protected void initialize() throws WorkflowException
	{
		super.initialize();
		this.systemUserVO = (SystemUserVO) getParameter(UserProvider.USER_PARAMETER);
		this.roleNames    = getRoleNames((List) getParameter(RolesProvider.ROLES_PARAMETER));
		this.groupNames   = getGroupNames((List) getParameter(GroupsProvider.GROUPS_PARAMETER));
	}
}
