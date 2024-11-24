package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.SolanaClientResponse;

class SolanaJsonRpcClientError implements SolanaClientResponse.SolanaClientError
{
    private final long errorCode;
    private final String errorMessage;

    SolanaJsonRpcClientError(final long errorCode, final String errorMessage)
    {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public long getErrorCode()
    {
        return errorCode;
    }

    @Override
    public String getErrorMessage()
    {
        return errorMessage;
    }
}
