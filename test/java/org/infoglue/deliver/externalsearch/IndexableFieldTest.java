package org.infoglue.deliver.externalsearch;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public class IndexableFieldTest
{
	Gson gson;

	@Before
	public void setup()
	{
		GsonBuilder gsonBuilder = new GsonBuilder();

		gsonBuilder.registerTypeAdapter(IndexableField.class, new IndexableField.Deserializer());

		gson = gsonBuilder.create();
	}

	@Test
	public void testDeserializeNonLanguage()
	{
		String input = "{\"name\":\"foobar\"}";

		IndexableField field = gson.fromJson(input, IndexableField.class);

		assertNotNull(field);
		assertEquals("", "foobar", field.getKey());
	}

	@Test
	public void testDeserializeNonLanguageDefault()
	{
		String input = "{\"name\":{\"default\":\"foo\"}}";

		IndexableField field = gson.fromJson(input, IndexableField.class);

		assertNotNull(field);
		assertEquals("", "foo", field.getKey());
	}

	@Test
	public void testDeserializeWithOneLanguage()
	{
		String input = "{\"name\":{\"sv\":\"barfoo\"}}";

		IndexableField field = gson.fromJson(input, IndexableField.class);

		assertNotNull(field);
		assertEquals("", "barfoo", field.getKey(new Locale("sv")));
	}

	@Test
	public void testDeserializeWithOneLanguageDefault()
	{
		String input = "{\"name\":{\"sv\":\"barfoo\"}}";

		IndexableField field = gson.fromJson(input, IndexableField.class);

		assertNotNull(field);
		assertEquals("", "barfoo", field.getKey());
	}

	@Test
	public void testDeserializeWithMultiLanguage()
	{
		String input = "{\"name\":{\"sv\":\"foo\",\"en\":\"bar\"}}";

		IndexableField field = gson.fromJson(input, IndexableField.class);

		assertNotNull(field);
		assertEquals("", "foo", field.getKey(new Locale("sv")));
		assertEquals("", "bar", field.getKey(new Locale("en")));
	}

	@Test
	public void testDeserializeWithMultiLanguageFallback()
	{
		String input = "{\"name\":{\"sv\":\"foo\",\"en\":\"bar\"}}";

		IndexableField field = gson.fromJson(input, IndexableField.class);

		assertNotNull(field);
		assertEquals("", "foo", field.getKey());
	}

	@Test
	public void testDeserializeWithMultiLanguageDefault()
	{
		String input = "{\"name\":{\"sv\":\"foo\",\"en\":\"bar\",\"default\":\"barfoo\"}}";

		IndexableField field = gson.fromJson(input, IndexableField.class);

		assertNotNull(field);
		assertEquals("", "barfoo", field.getKey());
	}

	@Test
	public void invalidWhenEmpty()
	{
		String input = "{}";

		try
		{
			gson.fromJson(input, IndexableField.class);
			fail("Should not allow empty objects");
		}
		catch (JsonParseException jpex)
		{
			assertTrue(true);
		}
	}

	@Test
	public void invalidWhenNoName()
	{
		String input = "{\"foobar\":\"bar\"}";

		try
		{
			gson.fromJson(input, IndexableField.class);
			fail("");
		}
		catch (JsonParseException jpex)
		{
			assertTrue(true);
		}
	}

	@Test
	public void invalidWhenNameIsNotStringOrObject()
	{
		String input = "{\"name\":5}";

		try
		{
			gson.fromJson(input, IndexableField.class);
			fail("");
		}
		catch (JsonParseException jpex)
		{
			assertTrue(true);
		}
	}

	@Test
	public void invalidWhenNameIsEmptyObject()
	{
		String input = "{\"name\":{}}";

		try
		{
			gson.fromJson(input, IndexableField.class);
			fail("");
		}
		catch (JsonParseException jpex)
		{
			assertTrue(true);
		}
	}

	@Test
	public void invalidWhenNameObjectHasNonStringMembers()
	{
		String input = "{\"name\":{\"sv\": 5}}";
		
		try
		{
			IndexableField field = gson.fromJson(input, IndexableField.class);
			fail("Parsed object: " + field);
		}
		catch (JsonParseException jpex)
		{
			assertTrue(true);
		}
	}

}
