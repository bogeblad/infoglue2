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
package org.infoglue.cms.applications.databeans;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.core.ProcessArrayCallback;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.managementtool.actions.ExportRepositoryAction;
import org.infoglue.cms.security.InfoGluePrincipal;

/**
 * This bean allows for processes to give information about the process itself and what the status is.
 * The bean has a listener option in which an external class can ask it to report process when it happens (push).
 * 
 * @author Mattias Bogeblad
 */

public class ProcessBean
{
    private final static Logger logger = Logger.getLogger(ProcessBean.class.getName());

    //The class has its own factory and list of all active processBeans
    private static List<ProcessBean> processBeans = new ArrayList<ProcessBean>();
    public static List<ProcessBean> getProcessBeans()
    {
    	return processBeans;
    }
    public static List<ProcessBean> getProcessBeans(String processName)
    {
    	List<ProcessBean> processBeansWithName = new ArrayList<ProcessBean>();
    	for(ProcessBean processBean : processBeans)
    	{
    		if(processBean.getProcessName().equals(processName))
    			processBeansWithName.add(processBean);
    	}
    	
    	return processBeansWithName;
    }
    public static ProcessBean getProcessBean(String processName, String processId)
    {
    	for(ProcessBean processBean : processBeans)
    	{
    		if(processBean.getProcessName().equals(processName) && processBean.getProcessId().equals(processId))
    			return processBean;
    	}
    	
    	return null;
    }
    public static ProcessBean createProcessBean(String processName, String processId)
    {
    	ProcessBean processBean = new ProcessBean(processName, processId);
    	getProcessBeans().add(processBean);
    	
    	return processBean;
    }
    //-End factory stuff
    
    
    public static final int NOT_STARTED = 0;
    public static final int RUNNING = 1;
    public static final int FINISHED = 2;
    
	//ID can be any string the process decides while processName is a general name for all instances of a certain process.
    private String processName;
    private String processId;
    private int status = NOT_STARTED;
    private Date started = new Date();
	private Date finished = new Date();
    
    private List<ProcessBeanListener> listeners = new ArrayList<ProcessBeanListener>();
    private List<String> processEvents = new ArrayList<String>();
    private Map<String,Map<String,Object>> artifacts = new HashMap<String,Map<String,Object>>();
    private List<File> files = new ArrayList<File>();

    private ProcessBean()
    {
    }

    private ProcessBean(String processName, String processId)
    {
    	this.processName = processName;
    	this.processId = processId;
    }
    
    /**
     * This method sends the event description to all listeners.
     * 
     * @param eventDescription
     */
    public void updateProcess(String eventDescription)
    {
    	processEvents.add(eventDescription);
    	for(ProcessBeanListener processBeanListener : listeners)
    	{
    		try
    		{
        		processBeanListener.processUpdated(eventDescription);
    		}
    		catch (Exception e) 
    		{
    			logger.error("Error updating ProcessBeanListener: " + e.getMessage());
			}
    	}
    }

    /**
     * This method sends the new artifact to all listeners.
     * 
     * @param eventDescription
     */
    public void updateProcessArtifacts(String artifactId, String url, File file)
    {
    	Map<String,Object> artifactDescMap = new HashMap<String,Object>();
    	artifactDescMap.put("url", url);
    	artifactDescMap.put("file", file);
    	
    	artifacts.put(artifactId, artifactDescMap);
    	
    	files.add(file);
    	for(ProcessBeanListener processBeanListener : listeners)
    	{
    		try
    		{
        		processBeanListener.processArtifactsUpdated(artifactId, url, file);
    		}
    		catch (Exception e) 
    		{
    			logger.error("Error updating ProcessBeanListener: " + e.getMessage());
			}
    	}
    }

    /**
     * This method removes the bean from list of active processes and clears all references.
     */
	public void removeProcess()
	{
		updateProcess("Process removed");
		this.listeners.clear();
		if(files != null)
		{
			for(File file : files)
			{
				file.delete();
			}
		}
		getProcessBeans().remove(this);
	}

	public String getProcessName()
	{
		return processName;
	}

	public String getProcessId()
	{
		return processId;
	}

    public int getStatus()
    {
    	return this.status;
    }

	public Date getStarted() 
	{
		return started;
	}

	public Date getFinished() 
	{
		return finished;
	}

    public void setStatus(int status)
    {
    	this.status = status;
    	if(status == RUNNING)
    		this.started = new Date();
    	else if(status == FINISHED)
    		this.finished = new Date();
    }
    
    public List<String> getProcessEvents()
    {
    	return this.processEvents;
    }
    
    public Map<String,Map<String,Object>> getProcessArtifacts()
    {
    	return this.artifacts;
    }
}
