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
package org.infoglue.cms.applications.workflowtool.function.email;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.mail.internet.InternetAddress;

import org.infoglue.cms.applications.workflowtool.function.ContentVersionFunction;

import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class ContentVersionAddressProvider extends ContentVersionFunction 
{
	/**
	 * The name of the attributes argument.
	 */
	private static final String ATTRIBUTES_ARGUMENT =
"attributes";
	
	/**
	 * The attribute argument value delimiter.
	 */
	private static final String DELIMITER = ",";
	
	/**
	 * The name of the attributes.
	 */
	private Collection attributeNames = new ArrayList();

	/**
	 * The addresses.
	 */
	private Collection addresses = new ArrayList(); // collection of <InternetAddress> 
	
	
	
	/**
	 * 
	 */
	public ContentVersionAddressProvider() 
	{
		super(true);
	}

	/**
	 * 
	 */
	protected void execute() throws WorkflowException 
	{
		for(final Iterator names = attributeNames.iterator(); names.hasNext(); )
		{
			createAddress(names.next().toString());
		}
		setParameter(EmailFunction.TO_PARAMETER, addresses.toArray());
	}

	/**
	 * 
	 */
	private void createAddress(final String attributeName) throws WorkflowException
	{
		try 
		{
			addresses.add(new InternetAddress(getAttribute(attributeName, false)));
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
	
	/**
	 * 
	 */
	protected void initialize() throws WorkflowException 
	{
		super.initialize();
		initializeAttributeNames(getArgument(ATTRIBUTES_ARGUMENT));
	}
	
	/**
	 * 
	 */
	private void initializeAttributeNames(final String names)
	{
		for(final StringTokenizer st = new StringTokenizer(names, DELIMITER); st.hasMoreTokens(); )
		{
			attributeNames.add(st.nextToken());
		}
	}
}
