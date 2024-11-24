package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.SolanaRpcResponse;

final class ContextDTO implements SolanaRpcResponse.Context
{
    private final long slot;
    private final String apiVersion;

    @JsonCreator
    ContextDTO(final @JsonProperty("slot") long slot,
               final @JsonProperty("apiVersion") String apiVersion)
    {
        this.slot = slot;
        this.apiVersion = apiVersion;
    }

    @Override
    public long getSlot()
    {
        return slot;
    }

    @Override
    public String apiVersion()
    {
        return apiVersion;
    }

    @Override
    public String toString()
    {
        return "ContextDTO{" +
                "slot=" + slot +
                ", apiVersion='" + apiVersion + '\'' +
                '}';
    }
}
