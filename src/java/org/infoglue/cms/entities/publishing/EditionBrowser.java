package org.infoglue.cms.entities.publishing;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This is a helper class that will provide convenience methods for getting information
 * about the page browsing aspect of Editions. This could easily be made generic and
 * should if we find the need to make more paginated pages.
 *
 * @author <a href="frank@phase2technology.com">Frank Febbraro</a>
 */
public class EditionBrowser implements Serializable
{
	private int totalEditions = 0;
	private int startIndex = 0;
	private int pageSize = 0;
	private int totalPages = 0;
	private int currentPage = 0;
	private List editions = new ArrayList();

	public EditionBrowser(int totalEditions, int pageSize, int startIndex)
	{
		this.totalEditions = totalEditions;
		this.pageSize = pageSize;
		this.startIndex = startIndex;
		init();
	}

	private void init()
	{
		BigDecimal total = new BigDecimal(totalEditions);
		BigDecimal page = new BigDecimal(pageSize);
		BigDecimal start = new BigDecimal(startIndex);
		totalPages = Math.max(1, total.divide(page, 0, BigDecimal.ROUND_UP).intValue());
		currentPage = Math.max(1, start.divide(page, 0, BigDecimal.ROUND_UP).intValue());
	}

	public int getTotalEditions()		{ return totalEditions; }
	public void setTotalEditions(int i)	{ totalEditions = i; }

	public int getStartIndex()			{ return startIndex; }
	public void setStartIndex(int i)	{ startIndex = i; }

	public int getPageSize()			{ return pageSize; }
	public void setPageSize(int i)		{ pageSize = i; }

	public List getEditions()			{ return editions; }
	public void setEditions(List c)		{editions = (c != null)? c : new ArrayList(); }

	public int getTotalPages()			{ return totalPages; }
	public int getCurrentPage()			{ return currentPage; }

	public int getPreviousPageSize()
	{
		return Math.min(pageSize, Math.max(0, startIndex - 1));
	}

	public int getNextPageSize()
	{
		return Math.min(pageSize, Math.max(0, totalEditions - (startIndex + pageSize)));
	}
}
