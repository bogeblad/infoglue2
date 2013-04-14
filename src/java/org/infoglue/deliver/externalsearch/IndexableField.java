package org.infoglue.deliver.externalsearch;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class IndexableField
{
	public enum IndexingType {TOKENIZED,UN_TOKENIZED};
	
//	private Map<Locale, String> keys;
//	private String defaultKey;
	private boolean languageDependent;
	private String name;
	private IndexingType indexingType;

	public IndexableField()
	{
		this.indexingType = IndexingType.TOKENIZED;
	}

	IndexableField(String name, boolean languageDependent)
	{
		this();
		this.name = name;
		this.languageDependent = languageDependent;
	}

	void setLanguageDependent(boolean languageDependent)
	{
		this.languageDependent = languageDependent;
	}

	public boolean isLanguageDependent()
	{
		return this.languageDependent;
	}

	void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}

	void setIndexingType(IndexingType indexingType)
	{
		this.indexingType = indexingType;
	}
	
	public IndexingType getIndexingType()
	{
		return indexingType;
	}

	public String getFieldName(Locale language)
	{
		if (language == null || !languageDependent)
		{
			return name;
		}
		else
		{
			return new StringBuilder().append(name).append("$$").append(language.getLanguage()).toString();
		}
	}

	@Override
	public String toString()
	{
		return "IndexableField. Name: " + name + ". LanguageDependent: " + languageDependent;
	}


	public static void registerField(Map<String, IndexableField> fields, String name, Boolean languageDependent)
	{
		IndexableField field = new IndexableField(name, languageDependent);
		fields.put(name, field);
	}

	static class Deserializer implements JsonDeserializer<Map<String, IndexableField>>
	{
		@Override
		public Map<String, IndexableField> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			Map<String, IndexableField> result = new HashMap<String, IndexableField>();
			try
			{
				JsonObject obj = (JsonObject) json;

				for (Map.Entry<String, JsonElement> entry : obj.entrySet())
				{
					IndexableField field = context.deserialize(entry.getValue(), IndexableField.class);
					field.setName(entry.getKey());
					result.put(entry.getKey(), field);
				}
			}
			catch (Throwable ex)
			{
				throw new JsonParseException("Failed to deserialize element. Exception message: " + ex.getMessage(), ex);
			}

			return result;
		}
	}
}
