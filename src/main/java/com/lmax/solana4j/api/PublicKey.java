package com.lmax.solana4j.api;

import java.nio.ByteBuffer;

public interface PublicKey
{
    int PUBLIC_KEY_LENGTH = 32;

    String base58();
    void write(ByteBuffer buffer);
}
