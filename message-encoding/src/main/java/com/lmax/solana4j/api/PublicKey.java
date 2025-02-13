package com.lmax.solana4j.api;

import java.nio.ByteBuffer;

/**
 * Interface representing a public key.
 * <p>
 * A public key is used to identify accounts on the blockchain.
 * </p>
 */
public interface PublicKey
{

    /**
     * The length of a public key in bytes.
     */
    int PUBLIC_KEY_LENGTH = 32;

    /**
     * Returns the base58-encoded string representation of the public key.
     *
     * @return the base58-encoded public key
     */
    String base58();

    /**
     * Returns the byte array representation of the public key.
     *
     * @return the byte array representation of the public key
     */
    byte[] bytes();

    /**
     * Writes the public key to the provided byte buffer.
     *
     * @param buffer the {@link ByteBuffer} to write the public key to
     */
    void write(ByteBuffer buffer);
}
