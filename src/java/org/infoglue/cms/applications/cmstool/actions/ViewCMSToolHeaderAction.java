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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;

import org.infoglue.cms.util.CmsPropertyHandler;

/**
 * This class implements the action class for the base fram for the entire tool.
 * 
 * @author Mattias Bogeblad  
 */

public class ViewCMSToolHeaderAction extends InfoGlueAbstractAction
{
    private Integer toolId = new Integer(0);
	private String userName = "";

    public void setToolId(Integer toolId)
    {
    	this.toolId = toolId;
    }

    public Integer getToolId()
    {
    	return this.toolId;
    }

	public String getUserName()
	{
		return this.getInfoGluePrincipal().getName();
	}
  	
	public List getToolLocales()
	{
	    List toolLocales = new ArrayList();
	    
	    int index = 0;
	    String languageCode = CmsPropertyHandler.getProperty(index + ".toolLanguageCode");
	    while(languageCode != null)
	    {
	        Locale locale = new java.util.Locale(languageCode);
	        if(locale != null)
	            toolLocales.add(locale);
	        
	        index++;
	        languageCode = CmsPropertyHandler.getProperty(index + ".toolLanguageCode");
	    }
	    
	    return toolLocales;
	}
	
    public String doExecute() throws Exception
    {
        return "success";
    }
               
}
