package com.google.gson.pointer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonElement;

/**
 * Implements the RFC6901 Json Pointers for GSON Json documents.
 * 
 * Code based on Jackson implementation of JsonPointer & TreePointer at:
 * https://github.com/fge/jackson-coreutils/blob/master/src/main/java/com/github/fge/jackson/jsonpointer/JsonPointer.java
 */
public class JsonPointer implements Iterable<ReferenceToken> {

	public static final JsonPointer EMPTY = new JsonPointer(Collections.emptyList());
	
	List<ReferenceToken> tokens;
	
	// ------------------------------------------------------------ Constructors
	
	public JsonPointer(List<ReferenceToken> tokens) {
		this.tokens = tokens;
	}
	public JsonPointer(String path) throws JsonPointerException {
		this(fromString(path));
	}
	
	// ---------------------------------------------------------- Implementation
	
	public ReferenceToken getToken(int index) {
		return tokens.get(index);
	}
	public ReferenceToken getLastToken() {
		return (tokens.isEmpty()) ?
				null : tokens.get(tokens.size()-1);
	}
	
	/**
	 * Returns the parent path
	 */
	public final JsonPointer parent() {
		if (tokens.isEmpty())
			return null;
		
		List<ReferenceToken> newTokens = tokens.subList(0, tokens.size()-1);
		return new JsonPointer(newTokens);
	}
	
	public final JsonPointer append(ReferenceToken token) {
		List<ReferenceToken> newTokens = new ArrayList<>(tokens.size()+1);
		newTokens.addAll(tokens);
		newTokens.add(token);
		return new JsonPointer(newTokens);
	}
	public final JsonPointer append(String token) {
		return append(ReferenceToken.fromRaw(token));
	}
	public final JsonPointer append(int index) {
		return append(ReferenceToken.fromInt(index));
	}
	
	/**
     * Traverse a node and return the result
     *
     * <p>Note that this method shortcuts: it stops at the first node it cannot
     * traverse.</p>
     *
     * @param node the node to traverse
     * @return the resulting node, {@code null} if not found
     */
    public final JsonElement resolve(final JsonElement ctx)
    {
        JsonElement ret = ctx;
        for (final ReferenceToken token : tokens) {
            if (ret == null)
                break;
            ret = token.resolve(ret);
        }

        return ret;
    }
	
	/**
	 * Tell whether this pointer is empty
	 *
	 * @return true if the reference token list is empty
	 */
	public final boolean isEmpty()
	{
		return tokens.isEmpty();
	}

	public final Iterator<ReferenceToken> iterator()
	{
		return tokens.iterator();
	}

	@Override
	public final int hashCode()
	{
		return tokens.hashCode();
	}

	@Override
	public final boolean equals(final Object obj)
	{
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final JsonPointer other = (JsonPointer)obj;
		return tokens.equals(other.tokens);
	}

	@Override
	public final String toString()
	{
		final StringBuilder sb = new StringBuilder();
		/*
		 * This works fine: a TokenResolver's .toString() always returns the
		 * cooked representation of its underlying ReferenceToken.
		 */
		for (final ReferenceToken token: tokens)
			sb.append('/').append(token.toString());

		return sb.toString();
	}

    /**
     * Decode an input into a list of reference tokens
     *
     * @param input the input
     * @return the list of reference tokens
     * @throws JsonPointerException input is not a valid JSON Pointer
     * @throws NullPointerException input is null
     */
    protected static List<ReferenceToken> fromString(final String input)
        throws JsonPointerException
    {
    	if (input == null)
    		throw new IllegalArgumentException("Input to 'tokensFromInput()' cannot be NULL");
    	
        String s = input;
        final List<ReferenceToken> ret = new ArrayList<>();
        String cooked;
        int index;
        char c;

        // TODO: see how this can be replaced with a CharBuffer -- seek etc
        while (!s.isEmpty()) {
            c = s.charAt(0);
            if (c != '/')
                throw new JsonPointerException("JsonPointers must begin with a slash ('/') if they are not empty");
            s = s.substring(1);
            index = s.indexOf('/');
            cooked = index == -1 ? s : s.substring(0, index);
            ret.add(ReferenceToken.fromCooked(cooked));
            if (index == -1)
                break;
            s = s.substring(index);
        }

        return ret;
    }
    
}
