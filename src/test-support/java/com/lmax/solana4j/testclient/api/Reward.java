package com.lmax.solana4j.testclient.api;

public interface Reward
{
    String getPubkey();

    long getLamports();

    long getPostBalance();

    String getRewardType();

    int getCommission();
}
