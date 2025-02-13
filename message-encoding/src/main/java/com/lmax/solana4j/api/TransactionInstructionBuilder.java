package com.lmax.solana4j.api;

/**
 * Interface for building transaction instructions for a Solana transaction.
 * <p>
 * This interface extends {@link InstructionBuilderBase} and provides an additional method
 * for building a {@link MessageBuilder}.
 * </p>
 */
public interface TransactionInstructionBuilder extends InstructionBuilderBase
{

    /**
     * Builds and returns a transaction instruction.
     *
     * @return a {@link TransactionInstruction} object representing the built instruction
     */
    TransactionInstruction build();
}
