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

package org.infoglue.cms.entities.structure;

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

import java.util.Date;

public class SiteNodeVersionVO implements BaseEntityVO
{

	public static final Integer WORKING_STATE   = new Integer(0);
	public static final Integer FINAL_STATE     = new Integer(1);
	public static final Integer PUBLISH_STATE   = new Integer(2);
	public static final Integer PUBLISHED_STATE = new Integer(3);
	public static final Integer UNPUBLISH_STATE = new Integer(4);

	public static final Integer NO 			= new Integer(0);
	public static final Integer YES 		= new Integer(1);
	public static final Integer INHERITED 	= new Integer(2);
	
    private Integer siteNodeVersionId;
    private Integer stateId       		= WORKING_STATE;
    private Integer versionNumber 		= new Integer(1);
    private Date modifiedDateTime 		= new Date();
    private String versionComment 		= "";
    private String versionModifier		= null;
    private Boolean isCheckedOut  		= new Boolean(false);
  	private Boolean isActive      		= new Boolean(true);
  	
	private Integer isProtected			= INHERITED;
	private Integer disablePageCache	= INHERITED;
	private Integer disableEditOnSight	= INHERITED;
  	private String contentType 			= "text/html";
  	
    private Integer siteNodeId			= null;
	private String siteNodeName			= "";
  
    public java.lang.Integer getSiteNodeVersionId()
    {
        return this.siteNodeVersionId;
    }
                
    public void setSiteNodeVersionId(java.lang.Integer siteNodeVersionId)
    {
        this.siteNodeVersionId = siteNodeVersionId;
    }

    public java.lang.Integer getSiteNodeId()
    {
        return this.siteNodeId;
    }
                
    public void setSiteNodeId(java.lang.Integer siteNodeId)
    {
        this.siteNodeId = siteNodeId;
    }
    
    public java.lang.Integer getStateId()
    {
        return this.stateId;
    }
                
    public void setStateId(java.lang.Integer stateId)
    {
        this.stateId = stateId;
    }
    
    public java.lang.Integer getVersionNumber()
    {
        return this.versionNumber;
    }
                
    public void setVersionNumber(java.lang.Integer versionNumber)
    {
        this.versionNumber = versionNumber;
    }
    
    public java.util.Date getModifiedDateTime()
    {
        return this.modifiedDateTime;
    }
                
    public void setModifiedDateTime(java.util.Date modifiedDateTime)
    {
        this.modifiedDateTime = modifiedDateTime;
    }
    
    public java.lang.String getVersionComment()
    {
        return this.versionComment;
    }
                
    public void setVersionComment(java.lang.String versionComment)
    {
        this.versionComment = versionComment;
    }
    
    public java.lang.Boolean getIsCheckedOut()
    {
        return this.isCheckedOut;
    }
                
    public void setIsCheckedOut(java.lang.Boolean isCheckedOut)
    {
        this.isCheckedOut = isCheckedOut;
    }
      
    public java.lang.Boolean getIsActive()
    {
    	return this.isActive;
	}
    
    public void setIsActive(java.lang.Boolean isActive)
	{
		this.isActive = isActive;
	}
	
	public Integer getId() 
	{
		return getSiteNodeVersionId();
	}
	
	public ConstraintExceptionBuffer validate() 
	{ 
		return null;
	}
	public String getContentType()
	{
		return contentType;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public Integer getDisableEditOnSight()
	{
		return (disableEditOnSight == null) ? INHERITED : disableEditOnSight;
	}

	public void setDisableEditOnSight(Integer disableEditOnSight)
	{
		this.disableEditOnSight = disableEditOnSight;
	}

	public Integer getDisablePageCache()
	{
		return (disablePageCache == null) ? INHERITED : disablePageCache;
	}

	public void setDisablePageCache(Integer disablePageCache)
	{
		this.disablePageCache = disablePageCache;
	}

	public Integer getIsProtected()
	{
		return (isProtected == null) ? INHERITED : isProtected;
	}

	public void setIsProtected(Integer isProtected)
	{
		this.isProtected = isProtected;
	}

	public String getVersionModifier()
	{
		return this.versionModifier;
	}

	public void setVersionModifier(String versionModifier)
	{
		this.versionModifier = versionModifier;
	}

	public String getSiteNodeName()
	{
		return siteNodeName;
	}

	public void setSiteNodeName(String siteNodeName)
	{
		this.siteNodeName = siteNodeName;
	}

}
        
