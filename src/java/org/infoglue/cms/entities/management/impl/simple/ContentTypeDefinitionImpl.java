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
import org.infoglue.cms.entities.management.ContentTypeDefinition;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.exception.ConstraintException;

import java.util.Collection;

public class ContentTypeDefinitionImpl implements ContentTypeDefinition
{
    private ContentTypeDefinitionVO valueObject = new ContentTypeDefinitionVO();
    private Collection contents;
    
    /**
	 * @see org.infoglue.cms.entities.kernel.BaseEntity#getVO()
	 */
	public BaseEntityVO getVO() {
		return (BaseEntityVO) getValueObject();
	}

	/**
	 * @see org.infoglue.cms.entities.kernel.BaseEntity#setVO(BaseEntityVO)
	 */
	public void setVO(BaseEntityVO valueObject) {
		setValueObject((ContentTypeDefinitionVO) valueObject);
	}
 
    /**
	 * @see org.infoglue.cms.entities.kernel.BaseEntity#getId()
	 */
	public Integer getId() {
		return getContentTypeDefinitionId();
	}
	
	public Object getIdAsObject()
	{
		return getId();
	}

	 
    public ContentTypeDefinitionVO getValueObject()
    {
        return this.valueObject;
    }

        
    public void setValueObject(ContentTypeDefinitionVO valueObject)
    {
        this.valueObject = valueObject;
    }   

    
    
    public java.lang.Integer getContentTypeDefinitionId()
    {
        return this.valueObject.getContentTypeDefinitionId();
    }
            
    public void setContentTypeDefinitionId(java.lang.Integer contentTypeDefinitionId)
    {
        this.valueObject.setContentTypeDefinitionId(contentTypeDefinitionId);
    }

    public java.lang.String getName()
    {
        return this.valueObject.getName();
    }
            
    public void setName(java.lang.String name)
    {
        this.valueObject.setName(name);
    }
      
    public java.lang.String getSchemaValue()
    {
        return this.valueObject.getSchemaValue();
    }
            
    public void setSchemaValue(java.lang.String schemaValue)
    {
        this.valueObject.setSchemaValue(schemaValue);
    }

	public Integer getType()
	{
		return this.valueObject.getType();
	}

	public void setType(Integer type) throws ConstraintException
	{
		this.valueObject.setType(type);
	}
	
	public Collection getContents()
	{
		return this.contents;
	}
	
	public void setContents(Collection contents)
	{
		this.contents = contents;
	}
	
}        
