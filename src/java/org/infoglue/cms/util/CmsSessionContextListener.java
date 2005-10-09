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

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;

/**
 * This class keeps track of all sessions created / removed so we can see 
 * how many users a site / cms has right now.
 * 
 * @author mattias
 */

public class CmsSessionContextListener implements HttpSessionListener 
{
    private	static int activeSessions =	0;
	
	public void	sessionCreated(HttpSessionEvent	se)	
	{
	    activeSessions++;
	}
	
	public void	sessionDestroyed(HttpSessionEvent se) 
	{
	    if(activeSessions >	0)
			activeSessions--;
	}
	
	public static int getActiveSessions() 
	{
		return activeSessions;
	}
}