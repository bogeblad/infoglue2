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

package org.infoglue.cms.applications.contenttool.actions;

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.util.CmsLogger;

import java.util.List;

/**
 * 	Action class for usecase ViewListContentUCC 
 *
 *  @author Mattias Bogeblad
 */

public class ViewListContentAction extends WebworkAbstractAction 
{

	private List contentVOList;
	

	protected String doExecute() throws Exception 
	{
	
	    CmsLogger.logInfo("Executing doExecute on ViewListContentAction..");
		//ViewListContentUCC viewListContentUCC = ViewListContentUCCFactory.newViewListContentUCC();
		//this.contentVOList = viewListContentUCC.viewListContent();
	    CmsLogger.logInfo("Finished executing doExecute on ViewListContentAction..");
        return "success";
	}
	

	public List getContents()
	{
		return this.contentVOList;		
	}
	
}
