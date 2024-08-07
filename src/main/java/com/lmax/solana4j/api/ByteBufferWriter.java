package com.lmax.solana4j.api;

import java.nio.ByteBuffer;

/**
 * Interface representing an entity capable of writing data to a {@link ByteBuffer} in the context of Solana blockchain references.
 * <p>
 * Implementations of this interface provide the ability to write data related to Solana blockchain references into a buffer.
 * </p>
 */
public interface ByteBufferWriter
{

    /**
     * Writes data to the provided buffer using the given references.
     *
     * @param references the {@link References} used for writing data
     * @param buffer     the {@link ByteBuffer} where the data will be written
     */
    void write(References references, ByteBuffer buffer);
}
