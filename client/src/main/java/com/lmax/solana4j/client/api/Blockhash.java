package com.lmax.solana4j.client.api;

/**
 * Represents a blockhash on the blockchain.
 */
public interface Blockhash
{
    /**
     * Returns the blockhash as a base58-encoded string.
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