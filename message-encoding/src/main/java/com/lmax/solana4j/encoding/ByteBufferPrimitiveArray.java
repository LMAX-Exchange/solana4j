package com.lmax.solana4j.encoding;

import java.nio.ByteBuffer;

import static java.util.Objects.requireNonNull;

final class ByteBufferPrimitiveArray
{
    private ByteBufferPrimitiveArray()
    {
    }

    static byte[] copy(final ByteBuffer buffer)
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
