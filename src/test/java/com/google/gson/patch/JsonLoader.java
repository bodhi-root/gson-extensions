package com.google.gson.patch;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonLoader {

	public static JsonElement fromResource(String resource) throws IOException {
    	
    	InputStream in = JsonPatchOperationTest.class.getResourceAsStream(resource);
    	if (in == null)
    		throw new IllegalStateException("Resource not found: " + resource);
    	
    	Reader rIn = new InputStreamReader(in);
    	try {
    		JsonParser parser = new JsonParser();
    		return parser.parse(rIn);
    	}
    	finally {
    		rIn.close();
    	}
    
    }
	
}
