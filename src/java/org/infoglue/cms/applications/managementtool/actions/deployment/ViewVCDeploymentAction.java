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

package org.infoglue.cms.applications.managementtool.actions.deployment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.DeploymentController;
import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.io.FileHelper;
import org.infoglue.cms.util.CmsPropertyHandler;

public class ViewVCDeploymentAction extends InfoGlueAbstractAction
{
    private final static Logger logger = Logger.getLogger(ViewVCDeploymentAction.class.getName());

	private static final long serialVersionUID = 1L;
		
	private Map<String,VersionControlServerBean> vcServers = new HashMap<String,VersionControlServerBean>();
	private String vcServerName = null;
	private String vcPassword = null;
	private List<String> tags = new ArrayList<String>();
	private String tagName = null;
	private List<DeploymentCompareBean> deviatingContents = new ArrayList<DeploymentCompareBean>();

	public String doInput() throws Exception
    {
    	this.vcServers = CmsPropertyHandler.getVCServers();
    	
    	return "input";
    }

    public String doInputChooseTag() throws Exception
    {
    	this.vcServers = CmsPropertyHandler.getVCServers();
    	
    	if(vcServerName != null && !vcServerName.equals(""))
    	{
	    	VersionControlServerBean serverBean = this.vcServers.get(vcServerName);
	    	if(serverBean != null)
	    	{
	    		if(this.vcPassword != null)
	    			serverBean.setPassword(this.vcPassword);
	    		this.tags = DeploymentController.getAvailableTags(serverBean);
	    		
	    	}
    	}

    	return "inputChooseTag";
    }

    public String doInputVerifyCheckout() throws Exception
    {
    	this.vcServers = CmsPropertyHandler.getVCServers();
    	
    	if(vcServerName != null && !vcServerName.equals(""))
    	{
    		logger.info("vcServerName:" + vcServerName);
	    	VersionControlServerBean serverBean = this.vcServers.get(vcServerName);
	    	if(serverBean != null)
	    	{
	    		logger.info("tagName:" + tagName);
	    		if(this.vcPassword != null)
	    			serverBean.setPassword(this.vcPassword);
	    		
    			this.deviatingContents = DeploymentController.getDeploymentComparisonBeans(serverBean, tagName, getInfoGluePrincipal());
	    	}
	    }
    	
    	return "inputVerifyCheckout";
    }
    
    
    public String doExecute() throws Exception
    {
    	String[] deviatingLocalContentIdArray = this.getRequest().getParameterValues("deviatingContentId");
    	logger.info("deviatingLocalContentIdArray:" + deviatingLocalContentIdArray);
    	
    	List deviatingComponents = new ArrayList();
    	if(deviatingLocalContentIdArray != null)
    	{
	    	for(int i=0; i<deviatingLocalContentIdArray.length; i++)
	    	{
	    		String deviatingLocalContentId = deviatingLocalContentIdArray[i];
	    		logger.info("deviatingLocalContentId:" + deviatingLocalContentId);
	    	
	        	String deviatingFilePath = this.getRequest().getParameter("deviatingRemoteVersionId_" + deviatingLocalContentId);
	        	logger.info("deviatingFilePath:" + deviatingFilePath);
	        	
	        	ContentVO contentVO = ContentController.getContentController().getContentVOWithId(new Integer(deviatingLocalContentId).intValue());
	    		if(contentVO != null)
	    		{
					LanguageVO languageVO = LanguageController.getController().getMasterLanguage(contentVO.getRepositoryId());
					ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(contentVO.getId(), languageVO.getId());
					String fileContent = FileHelper.getFileAsString(new File(deviatingFilePath), "iso-8859-1");
					
					contentVersionVO.setVersionValue(fileContent);
					contentVersionVO.setVersionComment("Checked out from version control system (tag: " + tagName + ")");
					
					logger.info("We are going to replace local content: " + contentVO.getName() + " with contents in " + deviatingFilePath);
					ContentVersionController.getContentVersionController().update(contentVersionVO.getContentId(), contentVersionVO.getLanguageId(), contentVersionVO, getInfoGluePrincipal());
				}
	    	}
    	}

    	return "success";
    }

	public Map<String,VersionControlServerBean> getVcServers() 
	{
		return vcServers;
	}

	public String getVcServerName() 
	{
		return vcServerName;
	}

	public void setVcServerName(String vcServerName) 
	{
		this.vcServerName = vcServerName;
	}

	public List getTags() 
	{
		return tags;
	}

	public void setTags(List tags) 
	{
		this.tags = tags;
	}

	public String getTagName() 
	{
		return tagName;
	}

	public void setTagName(String tagName) 
	{
		this.tagName = tagName;
	}

	public List<DeploymentCompareBean> getDeviatingContents() 
	{
		return deviatingContents;
	}

	public void setVcPassword(String vcPassword)
	{
		this.vcPassword = vcPassword;
	}

	public String getVcPassword()
	{
		return this.vcPassword;
	}
}
