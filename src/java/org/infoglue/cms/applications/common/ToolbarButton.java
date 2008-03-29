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

package org.infoglue.cms.applications.common;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a button in the CMSTools menu.
 */

public class ToolbarButton
{
	private String id            		= "";
	private String text            		= "";
	private String title            	= "";
	private String backgroundImageURL   = "";
	private String actionURL  	    	= "";
	//private Integer height			= new Integer(22);
	//private Integer width				= new Integer(76);
	//private List subButtons			= new ArrayList();
	//private boolean isSelfContained	= false;

	public ToolbarButton(String id, String text, String title, String actionURL, String backgroundImageURL)
	{
		this.id 				= id;
		this.text 				= text;
		this.title     			= title;
		this.backgroundImageURL = backgroundImageURL;
		this.actionURL 			= actionURL;
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

	public String getBackgroundImageURL()
	{
		return backgroundImageURL;
	}

	public String getActionURL()
	{
		return actionURL;
	}
}