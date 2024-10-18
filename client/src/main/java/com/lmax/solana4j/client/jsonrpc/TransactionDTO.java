package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.Transaction;
import com.lmax.solana4j.client.api.TransactionData;
import com.lmax.solana4j.client.api.TransactionMetadata;

final class TransactionDTO implements Transaction
{
    private final MetaDTO metaImpl;
    private final TransactionDataDTO transaction;

    @JsonCreator
    TransactionDTO(
            final @JsonProperty("meta") MetaDTO metaImpl,
            final @JsonProperty("transaction") TransactionDataDTO transaction)
    {
        this.metaImpl = metaImpl;
        this.transaction = transaction;
    }

    @Override
    public TransactionMetadata getMeta()
    {
        return metaImpl;
    }

    @Override
    public TransactionData getTransaction()
    {
        return transaction;
    }

    @Override
    public String toString()
    {
        return "Transaction{" +
               "meta=" + metaImpl +
               ", transaction=" + transaction +
               '}';
    }
}