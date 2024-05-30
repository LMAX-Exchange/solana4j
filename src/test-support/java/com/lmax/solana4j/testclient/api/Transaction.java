package com.lmax.solana4j.testclient.api;

public interface Transaction
{
    TransactionMetadata getMeta();

    TransactionData getTransaction();
}
