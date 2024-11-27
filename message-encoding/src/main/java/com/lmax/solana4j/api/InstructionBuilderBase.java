package com.lmax.solana4j.api;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * Base interface for building instructions for a Solana transaction.
 * <p>
 * This interface provides methods for specifying accounts, programs, and data associated with the instruction.
 * </p>
 */
public interface InstructionBuilderBase
{

    /**
     * Adds an account to the instruction with its role specified.
     *
     * @param account the {@link PublicKey} of the account
     * @param signs   indicates if the account signs the transaction
     * @param writes  indicates if the account writes in the transaction
     * @return this {@code InstructionBuilderBase} instance for method chaining
     */
    InstructionBuilderBase account(PublicKey account, boolean signs, boolean writes);

    /**
     * Specifies the program to be executed by the instruction.
     *
     * @param account the {@link PublicKey} of the program account
     * @return this {@code InstructionBuilderBase} instance for method chaining
     */
    InstructionBuilderBase program(PublicKey account);

    /**
     * Sets the data for the instruction.
     *
     * @param size   the size of the data in bytes
     * @param writer a {@link Consumer} that writes the data to a {@link ByteBuffer}
     * @return this {@code InstructionBuilderBase} instance for method chaining
     */
    InstructionBuilderBase data(int size, Consumer<ByteBuffer> writer);
}
