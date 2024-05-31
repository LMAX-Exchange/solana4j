package com.lmax.solana4j.client.api;

public interface TokenAmount
{
    String getAmount();

    long getDecimals();

    float getUiAmount();

    String getUiAmountString();
}
