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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a button in the CMSTools menu.
 */

public class LinkBean implements Serializable
{
	private String id            			= "";
	private String text            			= "";
	private String title            		= "";
	private String description				= "";
	private String backgroundImageURL   	= "";
	private String backgroundImageAlignment	= "left";
	private String actionURL  	    		= "";	

	public LinkBean(String id, String text, String title, String description, String actionURL, String backgroundImageURL)
	{
		this(id, text, title, description, actionURL, backgroundImageURL, "left");
	}

	public LinkBean(String id, String text, String title, String description, String actionURL, String backgroundImageURL, String backgroundImageAlignment)
	{
		this.id 						= id;
		this.text 						= text;
		this.title     					= title;
		this.description				= description;
		this.backgroundImageURL 		= backgroundImageURL;
		this.backgroundImageAlignment 	= backgroundImageAlignment;
		this.actionURL 					= actionURL;
	}

	public String getId()
	{
		return id;
	}

	public String getText()
	{
		return text;
	}

	public String getTitle()
	{
		return title;
	}
	
	public String getDescription()
	{
		return description;
	}

	public String getBackgroundImageURL()
	{
		return backgroundImageURL;
	}

	public String getActionURL()
	{
		return actionURL;
	}

	public String getBackgroundImageAlignment()
	{
		return backgroundImageAlignment;
	}
}