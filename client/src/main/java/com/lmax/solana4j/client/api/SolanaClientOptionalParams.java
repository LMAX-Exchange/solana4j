package com.lmax.solana4j.client.api;

import java.util.Map;

/**
 * Interface representing optional parameters for customizing Solana JSON-RPC requests.
 * Implementations of this interface allow adding parameters that modify request behavior.
 */
public interface SolanaClientOptionalParams
{
    /**
     * Adds an optional parameter to the request.
     * This can be used to pass additional customization options supported by the Solana JSON-RPC API.
     *
     * @param key   the name of the parameter to add (e.g., "commitment", "encoding")
     * @param value the value of the parameter, which can be any supported data type
     */
    void addParam(String key, Object value);

    /**
     * Retrieves all the optional parameters as a map.
     * This map can be used to access the parameters set for a given request.
     *
     * @return a map containing all the key-value pairs for optional parameters
     */
    Map<String, Object> getParams();
}