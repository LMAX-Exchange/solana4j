package com.lmax.solana4j.util;

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
     * Reverses the order of bytes in the given byte array.
     *
     * @param array the byte array to be reversed
     * @return a new byte array with the bytes in reverse order
     */
    public static byte[] reverse(final byte[] array)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(array.length);

        for (int i = array.length - 1; i >= 0; i--)
        {
            buffer.put(array[i]);
        }

        buffer.flip();

        return buffer.array();
    }
}
