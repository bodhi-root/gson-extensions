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

public final class NonObjectMergePatchTest
{
	
    private final JsonArray testData;

    public NonObjectMergePatchTest() throws IOException
    {
        final String resource = "/jsonpatch/mergepatch/patch-nonobject.json";
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
    		
    		JsonMergePatch patch = JsonMergePatch.fromJson(patchElem);
    		JsonElement patchedElem = patch.apply(victimElem);
    		
    		Assert.assertTrue(JsonUtil.jsonEquals(patchElem, patchedElem));
    	}
    }
}
