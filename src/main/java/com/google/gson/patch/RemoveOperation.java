package com.google.gson.patch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.pointer.JsonPointer;
import com.google.gson.pointer.JsonPointerException;

/**
 * JSON Path {@code remove} operation
 *
 * <p>This operation only takes one pointer ({@code path}) as an argument. It
 * is an error condition if no JSON value exists at that pointer.</p>
 */
public final class RemoveOperation extends PathValueOperation
{
	// ----------------------------------------------------------- Constructors
	
    public RemoveOperation(final JsonPointer path) {
        super(REMOVE, path, null);
    }
    public RemoveOperation(final String path) throws JsonPointerException {
    	this(new JsonPointer(path));
    }

    // --------------------------------------------------------- Implementation
    
    @Override
    public JsonElement apply(final JsonElement node) throws JsonPatchException
    {
    	//you want to remove the entire thing?  Well... OK
        if (path.isEmpty())
            return null;

        /*
         * Check the parent node: it must exist and be a container (ie an array
         * or an object) for the add operation to work.
         */
        final JsonElement parentNode = path.parent().resolve(node);
        if (parentNode == null)
            throw new JsonPatchException("No such parent for path '" + path.toString() + "'");
        
        String token = path.getLastToken().getRaw();
        
        if (parentNode instanceof JsonObject) {
        	JsonObject obj = (JsonObject)parentNode;
        	JsonElement removed = obj.remove(token);
        	if (removed == null)
        		throw new JsonPatchException("No such path: '" + path.toString() + "'");
        }
        else if (parentNode instanceof JsonArray) {
        	JsonElement removed = removeFromArray((JsonArray)parentNode, token);
        	if (removed == null)
        		throw new JsonPatchException("No such path: '" + path.toString() + "'");
        }
        else {
        	throw new JsonPatchException("Parent element of path '" + path.toString() + "' is not a container.  Parent must be an object or array");
        }
        
        return node;
    }

    private JsonElement removeFromArray(final JsonArray array, String token) throws JsonPatchException
    {
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

        return array.remove(index);
    }

}
    
