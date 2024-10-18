package com.lmax.solana4j.api;

/**
 * Interface for building unsigned messages.
 * <p>
 * This interface provides a method for building the unsigned message.
 * </p>
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
