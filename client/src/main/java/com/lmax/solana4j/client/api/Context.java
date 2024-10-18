package com.lmax.solana4j.client.api;

/**
 * Represents the context of a Solana blockchain query or transaction.
 * This interface provides access to the slot information associated with the current context.
 * A slot is a specific point in time or block on the Solana blockchain.
 */
public interface Context
{
    /**
     * Returns the slot number associated with the context.
     * A slot is a logical unit of time on the Solana blockchain, and each slot can contain multiple transactions.
     *
     * @return the slot number for the current context
     */
    long getSlot();
}