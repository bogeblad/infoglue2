/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.util.Map;

import org.infoglue.cms.exception.ConfigurationError;

/**
 * @author Erik Stenbäcka
 *
 */
public interface ConfigurableDelegate
{
	void setConfig(Map<String, String> config) throws ConfigurationError;
}
