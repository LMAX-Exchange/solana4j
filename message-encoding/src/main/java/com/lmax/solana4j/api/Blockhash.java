package com.lmax.solana4j.api;

import java.nio.ByteBuffer;

/**
 * Interface representing a blockhash on the blockchain.
 */
public interface Blockhash
{
    /**
     * Writes the blockhash to the provided byte buffer.
     *
     * @param buffer the {@link ByteBuffer} to write the blockhash to
     */
    void write(ByteBuffer buffer);
}
