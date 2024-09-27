package com.lmax.solana4j.api;

/**
 * Interface for building instructions in a Solana blockchain transaction.
 * <p>
 * This interface extends {@link InstructionBuilderBase} and provides an additional method
 * for building a {@link MessageBuilder}.
 * </p>
 */
public interface MessageInstructionBuilder extends InstructionBuilderBase
{

    /**
     * Builds and returns a message builder with the instructions.
     *
     * @return a {@link MessageBuilder} object representing the built instructions
     */
    MessageBuilder build();
}
