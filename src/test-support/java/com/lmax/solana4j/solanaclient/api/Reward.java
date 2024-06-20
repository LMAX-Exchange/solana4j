package com.lmax.solana4j.solanaclient.api;

public interface Reward
{
    String getPubkey();

    long getLamports();

    long getPostBalance();

    String getRewardType();

    int getCommission();
}
