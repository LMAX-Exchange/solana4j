package com.lmax.solana4j.client.api;

public interface TransactionResponse
{
    TransactionMetadata getMetadata();

    long getSlot();

    TransactionData getTransaction();

    Long getBlockTime();

}
