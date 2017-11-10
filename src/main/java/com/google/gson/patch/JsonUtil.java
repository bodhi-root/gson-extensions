package com.google.gson.patch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class JsonUtil {
	
	/**
	 * Performs a deep copy of the JsonElement.  This is done so that
	 * changes on the new object will not affect the old object (and
	 * vice versa).
	 */
	public static JsonElement deepCopy(JsonElement element) {
		
		if (element == null)
			return null;
		
		//primitives and NULLs are immutable, no need to copy:
		if (element.isJsonPrimitive() || element.isJsonNull())
			return element;
		
		//deep copy array:
		if (element.isJsonArray()) {
			JsonArray oldArray = (JsonArray)element;
			int size = oldArray.size();
			
			JsonArray newArray = new JsonArray();
			for (int i=0; i<size; i++)
				newArray.add(deepCopy(oldArray.get(i)));
			
			return newArray;
		}
		
		//deep copy object:
		if (element.isJsonObject()) {
			
			JsonObject oldObject = (JsonObject)element;
			JsonObject newObject = new JsonObject();
			
			for (Map.Entry<String, JsonElement> entry : oldObject.entrySet()) {
				newObject.add(entry.getKey(), deepCopy(entry.getValue()));
			}
			
			return newObject;
		}
		
		throw new IllegalStateException("Object of type '" + element.getClass().getName() + "' not valid.");
	}
	
	/**
	 * Inserts an element in the array at the given index.
	 * This is annoying... I can't believe GSON doesn't have this.  Am I 
	 * missing something?
	 */
	public static void add(JsonArray array, JsonElement value, int index) {
		if (index < 0)
			throw new IllegalArgumentException("Array index cannot be negative");
		
		int size = array.size();
		if (index > size) {
			for (int i=size; i<=index; i++) {
				array.add(JsonNull.INSTANCE);
			}
			array.set(index, value);
		} 
		else if (index == size) {
			array.add(value);
		}
		else {
			//move the last element back one
			array.add(array.get(size-1));
			for (int i=size-2; i>=index; i--)
				array.set(i+1, array.get(i));
			
			array.set(index, value);
		}
	}
	
	/**
	 * Tests equality between two JsonElements.  This delegates to JsonEquals
	 */
	public static boolean jsonEquals(JsonElement e1, JsonElement e2) {
		//return e1.toString().equals(e2.toString());
		return JsonEquals.INSTANCE.jsonEquals(e1, e2);
	}
	
	/**
	 * Returns a Map<String, JsonElement> of properties for the object.
	 * JsonObjects already can return their properties as a Set of
	 * Map.Entry<String, JsonElement> objects, but having the actual
	 * map can be a little more helpful.
	 */
	public static Map<String, JsonElement> mapProperties(JsonObject obj) {
    	Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
    	Map<String, JsonElement> map = new HashMap<>(entries.size());
    	
    	for (Map.Entry<String, JsonElement> entry : entries)
    		map.put(entry.getKey(), entry.getValue());
    	
    	return map;
    }
	
	/**
	 * Returns a Set<String> of property names for the JsonObject.
	 */
	public static Set<String> propertyNames(JsonObject obj) {
		Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
		Set<String> props = new HashSet<>(entries.size());
		
		for (Map.Entry<String, JsonElement> entry : entries)
    		props.add(entry.getKey());
		
		return props;
	}
	
}
