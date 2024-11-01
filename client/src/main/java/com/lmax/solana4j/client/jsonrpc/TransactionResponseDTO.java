package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.TransactionData;
import com.lmax.solana4j.client.api.TransactionMetadata;
import com.lmax.solana4j.client.api.TransactionResponse;

final class TransactionResponseDTO implements TransactionResponse
{
    private final MetaDTO metaImpl;
    private final long slot;
    private final TransactionDataDTO transaction;
    private final Long blockTime;
    private final String version;

    @JsonCreator
    TransactionResponseDTO(
            final @JsonProperty("meta") MetaDTO metaImpl,
            final @JsonProperty("slot") long slot,
            final @JsonProperty("transaction") TransactionDataDTO transaction,
            final @JsonProperty("blockTime") Long blockTime,
            final @JsonProperty("version") String version)
    {
        this.metaImpl = metaImpl;
        this.slot = slot;
        this.transaction = transaction;
        this.blockTime = blockTime;
        this.version = version;
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
    public TransactionData getTransactionData()
    {
        return transaction;
    }

    @Override
    public Long getBlockTime()
    {
        return blockTime;
    }

    @Override
    public String getVersion()
    {
        return version;
    }

    @Override
    public String toString()
    {
        return "TransactionResponseDTO{" +
                "metaImpl=" + metaImpl +
                ", slot=" + slot +
                ", transaction=" + transaction +
                ", blockTime=" + blockTime +
                ", version='" + version + '\'' +
                '}';
    }
}