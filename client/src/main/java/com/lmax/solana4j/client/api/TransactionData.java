package com.lmax.solana4j.client.api;

import java.util.List;

/**
 * Represents the core data of a transaction on the Solana blockchain.
 * This interface provides access to the transaction's message, account keys, and signatures.
 */
public interface TransactionData
{
    /**
     * Returns the message associated with the transaction.
     * The message contains the instructions and related data that define the transaction's actions.
     *
     * @return the {@link Message} object representing the transaction's message
     */
    Message getMessage();

    /**
     * Returns the list of account keys involved in the transaction.
     * Each account key represents a public key of an account that is referenced in the transaction.
     *
     * @return a list of {@link AccountKey} objects representing the account keys used in the transaction
     */
    List<AccountKey> getAccountKeys();

    /**
     * Returns the list of signatures for the transaction.
     * Each signature is a base58-encoded string that verifies the authenticity of the transaction.
     *
     * @return a list of base58-encoded strings representing the transaction's signatures
     */
    List<String> getSignatures();
}