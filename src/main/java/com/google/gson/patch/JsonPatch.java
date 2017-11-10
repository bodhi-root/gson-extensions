package com.google.gson.patch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * Implementation of JSON Patch
 *
 * <p><a href="http://tools.ietf.org/html/draft-ietf-appsawg-json-patch-10">JSON
 * Patch</a>, as its name implies, is an IETF draft describing a mechanism to
 * apply a patch to any JSON value. This implementation covers all operations
 * according to the specification; however, there are some subtle differences
 * with regards to some operations which are covered in these operations'
 * respective documentation.</p>
 *
 * <p>An example of a JSON Patch is as follows:</p>
 *
 * <pre>
 *     [
 *         {
 *             "op": "add",
 *             "path": "/-",
 *             "value": {
 *                 "productId": 19,
 *                 "name": "Duvel",
 *                 "type": "beer"
 *             }
 *         }
 *     ]
 * </pre>
 *
 * <p>This patch contains a single operation which adds an item at the end of
 * an array. A JSON Patch can contain more than one operation; in this case, all
 * operations are applied to the input JSON value in their order of appearance,
 * until all operations are applied or an error condition is encountered.</p>
 *
 * <p>The main point where this implementation differs from the specification
 * is initial JSON parsing. The draft says:</p>
 *
 * <pre>
 *     Operation objects MUST have exactly one "op" member
 * </pre>
 *
 * <p>and:</p>
 *
 * <pre>
 *     Additionally, operation objects MUST have exactly one "path" member.
 * </pre>
 *
 * <p>However, obeying these to the letter forces constraints on the JSON
 * <b>parser</b>. Here, these constraints are not enforced, which means:</p>
 *
 * <pre>
 *     [ { "op": "add", "op": "remove", "path": "/x" } ]
 * </pre>
 *
 * <p>is parsed (as a {@code remove} operation, since it appears last).</p>
 *
 * <p><b>IMPORTANT NOTE:</b> the JSON Patch is supposed to be VALID when the
 * constructor for this class ({@link JsonPatch#fromJson(JsonNode)} is used.</p>
 */
public class JsonPatch {

	/**
     * List of operations
     */
    private final List<JsonPatchOperation> operations;

    /**
     * Constructor
     *
     * <p>Normally, you should never have to use it.</p>
     *
     * @param operations the list of operations for this patch
     * @see JsonPatchOperation
     */
    public JsonPatch(final List<JsonPatchOperation> operations)
    {
        this.operations = operations;
    }
    public JsonPatch() {
    	this(new ArrayList<JsonPatchOperation>());
    }
    
    public void add(JsonPatchOperation op) {
    	operations.add(op);
    }

    /**
     * Apply this patch to a JSON value
     *
     * @param node the value to apply the patch to
     * @return the patched JSON value (This is usually the same as the value
     *         that was passed in.  See JsonPathcOperation.apply() for details.
     * @throws JsonPatchException failed to apply patch
     * @throws NullPointerException input is null
     */
    public JsonElement apply(final JsonElement node) throws JsonPatchException
    {
    	if (node == null)
    		throw new IllegalArgumentException("Parameter to 'JsonPatch.apply()' cannot be NULL");
    	
    	JsonElement ret = node;
        for (final JsonPatchOperation operation: operations)
            ret = operation.apply(ret);

        return ret;
    }
    
    public static JsonPatch fromJson(JsonElement element) throws IOException {
    	if (!element.isJsonArray())
    		throw new IOException("JsonPatch objects can only be parsed from JsonArray");
    	
    	JsonPatch patch = new JsonPatch();
    	
    	JsonArray opArray = (JsonArray)element;
    	for (JsonElement opElem : opArray) {
    		JsonPatchOperation op = JsonPatchOperation.fromJson(opElem);
    		patch.add(op);
    	}
    	
    	return patch;
    }
    
    public JsonArray toJson() {
    	JsonArray array = new JsonArray();
    	
    	for (JsonPatchOperation op : operations)
    		array.add(op.toJson());
    	
    	return array;
    }
	
}
