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

package org.infoglue.deliver.applications.databeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infoglue.cms.applications.common.Session;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.deliver.applications.actions.InfoGlueComponent;

/**
 * @author Mattias Bogeblad
 *
 * This class is used to store the context of a page and get and set information that is central to it.
 * TODO - write more
 */

public class ComponentDeliveryContext implements UsageListener
{
	private String componentKey = null;
	private InfoGlueComponent infoGlueComponent;
	    
	//This section has control over what contents and sitenodes are used where so the pagecache can be selectively updated.
	private List usedContents = new ArrayList();
	private List usedContentVersions = new ArrayList();
	private List usedSiteNodes = new ArrayList();
	private List usedSiteNodeVersions = new ArrayList();
	
	private DeliveryContext deliveryContext;
	
	public static ComponentDeliveryContext getComponentDeliveryContext(DeliveryContext deliveryContext, InfoGlueComponent infoGlueComponent)
	{
		return new ComponentDeliveryContext(deliveryContext, infoGlueComponent);
	}
	
	private ComponentDeliveryContext(DeliveryContext deliveryContext, InfoGlueComponent infoGlueComponent)
	{
	    this.deliveryContext = deliveryContext;
	    this.infoGlueComponent = infoGlueComponent;
 		this.componentKey = this.deliveryContext.getPageKey() + "_" + infoGlueComponent.getId();
	}
	
    public void addUsedContent(String usedContent)
    {
        this.usedContents.add(usedContent);
    }

    public void addUsedSiteNode(String usedSiteNode)
    {
        this.usedSiteNodes.add(usedSiteNode);
    }

    public void addUsedContentVersion(String usedContentVersion)
    {
        this.usedContentVersions.add(usedContentVersion);
    }

    public void addUsedSiteNodeVersion(String usedSiteNodeVersion)
    {
        this.usedSiteNodeVersions.add(usedSiteNodeVersion);
    }
    
    public String[] getAllUsedEntities()
    {
        List list = new ArrayList();
        list.addAll(this.usedContents);
        list.addAll(this.usedContentVersions);
        list.addAll(this.usedSiteNodes);
        list.addAll(this.usedSiteNodeVersions);
        Object[] array = list.toArray();
        String[] groups = new String[array.length];
        for(int i=0; i<array.length; i++)
            groups[i] = array[i].toString();
        
        return groups;
    }
    
    public String getComponentKey()
    {
        return componentKey;
    }
}
