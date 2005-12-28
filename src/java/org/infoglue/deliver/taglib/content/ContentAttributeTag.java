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

import java.util.Map;

import javax.servlet.jsp.JspException;

import org.infoglue.deliver.taglib.component.ComponentLogicTag;
import org.infoglue.deliver.util.Support;

/**
 * This is an attempt to make an TagLib for attempts to get a ContentAttribute from a content referenced by a component
 * in a JSP.
 * 
 * <%@ taglib uri="infoglue" prefix="infoglue" %>
 * 
 * <infoglue:component.ContentAttribute propertyName="Article" attributeName="Title"/>
 *
 * @author Mattias Bogeblad
 * 
 * 2005-12-22 Added mapKeyName which extracts a value from a properties.file formated text content. / per.jonsson@it-huset.se
 *
 * 
 */

public class ContentAttributeTag extends ComponentLogicTag
{
	private static final long serialVersionUID = 3257850991142318897L;
	
	private Integer contentId;
	private String propertyName;
    private String attributeName;
    private String mapKeyName;
    private boolean disableEditOnSight 	= false;
    private boolean useInheritance		= true;
    private boolean parse				= false;
    private boolean fullBaseUrl			= false;
    
    public ContentAttributeTag()
    {
        super();
    }
    
    public int doEndTag() throws JspException
    {
        boolean previousSetting = getController().getDeliveryContext().getUseFullUrl();
        Object result = null;
        if(previousSetting != fullBaseUrl)
        {
            getController().getDeliveryContext().setUseFullUrl(fullBaseUrl);
        }
        // Have to force a disable editon sight, not good with renderstuff
        // when converting a attributeto a map.
        // per.jonsson@it-huset.se
        if ( mapKeyName != null )
        {
            disableEditOnSight = true;
        }
            
        if(contentId != null)
        {
            if(!parse)
            {
                result = getController().getContentAttribute(contentId, attributeName, disableEditOnSight);
            }
	        else
	        {
	            result = getController().getParsedContentAttribute(contentId, attributeName, disableEditOnSight);
            }
        }
        else if(propertyName != null)
        {
	        if(!parse)
            {
                result = getComponentLogic().getContentAttribute(propertyName, attributeName, disableEditOnSight, useInheritance);
            }
	        else
            {
	            result = getComponentLogic().getParsedContentAttribute(propertyName, attributeName, disableEditOnSight, useInheritance);
            }
        }
        else
        {
            throw new JspException("You must specify either contentId or attributeName");
        }
        if ( mapKeyName != null && result != null )
        {
            Map map = Support.convertTextToProperties( result.toString() );
            if ( map != null && !map.isEmpty() )
            {
                result = map.get( mapKeyName );
            }
        }
        produceResult( result );
        //Resetting the full url to the previous state
        getController().getDeliveryContext().setUseFullUrl(previousSetting);

        return EVAL_PAGE;
    }

	public void setPropertyName(String propertyName) throws JspException
    {
        this.propertyName = evaluateString("contentAttribute", "propertyName", propertyName);
    }
    
    public void setAttributeName(String attributeName) throws JspException
    {
        this.attributeName = evaluateString("contentAttribute", "attributeName", attributeName);
    }
    
    public void setDisableEditOnSight(boolean disableEditOnSight)
    {
        this.disableEditOnSight = disableEditOnSight;
    }
    
    public void setUseInheritance(boolean useInheritance)
    {
        this.useInheritance = useInheritance;
    }
    
    public void setParse(boolean parse)
    {
        this.parse = parse;
    }
    
    public void setContentId(final String contentId) throws JspException
    {
        this.contentId = evaluateInteger("contentAttribute", "contentId", contentId);
    }
    
    public void setFullBaseUrl(boolean fullBaseUrl)
    {
        this.fullBaseUrl = fullBaseUrl;
    }

    public void setMapKeyName( String mapKeyName )
    {
        this.mapKeyName = mapKeyName;
    }
}