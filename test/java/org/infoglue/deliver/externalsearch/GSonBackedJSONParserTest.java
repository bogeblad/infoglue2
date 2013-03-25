/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import static org.junit.Assert.*;

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

	@Test
	public void testEmptyList()
	{
		String source = "[]";

		StringInputStream input = new StringInputStream(source);

		List<Map<String, Object>> result = this.parser.parse(input);

		assertEquals("There should be a list but it should not have any items in it", 0, result.size());
	}

	@Test
	public void testSimpleStructure()
	{
		String source = "[{\"name\":\"foobar\",\"age\":\"18\"}]";

		StringInputStream input = new StringInputStream(source);

		List<Map<String, Object>> result = this.parser.parse(input);

		assertEquals("There shouls be one item in the list", 1, result.size());
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
		Map<String, Object> address = (Map<String, Object>) item.get("address");
		assertEquals("There should be a street field", "road", address.get("street"));
		assertEquals("There should be a postal code field", "12345", address.get("postalcode"));
	}

}
