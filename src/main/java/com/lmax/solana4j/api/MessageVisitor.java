package com.lmax.solana4j.api;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Interface representing a visitor for messages in the Solana blockchain.
 * <p>
 * This interface defines methods for visiting and interacting with different views of messages,
 * including legacy and versioned messages.
 * </p>
 *
 * @param <T> the type of the result produced by the visitor
 */
public interface MessageVisitor<T>
{

    /**
     * Visits a message view and produces a result.
     *
     * @param message the message view to visit
     * @return the result produced by visiting the message
     */
    T visit(MessageView message);

    /**
     * Interface representing a view of a message in the Solana blockchain.
     */
    interface MessageView
    {

        /**
         * Returns the count of signed accounts in the message.
         *
         * @return the number of signed accounts
         */
        int countAccountsSigned();

        /**
         * Returns the count of read-only signed accounts in the message.
         *
         * @return the number of read-only signed accounts
         */
        int countAccountsSignedReadOnly();

        /**
         * Returns the count of read-only unsigned accounts in the message.
         *
         * @return the number of read-only unsigned accounts
         */
        int countAccountsUnsignedReadOnly();

        /**
         * Returns the accounts view of the message.
         *
         * @return the {@link AccountsView} of the message
         */
        AccountsView accounts();

        /**
         * Returns the transaction data as a byte buffer.
         * <p>
         * The buffer's position will be set to 0, its limit will be set to its capacity,
         * and its capacity will be the size of the transaction in bytes.
         * </p>
         *
         * @return the transaction data as a {@link ByteBuffer}
         */
        ByteBuffer transaction();

        /**
         * Returns the signature for the given account as a byte buffer.
         * <p>
         * The buffer's position will be set to 0, its limit will be set to its capacity,
         * and its capacity will be the size of the signature in bytes.
         * </p>
         *
         * @param account the account used to sign the transaction
         * @return the signature as a {@link ByteBuffer}
         */
        ByteBuffer signature(PublicKey account);

        /**
         * Returns the public key of the fee payer for the transaction.
         *
         * @return the {@link PublicKey} of the fee payer
         */
        PublicKey feePayer();

        /**
         * Returns the recent blockhash for the transaction.
         *
         * @return the {@link Blockhash} of the recent blockhash
         */
        Blockhash recentBlockHash();

        /**
         * Returns the list of instructions in the message.
         *
         * @return a list of {@link InstructionView} objects representing the instructions
         */
        List<InstructionView> instructions();

        /**
         * Checks if the given account is a signer.
         *
         * @param account the account to check
         * @return {@code true} if the account is a signer, {@code false} otherwise
         */
        boolean isSigner(PublicKey account);

        /**
         * Checks if the given account is a writer.
         *
         * @param account the account to check
         * @return {@code true} if the account is a writer, {@code false} otherwise
         */
        boolean isWriter(PublicKey account);

        /**
         * Returns the list of signer public keys for the transaction.
         *
         * @return a list of {@link PublicKey} objects representing the signers
         */
        List<PublicKey> signers();
    }

    /**
     * Interface representing a legacy message view in the Solana blockchain.
     * <p>
     * This interface extends {@link MessageView} and provides methods specific to legacy messages.
     * </p>
     */
    interface LegacyMessageView extends MessageView
    {
    }

    /**
     * Interface representing a version 0 message view in the Solana blockchain.
     * <p>
     * This interface extends {@link LegacyMessageView} and provides methods specific to version 0 messages.
     * </p>
     */
    interface Version0MessageView extends LegacyMessageView
    {

        /**
         * Returns the version of the message.
         *
         * @return the version of the message
         */
        int version();

        /**
         * Returns the list of account lookup tables used in the message.
         *
         * @return a list of {@link AccountLookupTableView} objects representing the lookup tables
         */
        List<AccountLookupTableView> lookupTables();
    }

    /**
     * Interface representing an instruction view in the Solana blockchain.
     */
    interface InstructionView
    {

        /**
         * Returns the index of the program in the instruction.
         *
         * @return the index of the program
         */
        int programIndex();

        /**
         * Returns the public key of the program for the instruction.
         *
         * @param transactionAccounts the list of transaction accounts
         * @return the {@link PublicKey} of the program
         */
        PublicKey program(List<PublicKey> transactionAccounts);

        /**
         * Returns the list of account indexes for the instruction.
         *
         * @return a list of integers representing the account indexes
         */
        List<Integer> accountIndexes();

        /**
         * Returns the list of accounts for the instruction.
         *
         * @param transactionAccounts the list of transaction accounts
         * @return a list of {@link PublicKey} objects representing the accounts
         */
        List<PublicKey> accounts(List<PublicKey> transactionAccounts);

        /**
         * Returns the instruction data as a byte buffer.
         * <p>
         * The buffer's position will be set to 0, its limit will be set to its capacity,
         * and its capacity will be the size of the data in bytes.
         * </p>
         *
         * @return the instruction data as a {@link ByteBuffer}
         */
        ByteBuffer data();
    }

    /**
     * Interface representing an account lookup table view in the Solana blockchain.
     */
    interface AccountLookupTableView
    {

        /**
         * Returns the public key of the lookup account.
         *
         * @return the {@link PublicKey} of the lookup account
         */
        PublicKey lookupAccount();

        /**
         * Returns the list of read-write table indexes.
         *
         * @return a list of integers representing the read-write table indexes
         */
        List<Integer> readWriteTableIndexes();

        /**
         * Returns the list of read-only table indexes.
         *
         * @return a list of integers representing the read-only table indexes
         */
        List<Integer> readOnlyTableIndexes();
    }

    /**
     * Interface representing a view of accounts in the Solana blockchain.
     */
    interface AccountsView
    {

        /**
         * Returns the list of static accounts.
         *
         * @return a list of {@link PublicKey} objects representing the static accounts
         */
        List<PublicKey> staticAccounts();

        /**
         * Returns the list of all accounts, static accounts and lookup accounts.
         *
         * @return a list of {@link PublicKey} objects representing all accounts
         */
        List<PublicKey> allAccounts();
    }
}
