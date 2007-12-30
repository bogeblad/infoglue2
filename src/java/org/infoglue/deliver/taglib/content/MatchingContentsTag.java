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

package org.infoglue.deliver.taglib.content;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.TemplateControllerTag;

public class MatchingContentsTag extends TemplateControllerTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3833470599837135666L;
	
	private String contentTypeDefinitionNames;
	private String categoryCondition;
	private String freeText;
	private String freeTextAttributeNames;
	private Date fromDate = null;
	private Date toDate = null;
	
	private boolean cacheResult = true;
	private int cacheInterval = 1800;
	private String cacheName = null;
	private String cacheKey = null;
	
    public MatchingContentsTag()
    {
        super();
    }

	public int doEndTag() throws JspException
    {
		List freeTextAttributeNamesList = null;
		if(freeTextAttributeNames != null && !freeTextAttributeNames.equals(""))
		{
			String[] freeTextAttributeNamesArray = freeTextAttributeNames.split(",");
			if(freeTextAttributeNamesArray.length > 0)
				freeTextAttributeNamesList = Arrays.asList(freeTextAttributeNamesArray);
		}
		
	    setResultAttribute(getController().getMatchingContents(contentTypeDefinitionNames, categoryCondition, freeText, freeTextAttributeNamesList, fromDate, toDate, true, cacheResult, cacheInterval, cacheName, cacheKey));
        return EVAL_PAGE;
    }

    public void setContentTypeDefinitionNames(String contentTypeDefinitionNames) throws JspException
    {
        this.contentTypeDefinitionNames = evaluateString("matchingContentsTag", "contentTypeDefinitionNames", contentTypeDefinitionNames);
    }

    public void setCategoryCondition(String categoryCondition) throws JspException
    {
        this.categoryCondition = evaluateString("matchingContentsTag", "categoryCondition", categoryCondition);
    }

	public void setFreeText(String freeText) throws JspException
	{
		this.freeText = evaluateString("matchingContentsTag", "freeText", freeText);
	}

	public void setFreeTextAttributeNames(String freeTextAttributeNames) throws JspException
	{
		this.freeTextAttributeNames = evaluateString("matchingContentsTag", "freeTextAttributeNames", freeTextAttributeNames);
	}

	public void setFromDate(String fromDate) throws JspException
	{
		this.fromDate = (Date)evaluate("matchingContentsTag", "fromDate", fromDate, Date.class);
	}

	public void setToDate(String toDate) throws JspException
	{
		this.toDate = (Date)evaluate("matchingContentsTag", "toDate", toDate, Date.class);
	}

	public void setCacheInterval(int cacheInterval)
	{
		this.cacheInterval = cacheInterval;
	}

	public void setCacheKey(String cacheKey) throws JspException
	{
		this.cacheKey = evaluateString("matchingContentsTag", "cacheKey", cacheKey);
	}

	public void setCacheName(String cacheName) throws JspException
	{
		this.cacheName = evaluateString("matchingContentsTag", "cacheName", cacheName);;
	}

	public void setCacheResult(boolean cacheResult)
	{
		this.cacheResult = cacheResult;
	}


}
