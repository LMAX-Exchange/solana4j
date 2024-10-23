package com.lmax.solana4j.client.api;

import java.util.Map;

/**
 * Represents detailed parsed data about an account on the Solana blockchain.
 * This interface provides access to the account's key properties and additional
 * parsed data, such as account type and other specific information.
 */
public interface AccountInfoParsedData
{
    /**
     * Returns the number of lamports in the account.
     * Lamports are the smallest unit of SOL, the native token of Solana.
     *
     * @return the account balance in lamports
     */
    long getLamports();

    /**
     * Returns the public key of the account's owner.
     * The owner is responsible for executing programs on behalf of this account.
     *
     * @return the base58-encoded string representing the public key of the account's owner
     */
    String getOwner();

    /**
     * Returns the parsed data stored in the account as a map of key-value pairs.
     * The content of the map is dependent on the program that owns the account and may
     * vary based on the account type.
     *
     * @return a map representing the account's parsed data
     */
    Map<String, Object> getData();

    /**
     * Returns the type of the account.
     * This represents the specific type of the account, often related to the program
     * that manages or owns the account.
     *
     * @return the string representing the account type
     */
    String getType();

    /**
     * Returns additional information about the account as a map of key-value pairs.
     * This map can contain specific fields that provide more detailed information
     * based on the account's type and usage.
     *
     * @return a map representing additional information about the account
     */
    Map<String, Object> getInfo();

    /**
     * Returns the number of decimals, if applicable, for the account's data.
     * This is commonly used for token accounts to define the precision of token amounts.
     *
     * @return the number of decimals, or null if not applicable
     */
    Integer getDecimals();

    /**
     * Indicates whether the account is marked as executable.
     * If true, the account contains a program that can be executed on the Solana blockchain.
     *
     * @return true if the account is executable, false otherwise
     */
    boolean isExecutable();

    /**
     * Returns the rent epoch for the account.
     * The rent epoch is the epoch number at which the account will no longer be rent-exempt
     * if its balance falls below the minimum required.
     *
     * @return the rent epoch number
     */
    String getRentEpoch();
}