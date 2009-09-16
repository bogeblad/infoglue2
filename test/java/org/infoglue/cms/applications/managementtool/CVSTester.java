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
 *
 * $Id: CVSTester.java,v 1.1.2.1 2009/09/16 12:46:23 mattias Exp $
 */

package org.infoglue.cms.applications.managementtool;

import java.io.File;

import org.infoglue.common.util.cvs.connectors.NetBeansConnector;
import org.infoglue.common.util.vc.connectors.VCConnector;

public class CVSTester
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{		
		try
		{			
			String cvsRoot		= ":pserver:dahlgrj@localhost:/Users/dahlgrj/Versionshantering";
			String password 	= "ANGE_ETT_LÖSENORD";
			String localPath	= "C:/Users/dahlgrj/Desktop/CVSTest/";
			String moduleName	= "testmapp/mapp1/mapp2/";
		    String tagName		= "xxx";
		    
		    File file1			= new File("C:/Users/dahlgrj/Desktop/CVSTest/testmapp/mapp1/mapp2/testfil.txt");
		    File file2			= new File("C:/Users/dahlgrj/Desktop/CVSTest/testmapp/mapp1/mapp2/testfil2.txt");
		    File[] files		= new File[] {file1, file2};
			
		    VCConnector connector = new NetBeansConnector(cvsRoot, localPath, password);
		    
		    //connector.checkOutModuleFromHead(moduleName);
		    //connector.checkOutModuleFromTag(moduleName, tagName);
		    connector.updateFilesFromHead(files);
		    //connector.updateFilesFromTag(files, tagName);
		    //connector.commitFilesToHead(files);
		    //connector.commitFilesToTag(files, tagName);
		    //connector.tagFiles(files, tagName);
		} 
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
