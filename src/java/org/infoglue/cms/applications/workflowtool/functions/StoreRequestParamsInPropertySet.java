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
 * $Id: StoreRequestParamsInPropertySet.java,v 1.1 2005/01/18 19:12:45 jed Exp $
 */
package org.infoglue.cms.applications.workflowtool.functions;

import java.util.*;

import javax.servlet.ServletRequest;

import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;

/**
 * @version $Revision: 1.1 $ $Date: 2005/01/18 19:12:45 $
 */
public class StoreRequestParamsInPropertySet implements FunctionProvider
{
	public void execute(Map transientVars, Map args, PropertySet propertySet)
	{
		System.out.println("transientVars:" + transientVars.getClass().getName());
		Iterator iterator = transientVars.keySet().iterator();
		while (iterator.hasNext())
		{
			Object key = iterator.next();
			System.out.println("key:" + key);
			Object value = transientVars.get(key);
			System.out.println("value:" + value.getClass().getName());
		}

		ServletRequest request = (ServletRequest)transientVars.get("request");

		String name = request.getParameter("name");
		String title = request.getParameter("title");
		String navigationTitle = request.getParameter("navigationTitle");
		String leadIn = request.getParameter("leadIn");
		String fullText = request.getParameter("fullText");

		System.out.println("Caller - ${caller}");
		System.out.println("name:" + name);
		System.out.println("title:" + title);
		System.out.println("navigationTitle:" + navigationTitle);
		System.out.println("leadIn:" + leadIn);
		System.out.println("fullText:" + fullText);
		propertySet.setString("name", name);
		propertySet.setString("title", title);
		propertySet.setString("navigationTitle", navigationTitle);
		propertySet.setString("leadIn", leadIn);
		propertySet.setString("fullText", fullText);
	}
}
