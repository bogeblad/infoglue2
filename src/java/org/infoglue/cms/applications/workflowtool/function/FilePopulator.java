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

import java.io.File;

import org.infoglue.cms.io.FileHelper;
import org.infoglue.cms.util.CmsPropertyHandler;

import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class FilePopulator extends InfoglueFunction 
{
	/**
	 * 
	 */
	private static final String PATH_ARGUMENT = "path";
	
	/**
	 * 
	 */
	private static final String PROPERTYSET_KEY_ARGUMENT = "key";
	
	/**
	 * 
	 */
	private String path;
	
	/**
	 * 
	 */
	private String key;
	
	/**
	 * 
	 */
	public FilePopulator() 
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
			final String fullPath = getFullPath();
			final String unparsed = FileHelper.getFileAsString(new File(fullPath));
			final String parsed   = translate(unparsed);
			
			getLogger().debug("path=[" + fullPath + "],unparsed=[" + unparsed + "],parsed=[" + parsed + "]");
			setPropertySetDataString(key, parsed);
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
	
	/**
	 * 
	 */
	private String getFullPath()
	{
		return CmsPropertyHandler.getProperty("contextRootPath") + path;
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
		this.path = getArgument(PATH_ARGUMENT); 
		this.key  = getArgument(PROPERTYSET_KEY_ARGUMENT);
	}
}
