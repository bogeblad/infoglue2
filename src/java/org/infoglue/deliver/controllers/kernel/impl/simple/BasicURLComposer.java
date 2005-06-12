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

package org.infoglue.deliver.controllers.kernel.impl.simple;

import org.exolab.castor.jdo.Database;
import org.infoglue.cms.controllers.kernel.impl.simple.SiteNodeController;
import org.infoglue.cms.entities.structure.SiteNode;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsLogger;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.applications.databeans.DeliveryContext;
import org.infoglue.deliver.applications.filters.FilterConstants;
import org.infoglue.deliver.controllers.kernel.URLComposer;

import webwork.action.ActionContext;

/**
 * Created by IntelliJ IDEA.
 * User: lbj
 * Date: 22-01-2004
 * Time: 16:41:17
 * To change this template use Options | File Templates.
 */
public class BasicURLComposer extends URLComposer
{

    public BasicURLComposer()
    {
    }


    public String composeDigitalAssetUrl(String dnsName, String filename)
    {
        String disableEmptyUrls = CmsPropertyHandler.getProperty("disableEmptyUrls");
        if((filename == null || filename.equals("")) && (disableEmptyUrls == null || disableEmptyUrls.equalsIgnoreCase("no")))
            return "";
            
        String assetUrl = "";
        
        String enableNiceURI = CmsPropertyHandler.getProperty("enableNiceURI");
        if(enableNiceURI == null || enableNiceURI.equalsIgnoreCase(""))
        	enableNiceURI = "false";

        String useDNSNameInUrls = CmsPropertyHandler.getProperty("useDNSNameInURI");
        if(useDNSNameInUrls == null || useDNSNameInUrls.equalsIgnoreCase(""))
            useDNSNameInUrls = "false";

        if(enableNiceURI.equalsIgnoreCase("true") || useDNSNameInUrls.equalsIgnoreCase("false"))
        {
	        StringBuffer sb = new StringBuffer(256);
	        
	        String servletContext = CmsPropertyHandler.getProperty(FilterConstants.CMS_PROPERTY_SERVLET_CONTEXT);
	        String digitalAssetPath = CmsPropertyHandler.getProperty("digitalAssetBaseUrl");
	        if (!digitalAssetPath.startsWith("/"))
	        	digitalAssetPath = "/" + digitalAssetPath;
	        
	        //CmsLogger.logInfo("servletContext:" + servletContext);
	        //CmsLogger.logInfo("digitalAssetPath:" + digitalAssetPath);
	
	        if(digitalAssetPath.indexOf(servletContext) == -1)
	        	sb.append(servletContext);
	        
	        sb.append(digitalAssetPath);
	     
	        sb.append("/").append(filename);
	        
	        //CmsLogger.logInfo("sb:" + sb);
	        
	        assetUrl = sb.toString();
        }
        else
        {
            assetUrl = dnsName + "/" + CmsPropertyHandler.getProperty("digitalAssetBaseUrl") + "/" + filename;
        }
        
        return assetUrl;
    }
    
    public String composePageUrl(Database db, InfoGluePrincipal infoGluePrincipal, Integer siteNodeId, Integer languageId, Integer contentId, DeliveryContext deliveryContext) throws SystemException
    {
        /*
        String disableEmptyUrls = CmsPropertyHandler.getProperty("disableEmptyUrls");
        if(filename == null || filename.equals("") && disableEmptyUrls == null || disableEmptyUrls.equalsIgnoreCase("no"))
            return "";
        */
        
    	String enableNiceURI = CmsPropertyHandler.getProperty("enableNiceURI");
        if(enableNiceURI == null || enableNiceURI.equalsIgnoreCase(""))
        	enableNiceURI = "false";
        
        String useDNSNameInUrls = CmsPropertyHandler.getProperty("useDNSNameInURI");
        if(useDNSNameInUrls == null || useDNSNameInUrls.equalsIgnoreCase(""))
            useDNSNameInUrls = "false";

        if(enableNiceURI.equalsIgnoreCase("true"))
        {
	        StringBuffer sb = new StringBuffer(26);
	        
	        sb.append(CmsPropertyHandler.getProperty(FilterConstants.CMS_PROPERTY_SERVLET_CONTEXT));
	        try 
			{
	            sb.append(NodeDeliveryController.getNodeDeliveryController(siteNodeId, languageId, contentId).getPageNavigationPath(db, infoGluePrincipal, siteNodeId, languageId, contentId, deliveryContext));
	            if (contentId != null && contentId.intValue() != -1)
	                sb.append("?contentId=").append(String.valueOf(contentId));
	     
	            return (!sb.toString().equals("") ? sb.toString() : "/");
	        } 
	        catch (Exception e) 
			{
	            e.printStackTrace();
	        }
	        return null;
        }
        else
        {           
            if(!useDNSNameInUrls.equalsIgnoreCase("false"))
            {
	    		if(siteNodeId == null)
	    			siteNodeId = new Integer(-1);
	
	    		if(languageId == null)
	    			languageId = new Integer(-1);
	
	    		if(contentId == null)
	    			contentId = new Integer(-1);
	
	            String arguments = "siteNodeId=" + siteNodeId + getRequestArgumentDelimiter() + "languageId=" + languageId + getRequestArgumentDelimiter() + "contentId=" + contentId;

	            SiteNode siteNode = SiteNodeController.getSiteNodeWithId(siteNodeId, db, true);
	    		String dnsName = CmsPropertyHandler.getProperty("webServerAddress");
	    		if(siteNode != null && siteNode.getRepository().getDnsName() != null && !siteNode.getRepository().getDnsName().equals(""))
	    			dnsName = siteNode.getRepository().getDnsName();

	            String url = dnsName + "/" + CmsPropertyHandler.getProperty("applicationBaseAction") + "?" + arguments;

				if(deliveryContext.getHttpServletRequest().getRequestURI().indexOf("!renderDecoratedPage") > -1)
				{
		            String componentRendererUrl = CmsPropertyHandler.getProperty("componentRendererUrl");
					if(componentRendererUrl.endsWith("/"))
					    componentRendererUrl += "/";
					
					url = componentRendererUrl + CmsPropertyHandler.getProperty("componentRendererAction") + "?" + arguments;
				}
				
	            //CmsLogger.logInfo("url:" + url);
	            return url;
            }
            else
            {
                String servletContext = CmsPropertyHandler.getProperty(FilterConstants.CMS_PROPERTY_SERVLET_CONTEXT);
    	        
                if(siteNodeId == null)
	    			siteNodeId = new Integer(-1);
	
	    		if(languageId == null)
	    			languageId = new Integer(-1);
	
	    		if(contentId == null)
	    			contentId = new Integer(-1);
	
	            String arguments = "siteNodeId=" + siteNodeId + getRequestArgumentDelimiter() + "languageId=" + languageId + getRequestArgumentDelimiter() + "contentId=" + contentId;
	            CmsLogger.logInfo("servletContext:" + servletContext);
	            String url = servletContext + "/" + CmsPropertyHandler.getProperty("applicationBaseAction") + "?" + arguments;
	            
				if(deliveryContext.getHttpServletRequest().getRequestURI().indexOf("!renderDecoratedPage") > -1)
				{
		            //String componentRendererUrl = CmsPropertyHandler.getProperty("componentRendererUrl");
				    //if(componentRendererUrl.endsWith("/"))
				    //   componentRendererUrl += "/";
					
					url = servletContext + "/" + CmsPropertyHandler.getProperty("componentRendererAction") + "?" + arguments;
				}

	            CmsLogger.logInfo("url:" + url);
	            //CmsLogger.logInfo("url:" + url);
	            return url;            
            }
        }
    }

    public String composePageUrlAfterLanguageChange(Database db, InfoGluePrincipal infoGluePrincipal, Integer siteNodeId, Integer languageId, Integer contentId, DeliveryContext deliveryContext) throws SystemException
    {
        String pageUrl = composePageUrl(db, infoGluePrincipal, siteNodeId, languageId, contentId, deliveryContext);

        String enableNiceURI = CmsPropertyHandler.getProperty("enableNiceURI");
        if(enableNiceURI == null || enableNiceURI.equalsIgnoreCase(""))
        	enableNiceURI = "false";
        
        if(enableNiceURI.equalsIgnoreCase("true"))
        {
	        if (pageUrl.indexOf("?") == -1) 
	        {
	            pageUrl += "?languageId=" + String.valueOf(languageId);
	        } 
	        else 
	        {
	            pageUrl += getRequestArgumentDelimiter() + "languageId=" + String.valueOf(languageId);
	        }
        }
        
        return pageUrl;
    }

    private String getRequestArgumentDelimiter()
    {
        String requestArgumentDelimiter = CmsPropertyHandler.getProperty("requestArgumentDelimiter");
        if(requestArgumentDelimiter == null || requestArgumentDelimiter.equalsIgnoreCase("") || (!requestArgumentDelimiter.equalsIgnoreCase("&") && !requestArgumentDelimiter.equalsIgnoreCase("&amp;")))
            requestArgumentDelimiter = "&";

        return requestArgumentDelimiter;
    }

    public String composePageBaseUrl(String dnsName)
    {
        String useDNSNameInUrls = CmsPropertyHandler.getProperty("useDNSNameInURI");
        if(useDNSNameInUrls == null || useDNSNameInUrls.equalsIgnoreCase(""))
            useDNSNameInUrls = "false";

        if(!useDNSNameInUrls.equalsIgnoreCase("false"))
        {
            return dnsName + "/" + CmsPropertyHandler.getProperty("applicationBaseAction");
        }
        
        if(ActionContext.getRequest().getRequestURI().indexOf("!renderDecoratedPage") > -1)
		{
            //String componentRendererUrl = CmsPropertyHandler.getProperty("componentRendererUrl");
		    //if(componentRendererUrl.endsWith("/"))
		    //   componentRendererUrl += "/";
			
			return "/" + CmsPropertyHandler.getProperty("componentRendererUrl");
		}
        
        return "/" + CmsPropertyHandler.getProperty("applicationBaseAction");
    }


} 