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

package org.infoglue.cms.applications.common.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.infoglue.cms.applications.common.actions.InfoGlueAbstractAction;
import org.infoglue.cms.controllers.kernel.impl.simple.InterceptionPointController;
import org.infoglue.cms.controllers.kernel.impl.simple.SubscriptionController;
import org.infoglue.cms.entities.management.InterceptionPointVO;
import org.infoglue.cms.entities.management.SubscriptionFilterVO;
import org.infoglue.cms.entities.management.SubscriptionVO;


/** 
 * This class shows which roles has access to the siteNode.
 */

public class SubscriptionsAction extends InfoGlueAbstractAction
{
    private final static Logger logger = Logger.getLogger(SubscriptionsAction.class.getName());

	private static final long serialVersionUID = 1L;
	
	private Integer interceptionPointId = null;
	private String interceptionPointName = null;
	private String[] interceptionPointCategory = null;
	private String[] explicitInterceptionPointNames = null;
	private String entityName = null;
	private String entityId = null;
	private String extraParameters = "";
	private String returnAddress;
	private Integer[] subscribedInterceptionPointId = null;

	private Map interceptionPointsGroupsMap = new HashMap();
	private Map subscriptions = new HashMap();
	private String interceptionPointIdString = "";
	
	//Global subscriptions
	private Collection subscriptionVOList = null;
	private String name;
	private String filterType;
	private String filterCondition;
	private Boolean isAndCondition;
	
	private static SubscriptionController subscriptionsController = SubscriptionController.getController();
	
	public String doInput() throws Exception
    {
		if(explicitInterceptionPointNames != null && explicitInterceptionPointNames.length > 0)
		{
			for(int i=0; i<explicitInterceptionPointNames.length; i++)
			{
				String interceptionPointName = explicitInterceptionPointNames[i];
				InterceptionPointVO interceptionPointVO = InterceptionPointController.getController().getInterceptionPointVOWithName(interceptionPointName);
				interceptionPointIdString += interceptionPointVO.getId() + ",";
				
				List subscriptionVOList = subscriptionsController.getSubscriptionVOList(interceptionPointVO.getId(), null, null, entityName, entityId, this.getInfoGluePrincipal().getName(), null);
				if(subscriptionVOList != null && subscriptionVOList.size() > 0)
					subscriptions.put(interceptionPointVO.getId(), true);
				else
					subscriptions.put(interceptionPointVO.getId(), false);
				
				List interceptionPointVOList = (List)interceptionPointsGroupsMap.get(interceptionPointVO.getCategory());
				if(interceptionPointVOList != null)
					interceptionPointVOList.add(interceptionPointVO);
				else
				{
					interceptionPointVOList = new ArrayList();
					interceptionPointVOList.add(interceptionPointVO);
					interceptionPointsGroupsMap.put(interceptionPointVO.getCategory(), interceptionPointVOList);
				}
			}
		}
		else
		{
			for(int i=0; i<interceptionPointCategory.length; i++)
			{
				String interceptionPointCategoryName = interceptionPointCategory[i];
				List interceptionPointVOList = InterceptionPointController.getController().getInterceptionPointVOList(interceptionPointCategoryName);
				Iterator interceptionPointVOListIterator = interceptionPointVOList.iterator();
				while(interceptionPointVOListIterator.hasNext())
				{
					InterceptionPointVO interceptionPointVO = (InterceptionPointVO)interceptionPointVOListIterator.next();
					interceptionPointIdString += interceptionPointVO.getId() + ",";
					
					List subscriptionVOList = subscriptionsController.getSubscriptionVOList(interceptionPointVO.getId(), null, null, entityName, entityId, this.getInfoGluePrincipal().getName(), null);
					if(subscriptionVOList != null && subscriptionVOList.size() > 0)
						subscriptions.put(interceptionPointVO.getId(), true);
					else
						subscriptions.put(interceptionPointVO.getId(), false);
				}
				this.interceptionPointsGroupsMap.put(interceptionPointCategoryName, interceptionPointVOList);
			}
		}
		
		return INPUT;
    }
    
    public String doExecute() throws Exception
    {
    	String[] interceptionPointIds = interceptionPointIdString.split(",");
    	for(int i=0; i<interceptionPointIds.length; i++)
    	{
    		String key = "subscription_" + interceptionPointIds[i] + "_" + extraParameters;
    		System.out.println("removing key:" + key);
			List subscriptionVOList = subscriptionsController.getSubscriptionVOList(interceptionPointId, null, null, entityName, entityId, this.getInfoGluePrincipal().getName(), null);
    		Iterator<SubscriptionVO> subscriptionVOListIterator = subscriptionVOList.iterator();
    		while(subscriptionVOListIterator.hasNext())
    		{
    			SubscriptionVO subscriptionVO = subscriptionVOListIterator.next();
    			subscriptionsController.delete(subscriptionVO);
    		}
    	}
    	
    	System.out.println("subscribedInterceptionPointId:" + subscribedInterceptionPointId);
	    for(int i=0; i<subscribedInterceptionPointId.length; i++)
    	{
    		Integer interceptionPointId = subscribedInterceptionPointId[i];
    		System.out.println("interceptionPointId:" + interceptionPointId);
    		SubscriptionVO subscriptionVO = new SubscriptionVO();
    		subscriptionVO.setInterceptionPointId(interceptionPointId);
    		subscriptionVO.setEntityName(entityName);
    		subscriptionVO.setEntityId(entityId);
    		subscriptionVO.setUserName(this.getInfoGluePrincipal().getName());
    		
    		subscriptionsController.create(subscriptionVO);
    	}
    	
    	return "success";
    }

    
	public String doInputGlobalSubscriptions() throws Exception
    {
		this.subscriptionVOList = subscriptionsController.getSubscriptionVOList(null, null, new Boolean(true), null, null, this.getInfoGluePrincipal().getName(), null);
		
		return "inputGlobalSubscriptions";
    }
    
    public String doGlobalSubscriptions() throws Exception
    {
    	SubscriptionVO subscriptionVO = new SubscriptionVO();
    	subscriptionVO.setIsGlobal(true);
    	subscriptionVO.setInterceptionPointId(interceptionPointId);
    	subscriptionVO.setName(name);
    	subscriptionVO.setUserName(this.getInfoGluePrincipal().getName());
    	
    	List<SubscriptionFilterVO> subscriptionFilterVOList = new ArrayList<SubscriptionFilterVO>();
    	
    	SubscriptionFilterVO subscriptionFilterVO = new SubscriptionFilterVO();
    	subscriptionFilterVO.setFilterType(filterType);
    	subscriptionFilterVO.setFilterCondition(filterCondition);
    	subscriptionFilterVO.setIsAndCondition(isAndCondition);
    	subscriptionFilterVOList.add(subscriptionFilterVO);
    	
    	subscriptionsController.create(subscriptionVO, subscriptionFilterVOList);
    	
    	return "successGlobalSubscriptions";
    }
    
	public String getReturnAddress()
	{
		return returnAddress;
	}

	public void setReturnAddress(String returnAddress)
	{
		this.returnAddress = returnAddress;
	}

	public Integer getInterceptionPointId()
	{
		return this.interceptionPointId;
	}

	public void setInterceptionPointId(Integer interceptionPointId)
	{
		this.interceptionPointId = interceptionPointId;
	}

	public String getExtraParameters()
	{
		return this.extraParameters;
	}

	public String getInterceptionPointName()
	{
		return this.interceptionPointName;
	}

	public void setExtraParameters(String extraParameters)
	{
		this.extraParameters = extraParameters;
	}

	public void setInterceptionPointName(String interceptionPointName)
	{
		this.interceptionPointName = interceptionPointName;
	}

	public String[] getInterceptionPointCategory()
	{
		return this.interceptionPointCategory;
	}

	public void setInterceptionPointCategory(String[] interceptionPointCategory)
	{
		this.interceptionPointCategory = interceptionPointCategory;
	}

	public Map getInterceptionPointsGroupsMap()
	{
		return interceptionPointsGroupsMap;
	}

	public String[] getExplicitInterceptionPointNames()
	{
		return explicitInterceptionPointNames;
	}

	public void setExplicitInterceptionPointNames(String[] explicitInterceptionPointNames)
	{
		this.explicitInterceptionPointNames = explicitInterceptionPointNames;
	}

	public Map getSubscriptions()
	{
		return subscriptions;
	}

	public Integer[] getSubscribedInterceptionPointId()
	{
		return subscribedInterceptionPointId;
	}

	public void setSubscribedInterceptionPointId(Integer[] subscribedInterceptionPointId)
	{
		this.subscribedInterceptionPointId = subscribedInterceptionPointId;
	}

	public String getInterceptionPointIdString()
	{
		return interceptionPointIdString;
	}

	public void setInterceptionPointIdString(String interceptionPointIdString)
	{
		this.interceptionPointIdString = interceptionPointIdString;
	}

	public String getEntityName()
	{
		return entityName;
	}

	public void setEntityName(String entityName)
	{
		this.entityName = entityName;
	}

	public String getEntityId()
	{
		return entityId;
	}

	public void setEntityId(String entityId)
	{
		this.entityId = entityId;
	}

	public Collection getSubscriptionVOList()
	{
		return subscriptionVOList;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getFilterType()
	{
		return filterType;
	}

	public void setFilterType(String filterType)
	{
		this.filterType = filterType;
	}

	public String getFilterCondition()
	{
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition)
	{
		this.filterCondition = filterCondition;
	}

	public Boolean getIsAndCondition()
	{
		return isAndCondition;
	}

	public void setIsAndCondition(Boolean isAndCondition)
	{
		this.isAndCondition = isAndCondition;
	}
}