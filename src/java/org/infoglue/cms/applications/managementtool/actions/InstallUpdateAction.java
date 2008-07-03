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

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.UpdateController;
import org.infoglue.cms.util.CmsPropertyHandler;


/**
 * @author ss
 *
 * 
 */
public class InstallUpdateAction extends InfoGlueAbstractAction 
{
    private final static Logger logger = Logger.getLogger(InstallUpdateAction.class.getName());

	UpdateController uc;
	private String updatePackageId;
	

	protected String doExecute() throws Exception {
	    
	    OutputStream os = getResponse().getOutputStream();
	    OutputStreamWriter writer = new OutputStreamWriter(os);
	    getResponse().setBufferSize(1);
	    getResponse().setContentType("text/html");
		// getResponse().setBufferSize(10);
		// PrintWriter out = getResponse().getWriter();		
		logger.info("Executing doExecute on RefreshUpdates..");
		String path = getRequest().getRealPath("/") + "up2date/";
		
		logger.info("UP2DATE: PATH: " + path);
		String url = CmsPropertyHandler.getUp2dateUrl();	
		
		uc = new UpdateController(url, path);
		
		writer.write("<!-- INFOGLUE AUTO-UPDATE SYSTEM -->\n");
		for(int i = 0;i<1200;i++)
		{
			writer.write("                                                                                ");
		}
		writer.write("\n");
		
		writer.write("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"css/cms.css\" /></head><body class=\"managementtooledit\"><font color=\"#888888\">");
		writer.flush();
		uc.runUpdatePackage(getUpdatePackageId(), writer);
		// writer.write("</pre>");
		
		writer.write("<script language='javascript'>\n");
		writer.write("alert('Infoglue Up2Date\\n\\nThis installation has finished. ');\n");
		// writer.write("parent.location.href = 'ViewListUp2Date.action?title=InfoGlue Up2Date';\n");
		
		writer.write("</script>");
		
		writer.write("</font></body></html>");
		writer.flush();
		
		os.flush();
		
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
