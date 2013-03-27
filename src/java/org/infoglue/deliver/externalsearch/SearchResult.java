/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.util.List;
import java.util.Locale;

/**
 * @author Erik Stenbäcka
 *
 */
public class SearchResult
{
	public static final String RESULT = "externalSearchResultObject";
	public final List<Object> result;
	public final Integer totalSize;

	public SearchResult(List<Object> result, Integer totalSize)
	{
		this.result = result;
		this.totalSize = totalSize;
	}
	public static String getResultLabel(Locale locale)
	{
		if (locale == null)
		{
			return RESULT;
		}
		else
		{
			return RESULT + locale.getLanguage();
		}
	}
}
