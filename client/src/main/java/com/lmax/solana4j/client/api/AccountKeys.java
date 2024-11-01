package com.lmax.solana4j.client.api;

import java.util.List;

/**
 * Represents a collection of account keys used in a Solana transaction.
 * Account keys are public addresses associated with accounts involved in a transaction.
 * This interface provides access to both encoded and parsed representations of these keys,
 * allowing detailed inspection of each key's properties, such as its origin, whether it is a signer, and if it is writable.
 */
public interface AccountKeys
{
    /**
     * Returns a list of parsed account keys, each providing detailed information on
     * the account's role in the transaction, including its source, signing status, and write permissions.
     *
     * @return a list of {@link AccountKeyParsed} objects representing the parsed account keys
     */
    List<AccountKeyParsed> getParsedAccountKeys();

    /**
     * Returns a list of base58-encoded account keys involved in the transaction.
     * Each key is a compact string representing the public address of an account referenced in the transaction.
     *
     * @return a list of base58-encoded strings representing the account keys
     */
    List<String> getEncodedAccountKeys();

    /**
     * Represents an account key used in Solana transactions.
     * This interface provides access to the key's properties, such as its source,
     * whether it is a signer, and whether it is writable.
     */
    interface AccountKeyParsed
    {
        /**
         * Enum representing the source of the account key.
         * The key can originate either from the transaction itself or from a lookup table.
         */
        enum KeySource
        {
            /** The key originates from the transaction. */
            TRANSACTION,

            /** The key originates from an address lookup table. */
            LOOKUPTABLE
        }

        /**
         * Returns the base58-encoded account key.
         * The key is the public address of the account involved in the transaction.
         *
         * @return the base58-encoded string representing the account key
         */
        String getKey();

        /**
         * Returns the source of the account key.
         * This can indicate whether the key was part of the transaction or fetched
         * from an address lookup table.
         *
         * @return the source of the account key, either {@link KeySource#TRANSACTION} or {@link KeySource#LOOKUPTABLE}
         */
        KeySource getSource();

        /**
         * Indicates whether the account key is a signer.
         * If true, the account has signed the transaction.
         *
         * @return true if the account is a signer, false otherwise
         */
        boolean isSigner();

        /**
         * Indicates whether the account associated with the key is writable.
         * If true, the account can be modified during the transaction.
         *
         * @return true if the account is writable, false otherwise
         */
        boolean isWritable();
    }
}
