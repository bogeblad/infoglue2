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

package org.infoglue.cms.controllers.kernel.impl.simple;

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.Language;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.management.RepositoryVO;
import org.infoglue.cms.entities.management.Repository;
import org.infoglue.cms.entities.management.RepositoryLanguage;
import org.infoglue.cms.entities.management.impl.simple.RepositoryImpl;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;

import org.infoglue.cms.exception.*;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.*;

import org.infoglue.deliver.util.CacheController;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServerNodeController
{
    private String useUpdateSecurity = CmsPropertyHandler.getProperty("useUpdateSecurity");
    
	/**
	 * Factory method
	 */

	public static ServerNodeController getController()
	{
		return new ServerNodeController();
	}
	
	public void initialize()
	{
	}
	
	public List getAllowedAdminIPList()
	{
	    Map args = new HashMap();
	    args.put("globalKey", "infoglue");
	    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
	    
	    String allowedAdminIP = ps.getString("allowedAdminIP");
	    if(allowedAdminIP != null)
	        return Arrays.asList(allowedAdminIP.split(","));
	    else
	        return new ArrayList();
	}
	
	/**
	 * This method return if the caller has access to the semi admin services.
	 * @param request
	 * @return
	 */

	public boolean getIsIPAllowed(HttpServletRequest request)
	{
	    boolean isIPAllowed = false;

	    //System.out.println("useUpdateSecurity:" + useUpdateSecurity);
	    if(useUpdateSecurity != null && useUpdateSecurity.equals("true"))
	    {
		    String remoteIP = request.getRemoteAddr();
		    //System.out.println("remoteIP:" + remoteIP);
		    if(remoteIP.equals("127.0.0.1"))
		    {
		        isIPAllowed = true;
		    }
		    else
		    {
		        List allowedAdminIPList = ServerNodeController.getController().getAllowedAdminIPList();
		        Iterator i = allowedAdminIPList.iterator();
		        while(i.hasNext())
		        {
		            String allowedIP = (String)i.next();
		            if(!allowedIP.trim().equals(""))
		            {
			            //System.out.println("allowedIP:" + allowedIP);
			            int index = allowedIP.indexOf(".*");
			            if(index > -1)
			                allowedIP = allowedIP.substring(0, index);
						//System.out.println("allowedIP:" + allowedIP);
				            
			            if(remoteIP.startsWith(allowedIP))
			            {
			                isIPAllowed = true;
			                break;
			            }
		            }
		        }
		    }
	    }
	    else
	        isIPAllowed = true;
	    
	    return isIPAllowed;
	}
	
	public String getAllowedAdminIP()
	{
	    Map args = new HashMap();
	    args.put("globalKey", "infoglue");
	    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
	    
	    String allowedAdminIP = ps.getString("allowedAdminIP");

	    return allowedAdminIP;
	}

	public void setAllowedAdminIP(String allowedAdminIP)
	{
	    Map args = new HashMap();
	    args.put("globalKey", "infoglue");
	    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
	    
	    ps.setString("allowedAdminIP", allowedAdminIP);
	}

}
 
