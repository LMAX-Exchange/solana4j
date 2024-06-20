package com.lmax.solana4j.solanaclient.api;

public interface SolanaRpcResponse<T>
{
    Context getContext();

    T getValue();
}
