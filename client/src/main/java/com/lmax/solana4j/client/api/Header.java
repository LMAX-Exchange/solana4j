package com.lmax.solana4j.client.api;

/**
 * Represents the header of a transaction on the Solana blockchain.
 * The header contains metadata about the transaction, including information about
 * the number of required signatures, readonly accounts, and whether the accounts are signed.
 */
public interface Header
{
    /**
     * Returns the number of readonly signed accounts in the transaction.
     * Readonly accounts cannot be modified during the transaction but must sign it.
     *
     * @return the number of readonly signed accounts
     */
    int getNumReadonlySignedAccounts();

    /**
     * Returns the number of readonly unsigned accounts in the transaction.
     * Readonly accounts cannot be modified during the transaction and do not need to sign it.
     *
     * @return the number of readonly unsigned accounts
     */
    int getNumReadonlyUnsignedAccounts();

    /**
     * Returns the number of required signatures for the transaction.
     * This represents the total number of accounts that must sign the transaction for it to be valid.
     *
     * @return the number of required signatures
     */
    int getNumRequiredSignatures();
}