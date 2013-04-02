/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Erik Stenbäcka
 *
 */
public class GSonBackedJSONParserTest
{
	Parser parser;
	
	@Before
	public void setUp() throws Exception
	{
		this.parser = new GSonBackedJSONParser();
		this.parser.init();
	}
	
	private List<Map<String, Object>> parseInputString(String source)
	{
		StringInputStream input = new StringInputStream(source);

		return this.parser.parse(input);
	}

	@Test
	public void parseEmptyList()
	{
		String source = "[]";

		List<Map<String, Object>> result = parseInputString(source);

		assertSame("Should not have any posts", 0, result.size());
	}

	@Test
	public void parseString()
	{
		String source = "[{\"name\":\"foobar\"}]";

		List<Map<String, Object>> result = parseInputString(source);

		Map<String, Object> item = result.get(0);
		assertEquals("A string primitive should be parsed to a Java String with the same value", "foobar", item.get("name"));
	}

	@Test
	public void parseNumber()
	{
		String source = "[{\"age\":18}]";

		List<Map<String, Object>> result = parseInputString(source);

		Map<String, Object> item = result.get(0);
		assertEquals("A number primitive should be parsed to a Java Number with the same value", 18, item.get("age"));
	}

	@Test
	public void parseBoolean()
	{
		String source = "[{\"isStudent\":true}]";

		List<Map<String, Object>> result = parseInputString(source);

		Map<String, Object> item = result.get(0);
		assertEquals("A boolean primitive should be parsed to a Java Boolean with the same value", true, item.get("isStudent"));
	}

	@Test
	public void parseMultipleObjects()
	{
		String source = "[{\"name\":\"apa\"},{\"name\":\"bepa\"}]";

		List<Map<String, Object>> result = parseInputString(source);

		assertEquals("A list with two objects should be parsed to a list with two maps", 2, result.size());
	}

	@Test
	public void parseMultipleFields()
	{
		String source = "[{\"name\":\"foobar\",\"age\":\"18\"}]";

		List<Map<String, Object>> result = parseInputString(source);

		Map<String, Object> item = result.get(0);
		assertEquals("There should be a name field", "foobar", item.get("name"));
		assertEquals("There should be an age field", "18", item.get("age"));
	}

	@Test
	public void testComplexStructure()
	{
		String source = "[{\"name\":\"foobar\",\"age\":\"18\",\"address\":{\"street\":\"road\",\"postalcode\":\"12345\"}}]";

		StringInputStream input = new StringInputStream(source);

		List<Map<String, Object>> result = this.parser.parse(input);

		assertEquals("There shouls be one item in the list", 1, result.size());
		Map<String, Object> item = result.get(0);
		assertEquals("There should be a name field", "foobar", item.get("name"));
		assertEquals("There should be an age field", "18", item.get("age"));
		assertTrue("There should be an address field", item.containsKey("address"));
		@SuppressWarnings("unchecked")
		Map<String, Object> address = (Map<String, Object>) item.get("address");
		assertEquals("There should be a street field", "road", address.get("street"));
		assertEquals("There should be a postal code field", "12345", address.get("postalcode"));
	}

}
