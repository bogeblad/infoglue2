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

package org.infoglue.cms.applications.cmstool.actions;

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;

/**
 * This class implements the action class for the base fram for the entire tool.
 * 
 * @author Mattias Bogeblad  
 */

public class ViewCMSToolAction extends InfoGlueAbstractAction
{
	private static final long serialVersionUID = -2904286525405758091L;

	private Integer contentId = -1;
	private Integer siteNodeId = -1;
	private Integer languageId = -1;
	

	public String doExecute() throws Exception
    {
        return "success";
    }
	
	public String doLoginStandalone() throws Exception 
	{
		return "loginComplete";
	}	

	public Integer getContentId() 
	{
		return contentId;
	}

	public void setContentId(Integer contentId) 
	{
		this.contentId = contentId;
	}

	public Integer getSiteNodeId() 
	{
		return siteNodeId;
	}

	public void setSiteNodeId(Integer siteNodeId) 
	{
		this.siteNodeId = siteNodeId;
	}

	public Integer getLanguageId() 
	{
		return languageId;
	}

	public void setLanguageId(Integer languageId) 
	{
		this.languageId = languageId;
	}

}
