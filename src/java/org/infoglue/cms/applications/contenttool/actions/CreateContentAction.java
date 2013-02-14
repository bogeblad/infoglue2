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

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.AccessRightController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.InfoGlueSettingsController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.exception.AccessConstraintException;
import org.infoglue.cms.util.AccessConstraintExceptionBuffer;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.sorters.ReflectionComparator;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

/**
 * This action represents the CreateContent Usecase.
 */

public class CreateContentAction extends InfoGlueAbstractAction
{
	private static final long serialVersionUID = 1L;
	
	private Integer parentContentId;
    private Integer contentTypeDefinitionId;
    private Integer repositoryId;
   	private ConstraintExceptionBuffer ceb;
   	private ContentVO contentVO;
   	private ContentVO newContentVO;
   	private String defaultFolderContentTypeName;
   	private String allowedContentTypeNames;
   	private String defaultContentTypeName;
  
  	public CreateContentAction()
	{
		this(new ContentVO());
	}
	
	public CreateContentAction(ContentVO contentVO)
	{
		this.contentVO = contentVO;
		this.ceb = new ConstraintExceptionBuffer();			
	}	

	public void setParentContentId(Integer parentContentId)
	{
		this.parentContentId = parentContentId;
	}

	public Integer getParentContentId()
	{
		return this.parentContentId;
	}

	public void setRepositoryId(Integer repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	public Integer getRepositoryId()
	{
		return this.repositoryId;
	}

	public void setContentTypeDefinitionId(Integer contentTypeDefinitionId)
	{
		this.contentTypeDefinitionId = contentTypeDefinitionId;
	}

	public Integer getContentTypeDefinitionId()
	{
		return this.contentTypeDefinitionId;
	}	
	
    public java.lang.String getName()
    {
        return this.contentVO.getName();
    }

    public String getPublishDateTime()
    {    		
        return new VisualFormatter().formatDate(this.contentVO.getPublishDateTime(), "yyyy-MM-dd HH:mm");
    }
        
    public String getExpireDateTime()
    {
        return new VisualFormatter().formatDate(this.contentVO.getExpireDateTime(), "yyyy-MM-dd HH:mm");
    }

   	public long getPublishDateTimeAsLong()
    {    		
        return this.contentVO.getPublishDateTime().getTime();
    }
        
    public long getExpireDateTimeAsLong()
    {
        return this.contentVO.getExpireDateTime().getTime();
    }
    
	public Boolean getIsBranch()
	{
 		return this.contentVO.getIsBranch();
	}    
            
    public void setName(java.lang.String name)
    {
    	this.contentVO.setName(name);
    }
    	
    public void setPublishDateTime(String publishDateTime)
    {
   		this.contentVO.setPublishDateTime(new VisualFormatter().parseDate(publishDateTime, "yyyy-MM-dd HH:mm"));
    }

    public void setExpireDateTime(String expireDateTime)
    {
       	this.contentVO.setExpireDateTime(new VisualFormatter().parseDate(expireDateTime, "yyyy-MM-dd HH:mm"));
	}
 
    public void setIsBranch(Boolean isBranch)
    {
       	this.contentVO.setIsBranch(isBranch);
    }
     
	public Integer getContentId()
	{
		return newContentVO.getContentId();
	}

    public String getDefaultFolderContentTypeName()
    {
        return defaultFolderContentTypeName;
    }

	/**
	 * This method fetches the list of ContentTypeDefinitions
	 */
	
	public List getContentTypeDefinitions() throws Exception
	{	
	    List contentTypeVOList = null;
	    
	    String protectContentTypes = CmsPropertyHandler.getProtectContentTypes();
	    if(protectContentTypes != null && protectContentTypes.equalsIgnoreCase("true"))
	        contentTypeVOList = ContentTypeDefinitionController.getController().getAuthorizedContentTypeDefinitionVOList(this.getInfoGluePrincipal());
		else
		    contentTypeVOList = ContentTypeDefinitionController.getController().getContentTypeDefinitionVOList();
	    
	    Collections.sort(contentTypeVOList, new ReflectionComparator("name"));
	    
	    return contentTypeVOList;
	}      
    
      
    public String doExecute() throws Exception
    {
		this.contentVO.setCreatorName(this.getInfoGluePrincipal().getName());

    	ceb = this.contentVO.validate();
    	ceb.throwIfNotEmpty();
    			
    	newContentVO = ContentControllerProxy.getController().acCreate(this.getInfoGluePrincipal(), parentContentId, contentTypeDefinitionId, repositoryId, contentVO);
		//newContentVO = ContentController.create(parentContentId, contentTypeDefinitionId, repositoryId, contentVO);
    	
        if ( newContentVO.getIsBranch().booleanValue() )
        {
            Map args = new HashMap();
            args.put("globalKey", "infoglue");
            PropertySet ps = PropertySetManager.getInstance("jdbc", args);
    
            String allowedContentTypeNames  = ps.getString("content_" + this.getParentContentId() + "_allowedContentTypeNames");
            String defaultContentTypeName = ps.getString("content_" + this.getParentContentId() + "_defaultContentTypeName");
            String initialLanguageId  = ps.getString("content_" + this.getParentContentId() + "_initialLanguageId");
                        
            if ( allowedContentTypeNames != null )
            {
                ps.setString("content_" + this.getContentId() + "_allowedContentTypeNames", allowedContentTypeNames );
            }
            if ( defaultContentTypeName != null )
            {
            	ps.setString("content_" + this.getContentId() + "_defaultContentTypeName", defaultContentTypeName );
            }
            if ( initialLanguageId != null )
            {
                ps.setString("content_" + this.getContentId() + "_initialLanguageId", initialLanguageId );
                InfoGlueSettingsController.addInitialLanguageCache(this.getParentContentId(), initialLanguageId);
            }
            
        }        
    	return "success";
    }

    public String doXML() throws Exception
    {
    	try
    	{
    		
		this.contentVO.setCreatorName(this.getInfoGluePrincipal().getName());

    	ceb = this.contentVO.validate();
    	ceb.throwIfNotEmpty();
    			
    	newContentVO = ContentControllerProxy.getController().acCreate(this.getInfoGluePrincipal(), parentContentId, contentTypeDefinitionId, repositoryId, contentVO);

		getResponse().setContentType("text/xml");
		PrintWriter out = getResponse().getWriter();
		out.println("" + newContentVO.getId());
    	}
    	catch (Exception e) 
    	{
    		e.printStackTrace();
		}
    	
    	return NONE;
    }

	public String doBindingView() throws Exception
	{
		doExecute();
		return "bindingView";
	}
	
	public String doTreeView() throws Exception
	{
		doExecute();
		return "treeView";
	}

    public String doInput() throws Exception
    {
		AccessConstraintExceptionBuffer ceb = new AccessConstraintExceptionBuffer();
		
		Integer protectedContentId = ContentControllerProxy.getController().getProtectedContentId(parentContentId);
		if(protectedContentId != null && !AccessRightController.getController().getIsPrincipalAuthorized(this.getInfoGluePrincipal(), "Content.Create", protectedContentId.toString()))
			ceb.add(new AccessConstraintException("Content.contentId", "1002"));
		
		Map args = new HashMap();
	    args.put("globalKey", "infoglue");
	    PropertySet ps = PropertySetManager.getInstance("jdbc", args);

		if(this.getIsBranch().booleanValue())
		{
		    this.defaultFolderContentTypeName = ps.getString("repository_" + this.getRepositoryId() + "_defaultFolderContentTypeName");
		}
		else
		{
		    this.defaultContentTypeName = ps.getString("content_" + this.parentContentId + "_defaultContentTypeName");
		}
        if ( ps.exists( "content_" + this.parentContentId + "_allowedContentTypeNames" ) )
        {
            this.allowedContentTypeNames = ps.getString("content_" + this.parentContentId + "_allowedContentTypeNames");
        }
		ceb.throwIfNotEmpty();
		
		return "input";
    }
        
    public String getAllowedContentTypeNames()
    {
        return allowedContentTypeNames;
    }
    
    public String getDefaultContentTypeName()
    {
        return defaultContentTypeName;
    }
}
