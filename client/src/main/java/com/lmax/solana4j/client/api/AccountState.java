package com.lmax.solana4j.client.api;

/**
 * Represents the possible states of an account on the Solana blockchain.
 * The account state indicates whether the account is ready for use, not yet initialized, or restricted.
 */
public enum AccountState
{
    /**
     * The account is not initialized.
     * This state indicates that the account is present but has not been set up for any specific purpose or usage.
     */
    UNINITIALIZED,

    /**
     * The account is fully initialized and ready for use.
     * In this state, the account can participate in transactions and perform its intended functions.
     */
    INITIALIZED,

    /**
     * The account is frozen and restricted from normal operations.
     * Accounts in this state are locked and may require specific actions to become active again.
     */
    FROZEN;
}
