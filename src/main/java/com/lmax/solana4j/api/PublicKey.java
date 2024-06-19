package com.lmax.solana4j.api;

import java.nio.ByteBuffer;

public interface PublicKey
{
    // TODO: javadoc comment
    int PUBLIC_KEY_LENGTH = 32;

    String base58();
    byte[] bytes();

    void write(ByteBuffer buffer);
}
