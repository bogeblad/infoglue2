package org.infoglue.cms.util.workflow;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.security.InfoGlueRole;
import org.infoglue.cms.util.CmsLogger;

import java.util.*;

/**
 * This action checks if the user has a particular role.
 * 
 * @author Mattias Bogeblad
 */

public class InfoGlueAuthorizationCondition implements Condition 
{
    private static final Log log = LogFactory.getLog(InfoGlueAuthorizationCondition.class);

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) 
    {
        boolean passesCondition = true;
        
        try 
        {
            WorkflowContext context = (WorkflowContext) transientVars.get("context");
            String roleName = (String)args.get("roleName");
            String userName = (String)args.get("userName");
            
            CmsLogger.logInfo("passesCondition.............");
            CmsLogger.logInfo("caller:" + context.getCaller());
            CmsLogger.logInfo("roleName:" + roleName);
            CmsLogger.logInfo("userName:" + userName);
            
            InfoGluePrincipal principal = UserControllerProxy.getController().getUser(context.getCaller());
            
            if(userName != null && userName.length() > 0 && !principal.getName().equals(userName))
                passesCondition = false;
            
            if(roleName != null && roleName.length() > 0)
            {
                boolean hasRole = false;
	            List roles = principal.getRoles();
	            Iterator rolesIterator = roles.iterator();
	            while(rolesIterator.hasNext())
	            {
	                InfoGlueRole role = (InfoGlueRole)rolesIterator.next();
	                if(role.getName().equalsIgnoreCase(roleName))
	                    hasRole = true;
	            }
	            
	            if(!hasRole)
	                passesCondition = false;
            }            
        } 
        catch (Exception e) 
        {
            CmsLogger.logSevere("A severe error occurred when checking workflow authorization:" + e.getMessage(), e);
        }
        
        return passesCondition;
    } 
} 