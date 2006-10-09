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

package org.infoglue.cms.controllers.kernel.impl.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.Language;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.sorters.ContentComparator;
import org.infoglue.deliver.util.CacheController;

/**
 * This class handles all access to components.
 * 
 * @author Mattias Bogeblad
 */

public class ComponentController extends BaseController
{
    private final static Logger logger = Logger.getLogger(ComponentController.class.getName());

    /**
	 * Factory method
	 */

	public static ComponentController getController()
	{
		return new ComponentController();
	}

	/**
	 * This method returns a sorted list of components.
	 * @param sortAttribute
	 * @return
	 * @throws SystemException
	 * @throws Bug
	 */
	
	public List getComponentVOList(String sortAttribute, String direction, String[] allowedComponentNames) throws SystemException, Bug, Exception
	{
		List componentVOList = null;
		
		Database db = CastorDatabaseService.getDatabase();
		try
		{
			beginTransaction(db);
			
			componentVOList = getComponentVOList(sortAttribute, direction, allowedComponentNames, db);
			    
			commitTransaction(db);
		}
		catch ( Exception e )
		{
		    e.printStackTrace();
			rollbackTransaction(db);
			throw new SystemException("An error occurred when we tried to fetch a list of users in this group. Reason:" + e.getMessage(), e);			
		}		
		
		return componentVOList;
	}
	
	/**
	 * This method returns a sorted list of components within a transaction.
	 * @param sortAttribute
	 * @return
	 * @throws SystemException
	 * @throws Bug
	 */
	
	private static List cachedComponents = null;
	
	public List getComponentVOList(String sortAttribute, String direction, String[] allowedComponentNames, Database db) throws SystemException, Bug, Exception
	{
	    String allowedComponentNamesString = "";
	    if(allowedComponentNames != null)
	    {
	        for(int i=0; i<allowedComponentNames.length; i++)
	            allowedComponentNamesString = allowedComponentNames[i] + ":";
	    }
	    
	    String componentsKey = "components_" + sortAttribute + "_" + direction + "_" + allowedComponentNamesString;
	    List components = (List)CacheController.getCachedObject("componentContentsCache", componentsKey);
		if(components != null)
		{
			logger.info("There was cached components:" + components.size());
		}
		else
		{
		    components = getComponents(allowedComponentNames);
			Iterator componentsIterator = components.iterator();
			while(componentsIterator.hasNext())
			{
			    ContentVO contentVO = (ContentVO)componentsIterator.next();
	
			    Language masterLanguage = LanguageController.getController().getMasterLanguage(db, contentVO.getRepositoryId());
				ContentVersion contentVersion = ContentVersionController.getContentVersionController().getLatestActiveContentVersion(contentVO.getId(), masterLanguage.getId(), db);
				
				String groupName = "Unknown";
				
				if(contentVersion != null)
				{
				    groupName = ContentVersionController.getContentVersionController().getAttributeValue(contentVersion.getValueObject(), "GroupName", false);
				}
	
				contentVO.getExtraProperties().put("GroupName", groupName);
			}
			
			CacheController.cacheObject("componentContentsCache", componentsKey, components);
		}
		
		ContentComparator comparator = new ContentComparator(sortAttribute, direction, null);
		Collections.sort(components, comparator);
		
		return components;
	}
	
	
	/**
	 * This method returns the contents that are of contentTypeDefinition "HTMLTemplate"
	 */
	
	public List getComponents(String[] allowedComponentNames) throws Exception
	{
		HashMap arguments = new HashMap();
		arguments.put("method", "selectListOnContentTypeName");
		
		List argumentList = new ArrayList();
		HashMap argument = new HashMap();
		argument.put("contentTypeDefinitionName", "HTMLTemplate");
		argumentList.add(argument);
		arguments.put("arguments", argumentList);
		
		List results = ContentController.getContentController().getContentVOList(arguments);
		
		if(allowedComponentNames != null && allowedComponentNames.length > 0)
		{
		    Iterator resultsIterator = results.iterator();
		    while(resultsIterator.hasNext())
		    {
		        ContentVO contentVO = (ContentVO)resultsIterator.next();
		        boolean isAllowed = false;
		        for(int i=0; i<allowedComponentNames.length; i++)
		        {
		            if(contentVO.getName().equals(allowedComponentNames[i]))
		                isAllowed = true;
		        }
		        
		        if(!isAllowed)
		            resultsIterator.remove();
		    }
		}
		
		return results;	
	}
	
    /* (non-Javadoc)
     * @see org.infoglue.cms.controllers.kernel.impl.simple.BaseController#getNewVO()
     */
    public BaseEntityVO getNewVO()
    {
        return null;
    }
	
	
}
