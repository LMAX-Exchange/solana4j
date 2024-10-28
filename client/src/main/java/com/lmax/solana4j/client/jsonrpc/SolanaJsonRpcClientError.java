package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.ErrorCode;
import com.lmax.solana4j.client.api.SolanaClientError;

class SolanaJsonRpcClientError implements SolanaClientError
{
    private final ErrorCode errorCode;
    private final String errorMessage;

    SolanaJsonRpcClientError(final ErrorCode errorCode, final String errorMessage)
    {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public ErrorCode getErrorCode()
    {
        return errorCode;
    }

    @Override
    public String getErrorMessage()
    {
        return errorMessage;
    }
}
