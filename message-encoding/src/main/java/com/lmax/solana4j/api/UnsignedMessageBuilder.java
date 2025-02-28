package com.lmax.solana4j.api;

/**
 * Interface for building unsigned messages.
 */
public interface UnsignedMessageBuilder
{

    /**
     * Builds and returns the unsigned message.
     *
     * @return the built {@link Message}
     */
    Message build();
}
