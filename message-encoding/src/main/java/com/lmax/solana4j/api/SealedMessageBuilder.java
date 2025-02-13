package com.lmax.solana4j.api;

/**
 * Interface for building sealed messages.
 */
public interface SealedMessageBuilder
{

    /**
     * Creates and returns a builder for a signed message.
     *
     * @return a {@link SignedMessageBuilder} instance for building a signed message
     */
    SignedMessageBuilder signed();

    /**
     * Creates and returns a builder for an unsigned message.
     *
     * @return a {@link UnsignedMessageBuilder} instance for building an unsigned message
     */
    UnsignedMessageBuilder unsigned();
}
