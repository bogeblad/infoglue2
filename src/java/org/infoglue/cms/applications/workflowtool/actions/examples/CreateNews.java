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

package org.infoglue.cms.applications.workflowtool.actions.examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.TransactionNotInProgressException;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentStateController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.entities.content.Content;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.Language;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.dom.DOMBuilder;
import org.infoglue.cms.util.workflow.CustomWorkflowAction;
import org.infoglue.deliver.controllers.kernel.impl.simple.ExtranetController;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

public class CreateNews implements CustomWorkflowAction
{
	public CreateNews()
	{
	}
	
	
	public void invokeAction(String callerUserName, HttpServletRequest request, Map args, PropertySet ps) throws WorkflowException
	{
	    System.out.println("Inside CreateNews.invokeAction");
	    System.out.println("callerUserName:" + callerUserName);
	    
	    /*
	    Iterator paramsIterator = request.getParameterMap().keySet().iterator();
	    while(paramsIterator.hasNext())
	    {
	        String key = (String)paramsIterator.next();
	        System.out.println("key:" + key);
	        Object value = request.getParameterMap().get(key);
	        System.out.println("value:" + value);
	    }
	    
	    Iterator psIterator = ps.getKeys().iterator();
	    while(psIterator.hasNext())
	    {
	        String key = (String)psIterator.next();
	        System.out.println("key:" + key);
	        //Object value = ps.getObject(key);
	        //System.out.println("value:" + value);
	    }
	    */
	    InfoGluePrincipal infoGluePrincipal = (InfoGluePrincipal)request.getSession().getAttribute("org.infoglue.cms.security.user");
	    if(infoGluePrincipal == null)
	    {
	        try
            {
	            Map arguments = new HashMap();
	            arguments.put("j_username", CmsPropertyHandler.getAnonymousUser());
	            arguments.put("j_password", CmsPropertyHandler.getAnonymousPassword());

	            infoGluePrincipal = (InfoGluePrincipal) ExtranetController.getController().getAuthenticatedPrincipal(arguments);
            } 
	        catch (Exception e2)
            {
                e2.printStackTrace();
            }
	    }
	    
	    Integer parentContentId 		= new Integer(ps.getString("parentContentId"));
	    Integer contentTypeDefinitionId = new Integer(ps.getString("contentTypeDefinitionId"));
	    Integer repositoryId 			= new Integer(ps.getString("repositoryId"));
	    
	    Integer languageId = new Integer(1);
	    if(ps.getString("languageId") != null && !ps.getString("languageId").equalsIgnoreCase(""))
	        languageId = new Integer(ps.getString("languageId"));
	    
	    String name 			= ps.getString("name");
	    String title 			= ps.getString("title");
	    String navigationTitle 	= ps.getString("navigationTitle");
	    String leadIn 			= ps.getString("leadIn");
	    String fullText 		= ps.getString("fullText");
	    
	    System.out.println("name:" + name);
        System.out.println("title:" + title);
        System.out.println("navigationTitle:" + navigationTitle);
        System.out.println("leadIn:" + leadIn);
        System.out.println("fullText:" + fullText);
        
	    ContentVO contentVO = new ContentVO();
	    contentVO.setName(name);
	    contentVO.setIsBranch(new Boolean(false));
	    contentVO.setCreatorName(infoGluePrincipal.getName());
	    
	    Database db = null;

	    try
        {
	        db = CastorDatabaseService.getDatabase();
			ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
	        
			db.begin();
		
            Content newContent = ContentControllerProxy.getContentController().create(db, parentContentId, contentTypeDefinitionId, repositoryId, contentVO);
        
            DOMBuilder domBuilder = new DOMBuilder();
            Document document = domBuilder.createDocument();

            Element rootElement = domBuilder.addElement(document, "article");
            domBuilder.addAttribute(rootElement, "xmlns", "x-schema:ArticleSchema.xml");
            Element attributesElement = domBuilder.addElement(rootElement, "attributes");

            Element titleElement = domBuilder.addElement(attributesElement, "Title");
            domBuilder.addCDATAElement(titleElement, title);

            Element navTitleElement = domBuilder.addElement(attributesElement, "NavigationTitle");
            domBuilder.addCDATAElement(navTitleElement, navigationTitle);

            Element leadElement = domBuilder.addElement(attributesElement, "Leadin");
            domBuilder.addCDATAElement(leadElement, leadIn);

            Element leftColumnElement = domBuilder.addElement(attributesElement, "FullText");
            domBuilder.addCDATAElement(leftColumnElement, fullText);
            
            String versionValue = document.asXML();
            System.out.println("versionValue:" + versionValue);
            
            ContentVersionVO newContentVersionVO = new ContentVersionVO();
            newContentVersionVO.setVersionComment("Generated");
            newContentVersionVO.setVersionModifier(infoGluePrincipal.getName());
            newContentVersionVO.setVersionValue(versionValue);

            Language language = LanguageController.getController().getLanguageWithId(languageId, db);
            ContentVersion newContentVersion = ContentVersionController.getContentVersionController().create(newContent, language, newContentVersionVO, null, db);
            
            db.commit();
            
            db.begin();
            
            List events = new ArrayList();
            ContentVersion publishContentVersion = ContentStateController.changeState(newContentVersion.getContentVersionId(), ContentVersionVO.PUBLISH_STATE, "Auto", true, infoGluePrincipal, newContent.getId(), events);

            db.commit();
        } 
	    catch (Exception e)
        {
	        try
            {
                db.rollback();
            } 
	        catch (TransactionNotInProgressException e1)
            {
	            e1.printStackTrace();
            }
            e.printStackTrace();
            throw new WorkflowException(e);
        }
	    finally
	    {
	        try
            {
                db.close();
            } 
	        catch (PersistenceException e1)
            {
                e1.printStackTrace();
            }
	    }
	}

}
