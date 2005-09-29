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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.infoglue.cms.controllers.kernel.impl.simple.RoleControllerProxy;

import com.opensymphony.workflow.WorkflowException;

public class RolesProvider extends InfoglueFunction {
	/**
	 * 
	 */
	public static final String ROLES_PARAMETER = "roles";
	
	/**
	 * 
	 */
	private static final String ROLES_PROPERTYSET_PREFIX = "roles_";
	
	/**
	 * 
	 */
	private List roles = new ArrayList();
	
	/**
	 * 
	 */
	public RolesProvider()
	{
		super();
	}
	
	/**
	 * 
	 */
	protected void execute() throws WorkflowException 
	{
		populateRoles();
		setParameter(ROLES_PARAMETER, roles);
	}
	
	/**
	 * 
	 */
	private void populateRoles() throws WorkflowException
	{
		for(final Iterator i = getParameters().keySet().iterator(); i.hasNext(); ) 
		{
			final String key = i.next().toString();
			if(key.startsWith(ROLES_PROPERTYSET_PREFIX))
			{
				populateRole(key.substring(ROLES_PROPERTYSET_PREFIX.length()));
			}
		}
	}

	/**
	 * 
	 */
	private void populateRole(final String roleName) throws WorkflowException
	{
		try
		{
			roles.add(RoleControllerProxy.getController().getRole(roleName));
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
}
