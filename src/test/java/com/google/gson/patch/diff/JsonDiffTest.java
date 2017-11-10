package com.google.gson.patch.diff;

import java.io.IOException;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.patch.JsonLoader;
import com.google.gson.patch.JsonPatch;
import com.google.gson.patch.JsonPatchException;
import com.google.gson.patch.JsonUtil;

import org.junit.Assert;

public final class JsonDiffTest
{
    
    private final JsonArray testData;

    public JsonDiffTest()
        throws IOException
    {
        final String resource = "/jsonpatch/diff/diff.json";
        testData = JsonLoader.fromResource(resource).getAsJsonArray();
    }

    @Test
    public void testPatchApply() throws JsonPatchException
    {
    	for (JsonElement element : testData) {
    		JsonObject obj = (JsonObject)element;
    		
    		JsonElement source = obj.get("first");
    		JsonElement target = obj.get("second");
    		
    		JsonPatch patch = JsonDiff.diff(source, target);
    		JsonElement actual = patch.apply(JsonUtil.deepCopy(source));
    		
    		Assert.assertTrue(
    				"Generated patch failed to apply\nexpected: " + target.toString() + "\nactual: " + actual.toString(),
    	            JsonUtil.jsonEquals(target, actual));
    	}
    }

    @Test
    public void generatedPatchesAreWhatIsExpected()
    {
    	for (JsonElement element : testData) {
    		JsonObject obj = (JsonObject)element;
    		
    		JsonElement patchElem = obj.get("patch");
    		if (patchElem == null)
    			continue;
    		
    		JsonElement source = obj.get("first");
    		JsonElement target = obj.get("second");
    		String message = obj.get("message").getAsString();
    		
    		JsonPatch actualPatch = JsonDiff.diff(source, target);
    		Assert.assertTrue(
    				 "patch is not what was expected\nscenario: " + message + "\n"
    				            + "expected: " + patchElem + 
    				            "\nactual: " + actualPatch + "\n",
    				JsonUtil.jsonEquals(patchElem, actualPatch.toJson()));
    	}
    	
    }
}
