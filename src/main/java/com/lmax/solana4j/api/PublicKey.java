package com.lmax.solana4j.api;

import java.nio.ByteBuffer;

public interface PublicKey
{

    /**
     * The length of a public key in bytes.
     * <p>
     * This constant defines the fixed length of a public key used in Solana.
     * </p>
     */
    int PUBLIC_KEY_LENGTH = 32;

    String base58();

    byte[] bytes();

    void write(ByteBuffer buffer);
}

