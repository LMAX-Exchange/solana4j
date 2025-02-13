package com.lmax.solana4j.api;

import java.nio.ByteBuffer;

/**
 * Interface representing a Slot.
 * <p>
 * A slot is a specific time interval during which transactions are processed on the blockchain.
 * </p>
 */
public interface Slot
{

    /**
     * Writes the slot information to the provided byte buffer.
     *
     * @param buffer the {@link ByteBuffer} to write the slot information to
     */
    void write(ByteBuffer buffer);

    /**
     * Returns the byte array representation of the slot.
     *
     * @return a byte array representing the slot
     */
    byte[] bytes();
}
