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

import javax.mail.internet.InternetAddress;

import org.infoglue.cms.applications.workflowtool.function.InfoglueFunction;
import org.infoglue.cms.entities.management.SystemUser;

import com.opensymphony.workflow.WorkflowException;

/**
 * 
 *
 */
public abstract class UsersAddressProvider extends InfoglueFunction {
	/**
	 * The addresses.
	 */
	private Collection addresses = new ArrayList(); // collection of <InternetAddress>
	
	
	
	/**
	 * 
	 */
	public UsersAddressProvider() 
	{
		super();
	}

	/**
	 * 
	 */
	protected abstract Collection getUsers() throws WorkflowException;
	
	/**
	 * 
	 */
	protected final void execute() throws WorkflowException 
	{
		try {
			for(Iterator users = getUsers().iterator(); users.hasNext(); )
			{
				createAddress((SystemUser) users.next());
			}
			setParameter(EmailFunction.TO_PARAMETER, addresses.toArray());
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
	
	/**
	 * Creates an address for the specified user.
	 * 
	 */
	private void createAddress(final SystemUser user) throws WorkflowException
	{
		try 
		{
			final String email = user.getEmail();
			if(email == null || email.length() == 0)
			{
				getLogger().warn("The [" + user.getUserName() + "] user has no email address.");
			}
			else
			{
				addresses.add(new InternetAddress(email));
			}
		}
		catch(Exception e)
		{
			throwException(e);
		}
	}
}