package com.lmax.solana4j.client.api;

import java.util.Map;

/**
 * Represents the status of a transaction signature on the blockchain.
 */
public interface SignatureStatus
{
    /**
     * Returns the number of confirmations for the transaction.
     * The number of confirmations indicates how many blocks have been confirmed
     * since the transaction was processed.
     *
     * @return the number of confirmations, or {@code null} if the information is not available
     */
    Long getConfirmations();

    /**
     * Returns the slot in which the transaction was processed.
     *
     * @return the slot number where the transaction was processed
     */
    long getSlot();

    /**
     * Returns the error information, if any, for the transaction.
     *
     * @return an object representing the error, or {@code null} if no error occurred
     */
    Object getErr();

    /**
     * Returns the confirmation status of the transaction.
     *
     * @return the {@link Commitment} representing the confirmation status of the transaction
     */
    Commitment getConfirmationStatus();

    /**
     * Returns the status of the transaction as a key-value pair.
     *
     * @return a {@link Map.Entry} containing the status type and corresponding information
     */
    Map.Entry<String, Object> getStatus();
}