package com.google.gson.pointer;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.pointer.JsonPointerException;
import com.google.gson.pointer.ReferenceToken;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;

public class ReferenceTokenTest {

	@Test
	public void testFromRaw() {
		assertEquals("abc", ReferenceToken.fromRaw("abc").toString());
		assertEquals("ABC", ReferenceToken.fromRaw("ABC").toString());
		assertEquals("A~0", ReferenceToken.fromRaw("A~").toString());
		assertEquals("A~1", ReferenceToken.fromRaw("A/").toString());
		assertEquals("A~0abc", ReferenceToken.fromRaw("A~abc").toString());
		assertEquals("A~1abc", ReferenceToken.fromRaw("A/abc").toString());
	}
	
	@Test
	public void testFromCooked() throws JsonPointerException {
		assertEquals("A~", ReferenceToken.fromCooked("A~0").getRaw());
		assertEquals("A/", ReferenceToken.fromCooked("A~1").getRaw());
	}
	
	/*
	 {
	   "firstName" : "Daniel",
	   "lastName" : "Rogers",
	   "values" : ["one","two","three"]
	 }
	 */
	private final String JSON = "{" +
			"\"firstName\":\"Daniel\"," +
			"\"lastName\":\"Rogers\"," +
			"\"values\":[\"one\",\"two\",\"three\"]" +
			"}";
	
	@Test
	public void testResolve() throws JsonPointerException {
		JsonElement obj = new JsonParser().parse(JSON);
		
		JsonElement firstName = ReferenceToken.fromRaw("firstName").resolve(obj);
				
		Assert.assertTrue(firstName instanceof JsonPrimitive);
		Assert.assertEquals(firstName.getAsString(), "Daniel");
		
		JsonElement lastName = ReferenceToken.fromRaw("lastName").resolve(obj);
		
		Assert.assertTrue(lastName instanceof JsonPrimitive);
		Assert.assertEquals(lastName.getAsString(), "Rogers");
		
		JsonArray array = ReferenceToken.fromRaw("values").resolve(obj).getAsJsonArray();
		Assert.assertEquals(3, array.size());
		
		Assert.assertEquals("one", ReferenceToken.fromRaw("0").resolve(array).getAsString());
		Assert.assertEquals("two", ReferenceToken.fromRaw("1").resolve(array).getAsString());
		Assert.assertEquals("three", ReferenceToken.fromRaw("2").resolve(array).getAsString());
		Assert.assertNull(ReferenceToken.fromRaw("3").resolve(array));
		Assert.assertNull(ReferenceToken.fromRaw("-").resolve(array));
	}
	
}
