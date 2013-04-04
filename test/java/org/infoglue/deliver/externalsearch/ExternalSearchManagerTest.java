
package org.infoglue.deliver.externalsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Erik Stenbäcka
 *
 */
public class ExternalSearchManagerTest
{
	private ExternalSearchManager manager;
	
	@Before
	public void setup()
	{
		manager = new ExternalSearchManager();
	}

	@Test
	public void testParseConfigEmpty()
	{
		List<ExternalSearchServiceConfig> configs = manager.parseConfiguartions("");

		assertEquals("There should be no items in the list", 0, configs.size());
	}

	@Test
	public void testParseConfig()
	{
		String configsString = "[" +
				"{\"name\":\"persons\"}" +
				",{\"name\":\"enhet\"}" +
				"]";
		List<ExternalSearchServiceConfig> configs = manager.parseConfiguartions(configsString);

		assertEquals("There should be two items in the list", 2, configs.size());
		assertTrue("The list should contain two items with names, persons and enhet.", configs.get(0).getName().equals("persons") && configs.get(1).getName().equals("enhet") || configs.get(1).getName().equals("persons") && configs.get(0).getName().equals("enhet"));
	}

	@Test
	public void testParseLanguage()
	{
		String configsString = "[" +
				"{\"defaultLanguage\":\"en\"}" +
				",{\"defaultLanguage\":\"sv\"}" +
				"]";
		List<ExternalSearchServiceConfig> configs = manager.parseConfiguartions(configsString);

		Locale firstServiceLanguage = configs.get(0).getDefaultLanguage();
		Locale secondServiceLanguage = configs.get(1).getDefaultLanguage();
		assertEquals("", firstServiceLanguage, new Locale("en"));
		assertEquals("", secondServiceLanguage, new Locale("sv"));
	}

	@Test
	public void testParseFieldsWithoutLanguages()
	{
		String configsString = "[" +
				"{\"fields\":{\"foo\":{\"name\":\"foobar\"}}}" +
				"]";
		List<ExternalSearchServiceConfig> configs = manager.parseConfiguartions(configsString);

		ExternalSearchServiceConfig config = configs.get(0);
		assertNotNull("", config.getFields());
		assertNotNull("", config.getFields().get("foo"));
		assertEquals("", config.getFields().get("foo").getKey(), "foobar");
	}

	@Test
	public void testParseFieldsWithLanguages()
	{
		String configsString = "[" +
				"{\"fields\":{\"foo\":{\"name\":{\"sv\":\"foobar\"}}}}" +
				"]";
		List<ExternalSearchServiceConfig> configs = manager.parseConfiguartions(configsString);

		ExternalSearchServiceConfig config = configs.get(0);
		assertNotNull("", config.getFields());
		assertNotNull("", config.getFields().get("foo"));
		assertEquals("", config.getFields().get("foo").getKey(), "foobar");
	}

	@Test
	public void testParseManyFieldsWithoutLanguages()
	{
		String configsString = "[" +
				"{\"fields\":{\"foo\":{\"name\":\"foobar\"},\"bar\":{\"name\":\"barfoo\"}}}" +
				"]";
		List<ExternalSearchServiceConfig> configs = manager.parseConfiguartions(configsString);

		ExternalSearchServiceConfig config = configs.get(0);
		assertNotNull("", config.getFields());
		assertNotNull("", config.getFields().get("foo"));
		assertNotNull("", config.getFields().get("bar"));
		assertEquals("", config.getFields().get("foo").getKey(), "foobar");
		assertEquals("", config.getFields().get("bar").getKey(), "barfoo");
	}

	@Test
	public void testParseCompleteConfig()
	{
		String configsString = "[{\r\n" + 
				"    \"name\": \"person\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"dataRetriever\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyRetriever\",\r\n" + 
				"        \"config\": {\r\n" + 
				"            \"filePath\": \"/appl/cms/research_datafiles\"\r\n" + 
				"        }\r\n" + 
				"    },\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"}]";
		List<ExternalSearchServiceConfig> configs = manager.parseConfiguartions(configsString);

		assertEquals("There should be two items in the list", 1, configs.size());

		ExternalSearchServiceConfig config = configs.get(0);

		assertEquals("DataRetriever mismatch", DummyRetriever.class.getName(), config.getDataRetriever().getClass().getName());
		DummyRetriever dr = (DummyRetriever)config.getDataRetriever();
		assertNotNull("", dr.getConfig());
		assertTrue("", dr.getConfig().containsKey("filePath"));

		assertEquals("Parser mismatch", DummyParser.class.getName(), config.getParser().getClass().getName());
		DummyParser p = (DummyParser)config.getParser();
		assertNotNull("", p.getConfig());
		assertTrue("", p.getConfig().size() == 0);

		assertEquals("Indexer mismatch", DummyIndexer.class.getName(), config.getIndexer().getClass().getName());
		DummyIndexer i = (DummyIndexer)config.getIndexer();
		assertNotNull("", i.getConfig());
		assertTrue("", i.getConfig().size() == 0);
	}

	@Test
	public void testGetServiceEmpty()
	{
		ExternalSearchService service = manager.getService("persons");

		assertNull("Unknown services should return null", service);
	}

	@Test
	public void testGetService()
	{
		String configsString = "[{\r\n" + 
				"    \"name\": \"person\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"dataRetriever\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyRetriever\",\r\n" + 
				"        \"config\": {\r\n" + 
				"            \"filePath\": \"/appl/cms/research_datafiles\"\r\n" + 
				"        }\r\n" + 
				"    },\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"}]";
		manager.updateConfigurations(configsString);

		ExternalSearchService service = manager.getService("person");
		ExternalSearchService service2 = manager.getService("enhet");

		assertNotNull("Should contain persons service", service);
		assertNull("Unknown services should return null", service2);
	}

	@Test
	public void testInvalidConfigFormat()
	{
		String configsString = "[{\r\n" + 
				"    \"name\": \"person\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"}]";
		manager.updateConfigurations(configsString);

		ExternalSearchService service = manager.getService("person");

		assertNull("Should contain persons service", service);
	}

	@Test
	public void testAddNewService()
	{
		String configsString = "[{\r\n" + 
				"    \"name\": \"person\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"dataRetriever\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyRetriever\",\r\n" + 
				"        \"config\": {\r\n" + 
				"            \"filePath\": \"/appl/cms/research_datafiles\"\r\n" + 
				"        }\r\n" + 
				"    },\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"}]";
		manager.updateConfigurations(configsString);
		configsString = "[{\r\n" + 
				"    \"name\": \"person\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"dataRetriever\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyRetriever\",\r\n" + 
				"        \"config\": {\r\n" + 
				"            \"filePath\": \"/appl/cms/research_datafiles\"\r\n" + 
				"        }\r\n" + 
				"    },\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"},{" + 
				"    \"name\": \"enhet\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"dataRetriever\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyRetriever\",\r\n" + 
				"        \"config\": {\r\n" + 
				"            \"filePath\": \"/appl/cms/research_datafiles\"\r\n" + 
				"        }\r\n" + 
				"    },\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"}]";
		manager.updateConfigurations(configsString);

		ExternalSearchService service = manager.getService("person");
		ExternalSearchService service2 = manager.getService("enhet");

		assertNotNull("Should contain persons service", service);
		assertNotNull("Should contain enhet service", service2);
	}

	@Test
	public void testRemoveNewService()
	{
		String configsString = "[{\r\n" + 
				"    \"name\": \"person\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"dataRetriever\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyRetriever\",\r\n" + 
				"        \"config\": {\r\n" + 
				"            \"filePath\": \"/appl/cms/research_datafiles\"\r\n" + 
				"        }\r\n" + 
				"    },\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"},{" + 
				"    \"name\": \"enhet\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"dataRetriever\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyRetriever\",\r\n" + 
				"        \"config\": {\r\n" + 
				"            \"filePath\": \"/appl/cms/research_datafiles\"\r\n" + 
				"        }\r\n" + 
				"    },\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"}]";
		manager.updateConfigurations(configsString);
		configsString = "[{\r\n" + 
				"    \"name\": \"person\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"dataRetriever\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyRetriever\",\r\n" + 
				"        \"config\": {\r\n" + 
				"            \"filePath\": \"/appl/cms/research_datafiles\"\r\n" + 
				"        }\r\n" + 
				"    },\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"}]";
		manager.updateConfigurations(configsString);

		ExternalSearchService service = manager.getService("person");
		ExternalSearchService service2 = manager.getService("enhet");

		assertNotNull("Should contain persons service", service);
		assertNull("Should not contain enhet service", service2);
	}

	@Test
	public void testUpdateToInvalidConfig()
	{
		String configsString = "[{\r\n" + 
				"    \"name\": \"person\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"dataRetriever\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyRetriever\",\r\n" + 
				"        \"config\": {\r\n" + 
				"            \"filePath\": \"/appl/cms/research_datafiles\"\r\n" + 
				"        }\r\n" + 
				"    },\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"},{" + 
				"    \"name\": \"enhet\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"dataRetriever\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyRetriever\",\r\n" + 
				"        \"config\": {\r\n" + 
				"            \"filePath\": \"/appl/cms/research_datafiles\"\r\n" + 
				"        }\r\n" + 
				"    },\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"}]";
		manager.updateConfigurations(configsString);
		configsString = "[{\r\n" + 
				"    \"name\": \"person\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"dataRetriever\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyRetriever\",\r\n" + 
				"        \"config\": {\r\n" + 
				"            \"filePath\": \"/appl/cms/research_datafiles\"\r\n" + 
				"        }\r\n" + 
				"    },\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"},{" + 
				"    \"name\": \"enhet\",\r\n" + 
				"    \"maxAge\": 3600,\r\n" + 
				"    \"dependencies\": [\"enhet\"],\r\n" + 
				"    \"parser\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyParser\",\r\n" + 
				"        \"config\": {}\r\n" + 
				"    },\r\n" + 
				"    \"indexer\": {\r\n" + 
				"        \"class\": \"org.infoglue.deliver.externalsearch.DummyIndexer\",\r\n" + 
				"        \"searchableFields\": [\"name\", \"title\"],\r\n" + 
				"        \"sortableFields\": [\"name\", \"phone\"],\r\n" + 
				"        \"config\": {}\r\n" + 
				"    }\r\n" + 
				"}]";
		manager.updateConfigurations(configsString);
		
		ExternalSearchService service = manager.getService("person");
		ExternalSearchService service2 = manager.getService("enhet");
		
		assertNotNull("Should contain persons service", service);
		assertNotNull("Should not contain enhet service", service2);
	}
}
