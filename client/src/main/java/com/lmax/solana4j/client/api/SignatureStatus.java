package com.lmax.solana4j.client.api;

import java.util.Map;

/**
 * Represents the status of a transaction signature on the Solana blockchain.
 * This interface provides details about the confirmation status, including the number of confirmations,
 * the slot in which the transaction was processed, any errors, and the level of confirmation.
 */
public interface SignatureStatus
{
    /**
     * Returns the number of confirmations for the transaction.
     * The number of confirmations indicates how many blocks have been confirmed
     * since the transaction was processed.
     *
     * @return the number of confirmations, or null if the information is not available
     */
    Long getConfirmations();

    /**
     * Returns the slot in which the transaction was processed.
     * The slot represents a specific block in the Solana blockchain where the transaction was included.
     *
     * @return the slot number where the transaction was processed
     */
    long getSlot();

    /**
     * Returns the error information, if any, for the transaction.
     * This method returns an object representing the error encountered during the transaction,
     * or null if the transaction was successful.
     *
     * @return an object representing the error, or null if no error occurred
     */
    Object getErr();

    /**
     * Returns the confirmation status of the transaction.
     * The confirmation status indicates the level of finality reached for the transaction, such as processed,
     * confirmed, or finalized.
     *
     * @return the {@link Commitment} representing the confirmation status of the transaction
     */
    Commitment getConfirmationStatus();

    Map.Entry<String, Object> getStatus();
}