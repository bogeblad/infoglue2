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

import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.applications.common.actions.InfoGluePropertiesAbstractAction;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the action class for viewRepositoryProperties.
 * The use-case lets the user see all extra-properties for a repository
 * 
 * @author Mattias Bogeblad  
 */

public class UpdateMySettingsAction extends InfoGlueAbstractAction
{ 
	private PropertySet propertySet				= null; 
	
	private String languageCode 				= null;

	    
    /**
     * The main method that fetches the Value-objects for this use-case
     */
    
    public String doExecute() throws Exception
    {
        Map args = new HashMap();
	    args.put("globalKey", "infoglue");
	    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
	    
	    ps.setString("principal_" + this.getInfoGluePrincipal().getName() + "_languageCode", languageCode);

        return "success";
    }
    
    /**
     * The main method that fetches the Value-objects for this use-case
     */
    /*
    public String doSave() throws Exception
    {
    	Map args = new HashMap();
	    args.put("globalKey", "infoglue");
	    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
	    
	    ps.setData("repository_" + this.getRepositoryId() + "_WYSIWYGConfig", WYSIWYGConfig.getBytes("utf-8"));
	    ps.setData("repository_" + this.getRepositoryId() + "_StylesXML", stylesXML.getBytes("utf-8"));
	    ps.setString("repository_" + this.getRepositoryId() + "_defaultFolderContentTypeName", defaultFolderContentTypeName);
	    ps.setString("repository_" + this.getRepositoryId() + "_defaultTemplateRepository", defaultTemplateRepository);
	    ps.setString("repository_" + this.getRepositoryId() + "_parentRepository", parentRepository);
	    
	    //TODO - hack to get the caches to be updated when properties are affected..
	    RepositoryVO repositoryVO = RepositoryController.getController().getFirstRepositoryVO();
	    repositoryVO.setDescription(repositoryVO.getDescription() + ".");
	    RepositoryController.getController().update(repositoryVO);
	    
    	return "save";
    }
    */


    public String getLanguageCode()
    {
        return languageCode;
    }
    
    public void setLanguageCode(String languageCode)
    {
        this.languageCode = languageCode;
    }
}
