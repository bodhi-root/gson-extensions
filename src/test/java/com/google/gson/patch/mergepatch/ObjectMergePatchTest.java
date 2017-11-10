package com.google.gson.patch.mergepatch;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.patch.JsonLoader;
import com.google.gson.patch.JsonPatchException;
import com.google.gson.patch.JsonUtil;

public final class ObjectMergePatchTest
{
    
    private final JsonArray testData;

    public ObjectMergePatchTest() throws IOException
    {
        final String resource = "/jsonpatch/mergepatch/patch-object.json";
        testData = JsonLoader.fromResource(resource).getAsJsonArray();
    }

    @Test
    public void patchYellsOnNullInput() throws JsonPatchException
    {
        try {
            JsonMergePatch.fromJson(new JsonArray()).apply(null);
            Assert.fail("No exception thrown!");
        } catch (NullPointerException e) {
            //assertEquals(e.getMessage(), BUNDLE.getMessage("jsonPatch.nullValue"));
        }
    }

    @Test
    public void patchingWorksAsExpected() throws JsonPatchException
    {
    	
    	for (JsonElement testElem : testData) {
    		JsonObject testObj = testElem.getAsJsonObject();
    		
    		JsonElement patchElem = testObj.get("patch");
    		JsonElement victimElem = testObj.get("victim");
    		JsonElement result = testObj.get("result");
    		
    		JsonMergePatch patch = JsonMergePatch.fromJson(patchElem);
    		JsonElement patchedElem = patch.apply(victimElem);
    		
    		Assert.assertTrue(
    				"patch is not what was expected\n"
				            + "expected: " + result + 
				            "\nactual: " + patchedElem + "\n",
    				JsonUtil.jsonEquals(result, patchedElem));
    	}
    	
    }
}
