package com.lmax.solana4j.api;

/**
 * Interface for building inner instructions in a Solana blockchain transaction.
 * <p>
 * This interface extends {@link InstructionBuilderBase} and provides additional functionality
 * for adding inner instructions to a transaction.
 * </p>
 */
public interface InnerInstructionBuilder extends InstructionBuilderBase
{

    /**
     * Adds an inner instruction to the transaction.
     */
    void addInstruction();
}
