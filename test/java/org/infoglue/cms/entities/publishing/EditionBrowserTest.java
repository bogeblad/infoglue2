package org.infoglue.cms.entities.publishing;

import org.infoglue.cms.entities.ValidationTestCase;
import org.infoglue.cms.entities.management.CategoryVO;

/**
 * Test the EditionBrowser calculations
 *
 * @author Frank Febbraro (frank@phase2technology.com)
 */
public class EditionBrowserTest extends ValidationTestCase
{
	private EditionBrowser testBrowser;

	public void testPlainCalculations() throws Exception
	{
		testBrowser = new EditionBrowser(100, 10, 21);
		assertEquals("Wrong total pages", 10, testBrowser.getTotalPages());
		assertEquals("Wrong current page", 3, testBrowser.getCurrentPage());
		assertEquals("Wrong next page size", 10, testBrowser.getNextPageSize());
		assertEquals("Wrong previous page size", 10, testBrowser.getPreviousPageSize());
	}

	public void testSmallPrevious() throws Exception
	{
		testBrowser = new EditionBrowser(100, 10, 8);
		assertEquals("Wrong total pages", 10, testBrowser.getTotalPages());
		assertEquals("Wrong current page", 1, testBrowser.getCurrentPage());
		assertEquals("Wrong next page size", 10, testBrowser.getNextPageSize());
		assertEquals("Wrong previous page size", 7, testBrowser.getPreviousPageSize());
	}

	public void testSmallNext() throws Exception
	{
		testBrowser = new EditionBrowser(100, 10, 85);
		assertEquals("Wrong total pages", 10, testBrowser.getTotalPages());
		assertEquals("Wrong current page", 9, testBrowser.getCurrentPage());
		assertEquals("Wrong next page size", 5, testBrowser.getNextPageSize());
		assertEquals("Wrong previous page size", 10, testBrowser.getPreviousPageSize());
	}

	public void testNoPrevious() throws Exception
	{
		testBrowser = new EditionBrowser(100, 10, 0);
		assertEquals("Wrong total pages", 10, testBrowser.getTotalPages());
		assertEquals("Wrong current page", 1, testBrowser.getCurrentPage());
		assertEquals("Wrong next page size", 10, testBrowser.getNextPageSize());
		assertEquals("Wrong previous page size", 0, testBrowser.getPreviousPageSize());
	}

	public void testNoNext() throws Exception
	{
		testBrowser = new EditionBrowser(100, 10, 93);
		assertEquals("Wrong total pages", 10, testBrowser.getTotalPages());
		assertEquals("Wrong current page", 10, testBrowser.getCurrentPage());
		assertEquals("Wrong next page size", 0, testBrowser.getNextPageSize());
		assertEquals("Wrong previous page size", 10, testBrowser.getPreviousPageSize());
	}

	public void testTotalPages() throws Exception
	{
		testBrowser = new EditionBrowser(93, 8, 10);
		assertEquals("Wrong total pages", 12, testBrowser.getTotalPages());
		assertEquals("Wrong current page", 2, testBrowser.getCurrentPage());
		assertEquals("Wrong next page size", 8, testBrowser.getNextPageSize());
		assertEquals("Wrong previous page size", 8, testBrowser.getPreviousPageSize());
	}

	public void testOnePage() throws Exception
	{
		testBrowser = new EditionBrowser(8, 10, 0);
		assertEquals("Wrong total pages", 1, testBrowser.getTotalPages());
		assertEquals("Wrong current page", 1, testBrowser.getCurrentPage());
		assertEquals("Wrong next page size", 0, testBrowser.getNextPageSize());
		assertEquals("Wrong previous page size", 0, testBrowser.getPreviousPageSize());
	}

	public void testNoEditions() throws Exception
	{
		testBrowser = new EditionBrowser(0, 10, 0);
		assertEquals("Wrong total pages", 1, testBrowser.getTotalPages());
		assertEquals("Wrong current page", 1, testBrowser.getCurrentPage());
		assertEquals("Wrong next page size", 0, testBrowser.getNextPageSize());
		assertEquals("Wrong previous page size", 0, testBrowser.getPreviousPageSize());
	}
}
