package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Slot;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

final class SolanaSlot implements Slot
{
    private static final int SLOT_LENGTH = 8;
    private final byte[] bytes;

    SolanaSlot(final byte[] bytes)
    {
        if (requireNonNull(bytes).length != SLOT_LENGTH)
        {
            throw new IllegalArgumentException("invalid slot length. Expected length: " + SLOT_LENGTH);
        }
        this.bytes = bytes.clone();
    }

    @Override
    public void write(final ByteBuffer buffer)
    {
        buffer.put(bytes);
    }

    @Override
    public byte[] bytes()
    {
        return bytes;
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
        final SolanaSlot that = (SolanaSlot) o;
        return Arrays.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString()
    {
        return "SolanaSlot{" +
               "bytes=" + Arrays.toString(bytes) +
               '}';
    }
}
