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

package org.infoglue.cms.util;

import org.infoglue.cms.exception.Bug;
import org.infoglue.cms.exception.ConfigurationError;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 */
class SimpleStringManager implements StringManager {
  // --- [Constants] -----------------------------------------------------------
  // --- [Attributes] ----------------------------------------------------------

  // The ResourceBundle for this StringManager.
  private ResourceBundle bundle;



  // --- [Static] --------------------------------------------------------------
  // --- [Constructors] --------------------------------------------------------
  
    SimpleStringManager(String bundleName, Locale locale) 
    { 
        try 
        { 
            CmsLogger.logInfo("Created a SimpleStringManager for package bundleName" + bundleName);
            this.bundle = ResourceBundle.getBundle(bundleName, locale);
        } 
        catch(MissingResourceException e) 
        {
            throw new ConfigurationError("Unable to find resource bundle: " + e.getMessage(), e);
        } 
        catch(NullPointerException e) 
        {
            throw new Bug("Unable to create resource bundle.", e);
        }
    }



  // --- [Public] --------------------------------------------------------------
  // --- [org.infoglue.cms.util.StringManager implementation] -----------------

  /**
   *
   */
    public final String getString(String key) 
    {
        try 
        {
            //CmsLogger.logInfo("Trying to find a string for key " + key + " in " + this);
            return this.bundle.getString(key);
        } 
        catch(MissingResourceException e) 
        {
        	CmsLogger.logWarning("Error trying to find a string for key " + key, e);
            throw new ConfigurationError("Key not found.", e);
        } 
        catch(Throwable t) 
        {
        	CmsLogger.logWarning("Error trying to find a string for key " + key);
            throw new Bug("Unable to fetch the value for the specified key.", t);
        }
    }

  /**
   *
   */
  public final String getString(String key, Object args[]) {
    return MessageFormat.format(getString(key), args);
  }

  /**
   *
   */
  public final String getString(String key, Object arg) {
    return getString(key, new Object[]{ arg });
  }

  /**
   *
   */
  public final String getString(String key, Object arg1, Object arg2) {
    return getString(key, new Object[]{ arg1, arg2 });
  }

  /**
   *
   */
  public final String getString(String key, Object arg1, Object arg2, Object arg3) {
    return getString(key, new Object[]{ arg1, arg2, arg3 });
  }

  // --- [X Overrides] ---------------------------------------------------------
  // --- [Package protected] ---------------------------------------------------

  /**
   *
   */
  boolean containsKey(String key) {
    try {
      this.bundle.getString(key);
      return true;
    } catch(MissingResourceException e) {
      return false;
    }
  }



  // --- [Private] -------------------------------------------------------------
  // --- [Protected] -----------------------------------------------------------
  // --- [Inner classes] -------------------------------------------------------
}
  


