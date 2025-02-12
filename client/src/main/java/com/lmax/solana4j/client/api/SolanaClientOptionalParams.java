package com.lmax.solana4j.client.api;

import java.util.Map;

/**
 * Represents optional parameter operations for customizing queries to a solana node.
 */
public interface SolanaClientOptionalParams
{
    /**
     * Adds an optional parameter to the request.
     *
     * @param key   the name of the parameter to add (e.g., "commitment", "encoding")
     * @param value the value of the parameter, which can be any supported data type
     */
    void addParam(String key, Object value);

    /**
     * Returns all the optional parameters as a map.
     *
     * @return a map containing all the key-value pairs for optional parameters
     */
    Map<String, Object> getParams();
}