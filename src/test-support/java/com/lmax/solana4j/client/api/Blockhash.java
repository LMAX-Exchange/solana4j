package com.lmax.solana4j.client.api;

public interface Blockhash
{
    String getBlockhashBase58();

    byte[] getBytes();

    int getLastValidBlockHeight();
}
