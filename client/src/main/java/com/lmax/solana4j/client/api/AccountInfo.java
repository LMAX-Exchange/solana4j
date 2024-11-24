package com.lmax.solana4j.client.api;

import java.util.List;
import java.util.Map;

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

    AccountInfoData getData();

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

    /**
    /**
     * Represents account information data in a Solana transaction.
     * This interface provides access to both encoded and parsed account information,
     * allowing clients to retrieve details in a flexible format.
     */
    interface AccountInfoData
    {
        /**
         * Returns the encoded account information.
         * This data is represented as a list of base64-encoded strings, providing a compact form
         * of the account information for efficient storage and transmission.
         *
         * @return a list of base64-encoded strings representing the account information
         */
        List<String> getAccountInfoEncoded();

        /**
         * Returns the parsed account information.
         * This data provides a structured representation of account details, including the associated program,
         * allocated space, and any additional parsed data fields.
         *
         * @return an {@link AccountInfoParsedData} object representing the parsed account information
         */
        AccountInfoParsedData getAccountInfoParsed();

        /**
         * Represents parsed account information data within an account.
         * This nested interface provides access to the program associated with the account,
         * the allocated space for the account, and a dynamically structured parsed data map.
         */
        interface AccountInfoParsedData
        {
            /**
             * Returns the name or identifier of the program associated with the account.
             * This program defines the logic for the account's operations within the Solana blockchain.
             *
             * @return a string representing the program's name or identifier
             */
            String getProgram();

            /**
             * Returns the allocated space for the account in bytes.
             * The space represents the amount of storage allocated to this account within the Solana blockchain.
             *
             * @return the size of the account in bytes
             */
            int getSpace();

            /**
             * Returns a map representation of parsed data fields for the account.
             * This parsed data is represented as a {@link Map} with string keys and object values,
             * allowing flexible access to dynamically structured data fields within the account.
             *
             * @return a {@link Map} where keys are field names and values are the corresponding parsed data,
             *         potentially containing nested structures as {@code Map<String, Object>}
             */
            Map<String, Object> getParsedData();
        }
    }
}