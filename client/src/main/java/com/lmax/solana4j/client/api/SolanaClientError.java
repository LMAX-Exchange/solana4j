package com.lmax.solana4j.client.api;

public interface SolanaClientError
{
    ErrorCode getErrorCode();

    String getErrorMessage();

    boolean isRecoverable();
}
