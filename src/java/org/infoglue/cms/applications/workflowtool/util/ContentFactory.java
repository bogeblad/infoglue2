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
package org.infoglue.cms.applications.workflowtool.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.controllers.kernel.impl.simple.CategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentCategoryController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.entities.content.Content;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.CategoryVO;
import org.infoglue.cms.entities.management.ContentTypeAttribute;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.dom.DOMBuilder;

public class ContentFactory 
{
	/**
	 * 
	 */
    private final static Logger logger = Logger.getLogger(ContentFactory.class.getName());
	
	
	/**
	 * 
	 */
	private final ContentTypeDefinitionVO contentTypeDefinitionVO;
	
	/**
	 * 
	 */
	private final ContentValues contentValues;
	
	/**
	 * 
	 */
	private final ContentVersionValues contentVersionValues;
	
	/**
	 * 
	 */
	private final InfoGluePrincipal principal;

	/**
	 * 
	 */
	private final LanguageVO language;

	
	
	/**
	 * 
	 */
	public ContentFactory(final ContentTypeDefinitionVO contentTypeDefinitionVO, final ContentValues contentValues, final ContentVersionValues contentVersionValues, final InfoGluePrincipal principal, final LanguageVO language) 
	{
		this.contentTypeDefinitionVO = contentTypeDefinitionVO;
		this.contentValues           = contentValues;
		this.contentVersionValues    = contentVersionValues;
		this.principal               = principal;
		this.language                = language;
	}
	
	/**
	 * 
	 */
	public ContentVO create(final ContentVO parentContent, final Map categories, final Database db) throws ConstraintException 
	{
		ContentVO contentVO               = createContentVO();
		Document contentVersionDocument   = buildContentVersionDocument();
		ContentVersionVO contentVersionVO = createContentVersionVO(contentVersionDocument.asXML());
		
		if(validate(contentVO, contentVersionVO).isEmpty())
		{
			return createContent(parentContent, contentVO, contentVersionVO, categories, db);
		}
		return null;
	}

	/**
	 * 
	 */
	public ContentVO update(final ContentVO contentVO, final Map categories, final Database db) throws ConstraintException 
	{
		populateContentVO(contentVO);
		Document contentVersionDocument   = buildContentVersionDocument();
		ContentVersionVO contentVersionVO = createContentVersionVO(contentVersionDocument.asXML());

		if(validate(contentVO, contentVersionVO).isEmpty())
		{
			return updateContent(contentVO, contentVersionVO, categories, db);
		}
		return null;
	}

	/**
	 * 
	 */
	public ConstraintExceptionBuffer validate() 
	{
		final ContentVO contentVO               = createContentVO();
		final Document contentVersionDocument   = buildContentVersionDocument();
		final ContentVersionVO contentVersionVO = createContentVersionVO(contentVersionDocument.asXML());
		return validate(contentVO, contentVersionVO);
	}
	
	/**
	 * 
	 */
	private ConstraintExceptionBuffer validate(final ContentVO contentVO, final ContentVersionVO contentVersionVO) 
	{
		final ConstraintExceptionBuffer ceb = contentVO.validate();
		ceb.add(contentVersionVO.validateAdvanced(contentTypeDefinitionVO));
		return ceb;
	}

	/**
	 * 
	 */
	private ContentVO createContent(final ContentVO parentContent, final ContentVO contentVO, final ContentVersionVO contentVersionVO, final Map categories, final Database db) 
	{
	    try 
	    {
			final Content content = ContentControllerProxy.getContentController().create(db, parentContent.getId(), contentTypeDefinitionVO.getId(), parentContent.getRepositoryId(), contentVO);
			final ContentVersion newContentVersion = ContentVersionController.getContentVersionController().create(content.getId(), language.getId(), contentVersionVO, null, db);
			createCategories(db, newContentVersion, categories);
			return content.getValueObject();
	    } 
	    catch(Exception e) 
	    {
			logger.warn(e);
	    }
		return null;
	}
	
	/**
	 * 
	 */
	private ContentVO updateContent(final ContentVO contentVO, final ContentVersionVO contentVersionVO, final Map categories, final Database db) 
	{
		try 
		{
			final Content content = ContentControllerProxy.getContentController().getContentWithId(contentVO.getId(), db);
			content.setValueObject(contentVO);
			
			final ContentVersion contentVersion = ContentVersionController.getContentVersionController().getLatestActiveContentVersion(content.getId(), language.getId(), db);
			contentVersion.getValueObject().setVersionValue(contentVersionVO.getVersionValue());
			
			deleteCategories(db, contentVersion);
			createCategories(db, contentVersion, categories);
			return content.getValueObject();
	    } 
		catch(Exception e) 
		{
			logger.warn(e);
	    }
		return null;
	}

	/**
	 * 
	 */
	private void deleteCategories(final Database db, final ContentVersion contentVersion) throws Exception 
	{
		ContentCategoryController.getController().deleteByContentVersion(contentVersion.getId(), db);
	}

	/**
	 * 
	 */
	private void createCategories(final Database db, final ContentVersion contentVersion, final Map categorieVOs) throws Exception 
	{
		if(categorieVOs != null)
		{
			for(Iterator i=categorieVOs.keySet().iterator(); i.hasNext(); ) 
			{
				String attributeName = (String) i.next();
				List categoryVOs     = (List) categorieVOs.get(attributeName);
				createCategory(db, contentVersion, attributeName, categoryVOs);
			}
		}
	}
	
	/**
	 * 
	 */
	private void createCategory(final Database db, final ContentVersion contentVersion, final String attributeName, final List categoryVOs) throws Exception 
	{
		final List categories = categoryVOListToCategoryList(db, categoryVOs);
		ContentCategoryController.getController().create(categories, contentVersion, attributeName, db);
	}
	
	/**
	 * 
	 */
	private List categoryVOListToCategoryList(final Database db, final List categoryVOList) throws Exception 
	{
		final List result = new ArrayList();
		for(Iterator i=categoryVOList.iterator(); i.hasNext(); ) 
		{
			CategoryVO categoryVO = (CategoryVO) i.next();
			result.add(CategoryController.getController().findById(categoryVO.getCategoryId(), db));
		}
		return result;
	}
	
	
	/**
	 * 
	 */
	private ContentVO createContentVO() 
	{
		final ContentVO contentVO = new ContentVO();
		populateContentVO(contentVO);
		return contentVO;
	}

	/**
	 * 
	 */
	private ContentVO populateContentVO(final ContentVO contentVO) 
	{
		contentVO.setName(contentValues.getName());
		contentVO.setPublishDateTime(contentValues.getPublishDateTime());
		contentVO.setExpireDateTime(contentValues.getExpireDateTime());
		contentVO.setIsBranch(Boolean.FALSE);
		contentVO.setCreatorName(principal.getName());
		return contentVO;
	}
	
	/**
	 * 
	 */
	private ContentVersionVO createContentVersionVO(final String versionValue) 
	{
		ContentVersionVO contentVersion = new ContentVersionVO();
		contentVersion.setVersionComment("TODO");
		contentVersion.setVersionModifier(principal.getName());
		contentVersion.setVersionValue(versionValue);
		return contentVersion;
	}
	
	/**
	 * 
	 */
	private Document buildContentVersionDocument() 
	{
		final DOMBuilder builder  = new DOMBuilder();
		final Document document   = builder.createDocument();
		final Element rootElement = builder.addElement(document, "TODO");
		builder.addAttribute(rootElement, "xmlns", "x-schema:TODOSchema.xml");
		final Element attributesRoot =  builder.addElement(rootElement, "attributes");
		
		final List attributes = getContentTypeAttributes();
		buildAttributes(builder, attributesRoot, attributes);
		
		return document;
	}

	/**
	 * 
	 */
	private List getContentTypeAttributes() 
	{
		return ContentTypeDefinitionController.getController().getContentTypeAttributes(contentTypeDefinitionVO.getSchemaValue());
	}
	
	/**
	 * 
	 */
	private void buildAttributes(final DOMBuilder domBuilder, final Element parentElement, final List attributes) 
	{	
		for(Iterator i=attributes.iterator(); i.hasNext(); ) 
		{
			final ContentTypeAttribute attribute = (ContentTypeAttribute) i.next();
			final Element element = domBuilder.addElement(parentElement, attribute.getName());
			final String value = contentVersionValues.get(attribute.getName());
			if(value != null)
			{
				domBuilder.addCDATAElement(element, value);
			}
		}		
	}
}
