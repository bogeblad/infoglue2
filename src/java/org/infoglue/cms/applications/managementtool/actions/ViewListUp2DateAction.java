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

package org.infoglue.cms.applications.managementtool.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.UpdateController;
import org.infoglue.cms.util.CmsPropertyHandler;


/**
 * @author ss
 *
 */
public class ViewListUp2DateAction extends InfoGlueAbstractAction {

    private final static Logger logger = Logger.getLogger(ViewListUp2DateAction.class.getName());

	private static final long serialVersionUID = 1L;

	UpdateController uc;
	private String currentUpdateServer = "";
	

	protected String doExecute() throws Exception {
		String path = getRequest().getRealPath("/") + "up2date/";
		String url = CmsPropertyHandler.getUp2dateUrl();	

		try 
		{
			URL u = new URL(url);
			setCurrentUpdateServer(u.getHost());
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		
		uc = new UpdateController(url, path);
		
	    logger.info("Executing doExecute on ViewListUp2Date..");
	    logger.info("Finished executing doExecute on ViewListUp2Date..");
        return "success";
	}
	
	public Vector getInstalledUpdates()
	{
		Vector ret = uc.getInstalledUpdates();
		if (ret == null) ret = new Vector();
		return ret;
	}

	public List getAvailableUpdates()
	{
		Vector ret = uc.getAvailableUpdates();
		if (ret == null) ret = new Vector();
		return ret;
	}

	public Date getLatestRefresh()
	{
		return uc.getLatestRefresh();
	}

	
	/**
	 * @return
	 */
	public String getCurrentUpdateServer() {
		return currentUpdateServer;
	}

	/**
	 * @param string
	 */
	public void setCurrentUpdateServer(String string) {
		currentUpdateServer = string;
	}

}
