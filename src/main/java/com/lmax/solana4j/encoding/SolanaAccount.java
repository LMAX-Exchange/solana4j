package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.PublicKey;
import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

final class SolanaAccount implements PublicKey, Comparable<SolanaAccount>
{
    public static final int PUBLIC_KEY_LENGTH = 32;
    /*protected*/ final byte[] bytes;

    SolanaAccount(final byte[] bytes)
    {
        if (requireNonNull(bytes).length != PUBLIC_KEY_LENGTH)
        {
            throw new IllegalArgumentException("invalid public key length. Expected length: " + PUBLIC_KEY_LENGTH);
        }
        this.bytes = bytes.clone();
    }

    @Override
    public String base58()
    {
        return Base58.encode(bytes);
    }

    @Override
    public void write(final ByteBuffer buffer)
    {
        buffer.put(bytes);
    }

    @Override
    public int compareTo(final SolanaAccount other)
    {
        final var otherBytes = requireNonNull(other).bytes;

        for (int i = 0; i < PUBLIC_KEY_LENGTH; i++)
        {
            final int diff = (bytes[i] & 0xff) - (otherBytes[i] & 0xff);
            if (diff != 0)
            {
                return diff;
            }
        }
        return 0;
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
        final SolanaAccount that = (SolanaAccount) o;
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
        return "SolanaAccount{'" + base58() + "'}";
    }
}
