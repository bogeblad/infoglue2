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

package org.infoglue.cms.applications.managementtool.actions;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.io.FileHelper;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.deliver.util.VelocityTemplateProcessor;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

/**
 * This class/action returns the WYSIWYG configuration in full.
 *
 * @author Mattias Bogeblad
 */

public class WYSIWYGPropertiesAction extends InfoGlueAbstractAction
{
	private static final long serialVersionUID = 1L;

	private Integer repositoryId = null;

	private String WYSIWYGProperties = "";
	private String StylesXML = "";
	
    public String doExecute() throws Exception
    {
    	return "success";
    }

    public String doViewStylesXML() throws Exception
    {
    	return "successStylesXML";
    }

	/**
	 * This method gets the WYSIWYG Properties
	 */
	
	public String getWYSIWYGProperties() throws Exception
	{
	    try
	    {
		    this.WYSIWYGProperties = getPrincipalPropertyValue("WYSIWYGConfig", false);
		    getLogger().info("WYSIWYGProperties:" + WYSIWYGProperties);
		    if(this.WYSIWYGProperties == null || this.WYSIWYGProperties.equalsIgnoreCase("") && this.repositoryId != null)
		    {
		        getLogger().info("Getting WYSIWYGProperties for repository...");
				Map args = new HashMap();
			    args.put("globalKey", "infoglue");
			    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
			    
			    byte[] WYSIWYGConfigBytes = ps.getData("repository_" + this.repositoryId + "_WYSIWYGConfig");
			    getLogger().info("WYSIWYGConfigBytes:" + WYSIWYGConfigBytes);
			    if(WYSIWYGConfigBytes != null)
			    {
			    	this.WYSIWYGProperties = new String(WYSIWYGConfigBytes, "UTF-8");
			    }
		    }
		     
		    getLogger().info("this.WYSIWYGProperties:" + this.WYSIWYGProperties);
	    }
	    catch(Exception e)
	    {
	        getLogger().error("Could not fetch WYSIWYG Configuration: " + e.getMessage(), e);
	    }
	    finally
	    {
	        try
            {
                if(this.WYSIWYGProperties == null || this.WYSIWYGProperties.equals(""))
                    this.WYSIWYGProperties = FileHelper.getFileAsString(new File(CmsPropertyHandler.getContextRootPath() + "cms/contenttool/WYSIWYGConfig.js"));
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
	        
	    }

	    Map parameters = new HashMap();
	    parameters.put("request", this.getRequest());
	    
		StringWriter tempString = new StringWriter();
		PrintWriter pw = new PrintWriter(tempString);
		new VelocityTemplateProcessor().renderTemplate(parameters, pw, this.WYSIWYGProperties);
		this.WYSIWYGProperties = tempString.toString();

	    this.getResponse().setContentType("text/javascript");
	    
	    return this.WYSIWYGProperties;
	}

	/**
	 * This method gets the Styles XML
	 */
	
	public String getStylesXML()
	{
	    try
	    {
	        this.StylesXML = getPrincipalPropertyValue("StylesXML", false);
		    getLogger().info("this.StylesXML:" + this.StylesXML);
		    if(this.StylesXML == null || this.StylesXML.equalsIgnoreCase("") && this.repositoryId != null)
		    {
		        getLogger().info("Getting StylesXML for repository...");
				Map args = new HashMap();
			    args.put("globalKey", "infoglue");
			    PropertySet ps = PropertySetManager.getInstance("jdbc", args);
			    
			    byte[] StylesXMLBytes = ps.getData("repository_" + this.repositoryId + "_StylesXML");
			    if(StylesXMLBytes != null)
			    {
			    	this.StylesXML = new String(StylesXMLBytes, "UTF-8");
			    }
		    }
	    }
	    catch(Exception e)
	    {
	        getLogger().error("Could not fetch Styles XML: " + e.getMessage(), e);
	    }
	    finally
	    {
	        try
            {
	            if(this.StylesXML == null || this.StylesXML.equals(""))
	                this.StylesXML = FileHelper.getFileAsString(new File(CmsPropertyHandler.getContextRootPath() + "cms/contenttool/StylesXML.xml"));
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
	    }
	    
	    this.getResponse().setContentType("text/xml");
	    return this.StylesXML;
	}

	
    public void setRepositoryId(Integer repositoryId)
    {
        this.repositoryId = repositoryId;
    }
}
