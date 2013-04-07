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
//	private Map<Locale, String> keys;
//	private String defaultKey;
	private boolean languageDependent;
	private String name;

	public IndexableField()
	{
//		this.name = name;
//		this.languageDependent = false;
//		this.keys = new HashMap<Locale, String>();
	}
	
	IndexableField(String name, boolean languageDependent)
	{
		this.name = name;
		this.languageDependent = languageDependent;
	}

//	public IndexableField(String defaultKey)
//	{
//		this.languageDependent = false;
//		this.defaultKey = defaultKey;
//		this.keys = new HashMap<Locale, String>();
//	}
	
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
//
//	void addKey(Locale language, String key)
//	{
//		this.keys.put(language, key);
//	}
//
//	public String getKey()
//	{
//		return getKey(null);
//	}

//	public String getKey(Locale language)
//	{
//		if (language == null)
//		{
//			return name;
//		}
//		else
//		{
//			return new StringBuilder().append(name).append("$$").append(language.getLanguage()).toString();
//		}
//	}
	
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
//			IndexableField field = null;
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

//				JsonElement nameElement = obj.get("name");
//
//				if (nameElement.isJsonPrimitive() && ((JsonPrimitive)nameElement).isString())
//				{
//					field = new IndexableField(nameElement.getAsString());
//				}
//				else if (nameElement.isJsonObject())
//				{
//					JsonObject nameObject = (JsonObject)nameElement;
//					if (nameObject.has("default"))
//					{
//						field = new IndexableField(nameObject.get("default").getAsString());
//					}
//					else
//					{
//						for (Map.Entry<String, JsonElement> entry : nameObject.entrySet())
//						{
//							if (!entry.getValue().isJsonPrimitive() || !((JsonPrimitive)entry.getValue()).isString())
//							{
//								throw new JsonParseException("Value of member in name must be a String");
//							}
//							if (field == null)
//							{
//								field = new IndexableField(entry.getValue().getAsString());
//							}
//							field.addKey(new Locale(entry.getKey()), entry.getValue().getAsString());
//						}
//					}
//				}
			}
			catch (Throwable ex)
			{
				throw new JsonParseException("Failed to deserialize element. Exception message: " + ex.getMessage(), ex);
			}

			return result;
		}
	}
}
