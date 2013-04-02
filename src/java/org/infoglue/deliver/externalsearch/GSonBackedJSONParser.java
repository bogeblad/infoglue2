/* ===============================================================================
 *
 * Part of the InfoGlue Content Management Platform (www.infoglue.org)
 *
 * ===============================================================================
 *
 *  Copyright (C)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2, as published by the
 * Free Software Foundation. See the file LICENSE.html for more information.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
 * Place, Suite 330 / Boston, MA 02111-1307 / USA.
 *
 * ===============================================================================
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
 * <p>A parser implementation that expects and parses the given input as a JSON array of JSON-objects.
 * The parser only handles String-, Number- and boolean- primitives as well as nested JSON-objects. Observe
 * in particular that the parser does not support JSON-arrays.</p>
 * 
 * <p>This parser uses {@link Gson} when parsing the input stream.</p>
 * 
 * @author Erik Stenbäcka
 */
public class GSonBackedJSONParser implements Parser
{

	private Gson gson;
	private Type listType;

	/**
	 * This parser does not require any configuration.
	 */
	@Override
	public void setConfig(Map<String, String> config)
	{
	}

	/**
	 * Sets up a common Gson object that will be used by all parsings done by this instance.
	 */
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
