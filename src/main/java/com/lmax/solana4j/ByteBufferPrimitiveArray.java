package com.lmax.solana4j;

import java.nio.ByteBuffer;

import static java.util.Objects.requireNonNull;

/**
 * Utility class for operations involving {@link ByteBuffer} and primitive byte arrays.
 */
public class ByteBufferPrimitiveArray
{

    /**
     * Creates a copy of the contents of the buffer.
     *
     * @param buffer the byte buffer to copy (does not copy the entire underlying array)
     * @return a new byte array of size buffer.limit() with the contents from position 0 to limit.
     * @throws UnsupportedOperationException if the buffer does not have an accessible array
     * @throws NullPointerException          if the buffer is null
     */
    public static byte[] copy(final ByteBuffer buffer)
    {
        requireNonNull(buffer);
        if (!buffer.hasArray())
        {
            throw new UnsupportedOperationException();
        }

        final var offset = buffer.arrayOffset(); // offset is relative to underlying array
        final var bytes = new byte[buffer.limit()]; // limit is relative to position
        System.arraycopy(buffer.array(), offset, bytes, 0, bytes.length);
        return bytes;
    }

    /**
     * Wraps a byte array into a {@link ByteBuffer}.
     *
     * @param bytes the byte array to wrap
     * @return a ByteBuffer containing the given byte array
     */
    public static ByteBuffer toByteBuffer(final byte[] bytes)
    {
        return ByteBuffer.wrap(bytes);
    }
}
