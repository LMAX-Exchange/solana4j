package com.lmax.solana4j.client.api;

public interface Transaction
{
    TransactionMetadata getMeta();

    TransactionData getTransaction();
}
