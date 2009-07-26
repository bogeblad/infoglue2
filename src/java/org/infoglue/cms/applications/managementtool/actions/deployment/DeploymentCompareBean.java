package org.infoglue.cms.applications.managementtool.actions.deployment;

import java.util.HashMap;
import java.util.Map;

public class DeploymentCompareBean
{
	private Object localVersion;
	private Object remoteVersion;
	private boolean repositoryMissing;
	
	public boolean isRepositoryMissing() 
	{
		return repositoryMissing;
	}

	public void setRepositoryMissing(boolean repositoryMissing) 
	{
		this.repositoryMissing = repositoryMissing;
	}

	public Object getLocalVersion()
	{
		return localVersion;
	}
	
	public void setLocalVersion(Object localVersion)
	{
		this.localVersion = localVersion;
	}
	
	public Object getRemoteVersion()
	{
		return remoteVersion;
	}
	
	public void setRemoteVersion(Object remoteVersion)
	{
		this.remoteVersion = remoteVersion;
	}

}
