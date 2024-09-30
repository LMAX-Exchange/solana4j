package com.lmax.solana4j.api;

import java.nio.ByteBuffer;

/**
 * Interface representing a blockhash on the Solana blockchain.
 * <p>
 * A blockhash is a unique identifier for a block in the Solana blockchain, used for various purposes
 * including transaction validation and preventing double-spending.
 * </p>
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
