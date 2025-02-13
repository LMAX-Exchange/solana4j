package com.lmax.solana4j.client.api;

import java.util.List;
import java.util.Map;

/**
 * Represents an account on the blockchain.
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
     * Returns the data associated with the account.
     * This data includes both encoded and parsed representations of the account's information.
     *
     * @return an instance of {@link AccountInfoData} containing the account's data
     */
    AccountInfoData getData();

    /**
     * Indicates whether the account is marked as executable.
     * If true, the account contains a program that can be executed on the blockchain.
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
     * program that owns the account.
     *
     * @return the number of bytes allocated for the account's data
     */
    long getSpace();

    /**
     * Represents the data stored by the account, in both encoded and parsed representations.
     */
    interface AccountInfoData
    {
        /**
         * Returns the encoded account information.
         * The first element of the list contains the data and the second the encoding scheme used to encode the data.
         *
         * @return a list containing the encoded account information data and the encoding scheme
         */
        List<String> getAccountInfoEncoded();

        /**
         * Returns the parsed account information.
         *
         * @return an {@link AccountInfoParsedData} object representing the parsed account information
         */
        AccountInfoParsedData getAccountInfoParsed();

        /**
         * Represents parsed account information data within an account.
         */
        interface AccountInfoParsedData
        {
            /**
             * Returns the name of the program associated with the account.
             * This program defines the logic for the account's operations on the blockchain.
             *
             * @return a string representing the program's name or identifier
             */
            String getProgram();

            /**
             * Returns the allocated space for the account in bytes.
             * The space represents the amount of storage allocated to this account on the blockchain.
             *
             * @return the size of the account in bytes
             */
            int getSpace();

            /**
             * Returns a map representation of parsed data fields for the account.
             * This parsed data is represented as a {@link Map} with string keys and object values,
             * allowing flexible access to dynamically structured data fields within the account.
             *
             * @return a {@link Map} where keys are field names and values are the corresponding parsed data
             */
            Map<String, Object> getParsedData();
        }
    }
}