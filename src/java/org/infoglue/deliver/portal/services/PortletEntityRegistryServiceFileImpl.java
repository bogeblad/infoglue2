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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jand
 *
 */
public class PortletEntityRegistryServiceFileImpl extends PortletEntityRegistryServiceAbs {
    private static final Log log = LogFactory.getLog(PortletEntityRegistryServiceFileImpl.class);

    private String filename = "WEB-INF/data/portletentityregistryIG.xml";

    /* (non-Javadoc)
     * @see org.infoglue.cms.portal.services.PortletEntityRegistryServiceAbs#getXML()
     */
    protected String getXML() {
        if (!new File(filename).isAbsolute())
            filename = super.servletContext.getRealPath(filename);

        File file = new File(filename);
        if (file.exists()) {
            StringBuffer str = new StringBuffer();
            try {
                FileReader is = new FileReader(file);
                BufferedReader bis = new BufferedReader(is);
                char[] buf = new char[256];
                for (int num = bis.read(buf); num >= 0; num = bis.read(buf)) {
                    str.append(buf, 0, num);
                }
                return str.toString();

            } catch (FileNotFoundException e) {
                log.error(filename + " NOT FOUND", e);
            } catch (IOException e) {
                log.error("Error reading config", e);
            }
        } else {
            log.error(filename + " NOT FOUND");
        }
        return null;
    }

}
