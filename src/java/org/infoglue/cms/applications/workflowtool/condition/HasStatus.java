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

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.workflowtool.function.InfoglueFunction;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.WorkflowException;

public class HasStatus implements Condition 
{
	/**
	 * 
	 */
    private final static Logger logger = Logger.getLogger(HasStatus.class.getName());
	/**
	 * 
	 */
	private final static String ARGUMENT_WANTED_STATUS = "status";
	
	/**
	 * 
	 */
	private String wantedStatus;
	
	/**
	 * 
	 */
	private String currentStatus;
	
	
	
	/**
	 * 
	 */
	public HasStatus() 
	{ 
		super(); 
	}

	/**
	 * 
	 */
	public boolean passesCondition(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException 
	{
		initialize(transientVars, args, ps);
		logger.debug("HasStatus.passesCondition() : " + wantedStatus.equals(currentStatus));
		return wantedStatus.equals(currentStatus);
	}

	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException 
	{
		if(!args.containsKey(ARGUMENT_WANTED_STATUS)) 
		{
			final WorkflowException e = new WorkflowException("The wanted status argument is missing.");
			logger.error(e.toString());
			throw e;
		}
		wantedStatus  = (String) args.get(ARGUMENT_WANTED_STATUS);
		currentStatus = (String) (ps.exists(InfoglueFunction.STATUS_PROPERTYSET_KEY) ? ps.getString(InfoglueFunction.STATUS_PROPERTYSET_KEY) : null); 
	}
}
