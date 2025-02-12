package com.lmax.solana4j.client.jsonrpc;

/**
 * Exception thrown when an error occurs during communication with a solana node.
 */
public class SolanaJsonRpcClientException extends Exception
{
    private final String message;
    private final Throwable throwable;
    private final boolean recoverable;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception.
     */
    SolanaJsonRpcClientException(final String message)
    {
        this.message = message;
        this.throwable = null;
        this.recoverable = false;
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message   the detail message explaining the reason for the exception.
     * @param throwable the cause of the exception, which can be retrieved later using {@link #getCause()}.
     */
    SolanaJsonRpcClientException(final String message, final Throwable throwable)
    {
        this.message = message;
        this.throwable = throwable;
        this.recoverable = false;
    }

    /**
     * Constructs a new exception with the specified detail message, cause, and recoverable flag.
     *
     * @param message     the detail message explaining the reason for the exception.
     * @param throwable   the cause of the exception, which can be retrieved later using {@link #getCause()}.
     * @param recoverable a boolean flag indicating whether the error is recoverable.
     */
    SolanaJsonRpcClientException(final String message, final Throwable throwable, final boolean recoverable)
    {
        this.message = message;
        this.throwable = throwable;
        this.recoverable = recoverable;
    }

    /**
     * Returns the detail message string of this exception.
     *
     * @return the detail message string.
     */
    @Override
    public String getMessage()
    {
        return message;
    }

    /**
     * Returns the cause of this exception or {@code null} if the cause is nonexistent or unknown.
     *
     * @return the cause of this exception
     */
    @Override
    public Throwable getCause()
    {
        return throwable;
    }

    /**
     * Indicates whether the error is recoverable.
     *
     * @return {@code true} if the error is recoverable; {@code false} otherwise
     */
    public boolean isRecoverable()
    {
        return recoverable;
    }

    @Override
    public String toString()
    {
        return "SolanaJsonRpcClientException{" +
                "message='" + message + '\'' +
                ", throwable=" + throwable +
                ", recoverable=" + recoverable +
                '}';
    }
}