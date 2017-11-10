package com.google.gson.patch;

import com.google.gson.JsonElement;
import com.google.gson.pointer.JsonPointer;
import com.google.gson.pointer.JsonPointerException;

/**
 * JSON Patch {@code move} operation
 *
 * <p>For this operation, {@code from} points to the value to move, and {@code
 * path} points to the new location of the moved value.</p>
 *
 * <p>As for {@code add}:</p>
 *
 * <ul>
 *     <li>the value at the destination path is either created or replaced;</li>
 *     <li>it is created only if the immediate parent exists;</li>
 *     <li>{@code -} appends at the end of an array.</li>
 * </ul>
 *
 * <p>It is an error condition if {@code from} does not point to a JSON value.
 * </p>
 *
 * <p>The specification adds another rule that the {@code from} path must not be
 * an immediate parent of {@code path}. Unfortunately, that doesn't really work.
 * Consider this patch:</p>
 *
 * <pre>
 *     { "op": "move", "from": "/0", "path": "/0/x" }
 * </pre>
 *
 * <p>Even though {@code /0} is an immediate parent of {@code /0/x}, when this
 * patch is applied to:</p>
 *
 * <pre>
 *     [ "victim", {} ]
 * </pre>
 *
 * <p>it actually succeeds and results in the patched value:</p>
 *
 * <pre>
 *     [ { "x": "victim" } ]
 * </pre>
 */
public class MoveOperation extends DualPathOperation {

    public MoveOperation(final JsonPointer from, final JsonPointer path) {
    	super(MOVE, from, path);
    }
    public MoveOperation(final String from, final String path) throws JsonPointerException {
    	this(new JsonPointer(from), new JsonPointer(path));
    }

    @Override
    public JsonElement apply(JsonElement node) throws JsonPatchException
    {
    	if (from.equals(path))
    		return node;
    	
    	JsonElement value = from.resolve(node);
    	if (value == null)
    		throw new JsonPatchException("No such path '" + from.toString() + "'");
    	
    	node = new RemoveOperation(from).apply(node);
    	node = new AddOperation(path, value).apply(node);
    	
    	return node;
    }
	
}
