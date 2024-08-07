package com.lmax.solana4j.api;

/**
 * Interface representing a destination for a transaction in the Solana blockchain.
 * <p>
 * A destination includes a public key representing the destination account and an amount of tokens or lamports to be transferred.
 * </p>
 */
public interface Destination
{

    /**
     * Returns the public key of the destination account.
     *
     * @return the {@link PublicKey} of the destination account
     */
    PublicKey getDestination();

    /**
     * Returns the amount to be transferred to the destination account.
     *
     * @return the amount to be transferred
     */
    long getAmount();
}
