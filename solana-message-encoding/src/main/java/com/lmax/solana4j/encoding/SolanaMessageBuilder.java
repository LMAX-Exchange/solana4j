package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.MessageBuilder;
import com.lmax.solana4j.api.MessageBuilderLegacy;
import com.lmax.solana4j.api.MessageBuilderV0;

import java.nio.ByteBuffer;

import static java.util.Objects.requireNonNull;

final class SolanaMessageBuilder implements MessageBuilder
{
    private final ByteBuffer buffer;

    SolanaMessageBuilder(final ByteBuffer buffer)
    {
        this.buffer = requireNonNull(buffer);
    }


    @Override
    public MessageBuilderLegacy legacy()
    {
        return new SolanaMessageBuilderLegacy(this, this.buffer);
    }

    @Override
    public MessageBuilderV0 v0()
    {
        return new SolanaMessageBuilderV0(this, this.buffer);
    }
}
