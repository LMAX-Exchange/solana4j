package com.lmax.solana4j.api;

import java.nio.ByteBuffer;

public interface ByteBufferSigner
{
    void sign(ByteBuffer transaction, ByteBuffer signature);
}
