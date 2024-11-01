package com.lmax.solana4j.client.api;

/**
 * Represents the response of a transaction on the Solana blockchain.
 * This interface provides access to various details of a confirmed transaction, including
 * its slot number, timestamp, version, core transaction data, and associated metadata.
 */
public interface TransactionResponse
{
    /**
     * Returns the slot in which the transaction was confirmed.
     * The slot represents a specific point in time on the Solana blockchain when the transaction was processed.
     *
     * @return the slot number where the transaction was confirmed
     */
    long getSlot();

    /**
     * Returns the block time when the transaction was confirmed.
     * The block time is a Unix timestamp representing the approximate time at which the block containing
     * the transaction was confirmed. This value may be null if the block time is not available.
     *
     * @return the block time as a Unix timestamp, or null if not available
     */
    Long getBlockTime();

    /**
     * Returns the version of the transaction.
     * The version represents the transaction version used when encoding the transaction data.
     * This can be used to distinguish between different encoding versions of Solana transactions.
     *
     * @return the version of the transaction as a string
     */
    String getVersion();

    /**
     * Returns the core transaction data and its encoding.
     * The transaction data contains essential components such as instructions, signatures, and account keys
     * involved in the transaction. This data provides the fundamental elements that make up the transaction.
     *
     * @return the {@link TransactionData} object representing the core transaction data and its encoding
     */
    TransactionData getTransactionData();

    /**
     * Returns the metadata associated with the transaction.
     * The metadata provides detailed information such as fees, balances, log messages, and other execution details
     * generated during the transaction's processing. This information helps to analyze the transaction's outcome.
     *
     * @return the {@link TransactionMetadata} object representing the transaction's metadata
     */
    TransactionMetadata getMetadata();
}