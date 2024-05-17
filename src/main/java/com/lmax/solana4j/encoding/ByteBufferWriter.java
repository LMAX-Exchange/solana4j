package com.lmax.solana4j.encoding;

import java.nio.ByteBuffer;

interface ByteBufferWriter
{
    void write(References references, ByteBuffer buffer);
}
