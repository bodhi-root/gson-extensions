package com.google.gson.patch;

public final class JsonPatchException extends Exception
{
   
	private static final long serialVersionUID = -220572940290990768L;

	public JsonPatchException(final String message)
    {
        super(message);
    }

    public JsonPatchException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
