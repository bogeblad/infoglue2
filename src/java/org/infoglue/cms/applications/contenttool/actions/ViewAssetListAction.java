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

/**
 * Work in progress
 * @author Stefan Sik
 */
package org.infoglue.cms.applications.contenttool.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.CategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentCategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentStateController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.DigitalAssetController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.RepositoryController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.util.MathHelper;


public class ViewAssetListAction extends InfoGlueAbstractAction
{
	private static final long serialVersionUID = 1L;
	
	private static CategoryController categoryController = CategoryController.getController();
	private static ContentCategoryController contentCategoryController = ContentCategoryController.getController();

	private Integer newContentId = new Integer(0);
	
	private Integer digitalAssetId = null;
	public ContentTypeDefinitionVO contentTypeDefinitionVO;
	public List availableLanguages = null;
	
	private Integer languageId;
	private Integer repositoryId;
	private Integer currentEditorId;
	private String attributeName = "";
	private String textAreaId = "";
	private boolean forceWorkingChange = false;
			
    private ContentVO contentVO;
    protected ContentVersionVO contentVersionVO;
	public List attributes = null;

	private List repositories;
	
	//This is used for showing navigationdata
	private Integer siteNodeId;

	private Integer oldContentId 	= null;
	private String assetKey 		= null;
	private boolean treatAsLink    = false;
	
	private Map WYSIWYGProperties = null;
	
	private String closeOnLoad 		= "false";
	private String publishOnLoad	= "false";

	private boolean concurrentModification = false;
	private long oldModifiedDateTime = -1;


    private HashMap contentMap = new HashMap();
    
    public ViewAssetListAction()
    {
        this(new ContentVO(), new ContentVersionVO());
    }
    
    public ViewAssetListAction(ContentVO contentVO, ContentVersionVO contentVersionVO)
    {
        this.contentVO = contentVO;
        this.contentVersionVO = contentVersionVO;
    }

    protected void initialize(Integer contentVersionId, Integer contentId, Integer languageId) throws Exception
    {
        initialize(contentVersionId, contentId, languageId, false);
    }

    private void createContentIdList(ContentVO parent) throws ConstraintException, SystemException
    {
        contentMap.put(parent.getContentId(), parent.getName());
        
        List children = ContentControllerProxy.getController().getContentChildrenVOList(parent.getContentId());
        for(Iterator i=children.iterator();i.hasNext();)
        {
            ContentVO cvo = (ContentVO) i.next();
            createContentIdList(cvo);
        }
    }
    
    protected void initialize(Integer contentVersionId, Integer contentId, Integer languageId, boolean fallBackToMasterLanguage) throws Exception
    {
        this.contentVO = ContentControllerProxy.getController().getACContentVOWithId(this.getInfoGluePrincipal(), contentId);
        createContentIdList(this.contentVO);
		    
        //this.contentVO = ContentController.getContentVOWithId(contentId);
        this.contentTypeDefinitionVO = ContentController.getContentController().getContentTypeDefinition(contentId);
        this.availableLanguages = ContentController.getContentController().getRepositoryLanguages(contentId);
        
        if(contentVersionId == null)
		{	
			//this.contentVersionVO = ContentVersionControllerProxy.getController().getACLatestActiveContentVersionVO(this.getInfoGluePrincipal(), contentId, languageId);
			//this.contentVersionVO = ContentVersionController.getLatestActiveContentVersionVO(contentId, languageId);
			this.contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(contentId, languageId);
			if(this.contentVersionVO == null && fallBackToMasterLanguage)
			{
			    //getLogger().info("repositoryId:" + repositoryId);
			    Integer usedRepositoryId = this.repositoryId;
			    if(this.repositoryId == null && this.contentVO != null)
			        usedRepositoryId = this.contentVO.getRepositoryId();
			    
			    LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(usedRepositoryId);
			    //getLogger().info("MasterLanguage: " + masterLanguageVO);
			    this.contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(contentId, masterLanguageVO.getId());
			}
			
			if(this.contentVersionVO != null)
				contentVersionId = contentVersionVO.getContentVersionId();
		}

        if(contentVersionId != null)	
			this.contentVersionVO = ContentVersionControllerProxy.getController().getACContentVersionVOWithId(this.getInfoGluePrincipal(), contentVersionId);    		 	
    		//this.contentVersionVO = ContentVersionController.getContentVersionVOWithId(contentVersionId);    		 	

		if(this.forceWorkingChange && contentVersionVO != null && !contentVersionVO.getStateId().equals(ContentVersionVO.WORKING_STATE))
		{
		    ContentVersion contentVersion = ContentStateController.changeState(contentVersionVO.getContentVersionId(), ContentVersionVO.WORKING_STATE, "Edit on sight", false, this.getInfoGluePrincipal(), this.getContentId(), new ArrayList());
		    contentVersionId = contentVersion.getContentVersionId();
		    contentVersionVO = contentVersion.getValueObject();
		}

        if(this.contentTypeDefinitionVO != null)
        {
            this.contentTypeDefinitionVO = ContentTypeDefinitionController.getController().validateAndUpdateContentType(this.contentTypeDefinitionVO);
            this.attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(this.contentTypeDefinitionVO.getSchemaValue());
        }
    } 

    public String doExecute() throws Exception
    {
        if(getContentId() != null && getContentId().intValue() != -1)
            this.initialize(getContentVersionId(), getContentId(), this.languageId);
        
   	    return "success";
    }
    
    public String doBrowser() throws Exception
    {
        if(this.oldContentId != null)
        {
            this.contentVO = ContentControllerProxy.getController().getACContentVOWithId(this.getInfoGluePrincipal(), getOldContentId());
        }
        else
        {
            if(getContentId() != null && getContentId().intValue() != -1)
                this.contentVO = ContentControllerProxy.getController().getACContentVOWithId(this.getInfoGluePrincipal(), getContentId());
        }
        
        this.repositories = RepositoryController.getController().getAuthorizedRepositoryVOList(this.getInfoGluePrincipal(), true);

        return "browser";
    }
    
    public String getContentPath(Integer contentId) throws ConstraintException, SystemException, Bug, Exception
    {
        ContentVO contentVO = ContentControllerProxy.getController().getACContentVOWithId(this.getInfoGluePrincipal(), contentId);
        StringBuffer ret = new StringBuffer();
        // ret.add(0, contentVO);

        while (contentVO.getParentContentId() != null)
        {
            try {
                contentVO = ContentControllerProxy.getController().getContentVOWithId(contentVO.getParentContentId());
            } catch (SystemException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Bug e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ret.insert(0, "" + contentVO.getContentId() + ",");
        }
        ret.append("" + contentId);
        return ret.toString();
    }
    

    public MathHelper getMathHelper()
    {
        return new MathHelper();
    }
    
    public java.lang.Integer getContentVersionId()
    {
        if(this.contentVersionVO != null)
            return this.contentVersionVO.getContentVersionId();
        else 
            return null;
    }
    
    public void setContentVersionId(java.lang.Integer contentVersionId)
    {
        this.contentVersionVO.setContentVersionId(contentVersionId);
    }
        
    public java.lang.Integer getContentId()
    {
        return this.contentVO.getContentId();
    }
        
    public void setContentId(java.lang.Integer contentId)
    {
	    this.contentVO.setContentId(contentId);
    }
    
    public java.lang.Integer getContentTypeDefinitionId()
    {
        return this.contentTypeDefinitionVO.getContentTypeDefinitionId();
    }

    public String getContentTypeDefinitionName()
    {
        return this.contentTypeDefinitionVO.getName();
    }
            
   	public void setLanguageId(Integer languageId)
	{
   	    this.languageId = languageId;
	}

    public java.lang.Integer getLanguageId()
    {
        return this.languageId;
    }
	
	public void setStateId(Integer stateId)
	{
	    if(this.contentVersionVO != null)
	        this.contentVersionVO.setStateId(stateId);
	}

	public void setVersionComment(String versionComment)
	{
	    if(this.contentVersionVO != null)
	        this.contentVersionVO.setVersionComment(versionComment);
	}

	public void setDigitalAssetId(Integer digitalAssetId)
	{
		this.digitalAssetId = digitalAssetId;
	}
	
	public String getVersionComment()
	{
		return this.contentVersionVO.getVersionComment();
	}
	
	public Integer getStateId()
	{
		return this.contentVersionVO.getStateId();
	}

	public Boolean getIsActive()
	{
		return this.contentVersionVO.getIsActive();
	}
            
    public String getName()
    {
        return this.contentVO.getName();
    }

    public java.lang.Integer getRepositoryId()
    {
        if(this.contentVO != null && this.contentVO.getRepositoryId() != null)
            return this.contentVO.getRepositoryId();
        else
            return this.repositoryId;
    }

	public List getAvailableLanguages()
	{
		return this.availableLanguages;
	}	

	/**
	 * Returns a list of digital assets available for this content version.
	 */
	
	public List getDigitalAssets()
	{
		List digitalAssets = null;
		
		try
		{
			if(this.contentVersionVO != null && this.contentVersionVO.getContentVersionId() != null)
	       	{
	       		digitalAssets = DigitalAssetController.getDigitalAssetVOList(this.contentVersionVO.getContentVersionId());
	       	}
		}
		catch(Exception e)
		{
			getLogger().warn("We could not fetch the list of digitalAssets: " + e.getMessage(), e);
		}
		
		return digitalAssets;
	}	
	
	/**
	 * Returns a list of digital assets available for this content version and all the child versions.
	 */
	public List getInheritedDigitalAssets()
	{
		List digitalAssets = new ArrayList();
		
		try
		{
            for(Iterator i = contentMap.keySet().iterator();i.hasNext();)
            {
                Integer cid = (Integer) i.next();
                DigitalAssetCollection collection = new DigitalAssetCollection(cid, (String) contentMap.get(cid));
                collection.setContentPath(getContentPath(cid));
                collection.getAssets().addAll(DigitalAssetController.getDigitalAssetVOList(cid, this.languageId, true));
                digitalAssets.add(collection);
            }
		}
		catch(Exception e)
		{
			getLogger().warn("We could not fetch the list of digitalAssets: " + e.getMessage(), e);
		}
		
		return digitalAssets;
	}	


	/**
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetUrl(Integer digitalAssetId) throws Exception
	{
		String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetUrl(digitalAssetId);
		}
		catch(Exception e)
		{
			getLogger().warn("We could not get the url of the digitalAsset: " + e.getMessage(), e);
			imageHref = e.getMessage();
		}
		
		return imageHref;
	}
	
	
	/**
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetThumbnailUrl(Integer digitalAssetId) throws Exception
	{
		String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetThumbnailUrl(digitalAssetId);
		}
		catch(Exception e)
		{
			getLogger().warn("We could not get the url of the thumbnail: " + e.getMessage(), e);
			imageHref = e.getMessage();
		}
		
		return imageHref;
	}

	
	/**
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetUrl(Integer contentId, Integer languageId) throws Exception
	{
		String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetUrl(contentId, languageId);
		}
		catch(Exception e)
		{
			getLogger().warn("We could not get the url of the digitalAsset: " + e.getMessage(), e);
			imageHref = e.getMessage();
		}
		
		return imageHref;
	}
	
	
	/**
	 * This method fetches the blob from the database and saves it on the disk.
	 * Then it returnes a url for it
	 */
	
	public String getDigitalAssetThumbnailUrl(Integer contentId, Integer languageId) throws Exception
	{
		String imageHref = null;
		try
		{
       		imageHref = DigitalAssetController.getDigitalAssetThumbnailUrl(contentId, languageId);
		}
		catch(Exception e)
		{
			getLogger().warn("We could not get the url of the thumbnail: " + e.getMessage(), e);
			imageHref = e.getMessage();
		}
		
		return imageHref;
	}
	

	
	
	/**
	 * This method returns the attributes in the content type definition for generation.
	 */
	
	public List getContentTypeAttributes()
	{   		
		return this.attributes;
	}

	public ContentVersionVO getContentVersionVO()
	{
		return contentVersionVO;
	}

	

	/**
	 * Return the listing of Category attributes for this type of Content
	 */
	public List getDefinedCategoryKeys()
	{
		try
		{
			if(contentTypeDefinitionVO != null)
				return ContentTypeDefinitionController.getController().getDefinedCategoryKeys(contentTypeDefinitionVO.getSchemaValue());
		}
		catch(Exception e)
		{
			getLogger().warn("We could not fetch the list of defined category keys: " + e.getMessage(), e);
		}

		return Collections.EMPTY_LIST;
	}

	/**
	 * Returns the Category tree for the given Category id.
	 * @param categoryId The base Category
	 * @return A list of all Children (and their children, etc)
	 */
	public List getAvailableCategories(Integer categoryId)
	{
		try
		{	
		    String protectCategories = CmsPropertyHandler.getProtectCategories();
		    if(protectCategories != null && protectCategories.equalsIgnoreCase("true"))
		        return categoryController.getAuthorizedActiveChildren(categoryId, this.getInfoGluePrincipal());
			else
			    return categoryController.findAllActiveChildren(categoryId);
		}
		catch(Exception e)
		{
			getLogger().warn("We could not fetch the list of categories: " + e.getMessage(), e);
		}

		return Collections.EMPTY_LIST;
	}

	/**
	 * Returns all current Category relationships for th specified attrbiute name
	 * @param attribute
	 * @return
	 */
	public List getRelatedCategories(String attribute)
	{
		try
		{
			if(this.contentVersionVO != null && this.contentVersionVO.getContentVersionId() != null)
				return contentCategoryController.findByContentVersionAttribute(attribute, contentVersionVO.getContentVersionId());
		}
		catch(Exception e)
		{
			getLogger().warn("We could not fetch the list of defined category keys: " + e.getMessage(), e);
		}

		return Collections.EMPTY_LIST;
	}
	
	public ContentVersionVO getMasterContentVersionVO(Integer contentId, Integer repositoryId) throws SystemException, Exception
	{
	    LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(repositoryId);
	    return ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(contentId, masterLanguageVO.getId());
	}

	public ContentVersionVO getLatestContentVersionVO(Integer contentId, Integer languageId) throws SystemException, Exception
	{
	    return ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(contentId, languageId);
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

	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}

	public String getTextAreaId()
	{
		return this.textAreaId;
	}

	public void setTextAreaId(String textAreaId)
	{
		this.textAreaId = textAreaId;
	}

	public Integer getSiteNodeId()
	{
		return this.siteNodeId;
	}

	public void setSiteNodeId(Integer siteNodeId)
	{
		this.siteNodeId = siteNodeId;
	}

    public void setRepositoryId(Integer repositoryId)
    {
        this.repositoryId = repositoryId;
    }
    
    public List getRepositories()
    {
        return repositories;
    }
    
    public String getAssetKey()
    {
        return assetKey;
    }
    
    public void setAssetKey(String assetKey)
    {
        this.assetKey = assetKey;
    }
    
    public Integer getOldContentId()
    {
        return oldContentId;
    }
    
    public void setOldContentId(Integer oldContentId)
    {
        this.oldContentId = oldContentId;
    }
    
    public boolean getTreatAsLink()
    {
        return treatAsLink;
    }
    
    public void setTreatAsLink(boolean treatAsLink)
    {
        this.treatAsLink = treatAsLink;
    }
    
	public ContentVO getContentVO() 
	{
		return contentVO;
	}
	
    public String getCloseOnLoad()
    {
        return closeOnLoad;
    }
    
    public void setCloseOnLoad(String closeOnLoad)
    {
        this.closeOnLoad = closeOnLoad;
    }
    
    public Integer getNewContentId()
    {
        return newContentId;
    }
    
    public void setNewContentId(Integer newContentId)
    {
        this.newContentId = newContentId;
    }
    
    public void setContentVersionVO(ContentVersionVO contentVersionVO)
    {
        this.contentVersionVO = contentVersionVO;
    }
    
    public ContentTypeDefinitionVO getContentTypeDefinitionVO()
    {
        return contentTypeDefinitionVO;
    }

	public boolean getConcurrentModification() 
	{
		return concurrentModification;
	}

	public void setConcurrentModification(boolean concurrentModification) 
	{
		this.concurrentModification = concurrentModification;
	}

	public long getOldModifiedDateTime() 
	{
		return oldModifiedDateTime;
	}

	public void setOldModifiedDateTime(long oldModifiedDateTime) 
	{
		this.oldModifiedDateTime = oldModifiedDateTime;
	}
    
    public void setForceWorkingChange(boolean forceWorkingChange)
    {
        this.forceWorkingChange = forceWorkingChange;
    }

	public String getPublishOnLoad() {
		return publishOnLoad;
	}

	public void setPublishOnLoad(String publishOnLoad) {
		this.publishOnLoad = publishOnLoad;
	}
    
    public class DigitalAssetCollection
    {
        List assets = new ArrayList();
        String contentPath = null;
        Integer contentId = null;
        String contentName = null;
        public DigitalAssetCollection(Integer contentId, String contentName)
        {
            this.contentId = contentId;
            this.contentName = contentName;
        }
        public List getAssets()
        {
            return assets;
        }
        public void setAssets(List assets)
        {
            this.assets = assets;
        }
        public Integer getContentId()
        {
            return contentId;
        }
        public void setContentId(Integer contentId)
        {
            this.contentId = contentId;
        }
        public String getContentName()
        {
            return contentName;
        }
        public void setContentName(String contentName)
        {
            this.contentName = contentName;
        }
        public String getContentPath()
        {
            return contentPath;
        }
        public void setContentPath(String contentPath)
        {
            this.contentPath = contentPath;
        }
        
    }
}
