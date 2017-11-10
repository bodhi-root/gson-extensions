package com.google.gson.patch;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.patch.JsonUtil;

import org.junit.Assert;

public class JsonUtilTest {

	@Test
	public void testAdd() {
		String JSON = "[\"one\",\"two\",\"three\"]";
		
		JsonArray array = new JsonParser().parse(JSON).getAsJsonArray();		
		JsonUtil.add(array, new JsonPrimitive("new"), 0);
		Assert.assertEquals("[\"new\",\"one\",\"two\",\"three\"]", array.toString());
		
		array = new JsonParser().parse(JSON).getAsJsonArray();		
		JsonUtil.add(array, new JsonPrimitive("new"), 1);
		Assert.assertEquals("[\"one\",\"new\",\"two\",\"three\"]", array.toString());
		
		array = new JsonParser().parse(JSON).getAsJsonArray();		
		JsonUtil.add(array, new JsonPrimitive("new"), 2);
		Assert.assertEquals("[\"one\",\"two\",\"new\",\"three\"]", array.toString());
		
		array = new JsonParser().parse(JSON).getAsJsonArray();		
		JsonUtil.add(array, new JsonPrimitive("new"), 3);
		Assert.assertEquals("[\"one\",\"two\",\"three\",\"new\"]", array.toString());
		
		array = new JsonParser().parse(JSON).getAsJsonArray();		
		JsonUtil.add(array, new JsonPrimitive("new"), 4);
		Assert.assertEquals("[\"one\",\"two\",\"three\",null,\"new\"]", array.toString());
		
	}
	
	@Test
	public void testEquals() {
		String JSON = "{\"a\":\"b\"}";
		JsonParser parser = new JsonParser();
		JsonElement p1 = parser.parse(JSON);
		JsonElement p2 = parser.parse(JSON);
		Assert.assertTrue(JsonUtil.jsonEquals(p1, p2));
	}
	
}
