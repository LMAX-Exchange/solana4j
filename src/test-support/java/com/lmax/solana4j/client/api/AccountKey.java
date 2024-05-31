package com.lmax.solana4j.client.api;

public interface AccountKey
{
    enum KeySource
    {
        TRANSACTION,
        LOOKUPTABLE
    }

    String getKey();

    KeySource getSource();

    boolean isSigner();

    boolean isWritable();
}
