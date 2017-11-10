package com.google.gson.patch;

import com.google.gson.JsonElement;
import com.google.gson.pointer.JsonPointer;

/**
 * Base class for patch operations taking a value in addition to a path
 */
public abstract class PathValueOperation extends JsonPatchOperation
{
    
    protected final JsonElement value;

    /**
     * Protected constructor
     *
     * @param op operation name
     * @param path affected path
     * @param value JSON value
     */
    protected PathValueOperation(final String op, final JsonPointer path,
        final JsonElement value)
    {
        super(op, path);
        this.value = JsonUtil.deepCopy(value);
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
        jgen.writeFieldName("value");
        jgen.writeTree(value);
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
    public final String toString() {
        return "op: " + op + "; path: \"" + path + "\"; value: " + value;
    }
    */
    
}