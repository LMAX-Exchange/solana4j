package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.VoteAccount;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for vote account information from the getVoteAccounts RPC call.
 */
public class VoteAccountDTO implements VoteAccount
{
    private final String nodePubkey;
    private final String votePubkey;
    private final long activatedStake;
    private final boolean epochVoteAccount;
    private final int commission;
    private final long lastVote;
    private final long rootSlot;
    private final List<List<Long>> epochCredits;

    @JsonCreator
    VoteAccountDTO(
            final @JsonProperty("nodePubkey") String nodePubkey,
            final @JsonProperty("votePubkey") String votePubkey,
            final @JsonProperty("activatedStake") long activatedStake,
            final @JsonProperty("epochVoteAccount") boolean epochVoteAccount,
            final @JsonProperty("commission") int commission,
            final @JsonProperty("lastVote") long lastVote,
            final @JsonProperty("rootSlot") long rootSlot,
            final @JsonProperty("epochCredits") List<List<Long>> epochCredits)
    {
        this.nodePubkey = nodePubkey;
        this.votePubkey = votePubkey;
        this.activatedStake = activatedStake;
        this.epochVoteAccount = epochVoteAccount;
        this.commission = commission;
        this.lastVote = lastVote;
        this.rootSlot = rootSlot;
        this.epochCredits = epochCredits;
    }

    @Override
    @JsonProperty("nodePubkey")
    public String getNodePubkey()
    {
        return nodePubkey;
    }

    @Override
    @JsonProperty("votePubkey")
    public String getVotePubkey()
    {
        return votePubkey;
    }

    @Override
    @JsonProperty("activatedStake")
    public long getActivatedStake()
    {
        return activatedStake;
    }

    @Override
    @JsonProperty("epochVoteAccount")
    public boolean isEpochVoteAccount()
    {
        return epochVoteAccount;
    }

    @Override
    @JsonProperty("commission")
    public int getCommission()
    {
        return commission;
    }

    @Override
    @JsonProperty("lastVote")
    public long getLastVote()
    {
        return lastVote;
    }

    @Override
    @JsonProperty("rootSlot")
    public long getRootSlot()
    {
        return rootSlot;
    }

    @Override
    @JsonProperty("epochCredits")
    public List<List<Long>> getEpochCredits()
    {
        return epochCredits;
    }

    @Override
    public String toString()
    {
        return "VoteAccountDTO{" +
               "nodePubkey='" + nodePubkey + '\'' +
               ", votePubkey='" + votePubkey + '\'' +
               ", activatedStake=" + activatedStake +
               ", epochVoteAccount=" + epochVoteAccount +
               ", commission=" + commission +
               ", lastVote=" + lastVote +
               ", rootSlot=" + rootSlot +
               ", epochCredits=" + epochCredits +
               '}';
    }
}