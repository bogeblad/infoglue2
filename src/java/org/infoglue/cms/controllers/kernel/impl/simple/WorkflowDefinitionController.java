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

import org.infoglue.cms.applications.databeans.AssetKeyDefinition;
import org.infoglue.cms.entities.kernel.*;
import org.infoglue.cms.entities.management.*;
import org.infoglue.cms.entities.management.impl.simple.ContentTypeDefinitionImpl;
import org.infoglue.cms.entities.workflow.WorkflowDefinition;
import org.infoglue.cms.entities.workflow.WorkflowDefinitionVO;
import org.infoglue.cms.entities.workflow.impl.simple.WorkflowDefinitionImpl;
import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.deliver.util.CacheController;
import org.infoglue.cms.security.InfoGluePrincipal;

import org.infoglue.cms.util.DomainUtils;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.*;

import javax.xml.transform.TransformerException;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

/**
 * @author Mattias Bogeblad
 */

public class WorkflowDefinitionController extends BaseController
{

	/**
	 * Factory method
	 */

	public static WorkflowDefinitionController getController()
	{
		return new WorkflowDefinitionController();
	}

    public WorkflowDefinitionVO getWorkflowDefinitionVOWithId(Integer workflowDefinitionId) throws SystemException, Bug
    {
		return (WorkflowDefinitionVO) getVOWithId(WorkflowDefinitionImpl.class, workflowDefinitionId);
    }

    public WorkflowDefinition getWorkflowDefinitionWithId(Integer workflowDefinitionId, Database db) throws SystemException, Bug
    {
		return (WorkflowDefinition) getObjectWithId(WorkflowDefinitionImpl.class, workflowDefinitionId, db);
    }

    public List getWorkflowDefinitionVOList() throws SystemException, Bug
    {
		/*
        String key = "contentTypeDefinitionVOList";
		getLogger().info("key:" + key);
		List cachedContentTypeDefinitionVOList = (List)CacheController.getCachedObject("contentTypeDefinitionCache", key);
		if(cachedContentTypeDefinitionVOList != null)
		{
			getLogger().info("There was an cached contentTypeDefinitionVOList:" + cachedContentTypeDefinitionVOList.size());
			return cachedContentTypeDefinitionVOList;
		}
		*/
        
		List workflowDefinitionVOList = getAllVOObjects(WorkflowDefinitionImpl.class, "workflowDefinitionId");

		//CacheController.cacheObject("contentTypeDefinitionCache", key, contentTypeDefinitionVOList);

		return workflowDefinitionVOList;
    }
    

    public WorkflowDefinitionVO create(WorkflowDefinitionVO workflowDefinitionVO) throws ConstraintException, SystemException
    {
        WorkflowDefinition workflowDefinition = new WorkflowDefinitionImpl();
        workflowDefinition.setValueObject(workflowDefinitionVO);
        workflowDefinition = (WorkflowDefinition) createEntity(workflowDefinition);
        return workflowDefinition.getValueObject();
    }

    public void delete(WorkflowDefinitionVO workflowDefinitionVO) throws ConstraintException, SystemException
    {
    	deleteEntity(WorkflowDefinitionImpl.class, workflowDefinitionVO.getWorkflowDefinitionId());
    }

    public WorkflowDefinitionVO update(WorkflowDefinitionVO workflowDefinitionVO) throws ConstraintException, SystemException
    {
    	return (WorkflowDefinitionVO) updateEntity(WorkflowDefinitionImpl.class, workflowDefinitionVO);
    }

	/**
	 * This is a method that gives the user back an newly initialized ValueObject for this entity that the controller
	 * is handling.
	 */

	public BaseEntityVO getNewVO()
	{
		return new ContentTypeDefinitionVO();
	}
}
