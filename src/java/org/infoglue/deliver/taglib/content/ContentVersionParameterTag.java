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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.deliver.taglib.AbstractTag;

/**
 * This class implements the &lt;common:parameter&gt; tag, which adds a parameter
 * to the parameters of the parent tag.
 *
 *  Note! This tag must have a &lt;common:urlBuilder&gt; ancestor.
 */
public class ContentVersionParameterTag extends AbstractTag 
{
	/**
	 * The universal version identifier.
	 */
	private static final long serialVersionUID = 4482006814634520239L;

	/**
	 * The content version object.
	 */
	private Map contentVersion = new HashMap();
	
	
	/**
	 * Default constructor. 
	 */
	public ContentVersionParameterTag()
	{
		super();
	}

	/**
	 * Initializes the parameters to make it accessible for the children tags (if any).
	 * 
	 * @return indication of whether to evaluate the body or not.
	 * @throws JspException if an error occurred while processing this tag.
	 */
	public int doStartTag() throws JspException 
	{
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * Adds a parameter with the specified name and value to the parameters
	 * of the parent tag.
	 * 
	 * @return indication of whether to continue evaluating the JSP page.
	 * @throws JspException if an error occurred while processing this tag.
	 */
	public int doEndTag() throws JspException
    {
		addContentVersion();
		return EVAL_PAGE;
    }
	
	/**
	 * Adds the content version to the ancestor tag.
	 * 
	 * @throws JspException if the ancestor tag isn't a url tag.
	 */
	protected void addContentVersion() throws JspException
	{
		final ContentParameterTag parent = (ContentParameterTag) findAncestorWithClass(this, ContentParameterTag.class);
		if(parent == null)
		{
			throw new JspTagException("URLParameterTag must have a URLTag ancestor.");
		}
		((ContentParameterTag) parent).addContentVersion(contentVersion);
	}

	/**
	 * Adds the content version attribute to the contentVersion Value.
	 * 
	 * @throws JspException if the ancestor tag isn't a url tag.
	 */
	protected void addContentVersionAttribute(String name, String value) throws JspException
	{
	    Map contentVersionAttributes = (Map)this.contentVersion.get("contentVersionAttributes");
	    if(contentVersionAttributes == null)
	    {
	        contentVersionAttributes = new HashMap();
	        this.contentVersion.put("contentVersionAttributes", contentVersionAttributes);
	    }
	    System.out.println("Adding attribute:" + name + "=" + value);
	    contentVersionAttributes.put(name, value);
	}

	/**
	 * 
	 */
	public void setLanguageId(final String languageId) throws JspException
	{
	    this.contentVersion.put("languageId", evaluateInteger("remoteContentService", "languageId", languageId));
	}

}
