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

import org.infoglue.cms.applications.workflowtool.function.InfoglueFunction;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.management.RepositoryVO;

import com.opensymphony.workflow.WorkflowException;

/**
 *
 */
public class PathContentProvider extends InfoglueFunction 
{
	/**
	 * Indicates how the arguments PATH_ARGUMENT and REPOSITORY_NAME_ARGUMENT should be used.
	 * If the mode is PROPERTYSET_MODE_ARGUMENT, then the arguments specifies propertyset keys.
	 * Otherwise, the arguments specifies the values to use.
	 */
	private static final String MODE_ARGUMENT = "mode";
	
	/**
	 * 
	 */
	private static final String PROPERTYSET_MODE_ARGUMENT = "propertyset";
	
	/**
	 * 
	 */
	private static final String PARAMETER_NAME_ARGUMENT = "parameter";
	
	/**
	 * 
	 */
	private static final String PATH_ARGUMENT = "path";
	
	/**
	 * 
	 */
	private static final String REPOSITORY_NAME_ARGUMENT = "repository";
	
	/**
	 * 
	 */
	private String parameter;
	
	/**
	 * 
	 */
	private String repositoryName;
	
	/**
	 * 
	 */
	private String path;
	
	/**
	 * 
	 */
	public PathContentProvider() 
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
			final RepositoryVO repository = RepositoryController.getController().getRepositoryWithName(repositoryName, getDatabase()).getValueObject();
			final ContentVO contentVO = ContentController.getContentController().getContentVOWithPath(repository.getId(), path, false, getPrincipal(), getDatabase());
			setParameter(parameter, contentVO);
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
		parameter      = getArgument(PARAMETER_NAME_ARGUMENT);
		path           = getPath();
		repositoryName = getRepositoryName();
	}
	
	/**
	 * 
	 */
	private boolean isPropertysetMode() throws WorkflowException
	{
		final boolean b = argumentExists(MODE_ARGUMENT) ? PROPERTYSET_MODE_ARGUMENT.equals(getArgument(MODE_ARGUMENT)) : false;
		getLogger().debug("Using " + (b ? "propertyset" : "normal") + " mode.");
		return b;
	}
	
	/**
	 * 
	 */
	private String getRepositoryName() throws WorkflowException
	{
		return isPropertysetMode() ? getPropertySetString(getArgument(REPOSITORY_NAME_ARGUMENT)) : getArgument(REPOSITORY_NAME_ARGUMENT);
	}

	/**
	 * 
	 */
	private String getPath() throws WorkflowException
	{
		return isPropertysetMode() ? getPropertySetString(getArgument(PATH_ARGUMENT)) : getArgument(PATH_ARGUMENT);
	}
}
