package com.lmax.solana4j.api;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

/**
 * Interface representing a transaction instruction in the Solana blockchain.
 * <p>
 * A transaction instruction defines the program to be executed and the accounts and data involved in the transaction.
 * </p>
 */
public interface TransactionInstruction
{

    /**
     * Returns a list of account references involved in this instruction.
     *
     * @return a list of {@link AccountReference} objects representing the accounts
     */
    List<AccountReference> accountReferences();

    /**
     * Returns the public key of the program to be executed.
     *
     * @return the {@link PublicKey} of the program
     */
    PublicKey program();

    /**
     * Returns the size of the data for this instruction.
     *
     * @return the size of the data in bytes
     */
    int datasize();

    /**
     * Returns a consumer that writes the data for this instruction to a byte buffer.
     *
     * @return a {@link Consumer} that writes the data to a {@link ByteBuffer}
     */
    Consumer<ByteBuffer> data();

    /**
     * Interface representing a reference to an account in a transaction instruction.
     */
    interface AccountReference
    {

        /**
         * Returns the public key of the account.
         *
         * @return the {@link PublicKey} of the account
         */
        PublicKey account();

        /**
         * Indicates whether this account is a signer.
         *
         * @return {@code true} if this account is a signer, {@code false} otherwise
         */
        boolean isSigner();

        /**
         * Indicates whether this account is a writer.
         *
         * @return {@code true} if this account is a writer, {@code false} otherwise
         */
        boolean isWriter();

        /**
         * Indicates whether this account is executable.
         *
         * @return {@code true} if this account is executable, {@code false} otherwise
         */
        boolean isExecutable();
    }
}
