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

package org.infoglue.deliver.applications.actions;

import org.infoglue.cms.applications.common.actions.WebworkAbstractAction;
import org.infoglue.deliver.controllers.kernel.impl.simple.*;
import org.infoglue.cms.util.*;
import org.infoglue.cms.io.*;

import java.util.List;
import java.io.File;

/**
 * This is the action that shows the application state and also can be used to set up surveilence.
 * The idea is to have one command which allways returns a known resultpage if it's ok. Otherwise it prints
 * an error-statement. This action is then called every x minutes by the surveilence and an alarm is raised if something is wrong.
 * We also have a command which can list more status about the application.
 *
 * @author Mattias Bogeblad
 */

public class ViewApplicationStateAction extends WebworkAbstractAction
{
	private boolean databaseConnectionOk 	= false;
	private boolean applicationSettingsOk 	= false;
	private boolean testQueriesOk			= false;
	private boolean diskPermissionOk 		= false;

	/**
	 * The constructor for this action - contains nothing right now.
	 */

    public ViewApplicationStateAction()
    {

    }


    /**
     * This method is the application entry-point. The method does a lot of checks to see if infoglue
     * is installed correctly and if all resources needed are available.
     */

    public String doExecute() throws Exception
    {
		try
		{
			DigitalAssetDeliveryController.getDigitalAssetDeliveryController().deleteDigitalAssets(new Integer(100020));

			List repositoryVOList = RepositoryDeliveryController.getRepositoryDeliveryController().getRepositoryVOList();
			CmsLogger.logInfo("Number of repositories:" + repositoryVOList.size());
			this.databaseConnectionOk = true;
		}
		catch(Exception e)
		{
			CmsLogger.logSevere(e.getMessage(), e);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}

		this.applicationSettingsOk 	= true;
		this.testQueriesOk 			= true;

		try
		{
			File testAsset = new File(CmsPropertyHandler.getProperty("digitalAssetPath") + File.separator + "test.txt");
			FileHelper.writeToFile(testAsset, "ViewApplicationState checking file permissions in asset directory", false);
			testAsset.delete();

			File testLog = new File(new File(CmsPropertyHandler.getProperty("logPath")).getParent() + File.separator + "test.txt");
			FileHelper.writeToFile(testLog, "ViewApplicationState checking file permissions in logs directory", false);
			testLog.delete();

			this.diskPermissionOk = true;
		}
		catch(Exception e)
		{
			CmsLogger.logSevere(e.getMessage(), e);
		}

        return "success";
    }



	public boolean getIsApplicationSettingsOk()
	{
		return applicationSettingsOk;
	}

	public boolean getIsDatabaseConnectionOk()
	{
		return databaseConnectionOk;
	}

	public boolean getIsDiskPermissionOk()
	{
		return diskPermissionOk;
	}

	public boolean getIsTestQueriesOk()
	{
		return testQueriesOk;
	}

}
