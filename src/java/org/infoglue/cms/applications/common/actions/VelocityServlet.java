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

import org.infoglue.cms.exception.ConfigurationError;
import org.infoglue.cms.applications.common.Session;
import org.infoglue.cms.applications.common.VisualFormatter;
import org.infoglue.cms.util.StringManager;
import org.infoglue.cms.util.StringManagerFactory;
import org.infoglue.cms.util.CmsLogger;

import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;

import webwork.view.velocity.WebWorkVelocityServlet;


/**
 * 
 * This class puts some things into the context object that you should
 * be aware of (check the superclasses as well):
 * <pre>
 * "ui" - the StringManagerChain handling all localized strings.
 * </pre>
 *
 * @author <a href="mailto:meat_for_the_butcher@yahoo.com">Patrik Nyborg</a>
 */
public class VelocityServlet extends WebWorkVelocityServlet 
{
  // --- [Constants] -----------------------------------------------------------

  private static final String PACKAGE_NAMES_INIT_PARAM = "packageNames";



  // --- [Attributes] ----------------------------------------------------------

  //
  private String packageNames[];



  // --- [Static] --------------------------------------------------------------
  // --- [Constructors] --------------------------------------------------------
  // --- [Public] --------------------------------------------------------------
  // --- [X implementation] ----------------------------------------------------
  // --- [org.apache.velocity.servlet.VelocityServlet Overrides] ---------------

  /** 
   * Performs initialization of this servlet. Called by the servlet container on loading.
   *
   * @param configuration The servlet configuration to apply.
   *
   * @exception ServletException
   */
  public void init(ServletConfig configuration) throws ServletException {
    super.init(configuration);
    initializePackageNames(configuration.getInitParameter(PACKAGE_NAMES_INIT_PARAM));    
  }

  

  // --- [Package protected] ---------------------------------------------------
  // --- [Private] -------------------------------------------------------------

  /**
   *
   *
   * @param commaSeparatedPackageNames comma-separareted list of package names.
   */
  private void initializePackageNames(String commaSeparatedPackageNames) {
    if(commaSeparatedPackageNames == null) {
      throw new ConfigurationError("web.xml not properly configured, did not contain the " + PACKAGE_NAMES_INIT_PARAM + " init param for the VelocityServlet.");
    }

    final StringTokenizer st = new StringTokenizer(commaSeparatedPackageNames, ",");
    this.packageNames = new String[st.countTokens()];
    for(int i=0; st.hasMoreTokens(); ++i) { 
      this.packageNames[i] = st.nextToken();
    }
  }

  /**
   * @param locale
   */
  private StringManager getStringManagerChain(Locale locale) 
  {
    return StringManagerFactory.getPresentationStringManager(this.packageNames, locale);
  }


  // --- [Protected] -----------------------------------------------------------

  /**
   *
   */
    protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context context) throws Exception 
    {
        CmsLogger.logInfo("handleRequest in VelocityServlet");
        final HttpSession httpSession = request.getSession();
        CmsLogger.logInfo("httpSession:" + httpSession);
        final Session session         = new Session(httpSession);
        
        //<todo>this should definitely not be placed here
        if(session.getLocale() == null) 
        {
            session.setLocale(java.util.Locale.ENGLISH);
        }
        //</todo>

        context.put("ui", getStringManagerChain(session.getLocale()));
        context.put("formatter", new VisualFormatter());
        
        request.setCharacterEncoding("UTF-8");	
		response.setContentType("text/html; charset=UTF-8");
		
        return super.handleRequest(request, response, context);
    }



  // --- [Inner classes] -------------------------------------------------------
}