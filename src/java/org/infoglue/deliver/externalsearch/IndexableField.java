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
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

class IndexableField
{
	private Map<Locale, String> keys;
	private String defaultKey;
	
	public IndexableField(String defaultKey)
	{
		this.defaultKey = defaultKey;
		this.keys = new HashMap<Locale, String>();
	}
	
	void addKey(Locale language, String key)
	{
		this.keys.put(language, key);
	}

	String getKey()
	{
		return getKey(null);
	}

	String getKey(Locale language)
	{
		String key = keys.get(language);
		if (key == null)
		{
			key = defaultKey;
		}
		return key;
	}

	@Override
	public String toString()
	{
		return "IndexableField. Default: " + defaultKey + ". Values: " + keys;
	}



	static class Deserializer implements JsonDeserializer<IndexableField>
	{
		Type configType = new TypeToken<Map<String, String>>() {}.getType();

		@Override
		public IndexableField deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			IndexableField field = null;
			try
			{
				JsonObject obj = (JsonObject) json;

				JsonElement nameElement = obj.get("name");

				if (nameElement.isJsonPrimitive() && ((JsonPrimitive)nameElement).isString())
				{
					field = new IndexableField(nameElement.getAsString());
				}
				else if (nameElement.isJsonObject())
				{
					JsonObject nameObject = (JsonObject)nameElement;
					if (nameObject.has("default"))
					{
						field = new IndexableField(nameObject.get("default").getAsString());
					}
					else
					{
						for (Map.Entry<String, JsonElement> entry : nameObject.entrySet())
						{
							if (!entry.getValue().isJsonPrimitive() || !((JsonPrimitive)entry.getValue()).isString())
							{
								throw new JsonParseException("Value of member in name must be a String");
							}
							if (field == null)
							{
								field = new IndexableField(entry.getValue().getAsString());
							}
							field.addKey(new Locale(entry.getKey()), entry.getValue().getAsString());
						}
					}
				}
			}
			catch (Throwable ex)
			{
				throw new JsonParseException("Failed to deserialize element. Exception message: " + ex.getMessage(), ex);
			}

			if (field == null)
			{
				throw new JsonParseException("Failed to deserialize IndexableField. The object should be a JSON-string or a JSON-object with a name member.");
			}

			return field;
		}
	}
}
