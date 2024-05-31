package com.lmax.solana4j.client.api;

public interface TokenAccount
{
    String getPublicKey();

    AccountInfo getAccountInfo();
}
