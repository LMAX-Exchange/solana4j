package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link SolanaClientOptionalParams} that provides support for adding
 * and retrieving optional parameters to be used in Solana JSON-RPC requests.
 */
public class SolanaJsonRpcClientOptionalParams implements SolanaClientOptionalParams
{
    private final Map<String, Object> optionalParams = new HashMap<>();

    /**
     * Adds a parameter to the set of optional parameters.
     *
     * @param key   the name of the parameter
     * @param value the value of the parameter
     */
    @Override
    public void addParam(final String key, final Object value)
    {
        optionalParams.put(key, value);
    }

    /**
     * Retrieves the current set of optional parameters.
     *
     * @return a {@link Map} containing the optional parameters
     */
    @Override
    public Map<String, Object> getParams()
    {
        return optionalParams;
    }

    /**
     * Creates and returns a default instance of {@link SolanaClientOptionalParams} as a {@link Map}.
     * This instance contains a set of commonly used default parameters for JSON-RPC requests.
     * By default, it includes the following parameters:
     * <ul>
     *   <li><b>encoding</b>: "base64" - Specifies that the data should be encoded in base64 format.</li>
     *   <li><b>commitment</b>: "finalized" - Sets the commitment level to "finalized" for queries, ensuring the request is processed with finalized data.</li>
     *   <li><b>maxSupportedTransactionVersion</b>: 0 - Indicates the maximum supported version of the transaction.
     *       Setting it to 0 specifies the use of versioned transactions.</li>
     * </ul>
     *
     * @return a {@link Map} containing default parameters to be used in JSON-RPC requests
     */
    public static Map<String, Object> defaultOptionalParams()
    {
        final SolanaClientOptionalParams params = new SolanaJsonRpcClientOptionalParams();

        params.addParam("encoding", "base64");
        params.addParam("commitment", Commitment.FINALIZED.name().toLowerCase());
        params.addParam("maxSupportedTransactionVersion", 0);

        return params.getParams();
    }
}