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

package org.infoglue.cms.entities.publishing;

import java.util.Date;

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.kernel.ValidatableEntityVO;
import org.infoglue.cms.entities.publishing.impl.simple.PublicationImpl;
import org.infoglue.cms.util.validators.ConstraintRule;
import org.infoglue.cms.util.validators.Range;
import org.infoglue.cms.util.CmsLogger;

public class PublicationVO extends ValidatableEntityVO implements BaseEntityVO
{

    private java.lang.Integer publicationId;
    private Integer repositoryId;
    private java.lang.String name;
    private java.lang.String description;
    private java.util.Date publicationDateTime;
    private String publisher = null;
    
    /**
	 * @see org.infoglue.cms.entities.kernel.BaseEntityVO#getId()
	 */
	public Integer getId()
	{
		return getPublicationId();
	}


  
    public java.lang.Integer getPublicationId()
    {
        return this.publicationId;
    }
                
    public void setPublicationId(java.lang.Integer publicationId)
    {
        this.publicationId = publicationId;
    }
    
    public java.lang.String getName()
    {
        return this.name;
    }
                
    public void setName(java.lang.String name)
    {
        this.name = name;
    }
    
    public java.lang.String getDescription()
    {
        return this.description;
    }
                
    public void setDescription(java.lang.String description)
    {
        this.description = description;
    }
    
    public java.util.Date getPublicationDateTime()
    {
        return this.publicationDateTime;
    }
                
    public void setPublicationDateTime(Date publicationDateTime)
    {
        this.publicationDateTime = publicationDateTime;
    }

	public String getPublisher()
	{
		return this.publisher;
	}

	public void setPublisher(String publisher)
	{
		this.publisher = publisher;
	}

	public void PrepareValidation()
	{
		CmsLogger.logInfo("preparing validation...");
		// Define the constraint rules for this valueobject
		// maybe this belongs in the setters of this object?.
		// then this method would be obsolete, and the validation
		// should be initiated through a controller from the
		// action class??.
		// -----------------------------------------
		
		// On the rulelist set the class that holds this vo, the class
		// that is known to castor. This is for unique validation and
		// if possible should not be set in the valueobject, but preferably
		// in the actual castor-entity class. (Im not to satisfied with this
		// construction).
		rules.setEntityClass(PublicationImpl.class);
		
		// Create a new constraintrule, supply constraint type, and field that this rule
		// applies to.
 		ConstraintRule cr = new ConstraintRule(org.infoglue.cms.util.validators.Constants.STRING, "Publication.name");
 		
 		// Set the constraints
 		cr.setValidRange(new Range(2, 50) );
 		cr.unique=true;	// public variabel will be changed to setter later
 		cr.required=true; // public variabel will be changed to setter later
 		cr.setValue(name);
 		
 		// Add this rule to the rulelist
 		rules.addRule(cr);		

		// Create a new constraintrule, supply constraint type, and field that this rule
		// applies to.
 		cr = new ConstraintRule(org.infoglue.cms.util.validators.Constants.STRING, "Publication.description");
 		
 		// Set the constraints
 		cr.setValidRange(new Range(2, 50) );
 		cr.unique=false;	// public variabel will be changed to setter later
 		cr.required=true; // public variabel will be changed to setter later
 		cr.setValue(description);
 		
 		// Add this rule to the rulelist
 		rules.addRule(cr);		
	}
        
	/**
	 * Returns the repositoryId.
	 * @return Integer
	 */
	public Integer getRepositoryId()
	{
		return repositoryId;
	}

	/**
	 * Sets the repositoryId.
	 * @param repositoryId The repositoryId to set
	 */
	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}
}
        
