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

package org.infoglue.deliver.taglib.management;

import javax.servlet.jsp.JspException;

import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.deliver.taglib.TemplateControllerTag;

public class PrincipalPropertyTag extends TemplateControllerTag 
{
	private static final long serialVersionUID = 4050206323348354355L;

	private String userName;
	private InfoGluePrincipal principal;
	private String attributeName;
	
    public PrincipalPropertyTag()
    {
        super();
    }

	public int doEndTag() throws JspException
    {
	    if(userName != null && !userName.equals(""))
	    {
	        setResultAttribute(this.getController().getPrincipalPropertyValue(getController().getPrincipal(userName), attributeName));
	    }
	    else if(principal != null)
	    {
            setResultAttribute(getController().getPrincipalPropertyValue(principal, attributeName));
	    }
	    else
	    {
	    	setResultAttribute(getController().getPrincipalPropertyValue(attributeName));
	    }
	    	
	    return EVAL_PAGE;
    }

    public void setUserName(final String userName) throws JspException
    {
        this.userName = evaluateString("principal", "userName", userName);
    }

    public void setPrincipal(final String principalString) throws JspException
    {
        this.principal = (InfoGluePrincipal)evaluate("principal", "principal", principalString, InfoGluePrincipal.class);
    }

    public void setAttributeName(final String attributeName) throws JspException
    {
        this.attributeName = evaluateString("principal", "attributeName", attributeName);
    }

}
