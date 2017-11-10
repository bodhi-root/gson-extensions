package com.google.gson.patch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Map;
import java.util.Set;

/**
 * An {@link Equivalence} strategy for JSON Schema equality
 *
 * <p>{@link JsonNode} does a pretty good job of obeying the  {@link
 * Object#equals(Object) equals()}/{@link Object#hashCode() hashCode()}
 * contract. And in fact, it does it too well for JSON Schema.</p>
 *
 * <p>For instance, it considers numeric nodes {@code 1} and {@code 1.0} to be
 * different nodes, which is true. But some IETF RFCs and drafts (among  them,
 * JSON Schema and JSON Patch) mandate that numeric JSON values be considered
 * equal if their mathematical value is the same. This class implements this
 * kind of equality.</p>
 */
public final class JsonEquals //extends Equivalence<JsonNode>
{
    public static final JsonEquals INSTANCE = new JsonEquals();

    private JsonEquals()
    {
    }

    public static JsonEquals getInstance()
    {
        return INSTANCE;
    }

    public boolean jsonEquals(final JsonElement a, final JsonElement b)
    {
    	//nulls:
    	if (a.isJsonNull())
    		return b.isJsonNull();
    	
    	//primitives:
    	if (a instanceof JsonPrimitive) {
    		if (b instanceof JsonPrimitive)
    			return primitiveEquals((JsonPrimitive)a, (JsonPrimitive)b);
    		
    		return false;
    	}
    	
    	//arrays:
    	if (a instanceof JsonArray) {
    		if (b instanceof JsonArray)
    			return arrayEquals((JsonArray)a, (JsonArray)b);
    		
    		return false;
    	}
    	
    	//objects:
    	if (a instanceof JsonObject) {
    		if (b instanceof JsonObject)
    			return objectEquals((JsonObject)a, (JsonObject)b);
    		
    		return false;
    	}
    	
    	throw new IllegalStateException("JsonElements are expected to be JsonObject, JsonArray, JsonPrimitive, or JsonNull");
    }

    protected int doHash(final JsonElement t)
    {
    	if (t.isJsonNull())
			return 0;
    	
    	//primitives:
    	if (t.isJsonPrimitive()) {
    		JsonPrimitive p = (JsonPrimitive)t;
    		if (p.isString())
    			return p.getAsString().hashCode();
    		if (p.isNumber())
    			return Double.hashCode(p.getAsDouble());
    		
    		throw new IllegalStateException("JsonPrimitives are expected to be Strings, Numbers, or NULLs");
    	}
        
    	//arrays:
        if (t.isJsonArray()) {
        	JsonArray a = (JsonArray)t;
        	int ret = 0;
        	
        	for (final JsonElement element : a) {
        		ret = 31 * ret + doHash(element);
        	}
                
            return ret;
        }
    	
        //objects:
        if (t.isJsonObject()) {
        	JsonObject obj = (JsonObject)t;
        	int ret = 0;
        	
        	for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
        		ret = 31 * ret + (entry.getKey().hashCode() ^ doHash(entry.getValue()));
        	}
        	
        	return ret;
        }
       
        throw new IllegalArgumentException("JSON objects expected to be instances of JsonObject, JsonArray, or JsonPrimitive");
    }

    private static boolean primitiveEquals(final JsonPrimitive a, final JsonPrimitive b)
    {
    	if (a.isString()) {
    		if (!b.isString())
    			return false;
    		
    		return a.getAsString().equals(b.getAsString());
    	}
    	
    	if (a.isNumber()) {
    		if (!b.isNumber())
    			return false;
    		
    		return a.getAsDouble() == b.getAsDouble();
    	}
    	
    	if (a.isBoolean()) {
    		if (!b.isBoolean())
    			return false;
    		
    		return a.getAsBoolean() == b.getAsBoolean();
    	}
    	
        throw new IllegalStateException("JsonPrimitives are expected to be Strings, Numbers, Booleans, or NULLs");
    }

    private boolean arrayEquals(final JsonArray a, final JsonArray b)
    {
    	//check size:
    	int size = a.size();
    	if (size != b.size())
    		return false;
    	
    	//check each component:
    	for (int i=0; i<size; i++) {
    		if (!jsonEquals(a.get(i), b.get(i)))
    			return false;
    	}
    	
    	return true;
    }
    
    private boolean objectEquals(final JsonObject a, final JsonObject b)
    {
    	Map<String, JsonElement> aProps = JsonUtil.mapProperties(a);
    	Map<String, JsonElement> bProps = JsonUtil.mapProperties(b);
    	
    	//check that keys are equal:
    	Set<String> aKeys = aProps.keySet();
    	Set<String> bKeys = bProps.keySet();
    	
    	if (!aKeys.equals(bKeys))
    		return false;
    	
    	//test each member individually:
    	for (Map.Entry<String, JsonElement> aEntry : aProps.entrySet()) {
    		String key = aEntry.getKey();
    		JsonElement aElem = aEntry.getValue();
    		JsonElement bElem = bProps.get(key);
    		if (!jsonEquals(aElem, bElem))
    			return false;
    	}
    	
        return true;
    }
}
