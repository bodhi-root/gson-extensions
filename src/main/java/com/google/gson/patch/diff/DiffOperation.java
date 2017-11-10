package com.google.gson.patch.diff;

import com.google.gson.JsonElement;
import com.google.gson.patch.AddOperation;
import com.google.gson.patch.CopyOperation;
import com.google.gson.patch.JsonPatchOperation;
import com.google.gson.patch.MoveOperation;
import com.google.gson.patch.RemoveOperation;
import com.google.gson.patch.ReplaceOperation;
import com.google.gson.pointer.JsonPointer;

final class DiffOperation
{
    private final Type type;
    /* An op's "from", if any */
    private final JsonPointer from;
    /* Value displaced by this operation, if any */
    private final JsonElement oldValue;
    /* An op's "path", if any */
    private final JsonPointer path;
    /* An op's "value", if any */
    private final JsonElement value;

    static DiffOperation add(final JsonPointer path, final JsonElement value)
    {
        return new DiffOperation(Type.ADD, null, null, path, value);
    }

    static DiffOperation copy(final JsonPointer from,
        final JsonPointer path, final JsonElement value)
    {
        return new DiffOperation(Type.COPY, from, null, path,
            value);
    }

    static DiffOperation move(final JsonPointer from,
        final JsonElement oldValue, final JsonPointer path,
        final JsonElement value)
    {
        return new DiffOperation(Type.MOVE, from, oldValue, path,
            value);
    }

    static DiffOperation remove(final JsonPointer from,
        final JsonElement oldValue)
    {
        return new DiffOperation(Type.REMOVE, from, oldValue, null, null);
    }

    static DiffOperation replace(final JsonPointer from,
        final JsonElement oldValue, final JsonElement value)
    {
        return new DiffOperation(Type.REPLACE, from, oldValue, null,
            value);
    }

    private DiffOperation(final Type type, final JsonPointer from,
        final JsonElement oldValue, final JsonPointer path,
        final JsonElement value)
    {
        this.type = type;
        this.from = from;
        this.oldValue = oldValue;
        this.path = path;
        this.value = value;
    }

    Type getType()
    {
        return type;
    }

    JsonPointer getFrom()
    {
        return from;
    }

    JsonElement getOldValue()
    {
        return oldValue;
    }

    JsonPointer getPath()
    {
        return path;
    }

    JsonElement getValue()
    {
        return value;
    }

    JsonPatchOperation asJsonPatchOperation()
    {
        return type.toOperation(this);
    }

    enum Type {
        ADD
            {
                @Override
                JsonPatchOperation toOperation(final DiffOperation op)
                {
                    return new AddOperation(op.path, op.value);
                }
            },
        COPY
        {
            @Override
            JsonPatchOperation toOperation(final DiffOperation op)
            {
                return new CopyOperation(op.from, op.path);
            }
        },
        MOVE
        {
            @Override
            JsonPatchOperation toOperation(final DiffOperation op)
            {
                return new MoveOperation(op.from, op.path);
            }
        },
        REMOVE
        {
            @Override
            JsonPatchOperation toOperation(final DiffOperation op)
            {
                return new RemoveOperation(op.from);
            }
        },
        REPLACE
        {
            @Override
            JsonPatchOperation toOperation(final DiffOperation op)
            {
                return new ReplaceOperation(op.from, op.value);
            }
        },
        ;

        abstract JsonPatchOperation toOperation(final DiffOperation op);
    }
}
