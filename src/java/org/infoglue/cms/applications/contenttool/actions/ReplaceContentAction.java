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

import org.infoglue.cms.controllers.kernel.impl.simple.SearchController;



/**
 * Action class for usecase ReplaceContentAction. 
 *
 * @author Magnus Güvenal
 * @author Mattias Bogeblad
 */

public class ReplaceContentAction extends SearchContentAction 
{
	//This is for replace
    //private Integer repositoryId	= null;
    //private String searchString		= null;
	private String replaceString	= null;
	private String contentVersionId = null;
		
	public String doExecute() throws Exception 
	{
	    String contentVersionIds[] = contentVersionId.split(",");
	    
	    SearchController.replaceString(getSearchString(), this.replaceString, contentVersionIds, this.getInfoGluePrincipal());
	    
        return "success";
	}
	
    public String getContentVersionId()
    {
        return contentVersionId;
    }
    
    public void setContentVersionId(String contentVersionId)
    {
        if(contentVersionId != null && !contentVersionId.equalsIgnoreCase(""))
            this.contentVersionId = contentVersionId.substring(1);
    }

    public String getReplaceString()
    {
        return replaceString;
    }
    
    public void setReplaceString(String replaceString)
    {
        this.replaceString = replaceString;
    }
}
