package com.lmax.solana4j.client.api;

import java.util.List;

/**
 * Represents an inner instruction in a Solana transaction.
 * Inner instructions are instructions that are executed within a broader transaction and can involve program invocations.
 * This interface provides access to the index of the inner instruction and the list of associated instructions.
 */
public interface InnerInstruction
{
    /**
     * Returns the index of the inner instruction within the transaction.
     * The index indicates the position of this inner instruction in the list of instructions in the transaction.
     *
     * @return the index of the inner instruction
     */
    long getIndex();

    /**
     * Returns the list of instructions that are part of this inner instruction.
     * Each instruction represents an action or a program invocation that is part of the inner transaction.
     *
     * @return a list of {@link Instruction} objects representing the inner instructions
     */
    List<Instruction> getInstructions();
}