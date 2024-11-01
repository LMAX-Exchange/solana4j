package com.lmax.solana4j.client.api;

import java.util.List;

/**
 * Represents the core data of a transaction on the Solana blockchain.
 * This interface provides access to both encoded and parsed transaction data.
 *
 */
public interface TransactionData
{
    /**
     * Returns the parsed core data of the transaction.
     * This parsed data provides structured access to essential elements of the transaction,
     * such as messages, account keys, and signatures.
     *
     * @return the {@link TransactionDataParsed} object representing the parsed transaction data
     */
    TransactionDataParsed getParsedTransactionData();

    /**
     * Returns the encoded transaction data.
     * This data is represented as a list of base64-encoded strings, which provide a compact form of the
     * transaction details for efficient storage and transmission.
     *
     * @return a list of base64-encoded strings representing the transaction data
     */
    List<String> getEncodedTransactionData();

    /**
     * Represents the core data of a transaction on the Solana blockchain.
     * This interface provides access to the transaction's message, account keys, and signatures.
     */
    interface TransactionDataParsed
    {
        /**
         * Returns the message associated with the transaction.
         * The message contains the instructions and related data that define the transaction's actions.
         *
         * @return the {@link Message} object representing the transaction's message
         */
        Message getMessage();

        /**
         * Returns the list of signatures for the transaction.
         * Each signature is a base58-encoded string that verifies the authenticity of the transaction.
         * These signatures confirm that the transaction has been authorized by the corresponding account holders.
         *
         * @return a list of base58-encoded strings representing the transaction's signatures
         */
        List<String> getSignatures();
    }
}
