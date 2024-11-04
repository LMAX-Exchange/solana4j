package com.lmax.solana4j.client.api;

import java.util.List;
import java.util.Map;

/**
/**
 * Represents account information data in a Solana transaction.
 * This interface provides access to both encoded and parsed account information,
 * allowing clients to retrieve details in a flexible format.
 */
public interface AccountInfoData
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
