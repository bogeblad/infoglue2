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
package org.infoglue.deliver.portal;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.entity.PortletApplicationEntityList;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.infoglue.deliver.portal.om.PortletApplicationEntityImpl;
import org.infoglue.deliver.portal.om.PortletApplicationEntityListImpl;
import org.infoglue.deliver.portal.om.PortletEntityImpl;
import org.infoglue.deliver.portal.om.PreferenceImpl;

/**
 * @author joran
 *
 * @version $Revision: 1.1 $
 */
public class OmBuilderDomImpl implements OmBuilder {
	private static final Log LOG = LogFactory.getLog(OmBuilderDomImpl.class);
	
	private PortletApplicationEntityListImpl applications;
	private Document document = null;
	
    
	public OmBuilderDomImpl(InputStream is){
		SAXReader reader = new SAXReader();
		try {
			document = reader.read(is);
            applications = buildPortletApplicationEntityList();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.infoglue.cms.portal.OmBuilder#build(java.io.InputStream)
	 */
	public PortletApplicationEntityList getPortletApplicationEntityList() {
		return applications;
	}

    private PortletApplicationEntityListImpl buildPortletApplicationEntityList(){
        List applications = new Vector();
        List applicationNodes = document.selectNodes("applications/application");
        // For each application
        for(Iterator iter = applicationNodes.iterator();iter.hasNext();){
            Node appNode = (Node)iter.next();
            String warName = appNode.selectSingleNode("warName").getText();
            
            List entities = new Vector();
            PortletApplicationEntityImpl appEntity = new PortletApplicationEntityImpl(entities);
            Node logicNode = appNode.selectSingleNode("logicName");
            if(logicNode != null){
                appEntity.setLogicName(logicNode.getText());
            }
            
            appEntity.setId(warName);
            applications.add(appEntity);
            
            // For each portlet in application
            List entityNodes = appNode.selectNodes("entities/entity");
            for (Iterator iterator = entityNodes.iterator(); iterator.hasNext();) {
                Node entityNode = (Node) iterator.next();
                List preferences = new Vector();
                PortletEntityImpl entity = new PortletEntityImpl(appEntity, preferences);
                entities.add(entity);
                
                // For each preference for this portlet
                List prefNodes = entityNode.selectNodes("preferences/preference");
                for (Iterator prefIter = prefNodes.iterator(); prefIter.hasNext();) {
                    Node prefNode = (Node) prefIter.next();
                    String prefName = prefNode.selectSingleNode("name").getText();
                    String prefValue = prefNode.selectSingleNode("value").getText();
                    PreferenceImpl preference = new PreferenceImpl(prefName, prefValue);
                    preferences.add(preference);
                }
                
                String portletName = entityNode.selectSingleNode("portletName").getText();
                entity.setId(appEntity.getId() + "." + portletName);
            }
            
        }
        return new PortletApplicationEntityListImpl(applications);
    }
    
 }
