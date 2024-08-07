package com.lmax.solana4j.api;

import java.util.function.Consumer;

/**
 * Interface for building inner transactions in a Solana blockchain transaction.
 * <p>
 * This interface provides methods for setting instructions, specifying the payer,
 * and building the inner transaction.
 * </p>
 */
public interface InnerTransactionBuilder
{

    /**
     * Sets the instructions for the inner transaction.
     *
     * @param builder a {@link Consumer} that accepts a {@link TransactionBuilder} to build the instructions
     * @return this {@code InnerTransactionBuilder} instance for method chaining
     */
    InnerTransactionBuilder instructions(Consumer<TransactionBuilder> builder);

    /**
     * Sets the payer for the inner transaction.
     *
     * @param payer the {@link PublicKey} of the payer
     * @return this {@code InnerTransactionBuilder} instance for method chaining
     */
    InnerTransactionBuilder payer(PublicKey payer);

    /**
     * Builds and returns the inner instructions.
     *
     * @return an {@link InnerInstructions} object representing the built inner instructions
     */
    InnerInstructions build();
}
