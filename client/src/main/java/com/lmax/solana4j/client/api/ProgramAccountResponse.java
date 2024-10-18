package com.lmax.solana4j.client.api;

/**
 * Represents a response for a program account on the Solana blockchain.
 * This interface provides access to the public key of the program account and the associated account information.
 */
public interface ProgramAccountResponse
{
    /**
     * Returns the public key of the program account.
     * The public key is a base58-encoded string representing the program account on the Solana blockchain.
     *
     * @return the base58-encoded public key of the program account
     */
    String getPubKey();

    /**
     * Returns the account information associated with the program account.
     * This provides detailed information about the account, such as lamports, owner, and other data.
     *
     * @return the {@link AccountInfo} object representing the account information
     */
    AccountInfo getAccount();
}