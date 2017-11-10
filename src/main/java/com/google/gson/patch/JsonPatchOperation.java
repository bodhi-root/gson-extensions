package com.google.gson.patch;

import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.pointer.JsonPointer;
import com.google.gson.pointer.JsonPointerException;

/**
 * Abstract class for all JsonPatchOperations.  Each operation has an "op" and
 * a "path".  The remaining parameters depend on the operation type.
 * 
 * Op         Params
 * add        value   PathValueOperation
 * remove     value
 * replace    value
 * test       value
 * 
 * copy       from    DualPathJsonOperation
 * move       from
 * 
 * see: https://github.com/java-json-tools/json-patch/blob/master/src/main/java/com/github/fge/jsonpatch/JsonPatchOperation.java
 * @author dr21060
 *
 */
public abstract class JsonPatchOperation {
	
	public static final String ADD = "add";
	public static final String REMOVE = "remove";
	public static final String REPLACE = "replace";
	public static final String TEST = "test";
	
	public static final String COPY = "copy";
	public static final String MOVE = "move";
	
	// -------------------------------------------------------------- Properties
	
	protected String op;
	protected JsonPointer path;
	
	// ------------------------------------------------------------ Constructors
	
	protected JsonPatchOperation(String op, JsonPointer path) {
		this.op = op;
		this.path = path;
	}
	
	// --------------------------------------------------------------- Accessors
	
	public void setOp(String op) {
		this.op = op;
	}
	public String getOp() {
		return op;
	}
	
	public void setPath(JsonPointer path) {
		this.path = path;
	}
	public JsonPointer getPath() {
		return path;
	}
	
	// ---------------------------------------------------------- Implementation
	
	/**
	 * Applies this operation to the JsonElement.  The object is modified
	 * by reference and in most cases this same object will be returned.
	 * The only time this is not the case is when REPLACE is used to replace
	 * the current element with a completely new value.  In this case, there
	 * is no other way to apply the update than to return the new value.
	 * 
	 * NOTE: This is different from how json-patch works.  json-patch creates
	 *   a new copy of the object with each operation and never modifies the
	 *   original.  This can consume a lot of memory and slow things down.  If
	 *   you don't want to modify the original object use Util.deepCopy() and
	 *   pass the copy to this method instead.
	 */
	public abstract JsonElement apply(final JsonElement node) throws JsonPatchException;
	
	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		
		obj.add("op", new JsonPrimitive(this.op));
		obj.add("path", new JsonPrimitive(this.path.toString()));
		
		if (this instanceof PathValueOperation) {
			PathValueOperation pv = (PathValueOperation)this;
			if (pv.value != null)
				obj.add("value", pv.value);
		}
		else if (this instanceof DualPathOperation) {
			DualPathOperation dp = (DualPathOperation)this;
			obj.add("from", new JsonPrimitive(dp.from.toString()));
		}
		else {
			throw new IllegalStateException("All JsonPatchOperations should be either PathValueOperation or DualPathOperation");
		}
		
		return obj;
	}
	
	/**
	 * Parses a JsonPatchOperation from a JsonElement.
	 */
	public static JsonPatchOperation fromJson(JsonElement element) throws IOException {
		
		if (!element.isJsonObject())
    		throw new IOException("JsonPatchOperation can only be parsed from JsonObject");
    	
    	JsonObject obj = (JsonObject)element;
    	
    	JsonPrimitive op = obj.getAsJsonPrimitive("op");
    	JsonPrimitive path = obj.getAsJsonPrimitive("path");
    	JsonPrimitive from = obj.getAsJsonPrimitive("from");
    	JsonElement value = obj.get("value");
    	
    	if (op == null)
    		throw new IOException("JsonPatchOperation is missing required property 'op'");
    	
    	String opText = op.getAsString();
    	
    	try {
    		if (opText.equals(ADD)) {
    			return new AddOperation(path.getAsString(), value);
    		} 
    		else if (opText.equals(REMOVE)) {
    			return new RemoveOperation(path.getAsString());
    		}
    		else if (opText.equals(REPLACE)) {
    			return new ReplaceOperation(path.getAsString(), value);
    		}
    		else if (opText.equals(MOVE)) {
    			return new MoveOperation(from.getAsString(), path.getAsString());
    		}
    		else if (opText.equals(COPY)) {
    			return new CopyOperation(from.getAsString(), path.getAsString());
    		}
    		else if (opText.equals(TEST)) {
    			return new TestOperation(path.getAsString(), value);
    		}
    		else {
    			throw new IOException("Invalid 'op' type: '" + opText + "'");
    		}
    	}
    	catch(JsonPointerException e) {
    		throw new IOException(e.getMessage());
    	}
		
	}
	
}
