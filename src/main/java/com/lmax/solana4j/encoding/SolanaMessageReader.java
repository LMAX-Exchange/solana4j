package com.lmax.solana4j.encoding;

import java.nio.ByteBuffer;

final class SolanaMessageReader
{
    private final ByteBuffer buffer;

    SolanaMessageReader(final ByteBuffer buffer)
    {
        this.buffer = buffer;
    }

    SolanaMessage read()
    {
        return new SolanaMessage(buffer);
    }
}
