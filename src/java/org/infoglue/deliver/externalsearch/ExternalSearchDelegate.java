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

package org.infoglue.deliver.externalsearch;

import java.util.Map;

import org.infoglue.cms.exception.ConfigurationError;

/**
 * @author Erik Stenbäcka
 */
public interface ExternalSearchDelegate
{
	/**
	 * Called sometime before the delegate is to be used the first time. The method will only be called once
	 */
	void init();
	/**
	 * Sets the configuration for the delegate. It is up to each implementation of the interface to provide information
	 * about what values are expected and required in the provided map.
	 * @param config The configuration for the delegate. See implementing class for more details.
	 * @throws ConfigurationError Thrown if a field is missing or has an unexpected format.
	 */
	void setConfig(Map<String, String> config) throws ConfigurationError;
	/**
	 * Called sometime after the last usage of the delegate and before the delegate is thrown away. The method will only be called once.
	 */
	void destroy();
}
