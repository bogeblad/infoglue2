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
 * $Id: InitializeCreateNews.java,v 1.6 2005/07/10 21:04:34 mattias Exp $
 */
package org.infoglue.cms.applications.workflowtool.functions;

import java.util.*;

import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.tasktool.actions.BasicScriptController;
import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.entities.content.ContentVO;


/**
 * THIS IS VERY TEMPORARY SOLUTION FOR ASSESSING WHERE TO PUT THE NEWS ITEMS.
 * @version $Revision: 1.6 $ $Date: 2005/07/10 21:04:34 $
 */
public class InitializeCreateNews implements FunctionProvider
{
    private final static Logger logger = Logger.getLogger(InitializeCreateNews.class.getName());

	public void execute(Map transientVars, Map args, PropertySet propertySet)
	{
		System.out.println("Now I start by setting some basic parameters like where this content should end up...");

		try
		{
		    Integer repositoryId 	= new Integer(((String[])transientVars.get("repositoryId"))[0]);
		    Integer parentContentId = new Integer(((String[])transientVars.get("parentContentId"))[0]);
		    Integer languageId 		= new Integer(((String[])transientVars.get("languageId"))[0]);
		    Integer contentTypeDefinitionId = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithName("Article").getContentTypeDefinitionId();
			
		    logger.info("parentContentId:" + parentContentId);
		    logger.info("contentTypeDefinitionId:" + contentTypeDefinitionId);
		    logger.info("repositoryId:" + repositoryId);
		    logger.info("languageId:" + languageId);

			propertySet.setString("parentContentId", parentContentId.toString());
			propertySet.setString("contentTypeDefinitionId", contentTypeDefinitionId.toString());
			propertySet.setString("repositoryId", repositoryId.toString());
			propertySet.setString("languageId", languageId.toString());
		}
		catch (Exception e)
		{
		    logger.info("An error occurred trying to assess the place where to put it.");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
