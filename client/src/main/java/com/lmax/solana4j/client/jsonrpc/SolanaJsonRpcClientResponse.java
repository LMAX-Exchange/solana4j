package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.SolanaClientError;
import com.lmax.solana4j.client.api.SolanaClientResponse;

final class SolanaJsonRpcClientResponse<T> implements SolanaClientResponse<T>
{
    private final T response;
    private final SolanaClientError error;

    private SolanaJsonRpcClientResponse(final T response, final SolanaClientError error)
    {
        this.response = response;
        this.error = error;
    }

    static <T> SolanaClientResponse<T> createSuccessResponse(final T response)
    {
        return new SolanaJsonRpcClientResponse<>(response, null);
    }

    static <T> SolanaClientResponse<T> creatErrorResponse(final SolanaClientError error)
    {
        return new SolanaJsonRpcClientResponse<>(null, error);
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
        return error == null;
    }
}
