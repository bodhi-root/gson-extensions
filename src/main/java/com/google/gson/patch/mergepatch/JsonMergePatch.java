package com.google.gson.patch.mergepatch;

import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.patch.JsonPatchException;
import com.google.gson.patch.JsonUtil;


/**
 * Implementation of JSON Merge Patch (RFC 7386)
 *
 * <p><a href="http://tools.ietf.org/html/rfc7386">JSON Merge Patch</a> is a
 * "toned down" version of JSON Patch. However, it covers a very large number of
 * use cases for JSON value modifications; its focus is mostly on patching
 * JSON Objects, which are by far the most common type of JSON texts exchanged
 * on the Internet.</p>
 *
 * <p>Applying a JSON Merge Patch is defined by a single, pseudo code function
 * as follows (quoted from the RFC; indentation fixed):</p>
 *
 * <pre>
 *     define MergePatch(Target, Patch):
 *         if Patch is an Object:
 *             if Target is not an Object:
 *                 Target = {} # Ignore the contents and set it to an empty Object
 *             for each Name/Value pair in Patch:
 *                 if Value is null:
 *                     if Name exists in Target:
 *                         remove the Name/Value pair from Target
 *                 else:
 *                     Target[Name] = MergePatch(Target[Name], Value)
 *             return Target
 *         else:
 *             return Patch
 * </pre>
 */
public class JsonMergePatch
{
    
	JsonElement patch;
	
	protected JsonMergePatch(JsonElement patch) {
		this.patch = patch;
	}
	
	public static JsonMergePatch fromJson(JsonElement patch) {
		return new JsonMergePatch(patch);
	}

    /**
     * Apply the patch to a given JSON value
     *
     * @param input the value to patch
     * @return the patched value
     * @throws JsonPatchException never thrown; only for consistency with
     * {@link JsonPatch}
     * @throws NullPointerException value is null
     */
    public JsonElement apply(final JsonElement input) throws JsonPatchException {
    	return apply(input, this.patch);
    }
    
    public static JsonElement apply(JsonElement input, JsonElement patch) {
    	
    	if (input == null)
    		throw new NullPointerException();
    	
    	//if patch is not an object, just return it as the new value
    	//this includes primitives and arrays
    	if (!patch.isJsonObject())
    		return JsonUtil.deepCopy(patch);
    	
    	JsonObject patchObj = (JsonObject)patch;
    	
    	//make sure input is an object. if not, we'll overwrite it with
    	//a new value
    	JsonObject inputObj = input.isJsonObject() ?
    			(JsonObject)input : new JsonObject();
    	
    	//for objects, iterate through properties.
    	//we'll delete anything that's NULL in the patch and recursively
    	//apply updates for anything that is defined in the patch
    	for (Map.Entry<String, JsonElement> entry : patchObj.entrySet()) {
    		
    		String key = entry.getKey();
    		JsonElement newValue = entry.getValue();
    		
    		//NULL property => remove from input
    		if (newValue.isJsonNull())
    			inputObj.remove(key);

    		//otherwise, modify existing:
    		else {
    			JsonElement oldValue = inputObj.get(key);
    			if (oldValue == null)
    				inputObj.add(key, JsonUtil.deepCopy(newValue));
    			else {
    				JsonElement modifiedValue = apply(oldValue, newValue);
    				if (modifiedValue != oldValue) {
    					inputObj.remove(key);
    					inputObj.add(key, modifiedValue);
    				}
    			}
    		}
    	}
    	
    	return inputObj;
    }
    
}
