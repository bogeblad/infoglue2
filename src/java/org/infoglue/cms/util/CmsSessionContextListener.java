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

package org.infoglue.cms.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.infoglue.cms.applications.databeans.SessionInfoBean;
import org.infoglue.cms.security.InfoGlueAuthenticationFilter;
import org.infoglue.cms.security.InfoGluePrincipal;

/**
 * This class keeps track of all sessions created / removed so we can see 
 * how many users a site / cms has right now.
 * 
 * @author mattias
 */

public class CmsSessionContextListener implements HttpSessionListener 
{	
	public static final Map sessions = Collections.synchronizedMap(new HashMap());
	
	private	static int activeSessions =	0;
	
	public void	sessionCreated(HttpSessionEvent	se)	
	{
		//System.out.println("Session created..");
	    activeSessions++;
	    synchronized (sessions)
	    {
	    	sessions.put(se.getSession().getId(), se.getSession());
	    }
	}
	
	public void	sessionDestroyed(HttpSessionEvent se) 
	{
		//System.out.println("Session destroyed..");
	    if(activeSessions >	0)
			activeSessions--;
	    
	    synchronized(sessions)
	    {
	    	sessions.remove(se.getSession().getId());
	    }
	}
	
	public static int getActiveSessions() 
	{
		return activeSessions;
	}

	static public List getSessionInfoBeanList()
	{
		List stiList = new ArrayList();

		//System.out.println("Sessions:" + sessions.size());
		synchronized(sessions)
		{
			Iterator iter = sessions.keySet().iterator();
			while (iter.hasNext())
			{
				String s = (String) iter.next();
				HttpSession sess = (HttpSession) sessions.get(s);
			
				SessionInfoBean sib = new SessionInfoBean();
				
				InfoGluePrincipal principal = (InfoGluePrincipal)sess.getAttribute(InfoGlueAuthenticationFilter.INFOGLUE_FILTER_USER);
				if(principal == null)
					principal = (InfoGluePrincipal)sess.getAttribute("infogluePrincipal");
				
				if(principal != null)
				{
					sib.setPrincipal(principal);
					sib.setLastAccessedDate(new Date(sess.getLastAccessedTime()));
					
					stiList.add(sib);
				}
			}
		}
	
		return stiList;
	}

}