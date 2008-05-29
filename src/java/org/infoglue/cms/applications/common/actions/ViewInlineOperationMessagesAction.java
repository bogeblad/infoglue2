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

package org.infoglue.cms.applications.common.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.infoglue.cms.applications.databeans.LinkBean;

/**
 * This action is used as a jump-point from inline actions in deliver edit on sight back to other actions.
 *
 * @author Mattias Bogeblad
 * @author Johan Dahlgren
 */

public class ViewInlineOperationMessagesAction extends InfoGlueAbstractAction 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -739264056619967471L;
	//private final static Logger logger = Logger.getLogger(ViewInlineOperationMessagesAction.class.getName());
    
    private String message;
    private List<LinkBean> actionLinks;
    private boolean isAutomaticRedirect = false;
    
    /**
     * This method is the application entry-point. The parameters has been set through the setters
     * and now we just have to render the appropriate output. 
     */
         
    public String doExecute() throws Exception
    {    	
    	actionLinks 					= new ArrayList<LinkBean>();
    	message							= getRequest().getParameter("message");
		String actionLinkString			= getRequest().getParameter("actionLinks");	
		String automaticRedirectString	= getRequest().getParameter("isAutomaticRedirect");	
		
		if (automaticRedirectString != null)
		{
			
		}
		
		String[] elements;
		String[] values;

		if (actionLinkString != null)
		{
			elements = actionLinkString.split(";");
			
    		for (String element : elements)
    		{    			
    			values 				= element.split(",");    			    			
    			LinkBean myLinkBean = new LinkBean(values[0], values[1], values[2], values[3], values[4], values[5]);
    			actionLinks.add(myLinkBean);
    		}
		}
		
		return SUCCESS;
    }

    public String getMessage()
	{				
    	if (message == null)
    	{
    		message = "Undefined";
    	}
		return message;
	}
    
    public void setMessage(String message)
    {
    	this.message = message;
    }
    
	public List<LinkBean> getActionLinks()
	{				
		return actionLinks;
	}
	
	public LinkBean getFirstActionLink()
	{		
		return actionLinks.get(0);
	}
	
	public boolean getIsAutomaticRedirect()
	{
		return isAutomaticRedirect;
	}
	
	public void setIsAutomaticRedirect(boolean isAutomaticRedirect)
	{
		this.isAutomaticRedirect = isAutomaticRedirect;
	}
}
