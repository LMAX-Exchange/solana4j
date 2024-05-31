package com.lmax.solana4j.client.api;

public interface SolanaRpcResponse<T>
{
    Context getContext();

    T getValue();
}
