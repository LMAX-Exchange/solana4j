package com.lmax.solana4j.solanaclient.api;

public interface TokenBalance
{
    int getAccountIndex();

    String getMint();

    String getOwner();

    String getProgramId();

    TokenAmount getUiTokenAmount();
}
