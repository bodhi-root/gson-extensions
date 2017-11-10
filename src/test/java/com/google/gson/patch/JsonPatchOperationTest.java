package com.google.gson.patch;

import java.io.IOException;

import org.junit.Test;
import org.junit.Assert;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class JsonPatchOperationTest
{
    
    private final JsonArray errors;
    private final JsonArray ops;

    protected JsonPatchOperationTest(final String prefix)
        throws IOException
    {
        final String resource = "/jsonpatch/" + prefix + ".json";
        final JsonElement node = JsonLoader.fromResource(resource);
        
        JsonObject obj = node.getAsJsonObject();
        
        errors = obj.get("errors").getAsJsonArray();
        ops = obj.get("ops").getAsJsonArray();
    }

    @Test
    public final void testOperations() throws IOException, JsonPatchException
    {
    	
    	for (JsonElement test : ops) {
    		JsonObject objTest = test.getAsJsonObject();
    		
    		JsonElement node = objTest.get("node");
    		JsonPatchOperation op = JsonPatchOperation.fromJson(objTest.get("op"));
    		JsonElement expected = objTest.get("expected");
    	
            JsonElement actual = op.apply(JsonUtil.deepCopy(node));
            
            Assert.assertTrue(
            		"patched node differs from expectations: expected " + expected.toString()
                    + " but found " + actual.toString(),
            		JsonUtil.jsonEquals(actual, expected));
    	}
    	
    }
    
    @Test
    public final void testErrors() throws IOException
    {
    	
    	for (JsonElement test : errors) {
    		JsonObject objTest = test.getAsJsonObject();
    		
    		JsonElement node = objTest.get("node");
    		JsonPatchOperation op = JsonPatchOperation.fromJson(objTest.get("op"));
    		//String message = objTest.get("message").getAsString();
    	
    		try {
                op.apply(JsonUtil.deepCopy(node));
                Assert.fail("No exception thrown (" + objTest.toString() + ")!!");
            }
            catch (JsonPatchException e) {
                //Assert.assertEquals(e.getMessage(), message);
            }
    	}
    	
    }

}
