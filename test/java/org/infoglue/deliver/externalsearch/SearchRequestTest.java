package org.infoglue.deliver.externalsearch;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Sort;
import org.infoglue.deliver.externalsearch.SearchRequest.ParameterType;
import org.junit.Before;
import org.junit.Test;

public class SearchRequestTest
{

	SearchRequest englishRequest;
	SearchRequest swedishRequest;
	StandardAnalyzer analyser;
	IndexableField languageIndependentField;
	IndexableField languageDependentField;
	Locale english;
	Locale swedish;

	@Before
	public void setUp() throws Exception
	{
		Map<String, IndexableField> fields = new HashMap<String, IndexableField>();

		english = Locale.ENGLISH;
		swedish = new Locale("sv");

		languageIndependentField = new IndexableField("foobar", false);
		fields.put("foobar", languageIndependentField);
		languageDependentField = new IndexableField("barfoo", true);
		fields.put("barfoo", languageDependentField);

		englishRequest = new SearchRequest(fields, Locale.ENGLISH);
		swedishRequest = new SearchRequest(fields, new Locale("sv"));
		analyser = new StandardAnalyzer();
	}

	@Test
	public void generateNonlanguageQuery() throws ParseException
	{
		englishRequest.addSearchParameter("foobar", "lol");
		swedishRequest.addSearchParameter("foobar", "lol");

		String englishQuery = englishRequest.getQuery(analyser).toString();
		String swedishQuery = swedishRequest.getQuery(analyser).toString();

		assertTrue(englishQuery.contains(languageIndependentField.getFieldName(english)));
		assertTrue(englishQuery.contains("lol"));
		assertTrue(swedishQuery.contains(languageIndependentField.getFieldName(swedish)));
		assertTrue(swedishQuery.contains("lol"));
	}

	@Test
	public void generateNonlanguageQueryMustNotCaluse() throws ParseException
	{
		englishRequest.addSearchParameter("foobar", "lol", ParameterType.MUST_NOT);
		swedishRequest.addSearchParameter("foobar", "lol", ParameterType.MUST_NOT);

		String englishQuery = englishRequest.getQuery(analyser).toString();
		String swedishQuery = swedishRequest.getQuery(analyser).toString();

		assertTrue(englishQuery.contains(languageIndependentField.getFieldName(english)));
		assertTrue(englishQuery.contains("lol"));
		assertTrue(englishQuery.contains("-"));
		assertTrue(swedishQuery.contains(languageIndependentField.getFieldName(swedish)));
		assertTrue(swedishQuery.contains("lol"));
		assertTrue(swedishQuery.contains("-"));
	}

	@Test
	public void generateLanguageDependentQuery() throws ParseException
	{
		englishRequest.addSearchParameter("barfoo", "lol");
		swedishRequest.addSearchParameter("barfoo", "lol");

		String englishQuery = englishRequest.getQuery(analyser).toString();
		String swedishQuery = swedishRequest.getQuery(analyser).toString();

		assertTrue("Query: " + englishQuery, englishQuery.contains(languageDependentField.getFieldName(english)));
		assertTrue("Query: " + englishQuery, englishQuery.contains("lol"));
		assertTrue("Query: " + swedishQuery, swedishQuery.contains(languageDependentField.getFieldName(swedish)));
		assertTrue("Query: " + swedishQuery, swedishQuery.contains("lol"));
	}

	@Test
	public void generateSortNonLanguage() throws ParseException
	{
		englishRequest.addSortParameter("foobar", false);
		swedishRequest.addSortParameter("foobar", false);

		Sort englishSort = englishRequest.getOrdering();
		Sort swedishSort = swedishRequest.getOrdering();

		assertTrue("Sort:" + englishSort.getSort() + " <=> " + languageDependentField.getFieldName(english), englishSort.toString().contains(languageIndependentField.getFieldName(english)));
		assertTrue("Sort:" + swedishSort.getSort() + " <=> " + languageDependentField.getFieldName(swedish), swedishSort.toString().contains(languageIndependentField.getFieldName(swedish)));
	}

	@Test
	public void generateSortLanguage() throws ParseException
	{
		englishRequest.addSortParameter("barfoo", false);
		swedishRequest.addSortParameter("barfoo", false);

		Sort englishSort = englishRequest.getOrdering();
		Sort swedishSort = swedishRequest.getOrdering();

		assertTrue("Sort:" + englishSort.getSort() + " <=> " + languageDependentField.getFieldName(english), englishSort.toString().contains(languageDependentField.getFieldName(english)));
		assertTrue("Sort:" + swedishSort.getSort() + " <=> " + languageDependentField.getFieldName(swedish), swedishSort.toString().contains(languageDependentField.getFieldName(swedish)));
	}


}
