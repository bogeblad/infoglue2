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

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.entities.content.Content;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.RegistryVO;
import org.infoglue.cms.entities.structure.SiteNode;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.XMLHelper;
import org.infoglue.deliver.applications.databeans.DeliveryContext;
import org.infoglue.deliver.controllers.kernel.impl.simple.BasicURLComposer;
import org.infoglue.deliver.controllers.kernel.impl.simple.DigitalAssetDeliveryController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
  * This is the action-class for UpdateContentVersionVersion
  * 
  * @author Mattias Bogeblad
  */

public class UpdateContentVersionAttributeAction extends ViewContentVersionAction 
{
    private final static Logger logger = Logger.getLogger(UpdateContentVersionAttributeAction.class.getName());

	private static final long serialVersionUID = 1L;
	
	private ContentVersionVO contentVersionVO;
	private Integer contentId;
	private Integer languageId;
	private Integer contentVersionId;
	private String attributeName;
	private String deliverContext = "infoglueDeliverWorking";

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
		System.out.println("Updating content version attribute....");
		System.out.println("contentId:" + contentId);
		System.out.println("languageId:" + languageId);
		System.out.println("attributeName:" + attributeName);
		
    	super.initialize(this.contentVersionId, this.contentId, this.languageId);

		this.contentVersionVO = this.getContentVersionVO();

		String attributeValue = getRequest().getParameter(this.attributeName);
		System.out.println("attributeValue:" + attributeValue);
		if(attributeValue != null)
		{
			setAttributeValue(this.contentVersionVO, this.attributeName, attributeValue);
			ceb.throwIfNotEmpty();
			
			this.contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());
    		ContentVersionController.getContentVersionController().update(this.contentId, this.languageId, this.contentVersionVO, this.getInfoGluePrincipal());
		}
		
		return "success";
	}

	public String doSaveAndReturnValue() throws Exception
    {
		System.out.println("Updating content version attribute through ajax....");
		System.out.println("contentId:" + contentId);
		System.out.println("languageId:" + languageId);
		System.out.println("attributeName:" + attributeName);
		
    	super.initialize(this.contentVersionId, this.contentId, this.languageId);

		this.contentVersionVO = this.getContentVersionVO();

		String attributeValue = getRequest().getParameter(this.attributeName);
		System.out.println("attributeValue:" + attributeValue);
		if(attributeValue != null)
		{
			setAttributeValue(this.contentVersionVO, this.attributeName, attributeValue);
			ceb.throwIfNotEmpty();
			
			System.out.println("attributeValue original:" + attributeValue);
			attributeValue = parseInlineAssetReferences(attributeValue);
			System.out.println("attributeValue transformed:" + attributeValue);
			
			this.contentVersionVO.setVersionModifier(this.getInfoGluePrincipal().getName());
    		ContentVersionController.getContentVersionController().update(this.contentId, this.languageId, this.contentVersionVO, this.getInfoGluePrincipal());
		
    		attributeValue = parseAttributeForInlineEditing(attributeValue);
		}
		
		this.getResponse().setContentType("text/plain");
        this.getResponse().getWriter().println(attributeValue);
		
		return NONE;
	}

	public String doGetAttributeValue() throws Exception
	{
		System.out.println("Getting content version attribute through ajax....");
		System.out.println("contentId:" + contentId);
		System.out.println("languageId:" + languageId);
		System.out.println("attributeName:" + attributeName);
		
		super.initialize(this.contentVersionId, this.contentId, this.languageId);
		this.contentVersionVO = this.getContentVersionVO();
		
		String attributeValue = ContentVersionController.getContentVersionController().getAttributeValue(contentVersionVO, attributeName, false);
		System.out.println("attributeValue:" + attributeValue);
		
		attributeValue = parseAttributeForInlineEditing(attributeValue);		
		
		this.getResponse().setContentType("text/plain");
        this.getResponse().getWriter().println(attributeValue);
		
		return NONE;		
	}
	
	//"DownloadAsset.action?contentId=6938&amp;languageId=1&amp;assetKey=downloadIcon"
	
	private String parseInlineAssetReferences(String attributeValue) throws Exception
	{
		Map<String,String> replacements = new HashMap<String,String>();
		
		System.out.println("********************\n\n");
	    Pattern pattern = Pattern.compile("\"DownloadAsset.action.*?\"");
	    Matcher matcher = pattern.matcher(attributeValue);
	    while ( matcher.find() ) 
	    { 
	        String match = matcher.group();
	        System.out.println("Found a inline asset: " + match);
	        String parsedContentId = match.substring(match.indexOf("contentId=") + 10, match.indexOf("&", 10));
	        System.out.println("parsedContentId: " + parsedContentId);
	        int langStartIndex = match.indexOf("languageId=") + 11;
	        String parsedLanguageId = match.substring(langStartIndex, match.indexOf("&", langStartIndex));
	        System.out.println("parsedLanguageId: " + parsedLanguageId);
	        int assetStartIndex = match.indexOf("assetKey=") + 9;
	        String parsedAssetKey = match.substring(assetStartIndex, match.indexOf("\"", assetStartIndex));
	        System.out.println("parsedAssetKey: " + parsedAssetKey);
	        
	        String url = "$templateLogic.getInlineAssetUrl(" + parsedContentId + ", \"" + parsedAssetKey + "\")";
    	    System.out.println("url:" + url);
            replacements.put(match.substring(1, match.length() - 1), url);
	    }
		System.out.println("********************\n\n");
	    
	    Iterator<String> replacementsIterator = replacements.keySet().iterator();
	    while(replacementsIterator.hasNext())
	    {
	    	String patternToReplace = replacementsIterator.next();
	    	String replacement = replacements.get(patternToReplace);
	    	
	    	System.out.println("Replacing " + patternToReplace + " with " + replacement);
	    	patternToReplace = patternToReplace.replaceAll("\\?", "\\\\?");
	    	System.out.println("patternToReplace " + patternToReplace);
	    	
	    	replacement = replacement.replaceAll("\\$", "\\\\\\$");
	    	System.out.println("replacement " + replacement);
	    	replacement = replacement.replaceAll("\\.", "\\\\.");
	    	System.out.println("replacement " + replacement);
	    	replacement = replacement.replaceAll("\\(", "\\\\(");
	    	System.out.println("replacement " + replacement);
	    	replacement = replacement.replaceAll("\\)", "\\\\)");
	    	System.out.println("replacement " + replacement);

	    	System.out.println("attributeValue before " + attributeValue);
	    	attributeValue = attributeValue.replaceAll(patternToReplace, replacement);
	    	System.out.println("attributeValue after " + attributeValue);
	    }
	    
	    return attributeValue;
	}

	private String parseAttributeForInlineEditing(String attributeValue) throws Exception
	{
		Map<String,String> replacements = new HashMap<String,String>();
		
	    Pattern pattern = Pattern.compile("\\$templateLogic\\.getPageUrl\\(.*?\\)");
	    Matcher matcher = pattern.matcher(attributeValue);
	    while ( matcher.find() ) 
	    { 
	        String match = matcher.group();
	        System.out.println("Adding match to registry after some processing: " + match);
	        Integer siteNodeId;
	        
	        int siteNodeStartIndex = match.indexOf("(");
	        int siteNodeEndIndex = match.indexOf(",");
	        if(siteNodeStartIndex > 0 && siteNodeEndIndex > 0 && siteNodeEndIndex > siteNodeStartIndex)
	        {
	            String siteNodeIdString = match.substring(siteNodeStartIndex + 1, siteNodeEndIndex); 

	            if(siteNodeIdString.indexOf("templateLogic.siteNodeId") == -1)
	            {
	            	siteNodeId = new Integer(siteNodeIdString);
	        		System.out.println("siteNodeId:" + siteNodeId);
	        		String parsedContentId = match.substring(match.lastIndexOf(",") + 1, match.lastIndexOf(")")).trim();
	        			
		            String url = getDeliverContext() + "/ViewPage!renderDecoratedPage.action?siteNodeId=" + siteNodeId + "&languageId=" + languageId + "&contentId=" + parsedContentId;
		            System.out.println("url:" + url);
		            replacements.put(match, url);
	            }
	        }
	    }

	    //$templateLogic.getInlineAssetUrl(6938, "downloadIcon")
	    Pattern assetPattern = Pattern.compile("\\$templateLogic\\.getInlineAssetUrl\\(.*?\\)");
	    Matcher assetMatcher = assetPattern.matcher(attributeValue);
	    while ( assetMatcher.find() ) 
	    { 
	        String match = assetMatcher.group();
	        System.out.println("Adding match to registry after some processing: " + match);
	        
	        int contentStartIndex = match.indexOf("(");
	        int contentEndIndex = match.indexOf(",");
	        if(contentStartIndex > 0 && contentEndIndex > 0 && contentEndIndex > contentStartIndex)
	        {
	            String contentIdString = match.substring(contentStartIndex + 1, contentEndIndex); 

            	contentId = new Integer(contentIdString);
        		System.out.println("contentId:" + contentId);
        		String parsedAssetKey = match.substring(match.lastIndexOf(",") + 1, match.lastIndexOf(")")).trim();
        		parsedAssetKey = parsedAssetKey.replaceAll("\"", "");
        		
	            String url = this.getCMSBaseUrl() + "/DownloadAsset.action?contentId=" + contentId + "&languageId=" + this.languageId + "&assetKey=" + parsedAssetKey;
	            System.out.println("url:" + url);
	            replacements.put(match, url);
	        }
	    }

	    Iterator<String> replacementsIterator = replacements.keySet().iterator();
	    while(replacementsIterator.hasNext())
	    {
	    	String patternToReplace = replacementsIterator.next();
	    	String replacement = replacements.get(patternToReplace);
	    	
	    	System.out.println("Replacing " + patternToReplace + " with " + replacement);
	    	//Fel just nu...
	    	patternToReplace = patternToReplace.replaceAll("\\$", "\\\\\\$");
	    	System.out.println("patternToReplace " + patternToReplace);
	    	patternToReplace = patternToReplace.replaceAll("\\.", "\\\\.");
	    	System.out.println("patternToReplace " + patternToReplace);
	    	patternToReplace = patternToReplace.replaceAll("\\(", "\\\\(");
	    	System.out.println("patternToReplace " + patternToReplace);
	    	patternToReplace = patternToReplace.replaceAll("\\)", "\\\\)");
	    	System.out.println("patternToReplace " + patternToReplace);
	    	
	    	System.out.println("attributeValue before " + attributeValue);
	    	attributeValue = attributeValue.replaceAll(patternToReplace, replacement);
	    	System.out.println("attributeValue after " + attributeValue);
	    }
		
	    return attributeValue;
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
		        logger.info("VersionValue:" + this.contentVersionVO.getVersionValue());
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
					    logger.info("Setting attributeValue: " + attributeValue);
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

	public String getDeliverContext()
	{
		return deliverContext;
	}

	public void setDeliverContext(String deliverContext)
	{
		this.deliverContext = deliverContext;
	}

}
