package com.lmax.solana4j.client.api;

/**
 * Represents a transaction signature associated with a specific address on the blockchain.
 */
public interface SignatureForAddress
{

    /**
     * Returns the error information associated with the transaction, if any.
     *
     * @return an object representing the error, or {@code null} if the transaction was successful
     */
    Object getErr();

    /**
     * Returns the memo associated with the transaction, if any.
     *
     * @return a string containing the memo, or {@code null} if no memo is present
     */
    String getMemo();

    /**
     * Returns the base58-encoded signature of the transaction.
     *
     * @return a string representing the transaction's signature
     */
    String getSignature();

    /**
     * Returns the slot number in which the transaction was confirmed.
     *
     * @return the slot number where the transaction was confirmed, or {@code null} if not available
     */
    Long getSlot();

    /**
     * Retrieves the estimated production time of the block containing the transaction.
     * The block time is represented as a Unix timestamp.
     *
     * @return the block time as a Unix timestamp, or {@code null} if not available
     */
    Long getBlockTime();

    /**
     * Retrieves the confirmation status of the transaction.
     *
     * @return the {@link Commitment} representing the confirmation status of the transaction
     */
    Commitment getConfirmationStatus();
}