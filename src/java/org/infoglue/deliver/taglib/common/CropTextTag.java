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
package org.infoglue.deliver.taglib.common;

import java.util.Collection;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.AbstractTag;

/**
 * This tag will crop a text sent in  
 */

public class CropTextTag extends AbstractTag 
{
	/**
	 * The universal version identifier.
	 */
	private static final long serialVersionUID = 8603406098980150888L;
	
	/**
	 * The original text.
	 */
	private String text;

	/**
	 * The position to start substring-method on - default from the first position.
	 */
	private int startIndex = 0;

	/**
	 * The maxlength of the modified text.
	 */
	private int maxLength = -1;

	/**
	 * The text indicating that there is more text
	 */
	private String suffix = "...";

	/**
	 * Default constructor.
	 */
	public CropTextTag() 
	{
		super();
	}
	
	/**
	 * Process the end tag. Crops the text and adds the suffix.  
	 * 
	 * @return indication of whether to continue evaluating the JSP page.
	 * @throws JspException if an error occurred while processing this tag.
	 */
	public int doEndTag() throws JspException
    {
	    String modifiedText = text;
	    if(maxLength > -1)
	    {
	        if(text.length() > (startIndex + maxLength))
	            modifiedText = text.substring(startIndex, maxLength);
	        else
	            modifiedText = text.substring(startIndex);
	    }
	    else
	    {
            modifiedText = text.substring(startIndex);
	    }
	    
	    modifiedText += suffix;
	    
	    setResultAttribute(modifiedText);
	    
        return EVAL_PAGE;
    }

    public void setMaxLength(String maxLength) throws JspException
    {
        this.maxLength = evaluateInteger("cropText", "maxLength", maxLength).intValue();
    }
    
    public void setStartIndex(String startIndex) throws JspException
    {
        this.startIndex = evaluateInteger("cropText", "startIndex", startIndex).intValue();
    }
    
    public void setSuffix(String suffix) throws JspException
    {
        this.suffix = evaluateString("cropText", "suffix", suffix);
    }
    
    public void setText(String text) throws JspException
    {
        this.text = evaluateString("cropText", "text", text);
    }
}
