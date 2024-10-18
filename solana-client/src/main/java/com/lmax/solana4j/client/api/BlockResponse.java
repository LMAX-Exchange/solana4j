package com.lmax.solana4j.client.api;

import java.util.List;

/**
 * Represents the response data for a block on the Solana blockchain.
 * This interface provides access to various details about a block, such as its height,
 * hash, time, transactions, and the parent block information.
 */
public interface BlockResponse
{
    /**
     * Returns the height of the block.
     * The block height is the number of blocks preceding the current block in the blockchain.
     *
     * @return the height of the block, or null if not available
     */
    Long getBlockHeight();

    /**
     * Returns the timestamp of the block.
     * The block time represents the approximate Unix timestamp (in seconds) when the block was produced.
     *
     * @return the block time as a Unix timestamp, or null if not available
     */
    Long getBlockTime();

    /**
     * Returns the base58-encoded blockhash for the current block.
     * The blockhash uniquely identifies the block on the Solana blockchain.
     *
     * @return the base58-encoded string representing the blockhash
     */
    String getBlockhash();

    /**
     * Returns the parent slot for the block.
     * The parent slot refers to the slot number of the parent block in the Solana blockchain.
     *
     * @return the parent slot of the block
     */
    long getParentSlot();

    /**
     * Returns the base58-encoded blockhash of the previous block.
     * This allows linking the current block to the previous block in the blockchain.
     *
     * @return the base58-encoded string representing the previous blockhash
     */
    String getPreviousBlockhash();

    /**
     * Returns a list of transactions included in the block.
     * Each transaction represents an action or set of actions that occurred in the block.
     *
     * @return a list of {@link Transaction} objects representing the block's transactions
     */
    List<Transaction> getTransactions();
}