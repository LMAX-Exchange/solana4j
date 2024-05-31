package com.lmax.solana4j.testclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.testclient.api.InnerInstruction;
import com.lmax.solana4j.testclient.api.Reward;
import com.lmax.solana4j.testclient.api.TokenBalance;
import com.lmax.solana4j.testclient.api.TransactionMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class MetaDTO implements TransactionMetadata
{
    public final Object err;
    public final long fee;
    public final List<InnerInstructionDTO> innerInstructions;
    public final List<String> logMessages;
    public final List<Long> postBalances;
    public final List<TokenBalanceDTO> postTokenBalances;
    public final List<Long> preBalances;
    public final List<TokenBalanceDTO> preTokenBalances;
    public final List<RewardDTO> rewards;

    @JsonCreator
    MetaDTO(
            final @JsonProperty("err") Object err,
            final @JsonProperty("fee") long fee,
            final @JsonProperty("innerInstructions") List<InnerInstructionDTO> innerInstructions,
            final @JsonProperty("logMessages") List<String> logMessages,
            final @JsonProperty("postBalances") List<Long> postBalances,
            final @JsonProperty("postTokenBalances") List<TokenBalanceDTO> postTokenBalances,
            final @JsonProperty("preBalances") List<Long> preBalances,
            final @JsonProperty("preTokenBalances") List<TokenBalanceDTO> preTokenBalances,
            final @JsonProperty("rewards") List<RewardDTO> rewards)
    {
        this.err = err;
        this.fee = fee;
        this.innerInstructions = innerInstructions;
        this.logMessages = logMessages;
        this.postBalances = postBalances;
        this.postTokenBalances = postTokenBalances;
        this.preBalances = preBalances;
        this.preTokenBalances = preTokenBalances;
        this.rewards = rewards;
    }

    @Override
    public Object getErr()
    {
        return err;
    }

    @Override
    public long getFee()
    {
        return fee;
    }

    @Override
    public List<InnerInstruction> getInnerInstructions()
    {
        return innerInstructions != null ?
               innerInstructions.stream().map(x -> (InnerInstruction) x).collect(Collectors.toList()) :
               Collections.emptyList();
    }

    @Override
    public List<String> getLogMessages()
    {
        return logMessages;
    }

    @Override
    public List<Long> getPostBalances()
    {
        return postBalances;
    }

    @Override
    public List<TokenBalance> getPostTokenBalances()
    {
        return new ArrayList<>(postTokenBalances);
    }

    @Override
    public List<Long> getPreBalances()
    {
        return preBalances;
    }

    @Override
    public List<TokenBalance> getPreTokenBalances()
    {
        return preTokenBalances.stream().map(x -> (TokenBalance) x).collect(Collectors.toList());
    }

    @Override
    public List<Reward> getRewards()
    {
        return rewards.stream().map(x -> (Reward) x).collect(Collectors.toList());
    }

    @Override
    public String toString()
    {
        return "Meta{" +
               "err=" + err +
               ", fee=" + fee +
               ", innerInstructions=" + innerInstructions +
               ", logMessages=" + logMessages +
               ", postBalances=" + postBalances +
               ", postTokenBalances=" + postTokenBalances +
               ", preBalances=" + preBalances +
               ", preTokenBalances=" + preTokenBalances +
               ", rewards=" + rewards +
               '}';
    }
}
