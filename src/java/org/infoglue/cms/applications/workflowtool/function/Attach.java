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
package org.infoglue.cms.applications.workflowtool.function;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.infoglue.cms.applications.workflowtool.util.Attachment;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.entities.content.DigitalAssetVO;
import org.infoglue.cms.util.CmsPropertyHandler;

import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class Attach extends ContentFunction
{
	/**
	 * 
	 */
	private static final String ATTACHMENTS_PARAMETER = "attachments";

	/**
	 * 
	 */
	private Collection attachments;
	
	/**
	 * Default constructor.
	 */
	public Attach() 
	{
		super();
	}

	/**
	 * 
	 */
	protected void execute() throws WorkflowException 
	{
		if(getContentVersion() != null)
		{
			try 
			{
				for(final Iterator i = attachments.iterator(); i.hasNext(); )
				{
					final Attachment attachment = (Attachment) i.next();
					DigitalAssetController.create(createDigitalAssetVO(attachment), getInputStream(attachment), getContentVersion(), getDatabase());
				}
			} 
			catch(Exception e)
			{
				throwException(e);
			}
		}
	}

	/**
	 * 
	 */
	private DigitalAssetVO createDigitalAssetVO(final Attachment attachment) 
	{
		final DigitalAssetVO digitalAssetVO = new DigitalAssetVO();
		digitalAssetVO.setAssetContentType(attachment.getContentType());
		digitalAssetVO.setAssetKey(attachment.getName());
		digitalAssetVO.setAssetFileName(attachment.getName());
		digitalAssetVO.setAssetFilePath(getDigitalAssetsDirectory());
		digitalAssetVO.setAssetFileSize(new Integer(attachment.getSize()));
		return digitalAssetVO;
	}
	
	/**
	 * 
	 */
	private InputStream getInputStream(final Attachment attachment)
	{
		return new ByteArrayInputStream(attachment.getBytes());
	}
	
	/**
	 * 
	 */
	private String getDigitalAssetsDirectory()
	{
    	return CmsPropertyHandler.getProperty("digitalAssetPath");
	}

	/**
	 * Method used for initializing the object; will be called before <code>execute</code> is called.
	 * Note! You must call <code>super.initialize()</code> first.
	 * 
	 * @throws WorkflowException if an error occurs during the initialization.
	 */
	protected void initialize() throws WorkflowException 
	{
		super.initialize();
		
		this.attachments = (Collection) getParameter(ATTACHMENTS_PARAMETER, new ArrayList());
	}
}
