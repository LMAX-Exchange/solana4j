package com.lmax.solana4j.buffer;

import java.nio.ByteBuffer;

import static java.util.Objects.requireNonNull;

public final class ByteBufferPrimitiveArray
{
    private ByteBufferPrimitiveArray()
    {
    }

    public static byte[] copy(final ByteBuffer buffer)
    {
        requireNonNull(buffer);
        if (!buffer.hasArray())
        {
            throw new UnsupportedOperationException();
        }

        final var offset = buffer.arrayOffset();
        final var bytes = new byte[buffer.limit()];
        System.arraycopy(buffer.array(), offset, bytes, 0, bytes.length);
        return bytes;
    }
}
