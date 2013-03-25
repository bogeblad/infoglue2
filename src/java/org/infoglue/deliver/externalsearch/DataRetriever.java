/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.io.InputStream;

/**
 * @author Erik Stenbäcka
 *
 */
public interface DataRetriever extends ConfigurableDelegate
{
	void init();
	InputStream openConnection();
	boolean closeConnection();
	void destroy();
}
