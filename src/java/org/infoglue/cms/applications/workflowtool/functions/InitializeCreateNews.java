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
 * $Id: InitializeCreateNews.java,v 1.1 2005/01/18 19:11:54 jed Exp $
 */
package org.infoglue.cms.applications.workflowtool.functions;

import java.util.*;

import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.util.CmsLogger;

/**
 * THIS IS VERY TEMPORARY SOLUTION FOR ASSESSING WHERE TO PUT THE NEWS ITEMS.
 * @version $Revision: 1.1 $ $Date: 2005/01/18 19:11:54 $
 */
public class InitializeCreateNews implements FunctionProvider
{
	public void execute(Map transientVars, Map args, PropertySet propertySet)
	{
		System.out.println("Now I start by setting some basic parameters like where this content should end up...");
		Integer repositoryId = null;
		Integer parentContentId = null;
		Integer contentTypeDefinitionId = null;

		try
		{
			List repositories = RepositoryController.getController().getRepositoryVOList();
			Iterator repositoriesIterator = repositories.iterator();
			while (repositoriesIterator.hasNext())
			{
				RepositoryVO repositoryVO = (RepositoryVO)repositoriesIterator.next();
				if (repositoryVO.getName().equalsIgnoreCase("www.officestand.com"))
				{
					repositoryId = repositoryVO.getRepositoryId();
					break;
				}
			}

			System.out.println("Found repositoryId when trying to assess where to put the news: " + repositoryId);

			ContentVO rootContentVO = ContentController.getContentController().getRootContentVO(repositoryId, "${caller}");
			System.out.println("rootContentVO: " + rootContentVO.getName());

			List children = ContentController.getContentController().getContentChildrenVOList(rootContentVO.getId());
			Iterator childrenIterator = children.iterator();
			while (childrenIterator.hasNext())
			{
				ContentVO childContentVO = (ContentVO)childrenIterator.next();
				System.out.println("childContentVO: " + childContentVO.getName());
				if (childContentVO.getName().equalsIgnoreCase("News Items"))
				{
					parentContentId = childContentVO.getId();
					break;
				}
			}

			contentTypeDefinitionId = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOWithName("Article").getContentTypeDefinitionId();
			System.out.println("contentTypeDefintionId: " + contentTypeDefinitionId);
		}
		catch (Exception e)
		{
			CmsLogger.logInfo("An error occurred trying to assess the place where to put it.");
		}

		System.out.println("parentContentId:" + parentContentId);
		System.out.println("contentTypeDefinitionId:" + contentTypeDefinitionId);
		System.out.println("repositoryId:" + repositoryId);

		propertySet.setString("parentContentId", "" + parentContentId);
		propertySet.setString("contentTypeDefinitionId", "" + contentTypeDefinitionId);
		propertySet.setString("repositoryId", "" + repositoryId);

	}
}
