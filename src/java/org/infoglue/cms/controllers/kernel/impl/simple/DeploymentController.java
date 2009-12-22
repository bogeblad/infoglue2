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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.cms.applications.managementtool.actions.deployment.DeploymentCompareBean;
import org.infoglue.cms.applications.managementtool.actions.deployment.VersionControlServerBean;
import org.infoglue.cms.entities.content.Content;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.management.Repository;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.io.FileHelper;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.common.util.cvsclient.connectors.NetBeansConnector;
import org.infoglue.common.util.vc.connectors.VCConnector;

public class DeploymentController 
{
    private final static Logger logger = Logger.getLogger(DeploymentController.class.getName());

	public static List<String> getAvailableTags(VersionControlServerBean serverBean) throws Exception
	{
		List<String> availableTags = new ArrayList<String>();
		availableTags.add("HEAD");
		
		try
		{			
			String cvsRoot 		= serverBean.getCVSROOT();
			String password 	= serverBean.getPassword();
			String localPath	= CmsPropertyHandler.getDigitalAssetUploadPath() + File.separator + "checkout";
						
		    VCConnector connector = new NetBeansConnector(cvsRoot, localPath, password);
		    availableTags.addAll(connector.getTags(serverBean.getModuleName()));
		} 
		catch (Exception e)
		{
			throw new ConstraintException("vcCommunication", "311", "" + e.getMessage() + "<br/>CVSRoot: " + serverBean.getCVSROOT());
		}		
		
		return availableTags;
	}

	public static List<DeploymentCompareBean> getDeploymentComparisonBeans(VersionControlServerBean versionControlServerBean, String tagName, InfoGluePrincipal principal) throws Exception
	{
		List<DeploymentCompareBean> deploymentCompareBeans = new ArrayList<DeploymentCompareBean>();
		
		int cvsFakeContentId = 1;
		
		Database db = CastorDatabaseService.getDatabase();
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();

		Content content = null;

		try
		{
			db.begin();
			
			String cvsRoot 		= versionControlServerBean.getCVSROOT();
			String password 	= versionControlServerBean.getPassword();
			String localPath	= CmsPropertyHandler.getDigitalAssetUploadPath() + File.separator + "checkout";
						
		    VCConnector connector = new NetBeansConnector(cvsRoot, localPath, password);
		    if(tagName.equals("HEAD"))
		    	tagName = null;
		    
		    List<File> taggedFiles = connector.checkOutModuleFromTag(versionControlServerBean.getModuleName(), tagName);
		    logger.info("taggedFiles:" + taggedFiles);
		    
		    File moduleRoot = new File(localPath + File.separator + versionControlServerBean.getModuleName());
		    getDeploymentComparisonBeansRecursive(moduleRoot, deploymentCompareBeans, null, db, principal, cvsFakeContentId);
	
			ceb.throwIfNotEmpty();
            
			db.rollback();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//logger.error("An error occurred so we should not complete the transaction:" + e, e);
			try
			{
				db.rollback();
			}
			catch (Exception e2) 
			{
			}
			throw new SystemException(e.getMessage());
		}
		
		return deploymentCompareBeans;
	}
	
	private static void getDeploymentComparisonBeansRecursive(File parentFile, List<DeploymentCompareBean> deploymentCompareBeans, final Content parentContent, Database db, InfoGluePrincipal principal, int cvsFakeContentId) throws Exception
	{
		Content localParentContent = parentContent;
		
		String fileContent = "";
		if(parentFile.isFile())
			fileContent = FileHelper.getFileAsString(parentFile, "iso-8859-1");
		
		Repository repository = null;

		if(parentContent != null)
			logger.info("\n\n****************************\nparentContent in top:" + parentContent.getName() + "= File:" + parentFile.getName());

		if(!parentFile.getName().equalsIgnoreCase("localIG3") && 
		   !parentFile.getName().equalsIgnoreCase("WebContent") && 
		   !parentFile.getParentFile().getName().equalsIgnoreCase("WebContent"))
		{
			logger.info("This was a content:" + parentFile.getName());
			
			if(parentContent != null)
			{
				ContentVO cvsContent = new ContentVO();
				cvsContent.setContentId(cvsFakeContentId);
				cvsContent.setName(localParentContent.getName());
				cvsContent.setFullPath(parentFile.getPath());
				
				//ContentVersionVO cvsVersion = new ContentVersionVO();
				//cvsVersion.setVersionValue(fileContent);
				cvsContent.setVersions(new String[]{fileContent});

				LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(db, localParentContent.getRepositoryId()).getValueObject();
				ContentVersionVO localVersion = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(localParentContent.getId(), masterLanguageVO.getId());

				ContentVO localContentVO = localParentContent.getValueObject();
				logger.info("localContentVO:" + localContentVO);
				//System.out.println("localContentVO:" + localContentVO);
				
				if(localVersion != null)
				{
					localContentVO.setVersions(new String[]{localVersion.getVersionValue()});
					logger.info("fileContent:" + fileContent.length());
					logger.info("localVersion.getVersionValue():" + localVersion.getVersionValue().length());
				}
				
				DeploymentCompareBean deploymentCompareBean = new DeploymentCompareBean();
				deploymentCompareBean.setRemoteVersion(cvsContent);
				deploymentCompareBean.setLocalVersion(localContentVO);
				
				logger.info("Adding:" + localContentVO.getName() + " = " + cvsContent.getName());
				deploymentCompareBeans.add(deploymentCompareBean);
			}
		}
		else if(parentFile.getParentFile().getName().equalsIgnoreCase("WebContent") && !parentFile.getName().equals("CVS"))
		{
			logger.info("This was a repository:" + parentFile);
			repository = RepositoryController.getController().getRepositoryWithName(parentFile.getName(), db);
			logger.info("Found repository:" + repository.getName());
			localParentContent = ContentController.getContentController().getRootContent(db, repository.getId(), principal.getName(), false);
		}
		else
		{
			logger.info("This was the webcontent file:" + parentFile);
		}
		
		logger.info("Now going through children files to:" + parentFile.getName());
		File[] childFiles = parentFile.listFiles();
		if(childFiles != null)
		{
			for(int i=0; i<childFiles.length; i++)
			{
				File childFile = childFiles[i];
				logger.info("childFile:" + childFile.getName());
				if(!childFile.getName().equals("CVS"))
				{
					//System.out.println("childFile:" + childFile.getName());
					Content newParentContent = null;
					if(localParentContent != null)
					{
						LanguageVO masterLanguageVO = LanguageController.getController().getMasterLanguage(db, localParentContent.getRepositoryId()).getValueObject();

						logger.info("Looking for children on " + localParentContent.getName() + " - matching " + childFile.getName());
						Iterator childContents = localParentContent.getChildren().iterator();
						while(childContents.hasNext())
						{
							Content childContent = (Content)childContents.next();
							logger.info("childContent " + childContent.getName());
							
							if(childFile.getName().equalsIgnoreCase(childContent.getName()) || childFile.getName().equalsIgnoreCase(childContent.getName() + "_" + masterLanguageVO.getId() + ".xml"))
							{
								logger.info("Match " + childContent.getName() + "=" + childFile.getName());
								newParentContent = childContent;
								//getDeploymentComparisonBeansRecursive(childFile, deploymentCompareBeans, childContent, db, principal, cvsFakeContentId+1);

								break;
							}
						}
					}

					getDeploymentComparisonBeansRecursive(childFile, deploymentCompareBeans, newParentContent, db, principal, cvsFakeContentId+1);
				}
			}
		}
		
		logger.info("End" + parentFile.getName() + " - continuing loop...");
	}
}