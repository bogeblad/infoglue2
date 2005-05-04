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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.CategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentCategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.EventController;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.structure.QualifyerVO;
import org.infoglue.cms.entities.workflow.EventVO;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.css.CSSHelper;
import org.infoglue.cms.util.dom.DOMBuilder;
import com.opensymphony.module.propertyset.*;

/**
 * This class/action returns the WYSIWYG configuration in full.
 *
 * @author Mattias Bogeblad
 */

public class WYSIWYGPropertiesAction extends InfoGlueAbstractAction
{
	private Integer repositoryId = null;

	private String WYSIWYGProperties = "";
	
    public String doExecute() throws Exception
    {
    	return "success";
    }

	/**
	 * This method gets the WYSIWYG Properties
	 */
	
	public String getWYSIWYGProperties() throws Exception
	{
	    System.out.println("Getting WYSIWYGProperties for principal...");
	    this.WYSIWYGProperties = getPrincipalPropertyValue("WYSIWYGConfig", false);
	    
	    if(this.WYSIWYGProperties == null && this.repositoryId != null)
	    {
		    System.out.println("Getting WYSIWYGProperties for repository...");
			Map args = new HashMap();
		    args.put("globalKey", "infoglue");
		    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
		    
		    byte[] WYSIWYGConfigBytes = ps.getData("repository_" + this.repositoryId + "_WYSIWYGConfig");
		    if(WYSIWYGConfigBytes != null)
		    {
		    	this.WYSIWYGProperties = new String(WYSIWYGConfigBytes);
		    }
	    }
	    
	    return this.WYSIWYGProperties;
	}
	
	
    public void setRepositoryId(Integer repositoryId)
    {
        this.repositoryId = repositoryId;
    }
}
