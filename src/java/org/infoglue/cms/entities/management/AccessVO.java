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

package org.infoglue.cms.entities.management;

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

public class AccessVO  implements BaseEntityVO
{
	private java.lang.Integer AccessId;
	private java.lang.String name;
	private java.lang.String value;
	private java.lang.String roleName;
	private java.lang.Boolean hasReadAccess;
	private java.lang.Boolean hasWriteAccess;
	private java.lang.Integer RoleId;
  	
	public Integer getId() 
	{
		return getAccessId();
	}

    public java.lang.Integer getAccessId()
    {
        return this.AccessId;
    }
                
    public void setAccessId(java.lang.Integer AccessId)
    {
        this.AccessId = AccessId;
    }
    
	public java.lang.Boolean getHasReadAccess()
	{
		return this.hasReadAccess;
	}

	public void setHasReadAccess(java.lang.Boolean hasReadAccess)
	{
		this.hasReadAccess = hasReadAccess;
	}

	public java.lang.Boolean getHasWriteAccess()
	{
		return this.hasWriteAccess;
	}

	public void setHasWriteAccess(java.lang.Boolean hasWriteAccess)
	{
		this.hasWriteAccess = hasWriteAccess;
	}

	public java.lang.String getName()
	{
		return this.name;
	}

	public void setName(java.lang.String name)
	{
		this.name = name;
	}

	public java.lang.String getValue()
	{
		return this.value;
	}

	public void setValue(java.lang.String value)
	{
		this.value = value;
	}

	public java.lang.Integer getRoleId()
	{
		return RoleId;
	}

	public java.lang.String getRoleName()
	{
		return this.roleName;
	}

	public void setRoleName(java.lang.String roleName)
	{
		this.roleName = roleName;
	}

	public void setRoleId(java.lang.Integer RoleId)
	{
		this.RoleId = RoleId;
	}

	public ConstraintExceptionBuffer validate() 
	{
    	
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
		//if (name != null) ValidatorFactory.createStringValidator("Access.name", true, 3, 50, true, RoleImpl.class, this.getId()).validate(name, ceb);

		return ceb;
	}

}
        
