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

package org.infoglue.cms.applications.contenttool.actions;

import org.infoglue.cms.controllers.kernel.impl.simple.*;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.*;

import java.io.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource; 
import org.apache.xerces.parsers.DOMParser;

/**
  * This is the action-class for UpdateContentVersionVersion
  * 
  * @author Mattias Bogeblad
  */

public class UpdateContentVersionAttributeAction extends ViewContentVersionAction 
{
	
	private ContentVersionVO contentVersionVO;
	private Integer contentId;
	private Integer languageId;
	private Integer contentVersionId;
	private String attributeName;

	private ConstraintExceptionBuffer ceb;
		
	public UpdateContentVersionAttributeAction()
	{
		this(new ContentVersionVO());
	}
	
	public UpdateContentVersionAttributeAction(ContentVersionVO contentVersionVO)
	{
		this.contentVersionVO = contentVersionVO;
		this.ceb = new ConstraintExceptionBuffer();	
	}
	
	public String doExecute() throws Exception
    {
    	super.initialize(this.contentVersionId, this.contentId, this.languageId);

		this.contentVersionVO = this.getContentVersionVO();

		String attributeValue = getRequest().getParameter(this.attributeName);
		if(attributeValue != null)
		{
			setAttributeValue(this.contentVersionVO, this.attributeName, attributeValue);
			ceb.throwIfNotEmpty();
			
			this.contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());
    		ContentVersionController.getContentVersionController().update(this.contentId, this.languageId, this.contentVersionVO);
		}
		
		return "success";
	}


	/**
	 * This method sets a value to the xml that is the contentVersions Value. 
	 */
	 
	private void setAttributeValue(ContentVersionVO contentVersionVO, String attributeName, String attributeValue)
	{
		String value = "";
		if(this.contentVersionVO != null)
		{
			try
	        {
		        CmsLogger.logInfo("VersionValue:" + this.contentVersionVO.getVersionValue());
		        InputSource inputSource = new InputSource(new StringReader(this.contentVersionVO.getVersionValue()));
				
				DOMParser parser = new DOMParser();
				parser.parse(inputSource);
				Document document = parser.getDocument();
				
				NodeList nl = document.getDocumentElement().getChildNodes();
				Node n = nl.item(0);
				
				nl = n.getChildNodes();
				for(int i=0; i<nl.getLength(); i++)
				{
					n = nl.item(i);
					if(n.getNodeName().equalsIgnoreCase(attributeName))
					{
						CmsLogger.logInfo("Setting attributeValue: " + attributeValue);
						Node valueNode = n.getFirstChild();
						n.getFirstChild().setNodeValue(attributeValue);
						break;
					}
				}
				contentVersionVO.setVersionValue(XMLHelper.serializeDom(document, new StringBuffer()).toString());		        	
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
		}
	}
	

	public void setContentVersionId(Integer contentVersionId)
	{
		this.contentVersionVO.setContentVersionId(contentVersionId);	
	}

    public java.lang.Integer getContentVersionId()
    {
        return this.contentVersionVO.getContentVersionId();
    }

	public void setStateId(Integer stateId)
	{
		this.contentVersionVO.setStateId(stateId);	
	}

    public java.lang.Integer getStateId()
    {
        return this.contentVersionVO.getStateId();
    }

	public void setContentId(Integer contentId)
	{
		this.contentId = contentId;	
	}

    public java.lang.Integer getContentId()
    {
        return this.contentId;
    }

	public void setLanguageId(Integer languageId)
	{
		this.languageId = languageId;
	}

    public java.lang.Integer getLanguageId()
    {
        return this.languageId;
    }
        
    public java.lang.String getVersionValue()
    {
        return this.contentVersionVO.getVersionValue();
    }
        
    public void setVersionValue(java.lang.String versionValue)
    {
    	this.contentVersionVO.setVersionValue(versionValue);
    }
    
	public String getAttributeName()
	{
		return attributeName;
	}

	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}

}
