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

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.util.CmsLogger;

import org.infoglue.cms.entities.content.impl.simple.ContentImpl;
import org.infoglue.cms.entities.content.impl.simple.SmallContentImpl;
import org.infoglue.cms.entities.management.impl.simple.ContentTypeDefinitionImpl;
import org.infoglue.cms.entities.management.impl.simple.InterceptionPointImpl;
import org.infoglue.cms.entities.management.impl.simple.InterceptorImpl;
import org.infoglue.cms.entities.management.impl.simple.RepositoryImpl;
import org.infoglue.deliver.util.CacheController;


/**
 * This is the action that takes care of all incoming update-calls. This action is
 * called by either the system or by replication-program and the class the distibutes the 
 * update-call to all the listeners which have registered earlier.
 *
 * @author Mattias Bogeblad
 */

public class UpdateCacheAction extends WebworkAbstractAction 
{
	private String className = null;
	private String objectId = null;
	private String objectName = null;
	private String typeId = null;

	private String repositoryName = null;
	private Integer languageId    = null;
	private Integer siteNodeId    = null;
	
	private static boolean cachingInProgress = false;
	
	/**
	 * The constructor for this action - contains nothing right now.
	 */
    
    public UpdateCacheAction() 
    {
	
    }
    
    
    /**
     * This method is the application entry-point. The parameters has been set through the setters
     * and now we just have to render the appropriate output. 
     */
         
    public String doExecute() throws Exception
    {
		try
		{  
			CacheController.clearCaches(className);
			
			CmsLogger.logInfo("Updating className with id:" + className + ":" + objectId);
			
			if(className != null)
			{
				Class[] types = {Class.forName(className)};
				Object[] ids = {new Integer(objectId)};
				
				CacheController.clearCache(types, ids);
									 
				//If it's an contentVersion we should delete all images it might have generated from attributes.
				/*
				if(Class.forName(className).getName().equals(ContentVersionImpl.class.getName()))
				{
					CmsLogger.logInfo("We should delete all images with contentVersionId " + objectId);
					DigitalAssetDeliveryController.getDigitalAssetDeliveryController().deleteContentVersionAssets(new Integer(objectId));
				}		
				*/
				
				//If it's an ContentImpl we update SmallContentImpl as well.
				if(Class.forName(className).getName().equals(ContentImpl.class.getName()))
				{
					CmsLogger.logInfo("We update SmallContentImpl as well");
					CacheController.clearCache(new Class[]{SmallContentImpl.class}, new Object[]{new Integer(objectId)});
				}
				
				if(Class.forName(className).getClass().getName().equals(RepositoryImpl.class.getName()))
				{
					CacheController.clearCache("repositoryCache");
				}
				else if(Class.forName(className).getClass().getName().equals(InterceptionPointImpl.class.getName()))
				{
					CacheController.clearCache("interceptionPointCache");
					CacheController.clearCache("interceptorsCache");
				}
				else if(Class.forName(className).getClass().getName().equals(InterceptorImpl.class.getName()))
				{
					CacheController.clearCache("interceptionPointCache");
					CacheController.clearCache("interceptorsCache");
				}
				else if(Class.forName(className).getClass().getName().equals(ContentTypeDefinitionImpl.class.getName()))
				{
					CacheController.clearCache("contentTypeDefinitionCache");
				}
				else if(Class.forName(className).getClass().getName().equals(ContentImpl.class.getName()))
				{
					CacheController.clearCache("childContentCache");
				}
				/*
				else if(Class.forName(className).getClass().getName().equals(ContentImpl.class.getName()))
				{
					CacheController.clearCache("childContentCache");
				}
				*/
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere(e.getMessage(), e);
		}
                
        return "success";
    }
    

	/**
	 * This method is for letting users update cache manually. 
	 */
         
	public String doInput() throws Exception
	{
		return "input";
	}
    

	/**
	 * Setters and getters for all things sent to the page in the request
	 */
	        
    public void setClassName(String className)
    {
	    this.className = className;
    }
        
    public void setObjectId(String objectId)
    {
	    this.objectId = objectId;
    }

    public void setObjectName(String objectName)
    {
	    this.objectName = objectName;
    }

    public void setTypeId(String typeId)
    {
	    this.typeId = typeId;
    }
    
}
