/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * @author Erik Stenbäcka
 *
 */
public class ExternalSearchServiceTest
{
	ExternalSearchService service;

	@Spy DataRetriever dataRetriever = new DummyRetriever();
	@Spy Parser parser = new DummyParser();
	@Spy Indexer indexer = new DummyIndexer();

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testDestroyService()
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(null);
		config.setDataRetriever(dataRetriever);
		config.setParser(parser);
		config.setIndexer(indexer);

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);

		// Execution
		service = new ExternalSearchService(config, false, handler);
		service.destroyService();

		verify(dataRetriever).destroy();
		verify(parser).destroy();
		verify(indexer).destroy();
	}

	@Test
	public void testDelegateLifeCycleInit()
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(null);
		config.setDataRetriever(dataRetriever);
		config.setParser(parser);
		config.setIndexer(indexer);

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);

		// Execution
		service = new ExternalSearchService(config, false, handler);

		verify(dataRetriever).init();
		verify(parser).init();
		verify(indexer).init();
	}

	@Test
	public void testDelegateLifeCycleDestroy()
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(null);
		config.setDataRetriever(dataRetriever);
		config.setParser(parser);
		config.setIndexer(indexer);

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);

		service = new ExternalSearchService(config, false, handler);

		ExternalSearchServiceConfig config2 = new ExternalSearchServiceConfig();
		config2.setMaxAge(null);
		config2.setDataRetriever(new DummyRetriever());
		config2.setParser(new DummyParser());
		config2.setIndexer(new DummyIndexer());

		// Execution
		service.setConfig(config2);

		verify(dataRetriever).destroy();
		verify(parser).destroy();
		verify(indexer).destroy();
	}

	@Test
	public void testIndexHasExpiredNoMaxAge()
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(null);
		config.setDataRetriever(new DummyRetriever());
		config.setParser(new DummyParser());
		config.setIndexer(new DummyIndexer());

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);

		// Execution
		service = new ExternalSearchService(config, false, handler);

		assertFalse("", service.indexHasExpired());
	}

	@Test
	public void testIndexHasExpiredNoCreated()
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(3600);
		config.setDataRetriever(new DummyRetriever());
		config.setParser(new DummyParser());
		config.setIndexer(new DummyIndexer());

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);
		when(handler.getDirectoryAge()).thenReturn(null);

		// Execution
		service = new ExternalSearchService(config, false, handler);

		assertTrue("", service.indexHasExpired());
	}

	@Test
	public void testIndexHasNotExpired()
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(3600);
		config.setDataRetriever(new DummyRetriever());
		config.setParser(new DummyParser());
		config.setIndexer(new DummyIndexer());

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);

		when(handler.getDirectoryAge()).thenReturn(1800000);

		// Execution
		service = new ExternalSearchService(config, false, handler);

		assertFalse("", service.indexHasExpired());
		verify(handler).getDirectoryAge();
	}

	@Test
	public void testIndexHasExpired()
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(3600);
		config.setDataRetriever(new DummyRetriever());
		config.setParser(new DummyParser());
		config.setIndexer(new DummyIndexer());

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);

		when(handler.getDirectoryAge()).thenReturn(3700000);

		// Execution
		service = new ExternalSearchService(config, false, handler);

		assertTrue("", service.indexHasExpired());
		verify(handler).getDirectoryAge();
	}

	@Test
	public void testDontIndexWhenNoMaxAge() throws NullPointerException, IOException
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(null);
		config.setDataRetriever(new DummyRetriever());
		config.setParser(new DummyParser());
		config.setIndexer(new DummyIndexer());

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);
		when(handler.getDirectory()).thenReturn(new RAMDirectory());

		service = new ExternalSearchService(config, false, handler);
		// Call start once to remove the start on new service indicator 
		service.startIndexing();

		// Execution
		boolean doIndex = service.startIndexing();

		assertFalse("Should not start index if no max age", doIndex);
	}

	@Test
	public void testIndexWhenNoDependencies() throws NullPointerException, IOException
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(3600);
		config.setDataRetriever(new DummyRetriever());
		config.setParser(new DummyParser());
		config.setIndexer(new DummyIndexer());
		config.setDependencis(null);

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);
		when(handler.getDirectory()).thenReturn(new RAMDirectory());

		// Execution
		service = new ExternalSearchService(config, false, handler);
		boolean doIndex = service.startIndexing();

		assertTrue("Should start index if no dependencies", doIndex);
	}

	@Test
	public void testDontIndexWhenNonExistingDependency() throws NullPointerException, IOException
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(3600);
		config.setDataRetriever(new DummyRetriever());
		config.setParser(new DummyParser());
		config.setIndexer(new DummyIndexer());
		config.setDependencis(Collections.singletonList("apa"));

		ExternalSearchManager mockManager = mock(ExternalSearchManager.class);
		when(mockManager.getService("apa")).thenReturn(null);

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);
		when(handler.getDirectory()).thenReturn(new RAMDirectory());

		// Execution
		service = new ExternalSearchService(config, false, handler);
		boolean doIndex = service.startIndexing();

		assertFalse("Should not start index missing dependency", doIndex);
	}

	@Test
	public void testDontIndexWhenExistingDependencyNotSearchable() throws NullPointerException, IOException
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(3600);
		config.setDataRetriever(new DummyRetriever());
		config.setParser(new DummyParser());
		config.setIndexer(new DummyIndexer());
		config.setDependencis(Collections.singletonList("apa"));

		ExternalSearchManager mockManager = mock(ExternalSearchManager.class);
		ExternalSearchService mockService = mock(ExternalSearchService.class);
		when(mockService.isSearchable()).thenReturn(false);
		when(mockManager.getService("apa")).thenReturn(mockService);

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);
		when(handler.getDirectory()).thenReturn(new RAMDirectory());

		// Execution
		service = new ExternalSearchService(config, false, handler);
		boolean doIndex = service.startIndexing();

		assertFalse("Should not start index when dependency not searchable", doIndex);
	}

	@Test
	public void testIndexWhenExistingDependencySearchable() throws NullPointerException, IOException
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(3600);
		config.setDataRetriever(new DummyRetriever());
		config.setParser(new DummyParser());
		config.setIndexer(new DummyIndexer());
		config.setDependencis(Collections.singletonList("apa"));

		ExternalSearchManager mockManager = mock(ExternalSearchManager.class);
		ExternalSearchService mockService = mock(ExternalSearchService.class);
		when(mockService.isSearchable()).thenReturn(true);
		when(mockManager.getService("apa")).thenReturn(mockService);
		ExternalSearchManager.injectManager(mockManager);

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);
		when(handler.getDirectory()).thenReturn(new RAMDirectory());

		// Execution
		service = new ExternalSearchService(config, false, handler);
		boolean doIndex = service.startIndexing();

		assertTrue("Should start index when dependency is searchable", doIndex);
	}

	@Test
	public void testDontIndexMultipleDependencies() throws NullPointerException, IOException
	{
		ExternalSearchServiceConfig config = new ExternalSearchServiceConfig();

		config.setName("foobar");
		config.setMaxAge(3600);
		config.setDataRetriever(new DummyRetriever());
		config.setParser(new DummyParser());
		config.setIndexer(new DummyIndexer());
		List<String> dependencies = new ArrayList<String>();
		dependencies.add("apa");
		dependencies.add("bepa");
		config.setDependencis(dependencies);

		ExternalSearchManager mockManager = mock(ExternalSearchManager.class);
		ExternalSearchService mockServiceA = mock(ExternalSearchService.class);
		when(mockServiceA.isSearchable()).thenReturn(true);
		when(mockManager.getService("apa")).thenReturn(mockServiceA);
		when(mockManager.getService("bepa")).thenReturn(null);

		ExternalSearchServiceDirectoryHandler handler = mock(ExternalSearchServiceDirectoryHandler.class);
		when(handler.getDirectory()).thenReturn(new RAMDirectory());

		// Execution
		service = new ExternalSearchService(config, false, handler);
		boolean doIndex = service.startIndexing();

		assertFalse("Should not start index if no dependencies", doIndex);
	}

}
