package com.lmax.solana4j.solanaclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.solanaclient.api.TransactionData;
import com.lmax.solana4j.solanaclient.api.TransactionResponse;

final class TransactionResponseDTO implements TransactionResponse
{
    private final MetaDTO metaImpl;
    private final long slot;
    private final TransactionDataDTO transaction;
    private final Long blockTime;


    @JsonCreator
    TransactionResponseDTO(
            final @JsonProperty("meta") MetaDTO metaImpl,
            final @JsonProperty("slot") long slot,
            final @JsonProperty("transaction") TransactionDataDTO transaction,
            final @JsonProperty("blockTime") Long blockTime)
    {
        this.metaImpl = metaImpl;
        this.slot = slot;
        this.transaction = transaction;
        this.blockTime = blockTime;
    }

    @Override
    @JsonProperty("meta")
    public MetaDTO getMetadata()
    {
        return metaImpl;
    }

    @Override
    public long getSlot()
    {
        return slot;
    }

    @Override
    public TransactionData getTransaction()
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
        return "TransactionResponse{" +
               "meta=" + metaImpl +
               ", slot=" + slot +
               ", transaction=" + transaction +
               '}';
    }
}