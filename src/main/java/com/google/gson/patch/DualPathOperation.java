package com.google.gson.patch;

import com.google.gson.pointer.JsonPointer;

/**
 * Base class for JSON Patch operations taking two JSON Pointers as arguments.
 * 
 * This is used for COPY and MOVE.
 */
public abstract class DualPathOperation extends JsonPatchOperation {

    protected final JsonPointer from;

    /**
     * Protected constructor
     *
     * @param op operation name
     * @param from source path
     * @param path destination path
     */
    protected DualPathOperation(final String op, final JsonPointer from, final JsonPointer path)
    {
        super(op, path);
        this.from = from;
    }

    public JsonPointer getFrom() {
    	return from;
    }
    
    /*
    @Override
    public final void serialize(final JsonGenerator jgen,
        final SerializerProvider provider)
        throws IOException, JsonProcessingException
    {
        jgen.writeStartObject();
        jgen.writeStringField("op", op);
        jgen.writeStringField("path", path.toString());
        jgen.writeStringField("from", from.toString());
        jgen.writeEndObject();
    }

    @Override
    public final void serializeWithType(final JsonGenerator jgen,
        final SerializerProvider provider, final TypeSerializer typeSer)
        throws IOException, JsonProcessingException
    {
        serialize(jgen, provider);
    }

    @Override
    public final String toString()
    {
        return "op: " + op + "; from: \"" + from + "\"; path: \"" + path + '"';
    }
    */
	
}
