package com.lmax.solana4j.testclient.api;

public interface TokenAmount
{
    String getAmount();

    long getDecimals();

    float getUiAmount();

    String getUiAmountString();
}
