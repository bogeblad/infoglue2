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

import org.infoglue.cms.util.StringManager;
import org.infoglue.cms.util.StringManagerFactory;

import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public abstract class ErrorPopulator extends InfoglueFunction 
{
	/**
	 * 
	 */
	private static final String PACKAGE = "org.infoglue.cms.entities";
	
	/**
	 * 
	 */
	public static final String ERROR_PROPERTYSET_PREFIX = "error_";

	/**
	 * 
	 */
	private StringManager stringManager; 
	
	/**
	 * 
	 */
	protected ErrorPopulator()
	{
		super();
	}
	
	/**
	 * 
	 */
	protected final void execute() throws WorkflowException
	{
		clean();
		populate();
	}
	
	/**
	 * 
	 */
	protected abstract void clean() throws WorkflowException;

	/**
	 * 
	 */
	protected abstract void populate() throws WorkflowException;
	
	/**
	 * 
	 */
	protected final void clean(final String errorPrefix) throws WorkflowException
	{
		removeFromPropertySet(errorPrefix, true);
	}

	/**
	 * 
	 */
	protected void initialize() throws WorkflowException 
	{
		super.initialize();
		stringManager = StringManagerFactory.getPresentationStringManager(PACKAGE, getLocale()); 
	}
	
	/**
	 * 
	 */
	protected final StringManager getStringManager()
	{
	    return stringManager;
	}
}
