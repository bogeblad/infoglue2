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

package org.infoglue.cms.workflow.taglib;

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.infoglue.cms.controllers.kernel.impl.simple.WorkflowController;
import org.infoglue.cms.security.InfoGluePrincipal;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class WorkflowHelper {

	/**
	 * 
	 */
	private static final String UTF8_ENCODING = "utf-8";

	public static final String ACTION_ID_PARAMETER   = "actionId";
	public static final String WORKFLOW_ID_PARAMETER = "workflowId";

	/**
	 * 
	 */
	private WorkflowHelper() {}

	/**
	 * 
	 */
	public static String getWorkflowID(ServletRequest request) {
		return request.getParameter(WORKFLOW_ID_PARAMETER);
	}

	/**
	 * 
	 */
	public static String getActionID(ServletRequest request) {
		return request.getParameter(ACTION_ID_PARAMETER);
	}

	/**
	 * 
	 */
	public static InfoGluePrincipal getPrincipal(HttpSession session) {
		return (InfoGluePrincipal) session.getAttribute("org.infoglue.cms.security.user");
	}

	/**
	 * 
	 */
	public static PropertySet getPropertySet(HttpSession session, ServletRequest request) {
		return WorkflowController.getController().getPropertySet(getPrincipal(session), Long.valueOf(getWorkflowID(request)).longValue());
	}
	
	/**
	 * 
	 */
	public static String getPropertyString(String name, HttpSession session, ServletRequest request) {
		return getPropertySet(session, request).getString(name);
	}

	/**
	 * 
	 */
	public static String getPropertyData(String name, HttpSession session, ServletRequest request) {
		return getPropertyData(getPropertySet(session, request), name);
	}

	/**
	 * 
	 */
	public static String getPropertyData(final PropertySet ps, final String name) {
		try {
			final byte[] data = ps.getData(name); 
			return (data == null) ? "" : new String(data, UTF8_ENCODING);
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 
	 */
	public static void setData(final PropertySet ps, final String name, final String value)
	{
		if(value != null)
		{
			try 
			{
				ps.setData(name, value.getBytes(UTF8_ENCODING));
			} 
			catch(UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
