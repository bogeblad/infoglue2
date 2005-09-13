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
package org.infoglue.cms.applications.workflowtool.util;

/**
 * 
 */
public class RangeCheck 
{
	/**
	 * 
	 */
	public static final int OK = 0;
	
	/**
	 * 
	 */
	public static final int EXACTLY = 1;
	
	/**
	 * 
	 */
	public static final int EXACTLY_ONE = 2;
	
	/**
	 * 
	 */
	public static final int LESS_THAN = 3;
	
	/**
	 * 
	 */
	public static final int GREATER_THAN = 4;

	/**
	 * 
	 */
	public static final int GREATER_THAN_ONE = 5;
	
	/**
	 * 
	 */
	public static final int BETWEEN = 6;
	
	/**
	 * 
	 */
	public static final int BETWEEN_ONE_AND_MANY = 7;
	
	/**
	 * 
	 */
	private Integer min;
	
	/**
	 * 
	 */
	private Integer max;
	
	
	
	/**
	 * 
	 */
	public RangeCheck(final Integer min, final Integer max)
	{
		this.min = min;
		this.max = max;
	}
	
	/**
	 * 
	 */
	public final Integer getMin()
	{
		return min;
	}
	
	/**
	 * 
	 */
	public final Integer getMax()
	{
		return max;
	}
	
	/**
	 * 
	 */
	public final int check(final int count)
	{
		boolean one = (min != null && min.intValue() == 1);
		if(min == null && max == null)
		{
			return OK;
		}
		if(min == null)
		{
			return (count > max.intValue()) ? LESS_THAN : OK;
		}
		if(max == null)
		{
			return (count < min.intValue()) ? (one ? GREATER_THAN_ONE : GREATER_THAN) : OK;
		}
		if(min.equals(max) && count != min.intValue())
		{
			return one ? EXACTLY_ONE : EXACTLY;
		}
		if(count < min.intValue() || count > max.intValue())
		{
			return one ? BETWEEN_ONE_AND_MANY : BETWEEN;
		}
		return OK;
	}
}
