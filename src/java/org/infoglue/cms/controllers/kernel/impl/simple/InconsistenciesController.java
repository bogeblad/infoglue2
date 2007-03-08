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

package org.infoglue.cms.controllers.kernel.impl.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.exolab.castor.jdo.Database;
import org.infoglue.cms.entities.content.Content;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.content.impl.simple.ContentImpl;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.management.RegistryVO;
import org.infoglue.cms.entities.structure.SiteNode;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.entities.structure.SiteNodeVersionVO;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.DateHelper;
import org.infoglue.cms.util.XMLHelper;
import org.infoglue.deliver.applications.databeans.DeliveryContext;
import org.infoglue.deliver.controllers.kernel.impl.simple.NodeDeliveryController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
* This class handles inconsistencies in the domain model and the cleanup of them.
* 
* @author mattias
*/

public class InconsistenciesController extends BaseController
{
	
	/**
	 * Factory method to get object
	 */
	
	public static InconsistenciesController getController()
	{
		return new InconsistenciesController();
	}

	/*
	public List getMetaDataInconsistencies() throws Exception
	{
		List inconsistencies = new ArrayList();
		
		HashMap arguments = new HashMap();
		arguments.put("method", "selectListOnContentTypeName");
		
		List argumentList = new ArrayList();
		HashMap argument = new HashMap();
		argument.put("contentTypeDefinitionName", "HTMLTemplate");
		argumentList.add(argument);
		arguments.put("arguments", argumentList);
		
		List metaInfoContentVOList = ContentController.getContentController().getContentVOList(arguments);
		System.out.println("metaInfoContentVOList:" + metaInfoContentVOList);
		if(metaInfoContentVOList != null && metaInfoContentVOList.size() > 0)
		{
			Iterator metaInfoContentVOListIterator = metaInfoContentVOList.iterator();
			while(metaInfoContentVOListIterator.hasNext())
			{
				ContentVO contentVO = (ContentVO)metaInfoContentVOListIterator.next();
				System.out.println("metaInfoContentVOList:" + metaInfoContentVOList.size());
				LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(contentVO.getRepositoryId());
				ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(contentVO.getId(), masterLanguageVO.getId());
				System.out.println("contentVersionVO:" + contentVersionVO);
			}
		}
		
		return inconsistencies;
	}
	*/
	
	public List getAllInconsistencies() throws Exception
	{
		List inconsistencies = new ArrayList();
		
	    Database db = CastorDatabaseService.getDatabase();
		
		try 
		{
			beginTransaction(db);
		
			List registryVOList = RegistryController.getController().getRegistryVOList(db);
			//System.out.println("registryVOList:" + registryVOList);
			if(registryVOList != null && registryVOList.size() > 0)
			{
				Iterator registryVOListIterator = registryVOList.iterator();
				while(registryVOListIterator.hasNext())
				{
					RegistryVO registryVO = (RegistryVO)registryVOListIterator.next();
					//System.out.println("registryVO:" + registryVO.getEntityName() + "-" + registryVO.getEntityId());
					if(registryVO.getEntityName().equals(Content.class.getName()))
					{
						try
						{
							ContentVO contentVO = ContentController.getContentController().getContentVOWithId(new Integer(registryVO.getEntityId()), db);
							if(contentVO == null)
								addContentInconsistency(inconsistencies, registryVO, db);
								//inconsistencies.add(registryVO);								
						}
						catch(Exception e)
						{
							addContentInconsistency(inconsistencies, registryVO, db);
							//inconsistencies.add(registryVO);
						}
					}
					else if(registryVO.getEntityName().equals(SiteNode.class.getName()))
					{
						try
						{
							SiteNodeVO siteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(new Integer(registryVO.getEntityId()), db);
							if(siteNodeVO == null)
								addSiteNodeInconsistency(inconsistencies, registryVO, db);
								//inconsistencies.add(registryVO);								
						}
						catch(Exception e)
						{
							addSiteNodeInconsistency(inconsistencies, registryVO, db);
							//inconsistencies.add(registryVO);
						}
					}
					else
					{
						System.out.println("The registry contained not supported entities:" + registryVO.getEntityName());
					}
				}
			}
		    
			commitTransaction(db);
		}
		catch (Exception e)		
		{
		    rollbackTransaction(db);
			throw new SystemException("An error occurred when we tried to fetch which sitenode uses a metainfo. Reason:" + e.getMessage(), e);			
		}
		
		return inconsistencies;
	}
	
	private void addContentInconsistency(List inconsistencies, RegistryVO registryVO, Database db) throws Exception
	{
		String referencingEntityName = registryVO.getReferencingEntityName();
		String referencingEntityCompletingName = registryVO.getReferencingEntityCompletingName();
		Integer referencingEntityId = new Integer(registryVO.getReferencingEntityId());
		Integer referencingEntityCompletingId = new Integer(registryVO.getReferencingEntityCompletingId());
		
		if(referencingEntityCompletingName.equals(SiteNode.class.getName()))
		{
			SiteNodeVO siteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(referencingEntityCompletingId, db);
			if(siteNodeVO != null)
			{
				LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(siteNodeVO.getRepositoryId());
				SiteNodeVersionVO siteNodeVersionVO = SiteNodeVersionController.getController().getLatestActiveSiteNodeVersionVO(db, siteNodeVO.getId());
				
				if(siteNodeVersionVO != null && siteNodeVersionVO.getId().intValue() == referencingEntityId.intValue())
					inconsistencies.add(registryVO);
			}
		}
		else if(referencingEntityCompletingName.equals(Content.class.getName()))
		{
			ContentVO contentVO = ContentController.getContentController().getContentVOWithId(new Integer(registryVO.getReferencingEntityCompletingId()), db);
			if(contentVO != null)
			{
				LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(contentVO.getRepositoryId());
				ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(contentVO.getId(), masterLanguageVO.getId(), db);
				
				if(contentVersionVO != null && contentVersionVO.getId().intValue() == referencingEntityId.intValue())
					inconsistencies.add(registryVO);
			}			
		}
		else
		{
			System.out.println("The registry contained a not supported referencingEntityCompletingName:" + referencingEntityCompletingName);			
		}
	}

	private void addSiteNodeInconsistency(List inconsistencies, RegistryVO registryVO, Database db) throws Exception
	{
		String referencingEntityName = registryVO.getReferencingEntityName();
		String referencingEntityCompletingName = registryVO.getReferencingEntityCompletingName();
		Integer referencingEntityId = new Integer(registryVO.getReferencingEntityId());
		Integer referencingEntityCompletingId = new Integer(registryVO.getReferencingEntityCompletingId());
		
		if(referencingEntityCompletingName.equals(SiteNode.class.getName()))
		{
			SiteNodeVO siteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(new Integer(registryVO.getReferencingEntityCompletingId()), db);
			if(siteNodeVO != null)
			{
				LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(siteNodeVO.getRepositoryId());
				SiteNodeVersionVO siteNodeVersionVO = SiteNodeVersionController.getController().getLatestActiveSiteNodeVersionVO(db, siteNodeVO.getId());
				
				if(siteNodeVersionVO != null && siteNodeVersionVO.getId().intValue() == referencingEntityId.intValue())
					inconsistencies.add(registryVO);
			}
		}
		else if(referencingEntityCompletingName.equals(Content.class.getName()))
		{
			ContentVO contentVO = ContentController.getContentController().getContentVOWithId(new Integer(registryVO.getReferencingEntityCompletingId()), db);
			if(contentVO != null)
			{
				LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(contentVO.getRepositoryId());
				ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(contentVO.getId(), masterLanguageVO.getId(), db);
				
				if(contentVersionVO != null && contentVersionVO.getId().intValue() == referencingEntityId.intValue())
					inconsistencies.add(registryVO);
			}			
		}
		else
		{
			System.out.println("The registry contained a not supported referencingEntityCompletingName:" + referencingEntityCompletingName);			
		}
	}

	public void removeReferences(Integer registryId, InfoGluePrincipal infoGluePrincipal) throws SystemException, Exception 
	{
		RegistryVO registryVO = RegistryController.getController().getRegistryVOWithId(registryId);
		
		String entityName = registryVO.getEntityName();
		String referencingEntityName = registryVO.getReferencingEntityName();
		String referencingEntityCompletingName = registryVO.getReferencingEntityCompletingName();
		Integer entityId = new Integer(registryVO.getEntityId());
		Integer referencingEntityId = new Integer(registryVO.getReferencingEntityId());
		Integer referencingEntityCompletingId = new Integer(registryVO.getReferencingEntityCompletingId());
		
		if(referencingEntityCompletingName.equals(SiteNode.class.getName()))
		{
			SiteNodeVO siteNodeVO = SiteNodeController.getController().getSiteNodeVOWithId(new Integer(registryVO.getReferencingEntityCompletingId()));
			if(siteNodeVO != null)
			{
				Integer metaInfoContentId = siteNodeVO.getMetaInfoContentId();
				LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(siteNodeVO.getRepositoryId());
				String pageStructure = ContentController.getContentController().getContentAttribute(metaInfoContentId, masterLanguageVO.getId(), "ComponentStructure");
				
				if(registryVO.getReferenceType().equals(RegistryVO.PAGE_COMPONENT))
					pageStructure = deleteComponentFromXML(pageStructure, new Integer(registryVO.getEntityId()));
				if(registryVO.getReferenceType().equals(RegistryVO.PAGE_COMPONENT_BINDING))
					pageStructure = deleteComponentBindingFromXML(pageStructure, new Integer(registryVO.getEntityId()));
				
				ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(metaInfoContentId, masterLanguageVO.getId());
				ContentVersionController.getContentVersionController().updateAttributeValue(contentVersionVO.getContentVersionId(), "ComponentStructure", pageStructure, infoGluePrincipal);
			}
		}
		else if(referencingEntityCompletingName.equals(Content.class.getName()))
		{
			if(referencingEntityName.equals(ContentVersion.class.getName()))
			{
				ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getContentVersionVOWithId(referencingEntityId);
				if(contentVersionVO != null)
				{
					String versionValue = contentVersionVO.getVersionValue();
					
					//if(registryVO.getReferenceType().equals(RegistryVO.INLINE_LINK))
						//TODO - versionValue = deleteInlineLinks(versionValue, new Integer(registryVO.getEntityId()));
					//if(registryVO.getReferenceType().equals(RegistryVO.INLINE_ASSET))
						//TODO - versionValue = deleteInlineAssets(versionValue, new Integer(registryVO.getEntityId()));
					if(registryVO.getReferenceType().equals(RegistryVO.INLINE_SITE_NODE_RELATION))
						versionValue = deleteInlineSiteNodeRelations(versionValue, new Integer(registryVO.getEntityId()));
					if(registryVO.getReferenceType().equals(RegistryVO.INLINE_CONTENT_RELATION))
						versionValue = deleteInlineContentRelations(versionValue, new Integer(registryVO.getEntityId()));
						
					contentVersionVO.setVersionModifier(infoGluePrincipal.getName());
					contentVersionVO.setModifiedDateTime(DateHelper.getSecondPreciseDate());
					contentVersionVO.setVersionValue(versionValue);
					
					ContentVersionController.getContentVersionController().update(contentVersionVO.getContentId(), contentVersionVO.getLanguageId(), contentVersionVO);
				}
			}
		}
		else
		{
			System.out.println("The registry contained a not supported referencingEntityCompletingName:" + referencingEntityCompletingName);			
		}
		
	}

	private String deleteInlineContentRelations(String versionValue, Integer contentId) 
	{
		String cleanedVersionValue = versionValue;
		StringBuffer cleanedVersionValueBuffer = new StringBuffer();
		
		int qualifyerEndIndex = 0;
		int qualifyerStartIndex = cleanedVersionValue.indexOf("<qualifyer entity='Content'>");
		if(qualifyerStartIndex == -1)
			qualifyerStartIndex = cleanedVersionValue.indexOf("<qualifyer entity=\"Content\">");

		while(qualifyerStartIndex > -1)
		{
			cleanedVersionValueBuffer.append(versionValue.substring(qualifyerEndIndex, qualifyerStartIndex));
			qualifyerEndIndex = cleanedVersionValue.indexOf("</qualifyer>", qualifyerStartIndex);
			if(qualifyerEndIndex != -1)
			{
				String qualifyerString = versionValue.substring(qualifyerStartIndex, qualifyerEndIndex);
				String cleanedQualifyerString = qualifyerString.replaceAll("<id>" + contentId + "</id>", "");
				cleanedVersionValueBuffer.append(cleanedQualifyerString);
			}
			
			qualifyerStartIndex = cleanedVersionValue.indexOf("<qualifyer entity='Content'>", qualifyerEndIndex);
			if(qualifyerStartIndex == -1)
				qualifyerStartIndex = cleanedVersionValue.indexOf("<qualifyer entity=\"Content\">");
		}

		cleanedVersionValueBuffer.append(versionValue.substring(qualifyerEndIndex));
		
		cleanedVersionValue = cleanedVersionValueBuffer.toString();
		
		return cleanedVersionValue;
	}

	private String deleteInlineSiteNodeRelations(String versionValue, Integer siteNodeId) 
	{
		String cleanedVersionValue = versionValue;
		StringBuffer cleanedVersionValueBuffer = new StringBuffer();
		
		int qualifyerEndIndex = 0;
		int qualifyerStartIndex = cleanedVersionValue.indexOf("<qualifyer entity='SiteNode'>");
		if(qualifyerStartIndex == -1)
			qualifyerStartIndex = cleanedVersionValue.indexOf("<qualifyer entity=\"SiteNode\">");

		while(qualifyerStartIndex > -1)
		{
			cleanedVersionValueBuffer.append(versionValue.substring(qualifyerEndIndex, qualifyerStartIndex));
			qualifyerEndIndex = cleanedVersionValue.indexOf("</qualifyer>", qualifyerStartIndex);
			if(qualifyerEndIndex != -1)
			{
				String qualifyerString = versionValue.substring(qualifyerStartIndex, qualifyerEndIndex);
				String cleanedQualifyerString = qualifyerString.replaceAll("<id>" + siteNodeId + "</id>", "");
				cleanedVersionValueBuffer.append(cleanedQualifyerString);
			}
			
			qualifyerStartIndex = cleanedVersionValue.indexOf("<qualifyer entity='SiteNode'>", qualifyerEndIndex);
			if(qualifyerStartIndex == -1)
				qualifyerStartIndex = cleanedVersionValue.indexOf("<qualifyer entity=\"SiteNode\">");
		}

		cleanedVersionValueBuffer.append(versionValue.substring(qualifyerEndIndex));
		
		//<?xml version='1.0' encoding='UTF-8'?><qualifyer entity='SiteNode'><id>45</id><id>1115</id></qualifyer>
		cleanedVersionValue = cleanedVersionValueBuffer.toString();
		
		return cleanedVersionValue;
	}

	/*
	private String deleteInlineAssets(String versionValue, Integer contentId) 
	{
		String cleanedVersionValue = versionValue;
		
		System.out.println("cleanedVersionValue:" + cleanedVersionValue);
		return cleanedVersionValue;
	}

	private String deleteInlineLinks(String versionValue, Integer contentId) 
	{
		String cleanedVersionValue = versionValue;
		
		System.out.println("cleanedVersionValue:" + cleanedVersionValue);
		return cleanedVersionValue;
	}
	*/
	
	private String deleteComponentFromXML(String componentXML, Integer contentId) throws Exception
	{
		Document document = XMLHelper.readDocumentFromByteArray(componentXML.getBytes("UTF-8"));
		String componentXPath = "//component[@contentId=" + contentId + "]";
		//logger.info("componentXPath:" + componentXPath);
		String modifiedXML = null;
		
		NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), componentXPath);
		//logger.info("anl:" + anl.getLength());
		for(int i=0; i<anl.getLength(); i++)
		{
			Element component = (Element)anl.item(i);
			component.getParentNode().removeChild(component);
		}
		
		modifiedXML = XMLHelper.serializeDom(document, new StringBuffer()).toString(); 
		
		return modifiedXML;
	}

	private String deleteComponentBindingFromXML(String componentXML, Integer contentId) throws Exception
	{
		Document document = XMLHelper.readDocumentFromByteArray(componentXML.getBytes("UTF-8"));
		String componentPropertyXPath = "//component/properties/property/binding[@entityId='" + contentId + "']";
		//logger.info("componentPropertyXPath:" + componentPropertyXPath);
		String modifiedXML = null;
		
		NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), componentPropertyXPath);
		//logger.info("anl:" + anl.getLength());
		for(int i=0; i<anl.getLength(); i++)
		{
			Element component = (Element)anl.item(i);
			String entity = component.getAttribute("entity");
			if(entity != null && entity.equalsIgnoreCase("Content"))
			{
				Element property = (Element)component.getParentNode();
				if(property != null && property.getParentNode() != null)
					property.getParentNode().removeChild(property);
			}
		}
		
		modifiedXML = XMLHelper.serializeDom(document, new StringBuffer()).toString(); 
		
		return modifiedXML;
	}

	
    public BaseEntityVO getNewVO()
    {
        return null;
    }

}
