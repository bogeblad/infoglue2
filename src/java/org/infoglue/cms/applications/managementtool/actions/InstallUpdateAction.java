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

import java.io.PrintWriter;

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.UpdateController;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;


/**
 * @author ss
 *
 * 
 */
public class InstallUpdateAction extends WebworkAbstractAction {

	UpdateController uc;
	private String updatePackageId;
	

	protected String doExecute() throws Exception {
		getResponse().setBufferSize(10);
		PrintWriter out = getResponse().getWriter();		
		CmsLogger.logInfo("Executing doExecute on RefreshUpdates..");
		String path = getRequest().getRealPath("/") + "up2date/";
		String url = CmsPropertyHandler.getProperty("up2dateUrl");	
	
		uc = new UpdateController(url, path);
		uc.runUpdatePackage(getUpdatePackageId(), out);
		
        return "success";
	}
	
	public InstallUpdateAction getThis()
	{
		return this;
	}

	
	/**
	 * @return
	 */
	public String getUpdatePackageId() {
		return updatePackageId;
	}

	/**
	 * @param string
	 */
	public void setUpdatePackageId(String string) {
		updatePackageId = string;
	}

}
