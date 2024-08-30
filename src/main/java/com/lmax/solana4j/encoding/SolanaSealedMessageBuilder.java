package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.SealedMessageBuilder;
import com.lmax.solana4j.api.SignedMessageBuilder;
import com.lmax.solana4j.api.UnsignedMessageBuilder;

import java.nio.ByteBuffer;

final class SolanaSealedMessageBuilder implements SealedMessageBuilder
{
    private final ByteBuffer buffer;

    SolanaSealedMessageBuilder(final ByteBuffer buffer)
    {
        this.buffer = buffer;
    }

    @Override
    public SignedMessageBuilder signed()
    {
        return new SolanaSignedMessageBuilder(buffer);
    }

    @Override
    public UnsignedMessageBuilder unsigned()
    {
        return new SolanaUnsignedMessageBuilder(buffer);
    }
}
