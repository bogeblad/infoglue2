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

package org.infoglue.cms.applications.managementtool.actions;

import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.applications.common.actions.InfoGluePropertiesAbstractAction;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the action class for viewRepositoryProperties.
 * The use-case lets the user see all extra-properties for a repository
 * 
 * @author Mattias Bogeblad  
 */

public class ViewRepositoryPropertiesAction extends InfoGluePropertiesAbstractAction
{ 
	private RepositoryVO repositoryVO 	= new RepositoryVO();
	private PropertySet propertySet		= null; 
	private String WYSIWYGConfig 		= null;
	
	
    public ViewRepositoryPropertiesAction()
    {
    }
        
    protected void initialize(Integer repositoryId) throws Exception
    {
        this.repositoryVO = RepositoryController.getController().getRepositoryVOWithId(repositoryId);

        Map args = new HashMap();
	    args.put("globalKey", "infoglue");
	    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
	    
	    byte[] WYSIWYGConfigBytes = ps.getData("repository_" + this.getRepositoryId() + "_WYSIWYGConfig");
	    if(WYSIWYGConfigBytes != null)
	    	this.WYSIWYGConfig = new String(WYSIWYGConfigBytes, "utf-8");
    } 

    /**
     * The main method that fetches the Value-objects for this use-case
     */
    
    public String doExecute() throws Exception
    {
        this.initialize(getRepositoryId());

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
	    
	    ps.setData("repository_" + this.getRepositoryId() + "_WYSIWYGConfig", WYSIWYGConfig.getBytes("utf-8"));
	    
    	return "save";
    }

    /**
     * The main method that fetches the Value-objects for this use-case
     */
    
    public String doSaveAndExit() throws Exception
    {
        return "saveAndExit";
    }

    public java.lang.Integer getRepositoryId()
    {
        return this.repositoryVO.getRepositoryId();
    }
        
    public void setRepositoryId(java.lang.Integer repositoryId) throws Exception
    {
        this.repositoryVO.setRepositoryId(repositoryId);
    }

	public RepositoryVO getRepositoryVO() 
	{
		return repositoryVO;
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
}
