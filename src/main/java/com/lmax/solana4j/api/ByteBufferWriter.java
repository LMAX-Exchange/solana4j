package com.lmax.solana4j.api;

import java.nio.ByteBuffer;

public interface ByteBufferWriter
{
    void write(References references, ByteBuffer buffer);
}
