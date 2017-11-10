package com.google.gson.patch.diff;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.patch.JsonLoader;
import com.google.gson.pointer.JsonPointer;
import com.google.gson.pointer.JsonPointerException;

import org.junit.Assert;
import org.junit.Test;

public final class UnchangedTest
{

    private final JsonArray testData;

    public UnchangedTest() throws IOException
    {
        final String resource = "/jsonpatch/diff/unchanged.json";
        testData = JsonLoader.fromResource(resource).getAsJsonArray();
    }

    @Test
    public void testUnchangedValuesWorks() throws JsonPointerException
    {
    	for (JsonElement testElem : testData) {
    		JsonObject testObj = (JsonObject)testElem;
    		
    		JsonElement first = testObj.get("first");
    		JsonElement second = testObj.get("second");
    		
    		JsonElement unchanged = testObj.get("unchanged"); 
    		
    		Map<JsonPointer, JsonElement> unchangedMap = new HashMap<>();
    		for (Map.Entry<String, JsonElement> entry : unchanged.getAsJsonObject().entrySet()) {
    			unchangedMap.put(new JsonPointer(entry.getKey()), entry.getValue());
    		}
    		
    		Map<JsonPointer, JsonElement> actual = JsonDiff.getUnchangedValues(first, second);
    		Assert.assertEquals(unchangedMap, actual);
    	}
    	
    }
}
