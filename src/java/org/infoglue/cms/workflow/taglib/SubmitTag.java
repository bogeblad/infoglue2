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
package org.infoglue.cms.workflow.taglib;

/**
 * 
 */
public class SubmitTag extends ContentInputTag 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1974076670375531350L;

	/**
	 * 
	 */
    public SubmitTag() 
    {
        super();
    }

    protected String getType() 
    {
    	return "submit";
    }
    
	/**
	 * 
	 */
	public void setActionID(final String id) 
	{
		getElement().attribute("onclick", "document.getElementById('" + ACTION_ID_PARAMETER + "').value=" + id + ";");
    }

	/**
	 * 
	 */
	public void setValue(final String value) 
	{
		getElement().attribute("value", value);
    }
}