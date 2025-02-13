package com.lmax.solana4j.api;

/**
 * Interface for building legacy and V0 messages for the blockchain.
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
     * Creates and returns a builder for a V0 message.
     *
     * @return a {@link MessageBuilderV0} instance for building a V0 message
     */
    MessageBuilderV0 v0();
}
