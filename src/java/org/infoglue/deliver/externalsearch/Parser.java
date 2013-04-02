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
import java.util.List;
import java.util.Map;

import com.sun.star.form.DatabaseDeleteEvent;

/**
 * Parsers are responsible for converting raw data into indexable objects. The result of a parsing
 * should be assumed to be sent into an {@link Indexer}.
 * 
 * @author Erik Stenbäcka
 */
public interface Parser extends ExternalSearchDelegate
{
	/**
	 * Parsers the data received in the input stream. It is up to author of the service
	 * configuration to make sure that the used {@link DatabaseDeleteEvent} and the used
	 * {@link Parser} match.
	 * @param input The input that should be parsed. May be null. A null value indicates that the data retrieval went bad.
	 * @return A list of parsed objects.
	 */
	List<Map<String, Object>> parse(InputStream input);
}
