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
	
	private String WYSIWYGConfig 				= null;
	private String defaultFolderContentTypeName = null;	

	
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
	    
	    byte[] WYSIWYGConfigBytes = ps.getData("content_" + this.getContentId() + "_WYSIWYGConfig");
	    if(WYSIWYGConfigBytes != null)
	    	this.WYSIWYGConfig = new String(WYSIWYGConfigBytes, "utf-8");

	    this.defaultFolderContentTypeName = ps.getString("content_" + this.getContentId() + "_defaultFolderContentTypeName");
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
    	Map args = new HashMap();
	    args.put("globalKey", "infoglue");
	    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
	    
	    ps.setData("content_" + this.getContentId() + "_WYSIWYGConfig", WYSIWYGConfig.getBytes("utf-8"));
	    ps.setString("content_" + this.getContentId() + "_defaultFolderContentTypeName", defaultFolderContentTypeName);
	    
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
	
	public String getWYSIWYGConfig() 
	{
		return WYSIWYGConfig;
	}
	
	public void setWYSIWYGConfig(String config) 
	{
		WYSIWYGConfig = config;
	}
	
	public PropertySet getPropertySet() 
	{
		return propertySet;
	}
	
    public String getDefaultFolderContentTypeName()
    {
        return defaultFolderContentTypeName;
    }
    
    public void setDefaultFolderContentTypeName(String defaultFolderContentTypeName)
    {
        this.defaultFolderContentTypeName = defaultFolderContentTypeName;
    }
    
    public List getContentTypeDefinitionVOList()
    {
        return contentTypeDefinitionVOList;
    }
}
