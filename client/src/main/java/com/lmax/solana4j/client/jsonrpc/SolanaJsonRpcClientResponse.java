package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.SolanaClientError;
import com.lmax.solana4j.client.api.SolanaClientResponse;

class SolanaJsonRpcClientResponse<T> implements SolanaClientResponse<T>
{
    private final T response;
    private final SolanaClientError error;

    SolanaJsonRpcClientResponse(final T response)
    {
        this.response = response;
        this.error = null;
    }

    SolanaJsonRpcClientResponse(final SolanaClientError error)
    {
        this.response = null;
        this.error = error;
    }

    @Override
    public T getResponse()
    {
        return response;
    }

    @Override
    public SolanaClientError getError()
    {
        return error;
    }

    @Override
    public boolean isSuccess()
    {
        return error != null;
    }
}
