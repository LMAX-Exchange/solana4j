package com.lmax.solana4j.client.api;

/**
 * Represents a token account on the Solana blockchain.
 * A token account is associated with a specific token and holds the token balance for a given public key.
 */
public interface TokenAccount
{
    /**
     * Returns the public key of the token account.
     *
     * @return the base58-encoded public key of the token account
     */
    String getPublicKey();

    /**
     * Returns the account information associated with the token account.
     *
     * @return the {@link AccountInfo} object detailing account information
     */
    AccountInfo getAccountInfo();
}