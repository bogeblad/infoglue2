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

package org.infoglue.cms.security;

import java.security.Principal;
import java.util.List;


/**
 * This class represents an generic InfoGluePrincipal in InfoGlue. It is used to identify a user no matter what source it was defined in.
 * 
 * @author Mattias Bogeblad
 */

public class InfoGluePrincipal implements Principal 
{
	private final String name;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final List roles;
	private final List groups;
	private final boolean isAdministrator;
	
	public InfoGluePrincipal(String name, String firstName, String lastName, String email, List roles, List groups, boolean isAdministrator)
	{
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.roles = roles;
		this.groups = groups;
		this.isAdministrator = isAdministrator;
	}

	public String getName()
	{
		return name;
	}

	public String getFirstName() 
	{
		return firstName;
	}
	
	public String getLastName() 
	{
		return lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public List getRoles()
	{
		return roles;
	}
	
    public List getGroups()
    {
        return groups;
    }

	public boolean getIsAdministrator()
	{
		return isAdministrator;
	}
	
	public String toString()
	{
        return name;
        /*
		StringBuffer sb = new StringBuffer("InfoGluePrincipal: " + name + ":" + email + ":" + isAdministrator + '\n');
		for(Iterator i=roles.iterator(); i.hasNext();)
		{ 
			InfoGlueRole role = (InfoGlueRole)i.next();
			sb.append("" + role.getName() + ",");
		}
		sb.append("]");
		
		return sb.toString();
        */
	}

	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof InfoGluePrincipal))
			return false;
		
		InfoGluePrincipal another = (InfoGluePrincipal)obj;
		return name.equals(another.getName());
	}

	public int hasCode()
	{
		return name.hashCode();
	}

}

