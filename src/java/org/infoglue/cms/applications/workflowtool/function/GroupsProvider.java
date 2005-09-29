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

import org.infoglue.cms.controllers.kernel.impl.simple.GroupControllerProxy;

import com.opensymphony.workflow.WorkflowException;

public class GroupsProvider extends InfoglueFunction {
	/**
	 * 
	 */
	public static final String GROUPS_PARAMETER = "groups";
	
	/**
	 * 
	 */
	private static final String GROUPS_PROPERTYSET_PREFIX = "groups_";
	
	/**
	 * 
	 */
	private List groups = new ArrayList();
	
	/**
	 * 
	 */
	public GroupsProvider()
	{
		super();
	}
	
	/**
	 * 
	 */
	protected void execute() throws WorkflowException 
	{
		populateGroups();
		setParameter(GROUPS_PARAMETER, groups);
	}
	
	/**
	 * 
	 */
	private void populateGroups() throws WorkflowException
	{
		for(final Iterator i = getParameters().keySet().iterator(); i.hasNext(); ) 
		{
			final String key = i.next().toString();
			if(key.startsWith(GROUPS_PROPERTYSET_PREFIX))
			{
				populateGroup(key.substring(GROUPS_PROPERTYSET_PREFIX.length()));
			}
		}
	}

	/**
	 * 
	 */
	private void populateGroup(final String groupName) throws WorkflowException
	{
		try
		{
			groups.add(GroupControllerProxy.getController().getGroup(groupName));
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
}
