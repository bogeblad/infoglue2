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

package org.infoglue.deliver.util;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.*;

import java.util.Map;
import java.util.Iterator;
import java.io.*;

/**
 *
 * @author Mattias Bogeblad
 */

public class VelocityTemplateProcessor
{

	/**
	 * This method takes arguments and renders a template given as a string to the specified outputstream.
	 * Improve later - cache for example the engine.
	 */

	public void renderTemplate(Map params, PrintWriter pw, String templateAsString) throws Exception
	{
	    Timer timer = new Timer();
	    timer.setActive(false);

		Velocity.init();

        VelocityContext context = new VelocityContext();
        Iterator i = params.keySet().iterator();
        while(i.hasNext())
        {
        	String key = (String)i.next();
            context.put(key, params.get(key));
        }

        Reader reader = new StringReader(templateAsString);
        boolean finished = Velocity.evaluate(context, pw, "Generator Error", reader);

        timer.printElapsedTime("End renderTemplate");
	}

}
