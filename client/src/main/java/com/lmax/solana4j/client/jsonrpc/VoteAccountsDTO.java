package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.VoteAccount;
import com.lmax.solana4j.client.api.VoteAccounts;

import java.util.List;

/**
 * Data Transfer Object for the response from the getVoteAccounts RPC call.
 */
public class VoteAccountsDTO implements VoteAccounts
{
    private final List<VoteAccount> current;
    private final List<VoteAccount> delinquent;

    @JsonCreator
    VoteAccountsDTO(
            final @JsonProperty("current") List<VoteAccountDTO> current,
            final @JsonProperty("delinquent") List<VoteAccountDTO> delinquent)
    {
        this.current = List.copyOf(current);
        this.delinquent = List.copyOf(delinquent);
    }

    @Override
    @JsonProperty("current")
    public List<VoteAccount> getCurrent()
    {
        return current;
    }

    @Override
    @JsonProperty("delinquent")
    public List<VoteAccount> getDelinquent()
    {
        return delinquent;
    }

    @Override
    public String toString()
    {
        return "VoteAccountsDTO{" +
               "current=" + current +
               ", delinquent=" + delinquent +
               '}';
    }
}