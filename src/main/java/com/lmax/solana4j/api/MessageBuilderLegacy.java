package com.lmax.solana4j.api;

import java.util.function.Consumer;

/**
 * Interface for building legacy messages in the Solana blockchain.
 * <p>
 * This interface provides methods for setting instructions, specifying the payer, setting the recent blockhash,
 * and sealing the message.
 * </p>
 */
public interface MessageBuilderLegacy
{

    /**
     * Sets the instructions for the legacy message.
     *
     * @param builder a {@link Consumer} that accepts a {@link TransactionBuilder} to build the instructions
     * @return this {@code MessageBuilderLegacy} instance for method chaining
     */
    MessageBuilderLegacy instructions(Consumer<TransactionBuilder> builder);

    /**
     * Sets the payer for the legacy message.
     *
     * @param account the {@link PublicKey} of the payer
     * @return this {@code MessageBuilderLegacy} instance for method chaining
     */
    MessageBuilderLegacy payer(PublicKey account);

    /**
     * Sets the recent blockhash for the legacy message.
     *
     * @param blockHash the {@link Blockhash} of the recent block
     * @return this {@code MessageBuilderLegacy} instance for method chaining
     */
    MessageBuilderLegacy recent(Blockhash blockHash);

    /**
     * Seals the legacy message and returns a sealed message builder.
     *
     * @return a {@link SealedMessageBuilder} instance representing the sealed message
     */
    SealedMessageBuilder seal();
}
