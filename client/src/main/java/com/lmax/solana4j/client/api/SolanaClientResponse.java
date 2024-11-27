package com.lmax.solana4j.client.api;

/**
 * Represents a response from the Solana client, encapsulating either a successful result or an error.
 *
 * @param <T> the type of the successful response payload
 */
public interface SolanaClientResponse<T>
{
    /**
     * Retrieves the response payload in case of a successful operation.
     *
     * @return the response payload, or {@code null} if the operation was not successful
     */
    T getResponse();

    /**
     * Retrieves the error details in case of a failed operation.
     *
     * @return an instance of {@link SolanaClientError} containing error information, or {@code null} if the operation was successful
     */
    SolanaClientError getError();

    /**
     * Indicates whether the operation was successful.
     *
     * @return {@code true} if the operation was successful; {@code false} otherwise
     */
    boolean isSuccess();

    /**
     * Represents an error encountered during a Solana client operation.
     */
    interface SolanaClientError
    {
        /**
         * Retrieves the error code associated with the failure.
         *
         * @return the error code
         */
        long getErrorCode();

        /**
         * Retrieves the error message providing details about the failure.
         *
         * @return the error message
         */
        String getErrorMessage();
    }
}