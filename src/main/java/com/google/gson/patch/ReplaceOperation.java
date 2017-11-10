package com.google.gson.patch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.pointer.JsonPointer;
import com.google.gson.pointer.JsonPointerException;

/**
 * JSON Patch {@code replace} operation
 *
 * <p>For this operation, {@code path} points to the value to replace, and
 * {@code value} is the replacement value.</p>
 *
 * <p>It is an error condition if {@code path} does not point to an actual JSON
 * value.</p>
 */
public final class ReplaceOperation extends PathValueOperation
{
	
	// ------------------------------------------------------------ Constructors
	
    public ReplaceOperation(final JsonPointer path, final JsonElement value) {
        super(REPLACE, path, value);
    }
    public ReplaceOperation(final String path, final JsonElement value) throws JsonPointerException {
    	this(new JsonPointer(path), value);
    }

    // ---------------------------------------------------------- Implementation
    
    @Override
    public JsonElement apply(final JsonElement node) throws JsonPatchException
    {
    	//replace entire document with new value:
    	if (path.isEmpty())
    		return JsonUtil.deepCopy(value);
    	
    	/*
         * FIXME cannot quite be replaced by a remove + add because of arrays.
         * For instance:
         *
         * { "op": "replace", "path": "/0", "value": 1 }
         *
         * with
         *
         * [ "x" ]
         *
         * If remove is done first, the array is empty and add rightly complains
         * that there is no such index in the array.
         */
    	final JsonElement parentNode = path.parent().resolve(node);
        if (parentNode == null)
            throw new JsonPatchException("No such parent for path '" + path.toString() + "'");
    	
        String token = path.getLastToken().getRaw();
        
        if (parentNode instanceof JsonObject) {
        	JsonObject obj = (JsonObject)parentNode;
        	JsonElement removed = obj.remove(token);
        	if (removed == null)
        		throw new JsonPatchException("No such path '" + path.toString() + "'");
        	
        	obj.add(token, JsonUtil.deepCopy(value));
        }
        else if (parentNode instanceof JsonArray) {
        	
        	JsonArray array = (JsonArray)parentNode;
        	
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

            array.set(index, JsonUtil.deepCopy(value));
        }
        else {
        	throw new JsonPatchException("Parent element of path '" + path.toString() + "' is not a container.  Parent must be an object or array");
        }
        
        return node;
    }
    
}
    
