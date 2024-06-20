package com.lmax.solana4j.solanaclient.api;

public interface TokenAmount
{
    String getAmount();

    long getDecimals();

    float getUiAmount();

    String getUiAmountString();
}
