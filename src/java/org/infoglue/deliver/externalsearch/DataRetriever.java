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

/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.io.InputStream;

/**
 * A DataRetriever is responsible for getting data that will be used by a {@link Parser}.
 * 
 * @author Erik Stenbäcka
 */
public interface DataRetriever extends ExternalSearchDelegate
{
	/**
	 * <p>Gets data from some source. It is the caller of this method's responsibility to
	 * call {@link #closeConnection()} when it is done parsing the data.</p>
	 * <p>The result of calling this method twice without calling closeConnection in between is undefined.</p>
	 * @return An open input stream containing data that can be parsed by a {@link Parser} or null if something went wrong.
	 */
	InputStream openConnection();
	/**
	 * Closes the connection that was opened by openConnection. The result of calling this method
	 * when there is no open connection is undefined.
	 * @return True if the connection was closed, false otherwise.
	 */
	boolean closeConnection();
}
