package com.lmax.solana4j.client.api;

/**
 * Represents a transaction on the Solana blockchain.
 * This interface provides access to the metadata of the transaction and the core transaction data.
 */
public interface Transaction
{
    /**
     * Returns the metadata associated with the transaction.
     * The metadata contains additional information about the transaction, such as fees, pre- and post-transaction balances, and log messages.
     *
     * @return the {@link TransactionMetadata} object representing the metadata of the transaction
     */
    TransactionMetadata getMeta();

    /**
     * Returns the transaction data and the encoding used.
     * The transaction data contains essential components such as instructions, signatures, and account keys involved in the transaction.
     *
     * @return a list representing the core transaction data and its encoding
     */
    TransactionData getTransaction();
}