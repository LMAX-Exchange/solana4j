package com.lmax.solana4j.testclient.api;

public interface Blockhash
{
    String getBlockhashBase58();

    byte[] getBytes();

    int getLastValidBlockHeight();
}
