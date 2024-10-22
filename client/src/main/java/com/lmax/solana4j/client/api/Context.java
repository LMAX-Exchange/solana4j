package com.lmax.solana4j.client.api;

/**
 * Represents the context of a Solana blockchain query or transaction.
 * The context provides access to essential metadata such as the current slot number
 * and the API version in use.
 *
 * <p>A slot is a unit of time during which the Solana network can process one or more transactions.
 * Each slot represents a specific point in time or block on the Solana blockchain,
 * helping to maintain the ledger's consistency and ordering.</p>
 *
 * <p>The interface allows querying for the current slot number and the API version
 * associated with the context in which a query or transaction is being executed.</p>
 */
public interface Context
{
    /**
     * Returns the slot number associated with this context.
     *
     * <p>A slot on the Solana blockchain is a fixed period during which a validator
     * can propose a block of transactions. Each slot may include multiple transactions,
     * and the slot number helps to identify the exact point in time or block in the chain.</p>
     *
     * @return the slot number for the current context, representing the point in time or block on the blockchain.
     */
    long getSlot();

    /**
     * Returns the API version associated with this context.
     *
     * <p>The API version provides information about the specific version of the Solana
     * network's API being used for this query or transaction. It can help determine
     * compatibility and feature availability when interacting with different versions of the network.</p>
     *
     * @return the API version string for the current context.
     */
    String apiVersion();
}
