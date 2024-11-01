package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.TransactionMetadata;
import com.lmax.solana4j.client.api.TransactionResponse;

import java.util.List;

final class TransactionResponseDTO implements TransactionResponse
{
    private final MetaDTO metaImpl;
    private final long slot;
    private final List<String> transaction;
    private final Long blockTime;

    @JsonCreator
    TransactionResponseDTO(
            final @JsonProperty("meta") MetaDTO metaImpl,
            final @JsonProperty("slot") long slot,
            final @JsonProperty("transaction") List<String> transaction,
            final @JsonProperty("blockTime") Long blockTime)
    {
        this.metaImpl = metaImpl;
        this.slot = slot;
        this.transaction = transaction;
        this.blockTime = blockTime;
    }

    @Override
    @JsonProperty("meta")
    public TransactionMetadata getMetadata()
    {
        return metaImpl;
    }

    @Override
    public long getSlot()
    {
        return slot;
    }

    @Override
    public List<String> getTransactionData()
    {
        return transaction;
    }

    @Override
    public Long getBlockTime()
    {
        return blockTime;
    }

    @Override
    public String toString()
    {
        return "TransactionResponseDTO{" +
                "metaImpl=" + metaImpl +
                ", slot=" + slot +
                ", transaction=" + transaction +
                ", blockTime=" + blockTime +
                '}';
    }
}