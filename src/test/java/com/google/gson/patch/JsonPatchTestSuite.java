package com.google.gson.patch;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Runs the JSON Test Suite code from:
 * 
 * https://github.com/json-patch/json-patch-tests/blob/master/tests.json
 * 
 */
public final class JsonPatchTestSuite
{
    private final JsonArray testNode;

    public JsonPatchTestSuite() throws IOException
    {
        testNode = JsonLoader.fromResource("/jsonpatch/testsuite.json").getAsJsonArray();
    }

    @Test
    public void testsFromTestSuitePass() throws IOException
    {
    	
    	for (JsonElement testElem : testNode) {
    		
    		//parse test:
    		JsonObject testObj = testElem.getAsJsonObject();
    		
    		if (!testObj.has("patch"))
    			continue;
    		
    		String comment = testObj.has("comment") ? 
    				testObj.get("comment").getAsString() : "(no comment)";
    				
    		//System.out.println("Running test: " + comment);
    				
    		JsonPatch patch = JsonPatch.fromJson(testObj.get("patch"));
    		JsonElement source = testObj.get("doc");
    		JsonElement expected = testObj.get("expected");
    		if (expected == null)
    			expected = source;
    		
    		boolean valid = !testObj.has("error");
    		
    		//run test:
    		try {
    			JsonElement actual = patch.apply(source);

    			if (!valid)
    				Assert.fail("Test was expected to fail!! (comment=" + comment + ")");
    			
    			Assert.assertTrue(
    					"patch is not what was expected" + 
    							"\ncomment: " + comment + 
    				            "\nexpected: " + expected.toString() + 
    				            "\nactual: " + actual.toString() + "\n",
    					JsonUtil.jsonEquals(actual, expected));
    		}
    		catch(JsonPatchException e) {
    			if (valid)
    				Assert.fail("Test was expected to succeed!! (comment=" + comment + "). Error: " + e.getMessage());
    		}
    	}
    	
    }
    
}
