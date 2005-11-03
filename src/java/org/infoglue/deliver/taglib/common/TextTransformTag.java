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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.AbstractTag;
import org.infoglue.deliver.taglib.TemplateControllerTag;

/**
 * This tag help modifying texts in different ways. An example is to html-encode strings or replace all 
 * whitespaces with &nbsp; or replacing linebreaks with <br/>  
 */

public class TextTransformTag extends TemplateControllerTag 
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
	 * Should we encode i18n chars
	 */
	private boolean htmlEncode = false;

	/**
	 * Should we replace linebreaks with something?
	 */
	private boolean replaceLineBreaks = false;

	/**
	 * The string to replace linebreaks with
	 */
	private String lineBreakReplacer = "<br/>";

	/**
	 * The linebreak char
	 */
	private String lineBreakChar = System.getProperty("line.separator");

	/**
	 * What to replace
	 */
	private String replaceString = null;

	/**
	 * What to replace with
	 */
	private String replaceWithString = null;

	/**
	 * Default constructor.
	 */
	public TextTransformTag() 
	{
		super();
	}
	
	/**
	 * Process the end tag. Modifies the string according to settings made.  
	 * 
	 * @return indication of whether to continue evaluating the JSP page.
	 * @throws JspException if an error occurred while processing this tag.
	 */
	public int doEndTag() throws JspException
    {
	    String modifiedText = text;
	    
	    if(replaceString != null && replaceWithString != null)
	    {
	        Pattern pattern = Pattern.compile(replaceString);
	        Matcher matcher = pattern.matcher(modifiedText);
	        modifiedText = matcher.replaceAll(replaceWithString);
	    }
	    
	    if(replaceLineBreaks)
	        modifiedText.replaceAll(lineBreakChar, lineBreakReplacer);	    
	    
	    if(htmlEncode)
	        modifiedText = this.getController().getVisualFormatter().escapeHTMLforXMLService(modifiedText);	        
	        
	    setResultAttribute(modifiedText);
	    
        return EVAL_PAGE;
    }

       
    public void setText(String text) throws JspException
    {
        this.text = evaluateString("cropText", "text", text);
    }    
    
    public void setHtmlEncode(boolean htmlEncode)
    {
        this.htmlEncode = htmlEncode;
    }
    
    public void setReplaceLineBreaks(boolean replaceLineBreaks)
    {
        this.replaceLineBreaks = replaceLineBreaks;
    }

    public void setLineBreakChar(String lineBreakChar) throws JspException
    {
        this.lineBreakChar = evaluateString("TextTransform", "lineBreakChar", lineBreakChar);
    }
    
    public void setLineBreakReplacer(String lineBreakReplacer) throws JspException
    {
        this.lineBreakReplacer = evaluateString("TextTransform", "lineBreakReplacer", lineBreakReplacer);
    }
        
    public void setReplaceString(String replaceString) throws JspException
    {
        this.replaceString = evaluateString("TextTransform", "replaceString", replaceString);
    }
    
    public void setReplaceWithString(String replaceWithString) throws JspException
    {
        this.replaceWithString = evaluateString("TextTransform", "replaceWithString", replaceWithString);
    }
}
