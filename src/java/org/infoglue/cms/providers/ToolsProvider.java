package org.infoglue.cms.providers;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.infoglue.cms.applications.common.ToolbarButton;
import org.infoglue.cms.applications.databeans.InfoglueTool;
import org.infoglue.cms.security.InfoGluePrincipal;

public interface ToolsProvider 
{
	public List<InfoglueTool> getTools(InfoGluePrincipal principal, Locale locale);
	
}
