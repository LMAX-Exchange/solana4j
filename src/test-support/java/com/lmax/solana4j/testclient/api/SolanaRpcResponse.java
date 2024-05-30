package com.lmax.solana4j.testclient.api;

public interface SolanaRpcResponse<T>
{
    Context getContext();

    T getValue();
}
