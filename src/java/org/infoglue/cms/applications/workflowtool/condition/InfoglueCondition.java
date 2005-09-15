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
package org.infoglue.cms.applications.workflowtool.condition;

import java.util.Map;

import org.infoglue.cms.applications.workflowtool.util.InfoglueWorkflowBase;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public abstract class InfoglueCondition extends InfoglueWorkflowBase implements Condition 
{
	/**
	 * Default constructor.
	 */
	protected InfoglueCondition() 
	{ 
		super(); 
	}

	/**
	 * 
	 */
	public final boolean passesCondition(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException 
	{
		boolean result = false;
		try 
		{
			storeContext(transientVars, args, ps);
			getLogger().debug(getClass().getName() + ".passesCondition()--------- START");
			initialize();
			result = passesCondition();
			getLogger().debug(getClass().getName() + ".passesCondition()--------- STOP (" + result + ")");
		}
		catch(Exception e)
		{
			throwException(e);
		}
		return result;
	}
	
	/**
	 * 
	 */
	protected abstract boolean passesCondition() throws WorkflowException;
}
