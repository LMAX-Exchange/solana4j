package com.lmax.solana4j.solanaclient.api;

public interface Transaction
{
    TransactionMetadata getMeta();

    TransactionData getTransaction();
}
