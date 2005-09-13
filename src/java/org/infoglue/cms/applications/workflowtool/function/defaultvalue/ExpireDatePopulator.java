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
package org.infoglue.cms.applications.workflowtool.function.defaultvalue;

import java.util.Calendar;

import org.infoglue.cms.applications.workflowtool.function.ContentPopulator;
import org.infoglue.cms.applications.workflowtool.util.ContentValues;

import com.opensymphony.workflow.WorkflowException;

public class ExpireDatePopulator extends DatePopulator
{
	/**
	 * 
	 */
	private static final String YEARS_AHEAD_ARGUMENT = "yearsAhead";
	
	/**
	 * 
	 */
	private static final int DEFAULT_YEARS_AHEAD = 10;
	
	/**
	 * 
	 */
	private int yearsAhead = DEFAULT_YEARS_AHEAD;
	
	
	
	/**
	 * 
	 */
	public ExpireDatePopulator() 
	{ 
		super();	
	}
	
	/**
	 * 
	 */
	protected void populate() throws WorkflowException 
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + yearsAhead);
		super.populate(ContentPopulator.CONTENT_PROPERTYSET_PREFIX + ContentValues.EXPIRE_DATE_TIME, calendar.getTime());
	}
	
	/**
	 * 
	 */
	protected void populate(final String name) throws WorkflowException 
	{
		populate();
	}

	/**
	 * 
	 */
	protected void populate(final String name, final String value) throws WorkflowException 
	{
		populate();
	}

	/**
	 * 
	 */
	protected void initialize() throws WorkflowException 
	{
		super.initialize();
		initializeYearsAhead();
	}
	
	/**
	 * 
	 */
	private void initializeYearsAhead()
	{
		if(argumentExists(YEARS_AHEAD_ARGUMENT))
		{
			try
			{
				int temporary = Integer.parseInt(getArgument(YEARS_AHEAD_ARGUMENT).toString());
				if(temporary > 0)
				{
					yearsAhead = temporary;
					getLogger().debug("Using [" + temporary + "] as years ahead value.");
				}
				else
					getLogger().warn("Illegal years ahead argument [" + temporary + "].");
						
			}
			catch(Exception e)
			{
				getLogger().warn("Illegal years ahead argument [" + e.getMessage() + "].");
			}
		}
		else
		{
			getLogger().debug("Using default years ahead value.");
		}
	}
}
