package com.lmax.solana4j.solanaclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.solanaclient.api.Reward;

final class RewardDTO implements Reward
{
    private final String pubkey;
    private final long lamports;
    private final long postBalance;
    private final String rewardType;
    private final int commission;

    @JsonCreator
    RewardDTO(
            final @JsonProperty("pubkey") String pubkey,
            final @JsonProperty("lamports") long lamports,
            final @JsonProperty("postBalance") long postBalance,
            final @JsonProperty("rewardType") String rewardType,
            final @JsonProperty("commission") int commission)
    {
        this.pubkey = pubkey;
        this.lamports = lamports;
        this.postBalance = postBalance;
        this.rewardType = rewardType;
        this.commission = commission;
    }

    @Override
    public String getPubkey()
    {
        return pubkey;
    }

    @Override
    public long getLamports()
    {
        return lamports;
    }

    @Override
    public long getPostBalance()
    {
        return postBalance;
    }

    @Override
    public String getRewardType()
    {
        return rewardType;
    }

    @Override
    public int getCommission()
    {
        return commission;
    }

    @Override
    public String toString()
    {
        return "Reward{" +
               "pubkey='" + pubkey + '\'' +
               ", lamports=" + lamports +
               ", postBalance=" + postBalance +
               ", rewardType='" + rewardType + '\'' +
               ", commission=" + commission +
               '}';
    }
}
