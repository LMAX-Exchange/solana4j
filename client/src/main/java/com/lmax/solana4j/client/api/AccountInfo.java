package com.lmax.solana4j.client.api;

import java.util.List;

/**
 * Represents the information of an account on the Solana blockchain.
 * This interface provides access to key properties of a Solana account, such as
 * its balance, owner, data, and status.
 */
public interface AccountInfo
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
     * Returns the data stored in the account as a list of base64-encoded strings.
     * The account's data may be program-specific and is serialized according to the
     * owning program's specifications.
     *
     * @return a list of base64-encoded strings representing the account's data
     */
    List<String> getData();

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
    long getRentEpoch();

    /**
     * Returns the amount of space, in bytes, allocated to the account.
     * The space is used to store data for the account, and the size is determined by the
     * program that owns the account. The amount of space is a key factor in determining
     * the rent required to keep the account alive, as larger accounts typically require
     * more rent.
     *
     * @return the number of bytes allocated for the account's data
     */
    long getSpace();
}