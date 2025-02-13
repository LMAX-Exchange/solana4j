package com.lmax.solana4j.api;

import java.util.function.Consumer;

/**
 * Interface for building Solana transactions.
 */
public interface TransactionBuilder
{

    /**
     * Appends an instruction to the transaction.
     *
     * @param builder a {@link Consumer} that accepts an {@link InstructionBuilderBase} to build the instruction
     * @return this {@code TransactionBuilder} instance for method chaining
     */
    TransactionBuilder append(Consumer<InstructionBuilderBase> builder);

    /**
     * Appends an instruction to the transaction.
     *
     * @param instruction a {@link TransactionInstruction} that is a pre-built instruction to be appended to the transaction
     * @return this {@code TransactionBuilder} instance for method chaining
     */
    TransactionBuilder append(TransactionInstruction instruction);
}
