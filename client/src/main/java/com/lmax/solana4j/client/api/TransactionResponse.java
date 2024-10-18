package com.lmax.solana4j.client.api;

/**
 * Represents the response of a transaction on the Solana blockchain.
 * This interface provides access to the transaction's metadata, the slot in which the transaction was confirmed,
 * the core transaction data, and the block time.
 */
public interface TransactionResponse
{
    /**
     * Returns the metadata associated with the transaction.
     * The metadata contains detailed information such as fees, balances, log messages, and other execution details.
     *
     * @return the {@link TransactionMetadata} object representing the transaction's metadata
     */
    TransactionMetadata getMetadata();

    /**
     * Returns the slot in which the transaction was confirmed.
     * The slot represents a specific point in time on the Solana blockchain when the transaction was processed.
     *
     * @return the slot number where the transaction was confirmed
     */
    long getSlot();

    /**
     * Returns the core transaction data.
     * The transaction data contains essential components such as instructions, signatures, and account keys involved in the transaction.
     *
     * @return the {@link TransactionData} object representing the core transaction data
     */
    TransactionData getTransaction();

    /**
     * Returns the block time when the transaction was confirmed.
     * The block time is a Unix timestamp representing the approximate time at which the block containing the transaction was confirmed.
     * This value may be null if the block time is not available.
     *
     * @return the block time as a Unix timestamp, or null if not available
     */
    Long getBlockTime();
}