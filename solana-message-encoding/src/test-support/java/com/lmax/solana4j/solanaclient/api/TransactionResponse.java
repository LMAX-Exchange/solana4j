package com.lmax.solana4j.solanaclient.api;

public interface TransactionResponse
{
    TransactionMetadata getMetadata();

    long getSlot();

    TransactionData getTransaction();

    Long getBlockTime();

}
