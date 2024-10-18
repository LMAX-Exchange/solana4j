package com.lmax.solana4j.client.api;

/**
 * Represents a generic response from the Solana RPC (Remote Procedure Call) API.
 * This interface provides access to the context of the response and the value returned by the RPC call.
 *
 * @param <T> the type of the value returned by the RPC response
 */
public interface SolanaRpcResponse<T>
{
    /**
     * Returns the context associated with the RPC response.
     * The context contains metadata, such as the slot in which the response was generated.
     *
     * @return the {@link Context} object representing the context of the response
     */
    Context getContext();

    /**
     * Returns the value of the RPC response.
     * The type of the value depends on the specific RPC request and can vary (e.g., an account balance, transaction details, etc.).
     *
     * @return the value of the response, which is of type {@code T}
     */
    T getValue();
}