package com.google.gson.pointer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.pointer.JsonPointer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test code for JsonPointer.  The JsonPointer and JsonPointerTest code was
 * taken from: https://github.com/johnnylambada/gson
 * 
 * The tests were modified to use the non-intrusive 'new JsonPointer(elem)'
 * instead of expected JsonElement to have a 'getPointer()' method.
 */
public class JsonPointerTest {

	/*
	{
		"library": {
			"name": "library of congress",
			"section": [{
				"name": "sci-fi",
				"title": [{
					"book": {
						"name": "Mote in Gods Eye",
						"author": ["Larry Niven", "Jerry Pournelle"]
					}
				}, {
					"book": {
						"name": "Ringworld",
						"author": ["Larry Niven"]
					}
				}]
			}]
		}
	}

	RFC 6901
	{
	      "foo": ["bar", "baz"],
	      "": 0,
	      "a/b": 1,
	      "c%d": 2,
	      "e^f": 3,
	      "g|h": 4,
	      "i\\j": 5,
	      "k\"l": 6,
	      " ": 7,
	      "m~n": 8
	}
	 */
	
    private final String JSON = "{\n" +
            "\t\"library\": {\n" +
            "\t\t\"name\": \"library of congress\",\n" +
            "\t\t\"section\": [{\n" +
            "\t\t\t\"name\": \"sci-fi\",\n" +
            "\t\t\t\"title\": [{\n" +
            "\t\t\t\t\"book\": {\n" +
            "\t\t\t\t\t\"name\": \"Mote in Gods Eye\",\n" +
            "\t\t\t\t\t\"author\": [\"Larry Niven\", \"Jerry Pournelle\"]\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"book\": {\n" +
            "\t\t\t\t\t\"name\": \"Ringworld\",\n" +
            "\t\t\t\t\t\"author\": [\"Larry Niven\"]\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}]\n" +
            "\t\t}]\n" +
            "\t}\n" +
            "}";
    private final String JSON_6901 = "{\n" +
            "      \"foo\": [\"bar\", \"baz\"],\n" +
            "      \"\": 0,\n" +
            "      \"a/b\": 1,\n" +
            "      \"c%d\": 2,\n" +
            "      \"e^f\": 3,\n" +
            "      \"g|h\": 4,\n" +
            "      \"i\\\\j\": 5,\n" +
            "      \"k\\\"l\": 6,\n" +
            "      \" \": 7,\n" +
            "      \"m~n\": 8\n" +
            "   }";

    @Test
    /**
     * See https://tools.ietf.org/html/rfc6901
     */
    public void rfc6901Test() throws Exception {
        JsonElement root = new JsonParser().parse(JSON_6901);
        
        assertEquals(root, new JsonPointer("").resolve(root));
        assertEquals(new JsonParser().parse("[\"bar\", \"baz\"]"), new JsonPointer("/foo").resolve(root));
        assertEquals("bar",new JsonPointer("/foo/0").resolve(root).getAsString());
        
        assertEquals(0,new JsonPointer("/").resolve(root).getAsInt());
        assertEquals(1,new JsonPointer("/a~1b").resolve(root).getAsInt());
        assertEquals(2,new JsonPointer("/c%d").resolve(root).getAsInt());
        assertEquals(3,new JsonPointer("/e^f").resolve(root).getAsInt());
        assertEquals(4,new JsonPointer("/g|h").resolve(root).getAsInt());
        assertEquals(5,new JsonPointer("/i\\j").resolve(root).getAsInt());
        assertEquals(6,new JsonPointer("/k\"l").resolve(root).getAsInt());
        assertEquals(7,new JsonPointer("/ ").resolve(root).getAsInt());
        assertEquals(8,new JsonPointer("/m~0n").resolve(root).getAsInt());
    }

    @Test
    public void getRootTest() throws Exception {
        JsonElement root = new JsonParser().parse(JSON);
        JsonElement actual = new JsonPointer("").resolve(root);
        assertEquals(root,actual);
    }

    @Test
    public void getSimpleStringTest() throws Exception {
        JsonElement root = new JsonParser().parse(JSON);
        String expected = "library of congress";
        String actual = new JsonPointer("/library/name").resolve(root).getAsString();
        assertEquals(expected,actual);
    }

    @Test
    public void getSimpleStringInArrayTest() throws Exception {
    	JsonElement root = new JsonParser().parse(JSON);
        String expected = "sci-fi";
        String actual = new JsonPointer("/library/section/0/name").resolve(root).getAsString();
        assertEquals(expected,actual);
    }

    @Test
    public void getDeepArrayTest() throws Exception {
    	JsonElement root = new JsonParser().parse(JSON);
        String expected = "Jerry Pournelle";
        String actual = new JsonPointer("/library/section/0/title/0/book/author/1").resolve(root).getAsString();
        assertEquals(expected,actual);
    }

}
