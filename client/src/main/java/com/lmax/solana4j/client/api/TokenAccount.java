package com.lmax.solana4j.client.api;

/**
 * Represents a token account on the Solana blockchain.
 * A token account is associated with a specific token and holds the token balance for a given public key.
 * This interface provides access to the public key of the token account and the associated account information.
 */
public interface TokenAccount
{
    /**
     * Returns the public key of the token account.
     * The public key is a base58-encoded string that uniquely identifies the token account on the Solana blockchain.
     *
     * @return the base58-encoded public key of the token account
     */
    String getPublicKey();

    /**
     * Returns the account information associated with the token account.
     * The account information contains details about the balance, ownership, and other metadata related to the token account.
     *
     * @return the {@link AccountInfo} object containing the token account information
     */
    AccountInfo getAccountInfo();
}