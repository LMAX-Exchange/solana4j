package com.lmax.solana4j.client.api;

/**
 * Represents a transaction signature associated with a specific address on the Solana blockchain.
 * This interface provides access to details such as errors, memos, signatures, slots, block times,
 * and confirmation statuses.
 */
public interface SignatureForAddress
{

    /**
     * Retrieves the error information associated with the transaction, if any.
     * If the transaction was successful, this method returns {@code null}.
     *
     * @return an object representing the error, or {@code null} if the transaction was successful
     */
    Object getErr();

    /**
     * Retrieves the memo associated with the transaction.
     * If no memo is present, this method returns {@code null}.
     *
     * @return a string containing the memo, or {@code null} if no memo is present
     */
    String getMemo();

    /**
     * Retrieves the base58-encoded signature of the transaction.
     *
     * @return a string representing the transaction's signature
     */
    String getSignature();

    /**
     * Retrieves the slot number in which the transaction was confirmed.
     * The slot is a specific point in the Solana blockchain where the transaction was processed.
     *
     * @return the slot number where the transaction was confirmed
     */
    Long getSlot();

    /**
     * Retrieves the estimated production time of the block containing the transaction.
     * The block time is represented as a Unix timestamp (seconds since the Unix epoch).
     * If the block time is not available, this method returns {@code null}.
     *
     * @return the block time as a Unix timestamp, or {@code null} if not available
     */
    Long getBlockTime();

    /**
     * Retrieves the confirmation status of the transaction.
     * The confirmation status indicates the level of finality reached for the transaction,
     * such as processed, confirmed, or finalized.
     *
     * @return the {@link Commitment} representing the confirmation status of the transaction
     */
    Commitment getConfirmationStatus();
}