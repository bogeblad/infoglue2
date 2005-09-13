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
package org.infoglue.cms.util.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.infoglue.cms.security.InfoGlueGroup;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.security.InfoGlueRole;

/**
 * 
 */
class PrincipalOwner implements Owner
{
	/**
	 * 
	 */
	private final InfoGluePrincipal principal;
	
	/**
	 * 
	 */
	PrincipalOwner(final InfoGluePrincipal principal)
	{
		this.principal = principal;
	}
	
	/**
	 * 
	 */
	public String getIdentifier() 
	{
		return principal.getName();
	}
}

/**
 * TODO NOTE NOTE NOTE! Will not work very well if any user/role/group contains '\' characters
 */
class RoleOwner implements Owner
{
	/**
	 * 
	 */
	private static final String PREFIX = "\\";
	
	/**
	 * 
	 */
	private final InfoGlueRole role;
	
	/**
	 * 
	 */
	private final InfoGlueGroup group;
	
	/**
	 * 
	 */
	RoleOwner(final InfoGlueRole role)
	{
		this(role, null);
	}
	
	/**
	 * 
	 */
	RoleOwner(final InfoGlueRole role, final InfoGlueGroup group)
	{
		this.role  = role;
		this.group = group;
	}
	
	/**
	 * 
	 */
	public String getIdentifier() 
	{
		if(group == null)
		{
			return PREFIX + role.getName();
		}
		else 
		{
			return PREFIX + role.getName() + PREFIX + group.getName();
		}
	}
}

/**
 * 
 */
public class OwnerFactory {
	/**
	 * Static class; don't allow instantiation.
	 */
	private OwnerFactory()
	{
		
	}
	
	/**
	 * 
	 */
	public static final Owner create(final InfoGluePrincipal principal)
	{
		return new PrincipalOwner(principal);
	}

	/**
	 * 
	 */
	public static final Owner create(final InfoGlueRole role)
	{
		return new RoleOwner(role);
	}

	/**
	 * 
	 */
	public static final Owner create(final InfoGlueRole role, final InfoGlueGroup group)
	{
		return new RoleOwner(role, group);
	}

	/**
	 * 
	 */
	public static final Collection createAll(final InfoGluePrincipal principal)
	{
		final List owners = new ArrayList();
		owners.add(OwnerFactory.create(principal));
		for(final Iterator roleIterator = principal.getRoles().iterator(); roleIterator.hasNext(); )
		{
			final InfoGlueRole role = (InfoGlueRole) roleIterator.next();
			owners.add(create(role));
			for(final Iterator groupIterator = principal.getGroups().iterator(); groupIterator.hasNext(); )
			{
				final InfoGlueGroup group = (InfoGlueGroup) groupIterator.next();
				owners.add(create(role, group));
			}
		}
		return owners;
	}
	
}
