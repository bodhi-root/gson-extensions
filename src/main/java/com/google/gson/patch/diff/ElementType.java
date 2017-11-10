package com.google.gson.patch.diff;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public enum ElementType {

	NULL,
	PRIMITIVE_STRING,
	PRIMITIVE_NUMBER,
	PRIMITIVE_BOOLEAN,
	OBJECT,
	ARRAY;
	
	public static ElementType of(JsonElement e) {
		
		if (e.isJsonNull())
			return NULL;
		
		if (e.isJsonObject())
			return OBJECT;
		
		if (e.isJsonArray())
			return ARRAY;
		
		if (e.isJsonPrimitive()) {
			JsonPrimitive p = (JsonPrimitive)e;
			if (p.isString())
				return PRIMITIVE_STRING;
			if (p.isNumber())
				return PRIMITIVE_NUMBER;
			if (p.isBoolean())
				return PRIMITIVE_BOOLEAN;
			
			throw new IllegalStateException("JsonPrimitives are expected to be either Strings or Numbers");
		}
		
		throw new IllegalStateException("JsonElements are expected to be JsonObject, JsonArray, JsonPrimitive, or JsonNull");
	}
	
}
