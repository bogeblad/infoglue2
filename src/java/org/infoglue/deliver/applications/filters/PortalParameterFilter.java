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
package org.infoglue.deliver.applications.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.cms.util.CmsPropertyHandler;

/**
 * @author jand
 *
 */
public class PortalParameterFilter implements Filter 
{
    private static final Log log = LogFactory.getLog(PortalParameterFilter.class);

    private boolean active = true;

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig arg0) throws ServletException 
    {
        String portalEnabled = CmsPropertyHandler.getEnablePortal();
        active = ((active) && (portalEnabled != null) && portalEnabled.equalsIgnoreCase("true"));

        log.info("PortalParameterFilter is active: " + active);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException 
    {
        if (active) 
        {
        	log.debug("wrapping " + ((HttpServletRequest) req).getRequestURI());
        	chain.doFilter(new PortalServletRequest((HttpServletRequest) req), resp);
        } 
        else 
        {
            chain.doFilter(req, resp);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() 
    {

    }

}
