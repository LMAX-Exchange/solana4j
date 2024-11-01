package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class SolanaJsonRpcClientOptionalParams implements SolanaClientOptionalParams
{
    private final Map<String, Object> optionalParams = new HashMap<>();

    @Override
    public void addParam(final String key, final Object value)
    {
        if (key.equals("encoding") && value.equals("jsonParsed"))
        {
            throw new UnsupportedOperationException("If you want 'jsonParsed', you must use" +
                    "the dedicated endpoint. It can not be provided through SolanaClientOptionalParams.");
        }
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
        params.addParam("commitment", Commitment.FINALIZED.name().toLowerCase(Locale.UK));
        params.addParam("maxSupportedTransactionVersion", 0);

        return params.getParams();
    }
}