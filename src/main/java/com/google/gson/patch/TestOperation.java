package com.google.gson.patch;

import com.google.gson.JsonElement;
import com.google.gson.pointer.JsonPointer;
import com.google.gson.pointer.JsonPointerException;

/**
 * JSON Patch {@code test} operation
 *
 * <p>The two arguments for this operation are the pointer containing the value
 * to test ({@code path}) and the value to test equality against ({@code
 * value}).</p>
 *
 * <p>It is an error if no value exists at the given path.</p>
 *
 * <p>Also note that equality as defined by JSON Patch is exactly the same as it
 * is defined by JSON Schema itself. As such, this operation reuses {@link
 * JsonNumEquals} for testing equality.</p>
 */
public final class TestOperation extends PathValueOperation
{
	// ----------------------------------------------------------- Constructors
	
    public TestOperation(final JsonPointer path, final JsonElement value) {
        super(TEST, path, value);
    }
    public TestOperation(final String path, final JsonElement value) throws JsonPointerException {
    	this(new JsonPointer(path), value);
    }

    // --------------------------------------------------------- Implementation
    
    @Override
    public JsonElement apply(final JsonElement node) throws JsonPatchException
    {
    	JsonElement testValue = path.resolve(node);
    	if (testValue == null)
    		throw new JsonPatchException("No such path: '" + path.toString() + "'");
    	
    	if (!JsonUtil.jsonEquals(value, testValue))
    		throw new JsonPatchException("Test operation failed for path '" + path.toString() + "'");

    	return node;
    }

}
    
