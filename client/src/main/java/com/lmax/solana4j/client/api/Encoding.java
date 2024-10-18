package com.lmax.solana4j.client.api;

/**
 * Represents the encoding format used in Solana data serialization or deserialization.
 * This enum provides different encoding options for retrieving or parsing data
 * from the Solana blockchain.
 */
public enum Encoding
{
    /**
     * Base64 encoding format.
     * Data is encoded as a base64-encoded string, which is commonly used for binary data.
     */
    BASE_64,

    /**
     * JSON parsed format.
     * Data is encoded in JSON format and parsed into structured, readable fields.
     */
    JSON_PARSED
}