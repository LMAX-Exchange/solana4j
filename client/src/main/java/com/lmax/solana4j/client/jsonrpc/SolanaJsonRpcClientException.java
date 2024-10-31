package com.lmax.solana4j.client.jsonrpc;


public class SolanaJsonRpcClientException extends Exception
{
    private final String message;
    private final Throwable throwable;
    private final boolean recoverable;

    SolanaJsonRpcClientException(final String message)
    {
        this.message = message;
        this.throwable = null;
        this.recoverable = false;
    }

    SolanaJsonRpcClientException(final String message, final Throwable throwable)
    {
        this.message = message;
        this.throwable = throwable;
        this.recoverable = false;
    }

    SolanaJsonRpcClientException(final String message, final Throwable throwable, final boolean recoverable)
    {
        this.message = message;
        this.throwable = throwable;
        this.recoverable = recoverable;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    @Override
    public Throwable getCause()
    {
        return throwable;
    }

    public boolean isRecoverable()
    {
        return recoverable;
    }

    @Override
    public String toString()
    {
        return "SolanaJsonRpcError{" +
                "message='" + message + '\'' +
                ", throwable=" + throwable +
                ", recoverable=" + recoverable +
                '}';
    }
}
