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

import org.apache.xerces.parsers.DOMParser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource; 
import java.io.*;

import org.infoglue.cms.entities.kernel.*;
import org.infoglue.cms.entities.content.ContentVersion;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.CmsLogger;

import org.exolab.castor.jdo.Database;

import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import java.util.List;
import java.util.ArrayList;

/**
 * @author mgu
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SearchController extends BaseController 
{

	public static String getAttributeValue(String xml,String key)
	{
		String value = "";
			try
	        {
		        InputSource inputSource = new InputSource(new StringReader(xml));
				DOMParser parser = new DOMParser();
				parser.parse(inputSource);
				Document document = parser.getDocument();
				NodeList nl = document.getDocumentElement().getChildNodes();
				Node n = nl.item(0);
				nl = n.getChildNodes();

				for(int i=0; i<nl.getLength(); i++)
				{
					n = nl.item(i);
					if(n.getNodeName().equalsIgnoreCase(key))
					{
						value = n.getFirstChild().getNodeValue();
						break;
					}
				}		        	
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
		if(value.equalsIgnoreCase(""))value="This Content is Unititled";
		return value;
	}

	public static String setScoreImg(double score)
	{
		if( 2.0 <  score){
			return "5star.gif";					
		}
		else if( 1.0 <  score){
			return "4star.gif";					
		}
		else if( 0.6 <  score){
			return "3star.gif";					
		}
		else if( 0.4 <  score){
			return "2star.gif";					
		}
		else{
			return "1star.gif";	
		}
	}

	
   	public static List getContentVersions(Integer repositoryId, String searchString, int maxRows)throws SystemException, Bug
   	{
		List matchingContents = new ArrayList();

		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
		Database db = CastorDatabaseService.getDatabase();
		try
		{
			beginTransaction(db);

			OQLQuery oql = db.getOQLQuery("SELECT cv FROM org.infoglue.cms.entities.content.impl.simple.ContentVersionImpl cv WHERE cv.isActive = $1 AND cv.versionValue LIKE $2 AND cv.owningContent.repository.repositoryId = $3 ORDER BY cv.owningContent asc, cv.language, cv.contentVersionId desc");
			oql.bind(new Boolean(true));
			oql.bind("%" + searchString + "%");
			oql.bind(repositoryId);
	        	
			QueryResults results = oql.execute(Database.ReadOnly);
			
			Integer previousContentId  = new Integer(-1);
			Integer previousLanguageId = new Integer(-1);  	
			int currentCount = 0;
			while(results.hasMore() && currentCount < maxRows) 
			{
				ContentVersion contentVersion = (ContentVersion)results.next();
				CmsLogger.logInfo("Found a version matching " + searchString + ":" + contentVersion.getId() + "=" + contentVersion.getOwningContent().getName());
				if(contentVersion.getOwningContent().getId().intValue() != previousContentId.intValue() || contentVersion.getLanguage().getId().intValue() != previousLanguageId.intValue())
				{
					matchingContents.add(contentVersion.getValueObject());
					previousContentId = contentVersion.getOwningContent().getId();
					previousLanguageId = contentVersion.getLanguage().getId();
					currentCount++;
				}
			}

			commitTransaction(db);
		}
		catch ( Exception e )
		{
			rollbackTransaction(db);
			throw new SystemException("An error occurred when we tried to fetch a list of users in this role. Reason:" + e.getMessage(), e);			
		}
		
		return matchingContents;
		
   	}
   	
	/**
	 * This is a method that never should be called.
	 */

	public BaseEntityVO getNewVO()
	{
		return null;
	}
   	
}