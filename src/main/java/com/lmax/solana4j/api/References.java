package com.lmax.solana4j.api;

/**
 * Interface representing a collection of references to accounts in the Solana blockchain.
 */
public interface References
{

    /**
     * Returns the index of the given account in the reference collection.
     *
     * @param account the {@link PublicKey} of the account
     * @return the index of the account in the collection, or -1 if the account is not found
     */
    int indexOfAccount(PublicKey account);
}
