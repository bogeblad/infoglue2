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

package org.infoglue.deliver.util.graphics;

import org.apache.avalon.framework.logger.Logger;
import org.infoglue.cms.util.CmsLogger;

/**
 * @author Stefan Sik
 *
 * Mapping Avalon Logger to CmsLogger.
 * 
 */
public class FOPCmsLogger implements Logger {

	public void debug(String arg0) {
		CmsLogger.logInfo(arg0);
	}

	public void debug(String arg0, Throwable arg1) {
		CmsLogger.logInfo(arg0, (Exception) arg1);
	}

	public boolean isDebugEnabled() {
		// TODO: Implement and Check CmsLogger.getLogLevel()
		return false;
	}

	public void info(String arg0) {
		CmsLogger.logInfo(arg0);
	}

	public void info(String arg0, Throwable arg1) {
		CmsLogger.logInfo(arg0, (Exception) arg1);
	}

	public boolean isInfoEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void warn(String arg0) {
		CmsLogger.logWarning(arg0);
	}

	public void warn(String arg0, Throwable arg1) {
		CmsLogger.logWarning(arg0, (Exception) arg1);
		
	}

	public boolean isWarnEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void error(String arg0) {
		CmsLogger.logSevere(arg0);
		
	}

	public void error(String arg0, Throwable arg1) {
		CmsLogger.logSevere(arg0, (Exception) arg1);
		
	}

	public boolean isErrorEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void fatalError(String arg0) {
		CmsLogger.logSevere(arg0);
		
	}

	public void fatalError(String arg0, Throwable arg1) {
		CmsLogger.logSevere(arg0, (Exception) arg1);
	}

	public boolean isFatalErrorEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public Logger getChildLogger(String arg0) {
		return null;
	}
}
