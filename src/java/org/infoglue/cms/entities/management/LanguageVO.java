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
import org.infoglue.cms.entities.management.impl.simple.LanguageImpl;
import org.infoglue.cms.exception.*;
import org.infoglue.cms.util.*;
import org.infoglue.cms.util.validators.*;

/**
 * This entity represents a language
 */

public class LanguageVO implements BaseEntityVO
{
    private java.lang.Integer languageId;
    private java.lang.String name;
    private java.lang.String languageCode;
    private java.lang.String charset = "ISO-8859-1";
    
	public String toString()
	{  
		return getName();
	}

	/**
	 * @see org.infoglue.cms.entities.kernel.BaseEntityVO#getId()
	 */
	public Integer getId() {
		return getLanguageId();
	}

    public java.lang.Integer getLanguageId()
    {
        return this.languageId;
    }
                
    public void setLanguageId(java.lang.Integer languageId)
    {
        this.languageId = languageId;
    }
    
    public java.lang.String getName()
    {
        return this.name;
    }

	public java.lang.String getLanguageCode()
	{
		return this.languageCode;
	}

	public java.lang.String getCharset()
	{
		return this.charset;
	}
                
    public void setName(java.lang.String name) throws ConstraintException
    {
    	ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
    	ValidatorFactory.createStringValidator("Language.name", true, 4, 20).validate(name, ceb); 
 

 		ceb.throwIfNotEmpty();
        this.name = name;
    }
      
    public void setLanguageCode(java.lang.String languageCode) throws ConstraintException
    {    	
    	ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
    	ValidatorFactory.createStringValidator("Language.languageCode", true, 2, 3).validate(languageCode, ceb); 
 

 		ceb.throwIfNotEmpty();
        this.languageCode = languageCode;
    }
	
	public void setCharset(java.lang.String charset)
	{
		this.charset = charset;
	}

	/**
	 * @see org.infoglue.cms.entities.kernel.BaseEntityVO#validate()
	 */

	public ConstraintExceptionBuffer validate() 
	{
    	ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
    	if (name != null) ValidatorFactory.createStringValidator("Language.name", true, 4, 20, true, LanguageImpl.class, this.getId(), null).validate(name, ceb);
    	if (languageCode != null) ValidatorFactory.createStringValidator("Language.languageCode", true, 2, 3, true, LanguageImpl.class, this.getId(), null).validate(languageCode, ceb); 
		return ceb;
	}
    
}
        
