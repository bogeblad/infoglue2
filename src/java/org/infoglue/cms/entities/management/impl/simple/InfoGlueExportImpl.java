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

package org.infoglue.cms.entities.management.impl.simple;

import org.infoglue.cms.entities.content.impl.simple.ContentImpl;
import org.infoglue.cms.entities.structure.impl.simple.SiteNodeImpl;

public class InfoGlueExportImpl
{
	private Integer infoGlueExportId;
	private ContentImpl rootContent;
	private SiteNodeImpl rootSiteNode;
	  
	public Integer getInfoGlueExportId()
	{
		return infoGlueExportId;
	}

	public void setInfoGlueExportId(Integer infoGlueExportId)
	{
		this.infoGlueExportId = infoGlueExportId;
	}

	public ContentImpl getRootContent()
	{
		return rootContent;
	}

	public SiteNodeImpl getRootSiteNode()
	{
		return rootSiteNode;
	}

	public void setRootContent(ContentImpl impl)
	{
		rootContent = impl;
	}

	public void setRootSiteNode(SiteNodeImpl impl)
	{
		rootSiteNode = impl;
	}

}        
