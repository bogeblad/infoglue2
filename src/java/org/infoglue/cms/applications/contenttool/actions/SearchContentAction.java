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

import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.controllers.kernel.impl.simple.SearchController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.util.CmsLogger;

import java.util.List;


/**
 * Action class for usecase SearchContentAction. Was better before but due to wanted support for multiple 
 * databases and lack of time I had to cut down on functionality - sorry Magnus. 
 *
 * @author Magnus Güvenal
 * @author Mattias Bogeblad
 */

public class SearchContentAction extends WebworkAbstractAction 
{

	private List contentVersionVOList;
	private Integer repositoryId;
	private String searchString;
	private int maxRows = 0;
	
	public void setSearchString(String s)
	{
		this.searchString = s.replaceAll("'","");
		CmsLogger.logInfo(this.searchString);
	}
	public String getSearchString()
	{
		return this.searchString;	
	}
	
	public void setMaxRows(int r)
	{
		this.maxRows = r;	
	}
	
	public int getMaxRows()
	{
		if(maxRows == 0)maxRows=100;
		return this.maxRows;	
	}

	public List getContentVersionVOList()
	{
		return this.contentVersionVOList;		
	}
	
	protected String doExecute() throws Exception 
	{
	    CmsLogger.logInfo("Executing doExecute on SearchContentAction..");
		int maxRows = 100;
		try
		{
			maxRows = Integer.parseInt(CmsPropertyHandler.getProperty("maxRows"));
		}
		catch(Exception e)
		{
		}
		
		contentVersionVOList = SearchController.getContentVersions(this.repositoryId, this.getSearchString(), maxRows);
	    CmsLogger.logInfo("Finished executing doExecute on SearchContentAction..");
        return "success";
	}
	
	public SearchContentAction getThis()
	{
		return this;
	}
	
	public ContentVO getContentVO(Integer contentId)
	{
		ContentVO contentVO = null;
		
		try
		{
			if(contentId != null)
			{
				contentVO = ContentController.getContentController().getContentVOWithId(contentId);
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred when we tried to get the content for this version:" + e.getMessage(), e);
		}
		
		return contentVO;
	}

	public LanguageVO getLanguageVO(Integer languageId)
	{
		LanguageVO languageVO = null;
		
		try
		{
			if(languageId != null)
			{
				languageVO = LanguageController.getController().getLanguageVOWithId(languageId);
			}
		}
		catch(Exception e)
		{
			CmsLogger.logSevere("An error occurred when we tried to get the language for this version:" + e.getMessage(), e);
		}
		
		return languageVO;
	}

	public Integer getRepositoryId()
	{
		return repositoryId;
	}

	public void setRepositoryId(Integer integer)
	{
		repositoryId = integer;
	}

}
