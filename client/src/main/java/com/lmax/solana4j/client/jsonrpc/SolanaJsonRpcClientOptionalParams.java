package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link SolanaClientOptionalParams} that provides support for adding
 * and retrieving optional parameters to be used in requests to a solana node.
 */
public final class SolanaJsonRpcClientOptionalParams implements SolanaClientOptionalParams
{
    private final Map<String, Object> optionalParams = new HashMap<>();

    @Override
    public void addParam(final String key, final Object value)
    {
        optionalParams.put(key, value);
    }

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
     *   <li><b>maxSupportedTransactionVersion</b>: 1 - Indicates the maximum supported version of the transaction.
     *       Setting it to 1 specifies support for legacy, V0, and V1 versioned transactions.</li>
     * </ul>
     *
     * <p>
     * Note that this default applies to all queries made by the client, so nodes that do not
     * support V1 transactions may respond with an error for this parameter value.
     * </p>
     *
     * @return a {@link Map} containing default parameters to be used in JSON-RPC requests
     */
    public static Map<String, Object> defaultOptionalParams()
    {
        final SolanaClientOptionalParams params = new SolanaJsonRpcClientOptionalParams();

        params.addParam("encoding", "base64");
        params.addParam("commitment", Commitment.FINALIZED.name().toLowerCase());
        params.addParam("maxSupportedTransactionVersion", 1);

        return params.getParams();
    }
}