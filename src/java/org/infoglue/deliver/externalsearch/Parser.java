/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Erik Stenbäcka
 *
 */
public interface Parser extends ConfigurableDelegate
{
	void init();
	List<Map<String, Object>> parse(InputStream input);
	void destroy();
}
