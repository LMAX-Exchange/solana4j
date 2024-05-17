package com.lmax.solana4j;

import java.nio.ByteBuffer;

import static java.util.Objects.requireNonNull;

public class ByteBufferPrimitiveArray
{
    /**
     * Creates a copy of the contents of the buffer.
     * @param buffer byte buffer to copy (does not copy entire underlying array)
     * @return new byte array of size buffer.limit() with the contents from position 0 to limit.
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

    public static ByteBuffer toByteBuffer(final byte[] bytes)
    {
        return ByteBuffer.wrap(bytes);
    }
}
