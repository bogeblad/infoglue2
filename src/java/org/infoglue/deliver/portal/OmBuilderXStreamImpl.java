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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletApplicationEntityList;
import org.infoglue.deliver.portal.om.PortletApplicationEntityImpl;
import org.infoglue.deliver.portal.om.PortletApplicationEntityListImpl;
import org.infoglue.deliver.portal.om.PortletEntityImpl;
import org.infoglue.deliver.portal.om.PreferenceImpl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * @author jand
 * @author joran
 *
 */
public class OmBuilderXStreamImpl implements OmBuilder {
    private static final Log log = LogFactory.getLog(OmBuilderXStreamImpl.class);

    private PortletApplicationEntityList applications;

    protected ServletContext servletContext;

    public OmBuilderXStreamImpl(InputStream is){
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("applications", ArrayList.class);
        xstream.alias("application", PortletApplicationEntityImpl.class);
        xstream.alias("entities", ArrayList.class);
        xstream.alias("entity", PortletEntityImpl.class);
        xstream.alias("preferences", ArrayList.class);
        xstream.alias("preference", PreferenceImpl.class, Preference.class);

        List apps = (List) xstream.fromXML(new InputStreamReader(is));
        this.applications = new PortletApplicationEntityListImpl(apps);

        // This is here to set back-references 
        for (Iterator iter = applications.iterator(); iter.hasNext();) {
            PortletApplicationEntity app = (PortletApplicationEntity) iter.next();

            for (Iterator ports = app.getPortletEntityList().iterator(); ports.hasNext();) {
                PortletEntityImpl port = (PortletEntityImpl) ports.next();
                port.setId(app.getId() + "." + port.getId());
                port.setPortletApplicationEntity(app);

            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService#getPortletApplicationEntityList()
     */
    public PortletApplicationEntityList getPortletApplicationEntityList() {
        return applications;
    }
}
