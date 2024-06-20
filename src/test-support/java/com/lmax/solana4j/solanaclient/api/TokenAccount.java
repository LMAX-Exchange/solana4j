package com.lmax.solana4j.solanaclient.api;

public interface TokenAccount
{
    String getPublicKey();

    AccountInfo getAccountInfo();
}
