package com.lmax.solana4j.client.api;

/**
 * Represents information about a signature on the Solana blockchain.
 * This interface provides details about the signature, such as the slot, block time, status, and memo associated with it.
 */
public interface SignatureInfo
{
    /**
     * Returns the base58-encoded transaction signature.
     * The signature is a unique identifier for a transaction on the Solana blockchain.
     *
     * @return the base58-encoded string representing the transaction signature
     */
    String getSignature();

    /**
     * Returns the slot in which the transaction was confirmed.
     * The slot refers to the point in the Solana blockchain where the transaction was processed.
     *
     * @return the slot number in which the transaction was confirmed
     */
    long getSlot();

    /**
     * Returns the error information associated with the transaction, if any.
     * This method returns an object representing the error, or null if the transaction was successful.
     *
     * @return an object representing the error, or null if no error occurred
     */
    Object getErr();

    /**
     * Returns the memo associated with the transaction, if any.
     * A memo is an optional string field that can include additional data or context for the transaction.
     *
     * @return the memo as a string, or null if no memo is associated with the transaction
     */
    String getMemo();

    /**
     * Returns the block time when the transaction was confirmed, if available.
     * The block time is a Unix timestamp representing when the block containing the transaction was confirmed.
     *
     * @return the block time as a Unix timestamp, or null if not available
     */
    Long getBlockTime();

    /**
     * Returns the confirmation status of the transaction.
     * The confirmation status indicates the level of finality for the transaction, such as processed, confirmed, or finalized.
     *
     * @return the {@link Commitment} representing the confirmation status of the transaction
     */
    Commitment getConfirmationStatus();
}