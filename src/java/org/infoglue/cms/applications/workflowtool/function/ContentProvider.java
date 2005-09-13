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
import org.infoglue.cms.entities.content.ContentVO;

import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class ContentProvider extends InfoglueFunction 
{
	/**
	 * 
	 */
	public static final String CONTENT_ID_PROPERTYSET_KEY = ContentPopulator.CONTENT_PROPERTYSET_PREFIX + "contentID";
	
	
	
	/**
	 * 
	 */
	protected void execute() throws WorkflowException 
	{
		if(propertySetContains(CONTENT_ID_PROPERTYSET_KEY))
		{
			try 
			{
				final Integer contentID = new Integer(getPropertySetString(CONTENT_ID_PROPERTYSET_KEY));
				final ContentVO content = ContentController.getContentController().getContentVOWithId(contentID, getDatabase());
				setParameter(ContentFunction.CONTENT_PARAMETER, content);
			} 
			catch(Exception e) 
			{
				getLogger().warn("Non-existing contentId found; removing from the resultset.");
				removeFromPropertySet(CONTENT_ID_PROPERTYSET_KEY);
			}
		}
	}
}
