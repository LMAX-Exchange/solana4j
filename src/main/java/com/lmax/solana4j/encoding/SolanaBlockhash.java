package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Blockhash;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

final class SolanaBlockhash implements Blockhash, ByteBufferWriter
{
    private static final int BLOCKHASH_LENGTH = 32;
    private final byte[] bytes;

    SolanaBlockhash(final byte[] bytes)
    {
        if (requireNonNull(bytes).length != BLOCKHASH_LENGTH)
        {
            throw new IllegalStateException("invalid block hash length. Expected length: " + BLOCKHASH_LENGTH);
        }
        this.bytes = bytes.clone();
    }

    @Override
    public void write(final References references, final ByteBuffer buffer)
    {
        buffer.put(bytes);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final SolanaBlockhash that = (SolanaBlockhash) o;
        return Arrays.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(bytes);
    }
}
