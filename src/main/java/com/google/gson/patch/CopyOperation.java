package com.google.gson.patch;

import com.google.gson.JsonElement;
import com.google.gson.pointer.JsonPointer;
import com.google.gson.pointer.JsonPointerException;

/**
 * JSON Patch {@code copy} operation
 *
 * <p>For this operation, {@code from} is the JSON Pointer of the value to copy,
 * and {@code path} is the destination where the value should be copied.</p>
 *
 * <p>As for {@code add}:</p>
 *
 * <ul>
 *     <li>the value at the destination path is either created or replaced;</li>
 *     <li>it is created only if the immediate parent exists;</li>
 *     <li>{@code -} appends at the end of an array.</li>
 * </ul>
 *
 * <p>It is an error if {@code from} fails to resolve to a JSON value.</p>
 */
public class CopyOperation extends DualPathOperation {

    public CopyOperation(final JsonPointer from, final JsonPointer path) {
    	super(COPY, from, path);
    }
    public CopyOperation(final String from, final String path) throws JsonPointerException {
    	this(new JsonPointer(from), new JsonPointer(path));
    }

    @Override
    public JsonElement apply(final JsonElement node) throws JsonPatchException
    {
    	JsonElement value = from.resolve(node);
    	if (value == null)
    		throw new JsonPatchException("No such path '" + from.toString() + "'");
    	
    	return new AddOperation(path, value).apply(node);
    }
	
}
