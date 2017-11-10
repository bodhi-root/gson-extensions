package com.google.gson.patch.diff;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.patch.JsonPatch;
import com.google.gson.patch.JsonUtil;
import com.google.gson.pointer.JsonPointer;

/**
 * JSON "diff" implementation
 *
 * <p>This class generates a JSON Patch (as in, an RFC 6902 JSON Patch) given
 * two JSON values as inputs. The patch can be obtained directly as a {@link
 * JsonPatch} or as a {@link JsonNode}.</p>
 *
 * <p>Note: there is <b>no guarantee</b> about the usability of the generated
 * patch for any other source/target combination than the one used to generate
 * the patch.</p>
 *
 * <p>This class always performs operations in the following order: removals,
 * additions and replacements. It then factors removal/addition pairs into
 * move operations, or copy operations if a common element exists, at the same
 * {@link JsonPointer pointer}, in both the source and destination.</p>
 *
 * <p>You can obtain a diff either as a {@link JsonPatch} directly or, for
 * backwards compatibility, as a {@link JsonNode}.</p>
 *
 * @since 1.2
 */
public final class JsonDiff
{
    
    private JsonDiff()
    {
    }

    /**
     * Generate a JSON patch for transforming the source node into the target
     * node
     *
     * @param source the node to be patched
     * @param target the expected result after applying the patch
     * @return the patch as a {@link JsonPatch}
     *
     * @since 1.9
     */
    public static JsonPatch diff(final JsonElement source, final JsonElement target)
    {
    	if (source == null)
    		throw new IllegalArgumentException("source may not be NULL");
    	if (target == null)
    		throw new IllegalArgumentException("target may not be NULL");
    	
        final Map<JsonPointer, JsonElement> unchanged
            = getUnchangedValues(source, target);
        final DiffProcessor processor = new DiffProcessor(unchanged);

        generateDiffs(processor, JsonPointer.EMPTY, source, target);
        return processor.getPatch();
    }

    private static void generateDiffs(
    		final DiffProcessor processor,
    		final JsonPointer pointer, 
    		final JsonElement source, final JsonElement target)
    {
        if (JsonUtil.jsonEquals(source, target))
            return;

        final ElementType firstType = ElementType.of(source);
        final ElementType secondType = ElementType.of(target);
        
        //Node types differ: generate a replacement operation.
        if (firstType != secondType) {
            processor.valueReplaced(pointer, source, target);
            return;
        }

        
        // If we reach this point, it means that both nodes are the same type,
        // but are not equivalent.
        
        // If this is not a container, generate a replace operation.
        if (source.isJsonPrimitive()) {
            processor.valueReplaced(pointer, source, target);
            return;
        }

        // now we have either JsonObject or JsonArray
        if (firstType == ElementType.OBJECT) {
            generateObjectDiffs(processor, pointer, 
            		(JsonObject)source, (JsonObject)target);
        }
        else if (firstType == ElementType.ARRAY) {
            generateArrayDiffs(processor, pointer, 
            		(JsonArray)source, (JsonArray)target);
        } 
        else {
        	throw new IllegalStateException("Should never reach this line of code");
        }
    }

    private static void generateObjectDiffs(final DiffProcessor processor,
        final JsonPointer pointer, final JsonObject source,
        final JsonObject target)
    {
        final Set<String> firstFields = JsonUtil.propertyNames(source);
        final Set<String> secondFields = JsonUtil.propertyNames(target);
        
        final Set<String> commonFields = new HashSet<>(firstFields.size());
        
        //remove common elements:
        for (String field : new HashSet<String>(firstFields)) {
        	if (secondFields.remove(field)) {
        		firstFields.remove(field);
        		commonFields.add(field);
        	}
        }
        
        //in the source but not the target => removed
        for (final String field: firstFields)
            processor.valueRemoved(pointer.append(field), source.get(field));

        //in the target but not the source => added
        for (final String field: secondFields)
            processor.valueAdded(pointer.append(field), target.get(field));

        //in both => look for value changes
        for (final String field: commonFields)
            generateDiffs(processor, pointer.append(field), source.get(field),
                target.get(field));
    }

    private static void generateArrayDiffs(final DiffProcessor processor,
        final JsonPointer pointer, final JsonArray source,
        final JsonArray target)
    {
        final int firstSize = source.size();
        final int secondSize = target.size();
        final int size = Math.min(firstSize, secondSize);

        // Source array is larger; in this case, elements are removed from the
        // target; the index of removal is always the original arrays's length.
        for (int index = size; index < firstSize; index++)
            processor.valueRemoved(pointer.append(size), source.get(index));

        for (int index = 0; index < size; index++) {
            generateDiffs(processor, pointer.append(index), source.get(index),
                target.get(index));
        }

        // Deal with the destination array being larger...
        for (int index = size; index < secondSize; index++)
            processor.valueAdded(pointer.append("-"), target.get(index));
    }


    //@VisibleForTesting
    static Map<JsonPointer, JsonElement> getUnchangedValues(
    		final JsonElement source, final JsonElement target)
    {
        final Map<JsonPointer, JsonElement> ret = new HashMap<>();
        computeUnchanged(ret, JsonPointer.EMPTY, source, target);
        return ret;
    }

    private static void computeUnchanged(final Map<JsonPointer, JsonElement> ret,
        final JsonPointer pointer, final JsonElement first, final JsonElement second)
    {
        if (JsonUtil.jsonEquals(first, second)) {
            ret.put(pointer, second);
            return;
        }

        final ElementType firstType = ElementType.of(first);
        final ElementType secondType = ElementType.of(second);

        if (firstType != secondType)
            return; // nothing in common

        // We know they are both the same type, so...

        switch (firstType) {
            case OBJECT:
                computeObject(ret, pointer, (JsonObject)first, (JsonObject)second);
                break;
            case ARRAY:
                computeArray(ret, pointer, (JsonArray)first, (JsonArray)second);
            default:
                /* nothing */
        }
    }

    private static void computeObject(final Map<JsonPointer, JsonElement> ret,
        final JsonPointer pointer, final JsonObject source,
        final JsonObject target)
    {
        final Set<String> firstFields = JsonUtil.propertyNames(source);

        for (String name : firstFields) {
            
            if (!target.has(name))
                continue;
            
            computeUnchanged(ret, pointer.append(name), source.get(name),
                target.get(name));
        }
    }

    private static void computeArray(final Map<JsonPointer, JsonElement> ret,
        final JsonPointer pointer, final JsonArray source, final JsonArray target)
    {
        final int size = Math.min(source.size(), target.size());

        for (int i = 0; i < size; i++)
            computeUnchanged(ret, pointer.append(i), source.get(i),
                target.get(i));
    }
}
