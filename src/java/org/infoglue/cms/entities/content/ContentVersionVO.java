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

package org.infoglue.cms.entities.content;

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

import java.util.Date;

public class ContentVersionVO implements BaseEntityVO
{

	public static final Integer WORKING_STATE   = new Integer(0);
	public static final Integer FINAL_STATE     = new Integer(1);
	public static final Integer PUBLISH_STATE   = new Integer(2);
	public static final Integer PUBLISHED_STATE = new Integer(3);
	public static final Integer UNPUBLISH_STATE = new Integer(4);
	public static final Integer UNPUBLISHED_STATE = new Integer(5);

	private java.lang.Integer contentVersionId;
    private java.lang.Integer stateId 			 = new Integer(0);
    private java.util.Date modifiedDateTime      = new Date();
    private java.lang.String versionComment      = "Saved";
    private java.lang.Boolean isCheckedOut       = new Boolean(false);
   	private java.lang.Boolean isActive           = new Boolean(true);
   	//private java.lang.Boolean isUnpublished	  = new Boolean(false);

	private Integer languageId					 = null;
   	private Integer contentId					 = null;
    private String contentName 					 = "";
    private String languageName 				 = "";
    private String versionModifier				 = null;
	private java.lang.String versionValue   	 = "";

    public java.lang.Integer getContentVersionId()
    {
        return this.contentVersionId;
    }

    public void setContentVersionId(java.lang.Integer contentVersionId)
    {
        this.contentVersionId = contentVersionId;
    }

    public java.lang.Integer getContentId()
    {
        return this.contentId;
    }

    public void setContentId(java.lang.Integer contentId)
    {
        this.contentId = contentId;
    }

    public java.lang.Integer getStateId()
    {
        return this.stateId;
    }

    public void setStateId(java.lang.Integer stateId)
    {
        this.stateId = stateId;
    }

    public java.lang.String getVersionValue()
    {
        return this.versionValue;
    }

    public void setVersionValue(java.lang.String versionValue)
    {
    	this.versionValue = versionValue;
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

/*
   	public java.lang.Boolean getIsUnpublished()
    {
    	return this.isUnpublished;
	}

    public void setIsUnpublished(java.lang.Boolean isUnpublished)
	{
		this.isUnpublished = isUnpublished;
	}
*/
/*
    public java.lang.Boolean getIsPublishedVersion()
    {
    	return this.isPublishedVersion;
	}

    public void setIsPublishedVersion(java.lang.Boolean isPublishedVersion)
	{
		this.isPublishedVersion = isPublishedVersion;
	}
*/

    /**
	 * @see org.infoglue.cms.entities.kernel.BaseEntityVO#getId()
	 */
	public Integer getId()
	{
		return getContentVersionId();
	}

	/**
	 * @see org.infoglue.cms.entities.kernel.BaseEntityVO#validate()
	 */
	public ConstraintExceptionBuffer validate()
	{
		return null;
	}


	/**
	 * Returns the languageId.
	 * @return Integer
	 */
	public Integer getLanguageId()
	{
		return languageId;
	}

	/**
	 * Sets the languageId.
	 * @param languageId The languageId to set
	 */
	public void setLanguageId(Integer languageId)
	{
		this.languageId = languageId;
	}


	public String getVersionModifier()
	{
		return this.versionModifier;
	}

	public void setVersionModifier(String versionModifier)
	{
		this.versionModifier = versionModifier;
	}


	public ContentVersionVO getCopy()
	{
		ContentVersionVO copy = new ContentVersionVO();

		copy.setContentId(new Integer(this.contentId.intValue()));
		copy.setIsActive(new Boolean(this.isActive.booleanValue()));
		copy.setIsCheckedOut(new Boolean(this.isCheckedOut.booleanValue()));
		copy.setLanguageId(new Integer(this.languageId.intValue()));
		copy.setVersionModifier(this.versionModifier);
		copy.setModifiedDateTime(new Date(this.modifiedDateTime.getTime()));
		copy.setStateId(new Integer(this.stateId.intValue()));
		copy.setVersionComment(new String(this.versionComment));
		copy.setVersionValue(new String(this.versionValue));

		return copy;
	}
	/**
	 * @return
	 */
	public String getContentName() {
		return contentName;
	}

	/**
	 * @param string
	 */
	public void setContentName(String string) {
		contentName = string;
	}

	/**
	 * @return
	 */
	public String getLanguageName() {
		return languageName;
	}

	/**
	 * @param string
	 */
	public void setLanguageName(String string) {
		languageName = string;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("id=").append(contentVersionId)
			.append(" contentId=").append(contentId)
			.append(" contentName=").append(contentName)
			.append(" languageName=").append(languageName)
			.append(" isActive=").append(isActive)
			.append(" isCheckedOut=").append(isCheckedOut)
			.append(" stateId=").append(stateId)
			.append(" versionModifier=").append(versionModifier)
			.append(" versionComment=").append(versionComment);
		return sb.toString();
	}
}

