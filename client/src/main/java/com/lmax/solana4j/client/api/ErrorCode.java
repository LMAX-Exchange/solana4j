package com.lmax.solana4j.client.api;

/**
 * Enum representing various error codes that can be encountered
 * during interactions with the Solana JSON-RPC API.
 */
public enum ErrorCode
{
    /**
     * Error encountered while processing JSON data.
     * This typically indicates a problem with serializing or deserializing
     * JSON when communicating with the Solana API.
     */
    JSON_PROCESSING_ERROR,

    /**
     * Error encountered during communication with the JSON-RPC endpoint.
     * This usually refers to network issues or failures when attempting to
     * send or receive data from the Solana JSON-RPC node.
     */
    JSON_RPC_COMMUNICATIONS_FAILURE,

    /**
     * Error indicating that the JSON-RPC call received an unexpected status code.
     * This could happen if the HTTP status code from the Solana node is not
     * the expected one (e.g., 4xx or 5xx errors).
     */
    JSON_RPC_UNEXPECTED_STATUS_CODE,

    /**
     * Error reported by the Solana JSON-RPC node itself.
     * This occurs when the node returns an error in response to a JSON-RPC request.
     */
    JSON_RPC_REPORTED_ERROR;
}