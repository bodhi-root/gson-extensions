package com.google.gson.patch.diff;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.patch.JsonPatch;
import com.google.gson.patch.JsonPatchOperation;
import com.google.gson.patch.JsonUtil;
import com.google.gson.pointer.JsonPointer;

// TODO: cleanup
final class DiffProcessor
{
   
    private final Map<JsonPointer, JsonElement> unchanged;

    private final List<DiffOperation> diffs = new ArrayList<>();

    DiffProcessor(final Map<JsonPointer, JsonElement> unchanged)
    {
        this.unchanged = new HashMap<>(unchanged);
    }

    /**
     * Add a DiffOperation indicating a value was replaced
     */
    void valueReplaced(final JsonPointer pointer, final JsonElement oldValue,
        final JsonElement newValue)
    {
        diffs.add(DiffOperation.replace(pointer, oldValue, newValue));
    }

    /**
     * Adds a DiffOperation indicating a value was removed
     */
    void valueRemoved(final JsonPointer pointer, final JsonElement value)
    {
        diffs.add(DiffOperation.remove(pointer, value));
    }

    /**
     * Adds a DiffOperation indicating a value was added
     */
    void valueAdded(final JsonPointer pointer, final JsonElement value)
    {
        final int removalIndex = findPreviouslyRemoved(value);
        if (removalIndex != -1) {
            final DiffOperation removed = diffs.get(removalIndex);
            diffs.remove(removalIndex);
            diffs.add(DiffOperation.move(removed.getFrom(),
                value, pointer, value));
            return;
        }
        final JsonPointer ptr = findUnchangedValue(value);
        final DiffOperation op = ptr != null
            ? DiffOperation.copy(ptr, pointer, value)
            : DiffOperation.add(pointer, value);

        diffs.add(op);
    }

    /**
     * Returns a JsonPatch object, created from the DiffOperations stored 
     * in this object.
     */
    JsonPatch getPatch()
    {
        final List<JsonPatchOperation> list = new ArrayList<>(diffs.size());

        for (final DiffOperation op: diffs)
            list.add(op.asJsonPatchOperation());

        return new JsonPatch(list);
    }

    /**
     * Returns the pointer to an object in the 'unchanged' list for an entry
     * with the given value.
     */
    //@Nullable
    private JsonPointer findUnchangedValue(final JsonElement value)
    {
        //final Predicate<JsonElement> predicate = EQUIVALENCE.equivalentTo(value);
        for (final Map.Entry<JsonPointer, JsonElement> entry: unchanged.entrySet()) {
            if (JsonUtil.jsonEquals(entry.getValue(), value))
                return entry.getKey();
        }
        
        return null;
    }

    /**
     * Returns the index of a DiffOperation in the 'diffs' list for an entry
     * with the given old value.
     */
    private int findPreviouslyRemoved(final JsonElement value)
    {
        DiffOperation op;

        for (int i = 0; i < diffs.size(); i++) {
            op = diffs.get(i);
            if (op.getType() == DiffOperation.Type.REMOVE
                && JsonUtil.jsonEquals(op.getOldValue(), value))
                return i;
        }
        return -1;
    }
}