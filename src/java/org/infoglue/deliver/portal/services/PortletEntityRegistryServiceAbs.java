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
package org.infoglue.deliver.portal.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletApplicationEntityList;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService;
import org.apache.pluto.portalImpl.util.Properties;
import org.infoglue.deliver.portal.om.PortletApplicationEntityImpl;
import org.infoglue.deliver.portal.om.PortletApplicationEntityListImpl;
import org.infoglue.deliver.portal.om.PortletEntityImpl;
import org.infoglue.deliver.portal.om.PortletWindowImpl;
import org.infoglue.deliver.portal.om.PreferenceImpl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author jand
 *
 */
public abstract class PortletEntityRegistryServiceAbs extends PortletEntityRegistryService {
    private static final Log log = LogFactory.getLog(PortletEntityRegistryServiceAbs.class);

    private PortletApplicationEntityList applications;

    protected ServletContext servletContext;

    /* (non-Javadoc)
     * @see org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService#getPortletApplicationEntityList()
     */
    public PortletApplicationEntityList getPortletApplicationEntityList() {
        return applications;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService#getPortletEntity(org.apache.pluto.om.common.ObjectID)
     */
    public PortletEntity getPortletEntity(ObjectID id) {
        String oid = id.toString();
        int dot = oid.indexOf(".");
        if (dot < 0) {
            log.warn("ID does not contain '.' to separate application- and portlet-id: " + id);
            return null;
        }

        ObjectID appID =
            org.apache.pluto.portalImpl.util.ObjectID.createFromString(oid.substring(0, dot));

        PortletApplicationEntity appEntity = applications.get(appID);
        if (appEntity == null) {
            log.warn("Application not found: " + appID);
            return null;
        }
        PortletEntity portletEntity = appEntity.getPortletEntityList().get(id);
        if (portletEntity == null) {
            log.warn("Portlet not found: " + id);
        }

        return portletEntity;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService#store()
     */
    public void store() throws IOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService#load()
     */
    public void load() throws IOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.pluto.portalImpl.services.portletentityregistry.PortletEntityRegistryService#refresh(org.apache.pluto.om.entity.PortletEntity)
     */
    public void refresh(PortletEntity arg0) {
        // TODO Auto-generated method stub

    }

    protected abstract String getXML();

    /*
     * Initialize registry. called before postInit()
     */
    protected void init(ServletContext context, Properties aProperties) throws Exception {
        log.info("********************************* Initializing...");
        this.servletContext = context;

        try {
            XStream xstream = new XStream(new DomDriver());
            xstream.alias("applications", ArrayList.class);
            xstream.alias("application", PortletApplicationEntityImpl.class);
            xstream.alias("entities", ArrayList.class);
            xstream.alias("entity", PortletEntityImpl.class);
            //xstream.alias("id", ObjectID.class, ObjectIDImpl.class);
            xstream.alias("preferences", ArrayList.class);
            xstream.alias("preference", PreferenceImpl.class, Preference.class);
            xstream.alias("windows", ArrayList.class);
            xstream.alias("window", PortletWindowImpl.class);
            //xstream.alias("description", DescriptionImpl.class, Description.class);

            log.debug("Reading xml...");
            String xml = getXML();
            if (xml != null) {
                log.debug("Parsing xml...");
                List apps = (List) xstream.fromXML(xml);
                this.applications = new PortletApplicationEntityListImpl(apps);
                log.debug("Parsing OK!");
            } else {
                log.error("No XML provided");
            }

            // This is here to set back-references 
            for (Iterator apps = applications.iterator(); apps.hasNext();) {
                PortletApplicationEntity app = (PortletApplicationEntity) apps.next();
                log.debug("App: " + app.getId());

                for (Iterator ports = app.getPortletEntityList().iterator(); ports.hasNext();) {
                    PortletEntityImpl port = (PortletEntityImpl) ports.next();
                    port.setId(app.getId() + "." + port.getId());
                    port.setPortletApplicationEntity(app);
                    log.debug("\tPortlet: " + port.getId());

                    for (Iterator wins = port.getPortletWindowList().iterator(); wins.hasNext();) {
                        PortletWindowImpl win = (PortletWindowImpl) wins.next();
                        win.setPortletEntity(port);
                        win.setLogicName(app.getId() + "." + win.getLogicName());
                        log.debug("\t\tWindow: " + win.getId() + " [" + win.getLogicName() + "]");
                    }
                }
            }

        } catch (Throwable e) {
            log.error("Failed to initialize: ", e);
            throw new Exception(e);
        }
        log.info("********************************* Initializing done!");
    }

    /*
     * Initialize registry.
    
    protected void postInit() throws Exception {
        log.info("********************************* Post-initializing...");
        log.info("********************************* Post-initializing done!");
    }
         */
}
