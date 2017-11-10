package com.google.gson.patch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.pointer.JsonPointer;
import com.google.gson.pointer.JsonPointerException;


/**
 * JSON Patch {@code add} operation
 *
 * <p>For this operation, {@code path} is the JSON Pointer where the value
 * should be added, and {@code value} is the value to add.</p>
 *
 * <p>Note that if the target value pointed to by {@code path} already exists,
 * it is replaced. In this case, {@code add} is equivalent to {@code replace}.
 * </p>
 *
 * <p>Note also that a value will be created at the target path <b>if and only
 * if</b> the immediate parent of that value exists (and is of the correct
 * type).</p>
 *
 * <p>Finally, if the last reference token of the JSON Pointer is {@code -} and
 * the immediate parent is an array, the given value is added at the end of the
 * array. For instance, applying:</p>
 *
 * <pre>
 *     { "op": "add", "path": "/-", "value": 3 }
 * </pre>
 *
 * <p>to:</p>
 *
 * <pre>
 *     [ 1, 2 ]
 * </pre>
 *
 * <p>will give:</p>
 *
 * <pre>
 *     [ 1, 2, 3 ]
 * </pre>
 */
public final class AddOperation extends PathValueOperation
{
	
    public AddOperation(final JsonPointer path, final JsonElement value) {
        super(ADD, path, value);
    }
    public AddOperation(final String path, final JsonElement value) throws JsonPointerException {
    	this(new JsonPointer(path), value);
    }

    @Override
    public JsonElement apply(final JsonElement node) throws JsonPatchException
    {
        if (path.isEmpty())
            return value;	//replace entire contents with value

        // Check the parent node: it must exist and be a container (ie an array
        // or an object) for the add operation to work.
        
        final JsonElement parentNode = path.parent().resolve(node);
        if (parentNode == null)
            throw new JsonPatchException("No such parent for path '" + path.toString() + "'");
        
        String token = path.getLastToken().getRaw();
        
        if (parentNode instanceof JsonObject) {
        	JsonObject obj = (JsonObject)parentNode;
        	obj.add(token, JsonUtil.deepCopy(value));
        }
        else if (parentNode instanceof JsonArray) {
        	addToArray((JsonArray)parentNode, token, JsonUtil.deepCopy(value));
        }
        else {
        	throw new JsonPatchException("Parent element of path '" + path.toString() + "' is not a container.  Parent must be an object or array");
        }
        
        return node;
    }

    private void addToArray(final JsonArray array, String token, JsonElement value) throws JsonPatchException
    {
        if (token.equals("-")) {
        	array.add(value);
        	return;
        }
        
        final int size = array.size();
        final int index;
        try {
        	index = Integer.parseInt(token);
        }
        catch(NumberFormatException e) {
        	throw new JsonPatchException("The last token of path '" + path.toString() + "' must indicate the element of an array.  '" + token + "' is an invalid value.");
        }

        if (index < 0 || index > size)
            throw new JsonPatchException("The last token of path '" + path.toString() + "' indicates a value that is outside the bounds of the array.");

        JsonUtil.add(array, value, index);
    }

}
    
