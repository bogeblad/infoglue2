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

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.UpdateController;
import org.infoglue.cms.util.CmsPropertyHandler;


/**
 * @author ss
 *
 * 
 */
public class UnInstallUpdateAction extends InfoGlueAbstractAction 
{

	UpdateController uc;
	private String updatePackageId;
	

	protected String doExecute() throws Exception {
		getResponse().setBufferSize(10);
		PrintWriter out = getResponse().getWriter();
		out.write("<!-- INFOGLUE AUTO-UPDATE SYSTEM -->\n");
		for(int i = 0;i<1200;i++)
		{
			out.write("                                                                                ");
		}
		out.write("\n");
		
		out.write("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"css/cms.css\" /></head><body class=\"managementtooledit\"><font color=\"#888888\">");
		out.flush();
		
		getLogger().info("Executing doExecute on RefreshUpdates..");
		String path = getRequest().getRealPath("/") + "up2date/";
		String url = CmsPropertyHandler.getUp2dateUrl();	
	
		uc = new UpdateController(url, path);
		uc.unInstallPackage(getUpdatePackageId(), out);

		out.write("<script language='javascript'>\n");
		out.write("alert('Infoglue Up2Date\\n\\nUninstall complete. ');\n");
		// writer.write("parent.location.href = 'ViewListUp2Date.action?title=InfoGlue Up2Date';\n");
		
		out.write("</script>");
		
		out.write("</font></body></html>");
		out.flush();

        return null;
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
