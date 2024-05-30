package com.lmax.solana4j.testclient.api;

public interface TransactionResponse
{
    TransactionMetadata getMetadata();

    long getSlot();

    TransactionData getTransaction();

    Long getBlockTime();

}
