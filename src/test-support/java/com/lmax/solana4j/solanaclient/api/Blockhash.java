package com.lmax.solana4j.solanaclient.api;

public interface Blockhash
{
    String getBlockhashBase58();

    byte[] getBytes();

    int getLastValidBlockHeight();
}
