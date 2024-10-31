package com.lmax.solana4j.client.api;

public interface SolanaClientError
{
    long getErrorCode();

    String getErrorMessage();
}
