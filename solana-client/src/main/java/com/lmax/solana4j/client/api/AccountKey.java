package com.lmax.solana4j.client.api;

/**
 * Represents an account key used in Solana transactions.
 * This interface provides access to the key's properties, such as its source,
 * whether it is a signer, and whether it is writable.
 */
public interface AccountKey
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