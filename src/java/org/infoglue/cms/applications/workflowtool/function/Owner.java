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

import org.infoglue.cms.controllers.kernel.impl.simple.GroupControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.RoleControllerProxy;
import org.infoglue.cms.security.InfoGlueGroup;
import org.infoglue.cms.security.InfoGlueRole;
import org.infoglue.cms.util.workflow.OwnerFactory;

import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class Owner extends InfoglueFunction 
{
	/**
	 * 
	 */
	private static final String OWNER_PARAMETER = "owner";
	
	/**
	 * 
	 */
	private static final String ROLE_ARGUMENT = "role";
	
	/**
	 * 
	 */
	private static final String GROUP_ARGUMENT = "group";
	
	/**
	 * 
	 */
	private String roleName;
	
	/**
	 * 
	 */
	private String groupName;
	
	/**
	 * 
	 */
	public Owner() 
	{
		super();
	}

	/**
	 * 
	 */
	protected void execute() throws WorkflowException 
	{
		try 
		{
			final InfoGlueRole role = RoleControllerProxy.getController(getDatabase()).getRole(roleName);
			if(groupName == null)
			{
				setParameter(OWNER_PARAMETER, OwnerFactory.create(role).getIdentifier());
			}
			else
			{
				final InfoGlueGroup group = GroupControllerProxy.getController(getDatabase()).getGroup(groupName);
				setParameter(OWNER_PARAMETER, OwnerFactory.create(role, group).getIdentifier());
			}
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
	
	/**
	 * 
	 */
	protected void initialize() throws WorkflowException 
	{
		super.initialize();
		this.roleName  = getArgument(ROLE_ARGUMENT);
		this.groupName = getArgument(GROUP_ARGUMENT, null);
	}
}
