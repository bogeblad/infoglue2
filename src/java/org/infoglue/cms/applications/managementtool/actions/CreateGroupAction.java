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

package org.infoglue.cms.applications.managementtool.actions;

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.GroupControllerProxy;
import org.infoglue.cms.entities.management.GroupVO;
import org.infoglue.cms.security.InfoGlueGroup;
import org.infoglue.cms.util.ConstraintExceptionBuffer;


/**
 * @author mgu
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */


public class CreateGroupAction extends InfoGlueAbstractAction
{
	private GroupVO groupVO;
	private InfoGlueGroup infoGlueGroup;
	private ConstraintExceptionBuffer ceb;

	public CreateGroupAction()
	{
		this(new GroupVO());
	}

	public CreateGroupAction(GroupVO GroupVO)
	{
		this.groupVO = GroupVO;	
		this.ceb = new ConstraintExceptionBuffer();
	}
		
	public String doInput() throws Exception
    {
    	return "input";
    }
	
	protected String doExecute() throws Exception 
	{
		ceb.add(this.groupVO.validate());
    	ceb.throwIfNotEmpty();	
    				
		this.infoGlueGroup = GroupControllerProxy.getController().createGroup(this.groupVO);
		
		return "success";
	}
	
	public void setGroupName(String groupName)
	{
		this.groupVO.setGroupName(groupName);	
	}

    public String getGroupName()
    {
        return this.groupVO.getGroupName();
    }
	
	public void setDescription(java.lang.String description)
	{
        this.groupVO.setDescription(description);
	}

	public String getDescription()
	{
		return this.groupVO.getDescription();	
	}
    


}
