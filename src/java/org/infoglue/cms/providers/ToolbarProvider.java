package org.infoglue.cms.providers;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.infoglue.cms.applications.common.ToolbarButton;
import org.infoglue.cms.security.InfoGluePrincipal;

public interface ToolbarProvider 
{
	public List<ToolbarButton> getRightToolbarButtons(String toolbarKey, InfoGluePrincipal principal, Locale locale, HttpServletRequest request, boolean disableCloseButton);
	
	public List<ToolbarButton> getToolbarButtons(String toolbarKey, InfoGluePrincipal principal, Locale locale, HttpServletRequest request, boolean disableCloseButton);

	public List<ToolbarButton> getFooterToolbarButtons(String toolbarKey, InfoGluePrincipal principal, Locale locale, HttpServletRequest request, boolean disableCloseButton);


}
