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

package org.infoglue.deliver.applications.actions;

import org.exolab.castor.jdo.Database;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.deliver.controllers.kernel.impl.simple.BasicTemplateController;
import org.infoglue.deliver.controllers.kernel.impl.simple.ExtranetController;
import org.infoglue.deliver.controllers.kernel.impl.simple.IntegrationDeliveryController;
import org.infoglue.deliver.controllers.kernel.impl.simple.NodeDeliveryController;
import org.infoglue.deliver.controllers.kernel.impl.simple.TemplateController;

import java.lang.reflect.*;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * This is the action that can supply a caller with a lot of information about the delivery-engine.
 *
 * @author Mattias Bogeblad
 */

public class ViewApplicationSettingsAction extends ViewPageAction //WebworkAbstractAction 
{
	//Used to get a list of all available mthods 
	private List templateMethods = new ArrayList();

	//Used to get the navigation title of a page
	private String navigationTitle = null;
	private String sourceId = null;

	/**
	 * The constructor for this action - contains nothing right now.
	 */
    
    public ViewApplicationSettingsAction() 
    {
    }
    
    /**
     * This method is the application entry-point. The parameters has been set through the setters
     * and now we just have to render the appropriate output. 
     */
         
    public String doExecute() throws Exception
    {
		return NONE;
    }
    
	/**
	 * This command is used to get a list of all available methods on the templateController.
	 * This service is mostly used by the template-editor so it can keep up with changes easily.
	 */
	
	public String doGetTemplateLogicMethods() throws Exception
	{
		try 
		{
            Method m[] = BasicTemplateController.class.getDeclaredMethods();
            for (int i = 0; i < m.length; i++)
            {
            	Method method = m[i];
	            if(!method.getName().startsWith("set"))
	            {
		            StringBuffer sb = new StringBuffer();
		            sb.append(method.getName());
		            sb.append("(");
		            Class[] parameters = method.getParameterTypes();
		            for (int j = 0; j < parameters.length; j++)
	               	{
	   		            if(j != 0)
	   		        		sb.append(", ");
	   		        		
	   		        	sb.append(parameters[j].getName());	
	   		        }
		            sb.append(")");
	               	
	               	String methodString = sb.toString();
	               	int position = 0;
	               	while(position < this.templateMethods.size())
	            	{
	            		String currentString = (String)this.templateMethods.get(position);
		            	if(currentString.compareToIgnoreCase(methodString) > 0)
		            	{
		            		break;
		            	}
		            	position++;
	            	}
	            	
	            	this.templateMethods.add(position, methodString);		
            
	            }
            }
        }
        catch (Throwable e) 
        {
            System.err.println(e);
        }

		return "templateMethods";
	}
	
	/**
	 * This command is used to get the navigationtitle for a sitenode in a certain language.
	 */
	
	public String doGetPageNavigationTitle() throws Exception
	{
    	Database db = CastorDatabaseService.getDatabase();
		
		beginTransaction(db);

		try
		{
		    Principal principal = (Principal)this.getHttpSession().getAttribute("infogluePrincipal");
			if(principal == null)
			{
				try
				{
				    Map arguments = new HashMap();
				    arguments.put("j_username", "anonymous");
				    arguments.put("j_password", "anonymous");
				    
				    principal = ExtranetController.getController().getAuthenticatedPrincipal(arguments);
				}
				catch(Exception e) 
				{
				    throw new SystemException("There was no anonymous user found in the system. There must be - add the user anonymous/anonymous and try again.", e);
				}
			}
			
			this.nodeDeliveryController		   		= NodeDeliveryController.getNodeDeliveryController(getSiteNodeId(), getLanguageId(), getContentId());
			this.integrationDeliveryController 		= IntegrationDeliveryController.getIntegrationDeliveryController(getSiteNodeId(), getLanguageId(), getContentId());
			TemplateController templateController 	= getTemplateController(db, getSiteNodeId(), getLanguageId(), getContentId(), getRequest(), (InfoGluePrincipal)principal);
			this.navigationTitle = templateController.getPageNavTitle(this.getSiteNodeId());

	        closeTransaction(db);
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred so we should not complete the transaction:" + e, e);
			rollbackTransaction(db);
			throw new SystemException(e.getMessage());
		}

		return "navigationTitle";
	}
		
	public List getTemplateMethods()
	{
		return templateMethods;
	}

	public String getNavigationTitle()
	{
		return navigationTitle;
	}

	public String getSourceId()
	{
		return this.sourceId;
	}

	public void setSourceId(String sourceId)
	{
		this.sourceId = sourceId;
	}

}
