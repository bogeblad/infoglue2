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

import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.applications.common.actions.InfoGluePropertiesAbstractAction;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the action class for viewContentProperties.
 * The use-case lets the user see all extra-properties for a content
 * 
 * @author Mattias Bogeblad  
 */

public class ViewContentPropertiesAction extends InfoGluePropertiesAbstractAction
{ 
	private ContentVO contentVO 				= new ContentVO();
	private PropertySet propertySet				= null; 
	private List contentTypeDefinitionVOList 	= null;
	
	private String allowedContentTypeNames 		= null;
	private String defaultContentTypeName 		= null;	

	
    public ViewContentPropertiesAction()
    {
    }
        
    protected void initialize(Integer contentId) throws Exception
    {
        this.contentVO = ContentController.getContentController().getContentVOWithId(contentId);
        this.contentTypeDefinitionVOList = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOList(ContentTypeDefinitionVO.CONTENT);
            
        Map args = new HashMap();
	    args.put("globalKey", "infoglue");
	    PropertySet ps = PropertySetManager.getInstance("jdbc", args);

	    this.allowedContentTypeNames 	= ps.getString("content_" + this.getContentId() + "_allowedContentTypeNames");
	    this.defaultContentTypeName		= ps.getString("content_" + this.getContentId() + "_defaultContentTypeName");
	    CmsLogger.logInfo("allowedContentTypeNames:" + allowedContentTypeNames);
	    CmsLogger.logInfo("defaultContentTypeName:" + defaultContentTypeName);
    } 

    /**
     * The main method that fetches the Value-objects for this use-case
     */
    
    public String doExecute() throws Exception
    {
        this.initialize(getContentId());

        return "success";
    }
    
    /**
     * The main method that fetches the Value-objects for this use-case
     */
    
    public String doSave() throws Exception
    {
        String allowedContentTypeNames = "";
        String[] allowedContentTypeNameArray = getRequest().getParameterValues("allowedContentTypeName");
        CmsLogger.logInfo("allowedContentTypeNameArray:" + allowedContentTypeNameArray);
        for(int i=0; i<allowedContentTypeNameArray.length; i++)
        {
            allowedContentTypeNames += allowedContentTypeNameArray[i] + ","; 
        }
        
    	Map args = new HashMap();
	    args.put("globalKey", "infoglue");
	    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
	    
	    if(allowedContentTypeNames != null)
	        ps.setString("content_" + this.getContentId() + "_allowedContentTypeNames", allowedContentTypeNames);
	    if(defaultContentTypeName != null)
	        ps.setString("content_" + this.getContentId() + "_defaultContentTypeName", defaultContentTypeName);
	    
    	return "save";
    }

    /**
     * The main method that fetches the Value-objects for this use-case
     */
    
    public String doSaveAndExit() throws Exception
    {
        return "saveAndExit";
    }

    public java.lang.Integer getContentId()
    {
        return this.contentVO.getContentId();
    }
        
    public void setContentId(java.lang.Integer contentId) throws Exception
    {
        this.contentVO.setContentId(contentId);
    }

	public ContentVO getContentVO() 
	{
		return contentVO;
	}
    
    public List getContentTypeDefinitionVOList()
    {
        return contentTypeDefinitionVOList;
    }
    
    public String getAllowedContentTypeNames()
    {
        return allowedContentTypeNames;
    }

    public String getDefaultContentTypeName()
    {
        return defaultContentTypeName;
    }
    
    public void setDefaultContentTypeName(String defaultContentTypeName)
    {
        this.defaultContentTypeName = defaultContentTypeName;
    }
}
