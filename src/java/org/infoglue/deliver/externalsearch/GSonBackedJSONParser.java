/**
 * 
 */
package org.infoglue.deliver.externalsearch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

/**
 * @author Erik Stenbäcka
 *
 */
public class GSonBackedJSONParser implements Parser
{

	private Gson gson;
	private Type listType;

	@Override
	public void setConfig(Map<String, String> config)
	{
	}

	@Override
	public void init()
	{
		GsonBuilder gsonBuilder = new GsonBuilder();

		listType = new TypeToken<List<Map<String, Object>>>(){}.getType();

		gsonBuilder.registerTypeAdapter(Object.class, new RecursiveMapDeserializer());

		this.gson = gsonBuilder.create();
	}

	@Override
	public List<Map<String, Object>> parse(InputStream input)
	{
		return gson.fromJson(new InputStreamReader(input), listType);
	}

	@Override
	public void destroy()
	{
		if (gson != null)
		{
			this.gson = null;
		}
	}

	private static class RecursiveMapDeserializer implements JsonDeserializer<Object>
	{
		private Type itemType = new TypeToken<Map<String, Object>>() {}.getType();

		@Override
		public Object deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException
		{
			if (element.isJsonPrimitive())
			{
				JsonPrimitive primitive = (JsonPrimitive)element.getAsJsonPrimitive();
				if (primitive.isBoolean())
				{
					return primitive.getAsBoolean();
				}
				else if (primitive.isNumber())
				{
					return primitive.getAsNumber();
				}
				else if (primitive.isString())
				{
					return primitive.getAsString();
				}
			}
			else if (element.isJsonObject())
			{
				JsonObject object = (JsonObject) element;
				return context.deserialize(object, itemType);
			}
			return null;
		}
	}
}
