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

package org.infoglue.cms.entities.management.impl.simple;

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.Access;
import org.infoglue.cms.entities.management.AccessVO;
import org.infoglue.cms.entities.management.Role;
import org.infoglue.cms.exception.ConstraintException;

public class AccessImpl implements Access
{
	private AccessVO valueObject = new AccessVO();
	private Role Role;
     
	public Integer getId()
	{
		return this.getAccessId();
	}

	public Object getIdAsObject()
	{
		return getId();
	}

    public AccessVO getValueObject()
    {
        return this.valueObject;
    }
        
    public void setValueObject(AccessVO valueObject)
    {
        this.valueObject = valueObject;
    }   

	public BaseEntityVO getVO() 
	{
		return (BaseEntityVO) getValueObject();
	}

	public void setVO(BaseEntityVO valueObject) 
	{
		setValueObject((AccessVO) valueObject);
	}
    
    public java.lang.Integer getAccessId()
    {
        return this.valueObject.getAccessId();
    }
            
    public void setAccessId(java.lang.Integer RoleId)
    {
        this.valueObject.setAccessId(RoleId);
    }
      
    public java.lang.String getName()
    {
        return this.valueObject.getName(); 
    }
            
    public void setName(java.lang.String name)
    {
        this.valueObject.setName(name);
    }

	public String getValue()
	{
        return this.valueObject.getValue();
	}

	public void setValue(String value)
	{
        this.valueObject.setValue(value);
	}
      
	public java.lang.String getRoleName()
	{
		return this.valueObject.getRoleName(); 
	}
            
    public void setRoleName(java.lang.String roleName)
	{
		this.valueObject.setRoleName(roleName);
	}
	 
	public java.lang.Boolean getHasReadAccess()
	{
		return this.valueObject.getHasReadAccess();
	}
    
	public void setHasReadAccess(java.lang.Boolean hasReadAccess) throws ConstraintException
	{
		this.valueObject.setHasReadAccess(hasReadAccess);
	}
    
	public java.lang.Boolean getHasWriteAccess()
	{
		return this.valueObject.getHasWriteAccess();
	}
	
	public void setHasWriteAccess(java.lang.Boolean hasWriteAccess) throws ConstraintException
	{
		this.valueObject.setHasWriteAccess(hasWriteAccess);
	}
	
    public Role getRole()
    {
        return this.Role;
    }
            
    public void setRole(Role Role)
    {
		this.valueObject.setRoleId(Role.getId());
        this.Role = Role;
    }
      

}        
