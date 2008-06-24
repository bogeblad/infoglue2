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

package org.infoglue.cms.applications.databeans;

import java.util.ArrayList;
import java.util.List;

/**
 * This bean represents a Asset Key definition. Used mostly by the content type definition editor.
 * 
 * @author Mattias Bogeblad
 */

public class ComponentPropertyDefinition
{
    public final static String BINDING 		= "binding";
    public final static String TEXTFIELD 	= "textfield";
    public final static String TEXTFAREA 	= "textarea";
    public final static String SELECTFIELD	= "select";
    public final static String CHECKBOXFIELD= "checkbox";
    
    private String name;
    private String displayName;
    private String type;
    private String entity;
    private Boolean multiple;
    private Boolean assetBinding;
    private String allowedContentTypeNames;
    private String description;
	private String defaultValue 			= "";
	private String dataProvider 			= "";
	private String dataProviderParameters 	= "";
	private Boolean WYSIWYGEnabled 			= new Boolean(false);
	private String WYSIWYGToolbar 			= "";
	private Boolean autoCreateContent		= new Boolean(false);
	private String autoCreateContentMethod	= "";
	private String autoCreateContentPath	= "";
	
    private List options = new ArrayList();

    
    public ComponentPropertyDefinition(String name, String displayName, String type, String entity, Boolean multiple, Boolean assetBinding, String allowedContentTypeNames, String description, String defaultValue, Boolean WYSIWYGEnabled, String WYSIWYGToolbar, String dataProvider, String dataProviderParameters, Boolean autoCreateContent, String autoCreateContentMethod, String autoCreateContentPath)
    {
        this.name 						= name;
        this.displayName				= displayName;
        this.type 						= type;
        this.entity 					= entity;
        this.multiple 					= multiple;
        this.assetBinding 				= assetBinding;
        this.allowedContentTypeNames 	= allowedContentTypeNames;
        this.description				= description;
        this.defaultValue				= defaultValue;
        this.WYSIWYGEnabled				= WYSIWYGEnabled;
        this.WYSIWYGToolbar				= WYSIWYGToolbar;
        this.dataProvider 				= dataProvider;
        this.dataProviderParameters		= dataProviderParameters;
        this.autoCreateContent 			= autoCreateContent;
        this.autoCreateContentMethod 	= autoCreateContentMethod;
        this.autoCreateContentPath		= autoCreateContentPath;
    }
        
    public String getEntity()
    {
        return entity;
    }
    
    public Boolean getMultiple()
    {
        return multiple;
    }

    public Boolean getAssetBinding()
	{
		return assetBinding;
	}

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
    	if(displayName == null || displayName.equals(""))
    		return name;
    	else
    		return displayName;
    }

    public String getType()
    {
        return type;
    }
    
    public String getAllowedContentTypeNames()
    {
        return allowedContentTypeNames;
    }

    public String getDescription()
    {
        return description;
    }

	public List getOptions() 
	{
		return options;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public Boolean getWYSIWYGEnabled()
	{
		return WYSIWYGEnabled;
	}

	public String getWYSIWYGToolbar()
	{
		return WYSIWYGToolbar;
	}

	public String getDataProvider()
	{
		return dataProvider;
	}

	public String getDataProviderParameters()
	{
		return dataProviderParameters;
	}

	public Boolean getAutoCreateContent()
	{
		return autoCreateContent;
	}

	public String getAutoCreateContentMethod()
	{
		return autoCreateContentMethod;
	}

	public String getAutoCreateContentPath()
	{
		return autoCreateContentPath;
	}

}
