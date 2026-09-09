package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.BlockResponse;
import com.lmax.solana4j.client.api.TransactionResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class BlockResponseDTO implements BlockResponse
{
    private final long blockHeight;
    private final Long blockTime;
    private final String blockhashBase58;
    private final long parentSlot;
    private final String previousBlockhashBase58;
    private final List<RewardsDTO> rewards;
    private final Long numRewardPartitions;
    private final List<TransactionResponseDTO> transactions;


    @JsonCreator
    BlockResponseDTO(
            final @JsonProperty("blockHeight") long blockHeight,
            final @JsonProperty("blockTime") Long blockTime,
            final @JsonProperty("blockhash") String blockhashBase58,
            final @JsonProperty("parentSlot") long parentSlot,
            final @JsonProperty("previousBlockhash") String previousBlockhashBase58,
            final @JsonProperty("rewards") List<RewardsDTO> rewards,
            final @JsonProperty("numRewardPartitions") Long numRewardPartitions,
            final @JsonProperty("transactions") List<TransactionResponseDTO> transactions)
    {
        this.blockHeight = blockHeight;
        this.blockTime = blockTime;
        this.blockhashBase58 = blockhashBase58;
        this.parentSlot = parentSlot;
        this.previousBlockhashBase58 = previousBlockhashBase58;
        this.rewards = rewards;
        this.numRewardPartitions = numRewardPartitions;
        this.transactions = transactions;
    }

    @Override
    public long getBlockHeight()
    {
        return blockHeight;
    }

    @Override
    public Long getBlockTime()
    {
        return blockTime;
    }

    @Override
    public String getBlockhash()
    {
        return blockhashBase58;
    }

    @Override
    public long getParentSlot()
    {
        return parentSlot;
    }

    @Override
    public String getPreviousBlockhash()
    {
        return previousBlockhashBase58;
    }

    @Override
    public List<BlockResponse.Rewards> getRewards()
    {
        return rewards != null ?
                rewards.stream().map(x -> (BlockResponse.Rewards) x).collect(Collectors.toList()) :
                Collections.emptyList();
    }

    @Override
    public List<TransactionResponse> getTransactions()
    {
        return transactions != null ?
                transactions.stream().map(x -> (TransactionResponse) x).collect(Collectors.toList()) :
                Collections.emptyList();
    }

    @Override
    public Long getNumRewardPartitions()
    {
        return numRewardPartitions;
    }

    @Override
    public String toString()
    {
        return "BlockResponseDTO{" +
                "blockHeight=" + blockHeight +
                ", blockTime=" + blockTime +
                ", blockhashBase58='" + blockhashBase58 + '\'' +
                ", parentSlot=" + parentSlot +
                ", previousBlockhashBase58='" + previousBlockhashBase58 + '\'' +
                ", rewards=" + rewards +
                ", numRewardPartitions=" + numRewardPartitions +
                ", transactions=" + transactions +
                '}';
    }

    static final class RewardsDTO implements BlockResponse.Rewards
    {
        Long commission;
        long lamports;
        long postBalance;
        String pubkeyBase58;
        String rewardType;

        @JsonCreator
        RewardsDTO(final @JsonProperty("commission") Long commission,
                   final @JsonProperty("lamports") long lamports,
                   final @JsonProperty("postBalance") long postBalance,
                   final @JsonProperty("pubkey") String pubkeyBase58,
                   final @JsonProperty("rewardType") String rewardType)
        {
            this.commission = commission;
            this.lamports = lamports;
            this.postBalance = postBalance;
            this.pubkeyBase58 = pubkeyBase58;
            this.rewardType = rewardType;
        }

        @Override
        public Long getCommission()
        {
            return commission;
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
        public String getPubkeyBase58()
        {
            return pubkeyBase58;
        }

        @Override
        public String getRewardType()
        {
            return rewardType;
        }

        @Override
        public String toString()
        {
            return "RewardsDTO{" +
                    "commission=" + commission +
                    ", lamports=" + lamports +
                    ", postBalance=" + postBalance +
                    ", pubkeyBase58='" + pubkeyBase58 + '\'' +
                    ", rewardType='" + rewardType + '\'' +
                    '}';
        }
    }
}
