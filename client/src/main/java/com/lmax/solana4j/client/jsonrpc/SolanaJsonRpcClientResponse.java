package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.ErrorCode;
import com.lmax.solana4j.client.api.SolanaClientError;
import com.lmax.solana4j.client.api.SolanaClientResponse;

class SolanaJsonRpcClientResponse<T> implements SolanaClientResponse<T>
{
    private final T response;
    private final SolanaRecoverableJsonRpcClientError error;

    SolanaJsonRpcClientResponse(final T response)
    {
        this.response = response;
        this.error = null;
    }

    SolanaJsonRpcClientResponse(final ErrorCode errorCode, final String errorMessage)
    {
        this.response = null;
        this.error = new SolanaRecoverableJsonRpcClientError(errorCode, errorMessage);
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
}
