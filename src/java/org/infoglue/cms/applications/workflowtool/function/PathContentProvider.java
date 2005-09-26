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

import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.management.Repository;

import com.opensymphony.workflow.WorkflowException;

/**
 *
 */
public class PathContentProvider extends InfoglueFunction 
{
	/**
	 * The name of the parameter argument.
	 */
	private static final String PARAMETER_NAME_ARGUMENT = "parameter";
	
	/**
	 * The name of the path argument.
	 */
	private static final String PATH_ARGUMENT = "path";
	
	/**
	 * The name of the repository argument.
	 */
	private static final String REPOSITORY_NAME_ARGUMENT = "repository";
	
	/**
	 * The key used to store the content in the <code>parameters</code>.
	 */
	private String parameter;
	
	/**
	 * The name of the repository.
	 */
	private String repositoryName;
	
	/**
	 * The path identifying the content inside the specified <code>repository</code>.
	 */
	private String path;
	
	/**
	 * Default constructor.
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
			getLogger().debug("Using repository=["+ repositoryName + "] path=["+ path + "]");
			final Repository repository = RepositoryController.getController().getRepositoryWithName(repositoryName, getDatabase());
			if(repository == null)
			{
				throwException("No repository with the name [" + repositoryName + "] found.");
			}
			final ContentVO contentVO = ContentController.getContentController().getContentVOWithPath(repository.getId(), path, false, getPrincipal(), getDatabase());
			setParameter(parameter, contentVO);
		} 
		catch(Exception e) 
		{
			throwException(e);
		}
	}

	/**
	 * Method used for initializing the object; will be called before <code>execute</code> is called.
	 * Note! You must call <code>super.initialize()</code> first.
	 * 
	 * @throws WorkflowException if an error occurs during the initialization.
	 */
	protected void initialize() throws WorkflowException 
	{
		super.initialize();
		parameter      = getArgument(PARAMETER_NAME_ARGUMENT);
		path           = getArgument(PATH_ARGUMENT);
		repositoryName = getArgument(REPOSITORY_NAME_ARGUMENT);
	}
}
