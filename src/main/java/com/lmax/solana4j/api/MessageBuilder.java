package com.lmax.solana4j.api;

/**
 * Interface for building messages in the Solana blockchain.
 * <p>
 * This interface provides methods for creating legacy and version 0 messages.
 * </p>
 */
public interface MessageBuilder
{

    /**
     * Creates and returns a builder for a legacy message.
     *
     * @return a {@link MessageBuilderLegacy} instance for building a legacy message
     */
    MessageBuilderLegacy legacy();

    /**
     * Creates and returns a builder for a version 0 message.
     *
     * @return a {@link MessageBuilderV0} instance for building a version 0 message
     */
    MessageBuilderV0 v0();
}
