package com.lmax.solana4j.client.api;

/**
 * Represents a generic response from a solana rpc node.
 *
 * @param <T> the type of the value returned by the rpc response
 */
public interface SolanaRpcResponse<T>
{
    /**
     * Returns the context associated with the rpc response.
     * The context contains metadata, such as the slot in which the response was generated.
     *
     * @return the {@link Context} object representing the context of the response
     */
    Context getContext();

    /**
     * Returns the value of the rpc response.
     * The type of the value depends on the specific rpc request and can vary (e.g., an account balance, transaction details, etc.).
     *
     * @return the value of the response, which is of type {@code T}
     */
    T getValue();

    /**
     * Represents the context of a query or transaction from the node.
     * The context provides access to essential metadata such as the current slot number and the api version in use.
     */
    interface Context
    {
        /**
         * Returns the slot number associated with this context.
         *
         * @return the slot number for the current context, representing the point in time or block on the blockchain.
         */
        long getSlot();

        /**
         * Returns the API version associated with this context.
         *
         * @return the API version string for the current context.
         */
        String apiVersion();
    }
}