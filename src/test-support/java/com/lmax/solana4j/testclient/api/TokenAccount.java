package com.lmax.solana4j.testclient.api;

public interface TokenAccount
{
    String getPublicKey();

    AccountInfo getAccountInfo();
}
