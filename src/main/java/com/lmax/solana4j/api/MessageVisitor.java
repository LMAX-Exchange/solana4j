package com.lmax.solana4j.api;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

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
         * Returns the list of static accounts in the message.
         *
         * @return a list of {@link PublicKey} objects representing the static accounts
         */
        List<PublicKey> staticAccounts();

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
         * Checks if the given account is a signer.
         *
         * @param account the account to check
         * @return {@code true} if the account is a signer, {@code false} otherwise
         */
        boolean isSigner(PublicKey account);

        /**
         * Returns the list of signer public keys for the transaction.
         *
         * @return a list of {@link PublicKey} objects representing the signers
         */
        List<PublicKey> signers();
    }

    /**
     * Interface representing a view of a legacy message in the Solana blockchain.
     * <p>
     * This interface provides methods to inspect and interact with legacy messages,
     * which are message formats used in earlier versions of the Solana protocol.
     * </p>
     */
    interface LegacyMessageView extends MessageView
    {

        /**
         * Checks if the specified account is marked as a writer in this legacy message.
         *
         * @param account the public key of the account to check
         * @return {@code true} if the account is marked as a writer, {@code false} otherwise
         */
        boolean isWriter(PublicKey account);

        /**
         * Retrieves the list of instructions included in this legacy message.
         *
         * @return a list of {@link LegacyInstructionView} objects representing the instructions
         * within the legacy message
         */
        List<LegacyInstructionView> instructions();
    }

    /**
     * Interface representing a view of a version 0 message in the Solana blockchain.
     * <p>
     * This interface extends the {@link LegacyMessageView} and provides additional methods
     * specific to version 0 messages, which include advanced features such as account lookup tables.
     * </p>
     */
    interface Version0MessageView extends MessageView
    {

        /**
         * Retrieves the version number of this message.
         *
         * @return the version number of the message, expected to be 0 for version 0 messages
         */
        int version();

        /**
         * Retrieves the list of account lookups in the message.
         * <p>
         * Account lookup tables help optimize the transaction by referencing accounts indirectly,
         * allowing for more efficient and flexible transaction structures.
         * </p>
         *
         * @return a list of {@link AccountLookupView} objects representing the account lookups in the message
         */
        List<AccountLookupView> accountLookups();

        /**
         * Retrieves the list of instructions included in this version 0 message.
         *
         * @return a list of {@link V0InstructionView} objects representing the instructions
         * in the version 0 message
         */
        List<V0InstructionView> instructions();

        /**
         * Checks if the specified account is designated as a writer in this version 0 message.
         * <p>
         * This method checks both static accounts and accounts referenced via the provided
         * address lookup tables to determine if the account has write access.
         * </p>
         *
         * @param account             the public key of the account to check
         * @param addressLookupTables the list of address lookup tables to consult when determining if the account is a writer
         * @return {@code true} if the account is a writer, {@code false} otherwise
         * @throws IllegalArgumentException if the address lookup tables provided do not map to
         *                                  any of the lookup accounts in the message
         */
        boolean isWriter(PublicKey account, List<AddressLookupTable> addressLookupTables) throws IllegalArgumentException;

        /**
         * Retrieves the list of all accounts, including both static and lookup accounts.
         *
         * @param addressLookupTables the list of address lookup tables to include
         * @return a list of {@link PublicKey} objects representing all accounts
         * @throws IllegalArgumentException if the address lookup tables provided do not map to
         *                                  any of the lookup accounts in the message
         */
        List<PublicKey> allAccounts(List<AddressLookupTable> addressLookupTables) throws IllegalArgumentException;
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
         * Returns the list of account indexes for the instruction.
         *
         * @return a list of integers representing the account indexes
         */
        List<Integer> accountIndexes();

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
     * Interface representing a legacy instruction view in the Solana blockchain.
     */
    interface LegacyInstructionView extends InstructionView
    {

        /**
         * Returns the list of accounts for the instruction.
         *
         * @return a list of {@link PublicKey} objects representing the accounts
         */
        List<PublicKey> accounts();

        /**
         * Returns the public key of the program for the instruction.
         *
         * @return the {@link PublicKey} of the program
         */
        PublicKey program();
    }

    /**
     * Interface representing a version 0 instruction view in the Solana blockchain.
     */
    interface V0InstructionView extends InstructionView
    {

        /**
         * Returns the list of accounts for the instruction, using the address lookup tables to map
         * the indexes stored in the message to actual addresses
         *
         * @param addressLookupTables the list of address lookup tables
         * @return a list of {@link PublicKey} objects representing the accounts
         * @throws IllegalArgumentException if the address lookup tables provided do not map to
         *                                  any of the lookup accounts in the message
         */
        List<PublicKey> accounts(List<AddressLookupTable> addressLookupTables) throws IllegalArgumentException;

        /**
         * Returns the public key of the program for the instruction, using the address lookup tables to map
         * the indexes stored in the message to actual addresses
         *
         * @param addressLookupTables the list of address lookup tables
         * @return the {@link PublicKey} of the program
         * @throws IllegalArgumentException if the address lookup tables provided do not map to
         *                                  any of the lookup accounts in the message
         */
        PublicKey program(List<AddressLookupTable> addressLookupTables) throws IllegalArgumentException;
    }

    /**
     * Interface representing an account lookup view in the Solana blockchain.
     */
    interface AccountLookupView
    {

        /**
         * Returns the public key of the lookup account.
         *
         * @return the {@link PublicKey} of the lookup account
         */
        PublicKey accountLookup();

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

        /**
         * Finds the address lookup table, using the address lookup tables to map
         * the indexes stored in the message to actual addresses
         *
         * @param addressLookupTables the list of address lookup tables
         * @return an {@link Optional} containing the matching {@link AddressLookupTable}, if found
         */
        Optional<AddressLookupTable> findAddressLookupTable(List<AddressLookupTable> addressLookupTables);
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
    }

    /**
     * Interface representing a view of legacy accounts in the Solana blockchain.
     * <p>
     * Extends the {@link AccountsView} interface to represent legacy account views.
     * </p>
     */
    interface LegacyAccountsView extends AccountsView
    {
    }

    /**
     * Interface representing a view of version 0 accounts in the Solana blockchain.
     * <p>
     * Extends the {@link AccountsView} interface and provides methods specific to version 0 accounts.
     * </p>
     */
    interface V0AccountsView extends AccountsView
    {

        /**
         * Returns the list of all accounts, using the address lookup tables to map
         * the indexes stored in the message to actual addresses
         *
         * @param addressLookupTables the list of address lookup tables to include
         * @return a list of {@link PublicKey} objects representing all accounts
         * @throws IllegalArgumentException if the address lookup tables provided do not map to
         *                                  any of the lookup accounts in the message
         */
        List<PublicKey> accounts(List<AddressLookupTable> addressLookupTables) throws IllegalArgumentException;
    }
}
