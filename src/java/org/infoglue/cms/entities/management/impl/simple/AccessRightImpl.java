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
import org.infoglue.cms.entities.management.AccessRight;
import org.infoglue.cms.entities.management.AccessRightVO;
import org.infoglue.cms.entities.management.InterceptionPoint;

public class AccessRightImpl implements AccessRight
{
	private AccessRightVO valueObject = new AccessRightVO();
	private InterceptionPoint interceptionPoint = null;

    public java.lang.Integer getAccessRightId()
    {
        return this.valueObject.getAccessRightId();
    }
      
	public Object getIdAsObject()
	{
		return getId();
	}
        
    public void setAccessRightId(java.lang.Integer accessRightId)
    {
        this.valueObject.setAccessRightId(accessRightId);
    }
      
    public java.lang.String getName()
    {
        return this.valueObject.getName(); 
    }
            
    public void setName(java.lang.String name)
    {
        this.valueObject.setName(name);
    }

	public String getParameters()
	{
        return this.valueObject.getParameters();
	}

	public void setParameters(String parameters)
	{
        this.valueObject.setParameters(parameters);
	}
      
	public java.lang.String getRoleName()
	{
		return this.valueObject.getRoleName(); 
	}
            
    public void setRoleName(java.lang.String roleName)
	{
		this.valueObject.setRoleName(roleName);
	}
	       
	public InterceptionPoint getInterceptionPoint()
	{
		return this.interceptionPoint;
	}
	
	public void setInterceptionPoint(InterceptionPoint interceptionPoint)
	{
	    this.valueObject.setInterceptionPointId(interceptionPoint.getId());
	    this.valueObject.setInterceptionPointName(interceptionPoint.getName());
		this.interceptionPoint =  interceptionPoint;
	}
	
	public Integer getId()
	{
		return this.getAccessRightId();
	}

	public AccessRightVO getValueObject()
	{
		return this.valueObject;
	}
        
	public void setValueObject(AccessRightVO valueObject)
	{
		this.valueObject = valueObject;
	}   

	public BaseEntityVO getVO() 
	{
		return (BaseEntityVO) getValueObject();
	}

	public void setVO(BaseEntityVO valueObject) 
	{
		setValueObject((AccessRightVO) valueObject);
	}
}        
