package com.lmax.solana4j.client.api;

public interface SolanaClientResponse<T>
{
    T getResponse();

    SolanaClientError getError();
}