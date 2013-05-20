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

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionControllerProxy;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

import com.thoughtworks.xstream.XStream;

/**
  * This is the action-class for UpdateContentVersionVersion
  * 
  * @author Mattias Bogeblad
  */

public class UpdateContentVersionAction extends ViewContentVersionAction 
{
	private static final long serialVersionUID = 1L;
	
    public final static Logger logger = Logger.getLogger(UpdateContentVersionAction.class.getName());

	private ContentVersionVO contentVersionVO;
	private Integer contentId;
	private Integer languageId;
	private Integer contentVersionId;
	private Integer currentEditorId;
	private String attributeName;
	private long oldModifiedDateTime = -1;
	private boolean concurrentModification = false;
	
	private ConstraintExceptionBuffer ceb;
	
	public UpdateContentVersionAction()
	{
		this(new ContentVersionVO());
	}
	
	public UpdateContentVersionAction(ContentVersionVO contentVersionVO)
	{
	    this.contentVersionVO = contentVersionVO;
		this.ceb = new ConstraintExceptionBuffer();	
	}
	
	public String doExecute() throws Exception
	{
		super.initialize(this.contentVersionId, this.contentId, this.languageId);
		ceb.throwIfNotEmpty();

		ContentVersionVO currentContentVersionVO = null;
		if(this.contentVersionVO.getId() != null)
		{
			currentContentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(this.contentVersionVO.getId());
		}
		
		if(currentContentVersionVO != null)
		{
			logger.info("oldModifiedDateTime:" + oldModifiedDateTime);
			logger.info("modifiedDateTime2:" + currentContentVersionVO.getModifiedDateTime().getTime());
		}
		
		if(currentContentVersionVO == null || this.oldModifiedDateTime == currentContentVersionVO.getModifiedDateTime().getTime())
		{	
			this.contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());
			
			try
			{
			    this.contentVersionVO = ContentVersionControllerProxy.getController().acUpdate(this.getInfoGluePrincipal(), this.contentId, this.languageId, this.contentVersionVO);
			    this.contentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(this.contentVersionVO.getId());
				this.oldModifiedDateTime = this.contentVersionVO.getModifiedDateTime().getTime();
			}
			catch(ConstraintException ce)
			{
			    super.contentVersionVO = this.contentVersionVO;
			    throw ce;
			}
		}
		else
		{
			this.contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());
			super.contentVersionVO = this.contentVersionVO;
		    /*
		    ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
			ceb.add(new ConstraintException("ContentVersion.concurrentModification", "3306"));
			ceb.throwIfNotEmpty();
			*/
			concurrentModification = true;
		}
		
		return "success";
	}

	public String doUpdateVersionValue() throws Exception
	{
		super.initialize(this.contentVersionId, this.contentId, this.languageId);
		ceb.throwIfNotEmpty();

		ContentVersionVO currentContentVersionVO = null;
		if(this.contentVersionVO.getId() != null)
		{
			currentContentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(this.contentVersionVO.getId());
		}
		
		if(currentContentVersionVO != null)
		{
			logger.info("oldModifiedDateTime:" + oldModifiedDateTime);
			logger.info("modifiedDateTime2:" + currentContentVersionVO.getModifiedDateTime().getTime());
		}
		
		this.contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());
		
		try
		{
		    this.contentVersionVO = ContentVersionControllerProxy.getController().acUpdate(this.getInfoGluePrincipal(), this.contentId, this.languageId, this.contentVersionVO);
		    //this.contentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(this.contentVersionVO.getId());
		    //this.oldModifiedDateTime = this.contentVersionVO.getModifiedDateTime().getTime();
			oldModifiedDateTime = this.contentVersionVO.getModifiedDateTime().getTime();
		}
		catch(ConstraintException ce)
		{
		    super.contentVersionVO = this.contentVersionVO;
		    throw ce;
		}
		
		currentEditorId = new Integer(1);
		concurrentModification = false;
		
		return "success";
	}

	public String doStandalone() throws Exception
	{
		super.initialize(this.contentVersionId, this.contentId, this.languageId);
		ceb.throwIfNotEmpty();
			
		if(this.attributeName == null)
			this.attributeName = "";
			
		if(this.currentEditorId == null)
			this.currentEditorId = 1;
			
		try
		{
			this.contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());
			this.contentVersionVO = ContentVersionControllerProxy.getController().acUpdate(this.getInfoGluePrincipal(), this.contentId, this.languageId, this.contentVersionVO);
		    this.contentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(this.contentVersionVO.getId());
		}
		catch(ConstraintException ce)
		{
		    super.contentVersionVO = this.contentVersionVO;
		    ce.setResult("inputStandalone");
		    throw ce;
		}
		
		return "standalone";
	}

	public String doStandaloneXML() throws Exception
	{
		try
		{
			String xmlResult = null;
			getResponse().setContentType("text/xml; charset=UTF-8");
	    	getResponse().setHeader("Cache-Control","no-cache"); 
	    	getResponse().setHeader("Pragma","no-cache");
	    	getResponse().setDateHeader ("Expires", 0);
			PrintWriter out = getResponse().getWriter();
			XMLWriter xmlWriter = new XMLWriter(out);
			XStream xStream = new XStream();
			xStream.omitField(contentVersionVO.getClass(),"versionValue");
			
			/*
			System.out.println("contentVersionId:" + this.contentVersionId);
			System.out.println("contentId:" + this.contentId);
			System.out.println("languageId:" + this.languageId);
			System.out.println("this.contentVersionVO:" + this.contentVersionVO);
			*/
			ceb.throwIfNotEmpty();
			
			if(this.attributeName == null)
				this.attributeName = "";
			
			if(this.currentEditorId == null)
				this.currentEditorId = 1;
			
			try
			{
				this.contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());
				this.contentVersionVO = ContentVersionControllerProxy.getController().acUpdate(this.getInfoGluePrincipal(), this.contentId, this.languageId, this.contentVersionVO);
			    this.contentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(this.contentVersionVO.getId());
			    xmlResult = xStream.toXML(this.contentVersionVO);
			}
			catch(ConstraintException ce)
			{
			    super.contentVersionVO = this.contentVersionVO;
			    xmlResult = xStream.toXML(ce);
			}

			//System.out.println("xmlResult:" + xmlResult);
			/*
			 * Output
			 */
			xmlWriter.write(DocumentHelper.parseText(xmlResult));
	        xmlWriter.flush();
		}
		catch (Exception e) 
		{
			logger.warn("Error in UpdateContentVersion.doStandaloneXML: " + e.getMessage());
			if(logger.isInfoEnabled())
				logger.info("Error in UpdateContentVersion.doStandaloneXML: " + e.getMessage(), e);
		}
		
		return NONE;
	}

	public String doSaveAndExit() throws Exception
    {
		doExecute();
						 
		return "saveAndExit";
	}

	public String doSaveAndExitStandalone() throws Exception
	{
		try
		{
			doExecute();
		}
		catch(ConstraintException ce)
		{
		    super.contentVersionVO = this.contentVersionVO;
		    ce.setResult("inputStandalone");
		    throw ce;
		}
						 
		return "saveAndExitStandalone";
	}

	public String doBackground() throws Exception
	{
		doExecute();
						 
		return "background";
	}
	
	public String doXml() throws IOException, SystemException, Bug, DocumentException
	{
		try
		{
			String xmlResult = null;
			getResponse().setContentType("text/xml; charset=UTF-8");
	    	getResponse().setHeader("Cache-Control","no-cache"); 
	    	getResponse().setHeader("Pragma","no-cache");
	    	getResponse().setDateHeader ("Expires", 0);
			PrintWriter out = getResponse().getWriter();
			XMLWriter xmlWriter = new XMLWriter(out);
			XStream xStream = new XStream();
			xStream.omitField(contentVersionVO.getClass(),"versionValue");
			
			// super.initialize(this.contentVersionId, this.contentId, this.languageId);
			
			ContentVersionVO currentContentVersionVO = null;
			ContentVersionVO activeContentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(contentId, languageId);
			
			//System.out.println("activeContentVersionVO:" + activeContentVersionVO);
			//System.out.println("this.contentVersionVO:" + this.contentVersionVO);
			/*
			 * Are we trying to update the active version?
			 */
			if(activeContentVersionVO.getContentVersionId().equals(this.contentVersionVO.getContentVersionId()))
			{
				if(this.contentVersionVO.getId() != null)
				{
					currentContentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(this.contentVersionVO.getId());
				}
				
				if(currentContentVersionVO == null || this.oldModifiedDateTime == currentContentVersionVO.getModifiedDateTime().getTime())
				{
					this.contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());
					
					try
					{
						if(activeContentVersionVO.getStateId().equals(ContentVersionVO.WORKING_STATE))
						{
						    this.contentVersionVO = ContentVersionControllerProxy.getController().acUpdate(this.getInfoGluePrincipal(), this.contentId, this.languageId, this.contentVersionVO);
						    this.contentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(this.contentVersionVO.getId());
						    this.oldModifiedDateTime = this.contentVersionVO.getModifiedDateTime().getTime();
						    xmlResult = xStream.toXML(contentVersionVO);
						}
						else
						{
							xmlResult = "<invalidstate/>";
						}
					}
					catch(ConstraintException ce)
					{
						ce.printStackTrace();
						xmlResult = xStream.toXML(ce);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					    xmlResult = xStream.toXML(e);
					}
				}
				else
				{
					this.contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());
					super.contentVersionVO = this.contentVersionVO;
					concurrentModification = true;
		            xmlResult = "<concurrentmodification/>";
				}
			}
			else
			{
				/*
				 * Not updating active version
				 */
				xmlResult = "<invalidversion/>";
			}
			
			//System.out.println("xmlResult:" + xmlResult);
			/*
			 * Output
			 */
			xmlWriter.write(DocumentHelper.parseText(xmlResult));
	        xmlWriter.flush();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return NONE;
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
        
    public void setVersionValue(java.lang.String versionValue) throws Exception
    {
    	try
    	{
    		SAXReader reader = new SAXReader(false);
    		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Document document = reader.read(new java.io.ByteArrayInputStream(versionValue.getBytes("UTF-8")));
            if(document == null)
            	throw new Exception("Faulty dom... must be corrupt");
    	}
    	catch (Exception e) 
    	{
    		logger.error("Faulty XML from Eclipse plugin.. not accepting", e);
    		throw new Exception("Faulty XML from Eclipse plugin.. not accepting");
		}

    	this.contentVersionVO.setVersionValue(versionValue);
    }
    
	public Integer getCurrentEditorId() 
	{
		return currentEditorId;
	}

	public void setCurrentEditorId(Integer integer) 
	{
		currentEditorId = integer;
	}

	public String getAttributeName()
	{
		return this.attributeName;
	}

	public String getVersionComment()
	{
		return this.contentVersionVO.getVersionComment();
	}
	
	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}

	public long getOldModifiedDateTime() 
	{
		return oldModifiedDateTime;
	}

	public void setOldModifiedDateTime(long oldModifiedDateTime) 
	{
		this.oldModifiedDateTime = oldModifiedDateTime;
	}

	public boolean getConcurrentModification() 
	{
		return concurrentModification;
	}

}
