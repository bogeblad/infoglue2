package org.infoglue.cms.applications.workflowtool.function.defaultvalue;

import java.util.Calendar;
import java.util.Map;

import org.infoglue.cms.applications.workflowtool.function.ContentPopulator;
import org.infoglue.cms.applications.workflowtool.util.ContentValues;


import com.opensymphony.module.propertyset.PropertySet;
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
	protected void populate(final PropertySet ps) throws WorkflowException 
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + yearsAhead);
		super.populate(ps, ContentPopulator.PROPERTYSET_CONTENT_PREFIX + ContentValues.EXPIRE_DATE_TIME, calendar.getTime());
	}
	
	/**
	 * 
	 */
	protected void populate(final PropertySet ps, final String name) throws WorkflowException 
	{
		populate(ps);
	}

	/**
	 * 
	 */
	protected void populate(final PropertySet ps, final String name, final String value) throws WorkflowException 
	{
		populate(ps);
	}

	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException 
	{
		super.initialize(transientVars, args, ps);
		initializeYearsAhead(args);
	}
	
	/**
	 * 
	 */
	private void initializeYearsAhead(final Map args)
	{
		if(args.containsKey(YEARS_AHEAD_ARGUMENT))
		{
			try
			{
				int temporary = Integer.parseInt(args.get(YEARS_AHEAD_ARGUMENT).toString());
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
