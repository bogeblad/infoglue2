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

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;

import java.util.List;


/**
 * This action handles all interaction a user wants to add/change/remove things in a components properties.
 */ 

public class ViewComponentPropertiesEditorAction extends InfoGlueAbstractAction
{
    private Integer contentVersionId;
    private String attributeName;
    private String propertiesXML;
    private List componentPropertyDefinitions;
    private List contentTypeDefinitions;
    
    private void initialize() throws Exception
    {
        String componentPropertiesXML = ContentVersionController.getContentVersionController().getAttributeValue(contentVersionId, attributeName, false);
        System.out.println("componentPropertiesXML:" + componentPropertiesXML);
        this.componentPropertyDefinitions = ComponentPropertyDefinitionController.getController().parseComponentPropertyDefinitions(componentPropertiesXML);        
        this.contentTypeDefinitions = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOList(ContentTypeDefinitionVO.CONTENT);
    }
    
    public String doExecute() throws Exception
    {
        initialize();
        
        return SUCCESS;
    }

    public String doUpdate() throws Exception
    {
        ContentVersionController.getContentVersionController().updateAttributeValue(this.contentVersionId, this.attributeName, this.propertiesXML, this.getInfoGluePrincipal());
        
        initialize();
        
        return SUCCESS;
    }


    public String getAttributeName()
    {
        return attributeName;
    }
    
    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
    }
    
    public Integer getContentVersionId()
    {
        return contentVersionId;
    }
    
    public void setContentVersionId(Integer contentVersionId)
    {
        this.contentVersionId = contentVersionId;
    }
    
    public List getComponentPropertyDefinitions()
    {
        return componentPropertyDefinitions;
    }
    
    public void setPropertiesXML(String propertiesXML)
    {
        this.propertiesXML = propertiesXML;
    }
    
    public List getContentTypeDefinitions()
    {
        return contentTypeDefinitions;
    }
}
