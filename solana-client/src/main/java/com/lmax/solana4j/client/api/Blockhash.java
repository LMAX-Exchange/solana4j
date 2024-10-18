package com.lmax.solana4j.client.api;

/**
 * Represents a blockhash on the Solana blockchain.
 * A blockhash is used to uniquely identify a block and validate the freshness
 * of transactions. This interface provides access to the blockhash value and
 * the last valid block height for the blockhash.
 */
public interface Blockhash
{
    /**
     * Returns the blockhash as a base58-encoded string.
     * The blockhash is a unique identifier for a block on the Solana blockchain
     * and is used to verify that transactions are processed in a timely manner.
     *
     * @return the base58-encoded blockhash
     */
    String getBlockhashBase58();

    /**
     * Returns the last valid block height for the blockhash.
     * This value indicates the height of the block after which the blockhash is no longer valid
     * for processing transactions.
     *
     * @return the last valid block height for the blockhash
     */
    int getLastValidBlockHeight();
}